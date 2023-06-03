package com.example.tfc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder> {
    private final List<Reserva> reservas;
    private Context c;
    public ReservasAdapter(List<Reserva> list, Context c) {
        this.reservas=list;
        this.c=c;
    }

    @Override
    public ReservasAdapter.ReservaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_items, parent, false);
        return new ReservasAdapter.ReservaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReservasAdapter.ReservaViewHolder holder, int position) {
        Reserva r=reservas.get(position);
        holder.bind(r);
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public void removeItem(int position) {
        reservas.remove(position);
        notifyItemRemoved(position);
    }






    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView fecha;
        TextView restaurante;

        public ReservaViewHolder(View itemView) {
            super(itemView);
            fecha=itemView.findViewById(R.id.texto);
            restaurante=itemView.findViewById(R.id.textorest);
        }

        void bind(Reserva r) {
            fecha.setText(r.getFecha());
            restaurante.setText(r.getNombreRestaurante());
        }
    }

    public Reserva getItem(int position) {
        if (position >= 0 && position < reservas.size()) {
            return reservas.get(position);
        } else {
            return null;
        }
    }
}
