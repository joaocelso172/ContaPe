package com.example.aulafirebase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

@RequiresApi(api = Build.VERSION_CODES.M)
public class AddDespesaActivity extends AppCompatActivity {
    
    //Objeto de verificacao de conexão
    private ConnectivityManager cm;
    private NetworkInfo networkInfo;
    //Botão para salvar despesa
    private Button btnSalvarDespesa;
    //Edit Text referente a campos
    private TextInputEditText edDataDespesa, edDescDespesa, edNomeDespesa;
    //EditText referente ao valor descrito
    private EditText edValorDespesa;
    //Spinner contendo categorias
    private Spinner spinnerCategoriaDespesa;
    //Objeto de acesso ao nó de movimentacoes
    private final MovimentacoesDAO despesasDAO = new MovimentacoesDAO();
    //Objeto de acesso aos resumos do nó de movimentacoes
    private final ResumoMensalDAO resumoMensalDAO = new ResumoMensalDAO();
    //Objeto ResumoMensal
    private ResumoMensal resumoMensal = new ResumoMensal();
    //Array de Strings que define as categorias disponíveis
    private final String[] categorias = new String[]{"Almoço", "Café da manhã", "Sobremesa", "Outros"};
    //Recupera UsuárioDAO
    private final UsuariosDAO usuariosDAO = new UsuariosDAO();
    //Recupera Usuario, deve ser recuperado SEMPRE antes de qualquer coisa
    private Usuario usuario = usuariosDAO.getUsuario();
    //Atributos resumo mensal
    private Double despesaMensal;
    private Double saldoMensal;
    //Atributo que retorna true quando resumo está ok
    private Boolean resumoRecuperado = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_despesa);

        btnSalvarDespesa = findViewById(R.id.btnSalvarDespesa);
        edDataDespesa = findViewById(R.id.edDataDespesa);
        edDescDespesa = findViewById(R.id.edDescDespesa);
        edNomeDespesa = findViewById(R.id.edNomeDespesa);
        edValorDespesa = findViewById(R.id.edValorDespesa);
        spinnerCategoriaDespesa = findViewById(R.id.spinnerCatDespesa);


        //Adapter para utilizar o spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);


        //Setando o Adapter ao Spinner
        spinnerCategoriaDespesa.setAdapter(arrayAdapter);

        //Preenche com a data atual, incluindo data
        edDataDespesa.setText( DateCustom.dataAtual() );

        btnSalvarDespesa.setOnClickListener(v -> salvarDespesa());

    }

    private void salvarDespesa(){
        //Objeto de movimentacao
        Movimentacao despesaSalva = new Movimentacao();

        //Recupera usuário antes de salvar
        usuario = usuariosDAO.getUsuario();
        //Seta valores
        //Despesa Total
        Double despesaTotal = usuario.getDespesaTotal();
        //Saldo antes da execução
        Double saldoAtual = usuario.getSaldoDisponivel();

        //Se for true, salva despesa
        if (validarCampos()){
            String dataMov;
            //Trata campo data para agrupar em ano, mes e dia
            dataMov = DateCustom.firebaseFormatDate(edDataDespesa.getText().toString());
            //Colocando o valor em uma varíavel, já que será usado múltiplas vezes
            Double valor = Double.parseDouble(edValorDespesa.getText().toString());
            //Chama método para recuperar resumo mensal
            atualizarOuCriarResumoMensal(dataMov, valor);
            //Setando valores de acordo com os campos
            despesaSalva.setValor(valor);
            despesaSalva.setDescTarefa(edDescDespesa.getText().toString());
            despesaSalva.setDataTarefa(edDataDespesa.getText().toString());
            despesaSalva.setCategoria(spinnerCategoriaDespesa.getSelectedItem().toString());
            //Caso despesa = 'r'; Caso Despesa = 'd'.
            despesaSalva.setTipo("d");

            //Se despesa não for nula significa que algo retornou
            if (despesaTotal != null) {
                //Soma a despesa total + o valor descrito
                //Despesa atualizada
                Double despesaAtualizada = despesaTotal + valor;
                saldoAtual -= despesaAtualizada;
                //Verifica conexão do celular
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                //Salva a despesa, atualiza atributo despesaGeral e saldoGeral desde que tenha internet
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
                        despesasDAO.salvarMovimentacao(despesaSalva);
                        despesasDAO.atualizarDespesa(despesaAtualizada);
                           if (saldoAtual >= usuario.getValorAlerta())
                               Toast.makeText(this, "Despesa salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                             if (saldoAtual < usuario.getValorAlerta())
                               Toast.makeText(this, "Despesa salva... Atente-se as finanças! Saldo abaixo do seguro...", Toast.LENGTH_SHORT).show(); //Adicionar valor seguro
                            finish();
                }else Toast.makeText(this, "Por favor, conecte a internet e tente novamente...", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this, "Parece que a conexão está um pouco lenta... Tente novamente", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Preencha todos os campos para poder continuar", Toast.LENGTH_SHORT).show();
    }

    private Boolean validarCampos(){

        //Valida se campos estão preenchidos
        if (edValorDespesa.getText().toString().isEmpty()){
            edValorDespesa.setError("Preencha o valor para continuar!");
            if (edNomeDespesa.getText().toString().isEmpty()) edNomeDespesa.setError("Preencha o nome da despesa para continuar!");
            if (edDescDespesa.getText().toString().isEmpty())edDescDespesa.setError("Preencha a descrição para continuar!");
            if (edDataDespesa.getText().toString().isEmpty()) edDataDespesa.setError("Preencha a data para continuar!");

            return false;
        }

        if (edNomeDespesa.getText().toString().isEmpty()){
            edNomeDespesa.setError("Preencha o nome da despesa para continuar!");
            if (edDescDespesa.getText().toString().isEmpty())edDescDespesa.setError("Preencha a descrição para continuar!");
            if (edDataDespesa.getText().toString().isEmpty()) edDataDespesa.setError("Preencha a data para continuar!");

            return false;
        }

        if (edDescDespesa.getText().toString().isEmpty()){
            edDescDespesa.setError("Preencha a descrição para continuar!");
            if (edDataDespesa.getText().toString().isEmpty()) edDataDespesa.setError("Preencha a data para continuar!");

            return false;
        }


        if (spinnerCategoriaDespesa.getSelectedItem().toString().isEmpty()){
            spinnerCategoriaDespesa.setSelection(0);
            Toast.makeText(this, "Categoria padrão selecionada", Toast.LENGTH_SHORT).show();
        }

        //Caso estejam, retorna true, caso não, retornam false
        return true;
    }

    private Boolean atualizarOuCriarResumoMensal(String dataMovimentacao, Double valorDespesa){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Verifica se há resumo, caso não haja cria e retorna
                resumoMensal = resumoMensalDAO.getOrSubResumoMensal(dataMovimentacao);
                //Verifica se há algo no resumoMensal a cada 300 milisegundos
                handler.postDelayed(this, 300);
                //Se não for nulo, significa que algo retornou
                if (resumoMensal.getDespesaMensal() != null) {
                        //Seta informações
                        despesaMensal = resumoMensal.getDespesaMensal();
                        resumoMensal.setDespesaMensal(despesaMensal + valorDespesa);
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
