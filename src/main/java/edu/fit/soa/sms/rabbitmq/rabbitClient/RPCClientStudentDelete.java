package edu.fit.soa.sms.rabbitmq.rabbitClient;

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

public class RPCClientStudentDelete {

    private Connection connection;
    private Channel channel;
    private String REQUEST_QUEUE_RPC_SMS_DELETE = StudentQueueName.RPC_SMS_DELETE;
    private String replyQueueName;

    private static Logger logger = LoggerFactory.getLogger(RPCClientStudentDelete.class);

    /**
     * setup connection RPC
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public RPCClientStudentDelete() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
    }

    /**
     * Delete Student
     *
     * @return
     * @throws Exception
     */
    public boolean delete(Long id) throws Exception {
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();


        channel.basicPublish("", REQUEST_QUEUE_RPC_SMS_DELETE, props, SerializationUtils.serialize(id));

        final BlockingQueue<Boolean> response = new ArrayBlockingQueue<Boolean>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                if (properties.getCorrelationId().equals(corrId)) {
                    try {
                        boolean statusResponse = SerializationUtils.deserialize(body);
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
        RPCClientStudentDelete rpcStudent = new RPCClientStudentDelete();

        System.out.println("Requesting");
//        Student student = new Student("Trần Minh Tuấn", "tuantmtb@gmail.com", "14020520");

        boolean response = rpcStudent.delete(Long.valueOf("4"));
        logger.info("Response: {}", response);
        if (response) {
            // deleted
        } else {
            // not found student id
        }


        rpcStudent.close();
    }

}