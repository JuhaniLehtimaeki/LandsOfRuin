package com.landsofruin.companion.ruleobjectsui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.landsofruin.gametracker.R;

public class RuleObjectsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_objects);

        findViewById(R.id.character_types).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RuleObjectsActivity.this, CharacterTypesActivity.class));
            }
        });

        findViewById(R.id.weapons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RuleObjectsActivity.this, WeaponsEditorActivity.class));
            }
        });

        findViewById(R.id.skills).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RuleObjectsActivity.this, SkillsEditorActivity.class));
            }
        });

        findViewById(R.id.actions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RuleObjectsActivity.this, ActionsEditorActivity.class));
            }
        });
    }
}
