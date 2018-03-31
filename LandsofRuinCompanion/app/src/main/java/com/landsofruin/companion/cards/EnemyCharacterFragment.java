package com.landsofruin.companion.cards;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.cards.events.CardSelectedEvent;
import com.landsofruin.companion.cards.events.EnemySingleCardModeSelectedEvent;
import com.landsofruin.companion.cards.events.UpdateCardPositionToHolderEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by juhani on 10/16/13.
 */
public class EnemyCharacterFragment extends CharacterCardFragment {
    private static final String ARGUMENT_PC_ID = "ARGUMENT_PC_ID";

    private BaseGameActivity activity;

    private View rootLayout;

    private boolean thisCardSelected = false;
    private CharacterState character;
    private TextView characterNameText;


    private boolean dragInProgress = false;

    private int defaultFlags;
    private ImageView characterImage;
    private ViewGroup assignedActionsContainer;
    private View overlay;
    private ViewGroup effectIconContainer;
    private ViewGroup zombieAttackCountContainer;
    private ViewGroup weaponsContainer;

    public static EnemyCharacterFragment newInstance(CharacterState pc) {
        EnemyCharacterFragment ret = new EnemyCharacterFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_PC_ID, pc.getIdentifier());

        ret.setArguments(args);

        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() instanceof GameActivity && getParentFragment() == null) {
            setRetainInstance(true);
        }


        View view = inflater.inflate(R.layout.enemy_character_card, container, false);


        characterImage = (ImageView) view.findViewById(R.id.character_image);

        rootLayout = view.findViewById(R.id.topLevelLayout);


        zombieAttackCountContainer = ((ViewGroup) view.findViewById(R.id.zombie_hit_count_container));

        ((TextView) view.findViewById(R.id.character_name)).setText("front " + getTag());


        characterNameText = ((TextView) view.findViewById(R.id.character_name));

        assignedActionsContainer = (ViewGroup) view
                .findViewById(R.id.assigned_actions);
        overlay = view.findViewById(R.id.click_stealer);

        weaponsContainer = (ViewGroup) view.findViewById(R.id.weapons_container);

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dragInProgress) {
                    BusProvider.getInstance().post(new CardSelectedEvent(EnemyCharacterFragment.this));
                }
            }
        });

        effectIconContainer = (ViewGroup) view
                .findViewById(R.id.effect_icon_container);


        if (getArguments() != null) {

            this.character = this.activity.getGame().findCharacterByIdentifier(getArguments().getString(ARGUMENT_PC_ID));


            Picasso.with(this.getActivity())
                    .load(character.getCardPictureUri())
                    .into(characterImage);

        } else {
            Log.e("pc control fragment",
                    "arguments missing. fragment doesn't know which PlayerCharacter this fragment is for");
        }


        defaultFlags = characterNameText.getPaintFlags();

        updateUI();


        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        this.activity = (BaseGameActivity) activity;
        updateUI();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Subscribe
    public void onCardSelectedEvent(CardSelectedEvent event) {
        if (event.getSelectedCharacterCardFragment() != this) {
            thisCardSelected = false;
            overlay.setVisibility(View.VISIBLE);

        } else {
            thisCardSelected = true;
            overlay.setVisibility(View.GONE);

        }
        highlightIfNeeded();
    }

    @Subscribe
    public void onSingleCardModeSelectedEvent(EnemySingleCardModeSelectedEvent event) {

        if (!thisCardSelected) {

        }
    }


    private void updateUI() {
        if (this.activity == null || this.character == null) {
            return;
        }

        GameState game = ((GameActivity) getActivity()).getGame();
        if (game == null) {
            getActivity().finish();
            return;
        }


        characterNameText.setText(this.character.getName());


        if (character.isDead() || character.isUnconsious() || character.isPinned()) {
            characterNameText.setPaintFlags(characterNameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            characterNameText.setPaintFlags(defaultFlags);
        }


        assignedActionsContainer.removeAllViews();

        if (showActions()) {
            assignedActionsContainer.setVisibility(View.VISIBLE);
            int movemementValue = 0;

            if (game.getPhase().isMine() && (game.getPhase().getPrimaryPhase() == PrimaryPhase.ACTION || game.getPhase().getPrimaryPhase() == PrimaryPhase.MOVE)) {
                movemementValue = this.character.getMovementAllowance(game);
            } else {
                movemementValue = character.getMovementAllowanceForNextTurn(game);
            }

            if (movemementValue > 0) {
                View movementView = getActivity().getLayoutInflater().inflate(
                        R.layout.one_action_in_card_movement, assignedActionsContainer, false);

                final TextView movement = (TextView) movementView
                        .findViewById(R.id.text_view);
                movement.setText("" + movemementValue + "\"");

                assignedActionsContainer.addView(movementView);
            }

            for (final Integer action : this.character.getCurrentActions()) {

                View oneIconView = getActivity().getLayoutInflater().inflate(
                        R.layout.one_action_in_card, assignedActionsContainer, false);

                final ImageView icon = (ImageView) oneIconView
                        .findViewById(R.id.action_icon);


                icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(action)));
                assignedActionsContainer.addView(oneIconView);
            }

        } else {
            assignedActionsContainer.setVisibility(View.GONE);
        }


        effectIconContainer.removeAllViews();
        boolean downAdded = false;
        for (final CharacterEffect characterEffect : this.character.getCharacterEffects()) {


            if (characterEffect.getId() == CharacterEffect.ID_PINNED || characterEffect.getId() == CharacterEffect.ID_UNCONSCIOUS || characterEffect.getId() == CharacterEffect.ID_DEAD) {
                if (downAdded) {
                    continue;
                }
                downAdded = true;

                View oneIconView = getActivity().getLayoutInflater().inflate(
                        R.layout.one_effect, effectIconContainer, false);


                final ImageView icon = (ImageView) oneIconView
                        .findViewById(R.id.effect_icon);
                icon.setImageResource(R.drawable.effect_icon_down);

                effectIconContainer.addView(oneIconView);

            }


        }


        if (game.getPhase().isZombiePhase()) {

            zombieAttackCountContainer.removeAllViews();


            for (final ThrowableState throwable : activity.getGame().getWorld()
                    .getThrowableStates()) {

                int zombieHitCount = character.getZombieHitCountForWargear(LookupHelper.getInstance().getWargearFor(throwable).getId());
                if (zombieHitCount > 0) {
                    TextView oneHitView = (TextView) getActivity().getLayoutInflater().inflate(
                            R.layout.one_enemy_zombie_hit, zombieAttackCountContainer, false);

                    oneHitView.setText("" + zombieHitCount);
                    oneHitView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.demolisions, 0, 0, 0);

                    zombieAttackCountContainer.addView(oneHitView);
                }


            }


            int zombieHitCount = character.getZombieHitCountForWargear(Zombie.ZOMBIE_WEAPON_ID);
            if (zombieHitCount > 0) {
                TextView oneHitView = (TextView) getActivity().getLayoutInflater().inflate(
                        R.layout.one_enemy_zombie_hit, zombieAttackCountContainer, false);

                oneHitView.setText("" + zombieHitCount);

                oneHitView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zombie_attack, 0, 0, 0);

                zombieAttackCountContainer.addView(oneHitView);

            }


            zombieHitCount = character.getZombieHitCountForWargear(Zombie.ZOMBIE_FAST_WEAPON_ID);
            if (zombieHitCount > 0) {
                TextView oneHitView = (TextView) getActivity().getLayoutInflater().inflate(
                        R.layout.one_enemy_zombie_hit, zombieAttackCountContainer, false);

                oneHitView.setText("" + zombieHitCount);

                oneHitView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zombie_attack_fast, 0, 0, 0);

                zombieAttackCountContainer.addView(oneHitView);

            }

            zombieHitCount = character.getZombieHitCountForWargear(Zombie.ZOMBIE_FAT_WEAPON_ID);
            if (zombieHitCount > 0) {
                TextView oneHitView = (TextView) getActivity().getLayoutInflater().inflate(
                        R.layout.one_enemy_zombie_hit, zombieAttackCountContainer, false);

                oneHitView.setText("" + zombieHitCount);

                oneHitView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zombie_attack_fat, 0, 0, 0);

                zombieAttackCountContainer.addView(oneHitView);

            }

            zombieAttackCountContainer.setVisibility(View.VISIBLE);
        } else {
            zombieAttackCountContainer.setVisibility(View.GONE);
        }


        weaponsContainer.removeAllViews();


        List<Integer> wg = this.character.getWargear();
        ArrayList<Integer> handledIds = new ArrayList<>();

        for (Integer wargear : wg) {
            if (!(LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearOffensive)) {
                continue;
            }

            if (handledIds.contains(LookupHelper.getInstance().getWargearFor(wargear).getWeaponId())) {
                continue;
            }

            final WargearOffensive offensiveWargear = (WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear);
            handledIds.add(LookupHelper.getInstance().getWargearFor(wargear).getWeaponId());


            View view = getActivity().getLayoutInflater().inflate(
                    R.layout.weapon_info, assignedActionsContainer, false);

            ImageView oneWeaponView = (ImageView) view.findViewById(R.id.highlight_icon);

            view.findViewById(R.id.skill_value).setVisibility(View.GONE);
            Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(offensiveWargear.getCategory());

            if (resource != null) {
                oneWeaponView.setImageResource(resource);


                weaponsContainer.addView(view);
            }
            view.findViewById(R.id.actions_left).setVisibility(View.GONE);


            int mostBulletsNeeded = Integer.MAX_VALUE;

            LinkedList<Wargear> allModes = WargearManager.getInstance().getWargearByWeaponID(offensiveWargear.getWeaponId());
            for (Wargear wgForModes : allModes) {
                mostBulletsNeeded = Math.min(mostBulletsNeeded, ((WargearOffensive) wgForModes).getBulletsPerAction());
            }


            view.setBackgroundResource(R.drawable.enemy_card_weapon);


        }
    }


    private void highlightIfNeeded() {

    }


    private boolean showActions() {
        GameState game = ((GameActivity) getActivity()).getGame();

        if (game.getPhase().getCurrentPlayer().equals(game.findPlayerByCharacterIdentifier(this.character.getIdentifier()).getIdentifier())) {

            return game.getPhase().getPrimaryPhase() == PrimaryPhase.ACTION || game.getPhase().getPrimaryPhase() == PrimaryPhase.MOVE;
        }
        return false;
    }


    @Subscribe
    public void onGameStateChangedEvent(GameStateChangedEvent event) {

        updateUI();


    }

    @Override
    public CharacterState getCharacter() {
        return this.character;
    }


    @Subscribe
    public void onUpdateCardPositionToHolderEvent(UpdateCardPositionToHolderEvent event) {
        updatePosition();
    }

    private void updatePosition() {

        if (isEndGamePrepareVisible()) {
            return;
        }

        int[] coordinates = new int[2];
        rootLayout.getLocationOnScreen(coordinates);
        CardLocationsHolder.setLocationFor(this.character.getIdentifier(), coordinates);
    }
}
