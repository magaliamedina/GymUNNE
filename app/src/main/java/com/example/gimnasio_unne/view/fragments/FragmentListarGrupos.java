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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.view.adapter.AdaptadorGrupos;
import com.example.gimnasio_unne.view.AltaGrupo;
import com.example.gimnasio_unne.view.DetallesGrupo;
import com.example.gimnasio_unne.view.EditarGrupos;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Grupos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentListarGrupos extends Fragment {

    private ListView list;
    AdaptadorGrupos adaptador;
    public static ArrayList<Grupos>groups= new ArrayList<>();
    String url="https://medinamagali.com.ar/gimnasio_unne/mostrargrupos.php";
    Grupos grupos;

    public FragmentListarGrupos() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listar_grupos, container, false);
        list = view.findViewById(R.id.listview);
        //sin internet
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);

        if(tieneConexionInternet()) { //INICIO IF
            FloatingActionButton fab = view.findViewById(R.id.fabgrupos);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity().getApplication(), AltaGrupo.class);
                    startActivity(intent);
                }
            });

            adaptador = new AdaptadorGrupos(getActivity().getApplicationContext(), groups);
            list.setAdapter(adaptador);

            //items para editar, eliminar y ver detalles
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                /*ProgressBar progressBar;
                progressBar.setProgress(int 1);
                progressBar.setVisibility(View.GONE);*/
                    CharSequence[] dialogoItem = {"Ver datos", "Editar datos", "Eliminar datos"};
                    builder.setTitle(groups.get(position).getDescripcion());
                    builder.setItems(dialogoItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            switch (i) {

                                case 0:
                                    //pasamos position para poder recibir en detalles
                                    startActivity(new Intent(getActivity().getApplicationContext(), DetallesGrupo.class)
                                            .putExtra("position", position));
                                    break;
                                case 1:
                                    //pasamos position para poder recibir en editar
                                    startActivity(new Intent(getActivity().getApplicationContext(), EditarGrupos.class)
                                            .putExtra("position", position));
                                    break;
                                case 2:
                                    //cambiamos de estado al grupo
                                    darDeBajaGrupo(groups.get(position).getId());
                                    break;

                            }
                        }
                    });
                    builder.create().show();
                }
            });
            mostrarDatos();
        } //FIN IF(tieneConexionIntenet)
        else {
            //mensaje de no hay internet
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
                groups.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess=jsonObject.getString("sucess");
                    JSONArray jsonArray=jsonObject.getJSONArray("grupos");
                    if (sucess.equals("1")) {
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject object= jsonArray.getJSONObject(i);
                            String id= object.getString("grupo_id");
                            String hora_inicio = object.getString("hora_inicio");
                            String hora_fin = object.getString("hora_fin");
                            String descripcion = object.getString("descripcion");
                            String nombres = object.getString("nombres");
                            String apellido = object.getString("apellido");
                            String prof = object.getString("profesor_id");
                            String cupototal = object.getString("total_cupos");
                            String estado = object.getString("estado");
                            grupos = new Grupos(id, nombres+" " + apellido, "de "+hora_inicio+" a " +
                                    hora_fin, cupototal, descripcion, estado);
                            groups.add(grupos);
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

    public void darDeBajaGrupo(final String id) {
        StringRequest request=new StringRequest(Request.Method.POST, "https://medinamagali.com.ar/gimnasio_unne/baja_grupo.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Se dió de baja al grupo exitosamente", Toast.LENGTH_LONG).show();
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
                params.put("grupo_id", id);
                params.put("estado", "0");
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

}
