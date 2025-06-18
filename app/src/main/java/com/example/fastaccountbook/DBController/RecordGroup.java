package com.example.fastaccountbook.DBController;

import java.util.List;

public class RecordGroup {
    private String date;
    private List<RecordModel> records;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<RecordModel> getRecords() {
        return records;
    }

    public void setRecords(List<RecordModel> records) {
        this.records = records;
    }

    public RecordGroup(String date, List<RecordModel> records) {
        this.date = date;
        this.records = records;
    }
}
