package com.android.management_linen.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class DetailsScanBersih implements Parcelable {
    private int batas_cuci, count_barang;

    @SerializedName("berat")
    private Double berat;
    private String sub_category__name, warna__name, ukuran__name, perusahaan__nama, barangruang__ruang__nama, status, result, message;

    public DetailsScanBersih(int batas_cuci, int count_barang, String sub_category__name, String warna__name, String ukuran__name, String perusahaan__nama, String barangruang__ruang__nama, String status, String result, String message, Double berat) {
        this.batas_cuci = batas_cuci;
        this.count_barang = count_barang;
        this.sub_category__name = sub_category__name;
        this.warna__name = warna__name;
        this.ukuran__name = ukuran__name;
        this.perusahaan__nama = perusahaan__nama;
        this.barangruang__ruang__nama = barangruang__ruang__nama;
        this.status = status;
        this.result = result;
        this.message = message;
        this.berat = berat;
    }

    public Double getBerat() {
        return berat;
    }

    public void setBerat(Double berat) {
        this.berat = berat;
    }

    public int getBatascuci() {
        return batas_cuci;
    }

    public void setBatascuci(int batas_cuci) {
        this.batas_cuci = batas_cuci;
    }

    public String getSubCategoryName() {
        return sub_category__name;
    }

    public void setSubCategoryName(String sub_category__name) {
        this.sub_category__name = sub_category__name;
    }

    public String getWarnaName() {
        return warna__name;
    }

    public void setWarnaName(String warna__name) {
        this.warna__name = warna__name;
    }

    public String getUkuranName() {
        return ukuran__name;
    }

    public void setUkuranName(String ukuran__name) {
        this.ukuran__name = ukuran__name;
    }

    public int getCountBarang() {
        return count_barang;
    }

    public void setCountBarang(int count_barang) {
        this.count_barang = count_barang;
    }

    public String getPerusahaanNama() {
        return perusahaan__nama;
    }

    public void setPerusahaanNama(String perusahaan__nama) {
        this.perusahaan__nama = perusahaan__nama;
    }

    public String getBarangruangRuangNama() {
        return barangruang__ruang__nama;
    }

    public void setBarangruangRuangNama(String barangruang__ruang__nama) {
        this.barangruang__ruang__nama = barangruang__ruang__nama;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    protected DetailsScanBersih(Parcel in) {
        batas_cuci = in.readInt();
        count_barang = in.readInt();
        sub_category__name = in.readString();
        warna__name = in.readString();
        ukuran__name = in.readString();
        perusahaan__nama = in.readString();
        barangruang__ruang__nama = in.readString();
        status = in.readString();
        result = in.readString();
        message = in.readString();
        berat = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(batas_cuci);
        dest.writeInt(count_barang);
        dest.writeString(sub_category__name);
        dest.writeString(warna__name);
        dest.writeString(ukuran__name);
        dest.writeString(perusahaan__nama);
        dest.writeString(barangruang__ruang__nama);
        dest.writeString(status);
        dest.writeString(result);
        dest.writeString(message);
        dest.writeDouble(berat);
    }

    public static final Creator<DetailsScanBersih> CREATOR = new Creator<DetailsScanBersih>() {
        @Override
        public DetailsScanBersih createFromParcel(Parcel in) {
            return new DetailsScanBersih(in);
        }

        @Override
        public DetailsScanBersih[] newArray(int size) {
            return new DetailsScanBersih[size];
        }
    };

    @Override
    public String toString() {
        return "DetailsScanBersih{" +
                "batas_cuci=" + batas_cuci +
                ", count_barang=" + count_barang +
                ", sub_category__name='" + sub_category__name + '\'' +
                ", warna__name='" + warna__name + '\'' +
                ", ukuran__name='" + ukuran__name + '\'' +
                ", perusahaan__nama='" + perusahaan__nama + '\'' +
                ", barangruang__ruang__nama='" + barangruang__ruang__nama + '\'' +
                ", status='" + status + '\'' +
                ", result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", berat='"+berat+'\''+
                '}';
    }
}
