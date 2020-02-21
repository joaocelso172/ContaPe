package com.example.aulafirebase.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.R;

import java.util.List;

public class MovimentacoesAdapter extends RecyclerView.Adapter<MovimentacoesAdapter.MyViewHolder> {

    List<Movimentacao> listaMovimentacaos;
    Context context;

    public MovimentacoesAdapter(List<Movimentacao> listaMovimentacaos, Context context) {
        this.listaMovimentacaos = listaMovimentacaos;
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

        Movimentacao movimentacao = listaMovimentacaos.get(position);
        holder.txtNomeTarefa.setText(movimentacao.getNomeTarefa());
        holder.txtDescTarefa.setText(movimentacao.getDescTarefa());

        holder.txtNomeTarefa.setTextColor(context.getResources().getColor(R.color.colorAccent));
        holder.txtDescTarefa.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));

    }


    @Override
    public int getItemCount() {
        return listaMovimentacaos.size();
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
