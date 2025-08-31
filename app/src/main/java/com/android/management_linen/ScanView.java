package com.android.management_linen;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rfid.trans.ReaderParameter;

public class ScanView extends AppCompatActivity implements OnClickListener {
	
	private TextView tvVersion;
	private TextView tvResult;
	private Spinner tvpowerdBm;
	private Spinner spType;
	private Spinner spMem;

	private Button bSetting;
	private Button bRead;
	
	private Button paramRead;
	private Button paramSet;

	private int soundid;
	private int tty_speed = 57600;
	private byte addr = (byte) 0xff; 
	private String[] strBand =new String[5];
    private String[] strmaxFrm =null;
    private String[] strminFrm =null;
	private String[] strtime =new String[256];

	private String[] strjtTime =new String[7];
	private String[] strBaudRate =new String[5];

	private String[] dwelltime =new String[254];

    private String[] strProfile =new String[8];
	Spinner jgTime;
	private ArrayAdapter<String> spada_jgTime;

	Spinner spBand;
    Spinner spmaxFrm;
	Spinner spminFrm;
	Spinner sptime;
	Spinner spqvalue;
	Spinner spsession;
	Spinner sptidaddr;
	Spinner sptidlen;
	Spinner spbaudRate;
	Spinner spDwell;
	Spinner spTagfocus;
    Spinner spProfilr;
	Button Setparam;
	Button Getparam;
	Button btOpenrf;
	Button btCloserf;
	Button btAnswer;
	Button btActive;
	Button btSetFocus;
	Button btGetFocus;
    Button btSetPro;

    private TextView tvTemp;
    private TextView tvLoss;
    Button btReadTemp;
    Button btReadLoss;
    private ArrayAdapter<String> spada_Band;
    private ArrayAdapter<String> spada_maxFrm;
    private ArrayAdapter<String> spada_minFrm;
	private ArrayAdapter<String> spada_time;
	private ArrayAdapter<String> spada_lowPwr;
	private ArrayAdapter<String> spada_qvalue;
    private ArrayAdapter<String> spada_session;
    private ArrayAdapter<String> spada_tidaddr;
    private ArrayAdapter<String> spada_tidlen;
	private ArrayAdapter<String> spada_baudrate;

	private ArrayAdapter<String> spada_dwell;
	private ArrayAdapter<String> spada_tagfocus;
    private ArrayAdapter<String> spada_profile;
	private static final String TAG = "SacnView";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub  properties
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.scan_view);
		initView();
	}
	
	private void initView(){

        tvTemp = (TextView)findViewById(R.id.txt_tempe);
        tvLoss= (TextView)findViewById(R.id.txt_loss);

        btReadTemp = (Button)findViewById(R.id.bt_Readtemp);
        btReadLoss = (Button)findViewById(R.id.bt_Readloss);


        tvVersion = (TextView)findViewById(R.id.version);
		tvResult = (TextView)findViewById(R.id.param_result);

		tvpowerdBm = (Spinner)findViewById(R.id.power_spinner);
		ArrayAdapter<CharSequence> adapter3 =  ArrayAdapter.createFromResource(this, R.array.Power_select, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tvpowerdBm.setAdapter(adapter3);

		tvpowerdBm.setSelection(33, true);

		bSetting = (Button)findViewById(R.id.pro_setting);
		bRead = (Button)findViewById(R.id.pro_read);
		paramRead = (Button)findViewById(R.id.ivt_read);
		paramSet = (Button)findViewById(R.id.ivt_setting);
		btOpenrf = (Button)findViewById(R.id.ivt_open);
		btCloserf = (Button)findViewById(R.id.ivt_close);
		btAnswer = (Button)findViewById(R.id.bt_answer);
		btActive = (Button)findViewById(R.id.bt_active);

		btSetFocus = (Button)findViewById(R.id.bt_SetFocus);
		btGetFocus = (Button)findViewById(R.id.bt_GetFocus);
        btSetPro = (Button)findViewById(R.id.bt_SetProfile);

		bSetting.setOnClickListener(this);
		bRead.setOnClickListener(this);
		paramRead.setOnClickListener(this);
		paramSet.setOnClickListener(this);
		btOpenrf.setOnClickListener(this);
		btCloserf.setOnClickListener(this);
		btAnswer.setOnClickListener(this);
		btActive.setOnClickListener(this);
        btReadLoss.setOnClickListener(this);
        btReadTemp.setOnClickListener(this);

		btSetFocus.setOnClickListener(this);
		btGetFocus.setOnClickListener(this);
        btSetPro.setOnClickListener(this);
		//最大询查时间
		for(int index=0;index<256;index++)
		{
			strtime[index] = String.valueOf(index)+"*100ms";
		}
		sptime = (Spinner)findViewById(R.id.time_spinner);
		spada_time = new ArrayAdapter<String>(ScanView.this,
				android.R.layout.simple_spinner_item, strtime);
		spada_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sptime.setAdapter(spada_time);
		sptime.setSelection(50,false);

		////////////Ƶ��ѡ��
		strBand[0]="Chinese band2";
		strBand[1]="US band";
		strBand[2]="Korean band";
		strBand[3]="EU band";
		strBand[4]="Chinese band1";

		strBaudRate[0] = "9600bps";
		strBaudRate[1] = "19200bps";
		strBaudRate[2] = "38400bps";
		strBaudRate[3] = "57600bps";
		strBaudRate[4] = "115200bps";


		spBand=(Spinner)findViewById(R.id.band_spinner);
		spada_Band = new ArrayAdapter<String>(ScanView.this,
	             android.R.layout.simple_spinner_item, strBand);  
		spada_Band.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		spBand.setAdapter(spada_Band);  
		spBand.setSelection(1,false); 
		SetFre(2);////��ʼ��Ƶ��
		 // ���Spinner�¼�����  
		spBand.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int arg2, long arg3) {
            // TODO Auto-generated method stub  
            // ������ʾ��ǰѡ�����  
            arg0.setVisibility(View.VISIBLE);
            if(arg2==0)SetFre(1);
            if(arg2==1)SetFre(2);
            if(arg2==2)SetFre(3);
            if(arg2==3)SetFre(4);
            if(arg2==4)SetFre(8);
            //ѡ��Ĭ��ֵ����ִ��  
        }  
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub  
        	}  
		});


		//strjtTime[0]="无间隔";
		for(int index=0;index<7;index++)
		{
			strjtTime[index] = String.valueOf(index*10)+"ms";
		}
		jgTime=(Spinner)findViewById(R.id.jgTime_spinner);
		spada_jgTime = new ArrayAdapter<String>(ScanView.this,
				android.R.layout.simple_spinner_item, strjtTime);
		spada_jgTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		jgTime.setAdapter(spada_jgTime);
		jgTime.setSelection(0,false);

		spqvalue=(Spinner)findViewById(R.id.qvalue_spinner);
		ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(this, R.array.men_q, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spqvalue.setAdapter(adapter); 
		spqvalue.setSelection(6, true);
		
		
		spsession=(Spinner)findViewById(R.id.session_spinner);
		ArrayAdapter<CharSequence> adapter1 =  ArrayAdapter.createFromResource(this, R.array.men_s, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spsession.setAdapter(adapter1); 
		spsession.setSelection(1, true);
		
		sptidaddr=(Spinner)findViewById(R.id.tidptr_spinner);
		sptidlen=(Spinner)findViewById(R.id.tidlen_spinner);
		ArrayAdapter<CharSequence> adapter2 =  ArrayAdapter.createFromResource(this, R.array.men_tid, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sptidaddr.setAdapter(adapter2); 
		sptidaddr.setSelection(0, true);
		sptidlen.setAdapter(adapter2); 
		sptidlen.setSelection(6, true);

		spada_baudrate= new ArrayAdapter<String>(ScanView.this,
				android.R.layout.simple_spinner_item, strBaudRate);
		spada_baudrate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


		////////////查询类型
		spType=(Spinner)findViewById(R.id.IvtType_spinner);
		ArrayAdapter<CharSequence> spada_Type = ArrayAdapter.createFromResource(this, R.array.IvtType_select, android.R.layout.simple_spinner_item);
		spada_Type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spType.setAdapter(spada_Type);
		spType.setSelection(0,false);


		////////////查询区域
		spMem=(Spinner)findViewById(R.id.mixmem_spinner);
		ArrayAdapter<CharSequence> spada_Mem = ArrayAdapter.createFromResource(this, R.array.readmen_select, android.R.layout.simple_spinner_item);
		spada_Mem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMem.setAdapter(spada_Mem);
		spMem.setSelection(1,false);




		for(int index=2;index<256;index++)
		{
			dwelltime[index-2] = String.valueOf(index*100)+"ms";
		}
		spDwell=(Spinner)findViewById(R.id.dwell_spinner);
		spada_dwell = new ArrayAdapter<String>(ScanView.this,
				android.R.layout.simple_spinner_item, dwelltime);
		spada_dwell.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDwell.setAdapter(spada_dwell);
		spDwell.setSelection(0,false);

		spTagfocus=(Spinner)findViewById(R.id.focus_spinner);
		ArrayAdapter<CharSequence> spada_focus = ArrayAdapter.createFromResource(this, R.array.en_select, android.R.layout.simple_spinner_item);
		spada_focus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTagfocus.setAdapter(spada_focus);
		spTagfocus.setSelection(1,false);

        strProfile[0]="11:640K,FM0,7.5us";
        strProfile[1]=" 1:640K, M2,7.5us";
        strProfile[2]="15:640K, M4,7.5us";
        strProfile[3]="12:320K, M2, 15us";
        strProfile[4]=" 3:320K, M2, 20us";
        strProfile[5]=" 5:320K, M4, 20us";
        strProfile[6]=" 7:250K, M4, 20us";
        strProfile[7]="13:160K, M8, 20us";

        spProfilr=(Spinner)findViewById(R.id.prof_spinner);
        spada_profile = new ArrayAdapter<String>(ScanView.this,
                android.R.layout.simple_spinner_item, strProfile);
        spada_profile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProfilr.setAdapter(spada_profile);
        spProfilr.setSelection(5,false);

	}

	@Override
	public void onClick(View view) {
		try{
			if(view == paramRead)
			{
				ReaderParameter param = Reader.rrlib.GetInventoryPatameter();
				sptidlen.setSelection(param.Length, true);
				sptidaddr.setSelection(param.WordPtr, true);
				spqvalue.setSelection(param.QValue,true);
				sptime.setSelection(param.ScanTime,true);
				spType.setSelection(param.IvtType,true);
				spMem.setSelection(param.Memory-1,true);

				int sessionindex = param.Session;
				if(sessionindex==255) sessionindex=4;
				spsession.setSelection(sessionindex,true);
				byte[]data = new byte[30];
				int[]len = new int[1];
				int fCmdRet = Reader.rrlib.GetCfgParameter((byte)7,data,len);
				if(fCmdRet==0 && len[0]==3)
				{
					jgTime.setSelection( data[0],true);
					spDwell.setSelection(data[1]-2,true);
				}
				//jgTime.setSelection( param.Interval /10,true);
				Reader.writelog(getString(R.string.get_success),tvResult);
			}
			else if(view == paramSet)
			{
				ReaderParameter param = Reader.rrlib.GetInventoryPatameter();
				param.Length = sptidlen.getSelectedItemPosition();
				param.WordPtr = sptidaddr.getSelectedItemPosition();
				param.QValue = spqvalue.getSelectedItemPosition();
				param.ScanTime = sptime.getSelectedItemPosition();
				param.IvtType = spType.getSelectedItemPosition();
				param.Memory = spMem.getSelectedItemPosition()+1;
				int Session = spsession.getSelectedItemPosition();
				if(Session==4)Session=255;
				param.Session = Session;

				int jgTimes = jgTime.getSelectedItemPosition();
				int dwell = spDwell.getSelectedItemPosition();
				param.Interval = 0;
				Reader.rrlib.SetInventoryPatameter(param);
				byte[]data = new byte[3];
				data[0] = (byte)jgTimes;
				data[1] = (byte)(dwell+2);
				data[2] = 4;
				int len = 3;
				int fCmdRet = Reader.rrlib.SetCfgParameter((byte)1,(byte)7,data,len);
				Reader.writelog(getString(R.string.set_success),tvResult);
			}
			else if (view == bSetting){
				
				int MaxFre=0;
				int MinFre=0;
				int Power= tvpowerdBm.getSelectedItemPosition();
				int fband = spBand.getSelectedItemPosition();
				int band=0;
				if(fband==0)band=1;
				if(fband==1)band=2;
				if(fband==2)band=3;
				if(fband==3)band=4;
				if(fband==4)band=8;
				int Frequent= spminFrm.getSelectedItemPosition();
				MinFre = Frequent;
				Frequent= spmaxFrm.getSelectedItemPosition();
				MaxFre = Frequent;
				int Antenna=0;

				String temp="";
				int result = Reader.rrlib.SetRfPower((byte)Power);
				if(result!=0)
				{
					temp=getString(R.string.power_error);
				}


				result = Reader.rrlib.SetRegion((byte)band,(byte)MaxFre,(byte)MinFre);
				if(result!=0)
				{
					if(temp=="")
					temp=getString(R.string.frequent_error);
					else
						temp+=(",\r\n"+getString(R.string.frequent_error));
				}
				if(temp!="")
				{
					Reader.writelog(temp,tvResult);
				}
				else
				{
					Reader.writelog(getString(R.string.set_success),tvResult);
				}
			}else if (view == bRead){
				byte[]Version=new byte[2];
				byte[]Power=new byte[1];
				byte[]band=new byte[1];
				byte[]MaxFre=new byte[1];
				byte[]MinFre=new byte[1];
				int result = Reader.rrlib.GetReaderInformation(Version, Power, band, MaxFre, MinFre);
				if(result==0)
				{
					String hvn = String.valueOf(Version[0]);
					if(hvn.length()==1)hvn="0"+hvn;
					String lvn = String.valueOf(Version[1]);
					if(lvn.length()==1)lvn="0"+lvn;
					tvVersion.setText(hvn+"."+lvn);
					tvpowerdBm.setSelection(Power[0],true);
					SetFre(band[0]);
					int bandindex = band[0];
					if(bandindex ==8)
					{
						bandindex=bandindex-4;
					}
					else
					{
						bandindex=bandindex-1;
					}
					spBand.setSelection(bandindex,true);
					spminFrm.setSelection(MinFre[0],true);
					spmaxFrm.setSelection(MaxFre[0],true);
					//sptime.setSelection(ScanTime[0]&255,true);
					Reader.writelog(getString(R.string.get_success),tvResult);
				}
				else
				{
					Reader.writelog(getString(R.string.get_failed),tvResult);
				}
			}
			else if(view == btSetFocus)
			{
				int index = spTagfocus.getSelectedItemPosition();
				byte[]data = new byte[1];
				data[0]=(byte)index;
				int len = 1;
				int fCmdRet = Reader.rrlib.SetCfgParameter((byte)1,(byte)8,data,len);
				if(fCmdRet==0)
				{
					Reader.writelog(getString(R.string.set_success),tvResult);
				}
				else
				{
					Reader.writelog(getString(R.string.set_failed),tvResult);
				}
			}
			else if(view == btGetFocus)
			{
				byte[]data = new byte[250];
				int[] len = new int[1];
				int fCmdRet = Reader.rrlib.GetCfgParameter((byte)8,data,len);
				if(fCmdRet==0 && len[0]==1)
				{
					spTagfocus.setSelection(data[0],true);
					Reader.writelog(getString(R.string.get_success),tvResult);
				}
				else
				{
					Reader.writelog(getString(R.string.get_failed),tvResult);
				}
			}
            else if(view == btSetPro)
            {
                int index = spProfilr.getSelectedItemPosition();
                int Profile=5;
                switch(index)
                {
                    case 0:
                        Profile = 11;
                        break;
                    case 1:
                        Profile = 1;
                        break;
                    case 2:
                        Profile = 15;
                        break;
                    case 3:
                        Profile = 12;
                        break;
                    case 4:
                        Profile = 3;
                        break;
                    case 5:
                        Profile = 5;
                        break;
                    case 6:
                        Profile = 7;
                        break;
                    case 7:
                        Profile = 13;
                        break;
                }


                int result = Reader.rrlib.SetProfile((byte)Profile);
                if(result==0)
                {
                    Reader.writelog(getString(R.string.set_success),tvResult);
                }
                else
                {
                    Reader.writelog(getString(R.string.set_failed),tvResult);
                }
            }
		}catch(Exception ex)
		{}
	}
	
	
	private void SetFre(int m)
	{
		if(m==1){ 
		    strmaxFrm=new String[20];
         	strminFrm=new String[20];
         	for(int i=0;i<20;i++){
         		String temp="";
         		float values=(float) (920.125 + i * 0.25);
         		temp= String.valueOf(values)+"MHz";
         		strminFrm[i]=temp;
         		strmaxFrm[i]=temp;
         	}
         	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);
         	spada_maxFrm = new ArrayAdapter<String>(ScanView.this,
                      android.R.layout.simple_spinner_item, strmaxFrm);  
         	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spmaxFrm.setAdapter(spada_maxFrm);  
         	spmaxFrm.setSelection(19,false);
         	
         	spminFrm=(Spinner)findViewById(R.id.min_spinner);
         	spada_minFrm = new ArrayAdapter<String>(ScanView.this,
                      android.R.layout.simple_spinner_item, strminFrm);  
         	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spminFrm.setAdapter(spada_minFrm);  
         	spminFrm.setSelection(0,false);
     }else if(m==2){
     	strmaxFrm=new String[50];
     	strminFrm=new String[50];
     	for(int i=0;i<50;i++){
     		String temp="";
     		float values=(float) (902.75 + i * 0.5);
     		temp= String.valueOf(values)+"MHz";
     		strminFrm[i]=temp;
     		strmaxFrm[i]=temp;
     	}
     	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);
     	spada_maxFrm = new ArrayAdapter<String>(ScanView.this,
                  android.R.layout.simple_spinner_item, strmaxFrm);  
     	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
     	spmaxFrm.setAdapter(spada_maxFrm);  
     	spmaxFrm.setSelection(49,false);
     	
     	spminFrm=(Spinner)findViewById(R.id.min_spinner);
     	spada_minFrm = new ArrayAdapter<String>(ScanView.this,
                  android.R.layout.simple_spinner_item, strminFrm);  
     	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
     	spminFrm.setAdapter(spada_minFrm);  
     	spminFrm.setSelection(0,false);
     }else if(m==3){
      	strmaxFrm=new String[32];
      	strminFrm=new String[32];
      	for(int i=0;i<32;i++){
      		String temp="";
      		float values=(float) (917.1 + i * 0.2);
      		temp= String.valueOf(values)+"MHz";
      		strminFrm[i]=temp;
      		strmaxFrm[i]=temp;
      	}
      	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);
      	spada_maxFrm = new ArrayAdapter<String>(ScanView.this,
                   android.R.layout.simple_spinner_item, strmaxFrm);  
      	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
      	spmaxFrm.setAdapter(spada_maxFrm);  
      	spmaxFrm.setSelection(31,false);
      	
      	spminFrm=(Spinner)findViewById(R.id.min_spinner);
      	spada_minFrm = new ArrayAdapter<String>(ScanView.this,
                   android.R.layout.simple_spinner_item, strminFrm);  
      	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
      	spminFrm.setAdapter(spada_minFrm);  
      	spminFrm.setSelection(0,false);
      }else if(m==4){
       	strmaxFrm=new String[15];
       	strminFrm=new String[15];
       	for(int i=0;i<15;i++){
       		String temp="";
       		float values=(float) (865.1 + i * 0.2);
       		temp= String.valueOf(values)+"MHz";
       		strminFrm[i]=temp;
       		strmaxFrm[i]=temp;
       	}
       	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);
       	spada_maxFrm = new ArrayAdapter<String>(ScanView.this,
                    android.R.layout.simple_spinner_item, strmaxFrm);  
       	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
       	spmaxFrm.setAdapter(spada_maxFrm);  
       	spmaxFrm.setSelection(14,false);
       	
       	spminFrm=(Spinner)findViewById(R.id.min_spinner);
       	spada_minFrm = new ArrayAdapter<String>(ScanView.this,
                    android.R.layout.simple_spinner_item, strminFrm);  
       	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
       	spminFrm.setAdapter(spada_minFrm);  
       	spminFrm.setSelection(0,false);
       }else if(m==8){
		    strmaxFrm=new String[20];
         	strminFrm=new String[20];
         	for(int i=0;i<20;i++){
         		String temp="";
         		float values=(float) (840.125 + i * 0.25);
         		temp= String.valueOf(values)+"MHz";
         		strminFrm[i]=temp;
         		strmaxFrm[i]=temp;
         	}
         	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);
         	spada_maxFrm = new ArrayAdapter<String>(ScanView.this,
                      android.R.layout.simple_spinner_item, strmaxFrm);  
         	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spmaxFrm.setAdapter(spada_maxFrm);  
         	spmaxFrm.setSelection(19,false);
         	
         	spminFrm=(Spinner)findViewById(R.id.min_spinner);
         	spada_minFrm = new ArrayAdapter<String>(ScanView.this,
                      android.R.layout.simple_spinner_item, strminFrm);  
         	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spminFrm.setAdapter(spada_minFrm);  
         	spminFrm.setSelection(0,false);
       }
	}
}
