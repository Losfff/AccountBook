package com.example.fastaccountbook.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastaccountbook.DBController.TypeModel;
import com.example.fastaccountbook.R;

import java.util.List;
import java.util.Map;

public class CategoryBarAdapter extends RecyclerView.Adapter<CategoryBarAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(TypeModel type);
    }

    private final List<TypeModel> typeList;
    private final Map<Integer, Double> expenseMap;
    private final double maxAmount;
    private boolean isExpanded = false;
    private static final int PREVIEW_LIMIT = 5;
    private final OnCategoryClickListener listener;

    public CategoryBarAdapter(List<TypeModel> typeList, Map<Integer, Double> expenseMap, OnCategoryClickListener listener) {
        this.typeList = typeList;
        this.expenseMap = expenseMap;
        this.listener = listener;
        this.maxAmount = expenseMap.values().stream().mapToDouble(d -> d).max().orElse(1.0);
    }

    public void toggleExpanded() {
        isExpanded = !isExpanded;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_bar, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TypeModel type = typeList.get(pos);
        double amt = expenseMap.getOrDefault(type.getTypeId(), 0.0);
        h.tvType.setText(type.getTypeName());
        h.tvAmount.setText(String.format("¥%.2f", amt));

        int progress = maxAmount == 0 ? 0 : (int) ((amt / maxAmount) * 100);
        h.progressBar.setProgress(progress);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(type);
        });
    }

    @Override
    public int getItemCount() {
        return isExpanded ? typeList.size() : Math.min(PREVIEW_LIMIT, typeList.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount;
        ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            tvType = view.findViewById(R.id.tvType);
            tvAmount = view.findViewById(R.id.tvAmount);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }
}
