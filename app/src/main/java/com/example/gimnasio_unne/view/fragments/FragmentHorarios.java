package com.example.gimnasio_unne.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.gimnasio_unne.AdministradorActivity;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Horarios;
import com.example.gimnasio_unne.view.AltaHorario;
import com.example.gimnasio_unne.view.adapter.AdaptadorHorarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentHorarios extends Fragment {
    private ListView list;
    private ProgressBar progressBar;
    AdaptadorHorarios adaptador;
    public static ArrayList<Horarios> horariosArrayList= new ArrayList<>();
    String url="https://medinamagali.com.ar/gimnasio_unne/listar_horarios.php";
    Horarios horarios;
    public FragmentHorarios() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_horarios, container, false);
        list = view.findViewById(R.id.listview);
        progressBar=view.findViewById(R.id.progressBarHorarios);
        //sin internet
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);

        if(tieneConexionInternet()) { //INICIO IF
            FloatingActionButton fab = view.findViewById(R.id.fabHorarios);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity().getApplication(), AltaHorario.class);
                    startActivity(intent);
                }
            });

            adaptador = new AdaptadorHorarios(getActivity().getApplicationContext(), horariosArrayList);
            list.setAdapter(adaptador);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    View mView = getLayoutInflater().inflate(R.layout.modificar_horario, null);
                    builder.setTitle("Modificar horario: de " + horariosArrayList.get(position).getHoraInicio() + " a " + horariosArrayList.get(position).getHoraFin());
                    final EditText etHoraFin = mView.findViewById(R.id.etHoraFinModificarHorario);
                    final Button btnCambiarEstado = mView.findViewById(R.id.btnModificarEstado);
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(tieneConexionInternet()) { //INICIO IF
                                String horaFin = etHoraFin.getText().toString();
                                if (horaFin.isEmpty()) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Ingrese una hora de fin", Toast.LENGTH_SHORT).show();
                                } else {
                                    String nueva_horafin = etHoraFin.getText().toString();
                                    cambiarEstado(horariosArrayList.get(position).getId(), horariosArrayList.get(position).getEstado(), nueva_horafin);
                                    Toast.makeText(getActivity().getApplicationContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                                }
                            } //fin if tiene conexion
                            else {
                                Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                                        "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    btnCambiarEstado.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(tieneConexionInternet()) { //INICIO IF
                                String p_estado;
                                if (horariosArrayList.get(position).getEstado().equals("1")) {
                                    p_estado = "0";
                                } else {
                                    p_estado = "1";
                                }
                                cambiarEstado(horariosArrayList.get(position).getId(), p_estado, horariosArrayList.get(position).getHoraFin());
                            } //FIN if tieneconexion
                            else {
                                Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                                        "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setView(mView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            mostrarDatos();
        } //FIN IF TIENE CONEXION
        else{
            //mensaje de no hay internet
            progressBar.setVisibility(View.GONE);
            imgSinConexion.setVisibility(View.VISIBLE);
            tvSinConexion1.setVisibility(View.VISIBLE);
            tvSinConexion2.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                    "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private boolean tieneConexionInternet() {
        ConnectivityManager con = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                horariosArrayList.clear();
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i= 0; i< jsonArray.length();i++){
                        String id= jsonArray.getJSONObject(i).getString("horario_id");
                        String hora_inicio = jsonArray.getJSONObject(i).getString("hora_inicio");
                        String hora_fin = jsonArray.getJSONObject(i).getString("hora_fin");
                        String estado = jsonArray.getJSONObject(i).getString("estado");
                        horarios = new Horarios(id, hora_inicio,hora_fin,  estado);
                        horariosArrayList.add(horarios);
                        adaptador.notifyDataSetChanged();
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

    public void cambiarEstado(final String id, final String p_estado, final String hora_fin) {
        StringRequest request=new StringRequest(Request.Method.POST, "https://medinamagali.com.ar/gimnasio_unne/update_horario.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Modificado exitosamente", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), AdministradorActivity.class);
                startActivity(intent);
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
                params.put("horario_id", id);
                params.put("estado", p_estado);
                params.put("hora_fin", hora_fin);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }
}
