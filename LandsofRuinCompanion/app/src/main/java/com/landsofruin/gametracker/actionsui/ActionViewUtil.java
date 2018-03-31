package com.landsofruin.gametracker.actionsui;

import android.view.ViewGroup;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.HideActionSelectorEvent;
import com.landsofruin.companion.eventbus.StartThrowUIEvent;
import com.landsofruin.companion.eventbus.TargetCharacterSelectionStateEndEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.PerformActionTransition;
import com.landsofruin.companion.state.transition.StartAttackTransition;

import java.util.ArrayList;
import java.util.List;

public class ActionViewUtil {

    public static List<ActionViewContainer> createActionView(
            final Action action, final CharacterState character,
            ViewGroup parentView, final BaseGameActivity activity,
            final ActionPerformCloseActionUICallback callback) {

        GameState gameState = activity.getGame();
        ArrayList<ActionViewContainer> ret = new ArrayList<>();

        if (action.getId() == Action.ACTION_ID_SHOOTING || action.getId() == Action.ACTION_ID_AIM_AND_SHOOTING) {

            boolean foundOneUsableWeapon = false;

            List<Integer> wargear = character.getWargear();
            for (final Integer wargear2 : wargear) {

                if (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                        || !"Ranged".equalsIgnoreCase(LookupHelper.getInstance().getWargearFor(wargear2)
                        .getType())) {
                    continue;
                }


                if (character.isWargearSkillRequirementsMet(
                        LookupHelper.getInstance().getWargearFor(wargear2))) {

                    boolean enabled = true;

                    int bulletsPerAction = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getBulletsPerAction();

                    int ammo = character.getAmmoFor(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId());

                    if (ammo < bulletsPerAction) {
                        enabled = false;
                    }


                    if (enabled) {
                        foundOneUsableWeapon = true;
                        break;
                    }


                }
            }

            if (foundOneUsableWeapon) {
                String text = action.getName();


                ActionPerformPressedCallback actionCallback = new ActionPerformPressedCallback() {

                    @Override
                    public void actionPerformPressed(CharacterState friendly,
                                                     CharacterState enemy, Wargear wargear) {

                        StartAttackTransition transition = new StartAttackTransition(character.getIdentifier(), action.getId(), -1);
                        activity.sendToServer(transition);
                        callback.resetActions();
                        BusProvider.getInstance().post(
                                new TargetCharacterSelectionStateEndEvent());
                        callback.actionPerformed();

                    }

                    @Override
                    public void actionCancelled() {
                        callback.actionSkippedUI();
                    }

                    @Override
                    public void resetActions() {
                        callback.resetActions();
                    }
                };

                ret.add(new ActionViewContainer(text, null, action.getId(), actionCallback));
            }

        } else if (action.getId() == Action.ACTION_ID_CC) {

            boolean foundOneUsableWeapon = false;
            List<Integer> wargear = character.getWargear();
            for (final Integer wargear2 : wargear) {

                if (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                        || !"CC".equalsIgnoreCase(LookupHelper.getInstance().getWargearFor(wargear2)
                        .getType())) {
                    continue;
                }
                if (character.isWargearSkillRequirementsMet(
                        LookupHelper.getInstance().getWargearFor(wargear2))) {

                    boolean enabled = true;

                    int bulletsPerAction = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getBulletsPerAction();

                    int ammo = character.getAmmoFor(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId());

                    if (ammo < bulletsPerAction) {
                        enabled = false;
                    }


                    if (enabled) {
                        foundOneUsableWeapon = true;
                        break;
                    }


                }
            }
            String text = action.getName();


            ActionPerformPressedCallback actionCallback = new ActionPerformPressedCallback() {

                @Override
                public void actionPerformPressed(CharacterState friendly,
                                                 CharacterState enemy, Wargear wargear) {

                    StartAttackTransition transition = new StartAttackTransition(character.getIdentifier(), action.getId(), -1);
                    activity.sendToServer(transition);
                    callback.resetActions();
                    BusProvider.getInstance().post(
                            new TargetCharacterSelectionStateEndEvent());
                    callback.actionPerformed();

                }

                @Override
                public void actionCancelled() {
                    callback.actionSkippedUI();
                }

                @Override
                public void resetActions() {
                    callback.resetActions();
                }
            };

            ret.add(new ActionViewContainer(text, null, action.getId(), actionCallback));


        } else if (action.getId() == Action.ACTION_ID_THROW || action.getId() == Action.ACTION_ID_EXECUTE_SLAVE || action.getId() == Action.ACTION_ID_ARTILLERY_STRIKE || action.getId() == Action.ACTION_ID_FIRE_BAZOOKA) {

            List<Integer> wargear = character.getWargear();
            for (final Integer wargear2 : wargear) {

                String actionWord = "Throw ";
                String category = Wargear.THROWABLE_CATEGORY_GRENADE;
                if (action.getId() == Action.ACTION_ID_EXECUTE_SLAVE) {
                    category = Wargear.THROWABLE_CATEGORY_MINION;
                    actionWord = "Execute ";
                } else if (action.getId() == Action.ACTION_ID_ARTILLERY_STRIKE) {
                    category = Wargear.THROWABLE_CATEGORY_ARTILLERY;
                    actionWord = "Call in ";
                } else if (action.getId() == Action.ACTION_ID_FIRE_BAZOOKA) {
                    category = Wargear.THROWABLE_CATEGORY_RPG;
                    actionWord = "Fire ";
                }


                Wargear wargearWargear = LookupHelper.getInstance().getWargearFor(wargear2);

                if (!(wargearWargear instanceof WargearConsumable)
                        || !Wargear.TYPE_THROWABLE.equalsIgnoreCase(LookupHelper.getInstance().getWargearFor(wargear2)
                        .getType()) || !category.equalsIgnoreCase(LookupHelper.getInstance().getWargearFor(wargear2)
                        .getCategory())) {
                    continue;
                }
                if (character.isWargearSkillRequirementsMet(
                        LookupHelper.getInstance().getWargearFor(wargear2))) {

                    String text = actionWord + LookupHelper.getInstance().getWargearFor(wargear2).getName();


// check if minions are still alive
                    boolean actionEnabled = true;
                    if (action.getId() == Action.ACTION_ID_EXECUTE_SLAVE || action.getId() == Action.ACTION_ID_FIRE_BAZOOKA || action.getId() == Action.ACTION_ID_ARTILLERY_STRIKE) {
                        if (wargearWargear instanceof WargearConsumable && ((WargearConsumable) wargearWargear).getRequiresSquad() != null) {

                            String requiredSquad = ((WargearConsumable) wargearWargear).getRequiresSquad();

                            boolean squadFound = false;

                            TeamState team = gameState.findTeamByCharacterIdentifier(character.getIdentifier());
                            for (CharacterStateSquad squad : team.getSquads()) {

                                if (requiredSquad.equals(squad.getBluePrintIdentifier())) {
                                    squadFound = true;
                                    if (squad.getSquadSize() <= 0) {
                                        actionEnabled = false;
                                        break;
                                    }

                                }
                            }

                            if (!squadFound) {
                                actionEnabled = false;
                            }


                        }


                    }


                    ActionPerformPressedCallback actionCallback = new ActionPerformPressedCallback() {

                        @Override
                        public void actionPerformPressed(CharacterState friendly,
                                                         CharacterState enemy, Wargear wargear) {


                            BusProvider.getInstance().post(new HideActionSelectorEvent());
                            BusProvider.getInstance().post(new StartThrowUIEvent(
                                    (WargearConsumable) LookupHelper.getInstance().getWargearFor(wargear2),
                                    character, action, new ActionPerformPressedCallback() {

                                @Override
                                public void actionPerformPressed(
                                        CharacterState friendly, CharacterState enemy,
                                        Wargear wargear) {

                                    activity.sendToServer(new PerformActionTransition(
                                            character, friendly, action.getId(),
                                            wargear));

                                    callback.actionPerformed();

                                }

                                @Override
                                public void actionCancelled() {
                                    callback.actionSkippedUI();
                                }

                                @Override
                                public void resetActions() {
                                    callback.resetActions();
                                }
                            }));

                        }

                        @Override
                        public void actionCancelled() {
                            callback.actionSkippedUI();
                        }

                        @Override
                        public void resetActions() {
                            callback.resetActions();
                        }
                    };


                    ActionViewContainer actionContainer = new ActionViewContainer(text, null, action.getId(), actionCallback);
                    actionContainer.setEnabled(actionEnabled);
                    ret.add(actionContainer);


                }

            }

        } else if (action.getId() == Action.ACTION_ID_FIRST_AID || action.getId() == Action.ACTION_ID_FORCE_FRENZY) {

            String text = action.getName();

            // Create and show the dialog.
            ActionsFriendlyUIDialogFragment newFragment = ActionsFriendlyUIDialogFragment
                    .newInstance(action, character);

            newFragment.addCallback(new ActionPerformPressedCallback() {

                @Override
                public void actionPerformPressed(CharacterState friendly,
                                                 CharacterState enemy, Wargear wargear) {

                    activity.sendToServer(new PerformActionTransition(
                            character, friendly, action.getId(), wargear));

                    callback.actionPerformed();

                }

                @Override
                public void actionCancelled() {
                    callback.resetActions();
                }

                @Override
                public void resetActions() {
                    callback.resetActions();
                }
            });
            ret.add(new ActionViewContainer(text, newFragment, action.getId(), null));

        } else if (action.getId() == Action.ACTION_ID_REVEAL_ENEMY ) {

            String text = action.getName();

            // Create and show the dialog.
            ActionsHiddenEnemyUIDialogFragment newFragment = ActionsHiddenEnemyUIDialogFragment
                    .newInstance(action, character);

            newFragment.addCallback(new ActionPerformPressedCallback() {

                @Override
                public void actionPerformPressed(CharacterState friendly,
                                                 CharacterState enemy, Wargear wargear) {

                    activity.sendToServer(new PerformActionTransition(
                            character, enemy, action.getId()));

                    callback.actionPerformed();

                }

                @Override
                public void actionCancelled() {
                    callback.resetActions();
                }

                @Override
                public void resetActions() {
                    callback.resetActions();
                }
            });
            ret.add(new ActionViewContainer(text, newFragment, action.getId(), null));

        } else {


            String text = action.getName();

            // Create and show the dialog.
            ActionsSelfUIDialogFragment newFragment = ActionsSelfUIDialogFragment
                    .newInstance(action, character);

            newFragment.addCallback(new ActionPerformPressedCallback() {

                @Override
                public void actionPerformPressed(CharacterState friendly,
                                                 CharacterState enemy, Wargear wargear) {

                    activity.sendToServer(new PerformActionTransition(
                            character, friendly, action.getId(), wargear));

                    callback.actionPerformed();

                }

                @Override
                public void actionCancelled() {
                    callback.resetActions();
                }

                @Override
                public void resetActions() {
                    callback.resetActions();
                }
            });

            ret.add(new ActionViewContainer(text, newFragment, action.getId(), null));

        }

        return ret;
    }

    public interface ActionPerformPressedCallback {
        void actionPerformPressed(CharacterState friendly,
                                  CharacterState enemy, Wargear wargear);

        void resetActions();

        void actionCancelled();
    }

    public interface ActionPerformCloseActionUICallback {
        void actionPerformed();

        void resetActions();

        void actionSkippedUI();
    }
}
