package com.example.management_linen;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Looper;
import android.widget.Toast;

import java.util.HashMap;

import static android.content.Context.AUDIO_SERVICE;

public class ToolsHelper {

    static HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private static SoundPool soundPool;
    private static float volumnRatio;
    private static AudioManager am;
    Context mContext;
    static Toast toast = null;

    public ToolsHelper(Context context){

        mContext = context;
    }

    public void initSound(){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(mContext, R.raw.barcodebeep, 1));
        am = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
        Reader.rrlib.SetSoundID(soundMap.get(1),soundPool);
    }

    public void ErrorSoundPool(int a){

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;

        SoundPool.Builder builder = new SoundPool.Builder();
        //传入音频数量
        builder.setMaxStreams(5);
        //AudioAttributes是一个封装音频各种属性的方法
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        //设置音频流的合适的属性
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//STREAM_MUSIC
        //加载一个AudioAttributes
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
        soundMap.put(1 , soundPool.load(mContext, R.raw.serror,1)) ; // scan_new
        am = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象

        soundPool.play(1, volumnRatio, volumnRatio, 1, 0, 1);
    }

    public static void show(Context context, String text) {
        try {
            if (toast != null) {
                toast.setText(text);
            } else {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

}
