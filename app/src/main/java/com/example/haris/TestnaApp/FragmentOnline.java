package com.example.haris.TestnaApp;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Haris on 12-May-18.
 */

public class FragmentOnline extends Fragment implements DohvatiKnjige.IDohvatiKnjigeDone, DohvatiNajnovije.IDohvatiNajnovijeDone,
                                                        MojResultReceiver.Receiver {

    private FragmentOnline.OnClickPovratak ocp;
    private ArrayList<Knjiga> knjigeRezultat = new ArrayList<Knjiga>();
    private Spinner spinnerKnjige;
    private ArrayList<String> naziviKnjiga = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Ovdje se dodjeljuje layout fragmentu, tj. sta ce se nalaziti unutar fragmenta
        View iv = inflater.inflate(R.layout.fragment_online, container, false);

        // Dohvacamo referencu na objekat
        Button dugmePovratak = (Button)iv.findViewById(R.id.dPovratak);
        final Spinner spinnerKategorije = (Spinner)iv.findViewById(R.id.sKategorije);
        spinnerKnjige = (Spinner)iv.findViewById(R.id.sRezultat);
        Button dugmePretraga = (Button)iv.findViewById(R.id.dRun);
        final EditText unos = (EditText)iv.findViewById(R.id.tekstUpit);
        Button dugmeDodajKnjigu = (Button)iv.findViewById(R.id.dAdd);

        // Kreiramo listu kategorija i dodjeljujemo mu listu kategorija iz kontejnera
        final ArrayList<String> kategorije;
        kategorije = ((Kontejner)getActivity().getApplication()).getKategorije();

        // Kreiramo adapter i povezujemo ga sa spinnerom
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategorije);
        spinnerKategorije.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Pravimo da se klikom na dugme 'PRETRAGA' izvrsi pretrazivanje knjige po unesenom nazivu putem web servisa
        dugmePretraga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tekst = unos.getText().toString();
                // Prolazimo kroz uneseni tekst (string) i provjeravamo da li je unesena jedna rijec (naziv jedne knjige), vise rijeci (nazivi vise knjiga),
                // autor (sa imenom) ili korisnik (sa ID-em)
                boolean jednaRijecUnos = false, viseRijeciUnos = false, autorUnos = false, korisnikUnos = false;
                int ind = 0; // Indeks za izdvajanje podstringa

                // Za sada necemo predvidjeti situacije ako je unos neispravan!
                // Uneseno vise rijeci (nazivi knjiga)
                if(tekst.contains(";")) {
                    viseRijeciUnos = true;
                    String nazivKnjige;
                    for(int i=0; i<tekst.length(); i++) {
                        if(tekst.charAt(i) == ';') {
                            // Izdvajamo sve unesene rijeci i vrsimo poziv task-a za svaku rijec
                            // Na ovaj nacin, petljom cemo izdvojiti samo one rijeci do posljednjeg znaka ";", te moramo naknadno izdvojiti posljednju rijec,
                            // tj. rijec iza zadnjeg znaka ";"
                            nazivKnjige = tekst.substring(ind, i);
                            new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone)FragmentOnline.this).execute(nazivKnjige);
                            ind = i + 1;
                        }
                    }

                    // Oostalo je jos da izvrsimo task za posljednju rijec iz stringa nakon znaka ";"
                    nazivKnjige = tekst.substring(ind, tekst.length());
                    new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone)FragmentOnline.this).execute(nazivKnjige);
                }
                // Unesen autor ili korisnik
                else if(tekst.contains(":")) {
                    ind = tekst.indexOf(':');
                    String provjera = tekst.substring(0,ind);
                    // Unesen autor
                    if(provjera.toLowerCase().equals("autor")) {
                        autorUnos = true;
                        String imeIPrezimeAutora = tekst.substring(ind+1,tekst.length());
                        new DohvatiNajnovije((DohvatiNajnovije.IDohvatiNajnovijeDone)FragmentOnline.this).execute(imeIPrezimeAutora);
                    }
                    // Unesen korisnik
                    else if (provjera.toLowerCase().equals("korisnik")) {
                        korisnikUnos = true;
                        String idKorisnika = tekst.substring(ind+1,tekst.length());

                        // Pozivanje IntentService-a radimo preko intent mehanizma
                        // Kreiramo eksplicitni intent koji poziva nas IntentService
                        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), KnjigePoznanika.class);
                        // Jedan od mnogih nacina kako mozemo da vratimo podatke iz intenta je koristeci klasu ResultReceiver
                        // Ova klasa je zapravo omotac oko klase Binder, a Binder je mehanizam za komunikaciju izmedju procesa
                        MojResultReceiver mReceiver = new MojResultReceiver(new Handler());
                        mReceiver.setReceiver((MojResultReceiver.Receiver)FragmentOnline.this);
                        // Sada prosljedjujemo dodatne podatke putem putExtra metode
                        intent.putExtra("idKorisnika", idKorisnika);
                        intent.putExtra("receiver", mReceiver);
                        // Pozivamo intent
                        getActivity().startService(intent);
                    }

                }
                // Unesena jedna rijec (naziv knjige)
                else {
                    jednaRijecUnos = true;
                    // S obzirom da 'tekst' predstavlja samo jednu rijec, pozivamo task nad tom rijeci
                    String nazivKnjige = tekst;
                    new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone)FragmentOnline.this).execute(nazivKnjige);
                }

                /*
                // Provjera ispravnosti unosa
                if(viseRijeciUnos) Toast.makeText(getActivity().getApplicationContext(), "UNESENO VISE RIJECI..", Toast.LENGTH_LONG).show();
                else if(korisnikUnos) Toast.makeText(getActivity().getApplicationContext(), "UNESEN KORISNIK..", Toast.LENGTH_LONG).show();
                else if(autorUnos) Toast.makeText(getActivity().getApplicationContext(), "UNESEN AUTOR..", Toast.LENGTH_LONG).show();
                else Toast.makeText(getActivity().getApplicationContext(), "UNESENA JEDNA RIJEC..", Toast.LENGTH_LONG).show();
                */

                // Brisemo unos
                unos.setText("");
            }
        });

        dugmeDodajKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Dohvacamo vrijednosti iz spinner-a
                String nazivKnjige = spinnerKnjige.getSelectedItem().toString();

                    String kategorija = spinnerKategorije.getSelectedItem().toString();
                    // Pronalazimo knjigu sa odabranim nazivom i upisujemo je u odabranu kategoriju
                    for (int i = 0; i < knjigeRezultat.size(); i++) {
                        Knjiga k = knjigeRezultat.get(i);
                        if (k.getNaziv().toLowerCase().equals(nazivKnjige.toLowerCase())) {
                            // Upisujemo knjigu
                            ((Kontejner)getActivity().getApplication()).DodajKnjigu(k);
                            // Dodajemo kategoriju za odabranu knjigu (jer je postavljena na null sa novim konstruktorom sa spirale!)
                            ((Kontejner) getActivity().getApplication()).PostaviKategorijuZadnjeUpisaneKnjige(kategorija);


                            // Dodajemo autora u listu autora
                            ArrayList<Autor> registrovaniAutori = ((Kontejner) getActivity().getApplication()).getAutori();
                            ArrayList<Autor> autori = k.getAutori();
                            // Prvo provjeravamo da li se navedeni autor vec nalazi u listi
                            for (int position = 0; position < autori.size(); position++) {
                                int pozicijaPronadjenog = 0;
                                Boolean test = false;
                                Autor a = autori.get(position);
                                for (int position2 = 0; position2 < registrovaniAutori.size(); position2++) {
                                    Autor ra = registrovaniAutori.get(position2);
                                    if (ra.getImeiPrezime().toLowerCase().equals(a.getImeiPrezime().toLowerCase())) {
                                        test = true;
                                        pozicijaPronadjenog = position2;
                                        break;
                                    }
                                }
                                // Ukoliko postoji, povecavamo mu broj knjiga za 1
                                if (test) {
                                    ((Kontejner) getActivity().getApplication()).PristupiUvecanjuKnjiga(pozicijaPronadjenog);
                                }
                                // Ukoliko ne postoji, upisujemo ga u listu autora
                                else {
                                    Autor autor = new Autor(a.getImeiPrezime(), k.getId());
                                    ((Kontejner) getActivity().getApplication()).DodajAutora(autor);
                                    // Postavljamo broj knjiga na 1 (jer je postavljeno na 0 sa novim konstruktorom sa spirale!)
                                    int zadnjaPozicija = ((Kontejner) getActivity().getApplication()).getAutori().size() - 1;
                                    ((Kontejner) getActivity().getApplication()).PristupiUvecanjuKnjiga(zadnjaPozicija);
                                }
                            }

                            break;
                        }
                    }

                    // Obavjestavamo iskocnom porukom da je unos uspjesno zavrsen
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.knjiga_upisana), Toast.LENGTH_LONG).show();
                
            }
        });

        // Prosljedjujemo event onClickPovratak u roditeljsku aktivnost (KategorijeAkt)
        try {
            // Dohvacamo referencu na roditeljsku aktivnost,
            // a kako ona implementira interfejs OnClick, moguce ju je cast-ati u taj interfejs
            ocp = (FragmentOnline.OnClickPovratak)getActivity();
        }
        catch(ClassCastException e) {
            // U slucaju da se u roditeljskoj aktivnosti nije implementirao interfejs, baca se izuzetak
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnClick !");
        }
        // Ukoliko je aktivnost uspjesno cast-ana u interfejs, tada joj prosljedjujemo event
        dugmePovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocp.onClickedPovratak();
            }
        });

        return iv;
    }

    // Pravimo da se klikom na dugme POVRATAK vratimo na pocetni fragment 'ListeFragment'
    // Dodajemo novi interfejs kojeg ce roditeljska aktivnost (KategorijeAkt) implementirati
    // Ovaj interfejs ce se koristiti poput obicnog onClickListener-a, ali sada mi unutar fragmenta iniciramo event po potrebi
    // Na ovaj nacin je funkcionalnost fragmenta enkapsulirana i ovakav fragment se moze iskoristiti u vise aktivnosti
    public interface OnClickPovratak {
        public void onClickedPovratak();
    }

    // Metoda koju ce pozivati interfejsi iz AsyncTask klasa 'DohvatiKnjige' i 'DohvatiNajnovije' i IntentService klase 'KnjigePoznanika'
    private void UpisiKnjigeUSpinner(ArrayList<Knjiga> rez) {
        // Pravimo funkcionalnost za prikaz dohvacenih knjiga u spinner 'sRezultat'
        for(int i=0; i<rez.size(); i++) {
            Knjiga k = rez.get(i);
            naziviKnjiga.add(k.getNaziv());
        }

        knjigeRezultat = rez;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, naziviKnjiga);
        spinnerKnjige.setAdapter(adapter);
    }

    // Implementiramo metodu interfejsa iz AsyncTask klase 'DohvatiKnjige'
    @Override
    public void onDohvatiDone(ArrayList<Knjiga> rez) {
        UpisiKnjigeUSpinner(rez);
    }

    // Implementiramo metodu interfejsa iz AsyncTask klase 'DohvatiNajnovije'
    @Override
    public void onNajnovijeDone(ArrayList<Knjiga> rez) {
        UpisiKnjigeUSpinner(rez);
    }

    // Implementiramo metodu interfejsa 'onReceiveResult' iz klase 'MojResultReceiver'
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch(resultCode) {
            case 0: // KnjigePoznanika.STATUS_START
                // Ovdje ide kod koji obavjestava korisnika da je poziv upucen
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.obrada_podataka_je_zapoceta), Toast.LENGTH_LONG).show();
                break;

            case 1: // KnjigePoznanika.STATUS_FINISH
                // Dohvatanje rezultata i update UI
                ArrayList<Knjiga> rez  = resultData.getParcelableArrayList("listaKnjiga");
                UpisiKnjigeUSpinner(rez);
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.knjige_uspjesno_dohvacene), Toast.LENGTH_LONG).show();
                break;

            case 2: // KnjigePoznanika.STATUS_ERROR
                // Slucaj kada je doslo do greske
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
