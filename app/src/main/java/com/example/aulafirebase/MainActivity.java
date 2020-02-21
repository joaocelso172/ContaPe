package com.example.aulafirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aulafirebase.Adapter.MovimentacoesAdapter;
import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.DAL.UsuarioDAO;
import com.example.aulafirebase.Model.Movimentacao;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.aulafirebase.LoginGoogle.GOOGLE_SIGN;
import static com.example.aulafirebase.RecyclerViewConfig.RecyclerViewConfig.ConfigurarRecycler;

public class MainActivity extends AppCompatActivity {

  //  private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();
  //  private String nomeUsuario, tipoUsuario;

    //Variavel referente a lista Recycler de Tarefas
    private RecyclerView recyclerTarefas;
    //Variavel referente a lista de tarefas que alimentará o recycler
    private List<Movimentacao> listaMovimentacaos = new ArrayList<>();
    //Variavel que recupera um objeto TarefaAdapter
    private MovimentacoesAdapter movimentacoesAdapter = new MovimentacoesAdapter(listaMovimentacaos, this);
    //Variavel de autentificacao para puxar usuário logado e deslogar
    private FirebaseAuth mAuth;
    //Variavel que vai receber o usuário, mUser = mAuth.getCurrentUser();
    private FirebaseUser mUser = mAuth.getInstance().getCurrentUser();
    //Configurações de Login com Google, necessário acompanhar GoogleSignInOption, incluso em LooginGoogle.java
    private GoogleSignInClient mGoogleSignInClient;
    //Variavel da classe que puxaremos os objetos
    private LoginGoogle loginGoogle;
    //Variavel que dá origem ao objeto que controlará o CRUD de dados no BD referente a usuários
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    //Variavel usada para interagir com o CRUD de Tarefas
    private MovimentacoesDAO movimentacoesDAO = new MovimentacoesDAO();


    private ImageButton imgPerfil;
    private Button btnEnviarTarefa;
    private EditText edNome, edDesc, edPrioridade, edAssociado;
    private Spinner spinNomes;
    private ProgressBar progressAuth;
    private String[] nomeUsuarios = new String[]{"João", "Teste 01", "Teste 02"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnviarTarefa = findViewById(R.id.btnEnviarTarefa);
        edNome = findViewById(R.id.editNomeTarefa);
        edDesc = findViewById(R.id.editDescTarefa);
        edPrioridade = findViewById(R.id.editPrioridadeTarefa);
        edAssociado = findViewById(R.id.editAssociado);
        spinNomes = findViewById(R.id.spinnerNomes);
        progressAuth = findViewById(R.id.progressAuth);
        imgPerfil = findViewById(R.id.imgDeslogar);
        recyclerTarefas = findViewById(R.id.rTarefas);

        Intent intentMovimentacoes = new Intent(this, MovimentacaoActivity.class);

        edPrioridade.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               startActivity(intentMovimentacoes);
               return true;
            }
        });


            //preenchendo usuário, opcional, pode utilizar-se as props do próprio FirebaseAuth
            mUser = mAuth.getInstance().getCurrentUser();
            //Configurando Login
            loginGoogle = new LoginGoogle();
            mGoogleSignInClient = loginGoogle.configurarGoogle(getString(R.string.default_web_client_id), this);
            //Validando se há usuário logado
            if (mUser == null) {
                logarGoogle();
            } //Se já está logado, verifica se está cadastrado, caso nao esteja, efetua cadastro
            else {
                usuarioDAO.cadastrarUsuario();
               // if (movimentacoesDAO.buscarTarefa()){
                //    listaMovimentacaos = movimentacoesDAO.listarMovimentacoes(movimentacoesAdapter);

                    //retornarRecyclerTarefas();

                /*    Movimentacao tarefa1 = new Movimentacao("Pinto", "MUITO CURTO");
                    Movimentacao tarefa2 = new Movimentacao("CARALHO Q ODIO", "PUTA Q PARIU VAI SE FODER");
                    Movimentacao tarefa3 = new Movimentacao("CARALHO Q ODIO", "PUTA Q PARIU VAI SE FODER");
                    Movimentacao tarefa4 = new Movimentacao("CARALHO Q ODIO", "PUTA Q PARIU VAI SE FODER");

                    listaMovimentacaos.add(tarefa1);
                    listaMovimentacaos.add(tarefa2);
                    listaMovimentacaos.add(tarefa3);
                    listaMovimentacaos.add(tarefa4);

                    ConfigurarRecycler(getApplicationContext(), recyclerTarefas, listaMovimentacaos, movimentacoesAdapter);*/


              //  }

            }



    //    retornarRecyclerTarefas();
        
        ArrayAdapter <String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, nomeUsuarios);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

       // spinNomes.setAdapter(arrayAdapter);


       /* refenciaDb.child( "usuarios" ).child("administrador").child("nome").setValue("João");
        refenciaDb.child( "usuarios" ).child("visitantes").child("nome").setValue("Visitante Teste");*/



        btnEnviarTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
       //         gravarFirebase();
            }
        });


        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGoogle.Logout();
                Toast.makeText(MainActivity.this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });



    }


    public void retornarRecyclerTarefas(){


       movimentacoesDAO.listarMovimentacoes(listaMovimentacaos, movimentacoesAdapter);

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (listaMovimentacaos.size() > 0) {

                    Log.i("Movimentacao", "Lista iniciada, " + listaMovimentacaos.get(0).getDescTarefa());
                    ConfigurarRecycler(getApplicationContext(), recyclerTarefas, listaMovimentacaos, movimentacoesAdapter);

                    edNome.setText(listaMovimentacaos.get(2).getDescTarefa());
                    movimentacoesAdapter.notifyDataSetChanged();
                    handler.removeCallbacks(this);
                } else {
                    Log.i("Movimentacao", "Lista vazia");
                    handler.postDelayed(this, 3000);
                }
            }
        });


    }


    public void gravarFirebase(){

        if (!edDesc.getText().toString().equals("")
                || !edDesc.getText().toString().equals("")) {

            try {

        //        movimentacoesDAO.salvarMovimentacao(edNome.getText().toString(), edDesc.getText().toString());
               /* DatabaseReference tarefasAdd = refenciaDb.child("usuarios").child(tipoUsuario).child(tarefaObj.getPessoaAtribuida()).child(tarefaObj.getNomeTarefa());
                tarefasAdd.child("desc da Movimentacao").setValue(tarefaObj.getDescTarefa());
                tarefasAdd.child("pessoa Atribuida").setValue(tarefaObj.getPessoaAtribuida());
                tarefasAdd.child("prioridade da tarefa").setValue(tarefaObj.getPrioridadeTarefa());*/
                Toast.makeText(this, "Dados gravados com sucesso!", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(this, "Houve um erro ao tentar gravar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        else {
            Toast.makeText(this, "Preencha todos os campos antes de continuar.  ;)", Toast.LENGTH_LONG).show();
        }

    }

    public void logarGoogle(){
        Intent logarIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(logarIntent, GOOGLE_SIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Logando", "onResult override");
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try{

                GoogleSignInAccount conta = task.getResult(ApiException.class);

                if (conta != null) {

                    Handler handler = new Handler();

                    loginGoogle.firebaseAuthAuthWithGoogle(conta);

                    handler.post(new Runnable() {
                        int i = 0;
                        @Override
                        public void run() {

                            mUser = mAuth.getInstance().getCurrentUser();

                            handler.postDelayed(this, 1000);
                            progressAuth.setVisibility(View.VISIBLE);

                            if (mUser != null) {

                                    if (usuarioDAO.cadastrarUsuario()) {
                                        handler.removeCallbacks(this);
                                        progressAuth.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Logado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Log.i("Logando", mUser.getEmail());
                                    } else {
                                        i++;
                                        if (i >= 15) {
                                            handler.removeCallbacks(this);
                                            Toast.makeText(MainActivity.this, "Houve uma falha durante o login. Reinicie o aplicativo.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }

                            }else {Log.i("Logando", "Usuário null " + i);}

                /*    i++;

                        if (i >= 5) {
                            handler.removeCallbacks(this);
                            }*/
                        }
                    });
                }


            }catch (ApiException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       // ConfigurarRecycler(getApplicationContext(), recyclerTarefas, listaMovimentacaos, movimentacoesAdapter);


        //Validando se já há usuário logado
        if (mUser == null) {
            logarGoogle();
        } //Se já está logado, verifica se está cadastrado, caso nao esteja, efetua cadastro
        else {
            usuarioDAO.cadastrarUsuario();

        }
    }


}
