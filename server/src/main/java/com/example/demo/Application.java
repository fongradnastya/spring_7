package com.example.demo;

import com.example.demo.messaging.MessageListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Класс, запускающий работу программы
 */
@SpringBootApplication
public class Application {
    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        MessageListener messageReceiver = context.getBean(MessageListener.class);
        messageReceiver.startReceivingMessages();
    }
}