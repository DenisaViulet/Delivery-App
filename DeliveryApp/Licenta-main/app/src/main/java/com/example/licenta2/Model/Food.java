package com.example.licenta2.Model;

import com.google.firebase.database.Exclude;

public class Food {

    private String Name;
    private String Image;
    private String Description;
    private String Price;
    private String Discount;
    private String MenuId;
    @Exclude
    private String foodId;

    public Food(){}

    public Food(String name, String image, String description, String price, String discount, String menuId) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        MenuId = menuId;
    }

    public String getName(){
        return Name;
    }
    public void setName(String name){
        this.Name=name;
    }

    public String getImage(){
        return Image;
    }
    public void setImage(String image){
        this.Image=image;
    }

    public String getDescription(){
        return Description;
    }
    public void setDescription(String description){
        this.Description=description;
    }

    public String getPrice(){
        return Price;
    }
    public void setPrice(String price){
        this.Price=price;
    }

    public String getDiscount(){
        return Discount;
    }
    public void setDiscount(String discount){
        this.Discount=discount;
    }

    public String getMenuId(){
        return MenuId;
    }
    public void setMenuId(String menuId){
        this.MenuId=menuId;
    }

    @Exclude
    public String getFoodId() {
        return foodId;
    }

    @Exclude
    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
}
