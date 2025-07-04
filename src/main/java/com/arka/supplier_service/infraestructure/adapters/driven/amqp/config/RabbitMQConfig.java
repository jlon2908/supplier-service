package com.arka.supplier_service.infraestructure.adapters.driven.amqp.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String STOCK_EXCHANGE = "stock";
    public static final String STOCK_RECEIVED_QUEUE = "stock.received";
    public static final String STOCK_RECEIVED_ROUTING_KEY = "stock.received";

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(STOCK_EXCHANGE);
    }

    @Bean
    public Queue stockReceivedQueue() {
        return new Queue(STOCK_RECEIVED_QUEUE);
    }

    @Bean
    public Binding stockReceivedBinding(Queue stockReceivedQueue, TopicExchange stockExchange) {
        return BindingBuilder.bind(stockReceivedQueue)
                .to(stockExchange)
                .with(STOCK_RECEIVED_ROUTING_KEY);
    }
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
