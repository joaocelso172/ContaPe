package com.example.aulafirebase.Controller.ActivityGrupos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.aulafirebase.Controller.ActivityLogin.LoginActivity;
import com.example.aulafirebase.Controller.ActivityMovimentacao.MovimentacaoActivity;
import com.example.aulafirebase.DAL.ConviteDAO;
import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.GrupoDAO;
import com.example.aulafirebase.Model.Convite;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.R;
import com.google.firebase.auth.FirebaseAuth;

public class AddGrupoActivity extends AppCompatActivity {

    private Button btnCriarGrupo;
    private EditText editNomeGrupo;
    private EditText editDescGrupo;
    private FirebaseAuth auth;

    private Grupo novoGrupo = new Grupo();
    private GrupoDAO grupoDAO = new GrupoDAO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_grupo);

        btnCriarGrupo = findViewById(R.id.btnCriarGrupo);
        editDescGrupo = findViewById(R.id.edDescGrupo);
        editNomeGrupo = findViewById(R.id.edNomeGrupo);


        btnCriarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarGrupo();
            }
        });


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuListarGrupos);
        MenuItem itemHome = menu.findItem(R.id.menuCriarGrupos);

        item.setTitle("Grupos");
        itemHome.setTitle("Home");

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
                telaHome();
                finish();
                break;
            case R.id.menuListarGrupos:
                telaGrupo();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void telaHome() {
        Intent intent = new Intent(this, MovimentacaoActivity.class);
        startActivity(intent);
    }

    private void telaGrupo() {
        Intent intent = new Intent(this, ListarGruposActivity.class);
        startActivity(intent);
    }

    private void registrarGrupo(){
        novoGrupo.setDescGrupo(editDescGrupo.getText().toString());
        novoGrupo.setNomeGrupo(editNomeGrupo.getText().toString());
        novoGrupo.setGrupoOwner(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        grupoDAO.setGrupo(novoGrupo);

        telaGrupo();
    }

}
