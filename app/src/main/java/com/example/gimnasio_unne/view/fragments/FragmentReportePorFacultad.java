package com.example.gimnasio_unne.view.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.Utiles;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FragmentReportePorFacultad extends Fragment {

    private PieChart pieChart;
    private ProgressBar progressBar;
    String arquitectura, artes, agrarias, economicas, exactas, veterinarias, derecho,
            humanidades, ingenieria, medicina, odontologia, criminalistica;
    String url="https://medinamagali.com.ar/gimnasio_unne/consulta_alumnos_facultad.php";
    Button btnDesde, btnHasta, btnFiltrar;
    DatePickerDialog datePickerDialog, datePickerDialog2;
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
        btnDesde=view.findViewById(R.id.btnDsdFacultades);
        btnHasta=view.findViewById(R.id.btnHstFacultades);
        btnFiltrar=view.findViewById(R.id.btnFiltrarFacultades);
        progressBar=view.findViewById(R.id.progressBarReportePorFacultad);
        try {
            initDatePickerDesde();
            initDatePickerHasta();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        btnDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        btnHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog2.show();
            }
        });
        if (tieneConexionInternet()){
            mostrarDatos("2021-11-2", Utiles.obtenerFechaActual("GMT -3"));
            btnFiltrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String desde = btnDesde.getText().toString();
                    String hasta= btnHasta.getText().toString();
                    if(desde.equals("Desde") || hasta.equals("Hasta"))
                        Toast.makeText(getActivity(), "Debe ingresar fecha desde y hasta", Toast.LENGTH_SHORT).show();
                    else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                        try {
                            Date date1 = dateFormat.parse(desde);
                            Date date2 = dateFormat.parse(hasta);
                            if(date1.before(date2))
                                mostrarDatos(desde, hasta);
                            else
                                Toast.makeText(getActivity(), "Fecha hasta debe ser mayor a fecha desde", Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } // fin else
                }
            });
        }
        else {
            progressBar.setVisibility(View.GONE);
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

        //personalizacion del grafico- etiquetas
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
        //LAS 2 siguientes lineas son para que cargue los datos sin hacer clic en el grafico
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    public void mostrarDatos(String desde, String hasta) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.GONE);
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("fecha_desde", desde);
                parametros.put("fecha_hasta", hasta);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    private void initDatePickerDesde() throws ParseException {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                String date= year+"-"+month+"-"+day;
                btnDesde.setText(date);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int style= AlertDialog.THEME_HOLO_LIGHT;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getActivity(), style,dateSetListener,year,month,day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long dateMillis;
        Date dateHoy;
        dateHoy= sdf.parse(Utiles.obtenerFechaActual("GMT -3"));
        dateMillis=dateHoy.getTime();
        datePickerDialog.getDatePicker().setMaxDate(dateMillis);

        datePickerDialog.getDatePicker().init(2022, 00, 01, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            }
        });
    }

    private void initDatePickerHasta() throws ParseException {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                String date= year+"-"+month+"-"+day;
                btnHasta.setText(date);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int style= AlertDialog.THEME_HOLO_LIGHT;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog2 = new DatePickerDialog(getActivity(), style,dateSetListener,year,month,day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long dateMillis;
        Date dateHoy= sdf.parse(Utiles.obtenerFechaActual("GMT -3"));
        dateMillis=dateHoy.getTime();
        datePickerDialog2.getDatePicker().setMaxDate(dateMillis);

        datePickerDialog2.getDatePicker().init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            }
        });
    }
}