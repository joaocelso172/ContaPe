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

import java.text.DecimalFormat;
import java.util.List;

public class MovimentacoesAdapter extends RecyclerView.Adapter<MovimentacoesAdapter.MyViewHolder> {

    private final List<Movimentacao> listaMovimentacaos;
    private final Context context;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

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
        holder.txtCat.setText(movimentacao.getCategoria());
        holder.txtDesc.setText(movimentacao.getDescTarefa());

            if (movimentacao.getTipo().equals("r")) {
                holder.txtCat.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtDesc.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtValor.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtResponsavelAtribuido.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtValor.setText("  R$ " + decimalFormat.format(movimentacao.getValor()));
                holder.txtParcela.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                if (movimentacao.getAtribuicao() != null) {
                    holder.txtResponsavelAtribuido.setText("Contribuição de: " + movimentacao.getAtribuicao());
                    holder.txtResponsavelAtribuido.setVisibility(View.VISIBLE);
                }
            } else {
                holder.txtCat.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtDesc.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtValor.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtResponsavelAtribuido.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtValor.setText("- R$ " + decimalFormat.format(movimentacao.getValor()));
                holder.txtParcela.setTextColor(context.getResources().getColor(R.color.colorAccent));
                if (movimentacao.getAtribuicao() != null) {
                    holder.txtResponsavelAtribuido.setText("Responsável: " + movimentacao.getAtribuicao());
                    holder.txtResponsavelAtribuido.setVisibility(View.VISIBLE);
                }
            }

            if (movimentacao.getTipoFaturamento() != null && movimentacao.getTipoFaturamento().equals("parcelado")) {
                holder.txtParcela.setText("Parcela " + movimentacao.getParcelaAtual() + " do total de " + movimentacao.getParcelaTotal());
                holder.txtParcela.setVisibility(View.VISIBLE);
            }


    }


    @Override
    public int getItemCount() {
        return listaMovimentacaos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView txtCat;
        final TextView txtParcela;
        final TextView txtDesc;
        final TextView txtValor;
        final TextView txtResponsavelAtribuido;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCat = itemView.findViewById(R.id.txtNomeTarefa);
            txtDesc = itemView.findViewById(R.id.txtDescTarefa);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtResponsavelAtribuido = itemView.findViewById(R.id.txtResponsavelMov);
            txtParcela = itemView.findViewById(R.id.txtParcelaMov);

        }
    }
}
