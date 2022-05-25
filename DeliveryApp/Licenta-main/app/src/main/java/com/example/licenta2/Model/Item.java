package com.example.licenta2.Model;

public class Item {

    private String productId;
    private String productName;
    private String quantity;
    private String price;
    private String firebaseRef;

    public Item() {
    }

    public Item(String productId, String productName, String quantity, String price, String firebaseRef) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.firebaseRef = firebaseRef;
    }


    public String getProductId() {
        return productName;
    }
    public void setProductId(String id){
        this.productId=id;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String name){
        this.productName=name;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String image){
        this.quantity=quantity;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price=price;
    }

    public String getFirebaseRef() {
        return firebaseRef;
    }

    public void setFirebaseRef(String firebaseRef) {
        this.firebaseRef = firebaseRef;
    }
}
