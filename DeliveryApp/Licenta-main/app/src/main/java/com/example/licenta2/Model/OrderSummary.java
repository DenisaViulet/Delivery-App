package com.example.licenta2.Model;

public class OrderSummary {

    private String name;
    private String quantity;
    private String ref;
    private String price;

    public OrderSummary() {
    }

    public OrderSummary(String name, String quantity,String price, String ref) {
        this.name = name;
        this.quantity = quantity;
        this.price=price;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
