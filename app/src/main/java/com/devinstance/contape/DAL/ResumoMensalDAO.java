package com.devinstance.contape.DAL;

import android.view.View;

import androidx.annotation.NonNull;

import com.devinstance.contape.controller.transaction_activity.TransactionActivity;
import com.devinstance.contape.model.Group;
import com.devinstance.contape.model.ResumoMensal;
import com.devinstance.contape.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResumoMensalDAO {

    //Referencia ao BD configurado no json
    private final DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    private ResumoMensal resumoMensal = new ResumoMensal();

    private final FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();

    public ResumoMensal getResumoMensal() {
        return resumoMensal;
    }

    public ResumoMensal getOrSubResumoMensal(String ano, String mes){

        getDatabaseResumo(ano, mes).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resumoMensal = dataSnapshot.getValue(ResumoMensal.class);
                }else {
                    resumoMensal = new ResumoMensal(0.0, 0.0, 0.0, ano, mes);
                    setResumoMensal(resumoMensal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return resumoMensal;
    }

    public ResumoMensal getOrSubResumoMensal(String anoMes){

        getDatabaseResumo(anoMes).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resumoMensal = dataSnapshot.getValue(ResumoMensal.class);
                }else {
                    resumoMensal = new ResumoMensal(0.0, 0.0, 0.0, anoMes);
                    setResumoMensal(resumoMensal, anoMes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return resumoMensal;
    }

    public ResumoMensal getOrSubResumoMensal(String anoMes, Group group){

        getDatabaseResumo(anoMes, group).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resumoMensal = dataSnapshot.getValue(ResumoMensal.class);
                }else {
                    resumoMensal = new ResumoMensal(0.0, 0.0, 0.0, anoMes);
                    setResumoMensal(resumoMensal, anoMes, group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return resumoMensal;
    }


    public ResumoMensal recuperarResumoMensal(String anoMes, View view){

            DatabaseReference resumo = getDatabaseResumo(anoMes);

            TransactionActivity transactionActivity = new TransactionActivity();

            resumo.addValueEventListener(new ValueEventListener() { //Cogitar alterar para ValueEventListener sempre ativo
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resumoMensal = dataSnapshot.getValue(ResumoMensal.class);
                }else resumoMensal = new ResumoMensal(0.0, 0.0, 0.0, anoMes);

                transactionActivity.notifyItemsResumoChanged(view, resumoMensal);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return resumoMensal;
    }

    private ValueEventListener valueEventMovGrupo = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ResumoMensal recuperarResumoMensal(String anoMes, View view, Group group){

        DatabaseReference resumo = getDatabaseResumo(anoMes, group);

        TransactionActivity transactionActivity = new TransactionActivity();

        resumo.addValueEventListener(new ValueEventListener() { //Cogitar alterar para ValueEventListener sempre ativo
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resumoMensal = dataSnapshot.getValue(ResumoMensal.class);
                }else resumoMensal = new ResumoMensal(0.0, 0.0, 0.0, anoMes);

                transactionActivity.notifyItemsResumoChanged(view, resumoMensal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return resumoMensal;
    }


    public Boolean setResumoMensal(ResumoMensal resumo){

        return getDatabaseResumo(resumo.getAnoMes()).setValue(resumo).isSuccessful();
    }

    public Boolean setResumoMensal(ResumoMensal resumo, String dataMov){

        return getDatabaseResumo(dataMov).setValue(resumo).isSuccessful();
    }

    public Boolean setResumoMensal(ResumoMensal resumo, String dataMov, Group group){ //Construtor usado para salvar Grupos

        return getDatabaseResumo(dataMov, group).setValue(resumo).isSuccessful();
    }

    private DatabaseReference getDatabaseResumo(String ano, String mes){

        return refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("Movimentacoes").child(ano).child(mes).child("ResumoMensal");
    }

    private DatabaseReference getDatabaseResumo(String anoMes){

        return refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("Movimentacoes").child(anoMes).child("ResumoMensal");
    }

    private DatabaseReference getDatabaseResumo(String anoMes, Group group){

        return refenciaDb.child("grupos").child(group.getGrupoId()).child("Movimentacoes").child(anoMes).child("ResumoMensal");
    }



}
