package com.landsofruin.companion.newsticker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by juhani on 19/12/2016.
 */

public class NewsTickerManager {


    private static NewsTickerManager instance = new NewsTickerManager();
    private ValueEventListener newsItemsValueListener;

    private ArrayList<NewsTickerItem> items = new ArrayList<>();

    private NewsTickerManager() {


    }

    public void initialise() {
        newsItemsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    NewsTickerItem newsItem = item.getValue(NewsTickerItem.class);
                    items.add(newsItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("news").addValueEventListener(newsItemsValueListener);

    }


    public ArrayList<NewsTickerItem> getItems() {
        return (ArrayList<NewsTickerItem>) items.clone();
    }

    public static NewsTickerManager getInstance() {
        return instance;
    }


}
