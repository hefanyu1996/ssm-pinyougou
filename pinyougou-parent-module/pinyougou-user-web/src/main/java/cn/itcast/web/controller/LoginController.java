package cn.itcast.web.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showName")
    public Map<String, String> showName(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String,String> loginName = new HashMap<>();

        loginName.put("loginName",name);

        return loginName;

    }

}
