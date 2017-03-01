package edu.fit.soa.sms;


import edu.fit.soa.sms.rabbitmq.rabbitServer.RPCServerStudentCreate;
import edu.fit.soa.sms.rabbitmq.rabbitServer.RPCServerStudentDelete;
import edu.fit.soa.sms.rabbitmq.rabbitServer.RPCServerStudentFindOne;
import edu.fit.soa.sms.rabbitmq.rabbitServer.RPCServerStudentGetAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

@Configuration
@EnableAsync
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class SystemManagementStudentServerApplication {

    private final static String QUEUE_NAME = "hello";

    @Autowired
    RPCServerStudentGetAll rpcServerStudentGetAll;

    @Autowired
    RPCServerStudentCreate rpcServerStudentCreate;

    @Autowired
    RPCServerStudentDelete rpcServerStudentDelete;

    @Autowired
    RPCServerStudentFindOne rpcServerStudentFindOne;

    @PostConstruct
    public void subcriberStudent() {

        try {
            // setup RPC after run server
            rpcServerStudentGetAll.subcriber();
            rpcServerStudentCreate.subcriber();
            rpcServerStudentDelete.subcriber();
            rpcServerStudentFindOne.subcriber();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(SystemManagementStudentServerApplication.class, args);


    }
}
