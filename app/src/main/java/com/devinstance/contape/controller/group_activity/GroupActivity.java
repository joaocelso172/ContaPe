package com.devinstance.contape.controller.group_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.devinstance.contape.model.Group;
import com.devinstance.contape.R;

import java.io.Serializable;

public class GroupActivity extends AppCompatActivity implements Serializable {

    private Bundle bundle = new Bundle();
    private Group group;

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
        group = (Group) intent.getSerializableExtra("grupo");
    }

}
