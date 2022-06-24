package com.devinstance.contape.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devinstance.contape.DAL.FirebaseConfig;
import com.devinstance.contape.model.Group;
import com.devinstance.contape.model.Transaction;
import com.devinstance.contape.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private final List<Transaction> listaTransactions;
    private final Context context;
    private FirebaseAuth fbUsuario;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private Group group;

    public TransactionAdapter(List<Transaction> listaTransactions, Context context, Group setGroup) {
        this.listaTransactions = listaTransactions;
        this.context = context;
        fbUsuario = FirebaseConfig.getFirebaseAuth();
        group = setGroup;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View tarefaView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_tarefas_adapter, parent, false);

        return new MyViewHolder(tarefaView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Transaction transaction = listaTransactions.get(position);
        holder.txtCat.setText(transaction.getCategoria());
        holder.txtDesc.setText(transaction.getDescTarefa());
        holder.txtDataMov.setText(transaction.getDataTarefa());
        holder.txtNomeGrupo.setVisibility(View.GONE);
        holder.txtResponsavelAtribuido.setVisibility(View.GONE);

  /*      if ( (movimentacao.getAtribuicao() != null && grupo == null) ) {
//            switch (movimentacao.getTipo()) {
//                case "r":
//                    movimentacao.setTipo("d");
//                    break;
//                case "d":
//                    movimentacao.setTipo("r");
//                    break;
//            }
            int i = 0;
            if (movimentacao.getTipo().equals("r") && i==0) {
                movimentacao.setTipo("d");
                i++;
            } else if (movimentacao.getTipo().equals("d") && i==0) {
                movimentacao.setTipo("r");
                i++;
            }
//            notifyDataSetChanged();
        }*/


            if (transaction.getTipo().equals("r")) {
                holder.txtCat.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtDesc.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtValor.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtResponsavelAtribuido.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtValor.setText("  R$ " + decimalFormat.format(transaction.getValor()));
                holder.txtParcela.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                if (transaction.getAtribuicao() != null) {
                    holder.txtResponsavelAtribuido.setText("Contribuidor: " + transaction.getAtribuicao());
                    holder.txtNomeGrupo.setText(transaction.getNomeGrupo());
                    holder.txtNomeGrupo.setVisibility(View.VISIBLE);
                    holder.txtResponsavelAtribuido.setVisibility(View.VISIBLE);
                //   if (fbUsuario.getCurrentUser().getEmail().equals(movimentacao.getAtribuicao())) holder.imgFavMov.setVisibility(View.VISIBLE);
                }
            } else {
                holder.txtCat.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtDesc.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtValor.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtResponsavelAtribuido.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtValor.setText("- R$ " + decimalFormat.format(transaction.getValor()));
                holder.txtParcela.setTextColor(context.getResources().getColor(R.color.colorAccent));
                if (transaction.getAtribuicao() != null) {
                    holder.txtResponsavelAtribuido.setText("Responsável: " + transaction.getAtribuicao());
                    holder.txtNomeGrupo.setText(transaction.getNomeGrupo());
                    holder.txtNomeGrupo.setVisibility(View.VISIBLE);
                    holder.txtResponsavelAtribuido.setVisibility(View.VISIBLE);
              //   if (fbUsuario.getCurrentUser().getEmail().equals(movimentacao.getAtribuicao())) holder.imgFavMov.setVisibility(View.VISIBLE);
                }
            }

            if (transaction.getTipoFaturamento().equals("parcelado") || transaction.getTipoFaturamento().equals("recorrente") ) {

                switch (transaction.getTipoFaturamento()){
                    case "parcelado":
                        holder.txtParcela.setText("Parcela " + transaction.getParcelaAtual() + " do total de " + transaction.getParcelaTotal());
                        break;

                    case "recorrente":
                        holder.txtParcela.setText("Recorrência " + transaction.getParcelaAtual() + " do total de " + transaction.getParcelaTotal());
                        break;
                }
                holder.txtParcela.setVisibility(View.VISIBLE);
            }


    }


    @Override
    public int getItemCount() {
        return listaTransactions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView txtCat;
        final TextView txtParcela;
        final TextView txtDesc;
        final TextView txtDataMov;
        final TextView txtValor;
        final TextView txtResponsavelAtribuido;
        final TextView txtNomeGrupo;
        final ImageView imgFavMov;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFavMov = itemView.findViewById(R.id.imgFav);
            txtCat = itemView.findViewById(R.id.txtNomeTarefa);
            txtDesc = itemView.findViewById(R.id.txtDescTarefa);
            txtDataMov = itemView.findViewById(R.id.txtDataMov);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtNomeGrupo = itemView.findViewById(R.id.txtNomeGrupo);
            txtResponsavelAtribuido = itemView.findViewById(R.id.txtResponsavelMov);
            txtParcela = itemView.findViewById(R.id.txtParcelaMov);

        }
    }
}
