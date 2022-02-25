package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.AlumnoActivity;
import com.example.gimnasio_unne.Login;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.Utiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentMisReservas extends Fragment {

    String url, id_reserva;
    TextView tvDescripcionGrupo, tvProfesor, tvDiayHora, tvFechaReserva, tvEstadoReserva,tvNingunaReserva;
    CardView cvMisReservas;
    Button btnCancelarReservaMisReservas;
    String urlEliminarReserva= "https://medinamagali.com.ar/gimnasio_unne/cancelar_reserva.php";
    private ProgressBar progressBar;

    public FragmentMisReservas() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_mis_reservas, container, false);
        tvDescripcionGrupo= view.findViewById(R.id.tvListarMisReservasGrupo);
        tvProfesor = view.findViewById(R.id.tvListarMisReservasNombreProfesor);
        tvDiayHora= view.findViewById(R.id.tvListarMisReservasDiayHora);
        tvFechaReserva = view.findViewById(R.id.tvListarMisReservasFechaReserva);
        tvEstadoReserva = view.findViewById(R.id.tvListarMisReservasEstadoReserva);
        tvNingunaReserva = view.findViewById(R.id.tvNingunaReserva);
        cvMisReservas= view.findViewById(R.id.cvMisReservas);
        btnCancelarReservaMisReservas = view.findViewById(R.id.btnCancelarReservaMisReservas);
        btnCancelarReservaMisReservas.setVisibility(View.GONE);
        progressBar=view.findViewById(R.id.progressBarMiReserva);
        //sin conexion a internet
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);
        tvNingunaReserva.setVisibility(View.INVISIBLE);

        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            //mostrar datos
            url = "https://medinamagali.com.ar/gimnasio_unne/mi_reserva.php?personas_id=" + Login.personas_id + "";
            mostrarDatos();
        }
        else {
            // no hay internet
            progressBar.setVisibility(View.GONE);
            imgSinConexion.setVisibility(View.VISIBLE);
            tvSinConexion1.setVisibility(View.VISIBLE);
            tvSinConexion2.setVisibility(View.VISIBLE);
            tvNingunaReserva.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                    "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void cancelarReserva(final String url) {
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Se canceló la reserva", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<String, String>();
                params.put("reserva_id", id_reserva);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess=jsonObject.getString("sucess");
                    if (sucess.equals("1")) {
                        JSONArray jsonArray=jsonObject.getJSONArray("misreservas");
                        if(jsonArray.length()>0) { //si hay en el arreglo "mis reservas" por lo menos un elemento
                            cvMisReservas.setVisibility(View.VISIBLE);
                            tvNingunaReserva.setVisibility(View.INVISIBLE);
                        } else{
                            tvNingunaReserva.setVisibility(View.VISIBLE);
                        }
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject object= jsonArray.getJSONObject(i);
                            tvDescripcionGrupo.setText("Grupo: " + object.getString("grupo_descripcion"));
                            tvProfesor.setText("Profesor: " + object.getString("personas_nombre") + " " + object.getString("personas_apellido"));
                            tvDiayHora.setText("Horario: de " + object.getString("horarios_hora_inicio") + " a " + object.getString("horarios_hora_fin"));
                            tvFechaReserva.setText("Fecha de la reserva: " + object.getString("fecha_reserva"));
                            String estado_reserva = object.getString("estado_reserva");
                            if (estado_reserva.equals("0")) {
                                tvEstadoReserva.setText("Estado de la reserva: PENDIENTE");
                                btnCancelarReservaMisReservas.setVisibility(View.VISIBLE);
                                mostrarBotonCancelar();
                            } else if (estado_reserva.equals("1")) {
                                tvEstadoReserva.setText("Estado de la reserva: CONFIRMADA");
                            } else {
                                tvEstadoReserva.setText("Estado de la reserva: CANCELADA");
                            }
                            String id_cupolibre = object.getString("id_cupolibre");
                            id_reserva = object.getString("id_reserva");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("personas_id", Login.personas_id);
                parametros.put("mes", Utiles.obtenerMesActual());
                parametros.put("anio", Utiles.obtenerAnio());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    private boolean tieneConexionInternet() {
        ConnectivityManager con = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void mostrarBotonCancelar() {
        btnCancelarReservaMisReservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tieneConexionInternet()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Selecciona una respuesta.")
                            .setMessage("Estas seguro que desea cancelar?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelarReserva(urlEliminarReserva);
                            tvNingunaReserva.setVisibility(View.VISIBLE);
                            cvMisReservas.setVisibility(View.INVISIBLE);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
