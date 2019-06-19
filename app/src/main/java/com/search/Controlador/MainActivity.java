package com.search.Controlador;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick_Registrar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Hola Mundo").setTitle("Mensaje");

        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
