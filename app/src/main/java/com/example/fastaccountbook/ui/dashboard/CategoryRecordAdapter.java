package com.example.fastaccountbook.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.R;

import java.util.List;

public class CategoryRecordAdapter extends RecyclerView.Adapter<CategoryRecordAdapter.ViewHolder> {

    private final List<RecordModel> recordList;

    public CategoryRecordAdapter(List<RecordModel> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_record, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordModel r = recordList.get(position);
        holder.tvDate.setText(r.getDate() + " " + r.getTime());
        holder.tvDesc.setText(r.getDescription());
        holder.tvAmount.setText(String.format("-¥%.2f", Math.abs(r.getAmount())));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDesc, tvAmount;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_record_date);
            tvDesc = view.findViewById(R.id.tv_record_desc);
            tvAmount = view.findViewById(R.id.tv_record_amount);
        }
    }
}
