package com.example.aulafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.DAL.ResumoMensalDAO;
import com.example.aulafirebase.DAL.UsuariosDAO;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.ResumoMensal;
import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.helper.DateCustom;
import com.google.android.material.textfield.TextInputEditText;

public class AddGanhoActivity extends AppCompatActivity {

    //Objeto de verificacao de conexão
    private ConnectivityManager cm;
    private NetworkInfo networkInfo;
    //Botão para salvar receita
    private Button btnSalvarReceita;
    //Edit Text referente a campos
    private TextInputEditText edDataReceita, edDescReceita, edNomeReceita;
    //EditText referente ao valor descrito
    private EditText edValorReceita;
    //Spinner contendo categorias
    private Spinner spinnerCategoriaReceita;
    //Objeto de acesso ao nó de movimentacoes
    private final MovimentacoesDAO receitasDAO = new MovimentacoesDAO();
    //Objeto de acesso aos resumos do nó de movimentacoes
    private final ResumoMensalDAO resumoMensalDAO = new ResumoMensalDAO();
    //Objeto ResumoMensal
    private ResumoMensal resumoMensal = new ResumoMensal();
    //Array de Strings que define as categorias disponíveis
    private final String[] categorias = new String[]{"Salário", "Freelance", "Reembolso", "Outros"};
    //Recupera UsuárioDAO
    private final UsuariosDAO usuariosDAO = new UsuariosDAO();
    //Recupera Usuario, deve ser recuperado SEMPRE antes de qualquer coisa
    private Usuario usuario = usuariosDAO.getUsuario();
    //Atributos resumo mensal
    private Double receitaMensal;
    private Double saldoMensal;
    //Atributo que retorna true quando resumo está ok
    private Boolean resumoRecuperado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ganho);


        btnSalvarReceita = findViewById(R.id.btnSalvarGanho);
        edDataReceita = findViewById(R.id.edDataGanho);
        edDescReceita = findViewById(R.id.edDescGanho);
        edNomeReceita = findViewById(R.id.edNomeGanho);
        edValorReceita = findViewById(R.id.edValorGanho);
        spinnerCategoriaReceita = findViewById(R.id.spinnerCatGanho);

        //Adapter para utilizar o spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        //Setando o Adapter ao Spinner
        spinnerCategoriaReceita.setAdapter(arrayAdapter);

        //Preenche com a data atual, incluindo data
        edDataReceita.setText( DateCustom.dataAtual() );

        btnSalvarReceita.setOnClickListener(v -> salvarReceita());


    }

        private void salvarReceita(){
        //Objeto de movimentacao
        Movimentacao receitaSalva = new Movimentacao();

        //Recupera usuário antes de salvar
        usuario = usuariosDAO.getUsuario();
        //Seta valores
        //Receita Total
        Double receitaTotal = usuario.getReceitaTotal();
        //Saldo antes da execução
        Double saldoAtual = usuario.getSaldoDisponivel();

        //Se for true, salva receita
        if (validarCampos()){
            String dataMov;
            //Trata campo data para agrupar em ano, mes e dia
            dataMov = DateCustom.firebaseFormatDate(edDataReceita.getText().toString());
            //Colocando o valor em uma varíavel, já que será usado múltiplas vezes
            Double valor = Double.parseDouble(edValorReceita.getText().toString());
            //Chama método para recuperar resumo mensal
            atualizarOuCriarResumoMensal(dataMov, valor);
            //Setando valores de acordo com os campos
            receitaSalva.setValor(valor);
            receitaSalva.setDescTarefa(edDescReceita.getText().toString());
            receitaSalva.setDataTarefa(edDataReceita.getText().toString());
            receitaSalva.setCategoria(spinnerCategoriaReceita.getSelectedItem().toString());
            //Caso receita = 'r'; Caso Receita = 'd'.
            receitaSalva.setTipo("r");

            //Se receita não for nula significa que algo retornou
            if (receitaTotal != null) {
                //Soma a receita total + o valor descrito
                //Receita atualizada
                Double receitaAtualizada = receitaTotal + valor;
                saldoAtual -= receitaAtualizada;
                //Verifica conexão do celular
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                //Salva a receita, atualiza atributo receitaGeral e saldoGeral desde que tenha internet
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
                        receitasDAO.salvarMovimentacao(receitaSalva);
                        receitasDAO.atualizarReceita(receitaAtualizada);
                        Toast.makeText(this, "Receita salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                        finish();
                }else Toast.makeText(this, "Por favor, conecte a internet e tente novamente...", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this, "Parece que a conexão está um pouco lenta... Tente novamente", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Preencha todos os campos para poder continuar", Toast.LENGTH_SHORT).show();
    }


    private Boolean validarCampos(){

        //Valida se campos estão preenchidos
        if (edValorReceita.getText().toString().isEmpty()){
            edValorReceita.setError("Preencha o valor para continuar!");
            if (edNomeReceita.getText().toString().isEmpty()) edNomeReceita.setError("Preencha o nome da receita para continuar!");
                if (edDescReceita.getText().toString().isEmpty())edDescReceita.setError("Preencha a descrição para continuar!");
                    if (edDataReceita.getText().toString().isEmpty()) edDataReceita.setError("Preencha a data para continuar!");

            return false;
        }

        if (edNomeReceita.getText().toString().isEmpty()){
            edNomeReceita.setError("Preencha o nome da receita para continuar!");
            if (edDescReceita.getText().toString().isEmpty())edDescReceita.setError("Preencha a descrição para continuar!");
                if (edDataReceita.getText().toString().isEmpty()) edDataReceita.setError("Preencha a data para continuar!");

                return false;
        }

        if (edDescReceita.getText().toString().isEmpty()){
            edDescReceita.setError("Preencha a descrição para continuar!");
            if (edDataReceita.getText().toString().isEmpty()) edDataReceita.setError("Preencha a data para continuar!");

            return false;
        }


        if (spinnerCategoriaReceita.getSelectedItem().toString().isEmpty()){
            spinnerCategoriaReceita.setSelection(0);
            Toast.makeText(this, "Categoria padrão selecionada", Toast.LENGTH_SHORT).show();
        }

        //Caso estejam, retorna true, caso não, retornam false
        return true;
    }

    private Boolean atualizarOuCriarResumoMensal(String dataMovimentacao, Double valorReceita){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Verifica se há resumo, caso não haja cria e retorna
                resumoMensal = resumoMensalDAO.getOrSubResumoMensal(dataMovimentacao);
                //Verifica se há algo no resumoMensal a cada 300 milisegundos
                handler.postDelayed(this, 300);
                //Se não for nulo, significa que algo retornou
                if (resumoMensal.getReceitaMensal() != null) {
                    //Seta informações
                    receitaMensal = resumoMensal.getReceitaMensal();
                    resumoMensal.setReceitaMensal(receitaMensal + valorReceita);
                    //Método de saldo, deve ser sempre igual
                    resumoMensal.setSaldoMensal(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal());
                    resumoMensalDAO.setResumoMensal(resumoMensal, dataMovimentacao);
                    handler.removeCallbacks(this);
                    resumoRecuperado =  true;
                }else resumoRecuperado = false; //Se for false, significa que nada retornou e precisa ser executado de novo

                Log.i("Logando", "Status: " + resumoRecuperado);
            }
        });

        return resumoRecuperado;
    }
}
