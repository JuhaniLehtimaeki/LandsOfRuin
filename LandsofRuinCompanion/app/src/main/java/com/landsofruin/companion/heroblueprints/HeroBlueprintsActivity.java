package com.landsofruin.companion.heroblueprints;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.companion.AppConstants;
import com.landsofruin.companion.state.heroblueprint.HeroBlueprint;
import com.landsofruin.gametracker.R;
import com.squareup.picasso.Picasso;

public class HeroBlueprintsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_blueprints);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("/admin/heroblueprint");

//FIXME: update to new Firebase UI
//        adapter = new FirebaseRecyclerAdapter<HeroBlueprint, HeroBlueprintViewHolder>(HeroBlueprint.class, R.layout.hero_blueprint_one_item, HeroBlueprintViewHolder.class, ref) {
//            @Override
//            public void populateViewHolder(HeroBlueprintViewHolder heroBlueprintViewHolder, final HeroBlueprint heroBlueprint, int position) {
//                Picasso.with(getBaseContext()).load(heroBlueprint.getCardImageUrl()).into(heroBlueprintViewHolder.cardImageView);
//                Picasso.with(getBaseContext()).load(heroBlueprint.getPortraitImageUrl()).into(heroBlueprintViewHolder.profileImageView);
//
//                heroBlueprintViewHolder.nameText.setText(heroBlueprint.getName());
//                heroBlueprintViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        HeroBlueprintActivity.startNewActivity(HeroBlueprintsActivity.this, heroBlueprint.getId());
//                    }
//                });
//
//            }
//        };
//
//        findViewById(R.id.add_new).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HeroBlueprintActivity.startNewActivity(HeroBlueprintsActivity.this, null);
//            }
//        });
//
//        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        adapter.cleanup();
    }

    public static class HeroBlueprintViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView profileImageView;
        ImageView cardImageView;

        public HeroBlueprintViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.text);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_pic);
            cardImageView = (ImageView) itemView.findViewById(R.id.card_pic);
        }
    }
}
