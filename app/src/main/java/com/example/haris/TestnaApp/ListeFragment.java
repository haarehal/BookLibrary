package com.example.haris.TestnaApp;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haris on 10-Apr-18.
 */

public class ListeFragment extends Fragment {

    private ArrayAdapter<String> adapterKategorije;
    private AdapterAutor adapterAutor;

    private OnItemClick oic;
    private OnClickDodajKnjigu ocdk;
    private OnClickDodajKnjiguOnline ocdko;

    private ArrayAdapter<String> adapter;
    private ListView lista;
    private ArrayList<String> kategorije;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Ovdje se dodjeljuje layout fragmentu, tj. sta ce se nalaziti unutar fragmenta
        View iv = inflater.inflate(R.layout.fragment_liste, container, false);

        return iv;
    }

    // Dohvacamo elemente iz Kontejner-a za popunjavanje liste kategorija (ListView)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Dohvacamo referencu na objekat
        final EditText tekst = (EditText)getView().findViewById(R.id.tekstPretraga);
        final Button dugmePretraga = (Button)getView().findViewById(R.id.dPretraga);
        final Button dugmeDodajKategoriju = (Button)getView().findViewById(R.id.dDodajKategoriju);
        final Button dugmeDodajKnjigu = (Button)getView().findViewById(R.id.dDodajKnjigu);
        lista = (ListView)getView().findViewById(R.id.listaKategorija);
        final Button dugmeKategorije = (Button)getView().findViewById(R.id.dKategorije);
        final Button dugmeAutori = (Button)getView().findViewById(R.id.dAutori);
        final Button dugmeDodajKnjiguOnline = (Button)getView().findViewById(R.id.dDodajOnline);

        // Iskljucujemo dugme za dodavanje kategorije
        dugmeDodajKategoriju.setEnabled(false);
        dugmeDodajKategoriju.setAlpha(0.5f);

        // Kreiramo listu kategorija
        kategorije = ((Kontejner)getActivity().getApplication()).getKategorije();

        // Kreiramo adapter i povezujemo ga sa listom kategorija
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, kategorije);
        lista.setAdapter(adapter);

        // Pravimo da se klikom na dugme za pretragu filtrira lista kategorija, te ako je rezultat prazna lista,
        // ukljucujemo dugme za dodavanje kategorije
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, kategorije);
        tekst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                dugmePretraga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lista.setAdapter(adapter2);
                        adapter2.getFilter().filter(charSequence, new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int count){
                                if(count == 0)
                                {
                                    dugmeDodajKategoriju.setEnabled(true);
                                    dugmeDodajKategoriju.setAlpha(1f);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
                lista.setAdapter(adapter);
                dugmeDodajKategoriju.setEnabled(false);
                dugmeDodajKategoriju.setAlpha(0.5f);
            }
        });

        // Pravimo da se klikom na dugme za dodavanje kategorije uneseni tekst doda u listu kategorija
        dugmeDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unos = tekst.getText().toString();
                // Postavljamo uslov da se ne moze dodati "prazna" kategorija klikom na dugme (tj. prazan string)
                if(!unos.isEmpty()) {
                    ((Kontejner)getActivity().getApplication()).DodajKategoriju(unos);
                    tekst.setText(""); // Brisemo uneseni tekst sa ekrana
                    adapter2.add(unos);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Pravimo da se klikom na dugme 'Kategorije' u ListView ucitaju kategorije
        dugmeKategorije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Odmah postavljamo varijablu 'provjera' na true da bismo znali da je izabran prikaz liste kategorija,
                // te da bismo znali da se klikom na element liste mora otvoriti 'KnjigeFragment' sa prikazom svih knjiga selektovane kategorije
                ((Kontejner)getActivity().getApplication()).setProvjera(true);

                // Povezujemo adapter sa listom kategorija
                lista.setAdapter(adapter);

                // Otkrivamo dugmadi i plain text
                dugmePretraga.setVisibility(View.VISIBLE);
                dugmeDodajKategoriju.setVisibility(View.VISIBLE);
                tekst.setVisibility(View.VISIBLE);
            }
        });

        // Pravimo da se klikom na dugme 'Autori' u ListView ucitaju autori sa brojem napisanih knjiga
        dugmeAutori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Odmah postavljamo varijablu 'provjera' na false da bismo znali da je izabran prikaz liste autora,
                // te da bismo znali da se klikom na element liste mora otvoriti 'KnjigeFragment' sa prikazom svih knjiga selektovanog autora
                ((Kontejner)getActivity().getApplication()).setProvjera(false);

                // Kreiramo adapter i povezujemo ga sa listom autora
                ArrayList<Autor> autori = ((Kontejner)getActivity().getApplication()).getAutori();
                adapterAutor = new AdapterAutor(getActivity(), R.layout.element_liste_autora, autori);
                lista.setAdapter(adapterAutor);

                // Sakrivamo dugmadi i plain text
                dugmePretraga.setVisibility(View.GONE);
                dugmeDodajKategoriju.setVisibility(View.GONE);
                tekst.setVisibility(View.GONE);
            }
        });

        // Prosljedjujemo event onItemClick u roditeljsku aktivnost (KategorijeAkt)
        try {
            // Dohvacamo referencu na roditeljsku aktivnost,
            // a kako ona implementira interfejs OnClickDodajKnjigu, moguce ju je cast-ati u taj interfejs
            ocdk = (OnClickDodajKnjigu)getActivity();
        }
        catch(ClassCastException e) {
            // U slucaju da se u roditeljskoj aktivnosti nije implementirao interfejs, baca se izuzetak
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnClickDodajKnjigu !");
        }
        // Ukoliko je aktivnost uspjesno cast-ana u interfejs, tada joj prosljedjujemo event
        dugmeDodajKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocdk.onClickedDodajKnjigu();
            }
        });

        // Prosljedjujemo event onItemClick u roditeljsku aktivnost (KategorijeAkt)
        try {
            // Dohvacamo referencu na roditeljsku aktivnost,
            // a kako ona implementira interfejs OnItemClick, moguce ju je cast-ati u taj interfejs
            oic = (OnItemClick)getActivity();
        }
        catch(ClassCastException e) {
            // U slucaju da se u roditeljskoj aktivnosti nije implementirao interfejs, baca se izuzetak
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick !");
        }
        // Ukoliko je aktivnost uspjesno cast-ana u interfejs, tada joj prosljedjujemo event
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                oic.onItemClicked(position);
        }
        });

        // Prosljedjujemo event onItemClick u roditeljsku aktivnost (KategorijeAkt)
        try {
            // Dohvacamo referencu na roditeljsku aktivnost,
            // a kako ona implementira interfejs OnClickDodajKnjiguOnline, moguce ju je cast-ati u taj interfejs
            ocdko = (OnClickDodajKnjiguOnline)getActivity();
        }
        catch(ClassCastException e) {
            // U slucaju da se u roditeljskoj aktivnosti nije implementirao interfejs, baca se izuzetak
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnClickDodajKnjiguOnline !");
        }
        // Ukoliko je aktivnost uspjesno cast-ana u interfejs, tada joj prosljedjujemo event
        dugmeDodajKnjiguOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocdko.onClickedDodajKnjiguOnline();
            }
        });

    }

    // Pravimo da se klikom na element liste kategorija otvori fragment 'KnjigeFragment'
    // Dodajemo novi interfejs kojeg ce roditeljska aktivnost (KategorijeAkt) implementirati
    // Ovaj interfejs ce se koristiti poput obicnog onClickListener-a, ali sada mi unutar fragmenta iniciramo event po potrebi
    // Na ovaj nacin je funkcionalnost fragmenta enkapsulirana i ovakav fragment se moze iskoristiti u vise aktivnosti
    public interface OnItemClick {
        public void onItemClicked(int pos);
    }
    // Pravimo da se klikom na dugme 'DODAJ KNJIGU' otvori fragment 'DodavanjeKnjigeFragment'
    public interface OnClickDodajKnjigu {
        public void onClickedDodajKnjigu();
    }
    // Pravimo da se klikom na dugme 'DODAJ KNJIGU ONLINE' otvori fragment 'FragmentOnline'
    public interface  OnClickDodajKnjiguOnline {
        public void onClickedDodajKnjiguOnline();
    }
}
