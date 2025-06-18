package com.example.fastaccountbook.ui.dashboard;

import android.content.Context;
import android.widget.TextView;

import com.example.fastaccountbook.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;

public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;
    private final List<String> dates;
    private final float[] expenses;

    public CustomMarkerView(Context context, int layoutResource, List<String> dates, float[] expenses) {
        super(context, layoutResource);
        this.dates = dates;
        this.expenses = expenses;
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int x = (int) e.getX();
        String date = dates.get(x);
        float value = expenses[x];
        tvContent.setText(date + "日共支出\n¥" + String.format("%.2f", value));
        super.refreshContent(e, highlight);
    }

//    @Override
//    public int getXOffset(float xpos) {
//        return -getWidth() / 2;
//    }
//
//    @Override
//    public int getYOffset(float ypos) {
//        return -getHeight();
//    }

}