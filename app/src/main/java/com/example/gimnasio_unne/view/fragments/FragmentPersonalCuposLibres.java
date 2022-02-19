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
import com.example.gimnasio_unne.PersonalActivity;
import com.example.gimnasio_unne.view.EditarCupoLibre;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.CuposLibres;
import com.example.gimnasio_unne.view.ReservasConfirmadas;
import com.example.gimnasio_unne.view.adapter.AdaptadorCuposLibres;
import com.example.gimnasio_unne.view.adapter.AdaptadorPersonalCupos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentPersonalCuposLibres extends Fragment {
    private ListView list;
    private ProgressBar progressBar;
    private Button btn;
    public static ArrayList<CuposLibres> arrayCuposLibres= new ArrayList<>();
    String url = "https://medinamagali.com.ar/gimnasio_unne/listarcuposlibres_personal.php";
    String url_limpiar_cupos = "https://medinamagali.com.ar/gimnasio_unne/limpiar_cupos.php";
    AdaptadorPersonalCupos adaptador;
    CuposLibres cuposLibres;
    public FragmentPersonalCuposLibres() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_personal_cupos_libres, container, false);
        list = view.findViewById(R.id.lvPersonalListarCuposLibres);
        progressBar=view.findViewById(R.id.progressBarPersonalCupos);
        btn = view.findViewById(R.id.btn_limpiar_cupos);
        //sin internet
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);

        if(tieneConexionInternet()) {
            //cargar el web service
            adaptador = new AdaptadorPersonalCupos(getActivity().getApplicationContext(), arrayCuposLibres);
            list.setAdapter(adaptador);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    CharSequence[] dialogoItem = {"Ver alumnos inscriptos", "Editar cupo", "Dar de baja"};
                    //titulo del alert dialog
                    builder.setTitle(arrayCuposLibres.get(position).getGrupo_descripcion());
                    builder.setItems(dialogoItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            switch (i) {
                                case 0:
                                    //pasamos position para poder recibir en ReservasConfirmadas
                                    if(tieneConexionInternet()) {
                                        startActivity(new Intent(getActivity().getApplicationContext(), ReservasConfirmadas.class)
                                                .putExtra("position", position));
                                    }
                                    else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                                                "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 1:
                                    //pasamos position para poder recibir en EditarCupolibre
                                    if (tieneConexionInternet()) {
                                        startActivity(new Intent(getActivity().getApplicationContext(), EditarCupoLibre.class)
                                                .putExtra("position", position));
                                    }
                                    else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                                                "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 2:
                                    if(tieneConexionInternet())
                                        darDeBajaCupoLibre(arrayCuposLibres.get(position).getId_cupolibre());
                                    else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                                                "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            });
            mostrarDatos();

            //limpiar cupos para iniciar el mes
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Selecciona una respuesta.")
                            .setMessage("Estas seguro que desea limpiar los cupos?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            limpiarCupoMes();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }); //fin del setOnClickListenet
        }
        else {
            //mensaje de no hay internet
            progressBar.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);
            imgSinConexion.setVisibility(View.VISIBLE);
            tvSinConexion1.setVisibility(View.VISIBLE);
            tvSinConexion2.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                    "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void limpiarCupoMes() {
        /*final ProgressDialog progressDialog= new ProgressDialog(getActivity().getApplicationContext());
        progressDialog.setMessage("Cargando....");
        progressDialog.show();*/

        StringRequest request=new StringRequest(Request.Method.POST, url_limpiar_cupos, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Cupos actualizados", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity().getApplicationContext(), PersonalActivity.class));
                //progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                //progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
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
                    progressBar.setVisibility(View.GONE);
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
                            String mes = object.getString("mes");
                            String anio = object.getString("anio");

                            cuposLibres = new CuposLibres(id_cupolibre, grupo_descripcion, nombres+" " + apellido,
                                    cupolibre_total,"de "+hora_inicio+" a " +  hora_fin, id_grupo,
                                    fecha_reserva, estado, mes, anio);
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
