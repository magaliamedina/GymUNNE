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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.view.EditarProfesor;
import com.example.gimnasio_unne.view.adapter.AdaptadorPersonas;
import com.example.gimnasio_unne.view.AltaProfesor;
import com.example.gimnasio_unne.view.DetallesProfesor;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Personas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentListarProfesores extends Fragment implements SearchView.OnQueryTextListener{
    private ListView list;
    AdaptadorPersonas adaptador;
    //SearchView txtBuscar;
    private ProgressBar progressBar;
    public static ArrayList<Personas> persons= new ArrayList<>();
    String url="https://medinamagali.com.ar/gimnasio_unne/mostrarpersonas.php";
    public FragmentListarProfesores() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listar_profesor, container, false);
        list = view.findViewById(R.id.listviewPersonas);
        //txtBuscar= view.findViewById(R.id.txtBuscarProfesor);
        progressBar=view.findViewById(R.id.progressBarListarProf);

        //sin internet
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);

        if(tieneConexionInternet()) { //INICIO IF
            FloatingActionButton fab = view.findViewById(R.id.fabpersonas);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity().getApplication(), AltaProfesor.class);
                    startActivity(intent);
                }
            });

            adaptador = new AdaptadorPersonas(getActivity().getApplicationContext(), persons);
            list.setAdapter(adaptador);
            mostrarDatos(url);
            //txtBuscar.setOnQueryTextListener(this);

            //items para editar, eliminar y ver detalles
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    ProgressDialog progressDialog = new ProgressDialog(view.getContext());

                    CharSequence[] dialogoItem = {"Ver datos", "Editar datos", "Dar de baja"};
                    builder.setTitle(persons.get(position).getApellido() + " " + persons.get(position).getNombres());
                    builder.setItems(dialogoItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            switch (i) {
                                case 0:
                                    // pasamos position para poder recibir en detalles
                                    startActivity(new Intent(getActivity().getApplicationContext(), DetallesProfesor.class)
                                            .putExtra("position", position));
                                    break;
                                case 1:
                                    //pasamos position para poder recibir en editar
                                    startActivity(new Intent(getActivity().getApplicationContext(), EditarProfesor.class)
                                            .putExtra("position", position));
                                    break;
                                case 2:
                                    if(tieneConexionInternet())
                                        darDeBajaPersona(persons.get(position).getId());
                                    else
                                        Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                                                "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                                    break;

                            }
                        }
                    });
                    builder.create().show();
                }
            });
        } //FIN IF TIENE CONEXION
        else {
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

    public void mostrarDatos(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                persons.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess=jsonObject.getString("sucess");
                    JSONArray jsonArray=jsonObject.getJSONArray("personas");
                    if (sucess.equals("1")) {
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject object= jsonArray.getJSONObject(i);
                            String id= object.getString("personas_id");
                            String dni = object.getString("dni");
                            String apellido = object.getString("apellido");
                            String nombres = object.getString("nombres");
                            String sexo = object.getString("sexo_id");
                            String fechaNac = object.getString("fecha_nacimiento");
                            String localidad = object.getString("localidad");
                            String provincia = object.getString("provincia");
                            String estado = object.getString("estado");
                            String estadoCivil = object.getString("estado_civil");
                            String usuario_id = object.getString("usuario_id");
                            String email = object.getString("email");
                            String pass = object.getString("pass");

                            Personas personas = new Personas(id, dni, apellido, nombres, sexo, fechaNac, localidad, provincia,
                            estado, estadoCivil, usuario_id, email, pass);
                            persons.add(personas);
                            adaptador.notifyDataSetChanged();
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
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    public void darDeBajaPersona(final String id) {
        StringRequest request=new StringRequest(Request.Method.POST, "https://medinamagali.com.ar/gimnasio_unne/baja_persona.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarDatos(url);
                adaptador.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), "Se di?? de baja exitosamente", Toast.LENGTH_LONG).show();
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
                params.put("personas_id", id);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adaptador.filtrado(newText);
        return false;
    }
}
