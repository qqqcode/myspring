package com.qqqmq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author Johnson
 * 2021/8/31
 */
public class productionMain {


    public static void main(String[] args) throws JMSException {

        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        Queue qqq = session.createQueue("qqqQueue");

        MessageProducer producer = session.createProducer(qqq);

        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        for (int i = 0; i < 5; i++) {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText("this is " + i + ";");
            producer.send(textMessage);
        }

        if (connection != null) {
            connection.close();
        }
    }
}
