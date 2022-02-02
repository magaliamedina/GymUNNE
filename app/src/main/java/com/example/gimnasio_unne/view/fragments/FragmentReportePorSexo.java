package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class FragmentReportePorSexo extends Fragment {

    private PieChart pieChart;
    private ProgressBar progressBar;
    String masculino, femenino;
    String url="https://medinamagali.com.ar/gimnasio_unne/consulta_sexos.php";

    public FragmentReportePorSexo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_reporte_por_sexo, container, false);
        pieChart=view.findViewById(R.id.pieChartEdadAlumnos);
        progressBar=view.findViewById(R.id.progressBarReportePorSexo);
        if(tieneConexionInternet()) {
            mostrarDatos();
        }
        else {
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

    private void crearGraficoPastel(String masculino, String femenino) {
        Description description = new Description();
        description.setText("Alumnos por género");
        description.setTextSize(15);
        pieChart.setDescription(description);

        final ArrayList<PieEntry> pieEntries=new ArrayList<>();
        pieEntries.add(new PieEntry(Float.parseFloat(masculino),"Masculino")); //para la leyenda
        pieEntries.add(new PieEntry(Float.parseFloat(femenino),"Femenino"));

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
        pieChart.setData(pieData);
        //LAS 2 siguientes lineas son para que cargue los datos sin hacer clic en el grafico
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray jsonArray = new JSONArray(response);
                    masculino = jsonArray.getJSONObject(0).getString("masculino");
                    femenino = jsonArray.getJSONObject(1).getString("femenino");
                    crearGraficoPastel(masculino, femenino);
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
