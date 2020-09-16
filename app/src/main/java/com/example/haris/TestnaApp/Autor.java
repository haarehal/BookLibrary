package com.example.haris.TestnaApp;

import java.util.ArrayList;

/**
 * Created by Haris on 12-Apr-18.
 */

public class Autor {

    // MOJI (STARI) PODACI ZA AUTORA
    String imeiPrezime;
    int brojKnjiga;

    // NOVI PODACI ZA AUTORA (SA SPIRALE)
    ArrayList<String> knjige = new ArrayList<>(); // Sadrzi id-eve knjiga

    // Konstruktor
    // MOJ (STARI) KONSTRUKTOR
    // Konstruktor sa jednim parametrom
    public Autor(String i) {
        imeiPrezime = i;
        brojKnjiga = 0;
    }

    // Konstruktor sa dva parametra
    public Autor(String i, int bk) {
        imeiPrezime = i;
        brojKnjiga = bk;
    }

    // NOVI KONSTRUKTOR (SA SPIRALE)
    public Autor(String imeiPrezime, String id) {
        this.imeiPrezime = imeiPrezime;

        // Ostale (stare) atribute postavljamo na null jer ne znamo njihove pocetne vrijednosti
        brojKnjiga = 0;

        dodajKnjigu(id);
    }

    // Geteri i seteri

    public void setImeiPrezime(String imeiPrezime) {
        this.imeiPrezime = imeiPrezime;
    }

    public String getImeiPrezime() {
        return imeiPrezime;
    }

    public void setBrojKnjiga(int brojKnjiga) {
        this.brojKnjiga = brojKnjiga;
    }

    public int getBrojKnjiga() {
        return brojKnjiga;
    }

    // Pravimo metodu za povecavanje atributa brojKnjiga za pojedinacnog autora
    public void UvecajBrojKnjiga() {
        brojKnjiga = brojKnjiga + 1;
    }

    public void setKnjige(ArrayList<String> knjige) {
        this.knjige = knjige;
    }

    public ArrayList<String> getKnjige() {
        return knjige;
    }

    public void dodajKnjigu(String id) {
        boolean a = false;
        for (int i = 0; i < knjige.size(); i++) {
            if (knjige.get(i).equals(id)) {
                a = true;
                break;
            }
        }
        if (a == false) {
            knjige.add(id);
        }
    }
}
