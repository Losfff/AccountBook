package com.example.fastaccountbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.fastaccountbook.ui.otherPages.LockActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fastaccountbook.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 初始化导航
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // 确保首次启动时锁定
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isPasswordEnabled = preferences.getBoolean("password_enabled", false);
        String storedPassword = preferences.getString("password_hash", "");

        if (isPasswordEnabled && !storedPassword.isEmpty()) {
            // 如果未设置锁定状态，则设置为锁定
            if (!preferences.contains("is_locked")) {
                preferences.edit().putBoolean("is_locked", true).apply();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 当应用进入后台时，设置锁定状态
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isPasswordEnabled = preferences.getBoolean("password_enabled", false);
        String storedPassword = preferences.getString("password_hash", "");

        if (isPasswordEnabled && !storedPassword.isEmpty()) {
            preferences.edit().putBoolean("is_locked", true).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isPasswordEnabled = preferences.getBoolean("password_enabled", false);
        String storedPassword = preferences.getString("password_hash", "");
        boolean isLocked = preferences.getBoolean("is_locked", false);

        // 如果有密码且应用处于锁定状态，显示锁屏
        if (isPasswordEnabled && !storedPassword.isEmpty() && isLocked) {
            startActivity(new Intent(this, LockActivity.class));
        }
    }
}
