package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.Login;
import com.example.gimnasio_unne.PersonalActivity;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Reservas;
import com.example.gimnasio_unne.view.adapter.AdaptadorReservas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentReservasPendientes extends Fragment {

    private ListView list;
    private ProgressBar progressBar;
    public static ArrayList<Reservas> arrayReservas= new ArrayList<>();
    //reservas pendientes
    String url = "https://medinamagali.com.ar/gimnasio_unne/listar_reservas.php";
    String urlReserva = "https://medinamagali.com.ar/gimnasio_unne/modificar_reserva.php";
    AdaptadorReservas adaptador;
    Reservas reservas;
    String id_cupolibre, total_cupolibre;
    TextView tvSinReservasPendientes;
    public FragmentReservasPendientes(){}

    @Override
    public void onResume() {
        super.onResume();
        mostrarDatos();
        adaptador.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservas_pendientes, container, false);
        list = view.findViewById(R.id.lvListarReservas);
        progressBar=view.findViewById(R.id.progressBarReservasPendientes);
        tvSinReservasPendientes= view.findViewById(R.id.tvSinReservasPendientes);
        //sin conexion a internet
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);

        if(tieneConexionInternet()) {
            //mostrar datos
            adaptador = new AdaptadorReservas(getActivity().getApplicationContext(), arrayReservas);
            list.setAdapter(adaptador);
            //mostrarDatos();
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    int which_item = position;
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    //String confirmar_reserva = "Confirmar reserva";
                   // String cancelar_reserva = "Cancelar reserva";
                    //CharSequence[] dialogoItem = {confirmar_reserva, cancelar_reserva};
                    CharSequence[] dialogoItem = {
                            Html.fromHtml("<font color='#1D9619'><b>Confirmar reserva</b></font>"),
                            Html.fromHtml("<font color='#FF0000'><b>Cancelar reserva</b></font>")};
                    //titulo del alert dialog
                    //builder.setTitle(Html.fromHtml("<font color='#FF0000'>Reserva n??mero <b>250</b></font>"));
                    builder.setTitle("Reserva N??" + arrayReservas.get(position).getId_reserva());
                    builder.setItems(dialogoItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            switch (i) {
                                case 0:
                                    //ESTADO CONFIRMADO: modificamos el estado en la base de datos
                                    if(tieneConexionInternet()) {
                                        confirmarCancelarReserva(arrayReservas.get(position).getId_reserva(), "1", "Reserva confirmada exitosamente", total_cupolibre);
                                        //para eliminar el elemento del arreglo y refrescar la lista
                                        adaptador.notifyDataSetChanged();
                                        arrayReservas.remove(which_item);
                                    } else {
                                        Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                                                "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 1:
                                    if (tieneConexionInternet()) {
                                        //ESTADO CANCELADO: cambiamos el estado o borramos de la bd?
                                        Integer total = Integer.parseInt(total_cupolibre);
                                        total = total + 1;
                                        String totalCupoLibreString = total + "";
                                        confirmarCancelarReserva(arrayReservas.get(position).getId_reserva(), "2", "Reserva cancelada", totalCupoLibreString);
                                        //para eliminar el elemento del arreglo y refrescar la lista
                                        adaptador.notifyDataSetChanged();
                                        arrayReservas.remove(which_item);
                                    } else {
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
            adaptador.notifyDataSetChanged();
        } // cierre de si hay internet
        else {
            // no hay internet
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
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                arrayReservas.clear();
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess=jsonObject.getString("sucess");
                    JSONArray jsonArray=jsonObject.getJSONArray("listarreservas");
                    if (sucess.equals("1")) {
                        list.setVisibility(View.VISIBLE);
                        //tvSinReservasPendientes.setVisibility(View.INVISIBLE);
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject object= jsonArray.getJSONObject(i);
                            String reserva_id= object.getString("reserva_id");
                            String fecha_reserva = object.getString("reserva_fecha");
                            String nombres = object.getString("estudiante_nombre");
                            String apellido = object.getString("estudiante_apellido");
                            String grupo_descripcion= object.getString("grupo_descripcion");
                            String hora_inicio = object.getString("horarios_hora_inicio");
                            String hora_fin = object.getString("horarios_hora_fin");
                            id_cupolibre= object.getString("cupolibre_id");
                            total_cupolibre= object.getString("total_cupolibre");
                            reservas = new Reservas(reserva_id, fecha_reserva, nombres+" " + apellido,
                                    grupo_descripcion,"de "+hora_inicio+" a " +  hora_fin);
                            arrayReservas.add(reservas);
                            adaptador.notifyDataSetChanged();
                        }
                    }
                    if(jsonArray.length()==0) {
                        tvSinReservasPendientes.setVisibility(View.VISIBLE);
                        list.setVisibility(View.INVISIBLE);
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

    private void confirmarCancelarReserva(final String p_idReserva, final String p_estado, final String mensaje, final String p_totalcupolibre) {
        StringRequest stringRequest= new StringRequest(Request.Method.POST, urlReserva, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("reserva_id", p_idReserva);
                parametros.put("estado", p_estado);
                parametros.put("cupolibre_id", id_cupolibre);
                parametros.put("total_cupolibre", p_totalcupolibre);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
