package com.hugogonzalez.polentracker.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.adapter.out.messaging.RabbitTopology;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitConfiguration {
  @Bean
  TopicExchange pollenExchange() {
    return new TopicExchange(RabbitTopology.EXCHANGE, true, false);
  }

  @Bean
  DirectExchange deadLetterExchange() {
    return new DirectExchange(RabbitTopology.DLX, true, false);
  }

  @Bean
  Queue requestQueue() {
    return QueueBuilder.durable(RabbitTopology.REQUEST_QUEUE)
        .deadLetterExchange(RabbitTopology.DLX)
        .deadLetterRoutingKey("collection.requests.failed")
        .build();
  }

  @Bean
  Queue completedQueue() {
    return QueueBuilder.durable(RabbitTopology.COMPLETED_QUEUE)
        .deadLetterExchange(RabbitTopology.DLX)
        .deadLetterRoutingKey("collection.results.failed")
        .build();
  }

  @Bean
  Queue measurementQueue() {
    return QueueBuilder.durable(RabbitTopology.MEASUREMENT_QUEUE)
        .deadLetterExchange(RabbitTopology.DLX)
        .deadLetterRoutingKey("collection.measurements.failed")
        .build();
  }

  @Bean
  Queue failedQueue() {
    return QueueBuilder.durable(RabbitTopology.FAILED_QUEUE)
        .deadLetterExchange(RabbitTopology.DLX)
        .deadLetterRoutingKey("collection.results.failed")
        .build();
  }

  @Bean
  Queue deadLetterQueue() {
    return QueueBuilder.durable("pollen.dead-letter").build();
  }

  @Bean
  Binding requests() {
    return BindingBuilder.bind(requestQueue()).to(pollenExchange()).with(RabbitTopology.REQUESTED);
  }

  @Bean
  Binding completed() {
    return BindingBuilder.bind(completedQueue())
        .to(pollenExchange())
        .with(RabbitTopology.COMPLETED);
  }

  @Bean
  Binding measurements() {
    return BindingBuilder.bind(measurementQueue())
        .to(pollenExchange())
        .with(RabbitTopology.MEASUREMENT);
  }

  @Bean
  Binding failed() {
    return BindingBuilder.bind(failedQueue()).to(pollenExchange()).with(RabbitTopology.FAILED);
  }

  @Bean
  Binding deadLetters() {
    return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("#");
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
