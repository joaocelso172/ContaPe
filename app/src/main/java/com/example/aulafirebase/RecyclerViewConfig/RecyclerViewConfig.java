package com.example.aulafirebase.RecyclerViewConfig;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aulafirebase.Adapter.MovimentacoesAdapter;

public class RecyclerViewConfig {


    public static void ConfigurarRecycler(Context c, RecyclerView recyclerView, RecyclerView.Adapter adapter) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(c, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);

    }

}
