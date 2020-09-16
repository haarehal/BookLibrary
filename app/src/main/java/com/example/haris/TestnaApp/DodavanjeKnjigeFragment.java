package com.example.haris.TestnaApp;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Haris on 10-Apr-18.
 */

public class DodavanjeKnjigeFragment extends android.app.Fragment {

    // Privatni atributi
    private static final int request_code = 1;
    private ImageView slika;
    private EditText nazivKnjige;
    private Uri uri;

    private OnClickPovratak ocp;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Ovdje se dodjeljuje layout fragmentu, tj. sta ce se nalaziti unutar fragmenta
        View iv = inflater.inflate(R.layout.fragment_dodavanje_knjige, container, false);

        // Dohvacamo referencu na objekat
        slika = (ImageView)iv.findViewById(R.id.naslovnaStr);
        final EditText imeAutora = (EditText)iv.findViewById(R.id.imeAutora);
        nazivKnjige = (EditText)iv.findViewById(R.id.nazivKnjige);
        Button dugmeNadjiSliku = (Button)iv.findViewById(R.id.dNadjiSliku);
        Button dugmeUpisiKnjigu = (Button)iv.findViewById(R.id.dUpisiKnjigu);
        Button dugmePonisti = (Button)iv.findViewById(R.id.dPonisti);
        final Spinner spinner = (Spinner)iv.findViewById(R.id.sKategorijaKnjige);

        slika.setImageResource(R.drawable.unknownbook);

        // Kreiramo listu kategorija i dodjeljujemo mu listu kategorija iz fragmenta ListeFragment
        final ArrayList<String> kategorije;
        kategorije = ((Kontejner)getActivity().getApplication()).getKategorije();

        // Kreiramo adapter i povezujemo ga sa spinnerom
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategorije);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Pravimo da se klikom na dugme 'Upisi knjigu' u niz doda knjiga sa unesenim podacima
        dugmeUpisiKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ia = imeAutora.getText().toString();
                String nk = nazivKnjige.getText().toString();
                String k = spinner.getSelectedItem().toString();

                // Preko Bitmapa provjeravamo da li je korisnik zaista odabrao sliku
                final Bitmap bmap1 = ((BitmapDrawable)slika.getDrawable()).getBitmap();
                Drawable myDrawable = getResources().getDrawable(R.drawable.unknownbook);
                final Bitmap bmap2 = ((BitmapDrawable) myDrawable).getBitmap();

                // Pravimo iskocnu poruku u slucaju da nisu popunjena sva polja za unos, u suprotnom vrsimo upisivanje knjige
                if(ia.equals("") || nk.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.molimo_popunite_sva_polja), Toast.LENGTH_LONG).show();
                }
                else if(bmap1.sameAs(bmap2)){
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.molimo_izaberite_sliku), Toast.LENGTH_LONG).show();
                }
                else {
                    // Upisujemo knjigu
                    Knjiga knjiga = new Knjiga(ia, nk, k);
                    ((Kontejner)getActivity().getApplication()).DodajKnjigu(knjiga);
                    ((Kontejner)getActivity().getApplication()).postaviUri(uri);

                    // Obavjestavamo iskocnom porukom da je unos uspjesno zavrsen
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.knjiga_upisana), Toast.LENGTH_LONG).show();

                    // Dodajemo autora u listu autora
                    // Prvo provjeravamo da li se navedeni autor vec nalazi u listi
                    ArrayList<Autor> autori = ((Kontejner)getActivity().getApplication()).getAutori();
                    Boolean test = false;
                    for(int position=0; position<autori.size(); position++) {
                        Autor a = autori.get(position);
                        if(a.getImeiPrezime().toLowerCase().equals(ia.toLowerCase())) {
                            test = true;
                            break;
                        }
                    }
                    // Ukoliko postoji, povecavamo mu broj knjiga za 1
                    if(test) {
                        ((Kontejner)getActivity().getApplication()).PristupiUvecanjuKnjiga(ia);
                    }
                    // Ukoliko ne postoji, upisujemo ga u listu autora
                    else {
                        Autor autor = new Autor(ia, 1);
                        ((Kontejner)getActivity().getApplication()).DodajAutora(autor);
                    }
                }
            }
        });

        // Pravimo da se klikom na dugme 'Nadji sliku' otvori dijalog za odabir slike,
        // te je zatim prikazujemo u aktivnosti metodom 'onActivityResult()'
        dugmeNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Koristimo implicitni intent
                Intent imageIntent = new Intent();
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                Intent ci = Intent.createChooser(imageIntent, "Select Image");

                startActivityForResult(ci, request_code);
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
        dugmePonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocp.onClickedPovratak();
            }
        });

        // Ovdje se dodjeljuje layout fragmentu, tj. sta ce se nalaziti unutar fragmenta
        return iv;
    }

    // Ucitavamo odabranu sliku u ImageView
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            slika.setImageURI(uri);
        }
    }

    // Pravimo da se klikom na dugme POVRATAK vratimo na pocetni fragment 'ListeFragment'
    // Dodajemo novi interfejs kojeg ce roditeljska aktivnost (KategorijeAkt) implementirati
    // Ovaj interfejs ce se koristiti poput obicnog onClickListener-a, ali sada mi unutar fragmenta iniciramo event po potrebi
    // Na ovaj nacin je funkcionalnost fragmenta enkapsulirana i ovakav fragment se moze iskoristiti u vise aktivnosti
    public interface OnClickPovratak {
        public void onClickedPovratak();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = null;
        while (image == null) {
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        }
        parcelFileDescriptor.close();
        return image;
    }


}
