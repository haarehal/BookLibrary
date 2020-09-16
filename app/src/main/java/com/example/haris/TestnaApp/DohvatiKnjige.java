package com.example.haris.TestnaApp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Haris on 13-May-18.
 */

// Kreiramo podklasu AsyncTask klase koju cemo koristiti za izdvajanje akcije ucitavanja podataka sa interneta u posebnu nit,
// cime osiguravamo da dok se podaci sa interneta ucitavaju, korisnik moze nesmetano koristiti aplikaciju
public class DohvatiKnjige extends AsyncTask<String, Integer, Void> {

    private ArrayList<Knjiga> rez = new ArrayList<Knjiga>();
    private IDohvatiKnjigeDone pozivatelj;

    // Dodajemo konstruktor koji ce primiti referencu na fragment koji ga poziva
    public DohvatiKnjige(IDohvatiKnjigeDone p) { pozivatelj = p; };

    // Implementiramo metodu doInBackground koja sadrzi kod koji ce se izvrsavati u novoj niti
    @Override
    protected Void doInBackground(String... params) {
        // Kako cemo navedeni parametar proslijediti u url-u poziva web servisa, potrebno je izvrsiti zamjenu svih znakova
        // koji se ne mogu nalaziti u url-u (razmak, upitnik i sl.)
        String query = null;
        try {
            query = URLEncoder.encode(params[0], "utf-8"); // Preuzimamo query
            // Parametar kojeg nasa nit prima je string po kojem se vrsi pretraga i preuzimamo ga sa params[0]
        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // Formiramo ispravan url
        String url1 = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + query + "&maxResults=5";
        try {
            URL url = new URL(url1);
            // Pozivamo web servis koristeci formirani URL
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            // connection.setRequestProperty("Authorization", "Bearer "+token​);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            // Rezultat poziva web servisa je u obliku InputStream-a i potrebno je da ga pretvorimo u String
            String rezultat = convertStreamToString(in); // Rezultat je u JSON formatu
            // Da bismo radili sa podacima koji su u JSON formatu, koristimo JSONObject klasu
            JSONObject jo = new JSONObject(rezultat);
            // Iz JSON objekta mozemo izdvojiti dijelove koji su nam potrebni
            JSONArray items = null;
            if(jo.has("items")) {
                items = jo.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {

                    // Prolazimo kroz niz knjiga 'items' i za svaku knjigu kupimo podatke koji su potrebni za registraciju te knjige (**NOVI PODACI ZA NOVI KONSTRUKTOR**)
                    JSONObject book = items.getJSONObject(i);
                    String id = null;
                    if (book.has("id")) {
                        id = book.getString("id");
                    }
                    JSONObject volumeInfo = null;
                    if (book.has("volumeInfo")) {
                        volumeInfo = book.getJSONObject("volumeInfo");
                    }
                    String title = null;
                    if (volumeInfo.has("title")) {
                        title = volumeInfo.getString("title");
                    }
                    JSONArray authors = null;
                    ArrayList<Autor> autori = new ArrayList<Autor>();
                    if (volumeInfo.has("authors")) {
                        authors = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authors.length(); j++) {
                            String ime = authors.getString(j);
                            Autor a = new Autor(ime, id);
                            autori.add(a);
                        }
                    }
                    String description = null;
                    if (volumeInfo.has("description")) {
                        description = volumeInfo.getString("description");
                    }
                    String publishedDate = null;
                    if (volumeInfo.has("publishedDate")) {
                        publishedDate = volumeInfo.getString("publishedDate");
                    }
                    JSONObject imageLinks = null;
                    URL urlSlika = null;
                    if (volumeInfo.has("imageLinks")) {
                        imageLinks = volumeInfo.getJSONObject("imageLinks");
                        String thumbnail = null;
                        if (imageLinks.has("thumbnail")) {
                            thumbnail = imageLinks.getString("thumbnail");
                            urlSlika = new URL(thumbnail);
                        }
                    }
                    String pageCount = null;
                    int brojStranica = 0;
                    if (volumeInfo.has("pageCount")) {
                        pageCount = volumeInfo.getString("pageCount");
                        brojStranica = Integer.parseInt(pageCount);
                    }

                    // Kreiramo objekat tipa Knjiga na isti nacin kao i kada smo hardkodirali podatke, samo sto sada radimo sa STVARNIM PODACIMA!!!
                    Knjiga k = new Knjiga(id, title, autori, description, publishedDate, urlSlika, brojStranica);
                    rez.add(k);

                    // Provjera ispravnosti
                    //rez.add(new Knjiga("123","hehe",new ArrayList<Autor>(),"bla bla bla","12.5.1998",new URL("abc123"),320));
                    //Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Implementacija metode koja pretvara InputStream u String
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    // Kreiramo interfejs putem kojeg cemo proslijediti podatke (knjige) u fragment 'FragmentOnline' (koji ce implementirati ovaj interfejs)
    public interface IDohvatiKnjigeDone {
        public void onDohvatiDone(ArrayList<Knjiga> rez);
    }

    // Dodajemo metodu 'onPostExecute' koja ce se pozvati nakon zavrsetka task-a
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pozivatelj.onDohvatiDone(rez);
    }
}
