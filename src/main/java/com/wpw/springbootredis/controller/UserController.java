package com.wpw.springbootredis.controller;

import com.wpw.springbootredis.config.CountInvokeTimes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author wpw
 */
@RestController
public class UserController {
    @CountInvokeTimes
    @RequestMapping(value = "/hello")
    public String hello() {
        return "hello redis";
    }

    @CountInvokeTimes
    @GetMapping(value = "/list")
    public List<String> list() {
        return Arrays.asList("hello", "hello");
    }

    @GetMapping(value = "/say")
    public String say() {
        return "say";
    }

    @CountInvokeTimes(value = "testModel")
    @GetMapping(value = "/test")
    public String test() {
        return "hello";
    }
}
