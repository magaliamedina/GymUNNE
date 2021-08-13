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


public class FragmentReportePorFacultad extends Fragment {

    private PieChart pieChart;
    String arquitectura, artes, agrarias, economicas, exactas, veterinarias, derecho,
            humanidades, ingenieria, medicina, odontologia, criminalistica;
    String url="https://medinamagali.com.ar/gimnasio_unne/consulta_alumnos_facultad.php";
    private int[] COLORS =new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.BLACK, Color.MAGENTA, Color.CYAN,
            Color.GRAY,Color.parseColor("#963DD3"), Color.parseColor("#EA741D"), -65444, Color.parseColor("#5A8C20"), Color.parseColor("#C3C93E")};

    public FragmentReportePorFacultad() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_reporte_por_facultad, container, false);
        pieChart=view.findViewById(R.id.pieChartAlumnosPorFacultad);
        if (tieneConexionInternet())
            mostrarDatos();
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

    private void crearGraficoPastel(String arquitectura, String artes, String agrarias, String economicas,
                                    String exactas, String veterinarias, String derecho,String humanidades,
                                    String ingenieria, String medicina, String odontologia, String criminalistica) {
        Description description = new Description();
        description.setText("Porcentaje de alumnos por facultad");
        description.setTextSize(15);
        pieChart.setDescription(description);
        final ArrayList<PieEntry> pieEntries=new ArrayList<>();
        pieEntries.add(new PieEntry(Float.parseFloat(arquitectura),"Arquitectura"));
        pieEntries.add(new PieEntry(Float.parseFloat(artes),"Artes"));
        pieEntries.add(new PieEntry(Float.parseFloat(agrarias),"Agrarias"));
        pieEntries.add(new PieEntry(Float.parseFloat(economicas),"Economicas"));
        pieEntries.add(new PieEntry(Float.parseFloat(exactas),"Exactas"));
        pieEntries.add(new PieEntry(Float.parseFloat(veterinarias),"Veterinarias"));
        pieEntries.add(new PieEntry(Float.parseFloat(derecho),"Derecho"));
        pieEntries.add(new PieEntry(Float.parseFloat(humanidades),"Humanidades"));
        pieEntries.add(new PieEntry(Float.parseFloat(ingenieria),"Ingenieria"));
        pieEntries.add(new PieEntry(Float.parseFloat(medicina),"Medicina"));
        pieEntries.add(new PieEntry(Float.parseFloat(odontologia),"Odontologia"));
        pieEntries.add(new PieEntry(Float.parseFloat(criminalistica),"Criminalistica"));

        PieDataSet pieDataSet=new PieDataSet(pieEntries,"");
        pieDataSet.setColors(COLORS);
        pieDataSet.setValueTextSize(20);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new PercentFormatter()); //agregar porcentaje

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(15);
        legend.setFormSize(20);//tamaño del ciculo de la leyenda
        pieChart.getLegend().setWordWrapEnabled(true);

        PieData pieData= new PieData(pieDataSet);
        pieChart.setData(pieData);
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.d("VOLLEYFacultad", response);
                    JSONArray jsonArray = new JSONArray(response);
                    arquitectura = jsonArray.getJSONObject(0).getString("arquitectura");
                    artes = jsonArray.getJSONObject(1).getString("artes");
                    agrarias = jsonArray.getJSONObject(2).getString("agrarias");
                    economicas = jsonArray.getJSONObject(3).getString("economicas");
                    exactas = jsonArray.getJSONObject(4).getString("exactas");
                    veterinarias = jsonArray.getJSONObject(5).getString("veterinarias");
                    derecho = jsonArray.getJSONObject(6).getString("derecho");
                    humanidades = jsonArray.getJSONObject(7).getString("humanidades");
                    ingenieria = jsonArray.getJSONObject(8).getString("ingenieria");
                    medicina = jsonArray.getJSONObject(9).getString("medicina");
                    odontologia = jsonArray.getJSONObject(10).getString("odontologia");
                    criminalistica = jsonArray.getJSONObject(11).getString("criminalistica");

                    crearGraficoPastel(arquitectura, artes, agrarias, economicas, exactas, veterinarias, derecho,
                            humanidades, ingenieria, medicina, odontologia, criminalistica);
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