package com.example.aulafirebase.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.UsuarioGrupoDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.GrupoUsuario;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.Model.UsuarioGrupo;
import com.example.aulafirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.List;

public class MovimentacoesAdapter extends RecyclerView.Adapter<MovimentacoesAdapter.MyViewHolder> {

    private final List<Movimentacao> listaMovimentacaos;
    private final Context context;
    private FirebaseAuth fbUsuario;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private Grupo grupo;

    public MovimentacoesAdapter(List<Movimentacao> listaMovimentacaos, Context context, Grupo setGrupo) {
        this.listaMovimentacaos = listaMovimentacaos;
        this.context = context;
        fbUsuario = FirebaseConfig.getFirebaseAuth();
        grupo = setGrupo;
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
        holder.txtDataMov.setText(movimentacao.getDataTarefa());
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


            if (movimentacao.getTipo().equals("r")) {
                holder.txtCat.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtDesc.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtValor.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtResponsavelAtribuido.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txtValor.setText("  R$ " + decimalFormat.format(movimentacao.getValor()));
                holder.txtParcela.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                if (movimentacao.getAtribuicao() != null) {
                    holder.txtResponsavelAtribuido.setText("Contribuidor: " + movimentacao.getAtribuicao());
                    holder.txtNomeGrupo.setText(movimentacao.getNomeGrupo());
                    holder.txtNomeGrupo.setVisibility(View.VISIBLE);
                    holder.txtResponsavelAtribuido.setVisibility(View.VISIBLE);
                //   if (fbUsuario.getCurrentUser().getEmail().equals(movimentacao.getAtribuicao())) holder.imgFavMov.setVisibility(View.VISIBLE);
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
                    holder.txtNomeGrupo.setText(movimentacao.getNomeGrupo());
                    holder.txtNomeGrupo.setVisibility(View.VISIBLE);
                    holder.txtResponsavelAtribuido.setVisibility(View.VISIBLE);
              //   if (fbUsuario.getCurrentUser().getEmail().equals(movimentacao.getAtribuicao())) holder.imgFavMov.setVisibility(View.VISIBLE);
                }
            }

            if (movimentacao.getTipoFaturamento().equals("parcelado") || movimentacao.getTipoFaturamento().equals("recorrente") ) {

                switch (movimentacao.getTipoFaturamento()){
                    case "parcelado":
                        holder.txtParcela.setText("Parcela " + movimentacao.getParcelaAtual() + " do total de " + movimentacao.getParcelaTotal());
                        break;

                    case "recorrente":
                        holder.txtParcela.setText("Recorrência " + movimentacao.getParcelaAtual() + " do total de " + movimentacao.getParcelaTotal());
                        break;
                }
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
