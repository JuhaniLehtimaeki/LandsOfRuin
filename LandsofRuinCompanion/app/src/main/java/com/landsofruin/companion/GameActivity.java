package com.landsofruin.companion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.landsofruin.companion.activity.AboutActivity;
import com.landsofruin.companion.cards.CardOverlayViewsLayout;
import com.landsofruin.companion.cards.CharacterCard;
import com.landsofruin.companion.cards.CharacterCardFragment;
import com.landsofruin.companion.cards.EnemyCardOverlayLayout;
import com.landsofruin.companion.cards.EnemyCharacterFragment;
import com.landsofruin.companion.cards.FriendlyCharacterView;
import com.landsofruin.companion.cards.FriendlySquadView;
import com.landsofruin.companion.cards.events.CardViewSelectedEvent;
import com.landsofruin.companion.cards.events.EnemyCardSelectedEvent;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.eventbus.EndThrowUIEvent;
import com.landsofruin.companion.eventbus.OpponentIsWaitingForYouEvent;
import com.landsofruin.companion.eventbus.RotateMapEventsEvent;
import com.landsofruin.companion.eventbus.ShowHideEnemyCardsEvent;
import com.landsofruin.companion.eventbus.ShowIntelOverlay;
import com.landsofruin.companion.eventbus.ShowScenarioInfoEvent;
import com.landsofruin.companion.eventbus.StartThrowUIEvent;
import com.landsofruin.companion.eventbus.TutorialShowEvent;
import com.landsofruin.companion.eventbus.TutorialShowMoraleInfoEvent;
import com.landsofruin.companion.fragment.GameEndedFragment;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.net.event.ServerClosedConnectionEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameConstants;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.NextAttackState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.transition.ActionPhaseTransition;
import com.landsofruin.companion.state.transition.PlayerReadyPreGameTransition;
import com.landsofruin.companion.state.transition.SetPlayerApprovePhaseUndoRequestTransition;
import com.landsofruin.companion.state.transition.StartEndGameConfirmationTransition;
import com.landsofruin.companion.state.transition.StartPhaseUndoRequestTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.tutorial.TutorialUtils;
import com.landsofruin.companion.utils.UIUtils;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actionsui.ActionsThrowUIFragment;
import com.landsofruin.gametracker.actionsui.AttackOverlayFragment;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;
import java.util.List;

import static com.landsofruin.companion.state.gameruleobjects.skill.Skill.SKILL_ID_INFILTRATE;

public class GameActivity extends BaseGameActivity implements OnClickListener {


    private View overlayNotReady;
    private View overlayWaitingForOpponent;
    private View overlayZombiePhaseSkipped;
    private View gameEndedContainer;
    private View attackUiContainer;
    private int previousPhase = -1;
    private Thread backButtonThread;

    private TextView teamMorale;
    private View moraleLevelProgressBarOrange;
    private View moraleLevelProgressBarWhite;

    private LinkedList<CharacterCard> characterCardsFriendly = new LinkedList<>();
    private LinkedList<CharacterCardFragment> characterCardsEnemy = new LinkedList<>();
    private boolean friendlyCharactersAdded = false;
    private CardOverlayViewsLayout friendlyCardOverlay;
    private EnemyCardOverlayLayout enemyCardOverlay;


    private boolean enemyCharactersAdded;

    private boolean uIReady = false;
    private boolean endGameFragmentAdded = false;
    private MenuItem undoPhaseChangeMenu;

    private View intelButton;
    private View throwUIContainer;
    private View enemyCardsButton;
    private View turnMoraleNegative;
    private View turnMoraleNegativeWhite;
    private View turnMoralePositive;
    private View turnMoralePositiveWhite;
    private View nextButton;
    private TextView phaseTitleText;
    private TextView phaseInfoText;
    private TextView nextButtonText;
    private TextView phaseSubTitleText;
    private View helpInfoOverlay;


    public void setImmersiveMode() {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);


        overlayNotReady = findViewById(R.id.overlay_not_ready);
        overlayNotReady.setOnClickListener(this);


        findViewById(R.id.show_tutorial).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getInstance().post(new TutorialShowEvent(helpInfoOverlay.getX(), helpInfoOverlay.getY(), helpInfoOverlay.getWidth(), helpInfoOverlay.getHeight()));
                helpInfoOverlay.setVisibility(View.GONE);
            }
        });

        phaseTitleText = (TextView) findViewById(R.id.phase_title_text);
        phaseSubTitleText = (TextView) findViewById(R.id.phase_sub_title_text);
        phaseInfoText = (TextView) findViewById(R.id.phase_info_text);
        helpInfoOverlay = findViewById(R.id.help_info_overlay);
        findViewById(R.id.help_info_overlay_close_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                helpInfoOverlay.setVisibility(View.GONE);
            }
        });

        final View infoButton = findViewById(R.id.info_button);
        findViewById(R.id.info_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                helpInfoOverlay.setScaleX(0);
                helpInfoOverlay.setScaleY(0);
                helpInfoOverlay.setPivotX(0);
                helpInfoOverlay.setPivotY(0);

                helpInfoOverlay.setTranslationX(infoButton.getX());
                helpInfoOverlay.setTranslationY(infoButton.getY());

                helpInfoOverlay.setVisibility(View.VISIBLE);

                helpInfoOverlay.animate().scaleX(1).scaleY(1);
            }
        });


        nextButtonText = (TextView) findViewById(R.id.next_button_text);
        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getGame().getPhase().getPrimaryPhase() != PrimaryPhase.PRE_GAME && getGame().getPhase().getPrimaryPhase() != PrimaryPhase.ZOMBIES && !getGame().getPhase().isMine()) {
                    return;
                }


                if (getGame().getPhase().getPrimaryPhase() == PrimaryPhase.ACTION) {


                    if (isUnperformedActions()) {


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);

                        alertDialogBuilder.setTitle("Unperformed Actions");
                        alertDialogBuilder
                                .setMessage("You have characters with actions remaining! Do you want to advance to next phase and lose the actions?")
                                .setCancelable(false)
                                .setPositiveButton("Yes, next phase", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        advanceGamePhase();
                                        setImmersiveMode();
                                    }
                                })
                                .setNegativeButton("No, stay in action phase", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                        setImmersiveMode();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        advanceGamePhase();
                    }


                } else {
                    advanceGamePhase();
                }


            }
        });


        attackUiContainer = findViewById(R.id.attack_ui_container);
        overlayWaitingForOpponent = findViewById(R.id.overlay_waiting_for_opponent);
        overlayZombiePhaseSkipped = findViewById(R.id.overlay_zombie_phase_skipped);
        gameEndedContainer = findViewById(R.id.game_ended);

        friendlyCardOverlay = (CardOverlayViewsLayout) findViewById(R.id.cards_friendly);
        enemyCardOverlay = (EnemyCardOverlayLayout) findViewById(R.id.cards_enemy);


        teamMorale = (TextView) findViewById(R.id.team_morale);
        moraleLevelProgressBarOrange = findViewById(R.id.morale_progress_bar_orange);
        moraleLevelProgressBarWhite = findViewById(R.id.morale_progress_bar_white);


        turnMoraleNegative = findViewById(R.id.turn_morale_negative);
        turnMoraleNegativeWhite = findViewById(R.id.turn_morale_negative_white);
        turnMoralePositive = findViewById(R.id.turn_morale_positive);
        turnMoralePositiveWhite = findViewById(R.id.turn_morale_positive_white);


        turnMoraleNegative.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                turnMoraleNegative.getViewTreeObserver().removeGlobalOnLayoutListener(this);


                turnMoraleNegative.setPivotX(turnMoraleNegative.getWidth());
                turnMoraleNegativeWhite.setPivotX(turnMoraleNegativeWhite.getWidth());
            }
        });


        intelButton = findViewById(R.id.intel_button);
        intelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new ShowIntelOverlay());
            }
        });

        throwUIContainer = findViewById(R.id.throw_ui_container);


        findViewById(R.id.menu_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });


        enemyCardsButton = findViewById(R.id.enemy_cards_stack_click_stealer);
        enemyCardsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new ShowHideEnemyCardsEvent((int) (((View) enemyCardsButton.getParent()).getX() + enemyCardsButton.getX()) + 5, (int) (((View) enemyCardsButton.getParent()).getY() + enemyCardsButton.getY())));
            }
        });

        findViewById(R.id.morale_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                BusProvider.getInstance().post(new TutorialShowMoraleInfoEvent());

            }
        });
        ;
        friendlyCardOverlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                friendlyCardOverlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                friendlyCardOverlay.animateToSide();

            }
        });

    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.in_game_menu, popupMenu.getMenu());

        undoPhaseChangeMenu = popupMenu.getMenu().findItem(R.id.undo_phase_change);
        undoPhaseChangeMenu.setEnabled(getGame().isPhaseChangeUndoEnabled() && getGame().getPhase().isMine());

        if (TutorialUtils.getInstance(this).isTutorialsEnabled()) {
            popupMenu.getMenu().findItem(R.id.enable_tutorial).setVisible(false);
        }


        if (!TutorialUtils.getInstance(this).isTutorialsEnabled()) {
            popupMenu.getMenu().findItem(R.id.reset_tutorials).setVisible(false);
        }


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.about_menu) {
                    Intent intent = new Intent(GameActivity.this, AboutActivity.class);
                    startActivity(intent);
                } else if (id == R.id.feedback) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.feedback_url)));
                    startActivity(intent);
                } else if (id == R.id.end_game) {
                    sendToServer(new StartEndGameConfirmationTransition());

                } else if (id == R.id.rotate_map) {
                    BusProvider.getInstance().post(new RotateMapEventsEvent());
                } else if (id == R.id.undo_phase_change) {
                    Toast.makeText(GameActivity.this, "Game phase change undo requested.", Toast.LENGTH_LONG).show();

                    if (getGame().getPlayers().size() <= 1) {
                        sendToServer(new SetPlayerApprovePhaseUndoRequestTransition(getGame().getMe().getIdentifier()));
                    } else {
                        sendToServer(new StartPhaseUndoRequestTransition(getGame().getMe().getIdentifier()));
                    }
                } else if (id == R.id.enable_tutorial) {
                    TutorialUtils.getInstance(GameActivity.this).setTutorialsEnabled(true);
                } else if (id == R.id.reset_tutorials) {
                    TutorialUtils.getInstance(GameActivity.this).resetTuorialsSeen();
                } else if (id == R.id.scenario) {
                    BusProvider.getInstance().post(new ShowScenarioInfoEvent());
                }


                setImmersiveMode();
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                setImmersiveMode();
            }
        });

        popupMenu.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveMode();
        BusProvider.getInstance().register(this);

        if (!hasGame()) {
            finish();
            return;
        }


        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        updateUI();
    }

    @Subscribe
    public void onServerClosedConnection(ServerClosedConnectionEvent event) {
        Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT)
                .show();

        finish();
    }

    private void updateUI() {

        setImmersiveMode();

        GameState game = getGame();

        addPlayerCharacterFragments();
        addOpponentCharacterFragments();


        PhaseState phase = game.getPhase();
        if (phase.getPrimaryPhase() == PrimaryPhase.GAME_END) {

            if (endGameFragmentAdded) {
                return;
            }
            GameEndedFragment fragment = new GameEndedFragment();

            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.game_ended, fragment);
            transaction.commit();

            gameEndedContainer.setVisibility(View.VISIBLE);

            endGameFragmentAdded = true;
            return;
        } else if (phase.getPrimaryPhase() == PrimaryPhase.PRE_GAME) {

            animateCardsToRightForm(false);

        } else if (previousPhase == -1
                || previousPhase != game.getPhase().getPrimaryPhase()) {
            previousPhase = game.getPhase().getPrimaryPhase();
            animateCardsToRightForm(false);
        }

        overlayNotReady
                .setVisibility(game.isReady() ? View.GONE : View.VISIBLE);

        overlayWaitingForOpponent.setVisibility(View.GONE);
        overlayZombiePhaseSkipped.setVisibility(View.GONE);

        updateSecondaryText();

        if (!game.isReady()) {
            uIReady = false;
            return;
        }

        if (!uIReady) {
            uIReady = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    enemyCardOverlay.setupChildrenOrderTracking(characterCardsEnemy);
                    enemyCardOverlay.animateToStack(((int) (((View) enemyCardsButton.getParent()).getX() + enemyCardsButton.getX())), (int) (((View) enemyCardsButton.getParent()).getY() + enemyCardsButton.getY()));
                }
            }, 1500);


        }


        if (phase.getSecondaryPhase() == SecondaryPhase.WAITING_FOR_ATTACK) {

            NextAttackState attackState = phase.getNextAttackState();
            if (attackState != null) {

                if (attackUiContainer.getVisibility() != View.VISIBLE) {
                    attackUiContainer.setVisibility(View.VISIBLE);

                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();

                    transaction.replace(R.id.attack_ui_container,
                            new AttackOverlayFragment());
                    transaction.commit();
                }

            }


        } else {
            attackUiContainer.setVisibility(View.GONE);
        }

        if (phase.getSecondaryPhase() == SecondaryPhase.ZOMBIE_PHASE_SKIPPED) {
            overlayZombiePhaseSkipped.setVisibility(View.VISIBLE);
        }

        TeamState team = game.getMe().getTeam();

        //update morale
        if (game.getGameMode() == GameState.GAME_MODE_BASIC) {
            teamMorale.setText("Basic Game Mode");
        } else {


            String oldText = teamMorale.getText().toString();
            String newText = "";
            if (team.getTeamStatus(game) == TeamState.TEAM_STATUS_NORMAL) {
                newText = "";
            } else if (team.getTeamStatus(game) == TeamState.TEAM_STATUS_CONFUSION) {
                newText = "Team Confused!";
            } else if (team.getTeamStatus(game) == TeamState.TEAM_STATUS_PANIC) {
                newText = "Team Panicked!";
            }


            if (!oldText.equals(newText)) {

                final String newTextF = newText;
                final ViewPropertyAnimator animator = teamMorale.animate();
                animator.scaleX(0).scaleY(0).setInterpolator(new AnticipateInterpolator());
                animator.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animator.setListener(null);
                        teamMorale.setText(newTextF);

                        teamMorale.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator());
                    }
                });


            }


            int progressValue = team.getPsychologyPool(game);
            if (progressValue < 1) {
                progressValue = 0;
            }

            float previousScale = moraleLevelProgressBarOrange.getScaleX();
            float newScale = progressValue / 25f;
            if (newScale != previousScale) {
                moraleLevelProgressBarOrange.setScaleX(newScale);
                moraleLevelProgressBarWhite.animate().scaleX(newScale).setDuration(2000).setStartDelay(3000);
            }


            int highestLD = team.getCurrentLeaderLeadershipValue();
            int effect = team.getCurrentPositivePsychologyEffect()
                    - team.getCurrentNegativePsychologyEffect();

            effect = effect + highestLD;

            float newPositiveEffect = effect / 10f;
            float newNegativeEffect = -effect / 10f;

            if (newNegativeEffect < 0) {
                newNegativeEffect = 0;
            }

            if (newPositiveEffect < 0) {
                newPositiveEffect = 0;
            }

            float previousTurnMoralePositive = turnMoralePositive.getScaleX();
            if (newPositiveEffect > previousTurnMoralePositive) {
                turnMoralePositive.clearAnimation();
                turnMoralePositive.animate().scaleX(newPositiveEffect).setDuration(2000).setStartDelay(3000);
                turnMoralePositiveWhite.setScaleX(newPositiveEffect);
            } else if (newPositiveEffect < previousTurnMoralePositive) {
                turnMoralePositive.setScaleX(newPositiveEffect);
                turnMoralePositiveWhite.clearAnimation();
                turnMoralePositiveWhite.animate().scaleX(newPositiveEffect).setDuration(2000).setStartDelay(3000);
            }


            float previousTurnMoraleNegative = turnMoraleNegative.getScaleX();
            if (newNegativeEffect > previousTurnMoraleNegative) {
                turnMoraleNegative.clearAnimation();
                turnMoraleNegative.animate().scaleX(newNegativeEffect).setDuration(2000).setStartDelay(3000);
                turnMoraleNegativeWhite.setScaleX(newNegativeEffect);
            } else if (newNegativeEffect < previousTurnMoraleNegative) {
                turnMoraleNegative.setScaleX(newNegativeEffect);
                turnMoraleNegativeWhite.clearAnimation();
                turnMoraleNegativeWhite.animate().scaleX(newNegativeEffect).setDuration(2000).setStartDelay(3000);
            }

        }

        updateToolbar();

    }


    private void animateCardsToRightForm(boolean force) {


        friendlyCardOverlay.animateToSide();
        enemyCardOverlay.animateToStack((int) (((View) enemyCardsButton.getParent()).getX() + enemyCardsButton.getX()), (int) (((View) enemyCardsButton.getParent()).getY() + enemyCardsButton.getY()));

    }

    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.overlay_not_ready) {
            return; // Do nothing but eat all events.
        }
    }

    @Override
    public void onBackPressed() {


        if (this.enemyCardOverlay.consumeBackPress()) {
            return;
        }

        if (getGame().getPhase().getPrimaryPhase() == PrimaryPhase.GAME_END) {
            super.onBackPressed();
            return;
        }


        if (backButtonThread == null) {
            Toast.makeText(this, "Press back again to quit the game",
                    Toast.LENGTH_LONG).show();
            backButtonThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                        backButtonThread = null;
                    } catch (InterruptedException e) {
                    }

                }
            });
            backButtonThread.start();
        } else {
            super.onBackPressed();
        }
    }


    private void updateToolbar() {

        final PhaseState phase = getGame().getPhase();

        if (phase.isSetupPhase() || phase.isPreGamePhase()) {
            setMainText(PrimaryPhase.getLabel(phase.getPrimaryPhase()), PrimaryPhase.getSubLabel(phase.getPrimaryPhase()));
        } else if (phase.isMine()) {
            setMainText(PrimaryPhase.getLabel(phase.getPrimaryPhase()), PrimaryPhase.getSubLabel(phase.getPrimaryPhase()));

        } else {
            setMainText("Opponent's " + PrimaryPhase.getLabel(phase.getPrimaryPhase()), "");
        }
        nextButtonText.setText("Ready");
        nextButton.setEnabled(true);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setSelected(false);
        nextButton.setBackgroundResource(R.drawable.solid_yellow_button_background);
        if (phase.isMine() || phase.isZombiePhase() || phase.isPreGamePhase()) {

            if (phase.isPreGamePhase()) {
                if (getGame().getMe().isPreGameReady()) {
                    nextButton.setSelected(true);
                } else {
                    nextButton.setEnabled(true);
                }
            } else if (phase.isZombiePhase()) {
                if (getGame().getMe().isZombieReady()) {
                    nextButton.setEnabled(false);
                    nextButton.setSelected(true);
                } else {
                    nextButton.setEnabled(true);
                }
            } else {
                nextButton.setEnabled(true);
            }


            if (phase.isZombiePhase() && !phase.isMine()) {
                nextButton.setBackgroundResource(R.drawable.solid_blue_button_background);
            }
        } else {
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.INVISIBLE);
        }


    }


    private void updateSecondaryText() {
        final PhaseState phase = getGame().getPhase();


        if (phase.isSetupPhase()) {
            setInfoTextArray(getResources().getStringArray(R.array.setup));
        } else if (phase.isPreGamePhase()) {
            setInfoTextArray(getResources().getStringArray(R.array.setup));
        } else if (phase.isZombiePhase()) {
            setInfoTextArray(getResources().getStringArray(R.array.environment));
        } else if (phase.isMine()) {

            switch (phase.getPrimaryPhase()) {
                case PrimaryPhase.ACTION:
                    setInfoTextArray(getResources().getStringArray(R.array.your_action));
                    break;
                case PrimaryPhase.MOVE:
                    setInfoTextArray(getResources().getStringArray(R.array.your_sync));
                    break;
                case PrimaryPhase.ASSIGN_ACTIONS:
                    setInfoTextArray(getResources().getStringArray(R.array.your_command));
                    break;

            }

        } else {
            setInfoTextArray(getResources().getStringArray(R.array.enemy_turn));

        }
    }

    private void setInfoTextArray(final String[] array) {


        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < array.length; ++i) {
            sb.append(array[i]);
            sb.append("\n");
        }
        phaseInfoText.setText(sb.toString());

    }


    private void setMainText(final String title, final String subtitle) {

        phaseSubTitleText.setText(subtitle);
        phaseTitleText.setText(title);
    }


    private void addOpponentCharacterFragments() {


        if (enemyCharactersAdded) {
            return;
        }


        List<CharacterState> characters = getGame().getEnemyCharacters();

        for (CharacterState characterState : characters) {

            if (characterState.getSkills().contains(SKILL_ID_INFILTRATE) && getGame().getPhase().getPrimaryPhase() == PrimaryPhase.PRE_GAME) {
                continue;
            }

            if (characterState.isHidden()) {
                continue;
            }


            characterCardsEnemy.add(EnemyCharacterFragment.newInstance(characterState));
        }


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int index = 0;
        for (CharacterCardFragment fragment : characterCardsEnemy) {

            transaction.add(R.id.cards_enemy, fragment, "" + index);

            ++index;
        }
        transaction.commit();

        enemyCharactersAdded = true;

        enemyCardOverlay.animateToSide();
    }

    private void addPlayerCharacterFragments() {
        if (friendlyCharactersAdded) {
            return;
        }
        friendlyCharactersAdded = true;

        friendlyCardOverlay.removeAllViews();


        List<CharacterState> characters = getGame().getMe().getTeam()
                .listAllTypesCharacters();


        for (CharacterState characterState : characters) {

            View view;
            if (characterState instanceof CharacterStateSquad) {
                view = new FriendlySquadView(this);
                ((FriendlySquadView) view).setCharacter((CharacterStateSquad) characterState);
            } else {
                view = new FriendlyCharacterView(this);
                ((FriendlyCharacterView) view).setCharacter((CharacterStateHero) characterState);
            }


            characterCardsFriendly.add((CharacterCard) view);
            friendlyCardOverlay.addView(view);
        }
        friendlyCardOverlay.setupChildrenOrderTracking(characterCardsFriendly);


        friendlyCardOverlay.animateToSide();
    }

    @Subscribe
    public void onCardViewSelectedEvent(CardViewSelectedEvent event) {

        CharacterCard view = event.getSelectedCharacterCardFragment();
        if (view != null) {

            this.friendlyCardOverlay.bringFront(event.getSelectedCharacterCardFragment());
            this.friendlyCardOverlay.animateToLocations();
            if (view.getCharacter() != null) {
                BusProvider.getInstance().post(new CharacterSelectedEvent(view.getCharacter()));

            }

        } else {
            this.friendlyCardOverlay.resetFrontCard();
            this.friendlyCardOverlay.animateToLocations();
        }
    }


    @Subscribe
    public void onEnemyCardSelectedEvent(EnemyCardSelectedEvent event) {
        if (event.getSelectedCharacterCardFragment() != null) {

            this.enemyCardOverlay.bringFront(event.getSelectedCharacterCardFragment());
            this.enemyCardOverlay.animateToHorizontalSpread();

        }
    }


    @Subscribe
    public void onStartThrowUIEvent(StartThrowUIEvent event) {
        this.throwUIContainer.setVisibility(View.VISIBLE);

        ActionsThrowUIFragment fragment = ActionsThrowUIFragment.newInstance(event.getWargear(), event.getCharacter(), event.getAction());
        fragment.addCallback(event.getCallback());

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.throw_ui_container, fragment);
        transaction.commit();

    }

    @Subscribe
    public void onEndThrowUIEvent(EndThrowUIEvent event) {
        this.throwUIContainer.setVisibility(View.GONE);
    }


    private boolean isUnperformedActions() {


        for (CharacterState characterState : getGame().getMe().getTeam().getHeroes()) {

            List<Integer> actions = characterState.getCurrentActions();

            for (final Integer action : actions) {
                if (characterState.hasActionPerformed(action)) {
                    continue;
                }

                return true;
            }
        }

        for (CharacterState characterState : getGame().getMe().getTeam().getSquads()) {

            List<Integer> actions = characterState.getCurrentActions();

            for (final Integer action : actions) {
                if (characterState.hasActionPerformed(action)) {
                    continue;
                }

                return true;
            }
        }


        return false;
    }


    private void advanceGamePhase() {

        PhaseState phase = getGame().getPhase();

        // Deselect characters
        BusProvider.getInstance().post(new CardViewSelectedEvent(null));
        BusProvider.getInstance().post(new CharacterSelectedEvent());

        if (phase.isPreGamePhase()) {
            boolean isPreGameReady = !getGame().getMe().isPreGameReady();

            PlayerReadyPreGameTransition transition = new PlayerReadyPreGameTransition(PlayerAccount.getUniqueIdentifier(), isPreGameReady);
            sendToServer(transition);

            new Handler().postDelayed(new UnlockTask(nextButton), GameConstants.ADVANCE_BUTTON_LOCK_TIME);
        } else if (phase.isZombiePhase()) {
            nextButton.setEnabled(false);

            sendToServer(new ActionPhaseTransition(getGame().getMe().getIdentifier()));

            new Handler().postDelayed(new UnlockTask(nextButton), GameConstants.ADVANCE_BUTTON_LOCK_TIME);
        } else {
            nextButton.setEnabled(false);

            new Handler().postDelayed(new UnlockTask(nextButton), GameConstants.ADVANCE_BUTTON_LOCK_TIME);

            Transition transition = PrimaryPhase.getAdvanceTransition(phase.getPrimaryPhase());
            sendToServer(transition);
        }
    }


    private static class UnlockTask implements Runnable {
        private View view;

        public UnlockTask(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            view.setEnabled(true);
        }
    }


    @Subscribe
    public void onOpponentIsWaitingForYouEvent(OpponentIsWaitingForYouEvent event) {

        final ViewPropertyAnimator animator = nextButton.animate();
        animator.translationX(UIUtils.convertDpToPixel(-50, this)).setInterpolator(new CycleInterpolator(5)).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                nextButton.animate().translationX(0);

                animator.setListener(null);
            }
        });
    }

}


