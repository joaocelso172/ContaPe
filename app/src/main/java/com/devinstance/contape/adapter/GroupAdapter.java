package com.devinstance.contape.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devinstance.contape.model.Group;
import com.devinstance.contape.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private List<Group> listaGroups;

    public GroupAdapter(List<Group> listaGroups) {
        this.listaGroups = listaGroups;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View grupoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_grupo_adapter, parent, false);

        return new MyViewHolder(grupoView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Group group = listaGroups.get(position);
        holder.txtNomeGrupo.setText(group.getNomeGrupo());
        holder.txtDescGrupo.setText(group.getDescGrupo());

    }

    @Override
    public int getItemCount() {
        return listaGroups.size();
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
