package com.example.fastaccountbook.ui.otherPages;

//import static android.os.Build.VERSION_CODES.R;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.example.fastaccountbook.DBController.TypeModel;
import com.example.fastaccountbook.R;
import com.example.fastaccountbook.DBController.DBHelper;

public class TypeActivity extends AppCompatActivity {
    private ImageButton backButton, addButton;
    private TextView titleTextView;
    private RecyclerView typeRecyclerView;
    private TypeAdapter typeAdapter;
    private List<TypeModel> typeList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // 初始化数据库
        dbHelper = new DBHelper(this);

        // 初始化界面元素
        backButton = findViewById(R.id.backButton);
        titleTextView = findViewById(R.id.titleTextView);
        typeRecyclerView = findViewById(R.id.typeRecyclerView);
        addButton = findViewById(R.id.fabAdd);

        // 设置返回按钮点击事件
        backButton.setOnClickListener(v -> finish());

        // 初始化账目类别列表
        typeList = new ArrayList<>();
        typeAdapter = new TypeAdapter(typeList);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        typeRecyclerView.setAdapter(typeAdapter);

        // 加载账目类别数据
        loadTypes();

        // 设置添加按钮点击事件
        addButton.setOnClickListener(v -> showAddTypeDialog());
    }

    private void loadTypes() {
        typeList.clear();
        typeList.addAll(dbHelper.getTypes());
        typeAdapter.notifyDataSetChanged();
    }

    private void showAddTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_type_edit, null);

        final EditText typeNameEditText = dialogView.findViewById(R.id.typeNameEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String typeName = typeNameEditText.getText().toString().trim();
            if (!typeName.isEmpty()) {
                TypeModel typeModel = new TypeModel();
                typeModel.setTypeName(typeName);
                if (dbHelper.insertType(typeModel)) {
                    Toast.makeText(TypeActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    loadTypes();
                } else {
                    Toast.makeText(TypeActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(TypeActivity.this, "类别名称不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // 自定义适配器
    private class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder> {
        private List<TypeModel> typeList;

        public TypeAdapter(List<TypeModel> typeList) {
            this.typeList = typeList;
        }

        @Override
        public TypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new TypeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TypeViewHolder holder, int position) {
            TypeModel typeModel = typeList.get(position);
            holder.typeNameTextView.setText(typeModel.getTypeName());

            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(typeModel.getTypeId());
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return typeList.size();
        }

        public class TypeViewHolder extends RecyclerView.ViewHolder {
            TextView typeNameTextView;

            public TypeViewHolder(View itemView) {
                super(itemView);
                typeNameTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }

    private void showDeleteDialog(final int typeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要删除这个类别吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbHelper.deleteType(typeId)) {
                            Toast.makeText(TypeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            loadTypes();
                        } else {
                            Toast.makeText(TypeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}