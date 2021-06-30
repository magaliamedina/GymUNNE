package com.example.gimnasio_unne.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Horarios;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentReportes extends Fragment {

    private PieChart pieChart;
    //private BarChart barChart;
    String masculino, femenino, otros, total_alumnos;
    String url="https://medinamagali.com.ar/gimnasio_unne/consulta_sexos.php";

    public FragmentReportes() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_reportes, container, false);
        pieChart=view.findViewById(R.id.pieChartEdadAlumnos);
        //barChart=view.findViewById(R.id.barChartInasistencias);
        mostrarDatos();
        return view;
    }

    private void crearGraficoPastel(String masculino, String femenino, String otros) {
        Description description = new Description();
        description.setText("grafico de pastel");
        description.setTextSize(15);
        pieChart.setDescription(description);
        final ArrayList<PieEntry> pieEntries=new ArrayList<>();
        int mas_nro =Integer.parseInt(masculino);
        int fem_nro = Integer.parseInt(femenino);
        int otros_nro = Integer.parseInt(otros);

        pieEntries.add(new PieEntry(mas_nro,3));
        pieEntries.add(new PieEntry(fem_nro,8));
        pieEntries.add(new PieEntry(otros_nro,3));

        PieDataSet pieDataSet=new PieDataSet(pieEntries,"leyenda");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData= new PieData(pieDataSet);
        pieChart.setData(pieData);
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("VOLLEY", response);
                    JSONArray jsonArray = new JSONArray(response);
                    masculino = jsonArray.getJSONObject(0).getString("masculino");
                    femenino = jsonArray.getJSONObject(1).getString("femenino");
                    otros = jsonArray.getJSONObject(2).getString("otros");
                    total_alumnos = jsonArray.getJSONObject(3).getString("total_alumnos");
                    crearGraficoPastel(masculino, femenino, otros);
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
