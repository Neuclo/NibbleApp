package com.example.tfc;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView tituloApp;
    ImageView foto;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tituloApp=findViewById(R.id.title);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cargarIcono(getIntent().getStringExtra("nombre_documento"));

        // Cargamos la fuente desde la carpeta assets.
        Typeface typeface=Typeface.createFromAsset(getAssets(), "From Cartoon Blocks.ttf");
        // Asignamos la fuente al TextView.
        tituloApp.setTypeface(typeface);

        //HACEMOS USO DE LA LIBRERIA NAVHOSTFRAGMENT PARA CREAR UN MENÚ DE NAVEGACIÓN
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment!=null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
        }
    }



    //MÉTODO PARA ASIGNAR EL ICONO QUE HA ELEGIDO EL USUARIO
    private void cargarIcono(String nombreDocumento) {
        foto=findViewById(R.id.imageView);
        CollectionReference cr=FirebaseFirestore.getInstance().collection("users");
        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document:queryDocumentSnapshots) {
                    if (document.getId().equals(nombreDocumento)) {
                        Glide.with(MainActivity.this).load(document.getString("imageUrl")).into(foto);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Salir");
        builder.setMessage("¿Estas seguro de que quieres cerrar la sesión?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
    }
}