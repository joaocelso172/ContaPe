package com.example.aulafirebase;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.aulafirebase.Model.Grupo;

import java.io.Serializable;

public class GrupoActivity extends AppCompatActivity implements Serializable {

    private Bundle bundle = new Bundle();
    private Grupo grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recuperarBundle();





    }

    public void recuperarBundle(){
        Intent intent = getIntent();
        grupo = (Grupo) intent.getSerializableExtra("grupo");
    }

}
