package com.android.management_linen;
import android.content.SharedPreferences;


//import com.UHF.scanlable.UHfData.UHfGetData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.UHFDevice;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends TabActivity {
    private int intLayout = 2;
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    String token, namePIC, pdf_title, namaPerusahaan;

    private TabHost myTabHost;
    private Toolbar toolbar;

    //******
    UHFDevice mUHFDevice;
    ToolsHelper mToolsHelper;

    private VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        if (token == null) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        
        // Set up toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Scan");
        
        // Set navigation icon (back button)
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mUHFDevice = new UHFDevice(this);
        mToolsHelper = new ToolsHelper(this);
        mToolsHelper.initSound();

        myTabHost = getTabHost();
        Intent intent0 = new Intent(this,ScanMode.class);
        Intent intent1 = new Intent(this,ReadWriteActivity.class);
        Intent intent2 = new Intent(this,ScanView.class);
        Intent intent3 = new Intent(this,FSTActivity.class);
//        Intent appSetup = new Intent(this, SetUpActivity.class);

        TabHost.TabSpec tabSpec0 = myTabHost.newTabSpec(getString(R.string.tab_scan)).setIndicator(getString(R.string.tab_scan)).setContent(intent0);
        TabHost.TabSpec tabSpec1 = myTabHost.newTabSpec(getString(R.string.tab_rw)).setIndicator(getString(R.string.tab_rw)).setContent(intent1);
        TabHost.TabSpec tabSpec2 = myTabHost.newTabSpec(getString(R.string.tab_param)).setIndicator(getString(R.string.tab_param)).setContent(intent2);
        //	TabHost.TabSpec tabSpec3 = myTabHost.newTabSpec(getString(R.string.tab_fst)).setIndicator(getString(R.string.tab_fst)).setContent(intent3);
//        TabHost.TabSpec setUp = myTabHost.newTabSpec(getString(R.string.tab_setup)).setIndicator(getString(R.string.tab_setup)).setContent(appSetup);

        myTabHost.addTab(tabSpec0);
        myTabHost.addTab(tabSpec1);
        //	myTabHost.addTab(tabSpec3);
        myTabHost.addTab(tabSpec2);
//        myTabHost.addTab(setUp);
        myTabHost.setCurrentTab(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        mVirtualKeyListenerBroadcastReceiver = new VirtualKeyListenerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        this.registerReceiver(mVirtualKeyListenerBroadcastReceiver, intentFilter);

        mUHFDevice.UhfOpen_long();
        mUHFDevice.setTriggerkey(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时操作执行
                SystemClock.sleep(2000);
                int result = Reader.rrlib.Connect(mUHFDevice.SerialDev(), mUHFDevice.BaudrateDev_3(),1);
                if(result ==0){
                    ToolsHelper.show(MainActivity.this, getString(R.string.openport_success));
                }else {
                    ToolsHelper.show(MainActivity.this, getString(R.string.openport_failed));
                }
            }
        }).start();

        ToolsHelper.show(MainActivity.this, "正在连接.....");

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

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
        System.out.println("Tag2: " + tagList2);
        if(intLayout == 2){
            intLayout = 1;
            Intent intent = new Intent(this, STORuanganActivity.class);
            intent.putExtra("tagList", tagList2);
            intent.putExtra("pdf_title", pdf_title);
            startActivity(intent);
            finish();

        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
