package edu.fit.soa.sms.service;

import edu.fit.soa.sms.domain.Student;

import java.util.List;

/**
 * Created by tuantmtb on 2/17/17.
 */
public interface StudentService extends BaseService<Student> {
    List<Student> findByNameContaining(String name);
}

