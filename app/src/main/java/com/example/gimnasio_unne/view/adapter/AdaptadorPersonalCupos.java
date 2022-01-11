package com.example.gimnasio_unne.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.CuposLibres;

import java.util.List;

public class AdaptadorPersonalCupos extends ArrayAdapter<CuposLibres> {
    Context context;
    List<CuposLibres> arrayListCuposLibres;
    Button btnReservarCupoLibre;
    TextView tvDescripcionGrupo, tvProfesor, tvDiayHora, tvTotalCuposLibres, tvEstado, tvmes, tvanio;
    String id_grupo, id_cupolibre;

    public AdaptadorPersonalCupos(@NonNull Context context, List<CuposLibres>arrayListCuposLibres) {
        super(context, R.layout.list_cupos_personal, arrayListCuposLibres);
        this.context = context;
        this.arrayListCuposLibres=arrayListCuposLibres;
    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cupos_personal, null, true);
        tvDescripcionGrupo= view.findViewById(R.id.tvListarCuposLibresGrupo);
        tvProfesor = view.findViewById(R.id.tvListarCuposLibresProfesor);
        tvDiayHora= view.findViewById(R.id.tvListarCuposLibresDiayHora);
        tvTotalCuposLibres = view.findViewById(R.id.tvListarCuposLibresDisponible);
        tvEstado=view.findViewById(R.id.tvListarCuposLibresEstado);
        tvmes=view.findViewById(R.id.tvListarMes);
        tvanio=view.findViewById(R.id.tvListarAnio);

        btnReservarCupoLibre = view.findViewById(R.id.btnReservarCupoLibre);

        tvDescripcionGrupo.setText("  " +arrayListCuposLibres.get(position).getGrupo_descripcion());
        tvProfesor.setText(arrayListCuposLibres.get(position).getProfesor_nombreYapellido());
        tvDiayHora.setText(arrayListCuposLibres.get(position).getHorarios_inicio_fin());
        id_grupo = arrayListCuposLibres.get(position).getGrupo_id();
        id_cupolibre = arrayListCuposLibres.get(position).getId_cupolibre();
        tvTotalCuposLibres.setText(arrayListCuposLibres.get(position).getCupolibre_total());
        if (arrayListCuposLibres.get(position).getEstado().equals("1")) {
            tvEstado.setText("ACTIVO");
        } else {
            tvEstado.setTextColor(Color.RED);
            tvEstado.setText("INACTIVO");
        }
        tvmes.setText(arrayListCuposLibres.get(position).getMes());
        tvanio.setText(arrayListCuposLibres.get(position).getAnio());
        return view;
    }

}