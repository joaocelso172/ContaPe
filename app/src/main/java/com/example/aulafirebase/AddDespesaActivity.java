package com.example.aulafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.helper.DateCustom;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddDespesaActivity extends AppCompatActivity {


    //Botão para salvar despesa
    private Button btnSalvarDespesa;
    //Edit Text referente a campos
    private TextInputEditText edDataDespesa, edDescDespesa, edNomeDespesa;
    //EditText referente ao valor descrito
    private EditText edValorDespesa;
    //Spinner contendo categorias
    private Spinner spinnerCategoriaDespesa;
    //Objeto de acesso ao nó de movimentacoes
    private MovimentacoesDAO despesasDAO = new MovimentacoesDAO();
    //Array de Strings que define as categorias disponíveis
    private String[] categorias = new String[]{"Almoço", "Café da manhã", "Sobremesa", "Outros"};
    //Despesa Total
    private Double despesaTotal = despesasDAO.getDespesaTotal();
    //Saldo antes da execução
    private Double saldoAtual = despesasDAO.getSaldo();
    //Despesa atualizada
    private Double despesaAtualizada;




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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);


        //Setando o Adapter ao Spinner
        spinnerCategoriaDespesa.setAdapter(arrayAdapter);

        //Preenche com a data atual, incluindo data
        edDataDespesa.setText( DateCustom.dataAtual() );

        btnSalvarDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDespesa(v);
            }
        });

    }

    private void salvarDespesa(View v){
        //Objeto de movimentacao
        Movimentacao despesaSalva = new Movimentacao();

        despesaTotal = despesasDAO.getDespesaTotal();
        saldoAtual = despesasDAO.getSaldo();

        //Se for true, salva despesa
        if (validarCampos()){

            //Colocando o valor em uma varíavel, já que será usado múltiplas vezes
            Double valor = Double.parseDouble(edValorDespesa.getText().toString());
            //Setando valores de acordo com os campos
            despesaSalva.setValor(valor);
            despesaSalva.setNomeTarefa(edNomeDespesa.getText().toString());
            despesaSalva.setDescTarefa(edDescDespesa.getText().toString());
            despesaSalva.setDataTarefa(edDataDespesa.getText().toString());
            despesaSalva.setCategoria(spinnerCategoriaDespesa.getSelectedItem().toString());
            //Caso despesa = 'r'; Caso Despesa = 'd'.
            despesaSalva.setTipo("d");

            if (despesaTotal != null) {
                //Soma a despesa total + o valor descrito
                despesaAtualizada = despesaTotal + valor;
                saldoAtual -= despesaAtualizada;
                //Atualiza no BD, nó usuário
                despesasDAO.atualizarDespesa(despesaAtualizada);

                //Salva a despesa
                if (despesasDAO.salvarMovimentacao(despesaSalva)) {
                    if (saldoAtual >= 100) Toast.makeText(this, "Despesa salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                    if (saldoAtual < 100){
                        Toast.makeText(this, "Despesa salva... Atente-se as finanças! Saldo abaixo do seguro...", Toast.LENGTH_SHORT).show();
                    }//Adicionar valor seguro
                        finish();
                }
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

}
