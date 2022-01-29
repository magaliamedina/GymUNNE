package com.example.gimnasio_unne;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText edtUsuario, edtPassword;
    Button btnLogin, btnCallPhone, btnSendMail;
    public static String usuario = "", password = "", personas_id = "", apellido = "", nombres = "", lu = "",
            sexo="", pcia="", estadocivil="", dni="", email="", facultad="";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    CheckBox cbRecordarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsuario = findViewById(R.id.txt_emailLogin);
        edtPassword = findViewById(R.id.txt_passLogin);
        btnLogin = findViewById(R.id.btn_iniciarsesion);
        btnCallPhone=findViewById(R.id.btnCallPhone);
        btnSendMail=findViewById(R.id.btnSendEmail);
        cbRecordarUsuario = findViewById(R.id.cbRecordarUsuario);

        inicializarElementos();
        if (revisarSesion()) {
            if (revisarUsuario().equals("1")) { //PERFIL ADMINISTRADOR
                Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
                startActivity(intent);
            }
            if (revisarUsuario().equals("3")) { //PERFIL ESTUDIANTE
                Intent intent = new Intent(getApplicationContext(), AlumnoActivity.class);
                startActivity(intent);
            }
            if (revisarUsuario().equals("4")) { //PERFIL PERSONAL ADMINISTRATIVO
                Intent intent = new Intent(getApplicationContext(), PersonalActivity.class);
                startActivity(intent);
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = edtUsuario.getText().toString();
                password = edtPassword.getText().toString();
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    //cargar web service
                    if (!usuario.isEmpty() && !password.isEmpty()) {
                        validarUsuario("https://medinamagali.com.ar/gimnasio_unne/validar_usuario.php");
                    } else {
                        edtUsuario.setError("Ingrese usuario");
                        edtPassword.setError("Ingrese contraseña");
                    }
                }
                else {
                    // no hay internet
                    Toast.makeText(Login.this, "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validarUsuario(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { //nos devuelve la fila encontrada en el servicio web
                if (!response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String estado = jsonObject.getString("estado");
                        String usuario_id = jsonObject.getString("usuario_id");
                        personas_id = jsonObject.getString("personas_id");
                        apellido = jsonObject.getString("apellido");
                        nombres = jsonObject.getString("nombres");
                        lu = jsonObject.getString("lu");
                        //agregado para mi perfil
                        dni= jsonObject.getString("dni");
                        sexo =jsonObject.getString("sexo_id");
                        pcia= jsonObject.getString("provincia");
                        estadocivil= jsonObject.getString("estado_civil");
                        email= jsonObject.getString("email");
                        facultad= jsonObject.getString("facultad_id");

                        if (estado.equals("0")){
                            Toast.makeText(Login.this, "Usuario dado de baja. Consulte al correo electrónico", Toast.LENGTH_LONG).show();
                        } else {
                            guardarSharedPreferences();
                            edtUsuario.setText("");
                            edtPassword.setText("");
                            guardarSesion(cbRecordarUsuario.isChecked(), usuario_id);
                            //PERFIL ADMINISTRADOR
                            if (usuario_id.equals("1")) {
                                finish();
                                Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
                                startActivity(intent);
                            }
                            //PERFIL ESTUDIANTE
                            else if (usuario_id.equals("3")) {
                                finish();
                                Intent intent = new Intent(getApplicationContext(), AlumnoActivity.class);
                                startActivity(intent);
                            }
                            //PERFIL PERSONAL ADMINISTRATIVO
                            else if (usuario_id.equals("4")) {
                                finish();
                                Intent intent = new Intent(getApplicationContext(), PersonalActivity.class);
                                startActivity(intent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show(); //recomendable luego cambiar con un mensaje para el usuario
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("email", edtUsuario.getText().toString());
                parametros.put("password", edtPassword.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this); //instancia de la cola de peticiones de Volley
        requestQueue.add(stringRequest);
    }

    private void inicializarElementos() {
        preferences = this.getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    private boolean revisarSesion() {
        //si devulve false no hace nada
        //si devuelve true va la vista principal
        return this.preferences.getBoolean("sesion", false);
    }

    private String revisarUsuario() {
        return this.preferences.getString("tipo_usuario", "");
    }

    private void guardarSesion(boolean checked, String tipousuario) {
        editor.putBoolean("sesion", checked);
        editor.putString("tipo_usuario", tipousuario);
        editor.apply();
    }

    public void guardarSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("datosusuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nya", nombres + " " + apellido);
        editor.putString("lu", lu);
        editor.putString("personas_id", personas_id);
        //agregados para mi perfil
        editor.putString("dni", dni);
        editor.putString("sexo", sexo);
        editor.putString("provincia", pcia);
        editor.putString("estado_civil", estadocivil);
        editor.putString("email", email);
        editor.putString("facultad_id", facultad);

        editor.apply();
    }

    public void onClickLlamada(View v) {
        int numero=4439627;
        startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + numero)));
    }

    public void onClickEmail(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","secretaria.sociales.unne@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Gimnasio APP - ");
        startActivity(Intent.createChooser(emailIntent,  "Enviar email"));
    }

}
