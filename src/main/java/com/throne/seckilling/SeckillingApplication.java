package com.throne.seckilling;

import com.throne.seckilling.dao.UserDOMapper;
import com.throne.seckilling.data_object.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootApplication(scanBasePackages = {"com.throne.seckilling.*"})
@Controller
@MapperScan("com.throne.seckilling.dao")
public class SeckillingApplication {

    @Resource(name="userDOMapper")
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home(){
        return "/html/index.html";
    }

    public static void main(String[] args) {
        SpringApplication.run(SeckillingApplication.class, args);
    }

}
