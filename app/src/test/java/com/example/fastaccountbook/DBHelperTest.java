package com.example.fastaccountbook;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

import com.example.fastaccountbook.DBController.DBHelper;
import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.DBController.TypeModel;

public class DBHelperTest {

    private DBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("my_database.db"); // 确保每次是干净的数据库
        dbHelper = new DBHelper(context);
    }

    @After
    public void tearDown() {
        dbHelper.close();
    }

    @Test
    public void testInsertAndGetType() {
        TypeModel type = new TypeModel();
        type.setTypeName("Test Type");

        boolean insertResult = dbHelper.insertType(type);
        assertTrue(insertResult);

        List<TypeModel> types = dbHelper.getTypes();
        assertFalse(types.isEmpty());
        assertEquals("Test Type", types.get(0).getTypeName());
    }

    @Test
    public void testInsertAndGetRecord() {
        // 插入一个类型
        TypeModel type = new TypeModel();
        type.setTypeName("Food");
        assertTrue(dbHelper.insertType(type));

        List<TypeModel> types = dbHelper.getTypes();
        int typeId = types.get(0).getTypeId();

        // 插入账单
        RecordModel record = new RecordModel(0, "2025-06-17", "12:00", typeId, "Lunch", -20.5);
        assertTrue(dbHelper.insertRecord(record));

        // 查询账单
        List<RecordModel> records = dbHelper.getRecords();
        assertFalse(records.isEmpty());
        RecordModel r = records.get(0);
        assertEquals("Lunch", r.getDescription());
        assertEquals(-20.5, r.getAmount(), 0.01);
    }

    @Test
    public void testUpdateType() {
        TypeModel type = new TypeModel();
        type.setTypeName("Old Name");
        assertTrue(dbHelper.insertType(type));

        List<TypeModel> types = dbHelper.getTypes();
        TypeModel t = types.get(0);
        t.setTypeName("New Name");

        assertTrue(dbHelper.updateType(t));
        List<TypeModel> updatedTypes = dbHelper.getTypes();
        assertEquals("New Name", updatedTypes.get(0).getTypeName());
    }

    @Test
    public void testDeleteType() {
        TypeModel type = new TypeModel();
        type.setTypeName("Delete Me");
        assertTrue(dbHelper.insertType(type));

        List<TypeModel> types = dbHelper.getTypes();
        int id = types.get(0).getTypeId();

        assertTrue(dbHelper.deleteType(id));
        assertTrue(dbHelper.getTypes().isEmpty());
    }

    @Test
    public void testUpdateRecord() {
        // 添加类型
        TypeModel type = new TypeModel();
        type.setTypeName("Transport");
        assertTrue(dbHelper.insertType(type));
        int typeId = dbHelper.getTypes().get(0).getTypeId();

        // 添加账单
        RecordModel record = new RecordModel(0, "2025-06-17", "08:00", typeId, "Bus", -2.5);
        assertTrue(dbHelper.insertRecord(record));
        RecordModel saved = dbHelper.getRecords().get(0);

        // 更新账单
        saved.setDescription("Metro");
        saved.setAmount(-3.0);
        assertTrue(dbHelper.updateRecord(saved));

        RecordModel updated = dbHelper.getRecords().get(0);
        assertEquals("Metro", updated.getDescription());
        assertEquals(-3.0, updated.getAmount(), 0.01);
    }

    @Test
    public void testDeleteRecord() {
        TypeModel type = new TypeModel();
        type.setTypeName("DeleteTest");
        assertTrue(dbHelper.insertType(type));
        int typeId = dbHelper.getTypes().get(0).getTypeId();

        RecordModel record = new RecordModel(0, "2025-06-17", "09:00", typeId, "DeleteMe", -5.0);
        assertTrue(dbHelper.insertRecord(record));
        int id = dbHelper.getRecords().get(0).getId();

        assertTrue(dbHelper.deleteRecord(id));
        assertTrue(dbHelper.getRecords().isEmpty());
    }
}
