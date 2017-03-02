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

public class RPCClientStudentCreate {

    private Connection connection;
    private Channel channel;
    private String REQUEST_QUEUE_RPC_SMS_CREATE = StudentQueueName.RPC_SMS_CREATE;
    private String replyQueueName;

    private static Logger logger = LoggerFactory.getLogger(RPCClientStudentCreate.class);

    /**
     * setup connection RPC
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public RPCClientStudentCreate() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
    }

    /**
     * Create Student
     *
     * @return
     * @throws Exception
     */
    public Student create(Student student) throws Exception {
        final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        String requestStudent = student.getName() + "###" + student.getEmail() + "###" + student.getCode();

        channel.basicPublish("", REQUEST_QUEUE_RPC_SMS_CREATE, props, SerializationUtils.serialize(requestStudent));

        final BlockingQueue<Student> response = new ArrayBlockingQueue<Student>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                if (properties.getCorrelationId().equals(corrId)) {
                    try {

                        System.out.println("recieve student response: ");
                        Student studentResponse = SerializationUtils.deserialize(body);
                        System.out.println("recieve student response: " + studentResponse);
                        response.offer(studentResponse);
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


        RPCClientStudentCreate rpcStudentGetAll = new RPCClientStudentCreate();

        System.out.println("Requesting");
        Student student = new Student("Tran Minh Tuan", "tuantmtb@gmail.com", "14020520");

        Student response = rpcStudentGetAll.create(student);

        logger.info("Response: {}", response);
        rpcStudentGetAll.close();
    }

}