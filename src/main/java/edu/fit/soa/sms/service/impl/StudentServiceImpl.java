package edu.fit.soa.sms.service.impl;

import edu.fit.soa.sms.dao.StudentDao;
import edu.fit.soa.sms.domain.Student;
import edu.fit.soa.sms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by tuantmtb on 2/17/17.
 */
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    StudentDao studentDao;

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    public List<Student> findAll(Sort sort) {
        return studentDao.findAll(sort);
    }

    @Override
    public List<Student> findAll(Iterable<Long> ids) {
        return studentDao.findAll(ids);
    }

    @Override
    public <S extends Student> List<S> save(Iterable<S> entities) {
        return studentDao.save(entities);
    }

    @Override
    public Student save(Student entities) {
        return studentDao.save(entities);
    }

    @Override
    public void flush() {
        studentDao.flush();
    }

    @Override
    public <S extends Student> S saveAndFlush(S entity) {
        return studentDao.saveAndFlush(entity);
    }

    @Override
    public void deleteInBatch(Iterable<Student> entities) {
        studentDao.deleteInBatch(entities);
    }

    @Override
    public void deleteAllInBatch() {
        studentDao.deleteAllInBatch();
    }

    @Override
    public Student getOne(Long id) {
        return studentDao.getOne(id);
    }
}
