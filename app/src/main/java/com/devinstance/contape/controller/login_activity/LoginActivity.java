package com.devinstance.contape.controller.login_activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devinstance.contape.controller.transaction_activity.TransactionActivity;
import com.devinstance.contape.DAL.FirebaseConfig;
import com.devinstance.contape.DAL.TransactionDAO;
import com.devinstance.contape.DAL.UsersDAO;
import com.devinstance.contape.model.User;
import com.devinstance.contape.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import static com.devinstance.contape.controller.login_activity.LoginGoogle.GOOGLE_SIGN;

public class LoginActivity extends AppCompatActivity {


    //Variavel de autentificacao para puxar usuário logado e deslogar
    private FirebaseAuth mAuth;
    //Variavel que vai receber o usuário, mUser = mAuth.getCurrentUser();
    private FirebaseUser mUser;
    //Configurações de Login com Google, necessário acompanhar GoogleSignInOption, incluso em LooginGoogle.java
    private GoogleSignInClient mGoogleSignInClient;
    //Variavel da classe que puxaremos os objetos
    private LoginGoogle loginGoogle;
    //Variavel que dá origem ao objeto que controlará o CRUD de dados no BD referente a usuários
    private final UsersDAO usersDAO = new UsersDAO();
    //Variavel de usuário
    private final User user = new User();
    //Variavel responsável por abrir o dialogo para o usuário colocar o nome
    private Dialog dialogNome;
    //Variavel que receberá o nome do usuário caso abra o popup
    private String nomeUsuario = null;
    //Botao de logar/deslogar
    private Button btnFazerLogin;
    //Botao para testes da tela de Movimentacao
    private Button btnMov;
    //Inicia movDAO para adiantar processo
    private TransactionDAO transactionDAO;
    //ProgressBar
    private ProgressBar pLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbarLogin);

        btnFazerLogin = findViewById(R.id.btnLogin);
        pLogin = findViewById(R.id.progressLoadingGoogle);


        btnFazerLogin.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                fazerLogin();
            }else {
                validarCadastro();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        //Verificando login
        mAuth = FirebaseConfig.getFirebaseAuth();
        //preenchendo usuário, opcional, pode utilizar-se as props do próprio FirebaseAuth
        mUser = FirebaseConfig.getFirebaseAuth().getCurrentUser();
        //Se já está logado, vai p outra tela
        if (mUser != null) validarCadastro();
        //Configurando Login
        loginGoogle = new LoginGoogle();
        mGoogleSignInClient = loginGoogle.configurarGoogle(getString(R.string.default_web_client_id), this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try{

                GoogleSignInAccount conta = task.getResult(ApiException.class);

                if (conta != null) {

                    //Configura login Google
                    loginGoogle.firebaseAuthAuthWithGoogle(conta);
                    //Antecipa método de pegar usuário
                    mUser = FirebaseAuth.getInstance().getCurrentUser();
                    //Executa método de pegar ou cadastrar usuário
                    validarCadastro();

                }else Toast.makeText(loginGoogle, "Conta Google nula... Por favor, tente novamente.", Toast.LENGTH_SHORT).show();

            }catch (ApiException e){
                e.printStackTrace();
            }

        }
    }

    private void solicitaNomeCadastro(User userCadastrado){
        EditText editNome;
        Button btnCadastrar;
        dialogNome = new Dialog(this);

        dialogNome.setContentView(R.layout.dialog_login);
        editNome = dialogNome.findViewById(R.id.editNomeUsuario);
        btnCadastrar = (Button) dialogNome.findViewById(R.id.btnJuntar);



        btnCadastrar.setOnClickListener(view -> {

            if (editNome.getText().toString().isEmpty()){
                editNome.setError("Digite um nome para continuar!");
            }else {
                nomeUsuario = editNome.getText().toString();
                user.setNome(editNome.getText().toString());
                dialogNome.dismiss();
            }
        });
        dialogNome.setCanceledOnTouchOutside(false);
        dialogNome.show();

    }

    private void fazerLogin(){
        pLogin.setVisibility(View.VISIBLE);
        btnFazerLogin.setVisibility(View.GONE);
        Intent logarIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(logarIntent, GOOGLE_SIGN);

    }

    private void telaMovimentacao(){
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
    }

    //Método genérico para determinar quanto tempo será aguardado para ir para a proxima tela
    private void finalizarLoading(Handler handler, int tempoEspera){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(this);
                pLogin.setVisibility(View.GONE);
                telaMovimentacao();
            }
        }, tempoEspera);
    }

    //Método que validará se o user está logado ou não
    private void validarCadastro(){

         pLogin.setVisibility(View.VISIBLE);
         btnFazerLogin.setVisibility(View.GONE);

         Handler handler = new Handler();

            handler.post(new Runnable() {
                int i = 0;
                @Override
                public void run() {

                    mUser = FirebaseAuth.getInstance().getCurrentUser();

                    handler.postDelayed(this, 800);
                    //    progressAuth.setVisibility(View.VISIBLE);

                    //Verificar se o usuário identificado está nulo

                    if (mUser != null) {
                        if (usersDAO.validateOrSubUsuario()) {
                            handler.removeCallbacks(this);
                            Toast.makeText(LoginActivity.this, "Logado com sucesso! Redirecionando...", Toast.LENGTH_SHORT).show();

                            String displayName = mUser.getDisplayName();

                            for (UserInfo userInfo : mUser.getProviderData()) {
                                 if (displayName == null && userInfo.getDisplayName() != null) {
                                    displayName = userInfo.getDisplayName();

                                }
                            }

                            Log.i("Logando", mUser.getEmail() + ", nome: " + displayName);

                          //De acordo com o contador determina velocidade do loading da proxima tela
                            if (i < 7) {
                                pLogin.setVisibility(View.GONE);
                                telaMovimentacao();
                            } else if (i >= 7 && i < 15) {
                                finalizarLoading(handler, 1000);
                            } else if (i >= 15 && i < 30) {
                                finalizarLoading(handler, 2300);
                            } else if (i >= 30) {
                                finalizarLoading(handler, 4500);
                            }

                        }
                    }else {

                        //Caso seja nulo, aumenta o contador em 1, caso atinja 5 o app será interrompido
                        Log.i("Logando", "Usuário null " + i);
                        i++;
                        if (i == 10) {
                            Toast.makeText(getApplicationContext(), "Conexão lenta... Pode ser mais demorado o carregamento...", Toast.LENGTH_SHORT).show();
                        } /*else if (i >= 75) {
                            handler.removeCallbacks(this);
                            Toast.makeText(LoginActivity.this, "Houve uma falha durante o login. Reinicie o aplicativo.", Toast.LENGTH_SHORT).show();
                            finish();
                        }*/
                    }

                }
            });
        }
        
}
