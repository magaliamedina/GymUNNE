package com.example.gimnasio_unne.view.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Grupos;

import java.util.List;

public class AdaptadorGrupos extends ArrayAdapter<Grupos> {
    Context context;
    List<Grupos>arrayListGroups;
    public AdaptadorGrupos(@NonNull Context context, List<Grupos>arrayListGroups) {
        super(context, R.layout.list_grupos, arrayListGroups);
        this.context = context;
        this.arrayListGroups=arrayListGroups;
    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grupos, null, true);

            TextView tvCupoTotal = view.findViewById(R.id.tvCupoTotal);
            TextView tvgrupo = view.findViewById(R.id.tvgrupo);
            tvCupoTotal.setText("Cupo total: " + arrayListGroups.get(position).getCupototal());
            tvgrupo.setText(arrayListGroups.get(position).getDescripcion());

        return view;
    }
}
