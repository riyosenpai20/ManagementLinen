package com.example.management_linen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.management_linen.helpers.ApiHelper;
import com.example.management_linen.models.*;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScanModeFragment extends Fragment implements OnClickListener, OnItemClickListener, OnItemSelectedListener {

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle the case when nothing is selected
    }

    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    String token;
    private int inventoryFlag = 1;
    Handler handler;
    private ArrayList<HashMap<String, String>> tagList;
    SimpleAdapter adapter;
    Button BtClear;
    TextView tv_count;
    TextView tv_time;
    TextView tv_alltag;
    RadioGroup RgInventory;
    RadioButton RbInventorySingle;
    RadioButton RbInventoryLoop;
    Button Btimport;
    Button BtInventory, BtSave, btnTest;
    ListView LvTags;
    CheckBox chkstoptime;
    EditText stoptime;
    private LinearLayout llContinuous;
    private HashMap<String, String> map;
    public boolean isStopThread = false;
    MsgCallback callback = new MsgCallback();
    private static final int MSG_UPDATE_LISTVIEW = 0;
    private static final int MSG_UPDATE_TIME = 1;
    private static final int MSG_UPDATE_ERROR = 2;
    private static final int MSG_UPDATE_STOP = 3;
    private Timer timer;
    public long beginTime;
    public long CardNumber;
    public static List<String> mlist = new ArrayList<String>();

    private KeyBroadReceiver receiver = new KeyBroadReceiver();
    private final String ON_TRIGGER_KEYUP = "com.android.action.KEYCODE_TRIGGER_KEYUP_UHF";
    private final String ON_TRIGGER_KEYDOWN = "com.android.action.KEYCODE_TRIGGER_KEYDOWN_UHF";

    private int intLayout = 1;

    private Integer idRuang;
    public String baseUrl, namePIC, namaPerusahaan, namaRuangReport;
    private int idPerusahaan, roleUser, type_rs;
    private ApiHelper apiService;

    private Boolean fromSTORuangan = false;
    private Boolean fromSearchCard = false;
    private Boolean fromSearchCardInfo = false;
    private String jenisSTO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            sharedpreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            token = sharedpreferences.getString("token", null);
            idRuang = sharedpreferences.getInt("idRuang", 0);
            namePIC = sharedpreferences.getString("namePIC", null);
            namaPerusahaan = sharedpreferences.getString("namaPerusahaan", null);
            namaRuangReport = sharedpreferences.getString("namaRuangReport", null);
            idPerusahaan = sharedpreferences.getInt("idPerusahaan", 0);
            roleUser = sharedpreferences.getInt("roleUser", 0);
            type_rs = sharedpreferences.getInt("type_rs", 0);
            fromSTORuangan = sharedpreferences.getBoolean("fromSTORuangan", false);
            fromSearchCard = sharedpreferences.getBoolean("fromSearchCard", false);
            jenisSTO = sharedpreferences.getString("jenisSTO", null);
            fromSearchCardInfo = sharedpreferences.getBoolean("fromSearchCardInfo", false);
            System.out.println("jenisSTO: " + jenisSTO);

            // Initialize apiService
            baseUrl = getResources().getString(R.string.BASE_URL);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(ApiHelper.class);

            System.out.println("idRuang ScanMode: " + idRuang);
            if (token == null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
                return;
            }

            tagList = TagListHolder.getInstance().getTagList();
            if (tagList == null) {
                tagList = new ArrayList<>();
                TagListHolder.getInstance().setTagList(tagList);
            }

//            for (String tag : hardcodedData) {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("tagUii", tag);
//                map.put("tagLen", "24"); // Example value for tagLen
//                map.put("tagCount", "1"); // Example value for tagCount
//                map.put("tagRssi", "-55"); // Example value for tagRssi
//                tagList.add(map);
//            }

            BtClear = view.findViewById(R.id.BtClear);
            Btimport = view.findViewById(R.id.BtImport);
            tv_count = view.findViewById(R.id.tv_count);
            tv_time = view.findViewById(R.id.tv_times);
            chkstoptime = view.findViewById(R.id.checkBox_chkstoptime);
            stoptime = view.findViewById(R.id.editText_stoptime);
            tv_alltag = view.findViewById(R.id.tv_alltag);
            RgInventory = view.findViewById(R.id.RgInventory);
            RbInventorySingle = view.findViewById(R.id.RbInventorySingle);
            RbInventoryLoop = view.findViewById(R.id.RbInventoryLoop);

            BtInventory = view.findViewById(R.id.BtInventory);
            LvTags = view.findViewById(R.id.LvTags);

            llContinuous = view.findViewById(R.id.llContinuous);
            BtSave = view.findViewById(R.id.BtSend);

            adapter = new SimpleAdapter(requireContext(), tagList, R.layout.listtag_items,
                    new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
                    new int[]{R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
                            R.id.TvTagRssi});

            System.out.println(tagList);

            BtClear.setOnClickListener(this);
            Btimport.setOnClickListener(new AddSample());
            RgInventory.setOnCheckedChangeListener(new RgInventoryCheckedListener());
            BtInventory.setOnClickListener(this);
//            BtSave.setOnClickListener(new SaveData());

            Reader.rrlib.SetCallBack(callback);
            LvTags.setAdapter(adapter);

//            Button btnNext = view.findViewById(R.id.BtNext);
//            btnNext.setOnClickListener(v -> {
//                intLayout = 2;
//                Intent intent = new Intent(getActivity(), TestPage.class);
//                intent.putExtra("tagList", (ArrayList<HashMap<String, String>>) tagList);
//                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                startActivity(intent);
//                requireActivity().finish();
//            });

            if (fromSTORuangan) {
                BtSave.setOnClickListener(v -> {
                    sendTagsToSTO();
                });
            } else if (fromSearchCardInfo) {
                BtSave.setText("Lanjut");
                BtSave.setOnClickListener(v -> {
                    List<String> listTags = new ArrayList<>();
                    for (HashMap<String, String> map : tagList) {
                        String tagUii = map.get("tagUii");
                        if (tagUii != null) {
                            listTags.add(tagUii);
                        }
                    }

                    System.out.println("token: " + token);

                    // Open SearchInfoActivity instead of PreviewSearchActivity
                    Intent intent = new Intent(requireContext(), SearchInfoActivity.class);
                    intent.putExtra("tagList", (java.io.Serializable) listTags);
                    intent.putExtra("namaRuang", namaRuangReport);
                    startActivity(intent);
                });
            } else {
                BtSave.setText("Lanjut");
                BtSave.setOnClickListener(v -> {
                    List<String> listTags = new ArrayList<>();
                    for (HashMap<String, String> map : tagList) {
                        String tagUii = map.get("tagUii");
                        if (tagUii != null) {
                            listTags.add(tagUii);
                        }
                    }

                    // Open PreviewSearchActivity
                    Intent intent = new Intent(requireContext(), PreviewSearchActivity.class);
                    intent.putExtra("tagList", (java.io.Serializable) listTags);
                    intent.putExtra("namaRuang", namaRuangReport);
                    startActivity(intent);
                });
            }

            // Setup handler for UI updates
            Log.i("MY", "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try{
                        switch (msg.what) {
                            case MSG_UPDATE_LISTVIEW:
                                String result = msg.obj+"";
                                String[] strs = result.split(",");
                                if(strs.length==2)
                                {
                                    addEPCToList(strs[0], strs[1]);
                                }
                                else
                                {
                                    addEPCToList(strs[0]+","+strs[1], strs[2]);
                                }

                                break;
                            case MSG_UPDATE_TIME:
                                String ReadTime = msg.obj+"";
                                tv_time.setText(ReadTime);
                                if(chkstoptime.isChecked() && Long.parseLong(ReadTime) >= Long.parseLong(stoptime.getText().toString()))
                                    stopInventory();
                                break;
                            case MSG_UPDATE_ERROR:

                                break;
                            case MSG_UPDATE_STOP:
                                setViewEnabled(true);
                                BtInventory.setText(getString(R.string.btInventory));
                                break;
                            default:
                                break;
                        }
                    }catch(Exception ex)
                    {ex.toString();}
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Added methods from ScanMode.java
    private class RgInventoryCheckedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == RbInventorySingle.getId()) {
                inventoryFlag = 0;
            } else if (checkedId == RbInventoryLoop.getId()) {
                inventoryFlag = 1;
            }
        }
    }

    public class AddSample implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                // For sample testing
                String[] hardcodedData = {
//                        Ruang
//                        "NA6QPoWNq02TyIDVTC6QqraI75qU6AFeeA7yRq4NquYLoYAIGK",
//                        "P9vfdzuF8cGfpm0fiRuBQzEZa8VKZhIsmCi5TyrkT86ClvNqow",
//                        "GtCgcGA9irm7ZN7x7gFHWv7vcn2a1GxcRSKyxcTE428tBjaH5i",
//                        "thKhbZWIAw9b0NJqfaxVZUKTrbcgykOpw82Z2atUJMAJfxgaiT",
//                        "3BGP5iaevnjThjBAPsSZLFLBCDNWgzHuPWybM2Xkdk2b74U4JO",
//                        "OOMSC3ae6wJhevXOqGd9Lp91ZJ3tXOaAUzSx512RyFFSmiX0z5",
//                        "votxwJGriy81BzlnRcKHTlHT6KYIRLyG2Hr2W7VN7Ga1KSMi43",
//                        "LXnrQkQEcCIYGsxS2wAW0Igx6VAeURanYtPzmd2ciXrFvrVhjn",
//                        "QYvGXp7ZTtVkRF9PJXVieqSqdlfAZ9tyHKNAXruiQxDmEB0WDY",
//                        "pVPiqSM2nqeQMUgMJIwpIXDwjpoCxLtbhJQhM0e9YmczghZd3D",
//                        "E280689400004021FF44A60D",
//                        "E28069950000400AAF21CE74"

//                      Inventory
//                        "faEMC2VxutiI24UkEqsBf7Qw0QxzlK2L745z7h59GI3UstvBT2",
//                        "55wiDnMx6WCXTtnzaKIbVqmFE9jh7KT3ns38aQWM4rXYyw5olu",
//                        "tpmPwE6swABmsiPYBsQ18rXwRrXUmNkF6ko3reSKx0x2CjLbxg",
//                        "rxcj8O1cvl4no97pZe9Qd5y0n9UlEPEyPfDgXkNis103s2DRvF",
//                        "U8KEVtJ2cIlglP9orSPjzPZADwyJ4bnMqAMLLfhHeYueS3tvwP",
//                        "85LFJHhdC3u8YVv2t8KT2pVXXssdwT1hI6hDwqf2LNsM7wp9jD",
//                        "f1D5ghUU1ud3QrQ9v2b9A1DtfJFrk9uF1rgIh44DUBvMv715lj",
//                        "GcY1ZRN4tPn3o94gt4O1MSA1GcIptcPNi96YG2rvLF7rsQozoX",
//                        "mTkH02vCxrrkLczE0iPsZ7TKUOE4owkQiGOW8wgpIib9GYesDl",
//                        "7c25aT2LX1Ah14oSpcotyoGKl9WQDYSskagMBmRqFqKSPsRDfP",
//                        "0BPE86NhJJtuhbCz2LV64In1jj1vRVhPQjJiYsvD2mkxDahfAN",
//                        "bSF4xhEaUewmAA5FO1FOv5ATRJpmp6ydvUuqzyHRKD01HBlSoF",
//                        "k26bWs4xFTofs0e9iiKUn0rG6tki4Rrglti0xWV3GXxclTojo4",
//                        "ewSL2yAt1NzE3TQyR81f0AeuIntAYYGWZ0z35b70Fofv1rocMb",
//                        "GjPoUiH2U25Jf0lBI2lqFqi0OAs7ahExUGjGGV2uO8UvmGQFX6",
//                        "UAJvF9mcI837bYn8dQDgO9FbyHzmREoys3ZVlFGH2CtFPzoro7",
//                        "ahjjtoY16q8n8KrDnH012zkC9oP7Xa3AT5JEGB5rSDkQW2DxDl",
//                        "abK08jDN76DFvEcx0ZmpvD5FyjRrsB05mjhV8MGkwnVEJnHpho",
//                        "3TXz75ppL9ZVR4GetOVxRHxsFJmCtbwrVP4cjq0PlFiA9QV9el",
//                        "iHbAbIJ2HsYWAx61DXHRVZ4qqchIn0kLSrE1cyxtYNeIXBsZzX",
//                        "TAGSAMPLESENTRA10003",
//                        "TAGSAMPLESENTRA10004",
//                        "TAGSAMPLESENTRA10001",
//                        "TAGSAMPLESENTRA10002",

                        "UAJvF9mcI837bYn8dQDgO9FbyHzmREoys3ZVlFGH2CtFPzoro7",
                        "ahjjtoY16q8n8KrDnH012zkC9oP7Xa3AT5JEGB5rSDkQW2DxDl",
                        "abK08jDN76DFvEcx0ZmpvD5FyjRrsB05mjhV8MGkwnVEJnHpho",
                        "3TXz75ppL9ZVR4GetOVxRHxsFJmCtbwrVP4cjq0PlFiA9QV9el",
                        "iHbAbIJ2HsYWAx61DXHRVZ4qqchIn0kLSrE1cyxtYNeIXBsZzX",

                        // Add more strings as needed
                };

                ArrayList<HashMap<String, String>> tagHolder = TagListHolder.getInstance().getTagList();

                // Kalau kosong, copy referensinya sekali saja
                if (tagList != tagHolder) {
                    tagList.clear();
                    tagList.addAll(tagHolder);
                }

                // Tambahkan data ke tagList (bukan ganti list!)
                for (String data : hardcodedData) {
                    boolean exists = false;
                    for (HashMap<String, String> map : tagList) {
                        if (map.get("tagUii").equals(data)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("tagUii", data);
                        map.put("tagLen", String.valueOf(data.length()));
                        map.put("tagCount", "1");
                        map.put("tagRssi", "0");
                        tagList.add(map);
                    }
                }

                // Sekarang tagList & TagListHolder tetap sinkron
                adapter.notifyDataSetChanged();
                
                // Update tv_count with length of hardcodedData
                tv_count.setText(String.valueOf(hardcodedData.length));

            }
            catch(Exception e)
            {

            }
        }
    }
    
    private class SaveData implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (tagList.size() == 0) {
                Toast.makeText(getActivity(), "No tags to save", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Example API call to save data
            saveTagsToServer();
        }
    }
    
    private void saveTagsToServer() {
        if (token == null) {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return;
        }

        // Use the class member apiService that was initialized in onViewCreated
        List<String> data = new ArrayList<>();
        System.out.println(tagList);
        for (int i = 0; i < tagList.size(); i++) {
            data.add(tagList.get(i).get("tagUii"));
        }


        try {
            Call<Void> call = apiService.inventory(data, token, "application/json", "application/json");
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if(response.code() == 200) {
							Toast.makeText(requireActivity(), "Data saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.code() == 401) {
                            sharedpreferences.edit().putString("token", null).apply();
                            Intent intent = new Intent(requireActivity(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(requireActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(requireActivity(), data.toString(), Toast.LENGTH_SHORT).show();
    }

    private void sendTagsToSTO() {
        List<String> listTags = new ArrayList<>();
        for (HashMap<String, String> map : tagList) {
            String tagUii = map.get("tagUii");
            if (tagUii != null) {
                listTags.add(tagUii);
            }
        }
        System.out.println(listTags);
        ScanSTOResponse scanSTOResponse = new ScanSTOResponse(listTags, idRuang, jenisSTO);

        Call<ResponseScanSTO> call = apiService.scan_sto(scanSTOResponse, "Token " + token, "application/json", "application/json");
        call.enqueue(new Callback<ResponseScanSTO>() {
            @Override
            public void onResponse(Call<ResponseScanSTO> call, Response<ResponseScanSTO> response) {
                if (response.isSuccessful()) {
                    // Get the data from response
                    List<DetailScanSto> stoData = response.body().getData();
                    List<DetailScanSto.UnmatchedGroup> unmatchedGroups = response.body().getUnmatchedGroups();
                    List<DetailScanSto> notRegistered = response.body().getNotRegistered();
                    System.out.println("scanmode - stoData: " + stoData);
                    System.out.println("scanmode - Unmatched groups: " + unmatchedGroups);
                    System.out.println("scanmode - Not registered: " + notRegistered);

                    // Create intent to start PreviewSTO activity
                    Intent intent = new Intent(requireContext(), PreviewSTO.class);

                    // Put the data as serializable extra
                    intent.putExtra("sto_data", (java.io.Serializable) stoData);
                    intent.putExtra("unmatched_groups", (java.io.Serializable) unmatchedGroups);
                    intent.putExtra("not_registered", (java.io.Serializable) notRegistered);
                    intent.putExtra("hospital_name", namaPerusahaan);
                    intent.putExtra("room_name", namaRuangReport);
                    // Pass the flag to indicate source
                    intent.putExtra("fromSTORuangan", fromSTORuangan);

                    // Start the activity
                    startActivity(intent);
                } else {
                    showAlertDialog("Perhatian!", "Response Failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseScanSTO> call, Throwable t) {
                Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void clearData() {
         tv_count.setText("0");
         tv_time.setText("0");
         tv_alltag.setText("0");
         tagList.clear();
         mlist.clear();
         CardNumber = 0;
         Log.i("MY", "tagList.size " + tagList.size());
         adapter.notifyDataSetChanged();
     }
     
     private void readTag() {
         if (BtInventory.getText().equals(getString(R.string.btInventory)))// 识别标签
         {
             switch (inventoryFlag) {
                 case 0:// 单步
                 {
                     List<ReadTag> newlist = new ArrayList<ReadTag>();
                     //int result =Reader.rrlib.InventoryOnce((byte)0,(byte)4,(byte)0,(byte)0,(byte)0x80,(byte)0,(byte)10,newlist);
                     Reader.rrlib.ScanRfid();
                 }
                 break;
                 case 1:
                 {
                     int result = Reader.rrlib.StartRead();
                     if(result==0)
                     {
                         BtInventory.setText(getString(R.string.title_stop_Inventory));
                         setViewEnabled(false);
                         if(timer == null) {
                             beginTime = System.currentTimeMillis();
                             timer = new Timer();
                             timer.schedule(new TimerTask() {
                                 @Override
                                 public void run() {
                                     long ReadTime = System.currentTimeMillis() - beginTime;
                                     Message msg = handler.obtainMessage();
                                     msg.what = MSG_UPDATE_TIME;
                                     msg.obj = String.valueOf(ReadTime) ;
                                     handler.sendMessage(msg);
                                 }
                             }, 0, 20);
                         }

                     }
                 }
                 break;
                 default:
                     break;
             }
         } else {// 停止识别

             stopInventory();
         }
     }
     
     private void addEPCToList(String rfid, String rssi) {
         if (!TextUtils.isEmpty(rfid)) {
             String epc="";
             String[] data = rfid.split(",");
             if(data.length==1)
             {
                 epc = data[0];
             }
             else
             {
                 epc = "EPC:"+data[0]+"\r\nMem:"+data[1];
             }

             int index = checkIsExist(epc);
             map = new HashMap<String, String>();

             map.put("tagUii", epc);
             map.put("tagCount", String.valueOf(1));
             map.put("tagRssi", rssi);
             CardNumber++;
             if (index == -1) {
                 tagList.add(map);
                 LvTags.setAdapter(adapter);
                 tv_count.setText("" + adapter.getCount());
                 mlist.add(data[0]);
             } else {
                 int tagcount = Integer.parseInt(
                         tagList.get(index).get("tagCount"), 10) + 1;

                 map.put("tagCount", String.valueOf(tagcount));

                 tagList.set(index, map);

             }
             tv_alltag.setText(String.valueOf(CardNumber));
             adapter.notifyDataSetChanged();

         }
     }
     
     public int checkIsExist(String strEPC) {
         int existFlag = -1;
         if (strEPC == null || strEPC.length() == 0) {
             return existFlag;
         }
         String tempStr = "";
         for (int i = 0; i < tagList.size(); i++) {
             HashMap<String, String> temp = new HashMap<String, String>();
             temp = tagList.get(i);
             tempStr = temp.get("tagUii");
             if (strEPC.equals(tempStr)) {
                 existFlag = i;
                 break;
             }
         }
         return existFlag;
     }
    
    private void setViewEnabled(boolean enabled) {
        RbInventorySingle.setEnabled(enabled);
        RbInventoryLoop.setEnabled(enabled);
        BtClear.setEnabled(enabled);
    }
    
    private void stopInventory() {
        try {
            // Pastikan Reader.rrlib tidak null sebelum memanggil StopRead
            if (Reader.rrlib != null) {
                Reader.rrlib.StopRead();
            }

            if(timer != null){
                timer.cancel();
                timer = null;
                BtInventory.setText(getString(R.string.btStoping));
            }
        } catch (Exception e) {
            Log.e("ScanModeFragment", "Error stopping inventory: " + e.getMessage());
            // Pastikan UI tetap diperbarui meskipun terjadi error
            if (BtInventory != null) {
                BtInventory.setText(getString(R.string.btInventory));
            }
            setViewEnabled(true);
        }
    }
    
    @Override
    public void onClick(View v) {
        try
        {
            if(v == BtInventory)
            {
                if(chkstoptime.isChecked())
                    clearData();

                readTag();
            }
            else if(v == BtClear)
            {
                clearData();
            }

        }
        catch(Exception e)
        {
            stopInventory();
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Handle item click
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle item selection
    }

    public class MsgCallback implements TagCallback {

        @Override
        public void tagCallback(ReadTag arg0) {

            // TODO Auto-generated method stub
            String epc="";
            String mem="";
            if(arg0.epcId!=null)
                epc = arg0.epcId.toUpperCase();
            if(arg0.memId!=null)
                mem = arg0.memId.toUpperCase();
            String rssi = String.valueOf(arg0.rssi);
            Message msg = handler.obtainMessage();
            msg.what = MSG_UPDATE_LISTVIEW;
            if(mem.length()==0)
                msg.obj =epc+","+rssi ;
            else
                msg.obj =epc+","+mem+","+rssi ;
            handler.sendMessage(msg);
        }

        @Override
        public void StopReadCallBack() {
            // TODO Auto-generated method stub

            Message msg = handler.obtainMessage();
            msg.what = MSG_UPDATE_STOP;
            msg.obj ="" ;
            handler.sendMessage(msg);
        }
    };

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        isStopThread = false;
        // Pastikan callback diatur ulang saat fragment dilanjutkan
        Reader.rrlib.SetCallBack(callback);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ON_TRIGGER_KEYDOWN);
        filter.addAction(ON_TRIGGER_KEYUP);
        requireActivity().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopInventory();
        // Hapus callback saat fragment tidak aktif untuk mencegah NullPointerException
        Reader.rrlib.SetCallBack(null);
        requireActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        // Pastikan callback dihapus saat fragment dihancurkan
        if (Reader.rrlib != null) {
            Reader.rrlib.SetCallBack(null);
        }
        super.onDestroy();
    }

    private class KeyBroadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ON_TRIGGER_KEYDOWN)){

                if (BtInventory.isEnabled()) {
                    BtInventory.performClick();
                } else {
                    BtInventory.performClick();
                }
            }
        }
    }
}