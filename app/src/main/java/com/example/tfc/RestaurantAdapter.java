package com.example.tfc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private final List<Restaurant> restaurants;
    static Context c;
    Button reservar;
    Button llamar;
    Button menu;

    public RestaurantAdapter(List<Restaurant> list, Context c) {
        this.restaurants=list;
        RestaurantAdapter.c =c;
    }

    @Override
    public RestaurantAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_row, parent, false);
        return new RestaurantAdapter.RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantAdapter.RestaurantViewHolder holder, int position) {
        Restaurant r=restaurants.get(position);

        reservar=holder.itemView.findViewById(R.id.botonReservar);
        menu=holder.itemView.findViewById(R.id.botonMenu);
        llamar=holder.itemView.findViewById(R.id.botonLlamar);

        holder.bind(r);

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(c, ConfirmarReservaActivity.class);
                i.putExtra("name", r.getNombre());
                c.startActivity(i);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(c, MostrarMenuActivity.class);
                i.putExtra("urlMenu", r.getUrlMenu());
                c.startActivity(i);
            }
        });
        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = r.getTlf();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + num));
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }









    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        RatingBar rating;
        TextView mediaTextView;
        TextView name;
        TextView description;
        ImageView fotoRestaurante;


        public RestaurantViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textViewNombre);
            description=itemView.findViewById(R.id.description_restaurant);
            fotoRestaurante=itemView.findViewById(R.id.imagenRestaurante);
            mediaTextView=itemView.findViewById(R.id.valoracion);
            rating=itemView.findViewById(R.id.ratingBar);

        }

        void bind(Restaurant r) {
            name.setText(r.getNombre());
            description.setText(r.getDescription());
            calcularMedia(new MediaCallback() {
                @Override
                public void obtenerMedia(double media) {
                    mediaTextView.setText(String.valueOf(media));
                }
            });


            CollectionReference cr=FirebaseFirestore.getInstance().collection("restaurants");
            cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document:queryDocumentSnapshots) {
                        if (document.getString("name").equals(name.getText().toString())) {
                            Glide.with(itemView).load(document.getString("imageUrl")).into(fotoRestaurante);
                        }
                    }
                }
            });

            CollectionReference cr2=FirebaseFirestore.getInstance().collection("users");
            cr2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documento:queryDocumentSnapshots) {
                        if(documento.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            setValores(cr2, documento.getId(), r.getNombre());
                        }
                    }
                }
            });

            rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(fromUser) {
                        Toast.makeText(c, "Has marcado una valoración de: "+rating, Toast.LENGTH_SHORT).show();
                    }

                    ratingBar.setRating(rating);

                    CollectionReference cr=FirebaseFirestore.getInstance().collection("users");
                    cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document:queryDocumentSnapshots) {
                                if(document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    DocumentReference documentReference=cr.document(document.getId());
                                    Map<String, Object> actualizacion = new HashMap<>();
                                    actualizacion.put("ratings."+r.getNombre(), rating);
                                    documentReference.update(actualizacion);

                                    calcularMedia(new MediaCallback() {
                                        @Override
                                        public void obtenerMedia(double media) {
                                            mediaTextView.setText(String.valueOf(media));
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });
        }

        void setValores(CollectionReference cr2, String documento, String r) {
            cr2.document(documento).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> valores= (Map<String, Object>) documentSnapshot.getData().get("ratings");
                    String valorString=String.valueOf(valores.get(r));
                    float valor=Float.parseFloat(valorString);
                    rating.setRating(valor);
                }
            });
        }

        void calcularMedia(MediaCallback llamada) {
            List<Number> valoraciones=new ArrayList<>();

            CollectionReference cr=FirebaseFirestore.getInstance().collection("users");
            cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        valoraciones.add((Number) document.getDouble("ratings."+name.getText().toString()));
                    }

                    double sumatorio=0;
                    for (Number valoracion:valoraciones) {
                        sumatorio=sumatorio+valoracion.doubleValue();
                    }

                    double redondeado=Math.round(sumatorio/valoraciones.size());
                    llamada.obtenerMedia(redondeado);
                }
            });
        }
    }
}
