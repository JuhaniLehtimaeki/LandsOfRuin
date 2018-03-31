package com.landsofruin.companion.ruleobjectsui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actions.ActionManager;

public class ActionsEditorActivity extends AppCompatActivity {

    private ViewGroup skillsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_editor);
        skillsContainer = ((ViewGroup) findViewById(R.id.skills_container));

        updateWeapons();
    }


    private void updateWeapons() {

        skillsContainer.removeAllViews();

        for (final Action action : ActionManager.getInstance().getActions()) {

            View oneWargear = getLayoutInflater().inflate(R.layout.skills_editor_one_skill, skillsContainer, false);

            ((TextView) oneWargear.findViewById(R.id.name)).setText(action.getName());
            ((TextView) oneWargear.findViewById(R.id.description)).setText(action.getDescription());


            oneWargear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionActivity.startNewActivity(ActionsEditorActivity.this, action.getId());
                }
            });

            skillsContainer.addView(oneWargear);
        }
    }

}
