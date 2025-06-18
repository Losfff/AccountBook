package com.example.fastaccountbook.ui.home;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastaccountbook.DBController.DBHelper;
import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.DBController.TypeModel;
import com.example.fastaccountbook.R;

import java.util.List;

public class EditRecordActivity extends AppCompatActivity {
    private EditText etDescription, etAmount;
    private String etDate,etTime;
    private Spinner spType;
    private DBHelper dbHelper;
    private int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        dbHelper = new DBHelper(this);
        recordId = getIntent().getIntExtra("recordId", -1);

        // 初始化控件（只保留需要编辑的字段）
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        spType = findViewById(R.id.spType);

        List<TypeModel> types = dbHelper.getTypes();
        ArrayAdapter<TypeModel> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        // 设置Spinner适配器
        setupSpinner();

        // 加载原始记录数据
        loadRecord();

        // 保存按钮点击事件
        findViewById(R.id.btnSave).setOnClickListener(v -> saveRecord());
    }

    private void setupSpinner() {
        List<TypeModel> types = dbHelper.getTypes();
        ArrayAdapter<TypeModel> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
    }

    private void loadRecord() {
        for (RecordModel record : dbHelper.getRecords()) {
            if (record.getId() == recordId) {
                etDate = record.getDate();
                etTime = record.getTime();
                etDescription.setText(record.getDescription());
                etAmount.setText(String.valueOf(record.getAmount()));

                // 设置Spinner选中项
                for (int i = 0; i < spType.getCount(); i++) {
                    TypeModel item = (TypeModel) spType.getItemAtPosition(i);
                    if (item.getTypeId() == record.getTypeId()) {
                        spType.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
    private void saveRecord () {
        RecordModel updated = new RecordModel();
        updated.setId(recordId);
        updated.setDate(etDate);
        updated.setTime(etTime);
        updated.setDescription(etDescription.getText().toString());
        updated.setAmount(Double.parseDouble(etAmount.getText().toString()));

        TypeModel selectedType = (TypeModel) spType.getSelectedItem();
        updated.setTypeId(selectedType.getTypeId());

        dbHelper.updateRecord(updated);
        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        finish(); // 返回原页面
    }
}
