package com.example.haris.TestnaApp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Haris on 20-Mar-18.
 */

public class Knjiga implements Parcelable{

    // NOVI PODACI ZA KNJIGU (SA SPIRALE)
    String id, naziv, opis, datumObjavljivanja;
    ArrayList<Autor> autori = new ArrayList<Autor>();
    URL slika;
    int brojStranica;

    // MOJI (STARI) PODACI ZA KNJIGU
    String imeIPrezimeAutora, kategorija;
    boolean kliknutoNaKnjigu;
    Uri uriSlike;

    // Konstruktor
    // MOJ (STARI) KONSTRUKTOR
    public Knjiga(String ip, String n, String k){
        imeIPrezimeAutora = ip;
        naziv = n;
        kategorija = k;
        kliknutoNaKnjigu = false;
        uriSlike = null;

        // Ostale (stare) atribute postavljamo na null jer ne znamo njihove pocetne vrijednosti
        id = null;
        opis = null;
        datumObjavljivanja = null;
        slika = null;
        brojStranica = 0;
    }
    // NOVI KONSTRUKTOR ZA NOVE PODATKE (SA SPIRALE)
    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, URL slika, int brojStranica) {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.slika = slika;
        this.brojStranica = brojStranica;

        // Ostale (stare) atribute postavljamo na null jer ne znamo njihove pocetne vrijednosti
        imeIPrezimeAutora = null;
        kategorija = null;
        kliknutoNaKnjigu = false;
        uriSlike = null;
    }

    // Geteri i seteri
    public void setImeIPrezimeAutora(String imeIPrezimeAutora) {
        this.imeIPrezimeAutora = imeIPrezimeAutora;
    }
    public String getImeIPrezimeAutora() {
        return imeIPrezimeAutora;
    }
    public void setNaziv(String nazivKnjige) {
        this.naziv = nazivKnjige;
    }
    public String getNaziv() {
        return naziv;
    }
    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }
    public String getKategorija() {
        return kategorija;
    }
    public void setKliknutoNaKnjigu(boolean kliknutoNaKnjigu) {
        this.kliknutoNaKnjigu = kliknutoNaKnjigu;
    }
    public boolean getKliknutoNaKnjigu(){
        return kliknutoNaKnjigu;
    }
    public void setUriSlike(Uri uriSlike) {
        this.uriSlike = uriSlike;
    }
    public Uri getUriSlike() {
        return uriSlike;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setOpis(String opis) {
        this.opis = opis;
    }
    public String getOpis() {
        return opis;
    }
    public void setDatumObjavljivanja(String datumObjavljivanja) {
        this.datumObjavljivanja = datumObjavljivanja;
    }
    public String getDatumObjavljivanja() {
        return datumObjavljivanja;
    }
    public void setAutori(ArrayList<Autor> autori) {
        this.autori = autori;
    }
    public ArrayList<Autor> getAutori() {
        return autori;
    }
    public void setSlika(URL slika) {
        this.slika = slika;
    }
    public URL getSlika() {
        return slika;
    }
    public void setBrojStranica(int brojStranica) {
        this.brojStranica = brojStranica;
    }
    public int getBrojStranica() {
        return brojStranica;
    }

    // IMPLEMENTIRAMO PARCELABLE INTERFEJS
    protected Knjiga(Parcel in) {
        id = in.readString();
        naziv = in.readString();
        opis = in.readString();
        datumObjavljivanja = in.readString();
        autori = (ArrayList<Autor>)in.readSerializable();
        slika = (URL)in.readSerializable();
        brojStranica = in.readInt();
        imeIPrezimeAutora = in.readString();
        kategorija = in.readString();
        kliknutoNaKnjigu = (Boolean)in.readSerializable();
        //uriSlike = (Uri)in.readSerializable(); ????????????????????????????????????????????????????????
    }
    public static final Creator<Knjiga> CREATOR = new Creator<Knjiga>() {
        @Override
        public Knjiga createFromParcel(Parcel in) {
            return new Knjiga(in);
        }

        @Override
        public Knjiga[] newArray(int size) {
            return new Knjiga[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(naziv);
        dest.writeString(opis);
        dest.writeString(datumObjavljivanja);
        dest.writeSerializable(autori);
        dest.writeSerializable(slika);
        dest.writeInt(brojStranica);
        dest.writeString(imeIPrezimeAutora);
        dest.writeString(kategorija);
        dest.writeSerializable(kliknutoNaKnjigu);
        //dest.writeSerializable(uriSlike); ???????????????????????????????????????????????????????????????
    }
}
