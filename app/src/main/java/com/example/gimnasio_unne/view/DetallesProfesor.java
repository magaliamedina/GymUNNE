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
import com.example.gimnasio_unne.view.fragments.FragmentListarProfesores;

//no muestra la contraseña
public class DetallesProfesor extends AppCompatActivity {
    TextView tvid, tvdni, tvApeYnom, tvsexo, tvfechaNac, tvlocalidad, tvprovincia, tvestado,tvestadocivil,
     tvemail;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_profesor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //tvid = findViewById(R.id.tvIdDetallePersona);
        tvdni = findViewById(R.id.txtdnipersonadetalle);
        tvApeYnom= findViewById(R.id.txtApeYNompersonadetalle);
        tvsexo = findViewById(R.id.txtsexopersonadetalle);
        tvfechaNac = findViewById(R.id.txtfechnacpersonadetalle);
        //tvlocalidad = findViewById(R.id.txtlocalidadpersonadetalle);
        tvprovincia = findViewById(R.id.txtprovinciapersonadetalle);
        tvestado = findViewById(R.id.txtestadopersonadetalle);
        tvestadocivil = findViewById(R.id.txtestadocivilpersonadetalle);
        tvemail = findViewById(R.id.txtemailpersonadetalle);

        //recibimos los parametros de Home
        Intent intent=getIntent();
        position= intent.getExtras().getInt("position");

        //tvid.setText(FragmentListarProfesores.persons.get(position).getId());
        tvdni.setText(FragmentListarProfesores.persons.get(position).getDni());
        tvApeYnom.setText(FragmentListarProfesores.persons.get(position).getApellido() + " " +
                FragmentListarProfesores.persons.get(position).getNombres());
        if (FragmentListarProfesores.persons.get(position).getSexo().equals("1")) {
            tvsexo.setText("Masculino");
        } else if (FragmentListarProfesores.persons.get(position).getSexo().equals("2")) {
            tvsexo.setText("Femenino");
        }
        tvfechaNac.setText( FragmentListarProfesores.persons.get(position).getFechaNac());
        //tvlocalidad.setText("Localidad: " + FragmentListarProfesores.persons.get(position).getLocalidad());
        tvprovincia.setText(FragmentListarProfesores.persons.get(position).getProvincia());
        if (FragmentListarProfesores.persons.get(position).getEstado().equals("1")) {
            tvestado.setText("Activo");
            tvestado.setTextColor(Color.GREEN);
        } else {
            tvestado.setText("Inactivo");
            tvestado.setTextColor(Color.RED);
        }
        tvestadocivil.setText(FragmentListarProfesores.persons.get(position).getEstadoCivil());
        tvemail.setText(FragmentListarProfesores.persons.get(position).getEmail());
    }

    //para el boton atras
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
