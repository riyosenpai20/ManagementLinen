package com.android.management_linen.helpers;

public class GroupedScan {
    private String subCategoryName;
    private String warnaName;
    private String ukuranName;
    private String ruangName; // opsional
    private int batasCuci;    // opsional
    private int jumlah;
    private double totalBerat;

    public GroupedScan(String subCategoryName, String warnaName, String ukuranName, String ruangName, int batasCuci) {
        this.subCategoryName = subCategoryName;
        this.warnaName = warnaName;
        this.ukuranName = ukuranName;
        this.ruangName = ruangName;
        this.batasCuci = batasCuci;
        this.jumlah = 0;
        this.totalBerat = 0.0;
    }

    public String getSubCategoryName() { return subCategoryName; }
    public String getWarnaName() { return warnaName; }
    public String getUkuranName() { return ukuranName; }
    public String getRuangName() { return ruangName; }
    public int getBatasCuci() { return batasCuci; }
    public int getJumlah() { return jumlah; }
    public double getTotalBerat() { return totalBerat; }

    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public void setTotalBerat(double totalBerat) { this.totalBerat = totalBerat; }
}

