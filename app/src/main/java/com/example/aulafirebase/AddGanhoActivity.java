package com.example.aulafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.helper.DateCustom;
import com.google.android.material.textfield.TextInputEditText;

public class AddGanhoActivity extends AppCompatActivity {


    //Botão para salvar receita
    private Button btnSalvarReceita;
    //Edit Text referente a campos
    private TextInputEditText edDataGanho, edDescGanho, edNomeGanho;
    //EditText referente ao valor descrito
    private EditText edValorGanho;
    //Spinner contendo categorias
    private Spinner spinnerCategoriaGanho;
    //Objeto de acesso ao nó de movimentacoes
    private MovimentacoesDAO receitasDAO = new MovimentacoesDAO();
    //Array de Strings que define as categorias disponíveis
    private String[] categorias = new String[]{"Salário", "Mesada", "Reembolso", "Outros"};
    //Receita Total
    private Double receitaTotal = receitasDAO.getReceitaTotal();
    //Saldo antes da execução
    private Double saldoAtual = receitasDAO.getSaldo();
    //Receita atualizada
    private Double receitaAtualizada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ganho);


        btnSalvarReceita = findViewById(R.id.btnSalvarGanho);
        edDataGanho = findViewById(R.id.edDataGanho);
        edDescGanho = findViewById(R.id.edDescGanho);
        edNomeGanho = findViewById(R.id.edNomeGanho);
        edValorGanho = findViewById(R.id.edValorGanho);
        spinnerCategoriaGanho = findViewById(R.id.spinnerCatGanho);

        //Adapter para utilizar o spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        //Setando o Adapter ao Spinner
        spinnerCategoriaGanho.setAdapter(arrayAdapter);

        //Preenche com a data atual, incluindo data
        edDataGanho.setText( DateCustom.dataAtual() );

        btnSalvarReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarReceita();
            }
        });


    }


    private void salvarReceita(){
        //Objeto de movimentacao
        Movimentacao receitaSalva = new Movimentacao();

        receitaTotal = receitasDAO.getReceitaTotal();
        saldoAtual = receitasDAO.getSaldo();

        //Se for true, salva receita
        if (validarCampos()){

            //Colocando o valor em uma varíavel, já que será usado múltiplas vezes
            Double valor = Double.parseDouble(edValorGanho.getText().toString());
            //Setando valores de acordo com os campos
            receitaSalva.setValor(valor);
            receitaSalva.setNomeTarefa(edNomeGanho.getText().toString());
            receitaSalva.setDescTarefa(edDescGanho.getText().toString());
            receitaSalva.setDataTarefa(edDataGanho.getText().toString());
            receitaSalva.setCategoria(spinnerCategoriaGanho.getSelectedItem().toString());
            //Caso Receita = 'r'; Caso Despesa = 'd'.
            receitaSalva.setTipo("r");

            if (receitaTotal != null) {
                //Soma a receita total + o valor descrito
                receitaAtualizada = receitaTotal + valor;
                saldoAtual += receitaAtualizada;
                //Atualiza no BD, nó usuário
                receitasDAO.atualizarReceita(receitaAtualizada);
                //Salva a receita
                if (receitasDAO.salvarMovimentacao(receitaSalva)){
                        Toast.makeText(this, "Receita salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                        finish();
                }
            }else Toast.makeText(this, "Parece que a conexão está um pouco lenta... Tente novamente", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Preencha todos os campos para poder continuar", Toast.LENGTH_SHORT).show();

    }

    private Boolean validarCampos(){

        //Valida se campos estão preenchidos
        if (edValorGanho.getText().toString().isEmpty()){
            edValorGanho.setError("Preencha o valor para continuar!");
            if (edNomeGanho.getText().toString().isEmpty()) edNomeGanho.setError("Preencha o nome da receita para continuar!");
                if (edDescGanho.getText().toString().isEmpty())edDescGanho.setError("Preencha a descrição para continuar!");
                    if (edDataGanho.getText().toString().isEmpty()) edDataGanho.setError("Preencha a data para continuar!");

            return false;
        }

        if (edNomeGanho.getText().toString().isEmpty()){
            edNomeGanho.setError("Preencha o nome da receita para continuar!");
            if (edDescGanho.getText().toString().isEmpty())edDescGanho.setError("Preencha a descrição para continuar!");
                if (edDataGanho.getText().toString().isEmpty()) edDataGanho.setError("Preencha a data para continuar!");

                return false;
        }

        if (edDescGanho.getText().toString().isEmpty()){
            edDescGanho.setError("Preencha a descrição para continuar!");
            if (edDataGanho.getText().toString().isEmpty()) edDataGanho.setError("Preencha a data para continuar!");

            return false;
        }


        if (spinnerCategoriaGanho.getSelectedItem().toString().isEmpty()){
            spinnerCategoriaGanho.setSelection(0);
            Toast.makeText(this, "Categoria padrão selecionada", Toast.LENGTH_SHORT).show();
        }

        //Caso estejam, retorna true, caso não, retornam false
        return true;
    }
}
