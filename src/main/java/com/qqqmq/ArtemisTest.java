package com.qqqmq;

import com.common.Contant;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.client.*;

public class ArtemisTest {

    public static void main(String[] args) throws Exception {
        ServerLocator locator = ActiveMQClient.createServerLocator(Contant.ArtemisAdress);

        ClientSessionFactory factory =  locator.createSessionFactory();
        ClientSession session = factory.createSession();

        ClientProducer producer = session.createProducer("example");
        ClientMessage message = session.createMessage(true);
        message.getBodyBuffer().writeString("Hello");


        session.createQueue("example", RoutingType.ANYCAST, "example", true);

        ClientConsumer consumer = session.createConsumer("example");

        producer.send(message);

        session.start();
        ClientMessage msgReceived = consumer.receive();

        System.out.println("message = " + msgReceived.getBodyBuffer().readString());

        session.close();
    }
}
