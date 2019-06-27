package com.search.Controlador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static FirebaseFirestore db;
    public static FirebaseAuth auth;

    private EditText txtNombreUsuario, txtPassword;
    private ProgressBar prbBarraEspera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.db = FirebaseFirestore.getInstance();
        MainActivity.auth = FirebaseAuth.getInstance();

        this.txtNombreUsuario = findViewById(R.id.txtNombreUsuario_main);
        this.txtPassword = findViewById(R.id.txtPassword_main);
        this.prbBarraEspera = findViewById(R.id.prbBarraEspera_main);
    }

    public void onClick_Ingresar(View view){
        if (this.txtNombreUsuario.getText().toString().trim().isEmpty() ||
            this.txtPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Debe ingresar usuario y contraseña para poder ingresar", Toast.LENGTH_SHORT).show();
            return;
        }

        final String nombreUsuario = this.txtNombreUsuario.getText().toString().trim();
        final String password = this.txtPassword.getText().toString().trim();

        MainActivity.db.collection("usuarios")
                .document(this.txtNombreUsuario.getText().toString().trim())
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documento) {
                                try{
                                    if (new String(Base64.decode(documento.get("password").toString().trim(), Base64.DEFAULT))
                                            .equals(password)){

                                        MainActivity.auth.signInWithEmailAndPassword(documento.get("correo").toString().trim(), password)
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<AuthResult>() {
                                                            @Override
                                                            public void onSuccess(AuthResult authResult) {
                                                                FirebaseUser user = MainActivity.auth.getCurrentUser();
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                                                                alert.setTitle("Mensaje").setCancelable(false);

                                                                if (user != null && !user.isEmailVerified()){

                                                                    alert.setMessage("Usted no ha verificado su correo, \n" +
                                                                            "por favor verifiquelo para poder iniciar sesión.")
                                                                            .setPositiveButton("ACEPTAR",
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            MainActivity.auth.signOut();
                                                                                        }
                                                                                    });
                                                                }else if(user != null){
                                                                    alert.setMessage("Sesión iniciada con exito.")
                                                                            .setPositiveButton("ACEPTAR",
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            // Session iniciada
                                                                                        }
                                                                                    });
                                                                }else {
                                                                    alert.setMessage("No se ha podido encontrar el usuario.")
                                                                            .setPositiveButton("ACEPTAR",
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            MainActivity.auth.signOut();
                                                                                        }
                                                                                    });
                                                                }

                                                                Dialog dialog = alert.create();
                                                                dialog.show();
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                MainActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                )
                                                .addOnFailureListener(
                                                        new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(MainActivity.this,
                                                                        "Hubo un fallo al iniciar sesión contacte con el " +
                                                                                "administrador [Error] : "+e,
                                                                        Toast.LENGTH_LONG).show();
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                MainActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                );
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this,
                                                "La contraseña es incorrecta.",
                                                Toast.LENGTH_LONG).show();
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        MainActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                    }
                                }
                                catch (NullPointerException e){
                                    Toast.makeText(MainActivity.this,
                                            "No se ha podido encontrar el usuario especificado.",
                                            Toast.LENGTH_LONG).show();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    MainActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,
                                        "Hubo un fallo al iniciar sesión contacte con el " +
                                                "administrador [Error] : "+e,
                                        Toast.LENGTH_LONG).show();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                MainActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                            }
                        }
                );

        this.prbBarraEspera.setVisibility(View.VISIBLE);
        this.prbBarraEspera.animate();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        startActivityForResult(intent, 200);
    }
}
