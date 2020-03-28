package com.example.aulafirebase.Controller.ActivityMovimentacao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.GrupoDAO;
import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.DAL.ResumoMensalDAO;
import com.example.aulafirebase.DAL.UsuariosDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.GrupoUsuario;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.ResumoMensal;
import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.R;
import com.example.aulafirebase.helper.DateCustom;
import com.google.android.material.textfield.TextInputEditText;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class AddGanhoActivity extends AppCompatActivity {

    //Objeto de verificacao de conexão
    private ConnectivityManager cm;
    private NetworkInfo networkInfo;
    //Botão para salvar receita
    private Button btnSalvarReceita;
    //Edit Text referente a campos
    private TextInputEditText edDataReceita, edDescReceita;
    private MaskedEditText edHoraReceita;
    //EditText referente ao valor descrito
    private EditText edValorReceita;
    //Spinner contendo categorias
    private Spinner spinnerCategoriaReceita, spinnerAtribuido;
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
    private Bundle bundleData;
    private String anoSel, mesSel;

    private Boolean dataClicada = false;

    private TextView txtMov;

    private Grupo grupoRecebido = null;
    private GrupoDAO grupoDAO = new GrupoDAO();
    private Grupo grupoAtualizado;
    private ArrayAdapter<String> arrayAdapterAtribuido;
    private List<String> listEmailIntegrantes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ganho);


        btnSalvarReceita = findViewById(R.id.btnSalvarGanho);
        edDataReceita = findViewById(R.id.edDataGanho);
        edDescReceita = findViewById(R.id.edDescGanho);
        edValorReceita = findViewById(R.id.edValorGanho);
        spinnerCategoriaReceita = findViewById(R.id.spinnerCatGanho);
        edHoraReceita = findViewById(R.id.editHoraReceita);
        spinnerAtribuido = findViewById(R.id.spinnerAtribuido);
        txtMov = findViewById(R.id.txtRecAtr);

        //Adapter para utilizar o spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        //Setando o Adapter ao Spinner
        spinnerCategoriaReceita.setAdapter(arrayAdapter);

        bundleData = getIntent().getExtras();
        if( (!bundleData.getString("ano").equals(null)) && (!bundleData.getString("mes").equals(null)) ) {
            anoSel = bundleData.getString("ano");
            mesSel = bundleData.getString("mes");
            edDataReceita.setText( "01" + "/" + mesSel + "/" + anoSel );
        }else edDataReceita.setText( DateCustom.dataAtual() ); //Preenche com a data atual

        //Preenche com a hora atual
         edHoraReceita.setText(DateCustom.horaAtual());


        edDataReceita.setOnTouchListener((v, event) -> {
           if (!dataClicada){
                dialogCalendario();
            }else dataClicada = false;
            return true;
        });

        edHoraReceita.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edHoraReceita.setText("");
                return false;
            }
        });

        edHoraReceita.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String[] hora;

                hora = DateCustom.recuperarHora(edHoraReceita.getText().toString());

                if ( (!hasFocus && edHoraReceita.getText().toString().equals(""))) edHoraReceita.setText(DateCustom.horaAtual());
                if ((!hasFocus && hora.length > 0 && hora[0] != "")) {
                    if (Integer.parseInt(hora[0]) >= 24 ){
                        edHoraReceita.setError("A hora não pode ser superior a 24!");
                        edHoraReceita.setText("");
                    }else if ( !hasFocus && hora.length == 1 && hora[0] != ""){
                        if (Integer.parseInt(hora[0]) < 10) hora[0] = "0" + hora[0];
                        edHoraReceita.setText(hora[0] + ":" + "00");
                    }else if ( !hasFocus && hora.length == 2 && hora[1] != ""){
                        if (Integer.parseInt(hora[1]) >= 60 ){
                            edHoraReceita.setError("O minuto não pode ser superior a 60!");
                            edHoraReceita.setText("");
                        }else if (Integer.parseInt(hora[1]) < 6) hora[1] = hora[1] + "0"; else if (Integer.parseInt(hora[1]) < 10) hora[1] = "0" + hora[1];
                        edHoraReceita.setText(hora[0] + ":" + hora[1]);
                    }
                }


            }
        });


        edDataReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        btnSalvarReceita.setOnClickListener(v -> {
            if (recuperarBundle()) {
                grupoAtualizado = grupoDAO.getGrupo(grupoRecebido);
                grupoAtualizado.setGrupoId(grupoRecebido.getGrupoId());
            }
            else grupoAtualizado = null;

            salvarReceita(grupoAtualizado);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recuperarBundle()){
            grupoAtualizado = grupoDAO.getGrupo(grupoRecebido);
            grupoAtualizado.setGrupoId(grupoRecebido.getGrupoId());
            recuperarUsuariosGrupo();
        }else {
            spinnerAtribuido.setVisibility(View.GONE);
            txtMov.setVisibility(View.GONE);
        }

    }


    private Boolean validarCampos(){

        //Valida se campos estão preenchidos
        if (edValorReceita.getText().toString().isEmpty()){
            edValorReceita.setError("Preencha o valor para continuar!");
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

        String[] hora;

        hora = DateCustom.recuperarHora(edHoraReceita.getText().toString());

        if (edHoraReceita.getText().toString().isEmpty()){
            edHoraReceita.setError("Preencha a hora da receita para continuar!");
            return false;
        }else if (Integer.parseInt(hora[0]) >= 24){
            edHoraReceita.setError("A hora não pode ser superior a 24!");
            return false;
        }else if (Integer.parseInt(hora[1]) >= 60){
            edHoraReceita.setError("O minuto não pode ser superior a 60!");
            return false;
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
                        resumoRecuperado = true;
                    } else
                        resumoRecuperado = false; //Se for false, significa que nada retornou e precisa ser executado de novo


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


                edDataReceita.setText(diaSel + "/" + mesSel + "/" + anoSel);

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

    public Boolean recuperarBundle(){
        //grupoRecebido = null;
        Intent intent = getIntent();
        grupoRecebido = (Grupo) intent.getSerializableExtra("grupo");
        if (grupoRecebido == null) return false;

        return true;
    }

    private void salvarReceita(Grupo grupo){
        //Objeto de movimentacao
        Movimentacao receitaSalva = new Movimentacao();

        Double receitaTotal = null;

        if (grupo == null){ //Se for nulo significa que é um usuário
            //Recupera usuário antes de salvar
            usuario = usuariosDAO.getUsuario();
            //Seta valores
            //Receita Total
            receitaTotal = usuario.getReceitaTotal();
        } else if (grupo != null){ //Se não for nulo significa que é um grupoRecebido
            //Recupera o grupo antes de salvar
            grupoAtualizado = grupoDAO.getGrupo(grupo);
            //Seta valores
            //Receita Total
            receitaTotal = grupoAtualizado.getReceitaGrupo();
        }

        //Se for true, salva receita
        if (validarCampos()){
            String dataMov;
            //Trata campo data para agrupar em ano, mes e dia
            dataMov = DateCustom.firebaseFormatDate(edDataReceita.getText().toString());
            //Colocando o valor em uma varíavel, já que será usado múltiplas vezes
            Double valor = Double.parseDouble(edValorReceita.getText().toString());
            //Chama método para recuperar resumo mensal
            if (grupo == null) atualizarOuCriarResumoMensal(dataMov, valor);
            else if (grupo != null) atualizarOuCriarResumoMensal(dataMov, valor, grupo);
            //Setando valores de acordo com os campos
            receitaSalva.setValor(valor);
            receitaSalva.setDescTarefa(edDescReceita.getText().toString());
            receitaSalva.setDataTarefa(edDataReceita.getText().toString() + " - " + edHoraReceita.getText());
            receitaSalva.setCategoria(spinnerCategoriaReceita.getSelectedItem().toString());
            if (spinnerAtribuido.getSelectedItem() != null) receitaSalva.setAtribuicao(spinnerAtribuido.getSelectedItem().toString());
            //Caso receita = 'r'; Caso Receita = 'd'.
            receitaSalva.setTipo("r");

            //Se receita não for nula significa que algo retornou
            if (receitaTotal != null) {
                //Soma a receita total + o valor descrito
                //Receita atualizada
                Double receitaAtualizada = receitaTotal + valor;
                //Verifica conexão do celular
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                //Salva a receita, atualiza atributo receitaGeral e saldoGeral desde que tenha internet
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
                    if (grupo == null){
                        receitasDAO.salvarMovimentacao(receitaSalva);
                        receitasDAO.atualizarReceita(receitaAtualizada);
                    }
                    else if (grupo != null){
                        receitasDAO.salvarMovimentacao(receitaSalva, grupoAtualizado);
                        receitasDAO.atualizarReceita(receitaAtualizada, grupoAtualizado);
                    }

                    Toast.makeText(this, "Receita salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                    finish();

                }else Toast.makeText(this, "Por favor, conecte a internet e tente novamente...", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this, "Parece que a conexão está um pouco lenta... Tente novamente", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Preencha todos os campos corretamente para poder continuar", Toast.LENGTH_SHORT).show();
    }

    private Boolean atualizarOuCriarResumoMensal(String dataMovimentacao, Double valorReceita, Grupo grupo){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Verifica se há resumo, caso não haja cria e retorna
                resumoMensal = resumoMensalDAO.getOrSubResumoMensal(dataMovimentacao, grupo);
                //Verifica se há algo no resumoMensal a cada 300 milisegundos
                handler.postDelayed(this, 300);
                //Se não for nulo, significa que algo retornou
                if (resumoMensal.getReceitaMensal() != null) {
                    //Seta informações
                    receitaMensal = resumoMensal.getReceitaMensal();
                    resumoMensal.setReceitaMensal(receitaMensal + valorReceita);
                    //Método de saldo, deve ser sempre igual
                    resumoMensal.setSaldoMensal(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal());
                    resumoMensalDAO.setResumoMensal(resumoMensal, dataMovimentacao, grupoAtualizado);
//                    resumoMensalDAO.setResumoMensal(resumoMensal, dataMovimentacao, grupoAtualizado);
                    handler.removeCallbacks(this);
                    resumoRecuperado =  true;
                }else resumoRecuperado = false; //Se for false, significa que nada retornou e precisa ser executado de novo

            }
        });

        return resumoRecuperado;
    }

    private void recuperarUsuariosGrupo (){

        txtMov.setVisibility(View.VISIBLE);

        listEmailIntegrantes.add(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        arrayAdapterAtribuido = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listEmailIntegrantes);
        arrayAdapterAtribuido.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerAtribuido.setAdapter(arrayAdapterAtribuido);

        grupoDAO.retornarIntegrantes(grupoAtualizado, listEmailIntegrantes, arrayAdapterAtribuido);


    }

    public void notifyIntegrantesGrupo (/*List<GrupoUsuario> grupoUsuarioList, View view*/){


      /*  ArrayAdapter<GrupoUsuario> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, grupoUsuarioList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);*/



    }

}
