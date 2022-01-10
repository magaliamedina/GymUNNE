package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.gimnasio_unne.view.Reservar;
import com.example.gimnasio_unne.model.CuposLibres;
import com.example.gimnasio_unne.view.adapter.AdaptadorCuposLibres;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentListarCuposLibres extends Fragment {
    //listar cupos para perfil estudiante
    private ListView list;
    public static ArrayList<CuposLibres> arrayCuposLibres= new ArrayList<>();
    String url = "https://medinamagali.com.ar/gimnasio_unne/listarcuposlibres.php?alumno_id="+Login.personas_id+"";

    AdaptadorCuposLibres adaptador;
    CuposLibres cuposLibres;
    TextView tvReservaRealizada;

    public FragmentListarCuposLibres() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_listar_cupos_libres, container, false);
        tvReservaRealizada=view.findViewById(R.id.tvReservaRealizada);
        list = view.findViewById(R.id.lvListarCuposLibres);
        ImageView imgSinConexion=view.findViewById(R.id.imgSinConexion);
        TextView tvSinConexion1=view.findViewById(R.id.tv_sinConexion1);
        TextView tvSinConexion2=view.findViewById(R.id.tv_sinConexion2);
        imgSinConexion.setVisibility(View.INVISIBLE);
        tvSinConexion1.setVisibility(View.INVISIBLE);
        tvSinConexion2.setVisibility(View.INVISIBLE);

        ConnectivityManager con = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            //cargar el webservice
            adaptador= new AdaptadorCuposLibres(getActivity().getApplicationContext(), arrayCuposLibres);
            list.setAdapter(adaptador);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    CharSequence[] dialogoItem={"Reservar"};
                    //titulo del alert dialog
                    builder.setTitle(arrayCuposLibres.get(position).getGrupo_descripcion());
                    builder.setItems(dialogoItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            switch (i) {
                                case 0:
                                    //pasamos position para poder recibir en Reservar
                                    startActivity(new Intent(getActivity().getApplicationContext(), Reservar.class)
                                            .putExtra("position",position));
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            }); //fin list.setOnItemClickListener
            mostrarDatos();
        }
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

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayCuposLibres.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess=jsonObject.getString("sucess");
                    if (sucess.equals("1")) {
                        list.setVisibility(View.VISIBLE);
                        JSONArray jsonArray=jsonObject.getJSONArray("cuposlibres");
                        for (int i=0;i<jsonArray.length();i++) {
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
                    if(sucess.equals("2")) {
                        tvReservaRealizada.setVisibility(View.VISIBLE);
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
