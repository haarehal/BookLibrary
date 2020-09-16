package com.example.haris.TestnaApp;

import java.util.ArrayList;

/**
 * Created by Haris on 04-Jun-18.
 */

public class Kontakt {

    private String id;
    private String imeIPrezime;
    private ArrayList<String> emailovi;

    public Kontakt() {
        id = null;
        imeIPrezime = null;
        emailovi = new ArrayList<String>();
    }

    public Kontakt(String id, String imeIPrezime, ArrayList<String> emailovi) {
        this.id = id;
        this.imeIPrezime = imeIPrezime;
        this.emailovi = emailovi;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImeIPrezime(String imeIPrezime) {
        this.imeIPrezime = imeIPrezime;
    }

    public String getImeIPrezime() {
        return imeIPrezime;
    }

    public void setEmailovi(ArrayList<String> emailovi) {
        this.emailovi = emailovi;
    }

    public ArrayList<String> getEmailovi() {
        return emailovi;
    }
}
