package com.example.fastaccountbook.ui.home;

import com.example.fastaccountbook.DBController.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastaccountbook.R;
import com.example.fastaccountbook.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private GroupedRecordAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DBHelper dbHelper = new DBHelper(getContext());
        insertSampleData(dbHelper); // ✅ 调用插入方法
        List<RecordModel> all = dbHelper.getRecords();


        Map<String, List<RecordModel>> groupedMap = new LinkedHashMap<>();
        for (RecordModel t : all) {
            if (!groupedMap.containsKey(t.getDate())) {
                groupedMap.put(t.getDate(), new ArrayList<>());
            }
            String d = t.getDate();
            groupedMap.get(d).add(t);
        }

        List<RecordGroup> grouped = new ArrayList<>();
        for (Map.Entry<String, List<RecordModel>> entry : groupedMap.entrySet()) {
            grouped.add(new RecordGroup(entry.getKey(), entry.getValue()));
        }

        Log.d("DB", "record size: " + all.size());

        adapter = new GroupedRecordAdapter(grouped);
        recyclerView.setAdapter(adapter);
    }

    private void insertSampleData(DBHelper dbHelper) {
        List<RecordModel> existing = dbHelper.getRecords();
        if (!existing.isEmpty()) return; // 已有数据，不重复插入

        // 添加类型（假设 1: 餐饮, 2: 交通, 3: 购物）
        dbHelper.insertType(new TypeModel(1, "餐饮"));
        dbHelper.insertType(new TypeModel(2, "交通"));
        dbHelper.insertType(new TypeModel(3, "购物"));

        // 插入账单数据
        dbHelper.insertRecord(new RecordModel(1,"2025-06-15", "12:30", 1, "午饭", 35.5));
        dbHelper.insertRecord(new RecordModel(2,"2025-06-15", "18:40", 3, "超市购物", 120.0));
        dbHelper.insertRecord(new RecordModel(3,"2025-06-14", "08:00", 2, "公交", 2.0));
        dbHelper.insertRecord(new RecordModel(4,"2025-06-14", "19:15", 1, "晚餐", 42.0));
        dbHelper.insertRecord(new RecordModel(5,"2025-06-13", "13:00", 3, "网购", 89.9));
    }


}