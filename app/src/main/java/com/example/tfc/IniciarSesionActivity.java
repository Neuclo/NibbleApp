package com.example.tfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

public class IniciarSesionActivity extends AppCompatActivity {
    TextView tituloApp;
    Toolbar toolbar;
    EditText user;
    EditText pass;
    Button iniciarSesion;
    Button registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        tituloApp=findViewById(R.id.titleIniciar);
        iniciarSesion=findViewById(R.id.iniciarSesion);
        registrar=findViewById(R.id.registrar);
        user=findViewById(R.id.editTextUser);
        pass=findViewById(R.id.editTextPass);
        toolbar=findViewById(R.id.toolbarIniciarSesionActivity);
        setSupportActionBar(toolbar);

        //Cargamos la fuente desde la carpeta assets.
        Typeface typeface=Typeface.createFromAsset(getAssets(), "From Cartoon Blocks.ttf");
        //Asignamos la fuente al TextView.
        tituloApp.setTypeface(typeface);

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=String.valueOf(user.getText());
                String contrasena=String.valueOf(pass.getText());
                if(email.trim().isEmpty() | contrasena.trim().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(IniciarSesionActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Campos vacíos");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialogo=builder.create();
                    dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button positiveButton=dialogo.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(getResources().getColor(R.color.black));
                            Button negativeButton=dialogo.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(getResources().getColor(R.color.black));
                        }
                    });
                    dialogo.show();
                }else {
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email, contrasena)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    Toast.makeText(IniciarSesionActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(IniciarSesionActivity.this, MainActivity.class);
                                    i.putExtra("nombre_documento", email);
                                    user.setText("");
                                    pass.setText("");
                                    startActivity(i);
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(IniciarSesionActivity.this);
                                    builder.setTitle("Error");
                                    builder.setMessage("Sesión no iniciada debido a: "+task.getException());
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {dialog.dismiss();
                                        }
                                    });
                                    AlertDialog dialogo=builder.create();
                                    dialogo.show();

                                }
                            });
                }
            }
        });


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(IniciarSesionActivity.this, RegistrarActivity.class);
                startActivity(i);
            }
        });
    }
}