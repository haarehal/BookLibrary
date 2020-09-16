package com.example.haris.TestnaApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Haris on 12-Apr-18.
 */

public class AdapterAutor extends ArrayAdapter<Autor> {
    int resource;

    // Konstruktor
    public AdapterAutor(Context context, int _resource, List<Autor> items) {
        super(context, _resource, items);
        resource = _resource; // resource je id layout-a listitem-a
    }

    // Override-amo metodu getView+
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Kreiranje i inflate-anje view klase
        LinearLayout newView;
        if (convertView == null) {
            // Ukoliko je ovo prvi put da se pristupa klasi convertView, odnosno nije update,
            // potrebno je kreirati novi objekat i inflate-at ga
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            // Ukoliko je update, potrebno je samo izmijeniti vrijednosti polja
            newView = (LinearLayout) convertView;
        }

        Autor a = getItem(position);

        // Ovdje mozemo dohvatiti reference na View i popuniti ga sa vrijednostima polja iz objekta
        TextView tekst1 = (TextView)newView.findViewById(R.id.imeAutora2);
        TextView tekst2 = (TextView)newView.findViewById(R.id.brojKnjiga);

        tekst1.setText(a.getImeiPrezime());
        tekst2.setText(Integer.toString(a.getBrojKnjiga()));

        return newView;
    }
}
