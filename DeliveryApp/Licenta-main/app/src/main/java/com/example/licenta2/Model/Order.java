package com.example.licenta2.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.List;

public class Order {
    public enum OrderStatus {
        QUEUED,
        ACCEPTED,
        PREPARING,
        ON_THE_WAY,
        DELIVERED,
        CANCELED
    }

    private String total;
    private String userRef;
    private String deliveryRef;
    private List<OrderSummary> orderItems;
    private long timestamp;
    private OrderStatus status = OrderStatus.QUEUED;

    public Order() {
    }

    public Order(String total, String userRef, String deliveryRef, List<OrderSummary> orderItems, long timestamp) {
        this.total = total;
        this.userRef = userRef;
        this.deliveryRef = deliveryRef;
        this.orderItems = orderItems;
        this.timestamp = timestamp;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public List<OrderSummary> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderSummary> orderItems) {
        this.orderItems = orderItems;
    }

    public java.util.Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public long getTimestampLong() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getDeliveryRef() {
        return deliveryRef;
    }

    public void setDeliveryRef(String deliveryRef) {
        this.deliveryRef = deliveryRef;
    }
}
