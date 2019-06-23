package cn.itcast.web.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name.do")
    public Map<String,String> name(){
        //获取登录用户名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String, String> nameMap = new HashMap<String,String>();
        nameMap.put("loginName",name);

        return nameMap;
    }

}
