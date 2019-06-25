package com.search.Controlador;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick_Registrar(View view){
        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Hola Mundo").setTitle("Mensaje");

        AlertDialog dialog = builder.create();

        dialog.show();*/

        Intent intent = new Intent(this, RegistroUsuarioActivity.class);
        startActivityFromChild(this, intent, 200);
    }
}
