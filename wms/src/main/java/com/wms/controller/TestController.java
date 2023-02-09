package com.wms.controller;

import com.wms.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/msg")
    public Result Test(){
        return Result.suc("部署成功");
    }
}
