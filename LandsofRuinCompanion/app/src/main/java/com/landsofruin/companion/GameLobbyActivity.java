package com.landsofruin.companion;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.activity.AboutActivity;
import com.landsofruin.companion.activity.FeedbackActivity;
import com.landsofruin.companion.activity.MapEditorActivity;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.fragment.UnfinishedGamesDialogFragment;
import com.landsofruin.companion.fragment.welcomefragments.WelcomePagerLoginFragment;
import com.landsofruin.companion.fragment.welcomefragments.WelcomePagerOneFragment;
import com.landsofruin.companion.fragment.welcomefragments.WelcomePagerTwoFragment;
import com.landsofruin.companion.heroblueprints.HeroBlueprintsActivity;
import com.landsofruin.companion.net.client.ClientService;
import com.landsofruin.companion.net.event.ClientConnectedEvent;
import com.landsofruin.companion.net.event.ServerClosedConnectionEvent;
import com.landsofruin.companion.net.event.ServerStartedEvent;
import com.landsofruin.companion.net.server.ServerService;
import com.landsofruin.companion.newsticker.NewsTickerItem;
import com.landsofruin.companion.newsticker.NewsTickerManager;
import com.landsofruin.companion.progress.ProgressActivity;
import com.landsofruin.companion.provider.clients.GameClient;
import com.landsofruin.companion.ruleobjectsui.RuleObjectsActivity;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.tribemanagement.Tribe;
import com.landsofruin.companion.tribemanagement.UserAccount;
import com.landsofruin.companion.tribemanagement.YourTribeActivity;
import com.landsofruin.companion.tutorial.LearnTheGameActivity;
import com.landsofruin.companion.user.UserAccountManager;
import com.landsofruin.gametracker.BuildConfig;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.Arrays;

public class GameLobbyActivity extends FragmentActivity {

    private static final int RC_SIGN_IN = 123;

    private static final String TAG = "LoR/GameLobbyActivity";
    private View connectingView;
    private View continueGameButton;
    private View loggedInControls;
    private View loggedOutControls;
    private TextView versionInfo;
    private ImageView tribeIcon;
    private TextView profileName;
    private TextView tribeName;

    private boolean isAdmin = false;

    private View incompleteProfileOverlay;
    private View incompleteTribeOverlay;
    private View loadingOverlay;

    private ValueEventListener adminUserValueListener;
    private UserAccountManager.AccountRefreshListener accountListener;
    private View welcomeOverlay;
    private ViewPager welcomePager;
    private View menuButton;
    private EditText userNameEditText;
    private TextView newsTicker;
    private View newsTickerLinkIndicator;

    private Handler handler = new Handler();
    private Runnable newsTickerRunnable = new Runnable() {

        int currentItem = 0;
        String currentString = null;

        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }


            final ArrayList<NewsTickerItem> items = NewsTickerManager.getInstance().getItems();

            if (items == null || items.isEmpty()) {
                handler.postDelayed(newsTickerRunnable, 30000);
                return;
            }

            ++currentItem;
            if (currentItem >= items.size()) {
                currentItem = 0;
            }

            String newString = items.get(currentItem).getText();

            if (newString.equals(currentString)) {
                // no change
                handler.postDelayed(newsTickerRunnable, 30000);
                return;
            }

            currentString = newString;
            newsTicker.setText("(" + items.get(currentItem).getDate() + ") " + newString);

            if (items.get(currentItem).getUrl() != null && !items.get(currentItem).getUrl().isEmpty()) {
                newsTickerLinkIndicator.setVisibility(View.VISIBLE);

                newsTicker.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(currentItem).getUrl()));
                        startActivity(intent);
                    }
                });
            } else {
                newsTickerLinkIndicator.setVisibility(View.GONE);

                newsTicker.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(currentItem).getUrl()));
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("news link", "News link failed", e);
                            Toast.makeText(GameLobbyActivity.this, "Failed to load link, please try agian", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }


            handler.postDelayed(newsTickerRunnable, 10000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        versionInfo = (TextView) findViewById(R.id.version_info);
        loadingOverlay = findViewById(R.id.loading_overlay);

        findViewById(R.id.join_game_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameLobbyActivity.this, NearbyGamesActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.learn_game_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameLobbyActivity.this, LearnTheGameActivity.class);
                startActivity(intent);
            }
        });


        newsTickerLinkIndicator = findViewById(R.id.link_indicator);
        newsTicker = (TextView) findViewById(R.id.news_ticker_text);
        welcomeOverlay = findViewById(R.id.welcome_overlay);
        welcomePager = (ViewPager) findViewById(R.id.welcome_pager);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), auth.getCurrentUser() != null);
        welcomePager.setAdapter(adapter);
        PageIndicator pageIndicator = (PageIndicator) findViewById(R.id.welcome_indicator);
        pageIndicator.setViewPager(welcomePager);


        findViewById(R.id.welcome_skip_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeOverlay.setVisibility(View.GONE);
            }
        });


        final TextView nextButton = (TextView) findViewById(R.id.welcome_next_button);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (welcomePager.getCurrentItem() >= welcomePager.getAdapter().getCount() - 1) {
                    welcomeOverlay.setVisibility(View.GONE);
                } else {
                    welcomePager.setCurrentItem(welcomePager.getCurrentItem() + 1);
                }
            }
        });


        findViewById(R.id.complete_account_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameLobbyActivity.this, YourTribeActivity.class);
                startActivity(intent);
            }
        });

        pageIndicator.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);

                        if (welcomePager.getCurrentItem() >= welcomePager.getAdapter().getCount() - 1) {
                            nextButton.setText("finish");
                        } else {
                            nextButton.setText("next");
                        }

                    }
                }


        );


        connectingView = findViewById(R.id.overlay_connecting);
        connectingView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eat all events
            }
        });
        connectingView.setVisibility(View.GONE);


        incompleteProfileOverlay = findViewById(R.id.incomplete_account_overlay);
        incompleteTribeOverlay = findViewById(R.id.incomplete_tribe_overlay);

        findViewById(R.id.host_game).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onHostNewGame();
            }
        });

        continueGameButton = findViewById(R.id.continue_game);


        continueGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinuePreviousGame();
            }
        });


        TextView feedbackAndRulesLink = (TextView) findViewById(R.id.feedback_link);
        feedbackAndRulesLink.setPaintFlags(feedbackAndRulesLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        feedbackAndRulesLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameLobbyActivity.this, FeedbackActivity.class));
            }
        });

        menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(v);
                    }
                }
        );

        loggedOutControls = findViewById(R.id.logged_out_controls);
        loggedInControls = findViewById(R.id.logged_in_controls);


        tribeIcon = (ImageView) findViewById(R.id.tribe_logo);

        profileName = (TextView) findViewById(R.id.profile_name);
        tribeName = (TextView) findViewById(R.id.tribe_name);

        findViewById(R.id.tribe_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameLobbyActivity.this, YourTribeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.user_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameLobbyActivity.this, YourTribeActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.login_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();

            }
        });


        userNameEditText = (EditText) findViewById(R.id.user_name);
        findViewById(R.id.complete_account_save_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccountManager.getInstance().getUserAccount().setUsername(userNameEditText.getText().toString());
                UserAccountManager.getInstance().saveUserAccount();

            }
        });

        if (!isWelcomeShown()) {
            showWelcome();
        }

        handler.postDelayed(newsTickerRunnable, 2000);
    }


    private void showWelcome() {
        welcomeOverlay.setVisibility(View.VISIBLE);


        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean("welcomeshown", true);
        editor.apply();

    }

    private boolean isWelcomeShown() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean("welcomeshown", false);

    }


    public void startLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);

        welcomeOverlay.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(GameLobbyActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.landing_page_menu, popupMenu.getMenu());

        if (isAdmin) {
            popupMenu.getMenuInflater().inflate(R.menu.landing_page_menu_admin, popupMenu.getMenu());
        }

        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.about_menu) {
                            Intent intent = new Intent(GameLobbyActivity.this, AboutActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.feedback) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(getString(R.string.feedback_url)));
                            startActivity(intent);

                        } else if (id == R.id.progress_menu) {
                            Intent intent = new Intent(GameLobbyActivity.this, ProgressActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.tutorials_menu) {
                            Intent intent = new Intent(GameLobbyActivity.this, TutorialsActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.rule_items_menu) {
                            Intent intent = new Intent(GameLobbyActivity.this, RuleObjectsActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.map_editor_menu) {
                            Intent intent = new Intent(GameLobbyActivity.this, MapEditorActivity.class);
                            startActivity(intent);

                        } else if (id == R.id.logout_menu) {


                            UserAccountManager.getInstance().clean();

                            AuthUI.getInstance()
                                    .signOut(GameLobbyActivity.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showLoggedOutUi();
                                        }
                                    });

//                    if (getFirebaseRef().getAuth() != null && userAccountValueListener != null) {
//                        getFirebaseRef().child("user").child(getFirebaseRef().getAuth().getUid()).removeEventListener(userAccountValueListener);
//                    }
//
//                    getFirebaseRef().unauth();
//                    userAccount = null;
//
//                    LoginManager.getInstance().logOut();


                        } else if (id == R.id.hero_blueprints_menu) {
                            Intent intent = new Intent(GameLobbyActivity.this, HeroBlueprintsActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.my_tribe_menu) {
                            Intent intent = new Intent(GameLobbyActivity.this, YourTribeActivity.class);
                            startActivity(intent);
                        }


                        setImmersiveMode();
                        return true;
                    }
                }

        );

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()

                                       {
                                           @Override
                                           public void onDismiss(PopupMenu menu) {
                                               setImmersiveMode();
                                           }
                                       }

        );
        popupMenu.show();
    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            initialiseDataAfterLogin();
        } else {
            showLoggedOutUi();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            loadingOverlay.setVisibility(View.GONE);
            if (resultCode == RESULT_OK) {
                initialiseDataAfterLogin();
            } else {
            }
        }
    }


    public static boolean isKindleFire() {
        return android.os.Build.MANUFACTURER.equals("Amazon")
                && (android.os.Build.MODEL.equals("Kindle Fire")
                || android.os.Build.MODEL.startsWith("KF"));
    }


    @Override
    protected void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);


        Intent serverIntent = new Intent(this, ServerService.class);
        stopService(serverIntent);

        Intent clientIntent = new Intent(this, ClientService.class);
        stopService(clientIntent);

        setImmersiveMode();


//        if (getFirebaseRef().getAuth() != null) {
//            initialiseDataAfterLogin();
//        } else {
//            showLoggedOutUi();
//        }

        FirebaseDatabase.getInstance().getReference().child("admin/minimum_app_version/android").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int minimumVersionRequired = dataSnapshot.getValue(Integer.class);

                if (PlayerAccount.getAppVersion() < minimumVersionRequired) {


                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(GameLobbyActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Update Required");
                    builder.setMessage("Your version of Lands of Ruin Command Console is not supported anymore. Please update the app from Google Play!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.landsofruin.gametracker"));
                            startActivity(i);
                            dialog.dismiss();
                            finish();
                        }
                    });
                    builder.show();

                }


            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }


    private void showLoggedOutUi() {
        menuButton.setVisibility(View.GONE);
        loggedOutControls.setVisibility(View.VISIBLE);
        loggedInControls.setVisibility(View.GONE);
        versionInfo.setText("  " + PlayerAccount.getAppVersionTitle() + (BuildConfig.DEBUG ? " (DEBUG)" : ""));


    }

    private void initialiseDataAfterLogin() {
        UserAccountManager.getInstance().initialise();

        versionInfo.setText(PlayerAccount.getAppVersionTitle() + (BuildConfig.DEBUG ? " (DEBUG)" : ""));

        menuButton.setVisibility(View.VISIBLE);
        loggedOutControls.setVisibility(View.GONE);
        loggedInControls.setVisibility(View.VISIBLE);


        if (accountListener == null) {
            accountListener = new UserAccountManager.AccountRefreshListener() {
                @Override
                public void onAccountRefreshed() {

                    if (isFinishing()) {
                        return;
                    }
                    UserAccount userAccount = UserAccountManager.getInstance().getUserAccount();

                    if (userAccount == null) {
                        // not ready yet! UserAccountManager will create a new account automatically
                        return;
                    }

                    Tribe usersTribe = userAccount.getTribe();
                    if (usersTribe != null) {

                        if (usersTribe.getName() == null || usersTribe.getName().isEmpty()) {
                            tribeName.setText("Unnamed tribe");
                        } else {
                            tribeName.setText(usersTribe.getName());
                        }
                        if (userAccount.getTribe().getLargeTribeLogoUri() != null && !userAccount.getTribe().getLargeTribeLogoUri().isEmpty()) {
                            Picasso.with(GameLobbyActivity.this).load(userAccount.getTribe().getLargeTribeLogoUri()).into(tribeIcon);
                        }
                    } else {
                        tribeName.setText("No tribe yet... ");
                    }


                    String userName = userAccount.getUsername();
                    if (userName == null || userName.isEmpty()) {
                        profileName.setText("No Screenname");
                        PlayerAccount.setPlayerScreenName("No Screenname");
                    } else {
                        profileName.setText(userName);
                        PlayerAccount.setPlayerScreenName(userName);
                    }


                    setIncompleteProfileOverlayVisibility();


                    boolean hasUnfinishedGames = GameClient.hasUnfinishedGames(GameLobbyActivity.this, PlayerAccount.getPlayerName());
                    continueGameButton.setVisibility(hasUnfinishedGames ? View.VISIBLE : View.GONE);
                }
            };


            UserAccountManager.getInstance().addAccountRefreshListener(accountListener);
        }

        adminUserValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    isAdmin = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(Boolean.class);
                } catch (Exception e) {
                    //happens if there's no element
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("adminusers").addValueEventListener(adminUserValueListener);


    }


    private void setIncompleteProfileOverlayVisibility() {

        incompleteProfileOverlay.setVisibility(View.GONE);
        incompleteTribeOverlay.setVisibility(View.GONE);

        UserAccount userAccount = UserAccountManager.getInstance().getUserAccount();
        if (userAccount == null) {
            return;
        }

        if (userAccount.getUsername() == null || userAccount.getUsername().isEmpty()) {
            incompleteProfileOverlay.setVisibility(View.VISIBLE);
            userNameEditText.requestFocus();
            return;
        }

        if (userAccount.getUnlockedCharacters() == null || userAccount.getUnlockedCharacters().isEmpty()) {
            incompleteTribeOverlay.setVisibility(View.VISIBLE);
            return;
        }

        if (userAccount.getTribe() == null || userAccount.getTribe().getCharacters() == null || userAccount.getTribe().getCharacters().isEmpty()) {
            incompleteTribeOverlay.setVisibility(View.VISIBLE);

            return;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onServerStarted(ServerStartedEvent event) {
        Log.d(TAG, "Server running for game " + event.getGameIdentifier());

        CompanionApplication.from(this).resetClientGame(event.getGameIdentifier(), event.getGameTitle());

        ClientService.connect(this, "127.0.0.1", event.getPort());
    }

    @Subscribe
    public void onClientConnected(ClientConnectedEvent event) {
        Intent intent = new Intent(this, GameSetupActivity.class);
        startActivity(intent);

        connectingView.setVisibility(View.GONE);
    }


    private void onHostNewGame() {

        GameState gameState = GameClient.createGame();

        ServerService.start(this, gameState);
    }


    private void setImmersiveMode() {

        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = 0; //= uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("ui mode", "Turning immersive mode mode off. ");
        } else {
            Log.i("ui mode", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }


    private void onContinuePreviousGame() {
        UnfinishedGamesDialogFragment fragment = new UnfinishedGamesDialogFragment();
        fragment.show(getSupportFragmentManager(), "unfinished_games_dialog");
    }

    public void onHostPreviousGame(String identifier) {
        connectingView.setVisibility(View.VISIBLE);

        GameClient.loadGame(identifier, new GameClient.GameLoadedCallback() {
            @Override
            public void onGameLoaded(GameState gameState) {
                for (PlayerState player : gameState.getPlayers()) {
                    player.setConnected(false);
                }

                GameClient.saveGame(gameState);

                ServerService.start(GameLobbyActivity.this, gameState);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adminUserValueListener != null) {
            FirebaseDatabase.getInstance().getReference().child("adminusers").removeEventListener(adminUserValueListener);
        }

        UserAccountManager.getInstance().clean();

        if (accountListener != null) {
            UserAccountManager.getInstance().removeAccountRefreshListener(accountListener);
        }
    }


    @Subscribe
    public void onServerClosedConnection(ServerClosedConnectionEvent event) {
        Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT).show();

        connectingView.setVisibility(View.GONE);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> fragments = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm, boolean loggedIn) {
            super(fm);

            fragments.add(new WelcomePagerOneFragment());
            fragments.add(new WelcomePagerTwoFragment());

            if (!loggedIn) {
                fragments.add(new WelcomePagerLoginFragment());
            }

        }


        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


}
