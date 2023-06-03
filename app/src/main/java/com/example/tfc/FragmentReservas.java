package com.example.tfc;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentReservas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReservas extends Fragment {
    List<Reserva> items;
    RecyclerView rv;
    ReservasAdapter adapter;
    private GestureDetector detectorPantallaPulsada;
    private static Context c;
    private ImageView gatito;
    private TextView frase;
    private TextView titulo;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentReservas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCalendario.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentReservas newInstance(String param1, String param2) {
        FragmentReservas fragment = new FragmentReservas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_reservas, container, false);
        titulo=v.findViewById(R.id.title);
        frase=v.findViewById(R.id.frase);
        //ASIGNAMOS LA FUENTE AL TITULO.
        Typeface typefont = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Bold.ttf");
        titulo.setTypeface(typefont);
        frase.setTypeface(typefont);
        gatito=v.findViewById(R.id.gatito);

        rv=v.findViewById(R.id.rv);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        String currentUser=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //MÉTODO PARA BUSCAR TODAS LAS RESERVAS SEGUN EL USUARIO QUE HAY LOGUEADO
        CollectionReference cr=FirebaseFirestore.getInstance().collection("reservas");
        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    items=new ArrayList<>();
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        if(document.getString("cliente").equals(currentUser)){
                            Reserva r=new Reserva(document.getId(), document.getString("fecha"), document.getString("restaurante"));
                            items.add(r);
                        }
                    }
                    adapter=new ReservasAdapter(items, getContext());

                    //SI LA CONSULTA NO DEVUELVE NADA, EL TAMAÑO DEL ADAPTER SERA 0
                    //POR LO QUE COMPROBAMOS SU TAMAÑO Y SI ES 0 HACEMOS VISIBLE
                    //LA IMAGEN DE GATITO Y LA FRASE, SI NO SE AÑADE EL ADAPTADOR AL
                    //RECYCLER VIEW
                    if(adapter.getItemCount()==0) {
                        gatito.setVisibility(View.VISIBLE);
                        frase.setVisibility(View.VISIBLE);
                    }else {
                        rv.setAdapter(adapter);
                    }
                }
            }
        });

        //METODO PARA DETECTAR SI SE PULSA EN LA PANTALLA ALGUN ITEM DEL RECYCLER (ALGUNA RESERVA) CON AYUDA DE GestureDetector.
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                detectorPantallaPulsada.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        detectorPantallaPulsada=new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                View view=rv.findChildViewUnder(e.getX(), e.getY());
                if (view!=null) {
                    //Si se encuentra una vista secundaria en la coordenada
                    //se obtiene su posición en el adapter del recycler
                    int position=rv.getChildAdapterPosition(view);
                    //Obtenemos el elemento seleccionado
                    Reserva reserva=adapter.getItem(position);
                    //Mostrar el diálogo pasandole el elemento
                    showDialog(reserva);
                }
            }
        });



        return v;
    }

    //METODO PARA MOSTRAR EL DIALOGO PARA PREGUNTAR AL USUARIO SI ESTA SEGURO DE
    //QUE QUIERE ELIMINAR LA RESERVA EXISTENTE.
    private void showDialog(Reserva reserva) {
        AlertDialog.Builder builder=new AlertDialog.Builder(c);
        builder.setTitle("Eliminar reserva");
        builder.setMessage("¿Estás seguro de que quieres eliminar la reserva?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CollectionReference cr=FirebaseFirestore.getInstance().collection("reservas");
                cr.document(reserva.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //SACAMOS EL INDEX DE LA RESERVA
                        int position=items.indexOf(reserva);

                        //SI ES DISTINTO DE -1, ELIMINAMOS LA RESERVA
                        if (position!=-1) {
                            adapter.removeItem(position);
                            Toast.makeText(c, "Reserva eliminada con éxito", Toast.LENGTH_SHORT).show();
                        }

                        //VOLVEMOS A COMPROBAR SI EL ADAPTER SE QUEDA A TAMAÑO 0
                        //CADA VEZ QUE SE ELIMINA UNA RESERVA PARA MOSTRAR EL GATITO Y LA FRASE O NO
                        if(adapter.getItemCount()==0) {
                            gatito.setVisibility(View.VISIBLE);
                            frase.setVisibility(View.VISIBLE);
                        }else {
                            gatito.setVisibility(View.INVISIBLE);
                            frase.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                dialog.dismiss();
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
                Button positiveButton = dialogo.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.black));
                Button negativeButton=dialogo.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialogo.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.c=context;
    }
}