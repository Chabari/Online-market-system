package com.marketside;

import java.util.Date;

public class UploadItemList extends PostID{
    public String user_id,imageUrl,title,desc,phone,fullname,category,price,pricebefore;
    public Date timeStamp;

    public UploadItemList() {
    }

    public UploadItemList(String user_id, String imageUrl, String title, String desc, String phone, String fullname, String category, String price, String pricebefore, Date timeStamp) {
        this.user_id = user_id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.desc = desc;
        this.phone = phone;
        this.fullname = fullname;
        this.category = category;
        this.price = price;
        this.pricebefore = pricebefore;
        this.timeStamp = timeStamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPricebefore() {
        return pricebefore;
    }

    public void setPricebefore(String pricebefore) {
        this.pricebefore = pricebefore;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
