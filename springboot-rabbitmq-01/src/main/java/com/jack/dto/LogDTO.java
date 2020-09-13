package com.jack.dto;

import java.io.Serializable;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/2 21:40
 */
public class LogDTO implements Serializable {
    private String methodName;

    private String operateData;

    public LogDTO(String methodName, String operateData) {
        this.methodName = methodName;
        this.operateData = operateData;
    }

    public LogDTO() {
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getOperateData() {
        return operateData;
    }

    public void setOperateData(String operateData) {
        this.operateData = operateData;
    }

    @Override
    public String toString() {
        return "LogDTO{" +
                "methodName='" + methodName + '\'' +
                ", operateData='" + operateData + '\'' +
                '}';
    }
}
