package com.devinstance.contape.controller.transaction_activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devinstance.contape.DAL.FirebaseConfig;
import com.devinstance.contape.DAL.GroupDAO;
import com.devinstance.contape.DAL.TransactionDAO;
import com.devinstance.contape.DAL.ResumoMensalDAO;
import com.devinstance.contape.DAL.UsersDAO;
import com.devinstance.contape.model.Group;
import com.devinstance.contape.model.Transaction;
import com.devinstance.contape.model.ResumoMensal;
import com.devinstance.contape.model.User;
import com.devinstance.contape.R;
import com.devinstance.contape.helper.DateCustom;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.sapereaude.maskedEditText.MaskedEditText;

@RequiresApi(api = Build.VERSION_CODES.M)
public class AddExpenseActivity extends AppCompatActivity {

    //Botão para salvar despesa
    private Button btnSalvarDespesa;
    //Edit Text referente a campos
    private TextInputEditText edDataDespesa, edDescDespesa, edValorParcela;
    //EditText referente ao valor descrito
    private TextInputLayout tilData, tilValorParcela;
    private EditText edValorDespesa;
    //Spinner contendo categorias
    private Spinner spinnerCategoriaDespesa, spinnerAtribuido;
    //Objeto de acesso ao nó de movimentacoes
    private final TransactionDAO despesasDAO = new TransactionDAO();
    //Objeto de acesso aos resumos do nó de movimentacoes
    private final ResumoMensalDAO resumoMensalDAO = new ResumoMensalDAO();
    //Objeto ResumoMensal
    private ResumoMensal resumoMensal = new ResumoMensal();
    //Array de Strings que define as categorias disponíveis
    private String[] categorias = new String[]{ "Moradia", "Contas", "Alimentação", "Faculdade", "Outros"};
    //Recupera UsuárioDAO
    private final UsersDAO usersDAO = new UsersDAO();
    //Recupera Usuario, deve ser recuperado SEMPRE antes de qualquer coisa
    private User user = usersDAO.getUsuario();
    //Atributos resumo mensal
    private Double despesaMensal;
    //Atributo que retorna true quando resumo está ok
    private Boolean resumoRecuperado = false;
    private MaskedEditText editHoraDespesa;
    private Bundle bundleData;
    private String anoSel, mesSel;
    private String tipoFaturamento = "aVista";

    private Boolean dataClicada = false, isAlteracao = false;

    private TextView txtMov, txtParcelaRadio;

    private EditText editInputDespParcela;
    private RadioButton radioParcelas;
    private RadioButton radioRecorrente;
    private RadioButton radioAVista;
    private LinearLayout linearParcelas;
    private CheckBox checkboxInversoGrupo;

    private Group groupRecebido = null;
    private GroupDAO groupDAO = new GroupDAO();
    private Group groupAtualizado;
    private ArrayAdapter<String> arrayAdapterAtribuido;
    private List<String> listEmailIntegrantes = new ArrayList<>();
    private String tipoMovimentacao;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

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
        edValorParcela = findViewById(R.id.edValorParcela);
        tilValorParcela = findViewById(R.id.txtinputValor);
        checkboxInversoGrupo = findViewById(R.id.checkBoxInverso);

        radiosConfig(); //Seta seleção de rádio

        bundleData = getIntent().getExtras();
        if ( bundleData.getString("ano") != null ) {
            anoSel = bundleData.getString("ano");
            mesSel = bundleData.getString("mes");
            String[] data = DateCustom.firebaseFormatDateBuild(DateCustom.dataAtual());
            edDataDespesa.setText( data[0] + "/" + mesSel + "/" + anoSel );
            tipoMovimentacao = bundleData.getString("tipo");
        }else setarCampos();

        if (tipoMovimentacao.equals("r")) {
            tilData.setHint("Data de Faturamento");
            radioParcelas.setText("Parcelar Faturamento");
            checkboxInversoGrupo.setText("Aparecer como despesa para o responsável");
            checkboxInversoGrupo.setHint("Assinale este campo quando o responsável estiver transferindo o valor para o grupo");

            if (recuperarBundle()) categorias = new String[]{"Proventos do Grupo", "Comissão", "Bonificação", "Outros"};
            else categorias = new String[]{"Salário", "Comissão", "Bonificação", "Outros"};

        }else {
            tilData.setHint("Data de Vencimento");
            if (recuperarBundle()) categorias = new String[]{"Moradia", "Contas", "Alimentação", "Outros"};
            checkboxInversoGrupo.setText("Aparecer como receita para o responsável");
            checkboxInversoGrupo.setHint("Assinale este campo quando o responsável estiver usufruindo do valor que tem como fonte o grupo");
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
            if (recuperarBundle()) groupAtualizado = groupDAO.getGrupo(groupRecebido);
            else groupAtualizado = null;

            if (isAlteracao) AlterarListaMov(recuperarEdicao(), groupAtualizado);
            else salvarDespesa(groupAtualizado);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (recuperarBundle()){
            groupAtualizado = groupDAO.getGrupo(groupRecebido);
            groupAtualizado.setGrupoId(groupRecebido.getGrupoId());
            recuperarUsuariosGrupo();
            checkboxInversoGrupo.setVisibility(View.VISIBLE);
        }else {
            spinnerAtribuido.setVisibility(View.GONE);
            txtMov.setVisibility(View.GONE);
            checkboxInversoGrupo.setVisibility(View.GONE);
        }

    }

    private void salvarDespesa(Group group){
        //Objeto de movimentacao
        Transaction despesaSalva = new Transaction();

        if (group == null){
            //Recupera usuário antes de salvar
            user = usersDAO.getUsuario();
            //Seta valores
            //Despesa Total
        } else if (group != null){
            //Recupera o grupo antes de salvar
            groupAtualizado = groupDAO.getGrupo(group);
            //Seta valores
            //Receita Total
        }

        //Se for true, salva despesa
        if (validarCampos()){

                setarValores(despesaSalva);

                //Verifica conexão do celular
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                //Salva a despesa desde que tenha internet
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){ //Se parcelado, executa método próprio
                        switch (despesaSalva.getTipoFaturamento()){
                            case "parcelado":
                            case "recorrente":
                                parcelarDespesa(despesaSalva, group);

                                break;

                                case "aVista":
                                despesasDAO.salvarMovimentacao(despesaSalva, group);
                                
                                break;
                        }

                    Toast.makeText(this, "Despesa salva com sucesso.   :)", Toast.LENGTH_SHORT).show();
                    finish();
                }else Toast.makeText(this, "Por favor, conecte a internet e tente novamente...", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Preencha todos os campos corretamente para poder continuar", Toast.LENGTH_SHORT).show();
    }

    private void setarValores(Transaction mov){
        if (groupAtualizado != null) {
            mov.setIdGrupoVinculo(groupAtualizado.getGrupoId());
            mov.setNomeGrupo(groupAtualizado.getNomeGrupo());
            mov.setInverso(checkboxInversoGrupo.isChecked());
        }

        String formatValor = edValorDespesa.getText().toString().replaceAll(",",".");

        Double valor = Double.parseDouble(formatValor);
        //Setando valores de acordo com os campos
        mov.setValor(valor);
        mov.setDescTarefa(edDescDespesa.getText().toString());
        mov.setDataTarefa(edDataDespesa.getText().toString() + " - " + editHoraDespesa.getText());
        mov.setCategoria(spinnerCategoriaDespesa.getSelectedItem().toString());
        mov.setTipoFaturamento(tipoFaturamento);
        if (spinnerAtribuido.getSelectedItem() != null) mov.setAtribuicao(spinnerAtribuido.getSelectedItem().toString());
        //Caso seja parcelado
        if (radioParcelas.isChecked() || radioRecorrente.isChecked()){
            mov.setParcelaTotal(Integer.parseInt(editInputDespParcela.getText().toString()));
        }
        //Caso despesa = 'r'; Caso Despesa = 'd'.
        mov.setTipo(tipoMovimentacao);
    }

    private void setarCampos(){ //edicao??

        isAlteracao = true;

        Transaction movEdit = recuperarEdicao();
//        tipoMovimentacao = recuperarEdicao().getTipo();
        tipoMovimentacao = movEdit.getTipo();
        tipoFaturamento = movEdit.getTipoFaturamento();

        edValorDespesa.setText(decimalFormat.format(movEdit.getValor()));
        edDescDespesa.setText(movEdit.getDescTarefa());
        if (movEdit.getInverso() != null) checkboxInversoGrupo.setChecked(movEdit.getInverso());

        String[] data;

        data = DateCustom.firebaseFormatDateBuild(movEdit.getDataTarefa());

        edDataDespesa.setText(data[0] + "/" + data[1] + "/" + data[2] );
        if (movEdit.getAtribuicao() != null) listEmailIntegrantes.add(movEdit.getAtribuicao());

        switch (tipoFaturamento){
            case "parcelado":
                radioParcelas.setChecked(true);
                editInputDespParcela.setText(String.valueOf(movEdit.getParcelaTotal()));
                break;
            case "recorrente":
                radioRecorrente.setChecked(true);
                editInputDespParcela.setText(String.valueOf(movEdit.getParcelaTotal()));
                break;
            case "aVista":
                radioAVista.setChecked(true);
                break;
        }

        radioAVista.setEnabled(false);
        radioRecorrente.setEnabled(false);
        radioParcelas.setEnabled(false);
        editInputDespParcela.setEnabled(false);
        txtParcelaRadio.setEnabled(false);

        btnSalvarDespesa.setText("Atualizar");

    }

    private void alteraMovAvista(Transaction mov, Group group){

        setarValores(mov);

        despesasDAO.atualizarMovimentacao(mov, group);

    }

    private void alterarMovRecorrente (Transaction mov, Group group){

        setarValores(mov);


        int parcelaTotal = mov.getParcelaTotal(), parcelaAtual = mov.getParcelaAtual();

        String data[] = DateCustom.firebaseFormatDateBuild(mov.getDataTarefa());
        List <Transaction> transactionParcelada = new ArrayList<>();
        List <String> datasMovs = new ArrayList<>();

        int i;
        int mes = Integer.parseInt(data[1]);
        int ano = Integer.parseInt(data[2]);

        for (i = parcelaAtual; i<=parcelaTotal; i++){
            Transaction movParcela = mov;

            String mesParcela = String.valueOf(mes);
            String anoParcela = String.valueOf(ano);
            if (mes < 10) mesParcela = "0" + mes;

            datasMovs.add(data[0] + "/" + mesParcela + "/" + anoParcela + " - " + editHoraDespesa.getText());
            movParcela.setDataTarefa(datasMovs.get(i - parcelaAtual));
            //movParcela.setParcelaAtual(i + 1);
            //if (i == (mov.getParcelaTotal() - 1)) movParcela.setUltimaContaRecorrente(true);
            transactionParcelada.add(movParcela);

            if (mes < 12) mes++;
            else if (mes >= 12) {
                mes = 1;
                Log.i("Parcelado", "Ano antes alterar: " + mes + "/" + ano);
                ano++;
                Log.i("Parcelado", "Ano alterado: " +
                        mes + "/" + ano);
            }

            Log.i("Data Mov", transactionParcelada.get(0).getDataTarefa());

        }
        //Construtor para salvar listas de movimentacoes
        despesasDAO.atualizarMovimentacao(transactionParcelada, datasMovs, group);

    }

    private void AlterarListaMov(Transaction mov, Group group){
        AlertDialog.Builder alertDialogExcluir = new AlertDialog.Builder(this);

        //Configurando AlertDialog
        alertDialogExcluir.setTitle("Editar Movimentação");
        alertDialogExcluir.setCancelable(false);

        if (validarCampos()) {
            if (!mov.getTipoFaturamento().equals("aVista")) {
                alertDialogExcluir.setMessage("Como deseja efetuar a edição?\nEsta ação não poderá ser revertida.");
                //Configurando botões
                alertDialogExcluir.setPositiveButton("Editar apenas esta parcela", (dialog, which) -> {
                    alteraMovAvista(mov, group);
                    finish();
                });

                alertDialogExcluir.setNegativeButton("Editar todas as parcelas (não afetará parceladas antigas)", (dialog, which) -> {
                    alterarMovRecorrente(mov, group);
                    finish();
                });

                alertDialogExcluir.setNeutralButton("Cancelar", (dialog, which) -> {

                });
            } else {
                alertDialogExcluir.setMessage("Deseja alterar a movimentação?\nEsta ação não poderá ser revertida.");

                alertDialogExcluir.setPositiveButton("Salvar", (dialog, which) -> {
                    alteraMovAvista(mov, group);
                    finish();
                });

                alertDialogExcluir.setNegativeButton("Cancelar", (dialog, which) -> {


                });

            }

            alertDialogExcluir.show();
        }
    }

    private void parcelarDespesa(Transaction despesa, Group group){

        int parcelaTotal = despesa.getParcelaTotal();
           if (despesa.getTipoFaturamento().equals("parcelado")) {
            Double valorDividido = (despesa.getValor() / parcelaTotal);
            despesa.setValor(valorDividido);
        }

        String data[] = DateCustom.firebaseFormatDateBuild(despesa.getDataTarefa());
        List <Transaction> transactionParcelada = new ArrayList<>();
        List <String> datasMovs = new ArrayList<>();

        int i;
        int mes = Integer.parseInt(data[1]);
        int ano = Integer.parseInt(data[2]);

        for (i = 0; i<parcelaTotal; i++){
            Transaction movParcela = despesa;

            String mesParcela = String.valueOf(mes);
            String anoParcela = String.valueOf(ano);
            if (mes < 10) mesParcela = "0" + mes;

            datasMovs.add(data[0] + "/" + mesParcela + "/" + anoParcela + " - " + editHoraDespesa.getText());
            movParcela.setDataTarefa(datasMovs.get(i));
            movParcela.setParcelaAtual(i + 1);
            if (i == (despesa.getParcelaTotal() - 1)) movParcela.setUltimaContaRecorrente(true);
            transactionParcelada.add(movParcela);

            if (mes < 12) mes++;
            else if (mes >= 12) {
                mes = 1;
                Log.i("Parcelado", "Ano antes alterar: " + mes + "/" + ano);
                ano++;
                Log.i("Parcelado", "Ano alterado: " +
                        mes + "/" + ano);
            }

            Log.i("Data Mov", transactionParcelada.get(0).getDataTarefa());

        }
        //Construtor para salvar listas de movimentacoes
        despesasDAO.salvarMovimentacao(transactionParcelada, datasMovs, group);

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

    private Boolean atualizarOuCriarResumoMensal(String dataMovimentacao, Double valorDespesa, Group group){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Verifica se há resumo, caso não haja cria e retorna
                resumoMensal = resumoMensalDAO.getOrSubResumoMensal(dataMovimentacao, group);
                //Verifica se há algo no resumoMensal a cada 300 milisegundos
                handler.postDelayed(this, 300);
                //Se não for nulo, significa que algo retornou
                if (resumoMensal.getDespesaMensal() != null) {
                    //Seta informações
                    despesaMensal = resumoMensal.getDespesaMensal();
                    resumoMensal.setDespesaMensal(despesaMensal + valorDespesa);
                    //Método de saldo, deve ser sempre igual
                    resumoMensal.setSaldoMensal(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal());
                    resumoMensalDAO.setResumoMensal(resumoMensal, dataMovimentacao, groupAtualizado);
                    handler.removeCallbacks(this);
                    resumoRecuperado =  true;
                }else resumoRecuperado = false; //Se for false, significa que nada retornou e precisa ser executado de novo
            }
        });

        return resumoRecuperado;
    }

    private Transaction recuperarEdicao() { //atualizar
        Intent intent = getIntent();
        return (Transaction) intent.getSerializableExtra("movEdit");

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
        groupRecebido = (Group) intent.getSerializableExtra("grupo");
        if (groupRecebido == null) return false;

        return true;
    }

    private void recuperarUsuariosGrupo (){

        txtMov.setVisibility(View.VISIBLE);

        listEmailIntegrantes.add("Em Aberto");

        listEmailIntegrantes.add(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        arrayAdapterAtribuido = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listEmailIntegrantes);
        arrayAdapterAtribuido.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerAtribuido.setAdapter(arrayAdapterAtribuido);

        groupDAO.retornarIntegrantes(groupAtualizado, listEmailIntegrantes, arrayAdapterAtribuido);


    }

    private void calcularValorParcelas(Boolean isEdicao, Boolean isParcelado){
        View.OnFocusChangeListener branco = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        };
        View.OnFocusChangeListener onFocusChangeParcela = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean event) {
                if (!edValorDespesa.getText().toString().isEmpty() && !editInputDespParcela.getText().toString().isEmpty()) {
//                    edValorParcela.setText("R$ " + decimalFormat.format(edValorDespesa.getText().toString()));
                    if (!isEdicao) {
                        if (!editInputDespParcela.getText().toString().isEmpty() && !editInputDespParcela.getText().toString().equals("0")){
                            tilValorParcela.setHint("Valor por Parcela");
                            Double valorParcela = Double.parseDouble(edValorDespesa.getText().toString()) / Integer.parseInt(editInputDespParcela.getText().toString());
//                            edValorParcela.setText("R$ " + valorParcela);
                            edValorParcela.setText("R$ " + decimalFormat.format(valorParcela));
                            tilValorParcela.setVisibility(View.VISIBLE);
                        }
                    }
                } else tilValorParcela.setVisibility(View.GONE);
            }
        };
        if (isParcelado) {
            edValorDespesa.setOnFocusChangeListener(onFocusChangeParcela);
            editInputDespParcela.setOnFocusChangeListener(onFocusChangeParcela);
        }
        else {
            edValorDespesa.setOnFocusChangeListener(branco);
            editInputDespParcela.setOnFocusChangeListener(branco);
        }
    }

    private void radiosConfig(){
        tipoFaturamento = "aVista";
        radioAVista.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tilValorParcela.setVisibility(View.GONE);
                    linearParcelas.setVisibility(View.GONE);
                    tipoFaturamento = "aVista";
                    calcularValorParcelas(false, false);
                }
            }
        });

        radioParcelas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if ( bundleData.getString("ano") != null ) calcularValorParcelas(false, true);
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
                    calcularValorParcelas(false, false);
                    tilValorParcela.setVisibility(View.GONE);
                    linearParcelas.setVisibility(View.VISIBLE);
                    editInputDespParcela.setHint("Duração da Recorrência");
                    txtParcelaRadio.setText("Meses");
                    tipoFaturamento = "recorrente";
                }
            }
        });
    }
}
