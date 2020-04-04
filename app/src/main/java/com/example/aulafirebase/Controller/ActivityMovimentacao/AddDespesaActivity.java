package com.example.aulafirebase.Controller.ActivityMovimentacao;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.GrupoDAO;
import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.DAL.ResumoMensalDAO;
import com.example.aulafirebase.DAL.UsuariosDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.ResumoMensal;
import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.R;
import com.example.aulafirebase.helper.DateCustom;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;

import br.com.sapereaude.maskedEditText.MaskedEditText;

@RequiresApi(api = Build.VERSION_CODES.M)
public class AddDespesaActivity extends AppCompatActivity {

    //Botão para salvar despesa
    private Button btnSalvarDespesa;
    //Edit Text referente a campos
    private TextInputEditText edDataDespesa, edDescDespesa;
    //EditText referente ao valor descrito
    private TextInputLayout tilData;
    private EditText edValorDespesa;
    //Spinner contendo categorias
    private Spinner spinnerCategoriaDespesa, spinnerAtribuido;
    //Objeto de acesso ao nó de movimentacoes
    private final MovimentacoesDAO despesasDAO = new MovimentacoesDAO();
    //Objeto de acesso aos resumos do nó de movimentacoes
    private final ResumoMensalDAO resumoMensalDAO = new ResumoMensalDAO();
    //Objeto ResumoMensal
    private ResumoMensal resumoMensal = new ResumoMensal();
    //Array de Strings que define as categorias disponíveis
    private String[] categorias = new String[]{ "Moradia", "Contas", "Alimentação", "Faculdade", "Outros"};
    //Recupera UsuárioDAO
    private final UsuariosDAO usuariosDAO = new UsuariosDAO();
    //Recupera Usuario, deve ser recuperado SEMPRE antes de qualquer coisa
    private Usuario usuario = usuariosDAO.getUsuario();
    //Atributos resumo mensal
    private Double despesaMensal;
    //Atributo que retorna true quando resumo está ok
    private Boolean resumoRecuperado = false;
    private MaskedEditText editHoraDespesa;
    private Bundle bundleData;
    private String anoSel, mesSel;
    private String tipoFaturamento = "aVista";

    private Boolean dataClicada = false;

    private TextView txtMov, txtParcelaRadio;

    private EditText editInputDespParcela;
    private RadioButton radioParcelas;
    private RadioButton radioRecorrente;
    private RadioButton radioAVista;
    private LinearLayout linearParcelas;

    private Grupo grupoRecebido = null;
    private GrupoDAO grupoDAO = new GrupoDAO();
    private Grupo grupoAtualizado;
    private ArrayAdapter<String> arrayAdapterAtribuido;
    private List<String> listEmailIntegrantes = new ArrayList<>();
    private String tipoMovimentacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_despesa);

        btnSalvarDespesa = findViewById(R.id.btnSalvarDespesa);
        edDataDespesa = findViewById(R.id.edDataDespesa);
        edDescDespesa = findViewById(R.id.edDescDespesa);
        edValorDespesa = findViewById(R.id.edValorDespesa);
        spinnerCategoriaDespesa = findViewById(R.id.spinnerCatDespesa);
        editHoraDespesa = findViewById(R.id.editHoraDespesa);
        spinnerAtribuido = findViewById(R.id.spinnerDespAtribuida);
        txtMov = findViewById(R.id.txtDespAtr);
        editInputDespParcela = findViewById(R.id.editInputDespParcela);
        editInputDespParcela = findViewById(R.id.editInputDespParcela);
        radioParcelas = findViewById(R.id.radioParcela);
        radioRecorrente = findViewById(R.id.radioRecorrente);
        radioAVista = findViewById(R.id.radioVista);
        linearParcelas = findViewById(R.id.linearParcelasDesp);
        tilData = findViewById(R.id.tilData);
        txtParcelaRadio = findViewById(R.id.txtParcelas);

        bundleData = getIntent().getExtras();
        if( (!bundleData.getString("ano").equals(null)) && (!bundleData.getString("mes").equals(null)) ) {
            anoSel = bundleData.getString("ano");
            mesSel = bundleData.getString("mes");
            String[] data = DateCustom.firebaseFormatDateBuild(DateCustom.dataAtual());
            edDataDespesa.setText( data[0] + "/" + mesSel + "/" + anoSel );
        }else edDataDespesa.setText( DateCustom.dataAtual() ); //Preenche com a data atual

        tipoMovimentacao = bundleData.getString("tipo");
        Log.i("Tipo Mov", tipoMovimentacao);

        if (tipoMovimentacao.equals("r")) {
            tilData.setHint("Data de Faturamento");
            radioParcelas.setText("Parcelar Faturamento");

            if (recuperarBundle()) categorias = new String[]{"Proventos do Grupo", "Comissão", "Bonificação", "Outros"};
            else categorias = new String[]{"Salário", "Comissão", "Bonificação", "Outros"};

        }else {
            tilData.setHint("Data de Vencimento");
            if (recuperarBundle()) categorias = new String[]{"Moradia", "Contas", "Alimentação", "Outros"};
        }

        //Adapter para utilizar o spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categorias);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);


        //Setando o Adapter ao Spinner
        spinnerCategoriaDespesa.setAdapter(arrayAdapter);


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

        radioAVista.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearParcelas.setVisibility(View.GONE);
                    tipoFaturamento = "aVista";
                }
            }
        });

        radioParcelas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearParcelas.setVisibility(View.VISIBLE);
                    editInputDespParcela.setHint("N.º de parcelas");
                    txtParcelaRadio.setText("Parcelas Mensais");
                    tipoFaturamento = "parcelado";
                }
            }
        });

        radioRecorrente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearParcelas.setVisibility(View.VISIBLE);
                    editInputDespParcela.setHint("Duração da Recorrência");
                    txtParcelaRadio.setText("Meses");
                    tipoFaturamento = "recorrente";
                }
            }
        });



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

        btnSalvarDespesa.setOnClickListener(v -> {
            if (recuperarBundle()) grupoAtualizado = grupoDAO.getGrupo(grupoRecebido);
            else grupoAtualizado = null;

            salvarDespesa(grupoAtualizado);
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

    private void salvarDespesa(Grupo grupo){
        //Objeto de movimentacao
        Movimentacao despesaSalva = new Movimentacao();

        if (grupo == null){
            //Recupera usuário antes de salvar
            usuario = usuariosDAO.getUsuario();
            //Seta valores
            //Despesa Total
        } else if (grupo != null){
            //Recupera o grupo antes de salvar
            grupoAtualizado = grupoDAO.getGrupo(grupo);
            //Seta valores
            //Receita Total
        }

        //Se for true, salva despesa
        if (validarCampos()){

            Double valor = Double.parseDouble(edValorDespesa.getText().toString());
            //Setando valores de acordo com os campos
            despesaSalva.setValor(valor);
            despesaSalva.setDescTarefa(edDescDespesa.getText().toString());
            despesaSalva.setDataTarefa(edDataDespesa.getText().toString() + " - " + editHoraDespesa.getText());
            despesaSalva.setCategoria(spinnerCategoriaDespesa.getSelectedItem().toString());
            despesaSalva.setTipoFaturamento(tipoFaturamento);
            if (spinnerAtribuido.getSelectedItem() != null) despesaSalva.setAtribuicao(spinnerAtribuido.getSelectedItem().toString());
            //Caso seja parcelado
            if (radioParcelas.isChecked()){
                despesaSalva.setParcelaTotal(Integer.parseInt(editInputDespParcela.getText().toString()));
            }
            //Caso despesa = 'r'; Caso Despesa = 'd'.
            despesaSalva.setTipo(tipoMovimentacao);

                //Verifica conexão do celular
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                //Salva a despesa desde que tenha internet
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){ //Se parcelado, executa método próprio
                  //  if (grupo == null) {
                //        despesasDAO.salvarMovimentacao(despesaSalva, null);

                        switch (despesaSalva.getTipoFaturamento()){
                            case "parcelado":
                                parcelarDespesa(despesaSalva, grupo);

                                break;
                            case "recorrente":
                                salvarMovRecorrente(despesaSalva, grupo);
                                
                                break;
                            case "aVista":
                                despesasDAO.salvarMovimentacao(despesaSalva, grupo);
                                
                                break;
                        }

                    //}else if (grupo == null && despesaSalva.getTipoFaturamento().equals("parcelado")) parcelarDespesa(despesaSalva); //Se parcelado, executa método próprio
                    /*    else if (grupo != null && !despesaSalva.getTipoFaturamento().equals("parcelado")){
                        despesasDAO.salvarMovimentacao(despesaSalva, grupoAtualizado);
                    }*/

                    Toast.makeText(this, "Despesa salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                    finish();
                }else Toast.makeText(this, "Por favor, conecte a internet e tente novamente...", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Preencha todos os campos corretamente para poder continuar", Toast.LENGTH_SHORT).show();
    }

    private void salvarMovRecorrente(Movimentacao movimentacaoSalva, Grupo grupo) {

        int parcelaTotal = 13;
        String data[] = DateCustom.firebaseFormatDateBuild(movimentacaoSalva.getDataTarefa());
        List <Movimentacao> movimentacaoParcelada = new ArrayList<>();
        List <String> datasMovs = new ArrayList<>();

        int i;
        int mes = Integer.parseInt(data[1]);
        int ano = Integer.parseInt(data[2]);

        for (i = 0; i<parcelaTotal; i++){
            Movimentacao movParcela = movimentacaoSalva;
            if (mes < 12) mes++;
            else if (mes >= 12) {
                mes = 1;
                Log.i("Parcelado", "Ano antes alterar: " + mes + "/" + ano);
                ano++;
                Log.i("Parcelado", "Ano alterado: " +
                        mes + "/" + ano);
            }

            String mesParcela = String.valueOf(mes);
            String anoParcela = String.valueOf(ano);
            if (mes < 10) mesParcela = "0" + mes;

            datasMovs.add(data[0] + "/" + mesParcela + "/" + anoParcela + " - " + editHoraDespesa.getText());
            movParcela.setDataTarefa(datasMovs.get(i));
            if (i == 12) movParcela.setUltimaContaRecorrente(true);
            movimentacaoParcelada.add(movParcela);


//            Log.i("Parcelado", "Antes de lançar - " + "Parcela: " + movimentacaoParcelada.get(i).getParcelaAtual() + ", Item: " + movimentacaoParcelada.get(i).getDescTarefa());

        }

        despesasDAO.salvarMovimentacao(movimentacaoParcelada, datasMovs, grupo);
    }

    private void parcelarDespesa(Movimentacao despesa, Grupo grupo){

        int parcelaTotal = despesa.getParcelaTotal();
        Double valorDividido = (despesa.getValor() / parcelaTotal);
        despesa.setValor(valorDividido);
        String data[] = DateCustom.firebaseFormatDateBuild(despesa.getDataTarefa());
        List <Movimentacao> movimentacaoParcelada = new ArrayList<>();
        List <String> datasMovs = new ArrayList<>();

        int i;
        int mes = Integer.parseInt(data[1]);
        int ano = Integer.parseInt(data[2]);

        for (i = 0; i<parcelaTotal; i++){
            Movimentacao movParcela = despesa;
            if (mes < 12) mes++;
            else if (mes >= 12) {
                mes = 1;
                Log.i("Parcelado", "Ano antes alterar: " + mes + "/" + ano);
                ano++;
                Log.i("Parcelado", "Ano alterado: " +
                        mes + "/" + ano);
            }

            String mesParcela = String.valueOf(mes);
            String anoParcela = String.valueOf(ano);
            if (mes < 10) mesParcela = "0" + mes;

            datasMovs.add(data[0] + "/" + mesParcela + "/" + anoParcela + " - " + editHoraDespesa.getText());
            movParcela.setDataTarefa(datasMovs.get(i));
            movParcela.setParcelaAtual(i + 1);
            movimentacaoParcelada.add(movParcela);


            Log.i("Parcelado", "Antes de lançar - " + "Parcela: " + movimentacaoParcelada.get(i).getParcelaAtual() + ", Item: " + movimentacaoParcelada.get(i).getDescTarefa());

        }
        //Construtor para salvar listas de movimentacoes
        despesasDAO.salvarMovimentacao(movimentacaoParcelada, datasMovs, grupo);


    }



    private Boolean validarCampos(){

        //Valida se campos estão preenchidos
        if (edValorDespesa.getText().toString().isEmpty()){
            edValorDespesa.setError("Preencha o valor para continuar!");
            if (edDescDespesa.getText().toString().isEmpty())edDescDespesa.setError("Preencha a descrição para continuar!");
            if (edDataDespesa.getText().toString().isEmpty()) edDataDespesa.setError("Preencha a data para continuar!");
            if (radioParcelas.isChecked() && editInputDespParcela.getText().toString().isEmpty()) editInputDespParcela.setError("Preencha o número de parcelas!");

            return false;
        }

        if (edDescDespesa.getText().toString().isEmpty()){
            edDescDespesa.setError("Preencha a descrição para continuar!");
            if (edDataDespesa.getText().toString().isEmpty()) edDataDespesa.setError("Preencha a data para continuar!");
            if (radioParcelas.isChecked() && editInputDespParcela.getText().toString().isEmpty()) editInputDespParcela.setError("Preencha o número de parcelas!");

            return false;
        }

        if (radioParcelas.isChecked() && editInputDespParcela.getText().toString().isEmpty()) {
            editInputDespParcela.setError("Preencha o número de parcelas!");

            return false;
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

    private Boolean atualizarOuCriarResumoMensal(String dataMovimentacao, Double valorDespesa, Grupo grupo){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Verifica se há resumo, caso não haja cria e retorna
                resumoMensal = resumoMensalDAO.getOrSubResumoMensal(dataMovimentacao, grupo);
                //Verifica se há algo no resumoMensal a cada 300 milisegundos
                handler.postDelayed(this, 300);
                //Se não for nulo, significa que algo retornou
                if (resumoMensal.getDespesaMensal() != null) {
                    //Seta informações
                    despesaMensal = resumoMensal.getDespesaMensal();
                    resumoMensal.setDespesaMensal(despesaMensal + valorDespesa);
                    //Método de saldo, deve ser sempre igual
                    resumoMensal.setSaldoMensal(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal());
                    resumoMensalDAO.setResumoMensal(resumoMensal, dataMovimentacao, grupoAtualizado);
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

    //GRUPOS

    public Boolean recuperarBundle(){
        Intent intent = getIntent();
        grupoRecebido = (Grupo) intent.getSerializableExtra("grupo");
        if (grupoRecebido == null) return false;

        return true;
    }

    private void recuperarUsuariosGrupo (){

        txtMov.setVisibility(View.VISIBLE);

        listEmailIntegrantes.add(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        arrayAdapterAtribuido = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listEmailIntegrantes);
        arrayAdapterAtribuido.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerAtribuido.setAdapter(arrayAdapterAtribuido);

        grupoDAO.retornarIntegrantes(grupoAtualizado, listEmailIntegrantes, arrayAdapterAtribuido);


    }
}
