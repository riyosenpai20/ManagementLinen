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
    private boolean fromSTORuangan = false;
    private boolean fromRuanganActivity = false;
    private boolean fromSearchCard = false; // Penanda jika dibuka dari cardSearch
    private boolean fromCardSto = false; // Penanda jika dibuka dari cardSto
    private String cardType = ""; // Menambahkan variabel untuk menyimpan jenis card
    private TabHost myTabHost;
    private Toolbar toolbar;

    //******
    UHFDevice mUHFDevice;
    ToolsHelper mToolsHelper;

    private VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver;

    // Static block to load native libraries
    static {
        try {
            System.loadLibrary("serial_port");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            // Cannot show toast here as it's a static block
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        // Check if MainActivity is opened from STORuanganActivity
        fromSTORuangan = sharedpreferences.getBoolean("fromSTORuangan", false);
        // Check if MainActivity is opened from RuanganActivity
        fromRuanganActivity = sharedpreferences.getBoolean("fromRuanganActivity", false);
        // Check if MainActivity is opened from cardSearch or cardSto
        fromSearchCard = sharedpreferences.getBoolean("fromSearchCard", false);
        fromCardSto = sharedpreferences.getBoolean("fromCardSto", false);
        cardType = sharedpreferences.getString("cardType", "");
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
                // Pastikan perilaku sama dengan tombol back di navigasi
                ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
                
                // Check if MainActivity was opened from STORuanganActivity
                if(fromSTORuangan && intLayout == 2){
                    intLayout = 1;
                    // Clear the flag since we're navigating back
                    sharedpreferences.edit().putBoolean("fromSTORuangan", false).apply();
                    Intent intent = new Intent(MainActivity.this, STORuanganActivity.class);
                    intent.putExtra("tagList", tagList2);
                    intent.putExtra("pdf_title", pdf_title);
                    startActivity(intent);
                    finish();
                } 
                // Check if MainActivity was opened from RuanganActivity
                else if(fromSearchCard && intLayout == 2){
                    intLayout = 1;
                    // Clear the flag since we're navigating back
                    sharedpreferences.edit().putBoolean("fromSearchCard", false).apply();
                    Intent intent = new Intent(MainActivity.this, RuanganActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(intLayout == 2){
                    // Handle other navigation scenarios here if needed
                    intLayout = 1;
                    MainActivity.super.onBackPressed();
                } else {
                    MainActivity.super.onBackPressed();
                }
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
            // Gunakan logika yang sama dengan tombol back di appbar
            ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
            
            // Check if MainActivity was opened from STORuanganActivity
            if(fromSTORuangan && intLayout == 2){
                intLayout = 1;
                // Clear the flag since we're navigating back
                sharedpreferences.edit().putBoolean("fromSTORuangan", false).apply();
                Intent intent = new Intent(this, STORuanganActivity.class);
                intent.putExtra("tagList", tagList2);
                intent.putExtra("pdf_title", pdf_title);
                startActivity(intent);
                finish();
            } 
            // Check if MainActivity was opened from RuanganActivity
            else if(fromSearchCard && intLayout == 2){
                intLayout = 1;
                // Clear the flag since we're navigating back
                sharedpreferences.edit().putBoolean("fromSearchCard", false).apply();
                Intent intent = new Intent(this, RuanganActivity.class);
                startActivity(intent);
                finish();
            }
            else if(intLayout == 2){
                // Handle other navigation scenarios here if needed
                intLayout = 1;
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        try {
            // Load the native library explicitly
            System.loadLibrary("serial_port");
            
            mUHFDevice.UhfOpen_long();
            mUHFDevice.setTriggerkey(0);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //耗时操作执行
                        SystemClock.sleep(2000);
                        int result = Reader.rrlib.Connect(mUHFDevice.SerialDev(), mUHFDevice.BaudrateDev_3(),1);
                        if(result ==0){
                            ToolsHelper.show(MainActivity.this, getString(R.string.openport_success));
                        }else {
                            ToolsHelper.show(MainActivity.this, getString(R.string.openport_failed));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToolsHelper.show(MainActivity.this, "Error connecting to device: " + e.getMessage());
                    }
                }
            }).start();

            ToolsHelper.show(MainActivity.this, "Connecting...");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            ToolsHelper.show(MainActivity.this, "Native library not found. RFID functionality will be limited.");
        } catch (Exception e) {
            e.printStackTrace();
            ToolsHelper.show(MainActivity.this, "Error initializing RFID: " + e.getMessage());
        }

//        if (sharedpreferences != null) {
//            sharedpreferences.edit().putBoolean("fromSTORuangan", false).apply();
//            sharedpreferences.edit().putBoolean("fromRuanganActivity", false).apply();
//            sharedpreferences.edit().putBoolean("fromSearchCard", false).apply();
//            sharedpreferences.edit().putBoolean("fromCardSto", false).apply();
//
//            System.out.println("fromSTORuangan2: " + fromSTORuangan);
//            System.out.println("fromSearchCard2: " + fromSearchCard);
//        }

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

    /**
     * Handle back button press
     * Note: onBackPressed() is deprecated in newer Android versions, but still works in TabActivity
     * We can't use OnBackPressedCallback here because TabActivity doesn't support it
     * 
     * This method is now consistent with the back button in the toolbar and action bar
     */
    @Override
    public void onBackPressed() {
        ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
        System.out.println("Tag2: " + tagList2);
        System.out.println("MainActivity fromSTORuangan: " + fromSTORuangan);
        System.out.println("MainActivity fromSearchCard: " + fromSearchCard);

        // Check if MainActivity was opened from STORuanganActivity
        if(fromSTORuangan && intLayout == 2){
            intLayout = 1;
            // Clear the flag since we're navigating back
            sharedpreferences.edit().putBoolean("fromSTORuangan", false).apply();
            Intent intent = new Intent(this, STORuanganActivity.class);
            intent.putExtra("tagList", tagList2);
            intent.putExtra("pdf_title", pdf_title);
            startActivity(intent);
            finish();
        } 
        // Check if MainActivity was opened from RuanganActivity
        else if(fromSearchCard && intLayout == 2){
            intLayout = 1;
            // Clear the flag since we're navigating back
            sharedpreferences.edit().putBoolean("fromSearchCard", false).apply();
            Intent intent = new Intent(this, RuanganActivity.class);
            startActivity(intent);
            finish();
        }
        else if(intLayout == 2){
            // Handle other navigation scenarios here if needed
            intLayout = 1;
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
