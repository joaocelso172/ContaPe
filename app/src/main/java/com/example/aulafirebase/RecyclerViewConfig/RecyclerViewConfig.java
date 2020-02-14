package com.example.aulafirebase.RecyclerViewConfig;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aulafirebase.Adapter.TarefasAdapter;
import com.example.aulafirebase.Model.Tarefa;

import java.util.List;

public class RecyclerViewConfig {



    public static void ConfigurarRecycler(Context c,RecyclerView recyclerView, List<Tarefa> listaTarefas, TarefasAdapter tarefasAdapter) {


        //Passando uma variavel recyclerView para retorno
    //    RecyclerView recyclerView = null;

        //Configurando adapter
        //TarefasAdapter tarefasAdapter = new TarefasAdapter(listaTarefas);

        //Configurando Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(c, LinearLayout.VERTICAL));

        //Setando adapter
        recyclerView.setAdapter(tarefasAdapter);

       // return recyclerView;
    }
}
