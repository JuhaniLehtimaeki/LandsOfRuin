package com.landsofruin.companion.ruleobjectsui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;

public class WeaponsEditorActivity extends AppCompatActivity {

    private ViewGroup weaponsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapons_editor);
        weaponsContainer = ((ViewGroup) findViewById(R.id.weapons_container));

        updateWeapons();
    }


    private void updateWeapons() {

        weaponsContainer.removeAllViews();

        for (final WargearOffensive wargear : WargearManager.getInstance().getOffensiveWargear()) {

            View oneWargear = getLayoutInflater().inflate(R.layout.weapon_editor_one_weapon, weaponsContainer, false);

            ((TextView) oneWargear.findViewById(R.id.name)).setText(wargear.getName());
            ((TextView) oneWargear.findViewById(R.id.description)).setText(wargear.getCategory());


            Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(wargear.getCategory());
            if (resource != null) {
                ((ImageView) oneWargear.findViewById(R.id.weapon_image)).setImageResource(resource);
            }


            oneWargear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeaponEditorActivity.startActivity(wargear.getWeaponId(), WeaponsEditorActivity.this);
                }
            });


            ((TextView) oneWargear.findViewById(R.id.id)).setText(""+wargear.getId());

            weaponsContainer.addView(oneWargear);
        }
    }

}
