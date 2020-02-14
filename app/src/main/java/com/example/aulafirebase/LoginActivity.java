package com.example.aulafirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.UsuarioDAO;
import com.example.aulafirebase.Model.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.aulafirebase.LoginGoogle.GOOGLE_SIGN;

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
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    //Variavel de usuário
    private Usuario usuario = new Usuario();
    //Variavel responsável por abrir o dialogo para o usuário colocar o nome
    private Dialog dialogNome;
    //Variavel que receberá o nome do usuário caso abra o popup
    private String nomeUsuario = null;

    private Button btnFazerLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnFazerLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseConfig.getFirebaseAuth();

        if (mAuth.getCurrentUser() == null) {
            btnFazerLogin.setText("Logar");
        }else {
            btnFazerLogin.setText("Deslogar");
        }

        btnFazerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    btnFazerLogin.setText("Deslogar");
                    fazerLogin();
                }else {
                    btnFazerLogin.setText("Logar");
                    loginGoogle.Logout();
                    Toast.makeText(LoginActivity.this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //preenchendo usuário, opcional, pode utilizar-se as props do próprio FirebaseAuth
        mUser = FirebaseConfig.getFirebaseAuth().getCurrentUser();
        //Configurando Login
        loginGoogle = new LoginGoogle();
        mGoogleSignInClient = loginGoogle.configurarGoogle(getString(R.string.default_web_client_id), this);

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
                    Handler handler1 = new Handler();

                    loginGoogle.firebaseAuthAuthWithGoogle(conta);

                    handler.post(new Runnable() {
                        int i = 0;
                        @Override
                        public void run() {

                            mUser = mAuth.getInstance().getCurrentUser();

                            handler.postDelayed(this, 2000);
                        //    progressAuth.setVisibility(View.VISIBLE);

                            //Verificar se o usuário identificado está nulo
                            if (mUser != null) {

                                //Verifica usuário no BD

                                     usuario = usuarioDAO.getUsuarioCadastradoFirebase();

                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {



                                    if (usuario.isCadastrado()) {
                                        handler.removeCallbacks(this);
                                        handler1.removeCallbacks(this);
                                //        progressAuth.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Logado com sucesso!", Toast.LENGTH_SHORT).show();
                                    }else {


                                            if (usuario.getNome() == null) {
                                                handler.removeCallbacks(this);
                                                handler1.removeCallbacks(this);
                                                //solicitar nome do usuário e cadastrar aqui
                                                solicitaNomeCadastro(usuario);

                                            } else if (!usuario.getNome().isEmpty()) {
                                                handler.removeCallbacks(this);
                                                handler1.removeCallbacks(this);
                                                //cadastrar usuário aqui

                                            }
                                    }
                                    }
                                }, 3000);

                            }else {
                                //Caso seja nulo, aumenta o contador em 1, caso atinja 5 o app será interrompido
                                Log.i("Logando", "Usuário null " + i);
                                i++;
                                if (i >= 5) {
                                    handler.removeCallbacks(this);
                                    Toast.makeText(LoginActivity.this, "Houve uma falha durante o login. Reinicie o aplicativo.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                        }
                    });
                }


            }catch (ApiException e){
                e.printStackTrace();
            }

        }
    }

    private void solicitaNomeCadastro(Usuario usuarioCadastrado){
        EditText editNome;
        Button btnCadastrar;
        dialogNome = new Dialog(this);

        dialogNome.setContentView(R.layout.dialog_login);
        editNome = (EditText) dialogNome.findViewById(R.id.editNomeUsuario);
        btnCadastrar = (Button) dialogNome.findViewById(R.id.btnJuntar);



        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editNome.getText().toString().isEmpty()){
                    editNome.setError("Digite um nome para continuar!");
                }else {
                    nomeUsuario = editNome.getText().toString();
                    usuario.setNome(editNome.getText().toString());
                    usuarioDAO.salvarUser(usuarioCadastrado);
                    dialogNome.dismiss();
                }
            }
        });
        dialogNome.setCanceledOnTouchOutside(false);
        dialogNome.show();

    }

    public void fazerLogin(){
        Intent logarIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(logarIntent, GOOGLE_SIGN);

    }
}
