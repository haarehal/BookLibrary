package com.example.haris.TestnaApp;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/**
 * Created by Haris on 03-Jun-18.
 */

public class FragmentPreporuci extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Ovdje se dodjeljuje layout fragmentu, tj. sta ce se nalaziti unutar fragmenta
        View iv = inflater.inflate(R.layout.fragment_preporuci, container, false);

        if(getArguments() != null && getArguments().containsKey("kliknutaKnjiga")) {

            // Dohvacamo kliknutu knjigu
            final Knjiga kliknutaKnjiga = getArguments().getParcelable("kliknutaKnjiga");

            // Popunjavamo detalje o preporucenoj knjizi
            TextView textViewId = (TextView)iv.findViewById(R.id.textViewId);
            if(kliknutaKnjiga.getId() == null) {
                textViewId.setText("/");
            } else {
                textViewId.setText(kliknutaKnjiga.getId());
            }
            TextView textViewNazivKnjige = (TextView)iv.findViewById(R.id.textViewNazivKnjige);
            textViewNazivKnjige.setText(kliknutaKnjiga.getNaziv());
            TextView textViewDatumObjavljivanja = (TextView)iv.findViewById(R.id.textViewDatumObjavljivanja);
            if(kliknutaKnjiga.getDatumObjavljivanja() == null) {
                textViewDatumObjavljivanja.setText("/");
            } else {
                textViewDatumObjavljivanja.setText(kliknutaKnjiga.getDatumObjavljivanja());
            }
            TextView textViewBrojStranica = (TextView)iv.findViewById(R.id.textViewBrojStranica);
            if(kliknutaKnjiga.getBrojStranica() == 0) {
                textViewBrojStranica.setText("/");
            } else {
                textViewBrojStranica.setText(Integer.toString(kliknutaKnjiga.getBrojStranica()));
            }
            TextView textViewAutor = (TextView)iv.findViewById(R.id.textViewAutor);
            String imeA = "";
            if (kliknutaKnjiga.getImeIPrezimeAutora() != null) {
                imeA = kliknutaKnjiga.getImeIPrezimeAutora();
                textViewAutor.setText(imeA);
            } else if(kliknutaKnjiga.getAutori().size() != 0){
                imeA = kliknutaKnjiga.getAutori().get(0).getImeiPrezime();
                textViewAutor.setText(imeA);
            }
            else {
                textViewAutor.setText("/");
            }
            if(kliknutaKnjiga.getOpis() == null) {
                TextView txt = (TextView)iv.findViewById(R.id.txt);
                txt.setText("/");
            }
            else {
                TextView textViewOpis = (TextView) iv.findViewById(R.id.textViewOpis);
                textViewOpis.setText(kliknutaKnjiga.getOpis());
            }

            // Dohvacamo kontakte iz liste kontakata sa uredjaja (mobitel ili emulator)
            final ArrayList<Kontakt> kontakti = new ArrayList<Kontakt>();

            String email = null;

            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

            Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex(_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    ArrayList<String> emailovi = new ArrayList<String>();

                    // Na osnovu svakog id-a kontakta citamo njegove email adrese
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        if(email != null) { emailovi.add(email); }
                    }
                    emailCursor.close();

                    // Dodajemo kontakt u listu kontakata
                    Kontakt kontakt = new Kontakt(id, name, emailovi);
                    kontakti.add(kontakt);
                }
            }
            cursor.close();

            // Popunjavamo spinner sa listom email adresa
            final ArrayList<String> listaEmailova = new ArrayList<String>();
            for(int i=0; i<kontakti.size(); i++) {
                ArrayList<String> emailovi = kontakti.get(i).getEmailovi();
                for(int j=0; j<emailovi.size(); j++) {
                    listaEmailova.add(emailovi.get(j));
                }
            }
            final Spinner spinnerKontakti = (Spinner)iv.findViewById(R.id.sKontakti);
            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaEmailova);
            spinnerKontakti.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // Omogucavamo slanje email-a klikom na dugme 'POSALJI E-MAIL'
            Button dugmePosaljiEmail = (Button)iv.findViewById(R.id.dPosalji);
            dugmePosaljiEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Provjeravamo da li je spinner prazan
                    if (!listaEmailova.isEmpty()) {

                        // Selektovani email
                        String emailKontakta = spinnerKontakti.getSelectedItem().toString();

                        // Trazimo ime kontakta koji ima navedeni selektovani email
                        String imeKontakta = "";
                        for(int i = 0; i < kontakti.size(); i++) {
                            ArrayList<String> emailovi = kontakti.get(i).getEmailovi();
                            for (int j = 0; j < emailovi.size(); j++) {
                                if (emailovi.get(j).equals(emailKontakta)) {
                                    imeKontakta = kontakti.get(i).getImeIPrezime();
                                    break;
                                }
                            }
                        }

                        String imeAutora = "";
                        if (kliknutaKnjiga.getImeIPrezimeAutora() != null) {
                            imeAutora = kliknutaKnjiga.getImeIPrezimeAutora();
                        } else {
                            imeAutora = kliknutaKnjiga.getAutori().get(0).getImeiPrezime();
                        }

                        String[] to = {emailKontakta};
                        String subject = "Preporuka za knjigu";
                        String message = "Zdravo " + imeKontakta + ",\nPročitaj knjigu " + kliknutaKnjiga.getNaziv() + " od autora " + imeAutora + "!";

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("text/plain"); // ili emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        } catch (android.content.ActivityNotFoundException e) {
                            Toast.makeText(getActivity().getApplicationContext(),  getResources().getString(R.string.nepostojeca_email_adresa), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.nepostojeca_email_adresa), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        return iv;
    }

}
