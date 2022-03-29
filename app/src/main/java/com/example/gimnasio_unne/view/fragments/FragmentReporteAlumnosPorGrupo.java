package com.example.gimnasio_unne.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.AdministradorActivity;
import com.example.gimnasio_unne.GenerarPDF;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.Utiles;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//reporte en base a alumnos por grupo de todos los años
public class FragmentReporteAlumnosPorGrupo extends Fragment {

    private BarChart barChart;
    private String URL="https://medinamagali.com.ar/gimnasio_unne/consulta_alumnos_por_grupo.php";
    private String total_reservas;
    private String[] grupos;
    private Button btnGenerar;
    private ProgressBar progressBar;
    Button btnDesde, btnHasta, btnFiltrar;
    DatePickerDialog datePickerDialog, datePickerDialog2;
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
        btnDesde=view.findViewById(R.id.btnDsdGrupos);
        btnHasta=view.findViewById(R.id.btnHstGrupos);
        btnFiltrar=view.findViewById(R.id.btnFiltrarGrupos);
        progressBar=view.findViewById(R.id.progressBarReportePorGrupo);

        //Permisos
        if(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},1000);
        }

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

        //Genera el documento
        GenerarPDF generarPDF = new GenerarPDF();
        if (tieneConexionInternet()) {
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
                            if(date1.before(date2)) {
                                mostrarDatos(desde, hasta);
                                limpiaGraficoBarra();
                            }
                            else
                                Toast.makeText(getActivity(), "Fecha hasta debe ser mayor a fecha desde", Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } // fin else
                }
            }); //FIL BOTON FILTRAR

            btnGenerar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barChart.buildDrawingCache();
                    Bitmap bm = barChart.getDrawingCache(); //convertir graph a bitmap
                    generarPDF.crearPDF(bm);
                    Toast.makeText(getActivity().getApplicationContext(), "Se generó el PDF en la carpeta de Descargas", Toast.LENGTH_SHORT).show();
                }
            });
            //FIN SI TIENE CONEXION
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                    "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void mostrarDatos(String desde, String hasta) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray jsonArray = new JSONArray(response);
                    grupos= new String[jsonArray.length()];
                    for (int i=0;i<jsonArray.length();i++) {
                        total_reservas = jsonArray.getJSONObject(i).getString("total_reservas");
                        grupos[i] = jsonArray.getJSONObject(i).getString("descripcion") ;
                        entradas.add(new BarEntry(i, Integer.parseInt(total_reservas)));
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

    private void limpiaGraficoBarra() {
        //Elimina datos
        entradas.clear();
        //mandamos los datos para crear la gráfica

        BarDataSet datos = new BarDataSet(entradas ,"");
        BarData data = new BarData(datos);
        barChart.setData(data);

        barChart.invalidate();
        barChart.clear();

    }

    private void legend(BarChart barChart) {
        Legend legend = barChart.getLegend();
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

    private boolean tieneConexionInternet() {
        ConnectivityManager con = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            return true;
        }
        return false;
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