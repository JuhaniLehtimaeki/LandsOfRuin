package com.landsofruin.companion.ruleobjectsui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.landsofruin.gametracker.R;

public class CharacterTypeActivity extends AppCompatActivity {


    public static void startNewActivity(Context context, String characterTypeId) {
        Intent intent = new Intent(context, CharacterTypeActivity.class);

        intent.putExtra("characterTypeId", characterTypeId);

        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_type);


        getSupportFragmentManager().beginTransaction().replace(R.id.container, CharacterTypeFragment.newInstance(getIntent().getStringExtra("characterTypeId"))).commit();
    }
}
