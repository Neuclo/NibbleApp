package com.example.tfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarActivity extends AppCompatActivity {
    TextView tituloApp;
    EditText emailView;
    EditText pass;
    Spinner spinner;
    SpinnerAdapter adapter;
    Button register;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        tituloApp=findViewById(R.id.titleRegistrar);
        toolbar=findViewById(R.id.toolbarRegistrarActivity);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Cargamos la fuente desde la carpeta assets.
        Typeface typeface = Typeface.createFromAsset(getAssets(), "From Cartoon Blocks.ttf");
        // Asignamos la fuente al TextView.
        tituloApp.setTypeface(typeface);

        emailView=findViewById(R.id.editTextEmail);
        pass=findViewById(R.id.editTextPass);
        register=findViewById(R.id.register);


        spinner=findViewById(R.id.spinner);
        recuperarImagenes(images -> {
            adapter=new SpinnerAdapter(RegistrarActivity.this, images);
            spinner.setAdapter(adapter);
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=String.valueOf(emailView.getText());
                String contrasena=String.valueOf(pass.getText());
                if(email.trim().isEmpty() | contrasena.trim().isEmpty()) {
                    //SI LOS CAMPOS SON VACIOS, SALTA UN AVISO
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Campos vacíos.");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialogo = builder.create();
                    dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button positiveButton = dialogo.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(getResources().getColor(R.color.black));
                            Button negativeButton=dialogo.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(getResources().getColor(R.color.black));
                        }
                    });
                    dialogo.show();
                }else {
                    //SI NO ESTAN VACÍOS, SE CREA EL USUARIO
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, contrasena)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    Toast.makeText(RegistrarActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    CollectionReference cr =FirebaseFirestore.getInstance().collection("users");
                                    DocumentReference document=cr.document(email);
                                    Map<String, Object> datos=new HashMap<>();
                                    datos.put("email", email);
                                    datos.put("password", contrasena);
                                    datos.put("imageUrl", adapter.getUrlOptionSpinner());
                                    recogerNombresRestaurantes(new NombresRestauranteCallback() {
                                        @Override
                                        public void obtenerRestaurantes(List<String> nombres) {
                                            Map<String, Object> mapRatings=new HashMap<>();
                                            for (int i=0;i<nombres.size();i++) {
                                                mapRatings.put(nombres.get(i), 0);
                                            }
                                            datos.put("ratings", mapRatings);
                                            document.set(datos, SetOptions.merge());
                                        }
                                    });
                                    onBackPressed();
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarActivity.this);
                                    builder.setTitle("Error al iniciar sesión");
                                    builder.setMessage("Usuario no registrado debido a: "+task.getException());
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    AlertDialog dialogo = builder.create();
                                    dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            Button positiveButton = dialogo.getButton(DialogInterface.BUTTON_POSITIVE);
                                            positiveButton.setTextColor(getResources().getColor(R.color.black));
                                            Button negativeButton=dialogo.getButton(DialogInterface.BUTTON_NEGATIVE);
                                            negativeButton.setTextColor(getResources().getColor(R.color.black));
                                        }
                                    });
                                    dialogo.show();
                                }
                            });

                }
            }
        });
    }

    private void recogerNombresRestaurantes(NombresRestauranteCallback llamada) {
        List<String> list=new ArrayList<>();
        CollectionReference cr=FirebaseFirestore.getInstance().collection("restaurants");
        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    list.add(document.getString("name"));
                }
                llamada.obtenerRestaurantes(list);
            }
        });
    }


    private void recuperarImagenes(CargarImagenesListener listener) {
        List<String> array=new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("/default_icons");

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    //item es una referencia a un elemento en la carpeta
                    item.getDownloadUrl().addOnSuccessListener(uri -> {
                        array.add(uri.toString());
                        //como se ejecuta en segundo plano hay que comprobar el tamaño del array
                        //para ver si se han descargado todas las imágenes
                        if(array.size()==listResult.getItems().size()) {
                            listener.onImageListLoaded(array);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Volver hacia atras
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}