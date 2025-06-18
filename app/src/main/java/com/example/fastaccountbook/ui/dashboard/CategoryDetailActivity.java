package com.example.fastaccountbook.ui.dashboard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastaccountbook.DBController.DBHelper;
import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle;
    private CategoryRecordAdapter adapter;
    private List<RecordModel> allRecords;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        recyclerView = findViewById(R.id.recycler_category_detail);
        tvTitle = findViewById(R.id.tv_category_title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int typeId = getIntent().getIntExtra("typeId", -1);
        String typeName = getIntent().getStringExtra("typeName");
        tvTitle.setText(typeName + " 详情");

        DBHelper dbHelper = new DBHelper(this);
        allRecords = dbHelper.getRecordsByTypeId(typeId);

        adapter = new CategoryRecordAdapter(allRecords);
        recyclerView.setAdapter(adapter);
    }
}
