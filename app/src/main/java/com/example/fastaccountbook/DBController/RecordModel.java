package com.example.fastaccountbook.DBController;

public class RecordModel {
    private int id;
    private String date;
    private String time;
    private int typeId;
    private String description;
    private double amount;//正收入，负支出

    public RecordModel(){}

    public RecordModel(int id, String date,String time,int typeId,String description,double amount){
        this.id=id;
        this.date=date;
        this.time=time;
        this.typeId=typeId;
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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
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
