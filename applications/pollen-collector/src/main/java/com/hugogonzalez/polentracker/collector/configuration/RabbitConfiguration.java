package com.hugogonzalez.polentracker.collector.configuration;
import com.fasterxml.jackson.databind.ObjectMapper; import com.hugogonzalez.polentracker.collector.adapter.out.messaging.RabbitTopology; import org.springframework.amqp.core.*; import org.springframework.amqp.support.converter.JacksonJsonMessageConverter; import org.springframework.context.annotation.*;
@Configuration public class RabbitConfiguration {
 @Bean TopicExchange pollenExchange(){return new TopicExchange(RabbitTopology.EXCHANGE,true,false);}@Bean DirectExchange deadLetterExchange(){return new DirectExchange(RabbitTopology.DLX,true,false);}
 @Bean Queue requestQueue(){return QueueBuilder.durable(RabbitTopology.REQUEST_QUEUE).deadLetterExchange(RabbitTopology.DLX).deadLetterRoutingKey("collection.requests.failed").build();}
 @Bean Binding requests(){return BindingBuilder.bind(requestQueue()).to(pollenExchange()).with(RabbitTopology.REQUESTED);}
 @Bean JacksonJsonMessageConverter messageConverter(){return new JacksonJsonMessageConverter();}@Bean ObjectMapper objectMapper(){return new ObjectMapper().findAndRegisterModules();}
}
