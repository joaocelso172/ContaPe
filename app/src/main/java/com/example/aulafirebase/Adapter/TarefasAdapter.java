package com.example.aulafirebase.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aulafirebase.Model.Tarefa;
import com.example.aulafirebase.R;

import java.util.ArrayList;
import java.util.List;

public class TarefasAdapter extends RecyclerView.Adapter<TarefasAdapter.MyViewHolder> {

    List<Tarefa> listaTarefas;
    Context context;

    public TarefasAdapter(List<Tarefa> listaTarefas, Context context) {
        this.listaTarefas = listaTarefas;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View tarefaView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_tarefas_adapter, parent, false);

        return new MyViewHolder(tarefaView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Tarefa tarefa = listaTarefas.get(position);
        holder.txtNomeTarefa.setText(tarefa.getNomeTarefa());
        holder.txtDescTarefa.setText(tarefa.getDescTarefa());

        holder.txtNomeTarefa.setTextColor(context.getResources().getColor(R.color.colorAccent));
        holder.txtDescTarefa.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));

    }


    @Override
    public int getItemCount() {
        return listaTarefas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtNomeTarefa;
        TextView txtDescTarefa;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeTarefa = itemView.findViewById(R.id.txtNomeTarefa);
            txtDescTarefa = itemView.findViewById(R.id.txtDescTarefa);

        }
    }
}
