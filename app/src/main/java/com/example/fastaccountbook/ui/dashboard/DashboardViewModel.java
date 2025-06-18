package com.example.fastaccountbook.ui.dashboard;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fastaccountbook.DBController.DBHelper;
import com.example.fastaccountbook.DBController.RecordModel;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<List<RecordModel>> records = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application app) {
        super(app);
    }

    /** 外部调用来加载数据库数据 */
    public void loadRecords() {
        Context ctx = getApplication().getApplicationContext();
        DBHelper db = new DBHelper(ctx);
        List<RecordModel> list = db.getRecords();
        records.setValue(list);
    }

    public LiveData<List<RecordModel>> getRecords() {
        return records;
    }
}
