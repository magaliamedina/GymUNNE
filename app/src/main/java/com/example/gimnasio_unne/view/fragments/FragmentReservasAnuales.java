package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.Login;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Reservas;
import com.example.gimnasio_unne.view.adapter.AdaptadorReservas;
import com.example.gimnasio_unne.view.adapter.AdaptadorReservasAnuales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentReservasAnuales extends Fragment {

    private ListView list;
    private ProgressBar progressBar;
    public static ArrayList<Reservas> arrayReservas= new ArrayList<>();
    String url = "https://medinamagali.com.ar/gimnasio_unne/listar_reservasanuales_alumno.php?alumno_id="+ Login.personas_id;
    AdaptadorReservasAnuales adaptador;
    Reservas reservas;
    String id_cupolibre;
    TextView tvSinReservas, tvTitulo;
    public FragmentReservasAnuales() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_reservas_anuales, container, false);
        list = view.findViewById(R.id.lvReservasAnuales);
        tvSinReservas= view.findViewById(R.id.tvSinReservasAnuales);
        progressBar=view.findViewById(R.id.progressBarReservasAnuales);
        //sin conexion a internet
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        tvTitulo=view.findViewById(R.id.tvTitulo);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);
        tvTitulo.setVisibility(View.INVISIBLE);

        if(tieneConexionInternet()) {
            //mostrar datos
            adaptador = new AdaptadorReservasAnuales(getActivity().getApplicationContext(), arrayReservas);
            list.setAdapter(adaptador);
            mostrarDatos();
        } // cierre de si hay internet
        else {
            // no hay internet
            imgSinConexion.setVisibility(View.VISIBLE);
            tvSinConexion1.setVisibility(View.VISIBLE);
            tvSinConexion2.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                    "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayReservas.clear();
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess=jsonObject.getString("sucess");
                    JSONArray jsonArray=jsonObject.getJSONArray("reservas");
                    if (sucess.equals("1")) {
                        list.setVisibility(View.VISIBLE);
                        tvTitulo.setVisibility(View.VISIBLE);
                        tvSinReservas.setVisibility(View.INVISIBLE);
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject object= jsonArray.getJSONObject(i);
                            String mes= object.getString("mes");
                            String anio = object.getString("anio");
                            String grupo_descripcion= object.getString("grupo");
                            String hora_inicio = object.getString("hora_inicio");
                            String hora_fin = object.getString("hora_fin");
                            id_cupolibre= object.getString("cupolibre_id"); //no se guarda en la clase
                            reservas = new Reservas(mes, anio, grupo_descripcion,"de "+hora_inicio+" a " +  hora_fin);
                            arrayReservas.add(reservas);
                            adaptador.notifyDataSetChanged();
                        }
                    }
                    if(jsonArray.length()==0) {
                        tvSinReservas.setVisibility(View.VISIBLE);
                        list.setVisibility(View.INVISIBLE);
                        tvTitulo.setVisibility(View.INVISIBLE);
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
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    private boolean tieneConexionInternet() {
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}