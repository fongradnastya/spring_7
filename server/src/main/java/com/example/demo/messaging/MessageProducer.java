package com.example.demo.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Класс отправителя сообщений
 */
@Component
public class MessageProducer {
    private final JmsTemplate jmsTemplate;
    private final String queueName;

    /**
     * Конструктор отправителя сообщений
     * @param jmsTemplate шаблон для работы с jms
     * @param queueName название jms очереди
     */
    @Autowired
    public MessageProducer(JmsTemplate jmsTemplate, @Value("${queue.name}") String queueName) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    /**
     * Отправляет сообщение в очередь
     * @param message строка с сообщением
     */
    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(queueName, message);
    }
}