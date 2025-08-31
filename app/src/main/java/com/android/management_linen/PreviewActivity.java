package com.android.management_linen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.adapter.DetailsScanBersihAdapter;
import com.android.management_linen.adapter.GroupedScanAdapter;
import com.android.management_linen.adapter.PdfDocumentAdapter;
import com.android.management_linen.component.DialogUtils;
import com.android.management_linen.helpers.GroupedScan;
import com.android.management_linen.helpers.ScanGrouper;
import com.android.management_linen.models.DetailsScanBersih;
import com.android.management_linen.utils.PrinterUtils;
import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
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
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PreviewActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    private int intLayout = 3;
    private PDFView pdfView;
    private Button btnPrint, btnPdf, btnAddLine;
    public String namaRuangReport, pdf_title, token, namaPerusahaan, beratTot, namaRuangLinen, currentDateAndTime;
    public int idRuang, type_rs;

    private List<DetailsScanBersih> detailsListScan;

    //For Connection Bluetooth Printer
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS_BT = 2;
    private static final String PRINTER_MAC_ADDRESS = "86:67:7A:5E:95:FA";
    private static final String PRINTER_NAME = "SYNERGI_PRINTER"; // Ganti dengan nama printer Anda
    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Ganti dengan UUID printer Anda
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice printerDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private Set<BluetoothDevice> pairedDevices;
    private Dialog loadingDialog;
    RecyclerView rv_table;
    DetailsScanBersihAdapter adpDetailsScanBersih;
    TextView titleReport, namaRSReport, namaRuangReport2, datePrint, totalLinen2, totalBerat, header_ruangan;
    public Table table;
    private Double pdfberatTot;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        idRuang = sharedpreferences.getInt("idRuang", 0);
        namaRuangReport = sharedpreferences.getString("namaRuangReport", null);
        namaPerusahaan = sharedpreferences.getString("namaPerusahaan", null);
        pdf_title = sharedpreferences.getString("pdf_title", null);
        beratTot = sharedpreferences.getString("beratTot", null);
        type_rs = sharedpreferences.getInt("type_rs", 0);

        System.out.println("namaPerusahaan: " + namaPerusahaan);
        System.out.println("namaRuangReport: " + namaRuangReport);
        System.out.println("type_rs: " + type_rs);

        setContentView(R.layout.activity_preview);

//        pdfView = findViewById(R.id.pdfView);
        btnPrint = findViewById(R.id.btnPrint);
        btnPdf = findViewById(R.id.btnPdf);
        btnAddLine = findViewById(R.id.btnAddNewLine);
        rv_table = findViewById(R.id.rv_table);
        titleReport = findViewById(R.id.titleReport);
        namaRSReport = findViewById(R.id.tv_namaRS);
        namaRuangReport2 = findViewById(R.id.tv_namaRuang);
        totalBerat = findViewById(R.id.tv_totalBerat);
        totalLinen2 = findViewById(R.id.tv_totalLinen);
        datePrint = findViewById(R.id.tv_datePrint);
//        header_ruangan = findViewById(R.id.header_ruangan);

        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Bluetooth is enabled, proceed with your logic
                    } else {
                        // Bluetooth is not enabled, handle accordingly
                    }
                }
        );

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        detailsListScan = bundle.getParcelableArrayList("detailsListScan");
        System.out.println("detailsListScan test: " + detailsListScan.size());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        String[] date = {"",currentDateAndTime};

        int totalLinen = detailsListScan.size();

        namaRSReport.setText(namaPerusahaan);
        namaRuangReport2.setText(namaRuangReport);
        titleReport.setText("REPORT " + pdf_title.toUpperCase());
        totalLinen2.setText(String.valueOf(totalLinen));
        totalBerat.setText(beratTot);
        datePrint.setText(currentDateAndTime);
        rv_table.setLayoutManager(new LinearLayoutManager(this));

//        if(type_rs == 1)
//        {
//            header_ruangan.setVisibility(View.GONE);
//        }
//        else
//        {
//            header_ruangan.setVisibility(View.VISIBLE);
//        }

//        adpDetailsScanBersih = new DetailsScanBersihAdapter(detailsListScan, PreviewActivity.this, type_rs);
//        rv_table.setAdapter(adpDetailsScanBersih);
        // Panggil helper
        List<GroupedScan> groupedList = ScanGrouper.group(detailsListScan);

        GroupedScanAdapter adapter = new GroupedScanAdapter(groupedList,PreviewActivity.this, type_rs);
        rv_table.setAdapter(adapter);

        initBluetooth();
        btnPdf.setOnClickListener(v -> {
            try {
                File pdfFile = generatePdf(detailsListScan, namaRuangReport);
                printPdf(pdfFile);
            } catch (IOException ex) {
                DialogUtils.showAlertDialog(PreviewActivity.this, "Perhatian", ex.getMessage(), "OK", (dialog1, which1) -> {
                    dialog1.dismiss();
                }, null, null);
            }
//            DialogUtils.showAlertDialog(PreviewActivity.this, "Perhatian", "", "OK",(dialog, which) -> {
//                try {
//                    File pdfFile = generatePdf(detailsListScan, namaRuangReport);
//                    printPdf(pdfFile);
//                } catch (IOException ex) {
//                    DialogUtils.showAlertDialog(PreviewActivity.this, "Perhatian", ex.getMessage(), "OK", (dialog1, which1) -> {
//                        dialog.dismiss();
//                    }, null, null);
//                }
//            }, null, null);
//            DialogUtils.showAlertDialog(PreviewActivity.this, "Informasi", "Mohon maaf, fitur pdf sedang dalam perbaikan", "OK", (dialog1, which1) -> {
//                dialog1.dismiss();
//            }, null, null);
        });
        btnPrint.setOnClickListener(v -> {
            if(setBTSocket()) {
                printReceipt2();
                new Handler(Looper.getMainLooper()).postDelayed(this::disconnectBluetooth, 500);
            }
        });
        btnAddLine.setOnClickListener(v -> {
            if(setBTSocket()) {
                addNewLine();
                new Handler(Looper.getMainLooper()).postDelayed(this::disconnectBluetooth, 80);
            }
        });
    }

    private void printPdf(File pdfFile) {
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

    private File generatePdf(List<DetailsScanBersih> detailsListScan, String namaRuang) throws IOException {
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
            table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 2, 1, 1}));
        } else {
            table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 1, 1}));
        }
        table.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Linen")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Warna")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Ukuran")).setTextAlignment(TextAlignment.CENTER).setBold());
        if (type_rs != 1) {
            table.addHeaderCell(new Cell().add(new Paragraph("Ruangan")).setTextAlignment(TextAlignment.CENTER).setBold());
        }
        table.addHeaderCell(new Cell().add(new Paragraph("Jumlah")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Berat (Kg)")).setTextAlignment(TextAlignment.CENTER).setBold());

        // Panggil helper
        List<GroupedScan> groupedList = ScanGrouper.group(detailsListScan);

        pdfberatTot = 0.0;
        for (GroupedScan dtl : groupedList) {
            String namaBarang = dtl.getSubCategoryName();
            String ukuran = dtl.getUkuranName();
            String warna = dtl.getWarnaName();
//            int batasCuci = dtl.getBatasCuci();
            String namaRuang2 = dtl.getRuangName();
            if (namaRuang2!= null){
                namaRuangLinen = namaRuang2;
            } else {
                namaRuangLinen = "";
            }
            Double berat = dtl.getTotalBerat();
            int jumlah = dtl.getJumlah();
            pdfberatTot += berat;

            table.addCell(new Cell().add(new Paragraph(namaBarang)).setTextAlignment(TextAlignment.LEFT));
            table.addCell(new Cell().add(new Paragraph(warna)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(ukuran)).setTextAlignment(TextAlignment.CENTER));
            if (type_rs != 1) {
                table.addCell(new Cell().add(new Paragraph("" + namaRuangLinen)).setTextAlignment(TextAlignment.CENTER));
            }
            table.addCell(new Cell().add(new Paragraph("" + jumlah)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("" + String.format("%.2f", berat))).setTextAlignment(TextAlignment.CENTER));
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
                        .add(new Paragraph("Total Berat (Kg) : " + String.format("%.2f", pdfberatTot)))
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
//        namePIC1 = "";
//        namePIC2 = "";

        footerTable2.addCell(new Cell().add(new Paragraph(""))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderBottom(new SolidBorder(1))
        );
        footerTable2.addCell(new Cell().add(new Paragraph(""))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderBottom(new SolidBorder(1))
        );

        document.add(footerTable2);
        document.close();
        return pdfFile;
    }

    private void initBluetooth() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Device ini tidak mendukung koneksi Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableIntent);
        }
    }

    public void findBluetoothDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(PRINTER_NAME)) {
                        printerDevice = device;

                        break;
                    }
                }
            }
        }
    }

    public boolean setBTSocket() {
        findBluetoothDevice();
        // Percobaan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API level 31) and higher
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, REQUEST_ENABLE_BT);
            } else {
                findPairedPrinter();
            }
        } else {
            // Android 11 (API level 30) and lower
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_ENABLE_BT);
            } else {
                findPairedPrinter();
            }
        }

        return connectToPrinter();
//        System.out.println("permission granted");
//        findPairedPrinter();

//        bluetoothSocket = null;
//        try {
//            bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
//            bluetoothAdapter.cancelDiscovery();
//            try {
//                bluetoothSocket.connect();
//                outputStream = bluetoothSocket.getOutputStream();
//                System.out.println("menghubungkan bluetooth socket");
//            } catch (IOException e) {
//                try {
//                    bluetoothSocket.close();
//                    System.out.println("menutup bluetooth socket");
//                } catch (IOException e1) {
//                    System.out.println("Error: " + e1);
//                }
//                alertDialog("Perhatian", "Ada kemungkinan printer dalam keadaan mati, silahkan periksa kembali.");
//            }
//        } catch (IOException e) {
//
//        }


//        try {

//            if (printerDevice != null) {
//                boolean isPrinterOff = isPrinterOff(printerDevice);
//                if (isPrinterOff) {
//                    Toast.makeText(this, "isPrinterOff: " + isPrinterOff, Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(this, "isPrinterOff: " + isPrinterOff, Toast.LENGTH_LONG).show();
//                }
//            } else {
//                bluetoothSocket.close();
//            }
//        } catch (IOException e) {
////            AlertDialog.Builder builder = new AlertDialog.Builder(this);
////            builder.setTitle("Perhatian")
////                    .setMessage("Printer tidak ditemukan, periksa kembali.")
////                    .setPositiveButton("OK", (dialog, which) -> finish())
////                    .show();
//            Toast.makeText(this, "Printer not found", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//            System.out.println(e.toString());
//        }
    }




    private void findPairedPrinter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API level 31) and higher
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
            }
        } else {
            // Android 11 (API level 30) and lower
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH/*, Manifest.permission.BLUETOOTH_ADMIN*/}, REQUEST_ENABLE_BT);
            }
        }
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    System.out.println("device.getName() : " + device.getAlias().toString());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (device.getAlias().toString().equals(PRINTER_NAME)) {
                        printerDevice = device;
                        break;
                    }
                }
            }
        }
        System.out.println("Nama Printer : " + printerDevice);
        System.out.println("pairedDevices : " + pairedDevices);
    }

    private boolean connectToPrinter() {
        findPairedPrinter(); // pastikan ini tidak null-kan printerDevice
        System.out.println("Konek ke Printer : " + printerDevice);
        if(printerDevice == null) {
            alertDialog("Printer Tidak ditemukan", "Tidak ada perangkat printer yang cocok dengan nama yang ditentukan.");
            return false;
        }
        bluetoothSocket = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12 (API level 31) and higher
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                }
            } else {
                // Android 11 (API level 30) and lower
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH/*, Manifest.permission.BLUETOOTH_ADMIN*/}, REQUEST_ENABLE_BT);
                }
            }
            bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
            try {
                bluetoothAdapter.cancelDiscovery();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
            try {
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                System.out.println("menghubungkan bluetooth socket");
                return true;
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                    System.out.println("menutup bluetooth socket");
                } catch (IOException e1) {
                    System.out.println("Error: " + e1);
                }
                alertDialog("Perhatian", "Ada kemungkinan printer dalam keadaan mati, silahkan periksa kembali.");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            alertDialog("Error", "Terjadi kesalahan saat menghubungkan ke printer.");
            return false;
        }
    }

    public void alertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(PreviewActivity.this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);

        ImageView loadingImage = loadingDialog.findViewById(R.id.loading_image);
        Glide.with(PreviewActivity.this).asGif().load(R.drawable.loading).into(loadingImage);
        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if(loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with device discovery
                findPairedPrinter();
            } else {

            }
        }
    }

//    private void printReceipt() {
//        try {
//            String title = "Report ".toUpperCase() + pdf_title.toUpperCase();
//            String namaRS = "Nama RS : " + namaPerusahaan;
//            String namaRuang = "Ruangan : " + namaRuangReport;
//            int totalLinen = detailsListScan.size();
//
//
//            int[] separatorWidth = {48}; // Adjust as necessary
//
//            // Title
//            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
//            outputStream.write(new byte[]{0x1B, 0x45, 0x01}); // Bold start
//            outputStream.write(new byte[]{0x1D, 0x21, 0x10}); // Double height + width
//            outputStream.write(title.getBytes("UTF-8"));
//            outputStream.write(new byte[]{0x0A}); // Line feed
//            outputStream.write(new byte[]{0x1B, 0x45, 0x00}); // Bold end
//            outputStream.write(new byte[]{0x1D, 0x21, 0x00}); // Normal height + width
//            outputStream.write(new byte[]{0x1B, 0x64, 0x01}); // add 1 line
//
//            // Nama RS dan Ruang
//            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Center align
//            outputStream.write(namaRS.getBytes("UTF-8"));
//            outputStream.write(new byte[]{0x0A}); // Line feed
//            outputStream.write(namaRuang.getBytes("UTF-8"));
//            outputStream.write(new byte[]{0x0A}); // Line feed
//
//
//            // Tabel Detail
//            if(type_rs != 1) {
//                int[] columnWidths1 = {10, 9, 7, 13, 5}; // Adjust as necessary
//                int[] alignments1 = {1, 1, 1, 1, 1}; // 0: Left, 1: Center, 2: Right
//                String[] headers1 = {"Linen", "Warna", "Ukuran", "Ruangan", "Berat"};
//                printSeparatorLine(outputStream, separatorWidth);
//                printRow(outputStream, headers1, columnWidths1, alignments1, true, false);
//                printSeparatorLine(outputStream, separatorWidth);
//            } else {
//                int[] columnWidths1 = {12, 11, 11, 11}; // Adjust as necessary
//                int[] alignments1 = {1, 1, 1, 1}; // 0: Left, 1: Center, 2: Right
//                String[] headers1 = {"Linen", "Warna", "Ukuran", "Berat"};
//                printSeparatorLine(outputStream, separatorWidth);
//                printRow(outputStream, headers1, columnWidths1, alignments1, true, false);
//                printSeparatorLine(outputStream, separatorWidth);
//            }
//
//            List<String[]> rows = new ArrayList<>();
//            for (DetailsScanBersih dtl : detailsListScan) {
//                String namaBarang = dtl.getSubCategoryName();
//                String ukuran = dtl.getUkuranName();
//                String warna = dtl.getWarnaName();
//                int batasCuci = dtl.getBatascuci();
//                String namaRuang2 = dtl.getBarangruangRuangNama();
//                if (namaRuang2!= null){
//                    namaRuangLinen = namaRuang2;
//                } else {
//                    namaRuangLinen = "";
//                }
//                Double berat = dtl.getBerat();
//                String dispBerat = berat.toString();
//                if(type_rs !=1 ) {
//                    rows.add(new String[]{namaBarang, warna, ukuran, namaRuangLinen, dispBerat});
//                } else {
//                    rows.add(new String[]{namaBarang, warna, ukuran, dispBerat});
//                }
//            }
//
//            if(type_rs != 1) {
//                int[] columnWidths1 = {10, 9, 7, 13, 5}; // Adjust as necessary
//                int[] alignments1 = {1, 1, 1, 1, 1}; // 0: Left, 1: Center, 2: Right
//                for (String[] row : rows) {
//                    printRow(outputStream, row, columnWidths1, alignments1, false, false);
//                }
//            } else {
//                int[] columnWidths1 = {21, 11, 11, 11}; // Adjust as necessary
//                int[] alignments1 = {1, 1, 1, 1}; // 0: Left, 1: Center, 2: Right
//                for (String[] row : rows) {
//                    printRow(outputStream, row, columnWidths1, alignments1, false, false);
//                }
//            }
//
//            PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");
//
//            // Print total item and weight
//            int[] colTot = {23,24};
//            int[] alignments2 = {1, 1}; // 0: Left, 1: Center, 2: Right
//            String[] headers2 = {"Total Linen: " + totalLinen, "Total Berat (Kg): " + beratTot};
//            printRow(outputStream, headers2, colTot, alignments2, true, false);
//
//            // Print Date and time
//            int[] colDateWidth = {23, 23};
//            int[] alignments4 = {1, 1}; // 0: Left, 1: Center, 2: Right
//
//            String[] date = {"",currentDateAndTime};
//            printRow(outputStream, date, colDateWidth, alignments4, true, false);
//
//            // Signature
//            int[] columnWidths3 = {23, 23}; // Adjust as necessary
//            int[] alignments5 = {1, 1}; // 0: Left, 1: Center, 2: Right
//            String[] footer = {"TTD Ruangan", "TTD Laundry"};
//            printSeparatorLine(outputStream, separatorWidth);
//            printRow(outputStream, footer, columnWidths3, alignments5, true, false);
//            printSeparatorLine(outputStream, separatorWidth);
//            outputStream.write(new byte[]{0x1B, 0x64, 0x03});
//
//            PrinterUtils.printSeparatorLine(outputStream, separatorWidth, "=");
//
//            outputStream.write(new byte[]{0x1B, 0x64, 0x01});
//            outputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    class GroupedDetail {
        String namaBarang;
        String ukuran;
        String warna;
        String namaRuang;
        int batasCuci;
        int jumlah;
        double berat;
        public  GroupedDetail(String namaBarang, String ukuran, String warna) {
            this.namaBarang = namaBarang;
            this.ukuran = ukuran;
            this.warna = warna;
            this.namaRuang = "";
            this.batasCuci = 0;
            this.jumlah = 0;
            this.berat = 0.0;

        }
    }

    private void printReceipt2() {
        try {
            String title = "Report ".toUpperCase() + pdf_title.toUpperCase();
            String namaRS = "Nama RS : " + namaPerusahaan;
            String namaRuang = "Ruangan : " + namaRuangReport;
            int totalLinen = detailsListScan.size();


            int[] separatorWidth = {48}; // Adjust as necessary

            // Title
            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
            outputStream.write(new byte[]{0x1B, 0x45, 0x01}); // Bold start
            outputStream.write(new byte[]{0x1D, 0x21, 0x10}); // Double height + width
            outputStream.write(title.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x0A}); // Line feed
            outputStream.write(new byte[]{0x1B, 0x45, 0x00}); // Bold end
            outputStream.write(new byte[]{0x1D, 0x21, 0x00}); // Normal height + width
            outputStream.write(new byte[]{0x1B, 0x64, 0x01}); // add 1 line

            // Nama RS dan Ruang
            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Center align
            outputStream.write(namaRS.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x0A}); // Line feed
            outputStream.write(namaRuang.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x0A}); // Line feed


//            // Tabel Detail v.2
//            if(type_rs != 1) {
//                int[] columnWidths1 = {12, 9, 7, 7, 5, 5}; // Adjust as necessary
//                int[] alignments1 = {0, 0, 0, 0, 0, 0}; // 0: Left, 1: Center, 2: Right
//                String[] headers1 = {"Linen", "Warna", "Ukuran", "Ruangan", "Jml", "Berat"};
////                printSeparatorLine(outputStream, separatorWidth);
//                PrinterUtils.printSeparatorLine(outputStream, separatorWidth);
////                printRow2(outputStream, headers1, columnWidths1, alignments1, true);
//                PrinterUtils.printRow(outputStream, headers1, columnWidths1, alignments1, true, false);
//                PrinterUtils.printSeparatorLine(outputStream, separatorWidth);
//            } else {
//                int[] columnWidths1 = {12, 11, 11, 6, 5}; // Adjust as necessary
//                int[] alignments1 = {0, 0, 0, 0, 0}; // 0: Left, 1: Center, 2: Right
//                String[] headers1 = {"Linen", "Warna", "Ukuran", "Jml", "Berat"};
//                PrinterUtils.printSeparatorLine(outputStream, separatorWidth);
//                PrinterUtils.printRow(outputStream, headers1, columnWidths1, alignments1, true, false);
//                PrinterUtils.printSeparatorLine(outputStream, separatorWidth);
//            }

            Map<String, GroupedDetail> groupedMap = new LinkedHashMap<>();

            for (DetailsScanBersih dtl : detailsListScan) {
                String namaBarang = dtl.getSubCategoryName();
                String ukuran = dtl.getUkuranName();
                String warna = dtl.getWarnaName();
//                int batasCuci = dtl.getBatascuci();
//                String namaRuang2 = dtl.getBarangruangRuangNama();
//                if (namaRuang2!= null){
//                    namaRuangLinen = namaRuang2;
//                } else {
//                    namaRuangLinen = "";
//                }
                String key = namaBarang + "|" + ukuran + "|" + warna;

                GroupedDetail grp = groupedMap.getOrDefault(key, new GroupedDetail(namaBarang, ukuran, warna));

                if(grp.namaRuang == null || grp.namaRuang.isEmpty()) {
                    String namaRuang2 = dtl.getBarangruangRuangNama();
                    if(namaRuang2 != null) {
                        grp.namaRuang = namaRuang2;
                    }
                }

                grp.batasCuci = dtl.getBatascuci();
                grp.jumlah += 1;
                grp.berat += dtl.getBerat();

                groupedMap.put(key, grp);

//                String dispBerat = berat.toString();
//                if(type_rs !=1 ) {
//                    rows.add(new String[]{namaBarang, warna, ukuran, namaRuangLinen, dispBerat});
//                } else {
//                    rows.add(new String[]{namaBarang, warna, ukuran, dispBerat});
//                }
            }

            // beri separator untuk awal
            PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");

            List<String[]> rows = new ArrayList<>();
            for(GroupedDetail grp : groupedMap.values()) {
//                if (type_rs != 1) {
//                    rows.add(new String[] {
//                            grp.namaBarang,
//                            grp.warna,
//                            grp.ukuran,
//                            grp.namaRuang,
//                            String.valueOf(grp.jumlah),
//                            String.format("%.2f", grp.berat),
//                            String.valueOf(grp.batasCuci)
//                    });
//                } else {
//                    rows.add(new String[] {
//                            grp.namaBarang,
//                            grp.warna,
//                            grp.ukuran,
//                            String.valueOf(grp.jumlah),
//                            String.format("%.2f", grp.berat)
//                    });
//                }


                String namaBarangLine = String.format("Nama Barang : %s", grp.namaBarang);
                outputStream.write(namaBarangLine.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x0A});

                String warnaLine = String.format("Warna       : %s", grp.warna);
                outputStream.write(warnaLine.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x0A});

                String ukuranLine = String.format("Ukuran      : %s", grp.ukuran);
                outputStream.write(ukuranLine.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x0A});

                if (type_rs != 1) {
                    String ruangLine = String.format("Ruangan     : %s", grp.namaRuang);
                    outputStream.write(ruangLine.getBytes("UTF-8"));
                    outputStream.write(new byte[]{0x0A});
                }

                String jumlahLine = String.format("Jumlah      : %d pcs", grp.jumlah);
                outputStream.write(jumlahLine.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x0A});

                String beratLine = String.format("Berat       : %.2f kg", grp.berat);
                outputStream.write(beratLine.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x0A});

                PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");
//                outputStream.write(separator.getBytes("UTF-8"));
//                outputStream.write(new byte[]{0x0A, 0x0A}); // Tambah LF biar renggang
            }

//            if(type_rs != 1) {
//                int[] columnWidths1 = {12, 9, 7, 7, 5, 5}; // Adjust as necessary
//                int[] alignments1 = {0, 0, 0, 0, 0, 0}; // 0: Left, 1: Center, 2: Right
//                for (String[] row : rows) {
//                    PrinterUtils.printRow(outputStream, row, columnWidths1, alignments1, false, true);
//                }
//            } else {
//                int[] columnWidths1 = {12, 11, 11, 6, 5}; // Adjust as necessary
//                int[] alignments1 = {0, 0, 0, 0, 0}; // 0: Left, 1: Center, 2: Right
//                for (String[] row : rows) {
//                    System.out.println("row: " + row);
//                    PrinterUtils.printRow(outputStream, row, columnWidths1, alignments1, false, true);
//                }
//            }

            // PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");

            // Print total item and weight
            int[] colTot = {23,24};
            int[] alignments2 = {0, 0}; // 0: Left, 1: Center, 2: Right
            String[] headers2 = {"Total Linen: " + totalLinen, "Total Berat (Kg): " + beratTot};
            PrinterUtils.printRow(outputStream, headers2, colTot, alignments2, true, false);

            // Print Date and time
            int[] colDateWidth = {23, 23};
            int[] alignments4 = {1, 1}; // 0: Left, 1: Center, 2: Right
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String currentDateAndTime = sdf.format(new Date());
            String[] date = {"",currentDateAndTime};
            PrinterUtils.printRow(outputStream, date, colDateWidth, alignments4, true, false);

            // Signature
            int[] columnWidths3 = {23, 23}; // Adjust as necessary
            int[] alignments5 = {0, 0}; // 0: Left, 1: Center, 2: Right
            String[] footer = {"TTD Ruangan", "TTD Laundry"};
            PrinterUtils.printSeparatorLine(outputStream, separatorWidth);
            PrinterUtils.printRow(outputStream, footer, columnWidths3, alignments5, true, false);
            PrinterUtils.printSeparatorLine(outputStream, separatorWidth);
            outputStream.write(new byte[]{0x1B, 0x64, 0x03});

            PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "=");

            outputStream.write(new byte[]{0x1B, 0x64, 0x01});
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addNewLine() {
        String textToPrint = " \n";

        printData(textToPrint);
    }

    private String formatDouble(double value) {
        return String.format("%.2f", value); // Format with two decimal places
    }

//    private void printRow(OutputStream outputStream, String[] columns, int[] columnWidths, int[] alignments, boolean isHeader, boolean wrapText) throws IOException {
//        StringBuilder rowBuilder = new StringBuilder();
//        if(isHeader) {
//            for (int i = 0; i < columns.length; i++) {
//                rowBuilder.append(padRight(columns[i], columnWidths[i]));
//                if (i < columns.length - 1) {
//                    rowBuilder.append(" "); // Add space between columns
//                }
//            }
//            rowBuilder.append("\n"); // New line at the end of the row
//        }
//        else {
//            for (int i = 0; i < columns.length; i++) {
//                System.out.println("col: " + columnWidths[i]);
//                if (i == 0) {
//                    rowBuilder.append(padRight(columns[i], 47));
//                    rowBuilder.append("\n");
//                    rowBuilder.append("          "); // added space from column 0
//                } else {
//                    rowBuilder.append(padRight(columns[i], columnWidths[i]));
//                }
//
//                if (i < columns.length - 1) {
//                    rowBuilder.append(" "); // Add space between columns
//                }
//            }
//            rowBuilder.append("\n"); // New line at the end of the row
//        }
//        outputStream.write(rowBuilder.toString().getBytes("UTF-8"));
//    }

//    private void printRow2(OutputStream outputStream, String[] columns, int[] columnWidths, int[] alignments, boolean isHeader, boolean wrapText) throws IOException {
//        StringBuilder rowBuilder = new StringBuilder();
//        for (int i = 0; i < columns.length; i++) {
//            if(isHeader) {
//                rowBuilder.append(padRight(columns[i], columnWidths[i]));
//                if (i < columns.length - 1) {
//                    rowBuilder.append(" "); // Add space between columns
//                }
//            } else {
//                System.out.println("txt "+i+": "+columns[i]);
//
//                rowBuilder.append(padRight(columns[i], columnWidths[i]));
//                if (i < columns.length - 1) {
//                    rowBuilder.append(" "); // Add space between columns
//                }
//            }
//
//        }
//        rowBuilder.append("\n"); // New line at the end of the row
//        outputStream.write(rowBuilder.toString().getBytes("UTF-8"));
//    }
//    private void printRow2(OutputStream outputStream, String[] columns, int[] columnWidths, int[] alignments, boolean isHeader) throws IOException {
//        StringBuilder rowBuilder = new StringBuilder();
//        for (int i = 0; i < columns.length; i++) {
//            if(isHeader) {
//                rowBuilder.append(padRight(columns[i], columnWidths[i]));
//                if (i < columns.length - 1) {
//                    rowBuilder.append(" "); // Add space between columns
//                }
//            } else {
//                System.out.println("txt "+i+": "+columns[i]);
//
//                rowBuilder.append(padRight(columns[i], columnWidths[i]));
//                if (i < columns.length - 1) {
//                    rowBuilder.append(" "); // Add space between columns
//                }
//            }
//
//        }
//        rowBuilder.append("\n"); // New line at the end of the row
//        outputStream.write(rowBuilder.toString().getBytes("UTF-8"));
//    }

//    private void printSeparatorLine(OutputStream outputStream, int[] columnWidths) throws IOException {
//        StringBuilder lineBuilder = new StringBuilder();
//        for (int width : columnWidths) {
//            for (int i = 0; i < width; i++) {
//                lineBuilder.append("-");
//            }
//            lineBuilder.append(""); // Add space between columns
//        }
//        lineBuilder.append("\n");
//        outputStream.write(lineBuilder.toString().getBytes("UTF-8"));
//    }


//    private String alignText(String text, int length, int alignment) {
//        if (alignment == 1) { // Center
//            return centerText(text, length);
//        } else if (alignment == 2) { // Right
//            return padLeft(text, length);
//        } else { // Left (default)
//            return padRight(text, length);
//        }
//    }

//    private String centerText(String text, int length) {
//        if (text.length() >= length) {
//            return text.substring(0, length);
//        }
//        int padding = (length - text.length()) / 2;
//        return String.format("%" + padding + "s%s%" + (length - text.length() - padding) + "s", "", text, "");
//    }

    private void printData(String text) {
        try {
            if (outputStream != null) {
                outputStream.write(text.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnectBluetooth() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (bluetoothSocket != null && !bluetoothSocket.isConnected()) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                }
                else {
                    bluetoothSocket.connect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
        System.out.println("Tag2: " + tagList2);
        if(intLayout == 3){
            intLayout = 2;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tagList", tagList2);
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
