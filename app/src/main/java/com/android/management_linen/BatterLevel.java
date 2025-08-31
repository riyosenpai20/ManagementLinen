package com.android.management_linen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import static android.os.BatteryManager.EXTRA_LEVEL;

public class BatterLevel {

    public boolean isOk = true;
    private Context mContext;

    public BatterLevel(Context context){

        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(mBroadcastReceiver, filter);
    }

    public void stop() {
        mContext.unregisterReceiver(mBroadcastReceiver);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra(EXTRA_LEVEL, 0);

                if(level < 5)
                    isOk = false;
            }
        }
    };


}
