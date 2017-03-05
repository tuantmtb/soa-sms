package edu.fit.soa.sms.web.controller;

import edu.fit.soa.sms.domain.Student;
import edu.fit.soa.sms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Created by tuantmtb on 2/17/17.
 */
@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class HomeController {
    private static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    StudentService studentService;

    @RequestMapping(value = "/")
    public ModelAndView home(ModelMap modelMap) {
        List<Student> students = studentService.findAll();
        modelMap.put("description", "Danh sách tài khoản sinh viên");
        modelMap.put("students", students);
        return new ModelAndView("home", modelMap);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView searchStudent(ModelMap modelMap, @RequestParam("name") String name) {
        List<Student> students = null;
        logger.info("moelmap: {}", modelMap);
        logger.info("name: {}", name);
        if (name != null && name.length() > 1) {
            students = studentService.findByNameContaining(name);
            modelMap.put("description", "Tìm thấy " + students.size() + " kết quả");
        } else {
            modelMap.put("description", "Không tìm thấy sinh viên có tên " + name);
        }

        modelMap.put("students", students);

        return new ModelAndView("home", modelMap);
    }

    @RequestMapping(value = "/student/{id}")
    public ModelAndView detail(ModelMap modelMap, @PathVariable("id") long id) {


        try {
            Student student = studentService.getOne(id);
            logger.debug("student: {}", student);
            modelMap.put("description", "Sinh viên " + student.getName());
            modelMap.put("student", student);
        } catch (Exception e) {
            modelMap.put("description", "Không tìm thấy sinh viên");
        }

        return new ModelAndView("student", modelMap);
    }

}
