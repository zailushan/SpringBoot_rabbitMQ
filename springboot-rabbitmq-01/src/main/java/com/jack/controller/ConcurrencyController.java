package com.jack.controller;

import com.jack.response.BaseResponse;
import com.jack.response.StatusCode;
import com.jack.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/8/25 21:57
 */
@RestController
public class ConcurrencyController {

    private static final Logger log = LoggerFactory.getLogger(ConcurrencyController.class);

    private static final String Prefix = "concurrency";

    @Autowired
    private InitService initService;

    @RequestMapping(value = Prefix+"/robbing/thread",method = RequestMethod.GET)
    public BaseResponse robbingThread(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        initService.generateMultiThread();
        return response;
    }

}
