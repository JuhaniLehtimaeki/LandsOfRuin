package com.landsofruin.companion.tribemanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class MiniaturePicsActivity extends AppCompatActivity {


    private String tribeCharacterId;
    private UserAccount userAccount;
    private TribeCharacter tribeCharacter;
    private ImageView currentProfilePic;


    private ViewPager pager;
    private ScreenSlidePagerAdapter adapter;
    private ValueEventListener userValueListener;
    private View resetToDefaultButton;
    private DatabaseReference firebaseRef;

    public static void startActivity(Context context, String tribeCharacterId) {
        Intent intent = new Intent(context, MiniaturePicsActivity.class);
        intent.putExtra("tribeCharacterId", tribeCharacterId);
        context.startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onSetImageEvent(SetImageEvent event) {

        if (tribeCharacter != null) {
            tribeCharacter.setPortraitImageUrl(event.getCharacterPic().getProfilePicture());
            tribeCharacter.setCardImageUrl(event.getCharacterPic().getCardPicture());

            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").child(tribeCharacterId).setValue(tribeCharacter);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tribeCharacterId = getIntent().getStringExtra("tribeCharacterId");

        setContentView(R.layout.activity_miniature_pics);

        resetToDefaultButton = findViewById(R.id.reset_to_default_button);
        resetToDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tribeCharacter != null) {
                    tribeCharacter.setPortraitImageUrl(tribeCharacter.getDefaultPortraitImageUrl());
                    tribeCharacter.setCardImageUrl(tribeCharacter.getDefaultCardImageUrl());

                    firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").child(tribeCharacterId).setValue(tribeCharacter);
                    finish();
                }

            }
        });

        pager = (ViewPager) findViewById(R.id.pager);

        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.pager_indicator);
        titleIndicator.setViewPager(pager);


        currentProfilePic = (ImageView) findViewById(R.id.profile_pic);

        firebaseRef = FirebaseDatabase.getInstance().getReference();

        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userAccount = dataSnapshot.getValue(UserAccount.class);


                ArrayList<CharacterPicture> availablePics = new ArrayList<>();
                for (DataSnapshot picDataSnapshot : dataSnapshot.child("miniaturepics").getChildren()) {
                    CharacterPicture characterPicture = picDataSnapshot.getValue(CharacterPicture.class);
                    availablePics.add(characterPicture);

                }

                adapter.setCharacterPics(availablePics);
                pager.setCurrentItem(adapter.getCount() - 1);

                if (userAccount != null && userAccount.getTribe() != null) {


                    ValueEventListener characterValueListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            tribeCharacter = dataSnapshot.getValue(TribeCharacter.class);


                            Picasso.with(MiniaturePicsActivity.this).load(tribeCharacter.getCardImageUrl()).into(currentProfilePic);


                            if (!tribeCharacter.getCardImageUrl().equals(tribeCharacter.getDefaultCardImageUrl()) || !tribeCharacter.getPortraitImageUrl().equals(tribeCharacter.getDefaultPortraitImageUrl())) {
                                resetToDefaultButton.setVisibility(View.VISIBLE);
                            } else {
                                resetToDefaultButton.setVisibility(View.GONE);
                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    };

                    firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").child(tribeCharacterId).addListenerForSingleValueEvent(characterValueListener);


                }
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(userValueListener);


        findViewById(R.id.add_pic_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPictureActivity.startActivity(MiniaturePicsActivity.this, false);
            }
        });

        findViewById(R.id.add_pic_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPictureActivity.startActivity(MiniaturePicsActivity.this, true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (userValueListener != null) {
            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(userValueListener);
        }
    }

    public static class CharacterPicFragment extends Fragment {

        private static final String ARGUMENT_PC_ID_1 = "ARGUMENT_PC_ID_1";
        private ImageView cardPic;
        private ImageView profilePic;
        private CharacterPicture characterPic;
        private View useThisPicButton;
        private String picId;
        private DatabaseReference firebaseRef;


        public static CharacterPicFragment newInstance(CharacterPicture pc1) {
            CharacterPicFragment ret = new CharacterPicFragment();

            Bundle args = new Bundle();
            args.putString(ARGUMENT_PC_ID_1, pc1.getId());
            ret.setArguments(args);

            return ret;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.character_picture_fragment, container, false);


            picId = getArguments().getString(ARGUMENT_PC_ID_1);

            cardPic = (ImageView) view.findViewById(R.id.card_pic);
            profilePic = (ImageView) view.findViewById(R.id.profile_pic);

            useThisPicButton = view.findViewById(R.id.use_pic);
            firebaseRef = FirebaseDatabase.getInstance().getReference();


            ValueEventListener characterValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    characterPic = dataSnapshot.getValue(CharacterPicture.class);
                    Picasso.with(getActivity()).load(characterPic.getCardPicture()).into(cardPic);
                    Picasso.with(getActivity()).load(characterPic.getProfilePicture()).into(profilePic);

                    useThisPicButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            BusProvider.getInstance().post(new SetImageEvent(characterPic));
                        }
                    });
                }


                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            };

            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("miniaturepics").child(picId).addListenerForSingleValueEvent(characterValueListener);


            return view;
        }


        @Override
        public void onResume() {
            super.onResume();
        }


        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }


    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        private List<CharacterPicture> pictures = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);

        }


        public void setCharacterPics(List<CharacterPicture> pictures) {
            this.pictures = pictures;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return CharacterPicFragment.newInstance(this.pictures.get(position));
        }

        @Override
        public int getCount() {
            return this.pictures.size();
        }


    }


    private static class SetImageEvent {
        private CharacterPicture characterPic;


        private SetImageEvent(CharacterPicture characterPic) {
            this.characterPic = characterPic;

        }

        public CharacterPicture getCharacterPic() {
            return characterPic;
        }
    }
}
