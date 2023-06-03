package com.example.tfc;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ConfirmarReservaActivity extends AppCompatActivity {
    private TextView nombre;
    private Button dateButton;
    private Button timeButton;
    private TextView dateTime;
    private ImageView imagen;
    private Button reservar;
    private Button volver;
    private Calendar fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_reserva);

        nombre=findViewById(R.id.nombreRestaurante);
        imagen=findViewById(R.id.imagenRestaurante);
        nombre.setText(getIntent().getStringExtra("name"));
        dateButton=findViewById(R.id.fecha);
        timeButton=findViewById(R.id.sel_hora);
        dateTime=findViewById(R.id.diayhora);
        reservar=findViewById(R.id.confirm_button);
        volver=findViewById(R.id.volver);

        CollectionReference cr=FirebaseFirestore.getInstance().collection("restaurants");
        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document:queryDocumentSnapshots) {
                    if (document.getString("name").equals(nombre.getText().toString())) {
                        Glide.with(ConfirmarReservaActivity.this).load(document.getString("imageUrl")).into(imagen);
                    }
                }
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarReloj();
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateTime.getText().toString().trim().isEmpty()){
                    Toast.makeText(ConfirmarReservaActivity.this, "CAMPOS VACIOS", Toast.LENGTH_SHORT).show();

                }else {
                    FirebaseFirestore firestore=FirebaseFirestore.getInstance();

                    Map<String, Object> data=new HashMap<>();
                    String cliente=FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    data.put("cliente", cliente);
                    data.put("fecha", dateTime.getText().toString());
                    data.put("restaurante", nombre.getText().toString());
                    firestore.collection("reservas").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(ConfirmarReservaActivity.this, "Reserva realizada con éxito", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                }
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void mostrarCalendario() {
        Calendar calendar=Calendar.getInstance();

        int ano=calendar.get(Calendar.YEAR);
        int mes=calendar.get(Calendar.MONTH);
        int dia=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(ConfirmarReservaActivity.this, android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        fecha=Calendar.getInstance();
                        fecha.set(Calendar.YEAR, year);
                        fecha.set(Calendar.MONTH, monthOfYear);
                        fecha.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        actualizarFecha();
                    }
                }, ano, mes, dia);

        datePickerDialog.show();
    }

    private void mostrarReloj() {
        Calendar calendar=Calendar.getInstance();

        int hora=calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(ConfirmarReservaActivity.this, android.R.style.Theme_Holo_Dialog,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (fecha==null) {
                            fecha=Calendar.getInstance();
                        }

                        fecha.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        fecha.set(Calendar.MINUTE, minute);
                        actualizarFecha();
                    }
                }, hora, min, true);

        timePickerDialog.show();
    }

    private void actualizarFecha() {
        if (fecha!=null) {
            SimpleDateFormat formato=new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            formato.setTimeZone(TimeZone.getDefault());
            String fechaString=formato.format(fecha.getTime());
            dateTime.setText(fechaString);
        }
    }
}

