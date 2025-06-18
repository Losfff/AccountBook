package com.example.fastaccountbook.ui.otherPages;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastaccountbook.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LockActivity extends AppCompatActivity {

    private EditText editPassword;
    private Button btnUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        editPassword = findViewById(R.id.editPassword);
        btnUnlock = findViewById(R.id.btnUnlock);

        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnUnlock.setOnClickListener(v -> {
            String input = editPassword.getText().toString();
            String inputHashed = hashPassword(input);

            SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
            String stored = preferences.getString("password_hash", "");

            if (inputHashed.equals(stored)) {
                preferences.edit().putBoolean("is_locked", false).apply();
                finish(); // 解锁成功，返回主界面
            } else {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String hashPassword(String password) {
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
