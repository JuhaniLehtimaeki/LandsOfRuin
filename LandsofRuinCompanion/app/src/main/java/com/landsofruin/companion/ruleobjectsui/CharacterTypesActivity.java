package com.landsofruin.companion.ruleobjectsui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.companion.AppConstants;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.gametracker.R;

public class CharacterTypesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_types);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("/rule/charactertype");

//FIXME: update to new Firebase UI
//        adapter = new FirebaseRecyclerAdapter<CharacterType, CharacterTypeViewHolder>(CharacterType.class, R.layout.rule_object_one_item, CharacterTypeViewHolder.class, ref) {
//            @Override
//            public void populateViewHolder(CharacterTypeViewHolder characterTypeViewHolder, final CharacterType characterType, int position) {
//                characterTypeViewHolder.nameText.setText(characterType.getName());
//                characterTypeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CharacterTypeActivity.startNewActivity(CharacterTypesActivity.this, characterType.getId());
//                    }
//                });
//
//            }
//        };
//
//        findViewById(R.id.add_new).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CharacterTypeActivity.startNewActivity(CharacterTypesActivity.this, null);
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

    public static class CharacterTypeViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        public CharacterTypeViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
