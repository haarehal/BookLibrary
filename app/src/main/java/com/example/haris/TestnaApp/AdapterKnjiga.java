package com.example.haris.TestnaApp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Haris on 20-Mar-18.
 */

public class AdapterKnjiga extends ArrayAdapter<Knjiga> {

    int resource;
    private Boolean provjeraa;
    private OnClickPreporuciKnjigu ocpk;

    // Konstruktor
    public AdapterKnjiga(Context context, int _resource, List<Knjiga> items, Boolean p) {
        super(context, _resource, items);
        resource = _resource; // resource je id layout-a listitem-a
        provjeraa = p;
    }

    // Override-amo metodu getView+
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Kreiranje i inflate-anje view klase
        final LinearLayout newView;
        if(convertView == null) {
            // Ukoliko je ovo prvi put da se pristupa klasi convertView, odnosno nije update,
            // potrebno je kreirati novi objekat i inflate-at ga
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        }
        else {
            // Ukoliko je update, potrebno je samo izmijeniti vrijednosti polja
            newView = (LinearLayout) convertView;
        }

        final Knjiga k = getItem(position);

        if(provjeraa) {

            // Ovdje mozemo dohvatiti reference na View i popuniti ga sa vrijednostima polja iz objekta
            ImageView slika = (ImageView) newView.findViewById(R.id.eNaslovna);
            TextView eNazivTextView = (TextView) newView.findViewById(R.id.eNaziv);
            TextView eAutorTextView = (TextView) newView.findViewById(R.id.eAutor);
            TextView eDatumObjavljivanjaTextView = (TextView) newView.findViewById(R.id.eDatumObjavljivanja);
            TextView eBrojStranicaTextView = (TextView) newView.findViewById(R.id.eBrojStranica);
            TextView eOpisTextView = (TextView) newView.findViewById(R.id.eOpis);
            Button dugmePreporuciKnjigu = (Button) newView.findViewById(R.id.dPreporuci);

            eNazivTextView.setText("''" + k.getNaziv() + "''");
            if (k.getImeIPrezimeAutora() != null) {
                eAutorTextView.setText(k.getImeIPrezimeAutora());
            } else {
                ArrayList<Autor> autori = k.getAutori();
                eAutorTextView.setText(autori.get(0).getImeiPrezime());
            }
            if (k.getDatumObjavljivanja() != null) {
                eDatumObjavljivanjaTextView.setText(k.getDatumObjavljivanja());
            } else {
                eDatumObjavljivanjaTextView.setText("/");
            }
            if (k.getBrojStranica() != 0) {
                eBrojStranicaTextView.setText(Integer.toString(k.getBrojStranica()));
            } else {
                eBrojStranicaTextView.setText("/");
            }
            if (k.getOpis() != null) {
                eOpisTextView.setText(k.getOpis());
            }

            if (k.getNaziv().equals("Shining")) {
                slika.setImageResource(R.drawable.shining);
            } else if (k.getNaziv().equals("House Of Leaves")) {
                slika.setImageResource(R.drawable.houseofleaves);
            } else if (k.getNaziv().equals("American Psycho")) {
                slika.setImageResource(R.drawable.americanpsycho);
            } else if (k.getNaziv().equals("Chalk")) {
                slika.setImageResource(R.drawable.chalk);
            } else if (k.getNaziv().equals("Kill Creek")) {
                slika.setImageResource(R.drawable.killcreek);
            } else if (k.getNaziv().equals("The Picture Of Dorian Gray")) {
                slika.setImageResource(R.drawable.thepictureofdoriangrey);
            } else if (k.getNaziv().equals("Fifty Shades Of Grey")) {
                slika.setImageResource(R.drawable.fiftyshadesofgrey);
            } else if (k.getNaziv().equals("Beautiful Bastard")) {
                slika.setImageResource(R.drawable.beautifulbastard);
            } else if (k.getNaziv().equals("Mile High")) {
                slika.setImageResource(R.drawable.milehigh);
            } else if (k.getNaziv().equals("Forbidden")) {
                slika.setImageResource(R.drawable.forbidden);
            } else if (k.getNaziv().equals("Romeo And Juliet")) {
                slika.setImageResource(R.drawable.romeoandjuliet);
            } else if (k.getNaziv().equals("The Bronze Horseman")) {
                slika.setImageResource(R.drawable.thebronzehorseman);
            } else if (k.getNaziv().equals("The 7th Victim")) {
                slika.setImageResource(R.drawable.the7thvictim);
            } else if (k.getNaziv().equals("Let Me Die in His Footsteps")) {
                slika.setImageResource(R.drawable.letmedieinhisfootsteps);
            } else if (k.getNaziv().equals("The Judas Child")) {
                slika.setImageResource(R.drawable.thejudaschild);
            } else if (k.getNaziv().equals("Captured")) {
                slika.setImageResource(R.drawable.captured);
            } else if (k.getNaziv().equals("The Da Vinci Code")) {
                slika.setImageResource(R.drawable.thedavincicode);
            } else if (k.getNaziv().equals("The Girl on the Train")) {
                slika.setImageResource(R.drawable.thegirlonthetrain);
            } else {

                if (k.getUriSlike() != null) {
                    slika.setImageURI(k.getUriSlike());
                } else if (k.getSlika() != null) {
                    Picasso.get().load(k.getSlika().toString()).into(slika);
                }
            }


            // Prosljedjujemo event onClickPreporuciKnjigu u roditeljsku aktivnost (KategorijeAkt)
            try {
                // Dohvacamo referencu na roditeljsku aktivnost,
                // a kako ona implementira interfejs OnClick, moguce ju je cast-ati u taj interfejs
                ocpk = (OnClickPreporuciKnjigu) getContext();
            } catch (ClassCastException e) {
                // U slucaju da se u roditeljskoj aktivnosti nije implementirao interfejs, baca se izuzetak
                throw new ClassCastException(getContext().toString() + "Treba implementirati OnClick !");
            }
            // Ukoliko je aktivnost uspjesno cast-ana u interfejs, tada joj prosljedjujemo event
            dugmePreporuciKnjigu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ocpk.onClickedPreporuciKnjigu(k);
                }
            });

        }
        else {
            // Ovdje mozemo dohvatiti reference na View i popuniti ga sa vrijednostima polja iz objekta
            TextView textViewNazivKnjige = (TextView)newView.findViewById(R.id.nazivKnjige2);
            textViewNazivKnjige.setText(k.getNaziv());
        }

        // Postavljamo boju pozadine elementa:
        // ako je kliknuto na knjigu, atribut 'boolean kliknutoNaKnjigu' ce uvijek biti 'true'
        if (k.getKliknutoNaKnjigu() == true) {
            newView.setBackgroundColor(0xffaabbed);
        } else {
            newView.setBackgroundColor(0xFFFFFFFF);
        }

        return newView;
    }

    public interface OnClickPreporuciKnjigu {
        public void onClickedPreporuciKnjigu(Knjiga kliknutaKnjiga);
    }
}
