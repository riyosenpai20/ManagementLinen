package com.android.management_linen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.adapter.DetailsScanAdapter;
import com.android.management_linen.adapter.PdfDocumentAdapter;
import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.models.DetailsScanBersih;
import com.android.management_linen.models.ResponseData;
import com.android.management_linen.models.ResponseDataRuangan;
import com.android.management_linen.models.ResponseScanBersih;
import com.android.management_linen.models.ScanBersihResponse;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailScanBersihActivity extends AppCompatActivity {
    private int intLayout = 2;
    RecyclerView recyclerView;
    DetailsScanAdapter adapter;
    LinearLayoutManager layoutManager;
    SharedPreferences sharedpreferences;
    private List<DetailsScanBersih> detailsList = new ArrayList<>();
    public static final String SHARED_PREFS = "shared_prefs";
    public ArrayList<HashMap<String, String>> tagList, tagList2;

    public String jenisScan, dispNamaRuang, namePIC1, namePIC2;

    String token, namePIC, pdf_title, namaPerusahaan;
    TextView txtPIC, valJumlahItem;
    Button cetak;

    private ApiHelper apiService;
    private Spinner ruanganSpinner;
    private int idPerusahaan, roleUser;
    private Integer idRuang;
    private Double beratTot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);

        setContentView(R.layout.activity_detailscanbersih);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new DetailsScanAdapter(detailsList, DetailScanBersihActivity.this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        txtPIC = findViewById(R.id.txtPIC);
        ruanganSpinner = findViewById(R.id.ddRuang);
        cetak = findViewById(R.id.BtnCetak);
        pdf_title = getIntent().getStringExtra("pdf_title");
        toolbar.setTitle(pdf_title);

        String baseUrl = getResources().getString(R.string.BASE_URL);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiHelper.class);

//        ArrayList<HashMap<String, String>> tagList2 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("tagList");
        ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();

        ArrayList<String> tagUiiList = new ArrayList<>();

        for (HashMap<String, String> map : tagList2) {
            String tagUii = map.get("tagUii");
            if (tagUii != null) {
                tagUiiList.add(tagUii);
            }
        }

        Call<List<DetailsScanBersih>> call = apiService.inventory_detail_scan(tagUiiList, "Token " +token, "application/json", "application/json");

        call.enqueue(new Callback<List<DetailsScanBersih>>() {

            @Override
            public void onResponse(Call<List<DetailsScanBersih>> call, Response<List<DetailsScanBersih>> response) {
                if (response.isSuccessful() && response.body() != null){
                    detailsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    valJumlahItem = findViewById(R.id.valJumlahItem);
                    valJumlahItem.setText("" + detailsList.size());

                    System.out.println("Test: " + response.body().toString());

                } else {
                    Toast.makeText(DetailScanBersihActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DetailsScanBersih>> call, Throwable t) {
                System.out.println(t.toString());
                Toast.makeText(DetailScanBersihActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

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
                        txtPIC.setText(namePIC);
                        fetchRuangan(idPerusahaan);
                        System.out.println("Role User: " + roleUser);
                    }
                }
                System.out.println(token);
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                txtPIC.setText("Error: " + t.getMessage());
            }
        });

        cetak.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                ScanBersihResponse scanBersihResponse = new ScanBersihResponse(tagUiiList, idRuang);

                Call<ResponseScanBersih> call2 = apiService.scan_bersih(scanBersihResponse, "Token " +token, "application/json", "application/json");
                call2.enqueue(new Callback<ResponseScanBersih>() {
                    @Override
                    public void onResponse(Call<ResponseScanBersih> call, Response<ResponseScanBersih> response) {
                        if (response.isSuccessful()) {
                            String result = response.body().getResult();
                            String message = response.body().getMessage();
                            List<DetailsScanBersih> data = response.body().getData();
                            String namaRuang = ruanganSpinner.getSelectedItem().toString();
//                            showDialogScanBersih();
                            System.out.println(response.body().toString());
                            System.out.println(response.body().getData().toString());

                            try {
                                if(result.equals("ok")) {
                                    File pdfFile = generatePdf(detailsList, namaRuang, namePIC, roleUser);
                                    showPDFPreview(pdfFile);
                                } else {
                                    showAlertDialog("Perhatian!",message);
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
                        Toast.makeText(DetailScanBersihActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

//                ScanBersihResponse scanBersihResponse = new ScanBersihResponse(tagUiiList, idRuang);
//
//                Call<ResponseData> call2 = apiService.scan_bersih(scanBersihResponse, "Token " +token, "application/json", "application/json");
//                call2.enqueue(new Callback<ResponseData>() {
//                    @Override
//                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
//                        if (response.isSuccessful()) {
//                            String result = response.body().getResult();
//                            String message = response.body().getMessage();
//                            showAlertDialog("Perhatian!",message);
//                        } else {
//                            showAlertDialog("Perhatian!","Response Failed");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseData> call, Throwable t) {
//                        Toast.makeText(DetailScanBersihActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                JSONArray jsonArray = new JSONArray(tagUiiList);
//                JSONObject jsonObject = new JSONObject();
//
//                try {
//                    jsonObject.put("tag", jsonArray);
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//                try {
//                    jsonObject.put("ruang", idRuang);
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//                String jsonString = jsonObject.toString();
//                Call<> call2 = apiService.scan_bersih(jsonString,  "Token " +token, "application/json", "application/json");

//                Call<ResponseDataRuangan> getListRuang = apiService.getRuangan(idPerusahaan,"Token " +token, "application/json", "application/json");
//                getListRuang.enqueue(new Callback<ResponseDataRuangan>() {
//
//                    @Override
//                    public void onResponse(Call<ResponseDataRuangan> call, Response<ResponseDataRuangan> response) {
//                        if (response.isSuccessful()) {
//                            List<ResponseDataRuangan.Ruangan> ruanganList = response.body().getData();
//                            List<String> namaRuang = new ArrayList<>();
//                            for (ResponseDataRuangan.Ruangan ruang : ruanganList) {
//                                namaRuang.add(ruang.getNama());
//                            }
//
//                            showPdfDialog(namaRuang);
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseDataRuangan> call, Throwable t) {
//                        Toast.makeText(DetailScanBersihActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

//    private void showDialogScanBersih() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater =getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_scanbersih, null);
//        builder.setView(dialogView);
//
//        TextView txtPIC = dialogView.findViewById(R.id.txtPIC);
//        TextView txtRuang = dialogView.findViewById(R.id.txtRuang);
//        TextView txtJumlah = dialogView.findViewById(R.id.txtJumlah);
//        Button printButton = dialogView.findViewById(R.id.print_button);
//
//        String namaRuang = ruanganSpinner.getSelectedItem().toString();
//
//        txtPIC.setText(namePIC);
//        txtRuang.setText(namaRuang);
//        txtJumlah.setText(detailsList.size() + " item");
//
//        RecyclerView recyclerView1 = dialogView.findViewById(R.id.RV_scanBersih);
//        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
//        DetailsScanBersihAdapter scanBersihAdapter = new DetailsScanBersihAdapter(detailsList);
//        recyclerView1.setAdapter(scanBersihAdapter);
//
//        // Button Print
//        printButton.setOnClickListener(v -> {
//            try {
//                File pdfFile = generatePdf(detailsList, namaRuang, namePIC);
//                showPDFPreview(pdfFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
////        printButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                try {
////                    File pdfFile = generatePdf(detailsList, namaRuang, namePIC);
////                    showPDFPreview(pdfFile);
////                } catch (IOException e) {
////                    throw new RuntimeException(e);
////                }
////
////            }
////        });
//
//        builder.setPositiveButton("OK", null);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

//    private void printContent(View dialogView) {
//        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
//        printManager.print("Document", new PdfDocumentAdapter(this, dialogView), null);
//    }

//    private void showPdfDialog(List<String> namaRuang) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.dialog_pdf_viewer, null);
//        builder.setView(dialogView);
//
//        Button printButton = dialogView.findViewById(R.id.print_button);
//        TextView textView = dialogView.findViewById(R.id.pdf_text_view);
//
//        // Display the names in the TextView
//        StringBuilder content = new StringBuilder();
//        for (String nama : namaRuang) {
//            content.append(nama).append("\n");
//        }
//        textView.setText(content.toString());
//
//        AlertDialog dialog = builder.create();
//
//        printButton.setOnClickListener(v -> {
//            try {
//                File pdfFile = generatePdf(namaRuang);
//                printPdf(pdfFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        dialog.show();
//    }

//    private File generatePdf(List<String> namaRuang) throws IOException {
//        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "report.pdf");
//        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
//        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
//        Document document = new Document(pdfDoc);
//
//        for (String nama : namaRuang) {
//            document.add(new Paragraph(nama));
//        }
//
//        document.close();
//        return pdfFile;
//    }
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
    private File generatePdf(List<DetailsScanBersih> detailsList, String namaRuang, String namePIC, int roleUser) throws IOException {
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

        // Add Jumlah
        // Paragraph jmlItem = new Paragraph("Jumlah Item : " + detailsList.size());
        // jmlItem.setFontSize(12);
        // document.add(jmlItem);


        // Define column width for table
        // float[] columnWidth = {1, 1, 1};
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 2, 1}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Linen")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Warna")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Ukuran")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Ruangan")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Berat (Kg)")).setTextAlignment(TextAlignment.CENTER).setBold());

        beratTot = 0.0;
        for (DetailsScanBersih dtl : detailsList) {
            String namaBarang = dtl.getSubCategoryName();
            String ukuran = dtl.getUkuranName();
            String warna = dtl.getWarnaName();
            int batasCuci = dtl.getBatascuci();
            String namaRuang2 = dtl.getBarangruangRuangNama();
            if (namaRuang2!= null){
                dispNamaRuang = namaRuang2;
            } else {
                dispNamaRuang = "";
            }
            Double berat = dtl.getBerat();
            beratTot += berat;

            table.addCell(new Cell().add(new Paragraph(namaBarang)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(warna)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(ukuran)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("" + dispNamaRuang)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("" + berat)).setTextAlignment(TextAlignment.CENTER));
        }
//        for (Details dtl : detailsList) {
//            String namaBarang = dtl.getSubCategoryName();
//            String ukuran = dtl.getUkuranName();
//            String warna = dtl.getWarnaName();
//
//            table.addCell(new Cell().add(new Paragraph(namaBarang)));
//            table.addCell(new Cell().add(new Paragraph(ukuran)));
//            table.addCell(new Cell().add(new Paragraph(warna)));
//        }
//        for (Details dtl : detailsList) {
//            String namaBarang = dtl.getSubCategoryName();
//            String ukuran = dtl.getUkuranName();
//            String warna = dtl.getWarnaName();
//
//            table.addCell(new Cell().add(new Paragraph(namaBarang)));
//            table.addCell(new Cell().add(new Paragraph(ukuran)));
//            table.addCell(new Cell().add(new Paragraph(warna)));
//        }
//        for (Details dtl : detailsList) {
//            String namaBarang = dtl.getSubCategoryName();
//            String ukuran = dtl.getUkuranName();
//            String warna = dtl.getWarnaName();
//
//            table.addCell(new Cell().add(new Paragraph(namaBarang)));
//            table.addCell(new Cell().add(new Paragraph(ukuran)));
//            table.addCell(new Cell().add(new Paragraph(warna)));
//        }
//        for (Details dtl : detailsList) {
//            String namaBarang = dtl.getSubCategoryName();
//            String ukuran = dtl.getUkuranName();
//            String warna = dtl.getWarnaName();
//
//            table.addCell(new Cell().add(new Paragraph(namaBarang)));
//            table.addCell(new Cell().add(new Paragraph(ukuran)));
//            table.addCell(new Cell().add(new Paragraph(warna)));
//        }

        document.add(table);

        // Add empty space
//        document.add(new Paragraph("" + String.format("%.2f",beratTot)));


        Table sumTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        sumTable.setWidth(UnitValue.createPercentValue(100));
        sumTable.addCell(
                new Cell()
                        .add(new Paragraph("Total Linen : " + String.format("%d", detailsList.size())))
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

    private void showPDFPreview(File pdfFile) {
        Intent intent = new Intent(DetailScanBersihActivity.this, PreviewActivity.class);
        intent.putExtra("filePath", pdfFile.getAbsolutePath());
        startActivity(intent);
    }

    private void printPdf (File pdfFile) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintAttributes printAttributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                    .setResolution(new PrintAttributes.Resolution("res1", "Resolution", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();
            PdfDocumentAdapter pdfDocumentAdapter = new PdfDocumentAdapter(this, pdfFile.getAbsolutePath());
            printManager.print("Document", pdfDocumentAdapter, printAttributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchRuangan(int idPerusahaan) {
        Call<ResponseDataRuangan> getListRuang = apiService.getRuangan(idPerusahaan,"Token " +token, "application/json", "application/json");
        getListRuang.enqueue(new Callback<ResponseDataRuangan>() {
            @Override
            public void onResponse(Call<ResponseDataRuangan> call, Response<ResponseDataRuangan> response) {
                if (response.isSuccessful()) {
                    List<ResponseDataRuangan.Ruangan> ruanganList = response.body().getData();
                    List<String> namaRuang = new ArrayList<>();
                    final Map<String, Integer> ruanganMap = new HashMap<>();

                    for (ResponseDataRuangan.Ruangan ruang : ruanganList) {
                        namaRuang.add(ruang.getNama());
                        ruanganMap.put(ruang.getNama(), ruang.getId());
                    }

                    ArrayAdapter<String> adapterRuang = new ArrayAdapter<>(DetailScanBersihActivity.this, android.R.layout.simple_spinner_item, namaRuang);
                    adapterRuang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ruanganSpinner.setAdapter(adapterRuang);

                    ruanganSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            idRuang = ruanganMap.get(selectedItem);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                    Toast.makeText(DetailScanBersihActivity.this, "failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDataRuangan> call, Throwable t) {
                Toast.makeText(DetailScanBersihActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
        System.out.println("Tag2: " + tagList2);
        if(intLayout == 2){
            intLayout = 1;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tagList", tagList2);
            intent.putExtra("pdf_title", pdf_title);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(intLayout == 2){
            ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
            TagListHolder.getInstance().setTagList(tagList2);
            System.out.println("Tag3: " + tagList2);
            intLayout = 1;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tagList", tagList2);
            intent.putExtra("pdf_title", pdf_title);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
