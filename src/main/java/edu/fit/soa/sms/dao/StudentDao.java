package edu.fit.soa.sms.dao;

import edu.fit.soa.sms.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by tuantmtb on 2/17/17.
 */
@Transactional
@PersistenceContext(type = PersistenceContextType.EXTENDED)
public interface StudentDao extends JpaRepository<Student, Long> {
    @Query("select c from Student c where c.name LIKE CONCAT('%',:name,'%')")
    List<Student> findByNameContaining(@Param("name") String name);
}
