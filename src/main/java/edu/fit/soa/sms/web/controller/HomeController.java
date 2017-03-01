package edu.fit.soa.sms.web.controller;

import edu.fit.soa.sms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by tuantmtb on 2/17/17.
 */
@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class HomeController {
    @Autowired
    StudentService studentService;

    @RequestMapping(value = "/")
    public ModelAndView home(ModelMap modelMap) {

        return new ModelAndView("home", modelMap);
    }

}
