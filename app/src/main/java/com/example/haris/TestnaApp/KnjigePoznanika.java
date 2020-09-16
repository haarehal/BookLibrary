package com.example.haris.TestnaApp;

import android.app.IntentService;
import android.content.Intent;

import android.os.Bundle;
import android.os.ResultReceiver;

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
 * Created by Haris on 16-May-18.
 */

public class KnjigePoznanika extends IntentService {

    public int STATUS_START = 0;
    public int STATUS_FINISH = 1;
    public int STATUS_ERROR = 2;

    private ArrayList<Knjiga> rez = new ArrayList<Knjiga>();

    // Konstruktor
    public KnjigePoznanika() {
        super(null);
    }
    public KnjigePoznanika(String name) {
        super(name);
        // ...
    }

    // Implementiramo metodu 'onCreate' pomocu koje postavljamo pocetne uslove za izvrsavanje servisa
    @Override
    public void onCreate() {
        super.onCreate();
        // Akcije koje se trebaju obaviti pri kreiranju servisa
    }

    // Implementiramo metodu 'onHandleIntent' koja treba da sadrzi posao koji je vremenski zahtjevan, odnosno vecinu procesiranja koje servis radi
    // Ova metoda ce se izvrsavati u posebnoj niti, a citav posao oko kreiranja, sinhronizovanja i unistavanja te niti obavlja IntentService umjesto nas
    @Override
    protected void onHandleIntent(Intent intent) {
        // Potrebno je da pokupimo sve proslijedjene podatke putem intenta
        final String parametar = intent.getStringExtra("idKorisnika");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();

        // Kada zapocnemo obradu intenta, mozemo poslati obavijest pozivatelju
        /* Update UI: pocetak task-a */
        receiver.send(STATUS_START, Bundle.EMPTY);

        // Ovdje izvrsavamo iste akcije za obradu podataka sa interneta kao u AsyncTask klasi
        String query = null;
        try {
            query = URLEncoder.encode(parametar, "utf-8"); // Preuzimamo query
            // Parametar kojeg nasa nit prima je string po kojem se vrsi pretraga i preuzimamo ga putem intenta sa kljucem "idKorisnika"
        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // Formiramo ispravan url
        String url1 = "https://www.googleapis.com/books/v1/users/" + query + "/bookshelves";
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
            if(jo.has("items")) { items = jo.getJSONArray("items"); }
            for(int i=0; i<items.length(); i++) {
                JSONObject bookShelf = items.getJSONObject(i);
                String access = null;
                if(bookShelf.has("access")) {
                    access = bookShelf.getString("access");
                    if(access.toLowerCase().equals("public")) {
                        String selfLink = null;
                        if(bookShelf.has("selfLink")) {
                            selfLink = bookShelf.getString("selfLink");

                            // Formiramo ispravan url
                            String link = selfLink + "/volumes";
                            URL url2 = new URL(link);
                            // Pozivamo web servis koristeci formirani URL
                            HttpURLConnection urlConnection2 = (HttpURLConnection)url2.openConnection();
                            // connection.setRequestProperty("Authorization", "Bearer "+token​);
                            InputStream in2 = new BufferedInputStream(urlConnection2.getInputStream());
                            // Rezultat poziva web servisa je u obliku InputStream-a i potrebno je da ga pretvorimo u String
                            String rezultat2 = convertStreamToString(in2); // Rezultat je u JSON formatu
                            // Da bismo radili sa podacima koji su u JSON formatu, koristimo JSONObject klasu
                            JSONObject jo2 = new JSONObject(rezultat2);
                            // Iz JSON objekta mozemo izdvojiti dijelove koji su nam potrebni
                            JSONArray items2 = null;
                            if(jo2.has("items")) {
                                items2 = jo2.getJSONArray("items");
                                for (int j = 0; j < items2.length(); j++) {

                                    // Prolazimo kroz niz knjiga 'items' i za svaku knjigu kupimo podatke koji su potrebni za registraciju te knjige (**NOVI PODACI ZA NOVI KONSTRUKTOR**)
                                    JSONObject book = items2.getJSONObject(j);
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
                                        for (int k = 0; k < authors.length(); k++) {
                                            String ime = authors.getString(k);
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
                    }
                }
            }



            /*
            // Provjera ispravnosti IntentService-a
            rez.add(new Knjiga("123","hehe",new ArrayList<Autor>(),"bla bla bla","12.5.1998",320));
            */
        }
        // Ukoliko je doslo do greske, vracamo obavijest da je doslo do izuzetka
        catch(MalformedURLException e) {
            //bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
            //e.printStackTrace();
        }
        catch(IOException e) {
            //bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
            //e.printStackTrace();
        }
        catch(JSONException e) {
            //bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
            //e.printStackTrace();
        }

        // Kada zavrsimo sa obradom, prosljedjujemo rezultate nazad u pozivatelja
        bundle.putParcelableArrayList("listaKnjiga", rez);
        receiver.send(STATUS_FINISH, bundle);
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
}
