package com.example.tfc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MostrarMenuActivity extends AppCompatActivity {
    ImageView fotoMenu;
    Button volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_menu);
        fotoMenu=findViewById(R.id.imageMenu);
        Glide.with(MostrarMenuActivity.this).load(getIntent().getStringExtra("urlMenu")).into(fotoMenu);
        volver=findViewById(R.id.volver_button);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}