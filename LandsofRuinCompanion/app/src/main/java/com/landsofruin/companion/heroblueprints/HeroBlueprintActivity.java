package com.landsofruin.companion.heroblueprints;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.landsofruin.gametracker.R;

public class HeroBlueprintActivity extends AppCompatActivity {


    public static void startNewActivity(Context context, String heroBlueprintId) {
        Intent intent = new Intent(context, HeroBlueprintActivity.class);

        intent.putExtra("heroBlueprintId", heroBlueprintId);

        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_blueprint);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, HeroBlueprintFragment.newInstance(getIntent().getStringExtra("heroBlueprintId"))).commit();
    }
}
