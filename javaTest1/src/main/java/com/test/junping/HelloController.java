package com.test.junping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HelloController{
    @RequestMapping(value="/hello",method=RequestMethod.GET)
    public String doGet(Model model){
        model.addAttribute("name","junping");
        return "index";

    }

}
