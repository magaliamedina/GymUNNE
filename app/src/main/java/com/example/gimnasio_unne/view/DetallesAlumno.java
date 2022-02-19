package com.example.gimnasio_unne.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.view.fragments.FragmentListarAlumnos;

public class DetallesAlumno extends AppCompatActivity {
    TextView tvid, tvdni, tvApeYnom, tvsexo, tvfechaNac, tvlocalidad, tvprovincia, tvestado,tvestadocivil,
            tvemail;
    int position;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_alumno);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //tvid = findViewById(R.id.tvIdDetallePersona);
        tvdni = findViewById(R.id.txtdnialumnodetalle);
        tvApeYnom= findViewById(R.id.txtApeYNomalumnodetalle);
        tvsexo = findViewById(R.id.txtsexoalumnodetalle);
        tvfechaNac = findViewById(R.id.txtfechnacalumnodetalle);
        //tvlocalidad = findViewById(R.id.txtlocalidadpersonadetalle);
        tvprovincia = findViewById(R.id.txtprovinciaalumnodetalle);
        tvestado = findViewById(R.id.txtestadoalumnodetalle);
        tvestadocivil = findViewById(R.id.txtestadocivilalumnodetalle);
        tvemail = findViewById(R.id.txtemailalumnodetalle);

        //recibimos los parametros de Home
        Intent intent=getIntent();
        position= intent.getExtras().getInt("position");

        //tvid.setText(FragmentListarAlumnos.persons.get(position).getId());
        tvdni.setText(FragmentListarAlumnos.persons.get(position).getDni());
        tvApeYnom.setText(FragmentListarAlumnos.persons.get(position).getApellido() + " " +
                FragmentListarAlumnos.persons.get(position).getNombres());
        if (FragmentListarAlumnos.persons.get(position).getSexo().equals("1")) {
            tvsexo.setText("Masculino");
        } else if (FragmentListarAlumnos.persons.get(position).getSexo().equals("2")) {
            tvsexo.setText("Femenino");
        }
        tvfechaNac.setText( FragmentListarAlumnos.persons.get(position).getFechaNac());
        //tvlocalidad.setText("Localidad: " + FragmentListarAlumnos.persons.get(position).getLocalidad());
        tvprovincia.setText(FragmentListarAlumnos.persons.get(position).getProvincia());
        if (FragmentListarAlumnos.persons.get(position).getEstado().equals("1")) {
            tvestado.setText("Activo");
            tvestado.setTextColor(Color.GREEN);
        } else {
            tvestado.setText("Inactivo");
            tvestado.setTextColor(Color.RED);
        }
        tvestadocivil.setText(FragmentListarAlumnos.persons.get(position).getEstadoCivil());
        tvemail.setText(FragmentListarAlumnos.persons.get(position).getEmail());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}