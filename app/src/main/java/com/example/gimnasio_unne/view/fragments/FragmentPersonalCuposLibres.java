package com.example.gimnasio_unne.view.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.view.EditarCupoLibre;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.CuposLibres;
import com.example.gimnasio_unne.view.ReservasConfirmadas;
import com.example.gimnasio_unne.view.adapter.AdaptadorCuposLibres;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentPersonalCuposLibres extends Fragment {
    private ListView list;
    public static ArrayList<CuposLibres> arrayCuposLibres= new ArrayList<>();
    String url = "https://medinamagali.com.ar/gimnasio_unne/listarcuposlibres_personal.php";
    AdaptadorCuposLibres adaptador;
    CuposLibres cuposLibres;
    public FragmentPersonalCuposLibres() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_listar_cupos_libres, container, false);
        list = view.findViewById(R.id.lvListarCuposLibres);
        adaptador= new AdaptadorCuposLibres(getActivity().getApplicationContext(), arrayCuposLibres);
        list.setAdapter(adaptador);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                CharSequence[] dialogoItem={"Ver alumnos inscriptos","Editar cupo libre", "Dar de baja cupo libre"};
                //titulo del alert dialog
                builder.setTitle(arrayCuposLibres.get(position).getGrupo_descripcion());
                builder.setItems(dialogoItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i) {
                            case 0:
                                //pasamos position para poder recibir en ReservasConfirmadas
                                startActivity(new Intent(getActivity().getApplicationContext(), ReservasConfirmadas.class)
                                        .putExtra("position",position));
                                break;
                            case 1:
                                //pasamos position para poder recibir en EditarCupolibre
                                startActivity(new Intent(getActivity().getApplicationContext(), EditarCupoLibre.class)
                                        .putExtra("position",position));
                                break;
                            case 2:
                                darDeBajaCupoLibre(arrayCuposLibres.get(position).getId_cupolibre());
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        mostrarDatos();
        return view;
    }

    public void darDeBajaCupoLibre(final String id) {
        StringRequest request=new StringRequest(Request.Method.POST, "https://medinamagali.com.ar/gimnasio_unne/baja_cupolibre.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Se dió de baja exitosamente", Toast.LENGTH_LONG).show();
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
                params.put("cupolibre_id", id);
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
                arrayCuposLibres.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess=jsonObject.getString("sucess");
                    JSONArray jsonArray=jsonObject.getJSONArray("cuposlibres");
                    if (sucess.equals("1")) {
                        for (int i=0;i<jsonArray.length();i++) {
                            list.setVisibility(View.VISIBLE);
                            JSONObject object= jsonArray.getJSONObject(i);
                            String id_cupolibre= object.getString("id_cupolibre");
                            String grupo_descripcion= object.getString("grupo_descripcion");
                            String nombres = object.getString("personas_nombre");
                            String apellido = object.getString("personas_apellido");
                            String cupolibre_total = object.getString("cupolibre_total");
                            String hora_inicio = object.getString("horarios_hora_inicio");
                            String hora_fin = object.getString("horarios_hora_fin");
                            String id_grupo = object.getString("grupo_id");
                            String fecha_reserva = object.getString("fecha");
                            String estado = object.getString("estado");
                            cuposLibres = new CuposLibres(id_cupolibre, grupo_descripcion, nombres+" " + apellido,
                                    cupolibre_total,"de "+hora_inicio+" a " +  hora_fin, id_grupo, fecha_reserva, estado);
                            arrayCuposLibres.add(cuposLibres);
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
}
