package com.example.aulafirebase.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.R;

import java.util.List;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.MyViewHolder> {

    private List<Grupo> listaGrupos;

    public GrupoAdapter(List<Grupo> listaGrupos) {
        this.listaGrupos = listaGrupos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View grupoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_grupo_adapter, parent, false);

        return new MyViewHolder(grupoView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Grupo grupo = listaGrupos.get(position);
        holder.txtNomeGrupo.setText(grupo.getNomeGrupo());
        holder.txtDescGrupo.setText(grupo.getDescGrupo());

    }

    @Override
    public int getItemCount() {
        return listaGrupos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtNomeGrupo, txtDescGrupo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeGrupo = itemView.findViewById(R.id.txtNomeGrupo);
            txtDescGrupo = itemView.findViewById(R.id.txtDescGrupo);
        }

    }
}
