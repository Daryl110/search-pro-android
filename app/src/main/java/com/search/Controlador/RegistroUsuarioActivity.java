package com.search.Controlador;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RegistroUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
    }

    public void cerrarActivity(View view){
        this.finish();
    }

    public void crearCuenta(View view){
        Toast.makeText(this, "Usuario Creado!!", Toast.LENGTH_SHORT).show();
    }
}
