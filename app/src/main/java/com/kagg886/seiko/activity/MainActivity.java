package com.kagg886.seiko.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.kagg886.seiko.R;
import com.kagg886.seiko.adapter.BotAdapter;
import com.kagg886.seiko.adapter.ModuleAdapter;
import com.kagg886.seiko.fragment.module.DICFragment;
import com.kagg886.seiko.fragment.module.LoginFragment;
import com.kagg886.seiko.fragment.module.PluginFragment;
import com.kagg886.seiko.fragment.module.SettingsFragment;
import com.kagg886.seiko.service.BotRunnerService;
import com.kagg886.seiko.util.IOUtil;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.activity
 * @className: MainActivity
 * @author: kagg886
 * @description: 主活动
 * @date: 2022/12/12 18:55
 * @version: 1.0
 */
public class MainActivity extends AppCompatActivity {

    private LinearLayout rootView;
    private TabLayout layout;
    private ViewPager pager;
    private ModuleAdapter adapter;

    private ActivityResult result;

    public ActivityResultLauncher<Intent> verifyCall = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> MainActivity.this.result = result);

    public ActivityResult getResult() {
        while (result == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        ActivityResult p = result;
        result = null;
        return p;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.activity_main_view_tab_layout);
        pager = findViewById(R.id.activity_main_view_view_pager);
        adapter = new ModuleAdapter(getSupportFragmentManager());
        rootView = findViewById(R.id.activity_main_root);

        BotAdapter.registerActivityResult(this);

        Intent a = new Intent(this, BotRunnerService.class);
        BotRunnerService.avt = this;
        if (BotRunnerService.INSTANCE == null) {
            startForegroundService(a);
            snack("绑定服务成功");
        }

        ArrayList<ModuleAdapter.Structure> fragments = new ArrayList<>();
        fragments.add(new ModuleAdapter.Structure("BOT列表", new LoginFragment()));
        fragments.add(new ModuleAdapter.Structure("插件", new PluginFragment()));
        fragments.add(new ModuleAdapter.Structure("词库",new DICFragment()));
        fragments.add(new ModuleAdapter.Structure("设置", new SettingsFragment()));
        adapter.setViews(fragments);
        pager.setAdapter(adapter);
        layout.setupWithViewPager(pager);
    }

    public void snack(String text) {
        Snackbar.make(rootView, text, BaseTransientBottomBar.LENGTH_LONG).show();
    }
}