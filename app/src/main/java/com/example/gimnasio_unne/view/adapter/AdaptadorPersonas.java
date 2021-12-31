package com.example.gimnasio_unne.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Personas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptadorPersonas extends ArrayAdapter<Personas> {

    Context context;
    List<Personas> arrayListPersons;
    List<Personas> listaOriginal;

    public AdaptadorPersonas(@NonNull Context context, List<Personas>arrayListPersons) {
        super(context, R.layout.list_personas, arrayListPersons);
        this.context = context;
        this.arrayListPersons=arrayListPersons;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(arrayListPersons);
    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_personas, null, true);
        TextView tvDNI= view.findViewById(R.id.tvDniPersona);
        TextView tvpersona = view.findViewById(R.id.tvpersona);
        TextView tvEstado = view.findViewById(R.id.tvEstadoPersona);

        tvDNI.setText("DNI: "+arrayListPersons.get(position).getDni());
        tvpersona.setText(arrayListPersons.get(position).getApellido()+" "+arrayListPersons.get(position).getNombres());
        if (arrayListPersons.get(position).getEstado().equals("0")) {
            tvEstado.setText("Inactivo");
            tvEstado.setTextColor(Color.RED);
        }else {
            tvEstado.setText("Activo");
            tvEstado.setTextColor(Color.GREEN);
        }
        return view;
    }

    public void  filtrado(final String txtBuscar) {
        int longitud= txtBuscar.length();
        if (longitud == 0) {
            arrayListPersons.clear();
            arrayListPersons.addAll(listaOriginal);
        }
        else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List <Personas> coleccion=arrayListPersons.stream()
                        .filter(i -> i.getApellido().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                arrayListPersons.clear();
                arrayListPersons.addAll(coleccion);
            }
            else {
               for (Personas p: listaOriginal){
                   if(p.getApellido().toLowerCase().contains(txtBuscar.toLowerCase())) {
                       arrayListPersons.add(p);
                   }
               }
            }
        }
        notifyDataSetChanged();
    }//fin filtrado
}
