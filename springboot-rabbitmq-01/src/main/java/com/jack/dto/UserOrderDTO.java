package com.jack.dto;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/1 20:55
 */
public class UserOrderDTO {

    private String orderNo;

    private Integer userId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserOrderDTO{" +
                "orderNo='" + orderNo + '\'' +
                ", userId=" + userId +
                '}';
    }
}
