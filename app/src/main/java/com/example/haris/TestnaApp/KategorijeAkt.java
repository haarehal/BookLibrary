package com.example.haris.TestnaApp;

import android.app.Activity;
 import android.app.Fragment;
 import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.view.View.GONE;

// import android.support.v4.app.Fragment;
// import android.support.v4.app.FragmentManager;

public class KategorijeAkt extends Activity implements ListeFragment.OnItemClick, KnjigeFragment.OnClickPovratak, ListeFragment.OnClickDodajKnjigu,
                                                       DodavanjeKnjigeFragment.OnClickPovratak, ListeFragment.OnClickDodajKnjiguOnline,
                                                       FragmentOnline.OnClickPovratak, AdapterKnjiga.OnClickPreporuciKnjigu {

    // siriL varijablu koristimo da bismo znali o kojem layout-u se radi
    // true - radi se o sirem layoutu
    // false - radi se o pocetnom layout-u
    private Boolean siriL = false;

    // Implementiramo parametar za dinamicko postavljanje tezina FrameLayout-a unutar koda
    private LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            1.0f
    );
    private LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            2.0f
    );

    // Dohvacamo reference na FrameLayout-e
    private FrameLayout fragment1;
    private FrameLayout fragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategorije_akt);

        fragment1 = (FrameLayout)findViewById(R.id.F1);
        fragment2 = (FrameLayout)findViewById(R.id.F2);

        // FRAGMENTI - 2. spirala

        // Dohvatanje FragmentManager-a
        FragmentManager fm = getFragmentManager();
        FrameLayout fragmentF2 = (FrameLayout)findViewById(R.id.F2);

        // Slucaj layout-a za siroke ekrane
        if(fragmentF2 != null) {
            siriL = true;
            KnjigeFragment fk;
            fk = (KnjigeFragment)fm.findFragmentById(R.id.F2);
            // Provjeravamo da li je fragment detalji vec kreiran
            if(fk == null) {
                fk = new KnjigeFragment();
                fm.beginTransaction().replace(R.id.F2, fk).commit();
            }
        }

        // Dodjeljivanje fragmenta ListeFragment
        ListeFragment fl = (ListeFragment)fm.findFragmentByTag("Lista");
        // Provjeravamo da li je vec kreiran navedeni fragment
        if(fl == null) {
            // Ukoliko nije, kreiramo
            fl = new ListeFragment();

            // Ovdje (prije poziva fragment transakcije) vrsimo prosljedjivanje potrebnih podataka u fragment
            // Dodjeljujemo argumente fragmentu i u njima prosljedjujemo te podatke

            fm.beginTransaction().replace(R.id.F1, fl, "Lista").commit();
        }
        else {
            // Slucaj kada mijenjamo orijentaciju uredjaja iz portrait(uspravno) u landscape(vodoravno)
            // a u aktivnosti je bio otvoren fragment KnjigeFragment,
            // tada je potrebno skinuti KnjigeFragment sa steka kako ne bi bio dodan na mjesto fragmenta ListeFragment
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    // Implementiramo metodu interfejsa (onItemClicked)
    @Override
    public void onItemClicked(int pos) {

        Boolean provjeraa = ((Kontejner)getApplication()).getProvjera();

        // Ukoliko je 'provjera' true, fragment KnjigeFragment treba da prikaze listu knjiga iz selektovane kategorije
        if(provjeraa) {

            // Provjeravamo da li se kliknuta kategorija moze otvoriti,
            // odnosno da li postoji barem jedna knjiga iz te kategorije
            ArrayList<String> kategorije = ((Kontejner) getApplication()).getKategorije();
            boolean a = false;
            ArrayList<Knjiga> test = ((Kontejner) getApplication()).getKnjige();
            for (int i = 0; i < test.size(); i++) {
                Knjiga k = test.get(i);
                if (k.kategorija.toLowerCase().equals(kategorije.get(pos).toLowerCase())) {
                    a = true;
                    break;
                }
            }
            if (a == false) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nema_upisanih_knjiga), Toast.LENGTH_LONG).show();
            } else {
                // Priprema novog fragmenta KnjigeFragment
                Bundle arguments = new Bundle();
                arguments.putString("kategorija", ((Kontejner) getApplication()).getKategorije().get(pos));
                KnjigeFragment fk = new KnjigeFragment();
                fk.setArguments(arguments);
                if (siriL) {
                    // Slucaj za ekrane sa sirom dijagonalom
                    getFragmentManager().beginTransaction().replace(R.id.F2, fk).commit();
                } else {
                    // Slucaj za ekrane sa pocetnom zadanom sirinom
                    getFragmentManager().beginTransaction().replace(R.id.F1, fk).addToBackStack(null).commit();
                }
            }
        }
        // U suprotnom, ako je 'provjera' false, fragment KnjigeFragment treba da prikaze listu knjiga selektovanog autora
        else {

            // U ovom slucaju ne trebamo ispitivati da li se kliknuti element (autor) moze otvoriti,
            // s obzirom da nije moguce unijeti autora bez naziva knjige, tako da ce svaki autor imati barem jednu knjigu

            // Priprema novog fragmenta KnjigeFragment
            Bundle arguments = new Bundle();
            arguments.putString("autor", ((Kontejner) getApplication()).getAutori().get(pos).getImeiPrezime());
            KnjigeFragment fk = new KnjigeFragment();
            fk.setArguments(arguments);
            if (siriL) {
                // Slucaj za ekrane sa sirom dijagonalom
                getFragmentManager().beginTransaction().replace(R.id.F2, fk).commit();
            } else {
                // Slucaj za ekrane sa pocetnom zadanom sirinom
                getFragmentManager().beginTransaction().replace(R.id.F1, fk).addToBackStack(null).commit();
            }
        }
    }

    // Implementiramo metodu interfejsa (onClickedPovratak)
    @Override
    public void onClickedPovratak() {

        // Ako smo u landscape-u, vracamo vidljivost desnog fragmenta i postavljamo tezine oba fragmenta na 1 da bi se prikazali (50:50) na displeju
        if(siriL) {
            fragment2.setVisibility(View.VISIBLE);
            fragment1.setLayoutParams(param1);
            fragment2.setLayoutParams(param1);
        }

        // Priprema novog fragmenta ListeFragment
        ListeFragment fl = new ListeFragment();
        // S obzirom da se vracamo iz KnjigeFragment na ListeFragment, dovoljno je uraditi replace samo za fragment F1
        // jer se ListeFragment uvijek prikazuje samo u F1
        getFragmentManager().beginTransaction().replace(R.id.F1, fl).addToBackStack(null).commit();
    }

    // Implementiramo metodu interfejsa (onClickedDodajKnjigu)
    @Override
    public void onClickedDodajKnjigu() {

        // Ukoliko smo u landscape-u, sakrivamo drugi fragment i prosirujemo prvi preko citavog layouta, tj. postavljamo mu tezinu na 2
        if(siriL) {
            fragment2.setVisibility(View.GONE);
            fragment1.setLayoutParams(param2);
        }

        // Priprema novog fragmenta DodavanjeKnjigeFragment
        DodavanjeKnjigeFragment fdk = new DodavanjeKnjigeFragment();
        getFragmentManager().beginTransaction().replace(R.id.F1, fdk).addToBackStack(null).commit();
    }

    // Implementiramo metodu interfejsa (onClickedDodajKnjiguOnline)
    @Override
    public void onClickedDodajKnjiguOnline() {

        // Priprema novog fragmenta FragmentOnline
        FragmentOnline fo = new FragmentOnline();
        getFragmentManager().beginTransaction().replace(R.id.F1, fo).addToBackStack(null).commit();
    }

    // Implementiramo metodu interfejsa (onClickedPreporuciKnjigu)
    @Override
    public void onClickedPreporuciKnjigu(Knjiga kliknutaKnjiga) {
        // Priprema novog fragmenta FragmentOnline
        FragmentPreporuci fp = new FragmentPreporuci();

        Bundle arguments = new Bundle();
        arguments.putParcelable("kliknutaKnjiga", kliknutaKnjiga);
        fp.setArguments(arguments);

        getFragmentManager().beginTransaction().replace(R.id.F1, fp).addToBackStack(null).commit();
    }

}
