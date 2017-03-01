package edu.fit.soa.sms.dao;

import edu.fit.soa.sms.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.Transactional;

/**
 * Created by tuantmtb on 2/17/17.
 */
@Transactional
@PersistenceContext(type = PersistenceContextType.EXTENDED)
public interface StudentDao extends JpaRepository<Student, Long> {

}
