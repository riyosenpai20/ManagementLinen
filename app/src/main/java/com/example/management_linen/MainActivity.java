package com.example.management_linen;
import android.content.SharedPreferences;


//import com.UHF.scanlable.UHfData.UHfGetData;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.hardware.UHFDevice;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    String token;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    //******
    UHFDevice mUHFDevice;
    ToolsHelper mToolsHelper;

    private boolean fromSTORuangan = false;
    private boolean fromSearchCard = false;
    private boolean fromRuangActivity = false;

    private VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        fromSTORuangan = sharedPreferences.getBoolean("fromSTORuangan", false);
        fromSearchCard = sharedPreferences.getBoolean("fromSearchCard", false);
        fromRuangActivity = sharedPreferences.getBoolean("fromRuangActivity", false);

        if (token == null) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Set up ActionBar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Scan Linen");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        mUHFDevice = new UHFDevice(this);
        mToolsHelper = new ToolsHelper(this);
        mToolsHelper.initSound();

        // Initialize ViewPager and TabLayout
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        
        // Setup ViewPager with adapter
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Connect TabLayout with ViewPager2
        String[] tabTitles = new String[]{
            getString(R.string.tab_scan),
            getString(R.string.tab_rw),
            getString(R.string.tab_param)
        };
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu with logout option
        menu.add(Menu.NONE, 1, Menu.NONE, "Logout")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle back button click
            onBackPressed();
            return true;
        } else if (id == 1) {
            // Handle logout
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        //super.onResume();
        // TODO Auto-generated method stub

        mVirtualKeyListenerBroadcastReceiver = new VirtualKeyListenerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        this.registerReceiver(mVirtualKeyListenerBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);

        try {
            mUHFDevice.UhfOpen_long();
            mUHFDevice.setTriggerkey(0);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //耗时操作执行
                        SystemClock.sleep(2000);
//                        int result = Reader.rrlib.Connect(mUHFDevice.SerialDev(), mUHFDevice.BaudrateDev_3(), 1);
//                        if (result == 0) {
//                            ToolsHelper.show(MainActivity.this, getString(R.string.openport_success));
//                        } else {
//                            ToolsHelper.show(MainActivity.this, getString(R.string.openport_failed));
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToolsHelper.show(MainActivity.this, "Error connecting to device: " + e.getMessage());
                    }
                }
            }).start();

            ToolsHelper.show(MainActivity.this, "正在连接.....");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            ToolsHelper.show(MainActivity.this, "Native library not found. RFID functionality will be limited.");
        } catch (Exception e) {
            e.printStackTrace();
            ToolsHelper.show(MainActivity.this, "Error initializing RFID: " + e.getMessage());
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //Reader.rrlib.DisConnect();
        super.onPause();

        mUHFDevice.UhfStop_long();
        mUHFDevice.setTriggerkey(1);
        this.unregisterReceiver(mVirtualKeyListenerBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        Reader.rrlib.DisConnect();
    }

    @Override
    public void onBackPressed() {
        if(fromSTORuangan) {
            // Navigasi ke HomeActivity
            Intent intent = new Intent(this, STORuanganActivity.class);
            startActivity(intent);
        } else if (fromSearchCard && fromRuangActivity) {
            // Navigasi ke HomeActivity
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
        }

        finish();

        // Animasi transisi
        if (getResources().getIdentifier("slide_in_left", "anim", getPackageName()) != 0 && 
            getResources().getIdentifier("slide_out_right", "anim", getPackageName()) != 0) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private class VirtualKeyListenerBroadcastReceiver extends BroadcastReceiver {
        private final String SYSTEM_REASON = "reason";
        private final String SYSTEM_HOME_KEY = "homekey";
        private final String SYSTEM_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String systemReason = intent.getStringExtra(SYSTEM_REASON);
                if (systemReason != null) {
                    mUHFDevice.UhfStop_long();
                    mUHFDevice.setTriggerkey(1);
                    if (systemReason.equals(SYSTEM_HOME_KEY)) {
                        System.out.println("Press HOME key");
                    } else if (systemReason.equals(SYSTEM_RECENT_APPS)) {
                        System.out.println("Press RECENT_APPS key");
                    }
                }
            }
        }
    }


}
