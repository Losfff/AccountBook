package com.example.fastaccountbook.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastaccountbook.DBController.DBHelper;
import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.DBController.TypeModel;

import com.example.fastaccountbook.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
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

    private PieChart pieChart; // 饼图控件
    private BarChart barChart; // 柱状图控件
    private RecyclerView categoryList; // 分类列表控件
    private TextView tvToggle, tvFilterMonth; // 切换显示和筛选按钮
    private List<RecordModel> recordList = new ArrayList<>(); // 记录列表
    private CategoryBarAdapter adapter; // 分类条形图适配器
    private boolean filterThisMonth = false; // 是否筛选本月记录

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 初始化控件
        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);
        categoryList = view.findViewById(R.id.category_bar_list);
        tvToggle = view.findViewById(R.id.tv_toggle);
        tvFilterMonth = view.findViewById(R.id.tv_filter_month);

        // 设置分类列表的布局管理器
        categoryList.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化数据库帮助类并获取记录
        DBHelper dbHelper = new DBHelper(requireContext());
        recordList = dbHelper.getRecords();

        // 设置筛选按钮的点击事件
        tvFilterMonth.setOnClickListener(v -> {
            filterThisMonth = !filterThisMonth; // 切换筛选状态
            tvFilterMonth.setText(filterThisMonth ? "查看全部 ▲" : "仅查看本月 ▼"); // 更新按钮文字
            refreshCharts(); // 刷新图表
        });

        // 加载模拟数据（仅用于测试）
        loadMockData();

        // 刷新图表
        refreshCharts();

        categoryList.setNestedScrollingEnabled(false);



        return view;
    }

    private void loadMockData() {
        // 模拟数据，用于测试
        recordList.add(new RecordModel(1, "2025-06-01", "10:00", 1, "早餐", -15.5));
        recordList.add(new RecordModel(2, "2025-06-02", "12:00", 1, "午餐", -20.0));
        recordList.add(new RecordModel(3, "2025-06-03", "18:00", 2, "晚餐", -30.0));
        recordList.add(new RecordModel(4, "2025-06-04", "20:00", 3, "购物", -50.0));
        recordList.add(new RecordModel(5, "2025-06-05", "22:00", 1, "宵夜", 10.0));
        // 添加更多模拟数据...
    }

    private void refreshCharts() {
        // 根据筛选条件过滤记录
        List<RecordModel> filtered = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH);

        for (RecordModel r : recordList) {
            if (!filterThisMonth) {
                filtered.add(r); // 不筛选时直接添加所有记录
            } else {
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(r.getDate()));
                    if (c.get(Calendar.YEAR) == curYear && c.get(Calendar.MONTH) == curMonth) {
                        filtered.add(r); // 筛选本月记录
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 更新图表数据
        showPieChart(filtered);
        showBarChart(filtered);
        showCategoryBars(filtered);
    }

    private void showPieChart(List<RecordModel> list) {
        // 获取分类数据
        DBHelper dbHelper = new DBHelper(requireContext());
        List<TypeModel> typeList = dbHelper.getTypes();

        // 建立 typeId => typeName 映射表
        Map<Integer, String> typeNameMap = new HashMap<>();
        for (TypeModel t : typeList) {
            typeNameMap.put(t.getTypeId(), t.getTypeName());
        }

        // 汇总每个 typeName 的支出金额
        Map<String, Float> map = new LinkedHashMap<>();
        for (RecordModel r : list) {
            if (r.getAmount() < 0) { // 只统计支出
                String typeName = typeNameMap.get(r.getTypeId());
                if (typeName == null) typeName = "未知";

                float amt = map.getOrDefault(typeName, 0f);
                amt += -r.getAmount(); // 累加支出金额
                map.put(typeName, amt);
            }
        }

        // 构造饼图数据
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(Color.rgb(76, 175, 80), Color.rgb(139, 195, 74), Color.rgb(205, 220, 57),
                Color.rgb(255, 193, 7), Color.rgb(255, 87, 34)); // 设置颜色
        ds.setValueTextColor(Color.WHITE); // 设置文字颜色
        ds.setValueTextSize(14f); // 设置文字大小
        ds.setSliceSpace(3f); // 设置扇区间距

        PieData data = new PieData(ds);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true); // 显示百分比
        pieChart.setHoleRadius(55f); // 设置中心孔半径
        pieChart.setTransparentCircleRadius(60f); // 设置透明圆环半径
        pieChart.setCenterText("支出构成"); // 设置中心文字
        pieChart.setCenterTextSize(18f); // 设置中心文字大小
        pieChart.getDescription().setEnabled(false); // 禁用描述
        pieChart.setDrawEntryLabels(false); // 不绘制扇区标签
        pieChart.setRotationEnabled(false); // 禁用旋转
        pieChart.animateY(1000); // Y轴动画

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true); // 启用图例
        legend.setTextColor(Color.DKGRAY); // 设置图例文字颜色
    }

    private void showBarChart(List<RecordModel> list) {
        // 清空柱状图数据
        barChart.clear();
        barChart.setDrawBarShadow(false); // 不绘制阴影
        barChart.setDrawValueAboveBar(true); // 在柱状图上方绘制值
        barChart.getDescription().setEnabled(false); // 禁用描述
        barChart.setPinchZoom(false); // 禁用缩放
        barChart.setDrawGridBackground(false); // 不绘制网格
        barChart.setExtraBottomOffset(10f); // 设置底部偏移
        barChart.setNoDataText("暂无支出记录"); // 设置无数据时的文本

        // 获取当前日期和起始日期（30天前）
        Calendar today = Calendar.getInstance();
        Calendar startDate = (Calendar) today.clone();
        startDate.add(Calendar.DAY_OF_MONTH, -29);

        // 初始化日期列表和支出数组
        List<String> dates = new ArrayList<>();
        float[] expenses = new float[30];

        for (int i = 0; i < 30; i++) {
            Calendar date = (Calendar) startDate.clone();
            date.add(Calendar.DAY_OF_MONTH, i);
            dates.add(new SimpleDateFormat("M.d", Locale.getDefault()).format(date.getTime()));
            expenses[i] = 0f;
        }

        // 解析记录日期并累加支出
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (RecordModel r : list) {
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

        // 构造柱状图数据
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < expenses.length; i++) {
            entries.add(new BarEntry(i, expenses[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(Color.rgb(84, 203, 168)); // 设置柱状图颜色
        dataSet.setHighLightAlpha(255); // 设置高亮透明度
        dataSet.setHighLightColor(Color.rgb(0, 128, 0)); // 设置高亮颜色
        dataSet.setDrawValues(false); // 不绘制值

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f); // 设置柱状图宽度

        // 设置X轴
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // 设置最小间隔
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置位置
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates)); // 设置值格式化器
        xAxis.setDrawGridLines(false); // 不绘制网格
        xAxis.setTextColor(Color.GRAY); // 设置文字颜色

        // 设置Y轴
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // 不绘制网格
        leftAxis.setAxisMinimum(0f); // 设置最小值
        leftAxis.setTextColor(Color.GRAY); // 设置文字颜色
        barChart.getAxisRight().setEnabled(false); // 禁用右侧Y轴
        // 点击提示框
        barChart.setMarker(new CustomMarkerView(requireContext(), R.layout.custom_marker_view, dates, expenses));

        barChart.setFitBars(true); // 自适应柱状图宽度
        barChart.setData(data); // 设置数据
        barChart.animateY(1000); // Y轴动画
        barChart.invalidate(); // 刷新图表
    }

    private void showCategoryBars(List<RecordModel> list) {
        // 获取分类数据
        DBHelper dbHelper = new DBHelper(requireContext());
        List<TypeModel> types = dbHelper.getTypes();
        Map<Integer, Double> expenseMap = new HashMap<>();

        // 汇总每个分类的支出金额
        for (RecordModel r : list) {
            if (r.getAmount() < 0) {
                int id = r.getTypeId();
                double amt = expenseMap.getOrDefault(id, 0.0);
                expenseMap.put(id, amt + (-r.getAmount()));
            }
        }

        // 根据支出金额对分类进行排序
        types.sort((a, b) -> Double.compare(
                expenseMap.getOrDefault(b.getTypeId(), 0.0),
                expenseMap.getOrDefault(a.getTypeId(), 0.0)));

        // 初始化适配器
        adapter = new CategoryBarAdapter(types, expenseMap, type -> {
            // 点击分类时跳转到分类详情页面
            Intent intent = new Intent(getContext(), CategoryDetailActivity.class);
            intent.putExtra("typeId", type.getTypeId());
            intent.putExtra("typeName", type.getTypeName());
            startActivity(intent);
        });

        // 设置分类列表适配器
        categoryList.setAdapter(adapter);

        // 设置展开/收起按钮点击事件
        tvToggle.setOnClickListener(v -> {
            adapter.toggleExpanded(); // 切换展开状态
            tvToggle.setText(adapter.getItemCount() > 4 ? "收起 ⌃" : "展开更多 ⌄"); // 更新按钮文字
        });
    }
}