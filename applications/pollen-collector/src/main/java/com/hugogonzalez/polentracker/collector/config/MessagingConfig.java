package com.hugogonzalez.polentracker.collector.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class MessagingConfig {
    public static final String EXCHANGE = "pollen.events", DLX = "pollen.dlx", REQUEST_QUEUE = "collection.requests";

    @Bean
    TopicExchange pollenExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    Queue requestQueue() {
        return QueueBuilder.durable(REQUEST_QUEUE).deadLetterExchange(DLX).deadLetterRoutingKey("collection.requests.failed").build();
    }

    @Bean
    Binding requests() {
        return BindingBuilder.bind(requestQueue()).to(pollenExchange()).with("collection.requested");
    }

    @Bean
    JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
