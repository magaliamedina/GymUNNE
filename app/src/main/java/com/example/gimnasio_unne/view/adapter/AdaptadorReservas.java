package com.example.gimnasio_unne.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Reservas;

import java.util.List;
//reservas pendientes
public class AdaptadorReservas extends ArrayAdapter<Reservas> {
    Context context;
    List<Reservas> arrayListReservas;
    TextView tvDescripcionGrupo, tvEstudiante, tvDiayHora, tvFechaReserva, tvIdReserva;

    public AdaptadorReservas(@NonNull Context context, List<Reservas>arrayListReservas) {
        super(context, R.layout.list_reservas, arrayListReservas);
        this.context = context;
        this.arrayListReservas=arrayListReservas;
    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reservas, null, true);
        tvDescripcionGrupo= view.findViewById(R.id.tvListarReservasGrupo);
        tvEstudiante = view.findViewById(R.id.tvListarReservasEstudiante);
        tvDiayHora= view.findViewById(R.id.tvListarReservasDiayHora);
        tvFechaReserva = view.findViewById(R.id.tvListarReservasFechaReserva);
        tvIdReserva = view.findViewById(R.id.tvListarReservasNumeroReserva);

        tvDescripcionGrupo.setText(arrayListReservas.get(position).getGrupo());
        tvEstudiante.setText(arrayListReservas.get(position).getEstudiante_nya());
        tvDiayHora.setText(arrayListReservas.get(position).getHorarios_inicio_fin());
        tvFechaReserva.setText(arrayListReservas.get(position).getFecha_reserva());
        tvIdReserva.setText(arrayListReservas.get(position).getId_reserva());
        return view;
    }

}
