package com.android.management_linen;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.management_linen.component.DialogUtils;
import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.models.DetailsScanBersih;
import com.android.management_linen.models.ResponseData;
import com.android.management_linen.models.DetailScanSto;
import com.android.management_linen.models.ResponseScanBersih;
import com.android.management_linen.models.ResponseScanSTO;
import com.android.management_linen.models.ScanBersihResponse;
import com.android.management_linen.models.ScanSTOResponse;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

//import com.UHF.scanlable.UHfData.InventoryTagMap;

public class ScanMode extends AppCompatActivity implements OnClickListener, OnItemClickListener, OnItemSelectedListener {
	SharedPreferences sharedpreferences;
	public static final String SHARED_PREFS = "shared_prefs";
	public String token, namePIC, pdf_title, namaPerusahaan, jenisScan, namaRuangReport, namePIC1, namePIC2, namaRuangLinen;
	private int inventoryFlag = 1;
	Handler handler;
	private ArrayList<HashMap<String, String>> tagList;
	private SimpleAdapter adapter;
	Button BtClear;
	TextView tv_count;
	TextView tv_time;
	TextView tv_alltag;
	RadioGroup RgInventory;
	RadioButton RbInventorySingle;
	RadioButton RbInventoryLoop;
	Button Btimport;
	Button BtInventory, BtSave, BtnDetail, BtnKembali, BtnCetak;
	ListView LvTags;
	CheckBox chkstoptime;
	EditText stoptime;
	//   private Button btnFilter;//过滤
	private LinearLayout llContinuous;
	private HashMap<String, String> map;
	PopupWindow popFilter;
	public boolean isStopThread=false;
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

	private int intLayout = 2;
	private static final int REQUEST_CODE = 1;
	private Double beratTot;
	private Spinner ruanganSpinner;
	private ApiHelper apiService;
	private Integer idRuang;
	private List<DetailsScanBersih> detailsListScan = new ArrayList<>();
	private ArrayList<HashMap<String, String>> detailsListScan2;
	private int idPerusahaan, roleUser, type_rs;
	public String baseUrl;
	public List<String> listTags = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
		token = sharedpreferences.getString("token", null);
		idRuang = sharedpreferences.getInt("idRuang", 0);
		namaRuangReport = sharedpreferences.getString("namaRuangReport", null);
		pdf_title = sharedpreferences.getString("pdf_title", null);

		System.out.println("namaRuangReport: " + namaRuangReport);

		if (token == null) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		try
		{


			tagList = TagListHolder.getInstance().getTagList();
			if (tagList == null) {
				tagList = new ArrayList<>();
				TagListHolder.getInstance().setTagList(tagList);
			}

			//if use sample data uncomment this part
			//tagList = new ArrayList<HashMap<String, String>>();

//			HashSet<String> seenTags = new HashSet<>();
//			for (String tag : hardcodedData) {
//				if(!seenTags.contains(tag)) {
//					HashMap<String, String> map = new HashMap<>();
//					map.put("tagUii", tag);
//					map.put("tagLen", "24"); // Example value for tagLen
//					map.put("tagCount", "1"); // Example value for tagCount
//					map.put("tagRssi", "-55"); // Example value for tagRssi
//					tagList.add(map);
//					seenTags.add(tag);
//				}
//
//			}


			setContentView(R.layout.query);
//			tagList = new ArrayList<HashMap<String, String>>();
			BtClear = (Button) findViewById(R.id.BtClear);
			Btimport = (Button)findViewById(R.id.BtImport);
			tv_count = (TextView)findViewById(R.id.tv_count);
			tv_time = (TextView)findViewById(R.id.tv_times);
			chkstoptime = (CheckBox)findViewById(R.id.checkBox_chkstoptime);
			stoptime = (EditText)findViewById(R.id.editText_stoptime);
			tv_alltag = (TextView)findViewById(R.id.tv_alltag);
			RgInventory = (RadioGroup) findViewById(R.id.RgInventory);
			String tr = "";
			RbInventorySingle = (RadioButton) findViewById(R.id.RbInventorySingle);
			RbInventoryLoop = (RadioButton) findViewById(R.id.RbInventoryLoop);

			BtInventory = (Button)findViewById(R.id.BtInventory);
			LvTags = (ListView) findViewById(R.id.LvTags);

			llContinuous = (LinearLayout)findViewById(R.id.llContinuous);
			BtSave = (Button) findViewById(R.id.BtSend);
			BtnDetail = (Button) findViewById(R.id.BtnDetail);
			BtnKembali = (Button) findViewById(R.id.BtnKembali);
			BtnCetak = (Button) findViewById(R.id.BtnCetak);

			adapter = new SimpleAdapter(this, tagList, R.layout.listtag_items,
					new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
					new int[]{R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
							R.id.TvTagRssi});

//			//change arrayList into List<string>
//			List<String> listTags = new ArrayList<>();
//			for (HashMap<String, String> hashMap : tagList) {
//				String tagUii = hashMap.get("tagUii");
//				if (tagUii != null) {
//					listTags.add(tagUii);
//				}
//			}


			BtClear.setOnClickListener(this);
			//Btimport.setOnClickListener(this);
			Btimport.setOnClickListener(new AddSample());
			RgInventory.setOnCheckedChangeListener(new RgInventoryCheckedListener());
			BtInventory.setOnClickListener(this);
			BtSave.setOnClickListener(new SaveData());
			BtnDetail.setOnClickListener(new Detail());
			if (RbInventorySingle.isChecked()) {
				inventoryFlag = 0;
			}
			else
			{
				inventoryFlag = 1;
			}

			// Uncomment this before deploy
			Reader.rrlib.SetCallBack(callback);

			LvTags.setAdapter(adapter);

			baseUrl = getResources().getString(R.string.BASE_URL);
			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl(baseUrl)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
			apiService = retrofit.create(ApiHelper.class);

			//Get detail list
			Call<List<DetailsScanBersih>> call = apiService.inventory_detail_scan(listTags, "Token " +token, "application/json", "application/json");
			call.enqueue(new Callback<List<DetailsScanBersih>>() {

				@Override
				public void onResponse(Call<List<DetailsScanBersih>> call, Response<List<DetailsScanBersih>> response) {
					if (response.isSuccessful() && response.body() != null){
						detailsListScan.addAll(response.body());
//						adapter.notifyDataSetChanged();
//						valJumlahItem = findViewById(R.id.valJumlahItem);
//						valJumlahItem.setText("" + detailsListScan.size());

						System.out.println("Test: " + response.body().toString());

					} else {
						Toast.makeText(ScanMode.this, "Response Failed", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFailure(Call<List<DetailsScanBersih>> call, Throwable t) {
					System.out.println(t.toString());
					Toast.makeText(ScanMode.this, t.toString(), Toast.LENGTH_SHORT).show();
				}
			});

			// Get User Detail
			Call<ResponseData> getUser = apiService.getResponseData("Token " +token, "application/json", "application/json");
			getUser.enqueue(new Callback<ResponseData>() {
				@Override
				public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
					if (response.isSuccessful()) {
						ResponseData responseData = response.body();
						if (responseData != null && !responseData.getData().isEmpty()) {
							namePIC = responseData.getData().get(0).getName();
							idPerusahaan = responseData.getData().get(0).getPerusahaan();
							namaPerusahaan = responseData.getNamaPerusahaan();
							roleUser = responseData.getRoleUser();
							type_rs = responseData.getTypeRs();
							System.out.println("Role User: " + roleUser);
						}
					}
					System.out.println(token);
				}

				@Override
				public void onFailure(Call<ResponseData> call, Throwable t) {
					showAlertDialog("Error", "Error: " + t.getMessage());
				}
			});
			BtnCetak.setOnClickListener(v -> {
				sendTagsToSTO();
			});
//			BtnCetak.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View view) {
//					System.out.println("listtags: " + listTags);
//					ScanBersihResponse scanBersihResponse = new ScanBersihResponse(listTags, idRuang);
//
//					Call<ResponseScanBersih> call2 = apiService.scan_bersih(scanBersihResponse, "Token " +token, "application/json", "application/json");
//					call2.enqueue(new Callback<ResponseScanBersih>() {
//
//						@Override
//						public void onResponse(Call<ResponseScanBersih> call, Response<ResponseScanBersih> response) {
//							if (response.isSuccessful()) {
//								String result = response.body().getResult();
//								String message = response.body().getMessage();
//								List<DetailsScanBersih> data = response.body().getData();
//
////                            showDialogScanBersih();
//								System.out.println(response.body().toString());
//								System.out.println(response.body().getData().toString());
//								System.out.println("namaRuangReport2: " + namaRuangReport);
//
//								try {
//									if(result.equals("ok")) {
//										File pdfFile = generatePdf(detailsListScan, namaRuangReport, namePIC, roleUser);
//										showPDFPreview(pdfFile, namaRuangReport, roleUser);
//									} else {
//										DialogUtils.showAlertDialog(ScanMode.this, "Perhatian", message, "OK",(dialog, which) -> {
//											try {
//												File pdfFile = generatePdf(detailsListScan, namaRuangReport, namePIC, roleUser);
//												showPDFPreview(pdfFile, namaRuangReport, roleUser);
//											} catch (IOException ex) {
//												DialogUtils.showAlertDialog(ScanMode.this, "Perhatian", ex.getMessage(), "OK", (dialog1, which1) -> {
//													dialog.dismiss();
//												}, null, null);
//											}
//										}, null, null);
//
//									}
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//							} else {
//								showAlertDialog("Perhatian!","Response Failed");
//							}
//						}
//
//						@Override
//						public void onFailure(Call<ResponseScanBersih> call, Throwable t) {
//							Toast.makeText(ScanMode.this, t.toString(), Toast.LENGTH_SHORT).show();
//						}
//					});
//
//				}
//			});

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
		}
		catch(Exception e)
		{

		}
	}

	private void sendTagsToSTO() {
		List<String> listTags = new ArrayList<>();
		for (HashMap<String, String> map : tagList) {
			String tagUii = map.get("tagUii");
			if (tagUii != null) {
				listTags.add(tagUii);
			}
		}

		ScanSTOResponse scanSTOResponse = new ScanSTOResponse(listTags, idRuang);

		Call<ResponseScanSTO> call = apiService.scan_sto(scanSTOResponse, "Token " + token, "application/json", "application/json");
		call.enqueue(new Callback<ResponseScanSTO>() {
			@Override
			public void onResponse(Call<ResponseScanSTO> call, Response<ResponseScanSTO> response) {
				if (response.isSuccessful()) {
					// Get the data from response
					List<DetailScanSto> stoData = response.body().getData();
					System.out.println(stoData);
					// Create intent to start PreviewSTO activity
					Intent intent = new Intent(ScanMode.this, PreviewSTO.class);
					
					// Put the data as serializable extra
					intent.putExtra("sto_data", (java.io.Serializable) stoData);
					intent.putExtra("hospital_name", namaPerusahaan);
					intent.putExtra("room_name", namaRuangReport);
					
					// Start the activity
					startActivity(intent);
				} else {
					showAlertDialog("Perhatian!", "Response Failed");
				}
			}

			@Override
			public void onFailure(Call<ResponseScanSTO> call, Throwable t) {
				Toast.makeText(ScanMode.this, t.toString(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void Cetak() {
		List<String> listTags = new ArrayList<>();
		for (HashMap<String, String> map : tagList) {
			String tagUii = map.get("tagUii");
			if (tagUii != null) {
				listTags.add(tagUii);
			}
		}

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		apiService = retrofit.create(ApiHelper.class);

		//Get detail list
		Call<List<DetailsScanBersih>> call = apiService.inventory_detail_scan(listTags, "Token " +token, "application/json", "application/json");
		call.enqueue(new Callback<List<DetailsScanBersih>>() {

			@Override
			public void onResponse(Call<List<DetailsScanBersih>> call, Response<List<DetailsScanBersih>> response) {
				if (response.isSuccessful() && response.body() != null){
					detailsListScan.addAll(response.body());
//						adapter.notifyDataSetChanged();
//						valJumlahItem = findViewById(R.id.valJumlahItem);
//						valJumlahItem.setText("" + detailsListScan.size());

					System.out.println("Test: " + response.body().toString());
					// Step ke 2
					// Get User Detail
					Call<ResponseData> getUser = apiService.getResponseData("Token " +token, "application/json", "application/json");
					getUser.enqueue(new Callback<ResponseData>() {
						@Override
						public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
							if (response.isSuccessful()) {
								ResponseData responseData = response.body();
								if (responseData != null && !responseData.getData().isEmpty()) {
									namePIC = responseData.getData().get(0).getName();
									idPerusahaan = responseData.getData().get(0).getPerusahaan();
									namaPerusahaan = responseData.getNamaPerusahaan();
									roleUser = responseData.getRoleUser();
									type_rs = responseData.getTypeRs();
									System.out.println("Role User: " + roleUser);

									// Step ke 3
									ScanBersihResponse scanBersihResponse = new ScanBersihResponse(listTags, idRuang);

									Call<ResponseScanBersih> call2 = apiService.scan_bersih(scanBersihResponse, "Token " +token, "application/json", "application/json");
									call2.enqueue(new Callback<ResponseScanBersih>() {

										@Override
										public void onResponse(Call<ResponseScanBersih> call, Response<ResponseScanBersih> response) {
											if (response.isSuccessful()) {
												String result = response.body().getResult();
												String message = response.body().getMessage();
												List<DetailsScanBersih> data = response.body().getData();

//                            showDialogScanBersih();
												System.out.println(response.body().toString());
												System.out.println(response.body().getData().toString());
												System.out.println("namaRuangReport2: " + namaRuangReport);

												try {
													if(result.equals("ok")) {
														File pdfFile = generatePdf(detailsListScan, namaRuangReport, namePIC, roleUser);
														showPDFPreview(pdfFile, namaRuangReport, roleUser);
													} else {
														DialogUtils.showAlertDialog(ScanMode.this, "Perhatian", message, "OK",(dialog, which) -> {
															try {
																File pdfFile = generatePdf(detailsListScan, namaRuangReport, namePIC, roleUser);
																showPDFPreview(pdfFile, namaRuangReport, roleUser);
															} catch (IOException ex) {
																DialogUtils.showAlertDialog(ScanMode.this, "Perhatian", ex.getMessage(), "OK", (dialog1, which1) -> {
																	dialog.dismiss();
																}, null, null);
															}
														}, null, null);

													}
												} catch (IOException e) {
													e.printStackTrace();
												}
											} else {
												showAlertDialog("Perhatian!","Response Failed");
											}
										}

										@Override
										public void onFailure(Call<ResponseScanBersih> call, Throwable t) {
											Toast.makeText(ScanMode.this, t.toString(), Toast.LENGTH_SHORT).show();
										}
									});
								}
							}
							System.out.println(token);
						}

						@Override
						public void onFailure(Call<ResponseData> call, Throwable t) {
							showAlertDialog("Error", "Error: " + t.getMessage());
						}
					});



				} else {
					Toast.makeText(ScanMode.this, "Response Failed", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Call<List<DetailsScanBersih>> call, Throwable t) {
				System.out.println(t.toString());
				Toast.makeText(ScanMode.this, t.toString(), Toast.LENGTH_SHORT).show();
			}
		});


	}

	public class AddSample implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// For sample testing
				String[] hardcodedData = {
						"E2806915000040207D83EDCA",
						"E2806915000050207E54ACC2",
						"E2806915000040207D86C864",
						"E28069150000501684150508",
						"E2806915000050207E590A06",
						"E28069150000500DF27455A1",
						"E2806915000040168415F1E6",
						"E2806915000050207E54EA44",
						"E28069150000400DF274D9B0",
						"E2806915000050207E53050F",
						"E28069150000500DF27704E8",
						"E2806915000040207D86D148",
						"E28069150000400DF27279A8",
						"E2806915000050207FD2E9E4",
						"E2806915000050207E58D891",
						"E28069150000400DF270C156",
						"E2806915000040234CD5708B",
						"E2806915000050234CD74921",
						"E2806915000040207E56D5F6",
						"E2806915000040207D813613",
						"E28069150000500DF271FA7C",
						"E2806915000050234D509D83",
						"E2806915000050207D846066",
						"E2806915000050234CD3E282",
						"E280691500005022A8D92D97",
						"E2806915000050234CD3C270",
						"E2806915049F46DEB94AF2CC",
						"E2806915CC6391040E031E7D",
						"E28069156C344D9CB4A79FDE",
						"E280691500004022A8D5590A",
						"E2806915000050207FD7816B",
						"E2806915000040234CD161D5",
						"E280691500005022A8D290EE",
						"E280691500004021BE03042F",
						"E2806915000050234CD5AA06",
						"E2806915000050234CD4A4EE",
						"E2806915000050207FD28E0B",
						"E28069150000500DF2793102",
						"E280691500005022A8D5C477",
						"E2806915000040207FD24591",
						"E2806915000040234CD835F0",
						"E28069150000500DF2773A9D",
                        "E2806915000050234CD2826E",
                        "E280691500005022A8D90926",
                        "E2806915000040234CD8105D",
                        "E280691500004022A8D382A3",
                        "E280691500005022A8D705ED",
                        "E2806915000050234CD61EB1",
                        "E2806915000040234CD5957C",
                        "E2806915000040234CD8ADFB",
                        "E280691500005022A8D45EB6",
                        "E2806915000050234CD1195C",
                        "E2806915000040234CD390D3",
                        "E280691500004022A8D510C1",
                        "E280691500005021BE02B165",
                        "E280691500004021BE07E5CC",
                        "E2806915000040234D575491",
                        "E280691500004021BE0249CA",
                        "E2806915000050234D587C99",
                        "E280691500005022A8D2F18C",
                        "E280691500005022A8D8B1D1",
                        "E280691500004022A8D085A4",
                        "E2806915000050207EC16D88",
                        "E280691500005022A8D201D6",
                        "E280691500005021BE06C1ED",
                        "E280691500005021BE0510FF",
                        "E2806915000040234CD3DEB9",
                        "E2806915000040234CD87504",
                        "E280691500004022A8D3F4A4",
                        "E2806915000040234CD4920B",
                        "E2806915000050168413BDDC",
                        "E280691500005021BE03B905",
                        "E280691500004022A8D2C0C4",
                        "E280691500005021BE03C43C",
                        "E280691500005021BE049544",
                        "E280691500004021BE06CE13",
                        "E280691500005022A8D234A8",
                        "E280691500004022A8D4C5B3",
                        "E2806915000050234CD26174",
                        "E2806915000040234CD40D31",
                        "E280691500004022A8D43236",
                        "E2806915000050207EC53D8C",
						"E280691500004022A8D1C537",
						"E2806915000040234CD9ADC0",
						"E280691500004022A8D7C9CD",
						"E2806915000050234CD7C1B2",
						"E280691500005022A8D658DB",
						"E2806915000050234CD5BCE5",
						"E2806915000040234D57CD80",
						"E2806915000040234D56C054",
						"E280691500004022A8D59E08",
						"E280691500004022A8D5E14E",
						"E280691500004022A8D411E6",
						"E280691500005022A8D38A95",
						"E2806915000040234CD7A0B1",
						"E280691500004022A8D4EE07",
						"E280691500005021BE089131",
						"E280691500004022A8D45472",
						"E280691500005021BE0330FB",
						"E2806915000050234CD959C9",
						"E2806915000050234CD319D9",
						"E2806915000050234D56E03D",
						"E280691500004021BE03582C",
						"E280691500004022A8D2EA3A",
						"E280691500005021BE018CE5",
						"E2806915000040234CD1C4B9",
						"E2806915000050234CD06D32",
						"E2806915000040234CD91D8F",
						"E280691500004022A8D519A8",
						"E280691500004022A8D759E3",
						"E280691500004022A8D48569",
						"E280691500005021BE08AA41",
						"E280691500005021BE05BD1F",
						"E280691500005022A8D814A0",
						"E2806915000040234CD8B0AD",
						"E2806915000050234CD46515",
						"E280691500004022A8D1B08B",
						"E2806915000050234CD23256",
						"E2806915000050234CD5F566",
						"E280691500004021BE04C610",
						"E280691500005021BE02B56D",
						"E280691500004021BE00E957",
						"E2806915000040234CD42583",
						"E280691500004022A8D2554A",
						"E280691500004022A8D64D35",
						"E280691500005021BE07CDD0",
                        "E280691500005022A8D6FD82",
                        "E280691500005022A8D57CD2",
                        "E2806915000040234CD93DB7",
                        "E280691500004022A8D5F060",
                        "E2806915000040234CD780D0",
                        "E2806915000040234CD86DE3",
                        "E2806915000040234CD9A1B7",
                        "E2806915000040234CD06945",
                        "E280691500004022A8D3C2A7",
                        "E280691500004021BE07FD21",
                        "E2806915000050234CD0F4F4",
                        "E2806915000050234CD6C16E",
                        "E280691500004022A8D60A1B",
                        "E280691500004022A8D0E8C9",
                        "E280691500005021BE07F46E",
                        "E2806915000040234CD8A0B2",
						"E280691500004022A8D33257",
						"E280691500004022A8D9355F",
						"E280691500005022A8D3E885",
						"E2806915000040234CD79559",
						"E2806915000040234CD3D91E",
						"E280691500004022A8D4E644",
						"E280691500004022A8D5FEC4",
						"E2806915000050234CD24CF0",
						"E2806915000040234CD598D4",
						"E280691500004022A8D2DE67",
						"E280691500005021BE045018",
						"E2806915000040234CD5E517",
						"E2806915000050234CD0E8F6",
						"E2806915000040234CD6CE39",
						"E2806915000040207EC8D5BA",

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

			}
			catch(Exception e)
			{

			}
		}
	}

	public class RgInventoryCheckedListener implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// llContinuous.setVisibility(View.GONE);
			if (checkedId == RbInventorySingle.getId()) {
				inventoryFlag = 0;
			} else if (checkedId == RbInventoryLoop.getId()) {
				inventoryFlag = 1;
			}
		}
	}

	public class Detail implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			TagListHolder.getInstance().setTagList(tagList);
			intLayout = 2;
			Intent intent = new Intent(ScanMode.this, DetailPage.class);
			intent.putExtra("tagList", (ArrayList<HashMap<String, String>>) tagList);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			startActivityForResult(intent, REQUEST_CODE);
			finish();
		}
	}

//	public class ScanBersih implements View.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			openDetailScanBersih();
//		}
//	}

	public void openDetailScanBersih(String title) {
//		TagListHolder.getInstance().setTagList(tagList);
//		intLayout = 2;
//		Intent intent = new Intent(ScanMode.this, DetailScanBersihActivity.class);
//		intent.putExtra("tagList", (ArrayList<HashMap<String, String>>) tagList);
//		intent.putExtra("pdf_title", title);
//		overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
//		startActivityForResult(intent, REQUEST_CODE);
//		finish();
		TagListHolder.getInstance().setTagList(tagList);
		intLayout = 1;
		Intent intent = new Intent(ScanMode.this, DetailScanBersihActivity.class);
		intent.putExtra("tagList", (ArrayList<HashMap<String, String>>) tagList);
		intent.putExtra("pdf_title", title);
		sharedpreferences.edit().putString(token, token).apply();
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public class ScanKotor implements View.OnClickListener {

		@Override
		public void onClick(View v) {

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (tagList != null) {
			// Retrieve the updated tagList from the result
			tagList = (ArrayList<HashMap<String, String>>) data.getSerializableExtra("tagList");
			TagListHolder.getInstance().setTagList(tagList); // Update Singleton
		}
	}



	public class SaveData implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (token == null) {
				Intent intent = new Intent(ScanMode.this, LoginActivity.class);
				startActivity(intent);
				finish();
				return;
			}

			String baseUrl = getResources().getString(R.string.BASE_URL);

			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl(baseUrl)
					.addConverterFactory(GsonConverterFactory.create()).build();
			ApiHelper apiService = retrofit.create(ApiHelper.class);

			List<String> data = new ArrayList<>();
			for (int i = 0; i < tagList.size(); i++) {
				data.add(tagList.get(i).get("tagUii"));
			}

			try {
				Call<Void> call = apiService.inventory(data, token, "application/json", "application/json");
				call.enqueue(new Callback<Void>() {
					@Override
					public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
						if(response.code() == 200) {
//							Toast.makeText(ScanMode.this, response.code(), Toast.LENGTH_SHORT).show();
//							assert response.body() != null;
							Toast.makeText(ScanMode.this, "Data saved!", Toast.LENGTH_SHORT).show();
						} else {
							if (response.code() == 401) {
								sharedpreferences.edit().putString("token", null).apply();
								Intent intent = new Intent(ScanMode.this, TestIntent.class);
								startActivity(intent);
								finish();
							} else if (response.code() == 500) {
								Toast.makeText(ScanMode.this, "Ada kesalahan pada server!", Toast.LENGTH_LONG).show();
							}
						}
					}

					@Override
					public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
						Toast.makeText(ScanMode.this, t.toString(), Toast.LENGTH_SHORT).show();
					}
				});
			}catch (Exception e)
			{
				Toast.makeText(ScanMode.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}

			Toast.makeText(ScanMode.this, data.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	private void setViewEnabled(boolean enabled) {
		RbInventorySingle.setEnabled(enabled);
		RbInventoryLoop.setEnabled(enabled);
		//   btnFilter.setEnabled(enabled);
		BtClear.setEnabled(enabled);
	}


	public int checkIsExist(String strEPC) {
		int existFlag = -1;
		if (strEPC==null ||strEPC.length()==0) {
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

	private void clearData() {
		tv_count.setText("0");
		tv_time.setText("0");
		tv_alltag.setText("0");
		tagList.clear();
		mlist.clear();
		CardNumber =0;
		Log.i("MY", "tagList.size " + tagList.size());
		adapter.notifyDataSetChanged();
	}
	/**
	 * 添加EPC到列表中
	 *
	 * @param
	 */
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isStopThread =false;

		IntentFilter filter = new IntentFilter();
		filter.addAction(ON_TRIGGER_KEYUP);
		filter.addAction(ON_TRIGGER_KEYDOWN);
		registerReceiver(receiver, filter);

	}

	@Override
	public void onClick(View arg0) {
		try
		{
			if(arg0 == BtInventory)
			{
				if(chkstoptime.isChecked())
					clearData();

				readTag();
			}
			else if(arg0 == BtClear)
			{
				clearData();
			}

		}
		catch(Exception e)
		{
			stopInventory();
		}
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
	private void stopInventory(){
		Reader.rrlib.StopRead();

		if(timer != null){
			timer.cancel();
			timer = null;
			BtInventory.setText(getString(R.string.btStoping));
		}
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopInventory();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
							   long arg3) {
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 按钮控制盘点开始以及停止
	 */
	public class KeyBroadReceiver extends BroadcastReceiver {
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



	// Fitur cetak
	// Generate PDF
	public Table table;
	private File generatePdf(List<DetailsScanBersih> detailsListScan, String namaRuang, String namePIC, int roleUser) throws IOException {
		File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "report.pdf");
		PdfWriter writer = new PdfWriter(Files.newOutputStream(pdfFile.toPath()));
		PdfDocument pdfDoc = new PdfDocument(writer);
		Document document = new Document(pdfDoc);

		// add page number
		PageNumberEventHandler pageNumberHandler = new PageNumberEventHandler(pdfDoc);
		pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, pageNumberHandler);

		// Add title
		Paragraph title2 = new Paragraph("Report ".toUpperCase() + pdf_title.toUpperCase());
		title2.setFontSize(18);
		title2.setBold();
		title2.setTextAlignment(TextAlignment.CENTER);
		document.add(title2);

		// Add Nama Rumah Sakit
		Paragraph rs = new Paragraph("Nama RS : " + namaPerusahaan);
		rs.setFontSize(12);
		document.add(rs);

		// Add Ruang
		Paragraph ruang = new Paragraph("Ruangan : " + namaRuang);
		ruang.setFontSize(12);
		document.add(ruang);

		// Define column width for table
		// float[] columnWidth = {1, 1, 1};
		if (type_rs != 1) {
			table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 2, 1}));
		} else {
			table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 1}));
		}
		table.setWidth(UnitValue.createPercentValue(100));

		// Add table headers
		table.addHeaderCell(new Cell().add(new Paragraph("Linen")).setTextAlignment(TextAlignment.CENTER).setBold());
		table.addHeaderCell(new Cell().add(new Paragraph("Warna")).setTextAlignment(TextAlignment.CENTER).setBold());
		table.addHeaderCell(new Cell().add(new Paragraph("Ukuran")).setTextAlignment(TextAlignment.CENTER).setBold());
		if (type_rs != 1) {
			table.addHeaderCell(new Cell().add(new Paragraph("Ruangan")).setTextAlignment(TextAlignment.CENTER).setBold());
		}
		table.addHeaderCell(new Cell().add(new Paragraph("Berat (Kg)")).setTextAlignment(TextAlignment.CENTER).setBold());

		beratTot = 0.0;
		for (DetailsScanBersih dtl : detailsListScan) {
			String namaBarang = dtl.getSubCategoryName();
			String ukuran = dtl.getUkuranName();
			String warna = dtl.getWarnaName();
			int batasCuci = dtl.getBatascuci();
			String namaRuang2 = dtl.getBarangruangRuangNama();
			if (namaRuang2!= null){
				namaRuangLinen = namaRuang2;
			} else {
				namaRuangLinen = "";
			}
			Double berat = dtl.getBerat();
			beratTot += berat;

			table.addCell(new Cell().add(new Paragraph(namaBarang)).setTextAlignment(TextAlignment.CENTER));
			table.addCell(new Cell().add(new Paragraph(warna)).setTextAlignment(TextAlignment.CENTER));
			table.addCell(new Cell().add(new Paragraph(ukuran)).setTextAlignment(TextAlignment.CENTER));
			if (type_rs != 1) {
				table.addCell(new Cell().add(new Paragraph("" + namaRuangLinen)).setTextAlignment(TextAlignment.CENTER));
			}
			table.addCell(new Cell().add(new Paragraph("" + berat)).setTextAlignment(TextAlignment.CENTER));
		}

		document.add(table);

		Table sumTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
		sumTable.setWidth(UnitValue.createPercentValue(100));
		sumTable.addCell(
				new Cell()
						.add(new Paragraph("Total Linen : " + String.format("%d", detailsListScan.size())))
						.setBorder(Border.NO_BORDER)
						.setBold()
						.setTextAlignment(TextAlignment.CENTER)
		);
		sumTable.addCell(
				new Cell()
						.add(new Paragraph("Total Berat (Kg) : " + String.format("%.2f", beratTot)))
						.setBorder(Border.NO_BORDER)
						.setBold()
						.setTextAlignment(TextAlignment.CENTER)
		);
		document.add(sumTable);
		document.add(new Paragraph(""));
		document.add(new Paragraph(""));

		Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
		footerTable.setWidth(UnitValue.createPercentValue(100));

		// Get current time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm");
		String currentTime = LocalDateTime.now().format(formatter);


		footerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
		footerTable.addCell(new Cell().add(new Paragraph("" + currentTime))
				.setTextAlignment(TextAlignment.CENTER)
				.setBorder(Border.NO_BORDER));

		document.add(footerTable);

		Table footerTable2 = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
		footerTable2.setWidth(UnitValue.createPercentValue(100));

		footerTable2.addCell(new Cell().add(new Paragraph("TTD Ruangan"))
				.setTextAlignment(TextAlignment.CENTER)
				.setBorderTop(new SolidBorder(1))
				.setBorderLeft(new SolidBorder(1))
				.setBorderRight(new SolidBorder(1))
				.setBorderBottom(Border.NO_BORDER)
		);
		footerTable2.addCell(new Cell().add(new Paragraph("TTD Laundry"))
				.setTextAlignment(TextAlignment.CENTER)
				.setBorderTop(new SolidBorder(1))
				.setBorderLeft(new SolidBorder(1))
				.setBorderRight(new SolidBorder(1))
				.setBorderBottom(Border.NO_BORDER)
		);

		footerTable2.addCell(new Cell().add(new Paragraph(""))
				.setBorderLeft(new SolidBorder(1))
				.setBorderRight(new SolidBorder(1))
				.setBorderTop(Border.NO_BORDER)
				.setBorderBottom(Border.NO_BORDER)
				.setHeight(30)
		);

		footerTable2.addCell(new Cell().add(new Paragraph(""))
				.setBorderLeft(new SolidBorder(1))
				.setBorderRight(new SolidBorder(1))
				.setBorderTop(Border.NO_BORDER)
				.setBorderBottom(Border.NO_BORDER)
				.setHeight(30)
		);

		/* TODO: Add PIC
		*   But for now client doesn't want it, so it will be comment first
		*
		// This is for write client name on signature

		if(roleUser == 2) {
			namePIC1 = namePIC;
			namePIC2 = "";
		} else if (roleUser == 3) {
			namePIC1 = "";
			namePIC2 = namePIC;
		} else {
			namePIC1 = "";
			namePIC2 = "";
		}
		* */
		namePIC1 = "";
		namePIC2 = "";

		footerTable2.addCell(new Cell().add(new Paragraph("" + namePIC1))
				.setTextAlignment(TextAlignment.CENTER)
				.setBorderTop(Border.NO_BORDER)
				.setBorderLeft(new SolidBorder(1))
				.setBorderRight(new SolidBorder(1))
				.setBorderBottom(new SolidBorder(1))
		);
		footerTable2.addCell(new Cell().add(new Paragraph("" + namePIC2))
				.setTextAlignment(TextAlignment.CENTER)
				.setBorderTop(Border.NO_BORDER)
				.setBorderLeft(new SolidBorder(1))
				.setBorderRight(new SolidBorder(1))
				.setBorderBottom(new SolidBorder(1))
		);

		document.add(footerTable2);
		// Add PIC
//        document.add(new Paragraph(currentTime))
//                .setTextAlignment(TextAlignment.RIGHT)
//                .setFontSize(12);
//        document.add(new Paragraph("tanda tangan"))
//                .setTextAlignment(TextAlignment.RIGHT)
//                .setFontSize(12);
//        document.add(new Paragraph(""));
//        document.add(new Paragraph(""));
//        Paragraph pic = new Paragraph(namePIC);
//        pic.setFontSize(12);
//        document.add(pic)
//                .setTextAlignment(TextAlignment.RIGHT);

//        document.add(new AreaBreak());

//        int numberOfPages = pdfDoc.getNumberOfPages();
//        for(int i = 1; i<= numberOfPages; i++) {
//            PdfPage page = pdfDoc.getPage(i);
//            int pageWidth = (int) page.getPageSize().getWidth();
//            int pageHeight = (int) page.getPageSize().getHeight();
//
//            document.showTextAligned(new Paragraph(String.format("Page %d of %d", i, numberOfPages)), pageWidth - 50, 20, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
//        }

		document.close();
		return pdfFile;
	}
	private class PageNumberEventHandler implements IEventHandler {
		private PdfFont font;
		private PdfDocument pdfDoc;

		public PageNumberEventHandler(PdfDocument pdfDoc) throws IOException {
			this.font = PdfFontFactory.createFont();
			this.pdfDoc = pdfDoc;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public void handleEvent(Event event) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
			PdfPage page = docEvent.getPage();
			int pageNumber = pdfDoc.getPageNumber(page);
			int totalPages = pdfDoc.getNumberOfPages();
			Rectangle pageSize = page.getPageSize();
			PdfCanvas pdfCanvas = new PdfCanvas(page);

			pdfCanvas.beginText()
					.setFontAndSize(font, 12)
					.moveText(pageSize.getWidth() - 100, pageSize.getBottom() + 20)
					.showText(String.format("%d / %d", pageNumber, totalPages))
					.endText()
					.release();
		}

	}

	private void showPDFPreview(File pdfFile, String namaRuangReport2, int roleUser) {
		intLayout = 3;
		TagListHolder.getInstance().setTagList(tagList);
		Intent intent = new Intent(ScanMode.this, PreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("detailsListScan", new ArrayList<>(detailsListScan));
		System.out.println("namaRuangReport3: " + namaRuangReport2);
		System.out.println("namaPerusahaan: " + namaPerusahaan);
		intent.putExtra("filePath", pdfFile.getAbsolutePath());
		intent.putExtra("tagList", (ArrayList<HashMap<String, String>>) tagList);
		intent.putExtras(bundle);
		sharedpreferences.edit().putString("pdf_title", pdf_title).apply();
		sharedpreferences.edit().putString(token, token).apply();
		sharedpreferences.edit().putInt("idRuang", idRuang).apply();
		sharedpreferences.edit().putString("namaRuangReport", namaRuangReport2).apply();
		sharedpreferences.edit().putString("namaPerusahaan", namaPerusahaan).apply();
		sharedpreferences.edit().putString("beratTot", String.format("%.2f", beratTot)).apply();
		sharedpreferences.edit().putInt("type_rs", type_rs).apply();
		startActivity(intent);
	}

	private void showAlertDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title)
				.setMessage(message)
				.setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
				.setCancelable(true)
				.show();
	}

	@Override
	public void finish() {
		super.finish();
	}
	@Override
	public void onBackPressed() {
		ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
		System.out.println("Tag2: " + tagList2);
		if(intLayout == 2){
			intLayout = 1;
			Intent intent = new Intent(this, STORuanganActivity.class);
//			intent.putExtra("tagList", tagList2);
			sharedpreferences.edit().putString("pdf_title", pdf_title).apply();
			sharedpreferences.edit().putString(token, token).apply();
			sharedpreferences.edit().putInt("idRuang", idRuang).apply();
			sharedpreferences.edit().putString("namaRuangReport", namaRuangReport).apply();
			startActivity(intent);
			finish();
		} else {
			super.onBackPressed();
		}
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
