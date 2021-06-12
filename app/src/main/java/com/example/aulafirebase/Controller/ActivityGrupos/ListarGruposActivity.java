package com.example.aulafirebase.Controller.ActivityGrupos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aulafirebase.Adapter.GrupoAdapter;
import com.example.aulafirebase.BundleRecuperado;
import com.example.aulafirebase.Controller.ActivityLogin.LoginActivity;
import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.UsuarioGrupoDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Controller.ActivityMovimentacao.MovimentacaoActivity;
import com.example.aulafirebase.R;
import com.example.aulafirebase.RecyclerViewConfig.RecyclerViewConfig;
import com.example.aulafirebase.helper.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListarGruposActivity extends AppCompatActivity implements Serializable, BundleRecuperado {

    private RecyclerView rGrupos;
    private List<Grupo> listaGrupos = new ArrayList<>();
    private UsuarioGrupoDAO grupoDAO = new UsuarioGrupoDAO();
    private GrupoAdapter grupoAdapter;
    private ProgressBar progressBar;
    private TextView txtStatus;
    //Booleano que informa se o status já foi verificado no DAO
    private Boolean verificouDAO;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_grupos);

        rGrupos = findViewById(R.id.rGrupos);
        progressBar = findViewById(R.id.progressGruposLista);
        txtStatus = findViewById(R.id.txtGrupoCarregado);

        grupoAdapter = new GrupoAdapter(listaGrupos);

        itemClickListener();
        
    }

    private void telaCriarGrupo(){
        Intent intentGrupo = new Intent(this, AddGrupoActivity.class);
        startActivity(intentGrupo);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuListarGrupos);

        item.setTitle("Home");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mov, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSair:
                auth = FirebaseConfig.getFirebaseAuth();
                auth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.menuCriarGrupos:
                telaCriarGrupo();
                finish();
                break;
            case R.id.menuListarGrupos:
                telaHome();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void telaHome() {
        Intent intentHome = new Intent(this, MovimentacaoActivity.class);
        startActivity(intentHome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperarGrupos();

        RecyclerViewConfig.ConfigurarRecycler(getApplicationContext(), rGrupos, grupoAdapter);

    }

    private void recuperarGrupos(){
        verificouDAO = false;

        progressBar.setVisibility(View.VISIBLE);
        txtStatus.setVisibility(View.VISIBLE);
        txtStatus.setText("Carregando...");
        rGrupos.setVisibility(View.GONE);

        verificouDAO = grupoDAO.getGruposUsuario(listaGrupos);

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                verificouDAO = grupoDAO.getStatus();

                if (verificouDAO){
                    if (!listaGrupos.isEmpty()) {
                        for (Grupo grups : listaGrupos) {
                            Log.i("Grupos ListarGrupo", grups.getNomeGrupo() + " - " + grups.getDescGrupo() + ", " + grups.getReceitaGrupo());
                        }
                        txtStatus.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        rGrupos.setVisibility(View.VISIBLE);
                    }else if (listaGrupos.isEmpty()){
                        txtStatus.setText("Você não está participando de um grupo atualmente.\nTente criar um :)");
                        progressBar.setVisibility(View.GONE);
                    }
                }else {
                    handler.postDelayed(this, 50);
                }

                grupoAdapter.notifyDataSetChanged();

            }
        });

    }

    private void itemClickListener(){

        rGrupos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(), rGrupos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                enviarBundle(position);
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

    @Override
    public void enviarBundle(int pos) {
        Intent intentGrupo = new Intent( this, MovimentacaoActivity.class );
        intentGrupo.putExtra("grupo", listaGrupos.get(pos));
        startActivity(intentGrupo);
    }

}
