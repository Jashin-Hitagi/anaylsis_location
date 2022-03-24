package com.tool.demo;

import com.tool.demo.tool.ConvertCsv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhoneLocationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneLocationApplication.class, args);
        System.out.println("第一个参数为数据类型(0:IP地址 1:手机号)\n" +
                "第二个参数为手机头配置文件\n" +
                "第三个参数为输入CSV文件位置\n" +
                "第四个参数为输出CSV文件位置\n");
        ConvertCsv.convert(args);
    }

}
