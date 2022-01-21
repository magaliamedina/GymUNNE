package com.example.gimnasio_unne.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.AdministradorActivity;
import com.example.gimnasio_unne.GenerarPDF;
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
import com.itextpdf.text.Image;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//reporte en base a alumnos por grupo de todos los años
public class FragmentReporteAlumnosPorGrupo extends Fragment {

    private BarChart barChart;
    private String URL="https://medinamagali.com.ar/gimnasio_unne/consulta_alumnos_por_grupo.php";
    private String total_reservas;
    private String[] grupos;
    private Button btnGenerar;
    //para el spinner
    private String[] meses = new String[] {"enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "setiembre", "octubre", "noviembre", "diciembre"};
    //creamos la lista con los valores de entrada
    List<BarEntry> entradas = new ArrayList<>();

    public FragmentReporteAlumnosPorGrupo() {  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_reporte_alumnos_por_grupo, container, false);
        barChart=view.findViewById(R.id.barChartAlumnosPorGrupo);
        btnGenerar=view.findViewById(R.id.btnGenerarPDF);
        mostrarDatos();

        //Permisos
        if(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},1000);
        }

        //Genera el documento
        GenerarPDF generarPDF = new GenerarPDF();
        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barChart.buildDrawingCache();
                Bitmap bm = barChart.getDrawingCache();
                generarPDF.crearPDF(bm);
                Toast.makeText(getActivity().getApplicationContext(), "Se generó el PDF en la carpeta de Descargas", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    grupos= new String[jsonArray.length()];
                    for (int i=0;i<jsonArray.length();i++) {
                        total_reservas = jsonArray.getJSONObject(i).getString("total_reservas");
                        grupos[i] = jsonArray.getJSONObject(i).getString("descripcion") ;
                        entradas.add(new BarEntry(i, Float.parseFloat(total_reservas)));
                    }
                    crearGraficoBarra();
                    legend(barChart); //metodo leyenda

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
        //mandamos los datos para crear la gráfica
        BarDataSet datos = new BarDataSet(entradas ,"");

        BarData data = new BarData(datos);

        //ponemos color a cada barra
        datos.setColors(ColorTemplate.COLORFUL_COLORS);
        datos.setValueTextSize(18);
        datos.setValueTextColor(Color.BLACK);

        //separacion entre las barras
        data.setBarWidth(0.45f);
        barChart.setData(data);

        barChart.setFitBars(true); //pone las barras centradas
        barChart.setDrawGridBackground(true); //las lineas que sean horizontales unicamente
        barChart.getLegend().setEnabled(false); //no mostrar las leyendas

        Description description = new Description();
        description.setText(""); //para que no muestre descripcion
        barChart.setDescription(description);

        ejeX(barChart.getXAxis());
        ejeY(barChart.getAxisRight()); //que muestre a la izquierda
        //LAS 2 siguientes lineas son para que cargue los datos sin hacer clic en el grafico
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private void legend(BarChart barChart) {
        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(20);
        legend.setFormSize(20);//tamaño del ciculo de la leyenda
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setFormToTextSpace(2);
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

    private void ejeY(YAxis axis) {
        axis.setAxisMinimum(0);
        axis.setEnabled(false); //que no aparezca la barra derecha
    }
}