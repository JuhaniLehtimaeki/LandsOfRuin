package com.landsofruin.companion.ruleobjectsui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.skills.SkillsManager;

public class SkillsEditorActivity extends AppCompatActivity {

    private ViewGroup skillsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_editor);
        skillsContainer = ((ViewGroup) findViewById(R.id.skills_container));

        updateWeapons();
    }


    private void updateWeapons() {

        skillsContainer.removeAllViews();

        for (final Skill skill : SkillsManager.getInstance().getSkills()) {

            View oneWargear = getLayoutInflater().inflate(R.layout.skills_editor_one_skill, skillsContainer, false);

            ((TextView) oneWargear.findViewById(R.id.name)).setText(skill.getName());
            ((TextView) oneWargear.findViewById(R.id.description)).setText(skill.getDescription());


            oneWargear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SkillActivity.startNewActivity(SkillsEditorActivity.this, skill.getId());
                }
            });

            skillsContainer.addView(oneWargear);
        }
    }

}
