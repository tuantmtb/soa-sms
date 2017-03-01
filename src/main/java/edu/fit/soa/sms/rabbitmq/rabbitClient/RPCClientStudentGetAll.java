package edu.fit.soa.sms.rabbitmq.rabbitClient;

/**
 * Created by tuantmtb on 3/1/17.
 */

import com.rabbitmq.client.*;
import edu.fit.soa.sms.rabbitmq.StudentQueueName;
import edu.fit.soa.sms.rabbitmq.message.MessageStudentAll;
import org.apache.commons.lang3.SerializationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClientStudentGetAll {

    private Connection connection;
    private Channel channel;
    private String REQUEST_QUEUE_RPC_SMS_GET_ALL = StudentQueueName.RPC_SMS_GET_ALL;
    private String replyQueueName;

    private static Logger logger = LoggerFactory.getLogger(RPCClientStudentGetAll.class);

    /**
     * setup connection RPC
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public RPCClientStudentGetAll() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
    }

    /**
     * Get All Student
     *
     * @return
     * @throws Exception
     */
    public MessageStudentAll getAll() throws Exception {
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", REQUEST_QUEUE_RPC_SMS_GET_ALL, props, null);

        final BlockingQueue<MessageStudentAll> response = new ArrayBlockingQueue<MessageStudentAll>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                if (properties.getCorrelationId().equals(corrId)) {
                    try {
                        MessageStudentAll lstStudent = SerializationUtils.deserialize(body);
                        response.offer(lstStudent);
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
        RPCClientStudentGetAll rpcStudentGetAll = new RPCClientStudentGetAll();

        System.out.println("Requesting");
        MessageStudentAll response = rpcStudentGetAll.getAll();

//        System.out.println(" [.] Got '" + response + "'");
        logger.info("Response: {}", response.getLstStudent());
        rpcStudentGetAll.close();
    }

}