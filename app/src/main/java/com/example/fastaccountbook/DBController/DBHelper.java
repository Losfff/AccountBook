package com.example.fastaccountbook.DBController;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;

    // TypeModel table
    private static final String TABLE_TYPE = "TypeModel";
    private static final String COLUMN_TYPE_ID = "typeId"; // 修改为 String 类型
    private static final String COLUMN_TYPE_NAME = "typeName";

    // RecordModel table
    private static final String TABLE_RECORD = "RecordModel";
    private static final String COLUMN_RECORD_ID = "id";
    private static final String COLUMN_RECORD_DATE = "date";
    private static final String COLUMN_RECORD_TIME = "time";
    private static final String COLUMN_RECORD_TYPE_ID = "typeId"; // 修改为 String 类型
    private static final String COLUMN_RECORD_DESCRIPTION = "description";
    private static final String COLUMN_RECORD_AMOUNT = "amount";

    private static final String CREATE_TABLE_TYPE = "CREATE TABLE " + TABLE_TYPE + " (" +
            COLUMN_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TYPE_NAME + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_RECORD = "CREATE TABLE " + TABLE_RECORD + " (" +
            COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECORD_DATE + " TEXT NOT NULL, " +
            COLUMN_RECORD_TIME + " TEXT NOT NULL, " +
            COLUMN_RECORD_TYPE_ID + " INTEGER NOT NULL, " +
            COLUMN_RECORD_DESCRIPTION + " TEXT, " +
            COLUMN_RECORD_AMOUNT + " REAL NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_RECORD_TYPE_ID + ") REFERENCES " + TABLE_TYPE + "(" + COLUMN_TYPE_ID + "));";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_TYPE);
            db.execSQL(CREATE_TABLE_RECORD);
        } catch (Exception e) {
            Log.e("DBHelper", "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPE);
            onCreate(db);
        } catch (Exception e) {
            Log.e("DBHelper", "Error upgrading database", e);
        }
    }

    /**
     * 添加账单
     * @param recordModel 传入一个账单对象
     * @return 插入成功返回 true，否则返回 false
     */
    public boolean insertRecord(RecordModel recordModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECORD_DATE, recordModel.date);
            values.put(COLUMN_RECORD_TIME, recordModel.time);
            values.put(COLUMN_RECORD_TYPE_ID, recordModel.typeId);
            values.put(COLUMN_RECORD_DESCRIPTION, recordModel.description);
            values.put(COLUMN_RECORD_AMOUNT, recordModel.amount);
            long result = db.insert(TABLE_RECORD, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DBHelper", "Error inserting record", e);
        } finally {
            db.close();
        }
        return false;
    }

    /**
     * 删除账单
     * @param id 传入账单id
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_RECORD, COLUMN_RECORD_ID + "=?", new String[]{String.valueOf(id)});
            return result > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting record", e);
        } finally {
            db.close();
        }
        return false;
    }

    /**
     * 更新账单
     * @param recordModel 传入一个账单对象
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean updateRecord(RecordModel recordModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECORD_DATE, recordModel.date);
            values.put(COLUMN_RECORD_TIME, recordModel.time);
            values.put(COLUMN_RECORD_TYPE_ID, recordModel.typeId);
            values.put(COLUMN_RECORD_DESCRIPTION, recordModel.description);
            values.put(COLUMN_RECORD_AMOUNT, recordModel.amount);
            int result = db.update(TABLE_RECORD, values, COLUMN_RECORD_ID + "=?", new String[]{String.valueOf(recordModel.id)});
            return result > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating record", e);
        } finally {
            db.close();
        }
        return false;
    }

    /**
     * 获取账单列表
     * @return 返回一个账单列表
     */
    @SuppressLint("Range")
    public List<RecordModel> getRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<RecordModel> recordList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_RECORD, new String[]{COLUMN_RECORD_ID, COLUMN_RECORD_DATE, COLUMN_RECORD_TIME,
                            COLUMN_RECORD_TYPE_ID, COLUMN_RECORD_DESCRIPTION, COLUMN_RECORD_AMOUNT},
                    null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    RecordModel record = new RecordModel();
                    record.id = cursor.getInt(cursor.getColumnIndex(COLUMN_RECORD_ID));
                    record.date = cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE));
                    record.time = cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_TIME));
                    record.typeId = cursor.getInt(cursor.getColumnIndex(COLUMN_RECORD_TYPE_ID)); // 修正为 getInt
                    record.description = cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DESCRIPTION));
                    record.amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_RECORD_AMOUNT));
                    recordList.add(record);
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting records", e);
        } finally {
            db.close();
        }
        return recordList;
    }

    /**
     * 添加类别
     * @param typeModel 传入一个类别对象
     * @return 插入成功返回 true，否则返回 false
     */
    public boolean insertType(TypeModel typeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TYPE_NAME, typeModel.typeName);
            long result = db.insert(TABLE_TYPE, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DBHelper", "Error inserting type", e);
        } finally {
            db.close();
        }
        return false;
    }

    /**
     * 删除类别
     * @param id 传入类别id
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean deleteType(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_TYPE, COLUMN_TYPE_ID + "=?", new String[]{String.valueOf(id)});
            return result > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting type", e);
        } finally {
            db.close();
        }
        return false;
    }

    /**
     * 更新类别
     * @param typeModel 传入一个类别对象
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean updateType(TypeModel typeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TYPE_NAME, typeModel.typeName);
            int result = db.update(TABLE_TYPE, values, COLUMN_TYPE_ID + "=?", new String[]{String.valueOf(typeModel.typeId)});
            return result > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating type", e);
        } finally {
            db.close();
        }
        return false;
    }

    /**
     * 获取类别列表
     * @return 返回一个类别列表
     */
    @SuppressLint("Range")
    public List<TypeModel> getTypes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<TypeModel> typeList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_TYPE, new String[]{COLUMN_TYPE_ID, COLUMN_TYPE_NAME},
                    null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TypeModel type = new TypeModel();
                    type.typeId = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE_ID));
                    type.typeName = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE_NAME));
                    typeList.add(type);
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting types", e);
        } finally {
            db.close();
        }
        return typeList;
    }
}