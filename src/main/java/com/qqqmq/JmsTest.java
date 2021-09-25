package com.qqqmq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsTest {
    public static void main(String[] args) throws JMSException {
        //建立连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://47.115.55.168:61616");
        //创建连接
        Connection con = factory.createConnection();
        //打开链接
        con.start();
        /**创建会话
         * 参数一：是否开启事务
         * 消息确认机制
         */
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建目标地址（Queue or Topic）
        Queue qqqQueue = session.createQueue("qqqQueue");
        //创建生产者
        MessageProducer producer = session.createProducer(qqqQueue);
        //创建消息
        TextMessage textMessage = session.createTextMessage("test message");
        //发送消息
        producer.send(textMessage);
        System.out.println("消息发送完成");
        //释放资源
        session.close();
        con.close();
    }
}
