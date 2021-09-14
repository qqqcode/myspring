package com.qqqmq;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author Johnson
 * 2021/8/31
 */
public class CustomerMain {

    public static void main(String[] args) throws JMSException {

        ConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        Queue qqq = session.createQueue("qqq");
        MessageConsumer consumer = session.createConsumer(qqq);

        while (true){
            TextMessage message = (TextMessage) consumer.receive();
            if (message==null){
                break;
            }
            System.out.println(message.getText());
        }
        if(connection!=null){
            connection.close();
        }
    }
}
