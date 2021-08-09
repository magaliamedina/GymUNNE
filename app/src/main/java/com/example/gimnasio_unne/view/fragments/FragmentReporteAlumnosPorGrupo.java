package com.example.gimnasio_unne.view.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

//reporte en base a alumnos por grupo en el mes o de año?
public class FragmentReporteAlumnosPorGrupo extends Fragment {

    private BarChart barChart;
    private String url="https://medinamagali.com.ar/gimnasio_unne/consulta_alumnos_por_grupo.php";

    public FragmentReporteAlumnosPorGrupo() {  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_reporte_alumnos_por_grupo, container, false);
        barChart=view.findViewById(R.id.barChartAlumnosPorGrupo);
        mostrarDatos();
        return view;
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.d("VOLLEY", response);
                    JSONArray jsonArray = new JSONArray(response);
                    /*masculino = jsonArray.getJSONObject(0).getString("masculino");
                    femenino = jsonArray.getJSONObject(1).getString("femenino");
                    otros = jsonArray.getJSONObject(2).getString("otros");*/
                    //Log.d("pepe",masculino);
                    crearGrafico();
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

    private void crearGrafico() {
        Description description = new Description();
        description.setText("Cantidad de alumnos por grupos");
        description.setTextSize(15);
        barChart.setDescription(description);

        final ArrayList<BarEntry> barEntries=new ArrayList<>();

        /*barEntries.add(new PieEntry(Float.parseFloat(masculino),"Masculino")); //para la leyenda
        barEntries.add(new PieEntry(Float.parseFloat(femenino),"Femenino"));
        barEntries.add(new PieEntry(Float.parseFloat(otros),"Otros"));

        PieDataSet pieDataSet=new PieDataSet(pieEntries,""); //leyenda
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(20);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new PercentFormatter()); //agregar porcentaje

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(15);
        legend.setFormSize(20);//tamaño del ciculo de la leyenda
        //legend.setFormToTextSpace(2);

        PieData pieData= new PieData(pieDataSet);
        pieChart.setData(pieData);*/
    }
}