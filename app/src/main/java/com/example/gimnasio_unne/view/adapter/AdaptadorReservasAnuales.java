package com.example.gimnasio_unne.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Reservas;

import java.util.List;

public class AdaptadorReservasAnuales extends ArrayAdapter<Reservas> {
    Context context;
    List<Reservas> arrayListReservas;
    TextView tvMes, tvAnio, tvDescripcionGrupo, tvDiayHora;
    String id_cupolibre;

    public AdaptadorReservasAnuales(@NonNull Context context, List<Reservas>arrayListReservas) {
        super(context, R.layout.list_reservas_anuales, arrayListReservas);
        this.context = context;
        this.arrayListReservas=arrayListReservas;
    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reservas_anuales, null, true);
        tvMes = view.findViewById(R.id.tvMesReservasAnuales);
        tvAnio = view.findViewById(R.id.tvAnioReservasAnuales);
        tvDescripcionGrupo= view.findViewById(R.id.tvGrupoReservasAnuales);
        tvDiayHora= view.findViewById(R.id.tvHoraReservasAnuales);

        tvMes.setText(arrayListReservas.get(position).getMes());
        tvAnio.setText(arrayListReservas.get(position).getAnio());
        tvDescripcionGrupo.setText("  " +arrayListReservas.get(position).getGrupo());
        tvDiayHora.setText(arrayListReservas.get(position).getHorarios_inicio_fin());
        id_cupolibre= arrayListReservas.get(position).getCupolibre_id();
        return view;
    }
}
