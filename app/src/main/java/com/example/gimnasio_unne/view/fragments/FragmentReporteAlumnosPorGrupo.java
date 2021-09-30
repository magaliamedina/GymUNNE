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
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

//reporte en base a alumnos por grupo en el mes o de año?
public class FragmentReporteAlumnosPorGrupo extends Fragment {

    private BarChart barChart;
    private String url="https://medinamagali.com.ar/gimnasio_unne/consulta_alumnos_por_grupo.php";
    private String grupo3, grupo4;
    private String[] grupos= new String[]{"Grupo 3", "Grupo 4", "Grupo 88", "Grupo 99"};

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
                    //Log.d("ingresa", response);
                    JSONArray jsonArray = new JSONArray(response);
                    grupo4 = jsonArray.getJSONObject(0).getString("grupo4");
                    grupo3 = jsonArray.getJSONObject(1).getString("grupo3");
                    crearGraficoBarra();
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

    private void crearGraficoBarra() {
        //creamos la lista con los valores de entrada
        List<BarEntry> entradas = new ArrayList<>();
        entradas.add(new BarEntry(0, Float.parseFloat(grupo4)));
        entradas.add(new BarEntry(1, Float.parseFloat(grupo3)));
        entradas.add(new BarEntry(2,6));
        entradas.add(new BarEntry(3,1));

        //mandamos los datos para crear la gráfica
        BarDataSet datos = new BarDataSet(entradas ,"");

        BarData data = new BarData(datos);

        //ponemos color a cada barra
        datos.setColors(ColorTemplate.COLORFUL_COLORS);
        datos.setValueTextSize(20);
        datos.setValueTextColor(Color.WHITE);

        //separacion entre las barras
        data.setBarWidth(0.9f);

        barChart.setData(data);

        //pone las barras centradas
        barChart.setFitBars(true);

        /*Description description = new Description();
        description.setText("Cantidad de alumnos por grupos");
        description.setTextSize(15);
        barChart.setDescription(description);*/

        //legend(barChart); //metodo leyenda
        ejeX(barChart.getXAxis());
    }

    private void legend(BarChart barChart) {
        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(15);
        legend.setFormSize(20);//tamaño del ciculo de la leyenda
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //legend.setFormToTextSpace(2);
        //datos que van a ir en la leyenda
        ArrayList<LegendEntry>entries = new ArrayList<>();
        for(int i=0; i< grupos.length;i++) {
            LegendEntry entry = new LegendEntry();
            entry.label= grupos[i];
            entries.add(entry);
        }
        legend.setCustom(entries);
    }

    private void ejeX(XAxis axis) {
        axis.setGranularityEnabled(true);
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setValueFormatter(new IndexAxisValueFormatter(grupos));
    }
}