package com.landsofruin.companion.ruleobjectsui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.landsofruin.gametracker.R;

public class SkillActivity extends AppCompatActivity {


    public static void startNewActivity(Context context, int skillId) {
        Intent intent = new Intent(context, SkillActivity.class);

        intent.putExtra("skillId", skillId);

        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_type);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, SkillFragment.newInstance(getIntent().getIntExtra("skillId", 0))).commit();
    }
}
