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


public class FragmentReportePorSexo extends Fragment {

    private PieChart pieChart;
    private ProgressBar progressBar;
    Button btnDesde, btnHasta, btnFiltrar;
    DatePickerDialog datePickerDialog, datePickerDialog2;
    String masculino, femenino;
    String url="https://medinamagali.com.ar/gimnasio_unne/consulta_sexos.php";

    public FragmentReportePorSexo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_reporte_por_sexo, container, false);
        pieChart=view.findViewById(R.id.pieChartEdadAlumnos);
        btnDesde=view.findViewById(R.id.btnDsdSexo);
        btnHasta=view.findViewById(R.id.btnHstSexo);
        btnFiltrar=view.findViewById(R.id.btnFiltrarSexo);
        progressBar=view.findViewById(R.id.progressBarReportePorSexo);
        progressBar.setVisibility(View.VISIBLE);

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
        if(tieneConexionInternet()) {
            progressBar.setVisibility(View.GONE);
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

    public void mostrarDatos(String desde, String hasta) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
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
        Date dateHoy;
        dateHoy= sdf.parse(Utiles.obtenerFechaActual("GMT -3"));
        dateMillis=dateHoy.getTime();
        datePickerDialog2.getDatePicker().setMaxDate(dateMillis);

        datePickerDialog2.getDatePicker().init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            }
        });
    }

}
