package com.example.management_linen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReadWriteFragment extends Fragment  implements OnClickListener, AdapterView.OnItemClickListener, OnItemSelectedListener  {
    // Implementasi metode onItemClick yang diperlukan oleh interface OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Implementasi kosong karena tidak digunakan
    }
    private int mode;
    EditText edENum0;
    int selectedEd = 3;
    int selectedWhenPause = 0;

    Spinner c_mem;
    Spinner lock_mem;
    Spinner lock_type;
    EditText c_wordPtr;
    EditText c_len;
    EditText c_pwd;
    EditText c_kwd;
    EditText c_ptr;
    TextView tvResult;

    EditText b_id;
    EditText b_addr;
    EditText b_num;

    EditText content;
    EditText readContent;
    Button rButton;
    Button wButton;
    Button btWriteEPC;
    Button btLock;
    Button btKill;
    private ArrayAdapter<String> spada_epc;
    Spinner spepc;
    private static final int CHECK_W_6B = 0;
    private static final int CHECK_R_6B = 1;
    private static final int CHECK_W_6C = 2;
    private static final int CHECK_R_6C = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read_write, container, false);
    }

    // onViewCreated method is now defined below with the view field

    @Override
    public void onResume() {
        super.onResume();
        // TODO Auto-generated method stub
        int epcCount = ScanModeFragment.mlist.size();
        String[]epcdata = new String[epcCount];

        for (int i = 0; i < ScanModeFragment.mlist.size(); i++) {
            String temp = ScanModeFragment.mlist.get(i);
            epcdata[i] = temp;
        }

        //if(epcCount>0)
        {
            spada_epc = new ArrayAdapter<String>(requireContext(),
                    android.R.layout.simple_spinner_item, epcdata);
            spada_epc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spepc.setAdapter(spada_epc);
            if(epcCount>0)
                spepc.setSelection(0,false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private View view;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your UI components here
        // This is where you would put the code that was in ReadWriteActivity.java's onCreate
        this.view = view;
        initView();
    }

    private void initView(){
        //edENum0 = (EditText)view.findViewById(R.id.epc0);
        spepc = (Spinner)view.findViewById(R.id.epc_spinner);
        tvResult = (TextView)view.findViewById(R.id.rw_result);
        c_mem = (Spinner)view.findViewById(R.id.mem_spinner);
        ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(requireContext(), R.array.men_select, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        c_mem.setAdapter(adapter);
        c_mem.setSelection(3, true);
        c_mem.setOnItemSelectedListener(this);

        lock_mem = (Spinner)view.findViewById(R.id.lockmem_spinner);
        ArrayAdapter<CharSequence> adapter1 =  ArrayAdapter.createFromResource(requireContext(), R.array.arrayLockMem, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lock_mem.setAdapter(adapter1);
        lock_mem.setSelection(4, true);

        lock_type = (Spinner)view.findViewById(R.id.locktype_spinner);
        ArrayAdapter<CharSequence> adapter2 =  ArrayAdapter.createFromResource(requireContext(), R.array.arrayLock, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lock_type.setAdapter(adapter2);
        lock_type.setSelection(2, true);




        c_wordPtr = (EditText)view.findViewById(R.id.et_wordptr);
        c_wordPtr.setText("0");
        c_len = (EditText)view.findViewById(R.id.et_length);
        c_len.setText("6");
        c_pwd = (EditText)view.findViewById(R.id.et_pwd);
        c_pwd.setText("00000000");
        c_kwd = (EditText)view.findViewById(R.id.et_kwd);
        content = (EditText)view.findViewById(R.id.et_content_6c);
        readContent =  (EditText)view.findViewById(R.id.et_read_6c);
        rButton = (Button)view.findViewById(R.id.button_read_6c);
        wButton = (Button)view.findViewById(R.id.button_write_6c);
        btWriteEPC = (Button)view.findViewById(R.id.button_write_epc);
        btKill = (Button)view.findViewById(R.id.button_kill);
        btLock = (Button)view.findViewById(R.id.button_lock);
        rButton.setOnClickListener(this);
        wButton.setOnClickListener(this);
        btWriteEPC.setOnClickListener(this);
        btKill.setOnClickListener(this);
        btLock.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        if(v == wButton){
            if(!checkContent(CHECK_W_6C))return;
            try{
                int result = 0x30;
                String strEPC = "";
                if(spepc.getSelectedItem()!=null)
                    strEPC = spepc.getSelectedItem().toString();
                byte Mem = (byte)c_mem.getSelectedItemPosition();
                byte WordPtr =(byte)(int) Integer.valueOf(c_wordPtr.getText().toString());
                String Password =c_pwd.getText().toString();
                String strData =content.getText().toString();
                result = Reader.rrlib.WriteData_G2(strData,strEPC, Mem, WordPtr, Password);
                if(result!=0){
                    Reader.writelog(getString(R.string.write_failed), tvResult);
                }
                else{
                    Reader.writelog(getString(R.string.write_success), tvResult);
                }
            }catch(Exception ex)
            {Reader.writelog(getString(R.string.write_failed), tvResult);}


        }else if(v == rButton){
            if(!checkContent(CHECK_R_6C))return;
            try{
                String strData="";

                String strEPC ="";
                if(spepc.getSelectedItem()!=null)
                    strEPC = spepc.getSelectedItem().toString();
                byte Mem = (byte)c_mem.getSelectedItemPosition();
                byte Num = (byte)(int) Integer.valueOf(c_len.getText().toString());
                int WordPtr =(int) Integer.valueOf(c_wordPtr.getText().toString());
                String Password =c_pwd.getText().toString();
                strData = Reader.rrlib.ReadData_G2(strEPC, Mem, WordPtr,Num, Password);
                if(strData==null){
                    Reader.writelog(getString(R.string.get_failed), tvResult);
                }
                else{
                    readContent.setText(strData);
                    Reader.writelog(getString(R.string.get_success), tvResult);
                }
            }catch(Exception ex)
            {
                Reader.writelog(getString(R.string.get_failed), tvResult);
            }
        }
        else if(v == btWriteEPC)
        {
            if(!checkContent(CHECK_W_6C))return;
            try{
                int result = 0x30;
                String Password = c_pwd.getText().toString();
                String strData =content.getText().toString();
                result = Reader.rrlib.WriteEPC_G2(strData, Password);
                if(result!=0){
                    Reader.writelog(getString(R.string.write_failed), tvResult);
                }
                else{
                    Reader.writelog(getString(R.string.write_success), tvResult);
                }
            }catch(Exception ex)
            {Reader.writelog(getString(R.string.write_failed), tvResult);}
        }
        else if(v == btKill)
        {
            try{
                int result = 0x30;
                String Password = c_kwd.getText().toString();
                if(Password==null || Password.length()!=8)return;
                byte[]Killword = Util.hexStringToBytes(Password);
                String strEPC ="";
                byte[]epc = null;
                if(spepc.getSelectedItem()!=null) {
                    strEPC = spepc.getSelectedItem().toString();
                    epc = Util.hexStringToBytes(strEPC);
                }
                byte epcNum = (byte)(strEPC.length()/4);
                byte[]errorcode = new byte[1];
                for(int index =0;index<5;index++)
                {
                    result = Reader.rrlib.Kill_G2(epcNum,epc,Killword,errorcode);
                    if(result==0)break;
                }
                if(result!=0){
                    Reader.writelog(getString(R.string.kill_failed), tvResult);
                }
                else{
                    Reader.writelog(getString(R.string.kill_success), tvResult);
                }
            }catch(Exception ex)
            {Reader.writelog(getString(R.string.kill_failed), tvResult);}
        }
        else if(v == btLock)
        {
            if(!checkContent(CHECK_W_6C))return;
            try{
                int result = 0x30;
                String PasswordStr = c_pwd.getText().toString();
                String strEPC ="";
                byte[]epc = null;
                byte[]Password = Util.hexStringToBytes(PasswordStr);
                if(spepc.getSelectedItem()!=null) {
                    strEPC = spepc.getSelectedItem().toString();
                    epc = Util.hexStringToBytes(strEPC);
                }
                byte epcNum = (byte)(strEPC.length()/4);
                byte[]errorcode = new byte[1];
                byte select = (byte) (lock_mem.getSelectedItemPosition());
                byte setprotect = (byte) (lock_type.getSelectedItemPosition());
                for(int index=0;index<5;index++)
                {
                    result = Reader.rrlib.Lock_G2(epcNum,epc,select,setprotect,Password,errorcode);
                    if(result==0) break;
                }
                if(result!=0){
                    Reader.writelog(getString(R.string.lock_failed), tvResult);
                }
                else{
                    Reader.writelog(getString(R.string.lock_success), tvResult);
                }
            }catch(Exception ex)
            {Reader.writelog(getString(R.string.lock_failed), tvResult);}
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        selectedEd = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    private boolean checkContent(int check){
        switch (check) {
            case CHECK_W_6C:
                if(Util.isEtEmpty(content)) return Util.showWarning(requireContext(),R.string.content_empty_warning);
                if((content.getText().toString().length()%4)!=0)
                    return Util.showWarning(requireContext(),R.string.length_content_warning);
                if(!(Util.isLenLegal(content)))
                    return Util.showWarning(requireContext(),R.string.str_lenght_odd_warning);
                if(!(Util.isLenLegal(c_pwd)))
                    return Util.showWarning(requireContext(),R.string.str_lenght_odd_warning);
            case CHECK_R_6C:
                if(Util.isEtEmpty(c_wordPtr)) return Util.showWarning(requireContext(),R.string.wordptr_empty_warning);
                if(Util.isEtEmpty(c_len)) return Util.showWarning(requireContext(),R.string.length_empty_warning);
                if(Util.isEtEmpty(c_pwd)) return Util.showWarning(requireContext(),R.string.pwd_empty_warning);

                if(!(Util.isLenLegal(c_pwd)))
                    return Util.showWarning(requireContext(),R.string.str_lenght_odd_warning);

                break;
            default:
                break;
        }
        return true;
    }

    public String bytesToHexString(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = offset; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() == 1) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}