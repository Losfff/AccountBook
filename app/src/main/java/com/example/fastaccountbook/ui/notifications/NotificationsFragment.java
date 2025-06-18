package com.example.fastaccountbook.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fastaccountbook.R;
import com.example.fastaccountbook.ui.otherPages.TypeActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NotificationsFragment extends Fragment {

    private View view;
    private LinearLayout editCategoryLayout;
    private LinearLayout setPasswordLayout;
    private View divider;
    private SwitchMaterial passwordSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editCategoryLayout = view.findViewById(R.id.editCategoryLayout);
        setPasswordLayout = view.findViewById(R.id.setPasswordLayout);
        divider = view.findViewById(R.id.divider);
        passwordSwitch = view.findViewById(R.id.passwordSwitch);

        // 分类管理跳转
        editCategoryLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TypeActivity.class); // 你需要创建这个 Activity
            startActivity(intent);
        });

        // 初始化密码保护开关状态
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("password_enabled", false);
        passwordSwitch.setChecked(enabled);
        setPasswordLayout.setClickable(enabled);
        setPasswordLayout.setAlpha(enabled ? 1.0f : 0.5f);
        divider.setVisibility(enabled ? View.VISIBLE : View.GONE);

        // 监听密码开关
        passwordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setPasswordLayout.setClickable(isChecked);
            setPasswordLayout.setAlpha(isChecked ? 1.0f : 0.5f);
            divider.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            prefs.edit().putBoolean("password_enabled", isChecked).commit();
        });

        // 设置密码弹窗
        setPasswordLayout.setOnClickListener(v -> showPasswordDialog());
    }

    private void showPasswordDialog() {
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedHash = prefs.getString("password_hash", null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置密码");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText oldPassword = new EditText(getContext());
        oldPassword.setHint("原密码");
        oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText newPassword = new EditText(getContext());
        newPassword.setHint("新密码");
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText confirmPassword = new EditText(getContext());
        confirmPassword.setHint("确认新密码");
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if (savedHash != null) {
            layout.addView(oldPassword);
        }
        layout.addView(newPassword);
        layout.addView(confirmPassword);

        builder.setView(layout);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String old = oldPassword.getText().toString();
            String newP = newPassword.getText().toString();
            String confirm = confirmPassword.getText().toString();

            if (!newP.equals(confirm)) {
                Toast.makeText(getContext(), "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (savedHash != null) {
                if (!hash(old).equals(savedHash)) {
                    Toast.makeText(getContext(), "原密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            prefs.edit().putString("password_hash", hash(newP)).apply();
            Toast.makeText(getContext(), "密码设置成功", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
