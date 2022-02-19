package com.example.gimnasio_unne.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        //sin internet
        ImageView imgSinConexion=findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);

        if(tieneConexionInternet()) { //INICIO IF

            tvid = findViewById(R.id.txtid);
            tvhorario = findViewById(R.id.txthorariogrupodetalle);
            tvnombre = findViewById(R.id.txtnombre);
            tvprof = findViewById(R.id.txtprof);
            tvtcupototal = findViewById(R.id.txtcupototal);
            tvestado = findViewById(R.id.tvEstadoGrupoDetalle);

            //recibimos los parametros de Home
            Intent intent = getIntent();
            position = intent.getExtras().getInt("position");

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
        } //FIN IF TIENE CONEXION
        else {
            //mensaje de no hay internet
            imgSinConexion.setVisibility(View.VISIBLE);
            tvSinConexion1.setVisibility(View.VISIBLE);
            tvSinConexion2.setVisibility(View.VISIBLE);
            Toast.makeText(DetallesGrupo.this, "No se pudo conectar, revise el " +
                    "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean tieneConexionInternet() {
        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            return true;
        }
        return false;
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
