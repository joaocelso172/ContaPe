package com.devinstance.contape.controller.group_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devinstance.contape.adapter.GroupAdapter;
import com.devinstance.contape.RecoveredBundle;
import com.devinstance.contape.controller.login_activity.LoginActivity;
import com.devinstance.contape.DAL.FirebaseConfig;
import com.devinstance.contape.DAL.UserGroupDAO;
import com.devinstance.contape.model.Group;
import com.devinstance.contape.controller.transaction_activity.TransactionActivity;
import com.devinstance.contape.R;
import com.devinstance.contape.rc_config.RecyclerViewConfig;
import com.devinstance.contape.helper.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListGroupActivity extends AppCompatActivity implements Serializable, RecoveredBundle {

    private RecyclerView rGrupos;
    private List<Group> listaGroups = new ArrayList<>();
    private UserGroupDAO grupoDAO = new UserGroupDAO();
    private GroupAdapter groupAdapter;
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

        groupAdapter = new GroupAdapter(listaGroups);

        itemClickListener();
        
    }

    private void telaCriarGrupo(){
        Intent intentGrupo = new Intent(this, AddGroupActivity.class);
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
        Intent intentHome = new Intent(this, TransactionActivity.class);
        startActivity(intentHome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperarGrupos();

        RecyclerViewConfig.ConfigurarRecycler(getApplicationContext(), rGrupos, groupAdapter);

    }

    private void recuperarGrupos(){
        verificouDAO = false;

        progressBar.setVisibility(View.VISIBLE);
        txtStatus.setVisibility(View.VISIBLE);
        txtStatus.setText("Carregando...");
        rGrupos.setVisibility(View.GONE);

        verificouDAO = grupoDAO.getGruposUsuario(listaGroups);

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                verificouDAO = grupoDAO.getStatus();

                if (verificouDAO){
                    if (!listaGroups.isEmpty()) {
                        for (Group grups : listaGroups) {
                            Log.i("Grupos ListarGrupo", grups.getNomeGrupo() + " - " + grups.getDescGrupo() + ", " + grups.getReceitaGrupo());
                        }
                        txtStatus.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        rGrupos.setVisibility(View.VISIBLE);
                    }else if (listaGroups.isEmpty()){
                        txtStatus.setText("Você não está participando de um grupo atualmente.\nTente criar um :)");
                        progressBar.setVisibility(View.GONE);
                    }
                }else {
                    handler.postDelayed(this, 50);
                }

                groupAdapter.notifyDataSetChanged();

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
        Intent intentGrupo = new Intent( this, TransactionActivity.class );
        intentGrupo.putExtra("grupo", listaGroups.get(pos));
        startActivity(intentGrupo);
    }

}
