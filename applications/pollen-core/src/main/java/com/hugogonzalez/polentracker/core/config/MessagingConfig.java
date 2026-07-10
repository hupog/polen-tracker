package com.hugogonzalez.polentracker.core.config;
import org.springframework.amqp.core.*; import org.springframework.amqp.support.converter.JacksonJsonMessageConverter; import org.springframework.context.annotation.*;
@Configuration public class MessagingConfig {
 public static final String EXCHANGE="pollen.events", DLX="pollen.dlx", REQUEST_QUEUE="collection.requests", COMPLETED_QUEUE="collection.completed.core", FAILED_QUEUE="collection.failed.core";
 @Bean TopicExchange pollenExchange(){return new TopicExchange(EXCHANGE,true,false);} @Bean DirectExchange deadLetterExchange(){return new DirectExchange(DLX,true,false);}
 @Bean Queue requestQueue(){return QueueBuilder.durable(REQUEST_QUEUE).deadLetterExchange(DLX).deadLetterRoutingKey("collection.requests.failed").build();}
 @Bean Queue completedQueue(){return QueueBuilder.durable(COMPLETED_QUEUE).deadLetterExchange(DLX).deadLetterRoutingKey("collection.results.failed").build();}
 @Bean Queue failedQueue(){return QueueBuilder.durable(FAILED_QUEUE).deadLetterExchange(DLX).deadLetterRoutingKey("collection.results.failed").build();}
 @Bean Queue deadLetterQueue(){return QueueBuilder.durable("pollen.dead-letter").build();}
 @Bean Binding requests(){return BindingBuilder.bind(requestQueue()).to(pollenExchange()).with("collection.requested");} @Bean Binding completed(){return BindingBuilder.bind(completedQueue()).to(pollenExchange()).with("collection.completed");} @Bean Binding failed(){return BindingBuilder.bind(failedQueue()).to(pollenExchange()).with("collection.failed");} @Bean Binding deadLetters(){return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("#");}
 @Bean JacksonJsonMessageConverter messageConverter(){return new JacksonJsonMessageConverter();}
}
