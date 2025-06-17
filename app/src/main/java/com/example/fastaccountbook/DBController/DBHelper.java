package com.example.fastaccountbook.DBController;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 添加账单
     * @param recordModel 传入一个账单对象
     * @return 视为插入成功
     */
    public boolean insertRecord(RecordModel recordModel){
        return true;
    }

    /**
     * 删除账单
     * @param id 传入账单id
     * @return 视为删除成功
     */
    public boolean deleteRecord(int id){
        return true;
    }

    /**
     * 更新账单
     * @param recordModel 传入一个账单对象
     * @return 视为更新成功
     */
    public boolean updateRecord(RecordModel recordModel){
        return true;
    }

    /**
     * 获取账单列表
     * @return 返回一个账单列表
     */
    public List<RecordModel> getRecords(){
        return null;
    }
}
