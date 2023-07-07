package com.asizdurden.kullanici_giris.Model;

public class Not {
    //private int id;
    private String baslik;
    private String icerik;

    public Not() {
    }

    public Not(String baslik, String icerik) {
        this.baslik = baslik;
        this.icerik = icerik;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }
}
