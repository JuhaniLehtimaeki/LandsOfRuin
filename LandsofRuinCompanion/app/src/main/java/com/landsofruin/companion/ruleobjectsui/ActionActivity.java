package com.landsofruin.companion.ruleobjectsui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.landsofruin.gametracker.R;

public class ActionActivity extends AppCompatActivity {


    public static void startNewActivity(Context context, int actionId) {
        Intent intent = new Intent(context, ActionActivity.class);

        intent.putExtra("actionId", actionId);

        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_type);


        getSupportFragmentManager().beginTransaction().replace(R.id.container, ActionFragment.newInstance(getIntent().getIntExtra("actionId", 0))).commit();
    }
}
