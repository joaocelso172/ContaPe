package com.example.aulafirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.example.aulafirebase.Adapter.GrupoAdapter;
import com.example.aulafirebase.DAL.UsuarioGrupoDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.RecyclerViewConfig.RecyclerViewConfig;
import com.example.aulafirebase.helper.RecyclerItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListarGruposActivity extends AppCompatActivity implements Serializable {

    private RecyclerView rGrupos;
    private List<Grupo> listaGrupos = new ArrayList<>();
    private UsuarioGrupoDAO grupoDAO = new UsuarioGrupoDAO();
    private GrupoAdapter grupoAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_grupos);

        rGrupos = findViewById(R.id.rGrupos);
        progressBar = findViewById(R.id.progressGruposLista);

        itemClickListener();
        
    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperarGrupos();

        RecyclerViewConfig.ConfigurarRecycler(getApplicationContext(), rGrupos, grupoAdapter);

    }

    private void recuperarGrupos(){

        listaGrupos = grupoDAO.getGruposUsuario();

        grupoAdapter = new GrupoAdapter(listaGrupos);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              //  listaGrupos = grupoDAO.getGruposUsuario();
                if (listaGrupos != null && !listaGrupos.isEmpty()){

                    for (Grupo grups : listaGrupos){
                        Log.i("Grupos ListarGrupo", grups.getNomeGrupo() + " - " + grups.getDescGrupo() + ", " + grups.getReceitaGrupo());
                    }

                }else handler.postDelayed(this, 1000);

                grupoAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                rGrupos.setVisibility(View.VISIBLE);

            }
        }, 100);

    }

    private void itemClickListener(){

        rGrupos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(), rGrupos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                cliqueGrupo(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }


    private void cliqueGrupo(int pos){
        Intent intentGrupo = new Intent( this, MovimentacaoActivity.class );
        intentGrupo.putExtra("grupo", listaGrupos.get(pos));
        startActivity(intentGrupo);

    }
}
