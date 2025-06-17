package com.example.fastaccountbook.DBController;

public class RecordModel {
    public int id;
    public String date;
    public String time;
    public String type;
    public String description;
    public double amount;//正收入，负支出

    public RecordModel(){}

    public RecordModel(int id, String date,String time,String type,String description,double amount){
        this.id=id;
        this.date=date;
        this.time=time;
        this.type=type;
        this.description=description;
        this.amount=amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
