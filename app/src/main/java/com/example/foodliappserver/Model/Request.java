package com.example.foodliappserver.Model;

import java.util.List;

public class Request {
    private String phone;
    private String address;
    private String total;
    private String name;
    private String status;
    private String paymentState;
    private String paymentMethod;
    private String comment;
    private List<Order> foods; // List of food order

    public Request() {
    }

    public Request(String phone, String address, String total, String name, String status, String paymentState, String paymentMethod, String comment, List<Order> foods) {
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.name = name;
        this.status = status;
        this.paymentState = paymentState;
        this.paymentMethod = paymentMethod;
        this.comment = comment;
        this.foods = foods;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentState) {
        this.paymentMethod = paymentState;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}