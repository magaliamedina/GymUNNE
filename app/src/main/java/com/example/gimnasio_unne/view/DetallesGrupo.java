package com.example.gimnasio_unne.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.view.fragments.FragmentListarGrupos;

public class DetallesGrupo extends AppCompatActivity {
    TextView tvnombre, tvprof,  tvtcupototal,tvid, tvhorario, tvestado;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvid = findViewById(R.id.txtid);
        tvhorario = findViewById(R.id.txthorariogrupodetalle);
        tvnombre = findViewById(R.id.txtnombre);
        tvprof = findViewById(R.id.txtprof);
        tvtcupototal = findViewById(R.id.txtcupototal);
        tvestado= findViewById(R.id.tvEstadoGrupoDetalle);

        //recibimos los parametros de Home
        Intent intent=getIntent();
        position= intent.getExtras().getInt("position");

        tvid.setText("ID " + FragmentListarGrupos.groups.get(position).getId());
        tvhorario.setText("Horario " + FragmentListarGrupos.groups.get(position).getHorario());
        tvnombre.setText("Nombre " + FragmentListarGrupos.groups.get(position).getDescripcion());
        tvprof.setText("Profesor " + FragmentListarGrupos.groups.get(position).getProf());
        tvtcupototal.setText("Cupo total " + FragmentListarGrupos.groups.get(position).getCupototal());
        if (FragmentListarGrupos.groups.get(position).getEstado().equals("0")) {
            tvestado.setText("Estado: INACTIVO");
        } else {
            tvestado.setText("Estado: ACTIVO");
        }
    }
}
