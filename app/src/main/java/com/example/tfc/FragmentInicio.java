package com.example.tfc;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.window.SplashScreen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentInicio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInicio extends Fragment {
    FirebaseFirestore firestore;

    TextView descubre;
    AutoCompleteTextView autoCompleteTextView;
    List<String> types;
    List<String> tipos;
    ArrayAdapter<String> adapter;

    RecyclerView recyclerRestaurants;
    List<Restaurant> restaurants;
    RestaurantAdapter adapter2;

    RestaurantAdapter adapter3;
    List<Restaurant> allRestaurants;

    private View splashScreen;
    private ViewGroup splashContainer;
    private ProgressBar progressBar;

    Context c;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentInicio() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentReserva.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentInicio newInstance(String param1, String param2) {
        FragmentInicio fragment = new FragmentInicio();
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
        View v=inflater.inflate(R.layout.fragment_inicio, container, false);
        autoCompleteTextView=v.findViewById(R.id.autoCompleteTextView);
        descubre=v.findViewById(R.id.titleDescubre);

        splashContainer=v.findViewById(R.id.splashScreenContenedor);
        //Inflamos el splash screen
        splashScreen=inflater.inflate(R.layout.progress_bar, splashContainer, false);
        progressBar=splashScreen.findViewById(R.id.progressBar);
        //Mostramos el splash screen
        splashContainer.addView(splashScreen);

        //Asignamos la fuente al titulo de inicio
        Typeface typeface=Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Bold.ttf");
        descubre.setTypeface(typeface);

        recyclerRestaurants=v.findViewById(R.id.recycler_restaurants);
        LinearLayoutManager llm2=new LinearLayoutManager(getContext());
        recyclerRestaurants.setLayoutManager(llm2);


        firestore=FirebaseFirestore.getInstance();
        CollectionReference cr=firestore.collection("restaurants");
        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            //LO GUARDA EN SEGUNDO PLANO PARA QUE LA APLICACIÓN SIGA SU CURSO MIENTRAS HACE LA CONSULTA.
            //POR ESO SE UTILIZA EL MÉTODO ONCOMPLETE.
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //Si la consulta ha sido correcta, creamos los arrays para guardar los tipos de cocina
                    //que existen.
                    types=new ArrayList<>();
                    tipos=new ArrayList<>();
                    for (QueryDocumentSnapshot document:task.getResult()) {
                        String name=document.getString("name");
                        String lastType=document.getString("type");
                        String description= document.getString("description");
                        String telefono=document.getString("tlf");
                        String urlMenu=document.getString("menu");

                        if(!tipos.contains(lastType)){
                            tipos.add(lastType);
                            Restaurant r=new Restaurant(name,lastType,description, telefono, urlMenu);
                            types.add(r.getType());
                        }
                    }

                    //Quitamos el progress bar cuando los datos se han cargado.
                    splashContainer.removeView(splashScreen);


                    //CREAMOS EL ADAPTADOR Y SE LO ASIGNAMOS AL RECYCLER.
                    //CONTIENE LOS TIPOS DE COMIDA QUE EXISTEN EN LA BASE DE DATOS SIN REPETIR.
                    //EL ADAPTADOR SE DEBE CREAR Y ASIGNAR DENTRO DEL onComplete().
                    adapter=new ArrayAdapter<String>(c, android.R.layout.simple_dropdown_item_1line, types);
                    autoCompleteTextView.setAdapter(adapter);

                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String opcionSeleccionada=(String) parent.getItemAtPosition(position);

                            //Segun la opción seleccionada del AutoCompleteTextView buscamos todos los restaurantes
                            //con el tipo de cocina seleccionado.
                            CollectionReference cr=firestore.collection("restaurants");
                            cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        //Creamos otro array para utilizarlo en la clase RestaurantAdapter y guardar los restaurantes
                                        restaurants=new ArrayList<>();
                                        for (QueryDocumentSnapshot document: task.getResult()){
                                            if(document.getString("type").equals(opcionSeleccionada)){
                                                Restaurant r=new Restaurant(document.getString("name"), document.getString("type"), document.getString("description"), document.getString("tlf"), document.getString("menu"));
                                                restaurants.add(r);
                                            }
                                        }

                                        //Asignamos el adaptador al RecyclerView
                                        adapter2=new RestaurantAdapter(restaurants, getContext());
                                        recyclerRestaurants.setAdapter(adapter2);
                                    }
                                }
                            });
                        }
                    });

                    //METODO PARA QUE EL TAMAÑO DEL AUTOCOMPLETE TEXTVIEW NO CAMBIE CADA VEZ QUE SE PULSA LA TECLA INTRO.
                    autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId==EditorInfo.IME_ACTION_SEARCH ||
                                    actionId==EditorInfo.IME_ACTION_DONE ||
                                    event!=null && event.getAction()==KeyEvent.ACTION_DOWN &&
                                            event.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                                return true;
                            }
                            return false;
                        }
                    });

                }
            }
        });

        //METODO PARA OBTENER TODOS LOS RESTAURANTES EXISTENTES Y MOSTRARLOS NADA MAS
        //ENTRAR EN LA APLICACIÓN
        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    allRestaurants=new ArrayList<>();
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        String name=document.getString("name");
                        String lastType=document.getString("type");
                        String description= document.getString("description");
                        String telefono=document.getString("tlf");
                        String urlMenu= document.getString("menu");
                        Restaurant r=new Restaurant(name,lastType,description, telefono, urlMenu);
                        allRestaurants.add(r);

                    }
                    adapter3=new RestaurantAdapter(allRestaurants, getContext());
                    recyclerRestaurants.setAdapter(adapter3);
                }
            }
        });

        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.c=context;
    }
}