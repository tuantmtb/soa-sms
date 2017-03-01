package edu.fit.soa.sms.rabbitmq.message;

import edu.fit.soa.sms.domain.Student;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tuantmtb on 3/1/17.
 */
public class MessageStudentAll implements Serializable {
    List<Student> lstStudent;


    public List<Student> getLstStudent() {
        return lstStudent;
    }

    public void setLstStudent(List<Student> lstStudent) {
        this.lstStudent = lstStudent;
    }

    public MessageStudentAll(List<Student> lstStudent) {
        this.lstStudent = lstStudent;
    }
}
