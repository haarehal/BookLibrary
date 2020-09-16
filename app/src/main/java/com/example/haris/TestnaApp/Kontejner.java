package com.example.haris.TestnaApp;

import android.app.Application;
import android.net.Uri;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Haris on 29-Mar-18.
 */

public class Kontejner extends Application {

    // Kreiramo privatne atribute
    private ArrayList<String> kategorije;
    private ArrayList<Knjiga> knjige;
    private String trenutnaKategorija = "";
    private ArrayList<Autor> autori; // lista je jedinstvena, dakle nece biti duplikata

    private Boolean provjera; // true - izabrana je lista kategorija za prikaz unutar ListView u fragmentu 'ListeFragment'
    // false - izabrana je lista autora za prikaz unutar ListView u fragmentu 'ListeFragment'

    // Konstruktor bez parametara
    public Kontejner() {

        // Postavljamo default-nu vrijednost za atribut 'provjera'
        provjera = true; // izabrana je defaultn-a pocetna lista -> tj. lista kategorija

        // Postavljamo default-ne vrijednosti za listu kategorija
        kategorije = new ArrayList<String>();
        kategorije.add("Horor");
        kategorije.add("Drama");
        kategorije.add("Triler");

        // Postavljamo default-ne vrijednosti za listu knjiga i listu autora
        knjige = new ArrayList<Knjiga>();
        autori = new ArrayList<Autor>();

        // Kategorija: Horor
        knjige.add(new Knjiga("Stephen King", "Shining", "Horor"));
        autori.add(new Autor("Stephen King", 1));
        knjige.add(new Knjiga("Mark Danielewski", "House Of Leaves", "Horor"));
        autori.add(new Autor("Mark Danielewski", 1));
        knjige.add(new Knjiga("Bret Easton Ellis", "American Psycho", "Horor"));
        autori.add(new Autor("Bret Easton Ellis", 1));

        // Kategorija: Drama
        knjige.add(new Knjiga("E.L. James", "Fifty Shades Of Grey", "Drama"));
        autori.add(new Autor("E.L. James", 1));
        knjige.add(new Knjiga("Christina Lauren", "Beautiful Bastard", "Drama"));
        autori.add(new Autor("Christina Lauren", 1));
        knjige.add(new Knjiga("R.K. Lilley", "Mile High", "Drama"));
        autori.add(new Autor("R.K. Lilley", 1));

        // Kategorija: Triler
        knjige.add(new Knjiga("Alan Jacobson", "The 7th Victim", "Triler"));
        autori.add(new Autor("Alan Jacobson", 1));
        knjige.add(new Knjiga("Lori Roy", "Let Me Die in His Footsteps", "Triler"));
        autori.add(new Autor("Lori Roy", 1));
        knjige.add(new Knjiga("Carol O’Connell", "The Judas Child", "Triler"));
        autori.add(new Autor("Carol O’Connell", 1));

    }

    // Geteri i seteri
    public void setKategorije(ArrayList<String> kategorije) {
        this.kategorije = kategorije;
    }

    public ArrayList<String> getKategorije() {
        return kategorije;
    }

    // Omogucavamo dodavanje novih kategorija u fiksnu listu kategorija
    public void DodajKategoriju(String kategorija) {
        kategorije.add(kategorija); // Bez nule ce se elementi u listu kategorija unositi na dno te liste
    }

    // Geteri i seteri
    public void setKnjige(ArrayList<Knjiga> knjige) {
        this.knjige = knjige;
    }

    public ArrayList<Knjiga> getKnjige() {
        return knjige;
    }

    // Omogucavamo dodavanje novih knjiga u fiksnu listu knjiga
    public void DodajKnjigu(Knjiga knjiga) {
        knjige.add(knjiga);
    }

    // Geteri i seteri
    public void setTrenutnaKategorija(String trenutnaKategorija) {
        this.trenutnaKategorija = trenutnaKategorija;
    }

    public String getTrenutnaKategorija() {
        return trenutnaKategorija;
    }

    // Pravimo metodu za postavljanje atributra knjige 'kliknutoNaDugme' na 'true', ukoliko se kliknulo na knjigu 'trenutnaKnjiga'
    public void postaviKlik(String trenutnaKnjiga) {
        for (int position = 0; position < knjige.size(); position++) {
            Knjiga k = knjige.get(position);
            if (k.getNaziv().toLowerCase().equals(trenutnaKnjiga.toLowerCase())) {
                knjige.get(position).setKliknutoNaKnjigu(true);
            }
        }
    }

    public void postaviUri(Uri uri) {
        int position = knjige.size() - 1;
        knjige.get(position).setUriSlike(uri);
    }

    // Geteri i seteri
    public void setAutori(ArrayList<Autor> autori) {
        this.autori = autori;
    }

    public ArrayList<Autor> getAutori() {
        return autori;
    }

    // Omogucavamo dodavanje novih autora u listu autora
    public void DodajAutora(Autor autor) {
        autori.add(autor);
    }

    // Geteri i seteri
    public void setProvjera(Boolean provjera) {
        this.provjera = provjera;
    }

    public Boolean getProvjera() {
        return provjera;
    }

    // Metode za povecavanje broja knjiga u listi knjiga
    public void PristupiUvecanjuKnjiga(String ia) { // Na osnovu imena i prezimena
        for (int position = 0; position < autori.size(); position++) {
            if (autori.get(position).getImeiPrezime().toLowerCase().equals(ia.toLowerCase())) {
                autori.get(position).UvecajBrojKnjiga();
                break;
            }
        }
    }

    public void PristupiUvecanjuKnjiga(int position) { // Na osnovu pozicije u listi
        autori.get(position).UvecajBrojKnjiga();
    }

    public void PostaviKategorijuZadnjeUpisaneKnjige(String kategorija) {
        int position = knjige.size() - 1;
        knjige.get(position).setKategorija(kategorija);
    }
}