package com.landsofruin.companion.progress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.gametracker.R;

public class ProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        final ViewGroup items = (ViewGroup) findViewById(R.id.items_container);


        FirebaseDatabase.getInstance().getReference().child("progress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.removeAllViews();


                for (DataSnapshot item : dataSnapshot.getChildren()) {

                    final ProgressItem oneProgressItem = item.getValue(ProgressItem.class);


                    View oneItem = LayoutInflater.from(ProgressActivity.this).inflate(R.layout.one_progress_item, items, false);
                    ((TextView) oneItem.findViewById(R.id.title)).setText(oneProgressItem.title);
                    ((TextView) oneItem.findViewById(R.id.description)).setText(oneProgressItem.description);
                    ((TextView) oneItem.findViewById(R.id.eta)).setText("ETA: " + oneProgressItem.eta);

                    final View progress = oneItem.findViewById(R.id.progress_bar_orange);


                    ViewTreeObserver vto = progress.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            progress.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            int width = progress.getMeasuredWidth();
                            ViewGroup.LayoutParams params = progress.getLayoutParams();
                            params.width = (int) (width * oneProgressItem.progress);
                            progress.setLayoutParams(params);
                        }
                    });

                    items.addView(oneItem);
                }


            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }


    public static class ProgressItem {
        public String description;
        public String lastupdated;
        public float progress;
        public String title;
        public String eta;
    }

}
