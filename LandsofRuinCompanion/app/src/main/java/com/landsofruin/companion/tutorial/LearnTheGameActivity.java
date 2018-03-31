package com.landsofruin.companion.tutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.landsofruin.companion.TutorialsActivity;
import com.landsofruin.companion.tribemanagement.UserAccount;
import com.landsofruin.companion.user.UserAccountManager;
import com.landsofruin.gametracker.R;

public class LearnTheGameActivity extends AppCompatActivity {


    private UserAccountManager.AccountRefreshListener accountListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please make sure your internet connection is working and that you're logged in to Lands of Ruin and try again. Sorry for the inconvinience!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        setContentView(R.layout.activity_learn_game);


        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        accountListener = new UserAccountManager.AccountRefreshListener() {
            @Override
            public void onAccountRefreshed() {


                UserAccount userAccount = UserAccountManager.getInstance().getUserAccount();

                if (userAccount == null) {
                    // user account manager will create one automatically
                } else if (userAccount.getTribe() == null) {
                    UserAccountManager.getInstance().createEmptyDefaultTribe();
                } else {
                    initialiseContentAfterEverythignLoaded();

                }

            }
        };

        findViewById(R.id.tutorial_1).setEnabled(false);
        findViewById(R.id.tutorial_2).setEnabled(false);
        findViewById(R.id.tutorial_3).setEnabled(false);

//        findViewById(R.id.tutorial_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LearnTheGameActivity.this, Tutorial1Activity.class));
//            }
//        });


        findViewById(R.id.in_app_tutorials).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LearnTheGameActivity.this, TutorialsActivity.class));
            }
        });

        findViewById(R.id.vid_link_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=TPHt4VR6ZqI")));
            }
        });


        findViewById(R.id.vid_link_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=F1UlT5NJwnU")));
            }
        });


        findViewById(R.id.vid_link_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=i1Y7D8fES2E")));
            }
        });


        findViewById(R.id.vid_link_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=M6bXO_QA_ns")));
            }
        });

        findViewById(R.id.getting_started_web).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://landsofruin.com/getstarted.html")));
            }
        });


        findViewById(R.id.rulebook_website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://landsofruin.com/downloads.html")));
            }
        });

        UserAccountManager.getInstance().addAccountRefreshListener(accountListener);


    }


    private void initialiseContentAfterEverythignLoaded() {
        UserAccount userAccount = UserAccountManager.getInstance().getUserAccount();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountListener != null) {
            UserAccountManager.getInstance().removeAccountRefreshListener(accountListener);
        }
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

}
