package edu.fit.soa.sms.rabbitmq.rabbitServer;

import com.rabbitmq.client.*;
import edu.fit.soa.sms.domain.Student;
import edu.fit.soa.sms.rabbitmq.StudentQueueName;
import edu.fit.soa.sms.service.StudentService;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Created by tuantmtb on 3/1/17.
 */
@Component
public class RPCServerStudentDelete {

    private static Logger log = LoggerFactory.getLogger(RPCServerStudentDelete.class);


    private static final String RPC_SMS_DELETE = StudentQueueName.RPC_SMS_DELETE;

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
            channel.queueDeclare(RPC_SMS_DELETE, false, false, false, null);
            channel.basicQos(1);

            log.info("Awaiting RPC find student");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(properties.getCorrelationId())
                            .contentEncoding("UTF-8")
                            .build();

                    long student_id = SerializationUtils.deserialize(body);
                    log.info("prepare find id={}", student_id);
                    boolean result = false;
                    Student student_find_by_id = studentService.getOne(student_id);
                    if (student_find_by_id != null) {
                        result = true;
                        log.info("deleted student= {}", student_id);
                        studentService.deleteInBatch(Arrays.asList(student_find_by_id));
                    }

                    byte[] response = SerializationUtils.serialize(result);

                    // send response
                    channel.basicPublish("", properties.getReplyTo(), replyProps, response);
                    channel.basicAck(envelope.getDeliveryTag(), false);

                }
            };

            channel.basicConsume(RPC_SMS_DELETE, false, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
