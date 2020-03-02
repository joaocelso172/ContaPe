package com.example.aulafirebase;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import br.com.sapereaude.maskedEditText.MaskedEditText;

@RequiresApi(api = Build.VERSION_CODES.M)
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
    private MaskedEditText editHoraDespesa;
    private Bundle bundleData;
    private String anoSel, mesSel;

    private Boolean dataClicada = false;



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
        editHoraDespesa = findViewById(R.id.editHoraDespesa);


        //Adapter para utilizar o spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);


        //Setando o Adapter ao Spinner
        spinnerCategoriaDespesa.setAdapter(arrayAdapter);

        bundleData = getIntent().getExtras();
        if( (!bundleData.getString("ano").equals(null)) && (!bundleData.getString("mes").equals(null)) ) {
            anoSel = bundleData.getString("ano");
            mesSel = bundleData.getString("mes");
            edDataDespesa.setText( "01" + "/" + mesSel + "/" + anoSel );
        }else edDataDespesa.setText( DateCustom.dataAtual() ); //Preenche com a data atual

        //Preenche com a hora atual
        editHoraDespesa.setText(DateCustom.horaAtual());

        edDataDespesa.setOnTouchListener((v, event) -> {
            if (!dataClicada){
                dialogCalendario();
            }else dataClicada = false;
            return true;
        });

        editHoraDespesa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editHoraDespesa.setText("");
                return false;
            }
        });


        /*editHoraDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editHoraDespesa.getText().toString().isEmpty()) editHoraDespesa.setText(DateCustom.horaAtual());
            }
        });*/

        editHoraDespesa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String[] hora;

                hora = DateCustom.recuperarHora(editHoraDespesa.getText().toString());

                if ( (!hasFocus && editHoraDespesa.getText().toString().equals(""))) editHoraDespesa.setText(DateCustom.horaAtual());
                if ((!hasFocus && hora.length > 0 && hora[0] != "")) {
                    if (Integer.parseInt(hora[0]) >= 24 ){
                        editHoraDespesa.setError("A hora não pode ser superior a 24!");
                        editHoraDespesa.setText("");
                    }else if ( !hasFocus && hora.length == 1 && hora[0] != ""){
                        if (Integer.parseInt(hora[0]) < 10) hora[0] = "0" + hora[0];
                        editHoraDespesa.setText(hora[0] + ":" + "00");
                    }else if ( !hasFocus && hora.length == 2 && hora[1] != ""){
                        if (Integer.parseInt(hora[1]) >= 60 ){
                            editHoraDespesa.setError("O minuto não pode ser superior a 60!");
                            editHoraDespesa.setText("");
                        }else if (Integer.parseInt(hora[1]) < 6) hora[1] = hora[1] + "0"; else if (Integer.parseInt(hora[1]) < 10) hora[1] = "0" + hora[1];
                        editHoraDespesa.setText(hora[0] + ":" + hora[1]);
                    }
                }


            }
        });

        edDataDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
            despesaSalva.setDataTarefa(edDataDespesa.getText().toString() + " - " + editHoraDespesa.getText());
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
                         /*  if (saldoAtual >= usuario.getValorAlerta()) Toast.makeText(this, "Despesa salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                           if (saldoAtual < usuario.getValorAlerta()) Toast.makeText(this, "Despesa salva... Atente-se as finanças! Saldo abaixo do seguro...", Toast.LENGTH_SHORT).show(); //Adicionar valor seguro*/
                    Toast.makeText(this, "Despesa salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                            finish();
                }else Toast.makeText(this, "Por favor, conecte a internet e tente novamente...", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this, "Parece que a conexão está um pouco lenta... Tente novamente", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Preencha todos os campos corretamente para poder continuar", Toast.LENGTH_SHORT).show();
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

        String[] hora;

        hora = DateCustom.recuperarHora(editHoraDespesa.getText().toString());

        if (editHoraDespesa.getText().toString().isEmpty()){
            editHoraDespesa.setError("Preencha a hora da receita para continuar!");
            return false;
        }else if (Integer.parseInt(hora[0]) >= 24){
            editHoraDespesa.setError("A hora não pode ser superior a 24!");
            return false;
        }else if (Integer.parseInt(hora[1]) >= 60){
            editHoraDespesa.setError("O minuto não pode ser superior a 60!");
            return false;
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
            }
        });

        return resumoRecuperado;
    }

    private void dialogCalendario(){
        dataClicada = true;
        Dialog dialogoCalendario = new Dialog(this);

        dialogoCalendario.setContentView(R.layout.calendario_selecao);
        MaterialCalendarView materialCalendarViewReceita = dialogoCalendario.findViewById(R.id.calendarioAddMovimentacao);
        Button btnConfirmarData = dialogoCalendario.findViewById(R.id.btnConfirmarCalendario);

        CharSequence[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        materialCalendarViewReceita.setTitleMonths(meses);

        CharSequence[] semanas = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        materialCalendarViewReceita.setWeekDayLabels(semanas);

        materialCalendarViewReceita.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String anoSel = String.valueOf( date.getYear() );
                String mesSel = String.valueOf( date.getMonth() );
                String diaSel = String.valueOf( date.getDay() );

                //Se for menor que dez, adiciona zero
                if (Integer.parseInt(mesSel) < 10) mesSel = "0" + Integer.parseInt(mesSel);
                if (Integer.parseInt(diaSel) < 10) diaSel = "0" + Integer.parseInt(diaSel);

                edDataDespesa.setText(diaSel + "/" + mesSel + "/" + anoSel);

            }
        });

        btnConfirmarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCalendario.dismiss();
            }
        });


        dialogoCalendario.show();


    }

}
