package com.example.gimnasio_unne.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Horarios;

import java.util.List;

public class AdaptadorHorarios extends ArrayAdapter<Horarios> {
    Context context;
    List<Horarios> arrayListHorarios;

    public AdaptadorHorarios(@NonNull Context context, List<Horarios>arrayListHorarios) {
        super(context, R.layout.list_horarios, arrayListHorarios);
        this.context = context;
        this.arrayListHorarios=arrayListHorarios;
    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_horarios, null, true);
        TextView tvHorario= view.findViewById(R.id.tvHorarioListarHorario);
        TextView tvEstado = view.findViewById(R.id.tvEstadoListarHorario);
        tvHorario.setText("De "+arrayListHorarios.get(position).getHoraInicio()+ " a " + arrayListHorarios.get(position).getHoraFin());
        if (arrayListHorarios.get(position).getEstado().equals("1")) {
            tvEstado.setText("Activo");
            tvEstado.setTextColor(Color.GREEN);
        } else {
            tvEstado.setText("Inactivo");
            tvEstado.setTextColor(Color.RED);
        }

        return view;
    }
}
