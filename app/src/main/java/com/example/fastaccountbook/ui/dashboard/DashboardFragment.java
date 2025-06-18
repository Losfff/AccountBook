package com.example.fastaccountbook.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.R;
import com.example.fastaccountbook.ui.dashboard.CustomMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardFragment extends Fragment {

    private FrameLayout pieChartContainer;
    private FrameLayout barChartContainer;
    private DashboardViewModel viewModel;

    private List<RecordModel> recordList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        pieChartContainer = root.findViewById(R.id.pie_chart_container);
        barChartContainer = root.findViewById(R.id.bar_chart_container);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        viewModel.getRecords().observe(getViewLifecycleOwner(), records -> {
            recordList = records;
            updateChart();
        });
        loadMockData(); // 加载模拟数据
        updateChart(); // 更新图表

        return root;
    }

    private void loadMockData() {
        // 模拟数据
        recordList.add(new RecordModel(1, "2025-06-01", "10:00", 1, "早餐", -15.5));
        recordList.add(new RecordModel(2, "2025-06-02", "12:00", 1, "午餐", -20.0));
        recordList.add(new RecordModel(3, "2025-06-03", "18:00", 2, "晚餐", -30.0));
        recordList.add(new RecordModel(4, "2025-06-04", "20:00", 3, "购物", -50.0));
        recordList.add(new RecordModel(5, "2025-06-05", "22:00", 1, "宵夜", 10.0));
        // 添加更多模拟数据...
    }

    private void updateChart() {
        pieChartContainer.removeAllViews();
        barChartContainer.removeAllViews();
        showPieChart();
        showBarChart();
    }
    //饼状图
    private void showPieChart() {
        PieChart pieChart = new PieChart(requireContext());
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setCenterText("收支饼图");
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setDrawCenterText(true); // 确保中心文字被绘制

        float income = 0, expense = 0;
        for (RecordModel r : recordList) {
            if (r.getAmount() >= 0) income += r.getAmount();
            else expense += -r.getAmount();
        }

        List<PieEntry> entries = new ArrayList<>();
        if (income > 0) entries.add(new PieEntry(income, "收入"));
        if (expense > 0) entries.add(new PieEntry(expense, "支出"));

        // 自定义颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(255, 99, 132)); // 红色
        colors.add(Color.rgb(44, 125, 184)); // 蓝色

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(colors); // 直接传递颜色列表
        ds.setValueTextColor(Color.WHITE);
        ds.setValueTextSize(14f);
        ds.setDrawValues(true); // 显示数值

        PieData data = new PieData(ds);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000); // 添加动画效果

        // 添加图例
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setFormSize(15f);
        legend.setXEntrySpace(12f);
        legend.setFormToTextSpace(5f);
        legend.setTextColor(Color.BLACK);

        pieChartContainer.addView(pieChart);
    }
    //柱状图
    private void showBarChart() {
        BarChart barChart = new BarChart(requireContext());

        // 图表基本设置
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setExtraBottomOffset(10f);
        barChart.setNoDataText("暂无支出记录");

        // X轴准备
        Calendar today = Calendar.getInstance();
        Calendar startDate = (Calendar) today.clone();
        startDate.add(Calendar.DAY_OF_MONTH, -29); // 最近30天

        List<String> dates = new ArrayList<>();
        float[] expenses = new float[30];

        for (int i = 0; i < 30; i++) {
            Calendar date = (Calendar) startDate.clone();
            date.add(Calendar.DAY_OF_MONTH, i);
            dates.add(new SimpleDateFormat("M.d", Locale.getDefault()).format(date.getTime()));
            expenses[i] = 0f;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (RecordModel r : recordList) {
            try {
                Date recordDate = dateFormat.parse(r.getDate());
                Calendar recordCal = Calendar.getInstance();
                recordCal.setTime(recordDate);

                long diff = (recordCal.getTimeInMillis() - startDate.getTimeInMillis()) / (1000 * 60 * 60 * 24);
                if (diff >= 0 && diff < 30 && r.getAmount() < 0) {
                    expenses[(int) diff] += -r.getAmount();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 构建数据项
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < expenses.length; i++) {
            entries.add(new BarEntry(i, expenses[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(Color.rgb(84, 203, 168)); // 默认柱子颜色（浅绿色）
        dataSet.setHighLightAlpha(255); // 高亮透明度
        dataSet.setHighLightColor(Color.rgb(0, 128, 0)); // 选中柱子的颜色
        dataSet.setDrawValues(false); // 不显示顶部数值

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        // X轴样式
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5); // 最多显示 5 个标签（避免太挤）
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setTextColor(Color.GRAY);

        // Y轴样式
        YAxis leftAxis = barChart.getAxisLeft();

        float maxExpense = 0;
        for (float exp : expenses) {
            if (exp > maxExpense) maxExpense = exp;
        }
        // 设置Y轴范围（增加10%的顶部空间）
        leftAxis.setAxisMaximum(maxExpense * 1.1f+1);

        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.GRAY);

        barChart.getAxisRight().setEnabled(false); // 右侧Y轴不显示

        // 高亮指示样式（十字线）
        barChart.setHighlightPerTapEnabled(true);
        barChart.setHighlightPerDragEnabled(false);

        // 点击提示框
        barChart.setMarker(new CustomMarkerView(requireContext(), R.layout.custom_marker_view, dates, expenses));

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate();

        barChartContainer.addView(barChart);
    }
}







