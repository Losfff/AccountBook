package com.example.fastaccountbook.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fastaccountbook.DBController.DBHelper;
import com.example.fastaccountbook.DBController.RecordGroup;
import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.DBController.TypeModel;
import com.example.fastaccountbook.ui.home.EditRecordActivity;
import com.example.fastaccountbook.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupedRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0, TYPE_ITEM = 1;
    private final List<Object> items = new ArrayList<>();

    public GroupedRecordAdapter(List<RecordGroup> groups) {
        for (RecordGroup group : groups) {
            items.add(group.getDate());           // Header：字符串类型
            items.addAll(group.getRecords());     // Item：RecordModel 类型
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_grouped_transaction, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_transaction, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            ((HeaderViewHolder) holder).dateText.setText((String) items.get(position));
        } else {
            RecordModel t = (RecordModel) items.get(position);
            ((ItemViewHolder) holder).bind(t);
        }
    }

    // Header ViewHolder（用于显示日期）
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.tvDateHeader);
        }
    }

    // Item ViewHolder（用于显示一条账单）
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView time, type, description, amount;
        DBHelper dbHelper;

        ItemViewHolder(View itemView) {
            super(itemView);
            dbHelper = new DBHelper(itemView.getContext());
            time = itemView.findViewById(R.id.tvTime);
            type = itemView.findViewById(R.id.tvType);
            description = itemView.findViewById(R.id.tvDescription);
            amount = itemView.findViewById(R.id.tvAmount);
        }

        void bind(RecordModel t) {
            // 设置账单类型名
            List<TypeModel> types = dbHelper.getTypes();
            for (TypeModel ts : types) {
                if (ts.getTypeId() == t.getTypeId()) {
                    type.setText(ts.getTypeName());
                    break;
                }
            }

            // 设置账单其他字段
            time.setText(t.getTime());
            description.setText(t.getDescription());
            amount.setText("￥"+String.valueOf(t.getAmount()));

            // 设置点击跳转编辑
            itemView.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, EditRecordActivity.class);
                intent.putExtra("recordId", t.getId());
                context.startActivity(intent);
            });
        }
    }
}
