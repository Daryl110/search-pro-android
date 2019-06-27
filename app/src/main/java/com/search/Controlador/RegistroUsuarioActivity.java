package com.search.Controlador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private CollectionReference usuarios;

    private EditText txtNombreUsuario, txtCorreo, txtPassword;
    private Spinner cbTipoCuenta;
    private ProgressBar prbBarraEspera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        //Iniciar Firebase

        this.usuarios = MainActivity.db.collection("usuarios");

        //Iniciar Campos
        this.txtNombreUsuario = findViewById(R.id.txtNombreUsuario_registroUsuario);
        this.txtCorreo = findViewById(R.id.txtCorreo_registroUsuario);
        this.txtPassword = findViewById(R.id.txtPassword_registroUsuario);
        this.cbTipoCuenta = findViewById(R.id.cbTipoCuenta_registroUsuario);
        this.prbBarraEspera = findViewById(R.id.prbBarraEspera_registroUsuario);
    }

    public void cerrarActivity(View view){
        this.finish();
    }

    public void crearCuenta(View view){
        if (this.txtNombreUsuario.getText().toString().trim().isEmpty() ||
        this.txtPassword.getText().toString().trim().isEmpty() ||
        this.txtCorreo.getText().toString().trim().isEmpty() ||
        this.cbTipoCuenta.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Debe diligenciar bebidamente el formulario", Toast.LENGTH_LONG).show();
            return;
        }

        final String correo = this.txtCorreo.getText().toString().trim();
        final String password = this.txtPassword.getText().toString().trim();
        final String nombreUsuario = this.txtNombreUsuario.getText().toString().trim();
        final String tipoUsuario = this.cbTipoCuenta.getSelectedItem().toString().trim();

        final Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombreUsuario", nombreUsuario);
        usuario.put("correo", correo);
        usuario.put("password", Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
        usuario.put("tipoUsuario", tipoUsuario);

        this.usuarios.get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    ArrayList<DocumentSnapshot> documentos = new ArrayList<>(task.getResult().getDocuments());
                                        for (DocumentSnapshot documento : documentos){
                                            if (documento.get("correo").toString().equalsIgnoreCase(correo)){
                                                Toast.makeText(RegistroUsuarioActivity.this,
                                                        "Su correo ya tiene una cuenta existente",
                                                        Toast.LENGTH_LONG).show();
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                RegistroUsuarioActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                return;
                                            }
                                        }
                                        MainActivity.auth.createUserWithEmailAndPassword(correo, password)
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<AuthResult>() {
                                                            @Override
                                                            public void onSuccess(AuthResult authResult) {
                                                                final FirebaseUser user = MainActivity.auth.getCurrentUser();
                                                                try {
                                                                    user.sendEmailVerification();
                                                                    usuario.put("status", "inVerification");

                                                                    RegistroUsuarioActivity.this.usuarios
                                                                            .get()
                                                                            .addOnSuccessListener(
                                                                                    new OnSuccessListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(QuerySnapshot snapshots) {
                                                                                            for (DocumentSnapshot document : snapshots.getDocuments()){
                                                                                                if(document.get("nombreUsuario").toString().equals(nombreUsuario)){
                                                                                                    user.delete();
                                                                                                    Toast.makeText(RegistroUsuarioActivity.this,
                                                                                                            "El nombre de usuario ya existe",
                                                                                                            Toast.LENGTH_SHORT).show();
                                                                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                                    RegistroUsuarioActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                                                                    return;
                                                                                                }
                                                                                            }
                                                                                            RegistroUsuarioActivity.this.usuarios.document(nombreUsuario)
                                                                                                    .set(usuario)
                                                                                                    .addOnSuccessListener(
                                                                                                            new OnSuccessListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Void aVoid) {
                                                                                                                    AlertDialog.Builder builder = new
                                                                                                                            AlertDialog.Builder(RegistroUsuarioActivity.this);

                                                                                                                    builder.setMessage(
                                                                                                                            "Se ha enviado un correo de" +
                                                                                                                                    " verificación a "+correo+", por favor " +
                                                                                                                                    "valide su cuenta para terminar la creación de esta."
                                                                                                                    ).setTitle("Mensaje")
                                                                                                                            .setPositiveButton(
                                                                                                                                    "ACEPTAR",
                                                                                                                                    new DialogInterface.OnClickListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                                                                            RegistroUsuarioActivity.this.finish();
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                            )
                                                                                                                            .setCancelable(false);

                                                                                                                    AlertDialog dialog = builder.create();
                                                                                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                                                    dialog.show();
                                                                                                                }
                                                                                                            }
                                                                                                    )
                                                                                                    .addOnFailureListener(
                                                                                                            new OnFailureListener() {
                                                                                                                @Override
                                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                                    Toast.makeText(RegistroUsuarioActivity.this,
                                                                                                                            "Hubo un fallo al crear la cuenta contacte con el " +
                                                                                                                                    "administrador [Error] : "+e,
                                                                                                                            Toast.LENGTH_LONG).show();
                                                                                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                                                    RegistroUsuarioActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                                                                                }
                                                                                                            }
                                                                                                    );
                                                                                        }
                                                                                    }
                                                                            )
                                                                            .addOnFailureListener(
                                                                                    new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(RegistroUsuarioActivity.this,
                                                                                                    "Hubo un fallo al crear la cuenta contacte con el " +
                                                                                                            "administrador [Error] : "+e,
                                                                                                    Toast.LENGTH_LONG).show();
                                                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                            RegistroUsuarioActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                                                        }
                                                                                    }
                                                                            );
                                                                }catch (Exception e){
                                                                    Toast.makeText(RegistroUsuarioActivity.this,
                                                                            "Hubo un fallo al crear la cuenta contacte con el " +
                                                                                    "administrador [Error] : "+e,
                                                                            Toast.LENGTH_LONG).show();
                                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                    RegistroUsuarioActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                                }
                                                            }
                                                        }
                                                )
                                                .addOnFailureListener(
                                                        new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                if (e.getClass().getSimpleName().equals("FirebaseAuthWeakPasswordException")){
                                                                    Toast.makeText(RegistroUsuarioActivity.this,
                                                                            "La contraseña debe tener como minimo 6 caracteres",
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                                else if(e.getClass().getSimpleName().equals("FirebaseAuthInvalidCredentialsException")){
                                                                    Toast.makeText(RegistroUsuarioActivity.this,
                                                                            "El correo que ha ingresado no es valido",
                                                                            Toast.LENGTH_LONG).show();
                                                                } else {
                                                                    Toast.makeText(RegistroUsuarioActivity.this,
                                                                            "Hubo un fallo al crear la cuenta contacte con el " +
                                                                                    "administrador [Error] : "+e,
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                RegistroUsuarioActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                );
                                }else {
                                    Toast.makeText(RegistroUsuarioActivity.this,
                                            "Hubo un fallo al crear la cuenta contacte con el " +
                                                    "administrador [Error] : "+task.getException(),
                                            Toast.LENGTH_LONG).show();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    RegistroUsuarioActivity.this.prbBarraEspera.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                );

        this.prbBarraEspera.setVisibility(View.VISIBLE);
        this.prbBarraEspera.animate();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
