package com.artnaseef.amq.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Main {

    private String brokerUrl = "failover://(tcp://localhost:61616)";
    private String queueName = "simple.listener.test";

    public static void main(String[] args) {
      Main instance = new Main();
      instance.instanceMain(args);
    }

    public void instanceMain(String[] args) {
      try {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.brokerUrl);
        Connection connection = connectionFactory.createConnection();

        connection.setExceptionListener(new MyExceptionListener());


        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(this.queueName);
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(new MyMessageListener());

        connection.start();
      } catch (Exception exc) {
        exc.printStackTrace();
      }
    }

    private static class MyMessageListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            System.out.println("RECEIVED MESSAGE: type=" + message.getClass().getName());
            if (message instanceof TextMessage) {
                System.out.println("BODY:");

                try {
                    System.out.println(((TextMessage) message).getText());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            } else {
                System.out.println(message.toString());
            }
        }
    }

    private static class MyExceptionListener implements ExceptionListener {
        @Override
        public void onException(JMSException exc) {
            System.out.println("CONNECTION EXCEPTION:");
            exc.printStackTrace();
        }
    }
}
