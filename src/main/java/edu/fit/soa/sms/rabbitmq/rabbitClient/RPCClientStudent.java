package edu.fit.soa.sms.rabbitmq.rabbitClient;

import edu.fit.soa.sms.domain.Student;

import java.util.List;

/**
 * Interface RPC Client RabbitMQ
 * Created by tuantmtb on 3/1/17.
 */
public class RPCClientStudent {
    public List<Student> getAll() throws Exception {
        return new RPCClientStudentGetAll().getAll().getLstStudent();
    }

    public Student findById(Long id) throws Exception {
        return new RPCClientStudentFindOne().find(id);
    }

    public boolean deleteById(Long id) throws Exception {
        return new RPCClientStudentDelete().delete(id);
    }

    public Student create(Student student) throws Exception {
        return new RPCClientStudentCreate().create(student);
    }
}
