package com.example.haris.TestnaApp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Haris on 10-Apr-18.
 */

public class KnjigeFragment extends Fragment {

    private String kategorija;
    private String imeAutora;
    private OnClickPovratak ocp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Ovdje se dodjeljuje layout fragmentu, tj. sta ce se nalaziti unutar fragmenta
        View iv = inflater.inflate(R.layout.fragment_knjige, container, false);

        // Dohvacamo referencu na objekat
        ListView lista = (ListView)iv.findViewById(R.id.listaKnjiga);
        Button dugmePovratak = (Button)iv.findViewById(R.id.dPovratak);
        //Button dugmePreporuciKnjigu = (Button)lista.findViewById(R.id.dPreporuci);

        // Kreiramo listu knjiga
        ArrayList<Knjiga> knjige = ((Kontejner)getActivity().getApplication()).getKnjige();
        final ArrayList<Knjiga> knjigeZaPrikaz = new ArrayList<Knjiga>();

        // Kreiramo adapter kojeg cemo povezati sa listom knjiga
        AdapterKnjiga adapter;

        // Vrsimo provjeru
        Boolean provjeraa = ((Kontejner)getActivity().getApplication()).getProvjera();

        // Ukoliko je 'provjera' true, treba da se prikaze lista knjiga iz selektovane kategorije
        // Preuzimamo proslijedjenu kategoriju na koju je korisnik kliknuo i prikazujemo njene knjige
        if(getArguments() != null && getArguments().containsKey("kategorija") && provjeraa == true) {

            kategorija = getArguments().getString("kategorija");

            for(int position=0; position<knjige.size(); position++) {
                Knjiga k = knjige.get(position);
                if(k.kategorija.toLowerCase().equals(kategorija.toLowerCase())) {
                    knjigeZaPrikaz.add(k);
                }
            }

            // Povezujemo adapter sa listom knjiga
            adapter = new AdapterKnjiga(getActivity(), R.layout.element_liste_knjiga, knjigeZaPrikaz, provjeraa);
            lista.setAdapter(adapter);

        }
        // U suprotnom, ako je 'provjera' false, treba da se prikaze lista knjiga selektovanog autora
        else if(getArguments() != null && getArguments().containsKey("autor") && provjeraa == false) {

            imeAutora = getArguments().getString("autor");

            for(int position=0; position<knjige.size(); position++) {
                Knjiga k = knjige.get(position);
                if(k.imeIPrezimeAutora.toLowerCase().equals(imeAutora.toLowerCase())) {
                    knjigeZaPrikaz.add(k);
                }
            }

            // Povezujemo adapter sa listom knjiga
            adapter = new AdapterKnjiga(getActivity(), R.layout.element_liste_knjiga_2, knjigeZaPrikaz, provjeraa);
            lista.setAdapter(adapter);
        }

        // Bojimo element liste na koji se kliknulo
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Postavljamo atribut 'kliknutoNaKnjigu' klase Knjiga na 'true' da bi adapter znao da taj element ponovo treba
                // obojiti kada se otvori lista knjiga
                String trenutnaKnjiga = knjigeZaPrikaz.get(position).getNaziv();
                ((Kontejner)getActivity().getApplication()).postaviKlik(trenutnaKnjiga);

                // Postavljamo boju trenutno kliknutog elementa
                view.setBackgroundColor(0xffaabbed);
            }
        });

        // Prosljedjujemo event onClickPovratak u roditeljsku aktivnost (KategorijeAkt)
        try {
            // Dohvacamo referencu na roditeljsku aktivnost,
            // a kako ona implementira interfejs OnClick, moguce ju je cast-ati u taj interfejs
            ocp = (OnClickPovratak)getActivity();
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

}
