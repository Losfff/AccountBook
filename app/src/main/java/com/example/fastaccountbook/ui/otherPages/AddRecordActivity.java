package com.example.fastaccountbook.ui.otherPages;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastaccountbook.DBController.DBHelper;
import com.example.fastaccountbook.DBController.RecordModel;
import com.example.fastaccountbook.DBController.TypeModel;
import com.example.fastaccountbook.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddRecordActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int selectedTypeId = -1;
    private boolean isExpense = true;

    private Button btnExpense, btnIncome;
    private GridLayout typeGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        dbHelper = new DBHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageButton btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(v -> finish());

        btnExpense = findViewById(R.id.expenseButton);
        btnIncome = findViewById(R.id.incomeButton);

        EditText etAmount = findViewById(R.id.et_amount);
        EditText etDescription = findViewById(R.id.et_description);
        Button btnSubmit = findViewById(R.id.btn_submit);
        typeGrid = findViewById(R.id.type_grid);

        setupTypeButtons();

        btnExpense.setOnClickListener(v -> toggleType(true));
        btnIncome.setOnClickListener(v -> toggleType(false));

        btnSubmit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            String description = etDescription.getText().toString();

            if (selectedTypeId == -1 || amountStr.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (isExpense) amount = -Math.abs(amount);

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

            RecordModel record = new RecordModel(0, date, time, selectedTypeId, description, amount);
            if (dbHelper.insertRecord(record)) {
                Toast.makeText(this, "记账成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleType(boolean expense) {
        isExpense = expense;

        btnExpense.setSelected(expense);
        btnIncome.setSelected(!expense);

        btnExpense.setTextColor(expense ? Color.WHITE : Color.BLACK);
        btnIncome.setTextColor(expense ? Color.BLACK : Color.WHITE);
    }

    private void setupTypeButtons() {
        typeGrid.removeAllViews(); // 确保不重复添加
        List<TypeModel> types = dbHelper.getTypes();

        int columnCount = 4;
        int totalColumns = columnCount;
        typeGrid.setColumnCount(totalColumns);

        for (TypeModel type : types) {
            Button btn = new Button(this);
            btn.setText(type.getTypeName());
            btn.setBackground(getDrawable(R.drawable.circle_button));
            btn.setTextColor(Color.BLACK);
            btn.setAllCaps(false);

            GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.width = 0;  // 关键：宽度设置为 0
            params.height = dpToPx(60);
            params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                selectedTypeId = type.getTypeId();
                highlightSelectedType(btn, typeGrid);
            });

            typeGrid.addView(btn);
        }
    }


    private void highlightSelectedType(Button selectedBtn, GridLayout grid) {
        for (int i = 0; i < grid.getChildCount(); i++) {
            View child = grid.getChildAt(i);
            if (child instanceof Button) {
                child.setBackground(getDrawable(R.drawable.circle_button));
                ((Button) child).setTextColor(Color.BLACK);
            }
        }
        selectedBtn.setBackground(getDrawable(R.drawable.circle_button_selected));
        selectedBtn.setTextColor(Color.WHITE);
    }

    // 工具函数：dp 转 px
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}

