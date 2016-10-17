package br.com.thesource.ondedescer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.thesource.ondedescer.R;
import br.com.thesource.ondedescer.model.Alerta;

/**
 * Created by xxnickfuryxx on 13/03/15.
 */
public class AlertaAdapter extends ArrayAdapter<Alerta> {

    private int resource;

    public AlertaAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public Alerta getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(resource, parent, false);

        Alerta alerta = getItem(position);

        TextView tx_nome = (TextView)vi.findViewById(R.id.tx_nome);
        TextView tx_metro = (TextView)vi.findViewById(R.id.tx_metro);
        TextView tx_end = (TextView)vi.findViewById(R.id.tx_end);

        tx_nome.setText(alerta.getNome());
        tx_metro.setText(alerta.getMetros()+" metros");
        tx_end.setText(alerta.getEndrereco().getAddressLine(0));


        return vi;

    }
}
