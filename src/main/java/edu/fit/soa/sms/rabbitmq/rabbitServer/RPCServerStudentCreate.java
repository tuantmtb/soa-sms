package edu.fit.soa.sms.rabbitmq.rabbitServer;

import com.rabbitmq.client.*;
import edu.fit.soa.sms.domain.Student;
import edu.fit.soa.sms.rabbitmq.StudentQueueName;
import edu.fit.soa.sms.rabbitmq.message.MessageStudentAll;
import edu.fit.soa.sms.service.StudentService;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


/**
 * Created by tuantmtb on 3/1/17.
 */
@Component
public class RPCServerStudentCreate {

    private static Logger log = LoggerFactory.getLogger(RPCServerStudentCreate.class);


    private static final String RPC_SMS_CREATE = StudentQueueName.RPC_SMS_CREATE;

    @Autowired
    private StudentService studentService;

    public void subcriber() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();

            //declare channel
            channel.queueDeclare(RPC_SMS_CREATE, false, false, false, null);
            channel.basicQos(1);

            log.info("Awaiting RPC create student");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(properties.getCorrelationId())
                            .contentEncoding("UTF-8")
                            .build();

                    log.info("Have a request:");
//                    String recive = SerializationUtils.deserialize(body);
//                    log.info("Have a request:" + recive);
//                    String request = SerializationUtils.deserialize(body);
                    String request = new String(body, StandardCharsets.UTF_8);
                    List<String> items = Arrays.asList(request.split("\\s*###\\s*"));

                    Student studentRequest = new Student(items.get(0), items.get(1), items.get(2));
                    Student studentCreated = studentService.save(studentRequest);
                    byte[] response = SerializationUtils.serialize(studentCreated);
                    log.info("sending response: {}", studentCreated);
                    // send response
                    channel.basicPublish("", properties.getReplyTo(), replyProps, response);
//                    channel.basicPublish("", properties.getReplyTo(), replyProps, null);
                    channel.basicAck(envelope.getDeliveryTag(), false);

                }
            };

            channel.basicConsume(RPC_SMS_CREATE, false, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
