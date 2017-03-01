package edu.fit.soa.sms.rabbitmq.rabbitClient.service;

/**
 * Created by tuantmtb on 3/1/17.
 */

import com.rabbitmq.client.*;
import edu.fit.soa.sms.domain.Student;
import edu.fit.soa.sms.rabbitmq.StudentQueueName;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClientStudentFindOne {

    private Connection connection;
    private Channel channel;
    private String REQUEST_QUEUE_RPC_SMS_FIND_ONE = StudentQueueName.RPC_SMS_FIND_ONE;
    private String replyQueueName;

    private static Logger logger = LoggerFactory.getLogger(RPCClientStudentFindOne.class);

    /**
     * setup connection RPC
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public RPCClientStudentFindOne() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
    }

    /**
     * Find Student By Id
     *
     * @return
     * @throws Exception
     */
    public Student find(Long id) throws Exception {
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();


        channel.basicPublish("", REQUEST_QUEUE_RPC_SMS_FIND_ONE, props, SerializationUtils.serialize(id));

        final BlockingQueue<Student> response = new ArrayBlockingQueue<Student>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                if (properties.getCorrelationId().equals(corrId)) {
                    try {
                        Student statusResponse = SerializationUtils.deserialize(body);
                        response.offer(statusResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return response.take();
    }


    private void close() throws IOException {
        connection.close();
    }

    // test mode
    public static void main(String[] args) throws Exception {
        RPCClientStudentFindOne rpcStudent = new RPCClientStudentFindOne();

        System.out.println("Requesting");

        Student response = rpcStudent.find(Long.valueOf("3"));
        logger.info("Response: {}", response);
        if (response != null) {
            // found student by id
        } else {
            // not found student id
        }


        rpcStudent.close();
    }

}