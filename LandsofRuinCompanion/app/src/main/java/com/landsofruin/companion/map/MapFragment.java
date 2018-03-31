package com.landsofruin.companion.map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BlinkCharactersEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.eventbus.RotateMapEventsEvent;
import com.landsofruin.companion.eventbus.TargetCharacterSelectionStateEndEvent;
import com.landsofruin.companion.eventbus.TargetCharacterSelectionStateStartEvent;
import com.landsofruin.companion.eventbus.TargetCharactersSelectionChangedEvent;
import com.landsofruin.companion.eventbus.ThrowableDragCompleMapEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.transition.CreateThrowableTransition;
import com.landsofruin.companion.state.transition.MoveCharacterOffTableTransition;
import com.landsofruin.companion.state.transition.MoveCharacterTransition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Fragment for displaying a {@link MapState}.
 */
public class MapFragment extends Fragment implements MapViewListener,
        OnDragListener {
    boolean isLarge = false;
    boolean isMedium = false;
    boolean isSmall = false;
    int currentYDelta = 0;
    private BaseGameActivity activity;
    private Bus bus;
    private MapView mapView;
    private MapBadgeLayout mapBadgeLayer;
    private PointState lastAcceptableDrapPoint = null;
    private int previousPhase = -1;
    private ImageView mapBackground;
    private View mapContainer;
    private View mainContainer;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bus = BusProvider.getInstance();
        bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        bus.unregister(this);
    }


    @Subscribe
    public void onCharacterSelected(CharacterSelectedEvent event) {
        CharacterState character = event.getCharacter();

        mapView.setSelectedCharacter(character);
        mapView.invalidate();
    }

    private void refreshMapBadgeInfos() {
        mapBadgeLayer.removeAllViews();
        if (activity.getGame().getPhase().isMine()) {
            if (activity.getGame().getPhase().getPrimaryPhase() == PrimaryPhase.ZOMBIES) {
                refreshBadgesZombiePhase();
            }
//            else {
//                refreshBadgesActionPhase();
//            }

        } else {
            if (activity.getGame().getPhase().getPrimaryPhase() == PrimaryPhase.ZOMBIES) {
                refreshBadgesZombiePhase();
            }
//            else {
//                refreshBadgesActionPhase();
//            }

        }

    }

    private void refreshBadgesActionPhase() {
        LayoutInflater layoutInflater = this.activity.getLayoutInflater();

        GameState game = this.activity.getGame();


        PlayerState player = game.getMe();

        for (CharacterState character : player.getTeam().listAllTypesCharacters()) {
            if (!character.isOnMap()) {
                continue;
            }

            View view = layoutInflater.inflate(
                    R.layout.character_map_badge_action, mapBadgeLayer,
                    false);
            view.setTag(character);

            ViewGroup effectIconContainer = (ViewGroup) view
                    .findViewById(R.id.effect_icon_container);

            drawEffectIcons(character, effectIconContainer);


            ViewGroup assignedActionsContainer = (ViewGroup) view
                    .findViewById(R.id.actions_container);

            int movemementValue = 0;

            if (game.getPhase().isMine() && (game.getPhase().getPrimaryPhase() == PrimaryPhase.ACTION || game.getPhase().getPrimaryPhase() == PrimaryPhase.MOVE)) {
                movemementValue = character.getMovementAllowance(game);
            } else {
                movemementValue = character.getMovementAllowanceForNextTurn(game);
            }
            if (movemementValue > 0) {
                View movementView = getActivity().getLayoutInflater().inflate(
                        R.layout.one_action_in_card_movement, assignedActionsContainer, false);

                final TextView movement = (TextView) movementView
                        .findViewById(R.id.text_view);
                movement.setText("" + movemementValue + "\"");
                movement.setBackgroundResource(R.drawable.card_action_bg);
                assignedActionsContainer.addView(movementView);
            }


            for (final Integer action : character.getCurrentActions()) {

                View oneIconView = getActivity().getLayoutInflater().inflate(
                        R.layout.one_action_in_card, assignedActionsContainer, false);

                final ImageView icon = (ImageView) oneIconView
                        .findViewById(R.id.action_icon);


                icon.setBackgroundResource(R.drawable.card_action_bg);
                icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(action)));
                assignedActionsContainer.addView(oneIconView);
            }

            mapBadgeLayer.addView(view);
        }


    }
//
//    private void refreshBadgesDamagePhase() {
//        LayoutInflater layoutInflater = this.activity.getLayoutInflater();
//
//        for (PlayerState player : this.activity.getGame().getPlayers()) {
//
//            boolean isMe = player.isMe();
//            if (!isMe) {
//                continue;
//            }
//
//            for (CharacterState character : player.getTeam().listAllTypesCharacters()) {
//                if (!character.isOnMap()) {
//                    continue;
//                }
//
//                View view = layoutInflater.inflate(
//                        R.layout.character_map_badge_action, mapBadgeLayer,
//                        false);
//                view.setTag(character);
//
//                ViewGroup effectIconContainer = (ViewGroup) view
//                        .findViewById(R.id.effect_icon_container);
//
//                boolean hasEffects = drawEffectIcons(character,
//                        effectIconContainer);
//
//                ((TextView) view.findViewById(R.id.action_points))
//                        .setVisibility(View.GONE);
//
//                if (hasEffects) {
//
//                    mapBadgeLayer.addView(view);
//                }
//            }
//
//        }
//
//    }

//    private void refreshBadgesAssignActionPhase() {
//        LayoutInflater layoutInflater = this.activity.getLayoutInflater();
//
//        for (PlayerState player : this.activity.getGame().getPlayers()) {
//
//            boolean isMe = player.isMe();
//            if (!isMe) {
//                continue;
//            }
//
//            for (CharacterState character : player.getTeam().listAllTypesCharacters()) {
//                if (!character.isOnMap()) {
//                    continue;
//                }
//
//                View view = layoutInflater.inflate(
//                        R.layout.character_map_badge_action, mapBadgeLayer,
//                        false);
//                view.setTag(character);
//
//                ViewGroup effectIconContainer = (ViewGroup) view
//                        .findViewById(R.id.effect_icon_container);
//
//                drawEffectIcons(character, effectIconContainer);
//
//                int remainingPoints = character
//                        .getRemainingActionPoints(activity.getGame());
//                ((TextView) view.findViewById(R.id.action_points))
//                        .setText("APs left: " + remainingPoints);
//
//                mapBadgeLayer.addView(view);
//            }
//
//        }
//
//    }

    private boolean drawEffectIcons(CharacterState character,
                                    ViewGroup effectIconContainer) {

        boolean ret = false;
        List<CharacterEffect> effects = character.getCharacterEffects();
        effectIconContainer.removeAllViews();

        for (final CharacterEffect characterEffect : effects) {

            View oneIconView = getActivity().getLayoutInflater().inflate(
                    R.layout.one_effect_badge, effectIconContainer, false);

            final ImageView icon = (ImageView) oneIconView
                    .findViewById(R.id.effect_icon);
            icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(characterEffect.getIcon()));
            effectIconContainer.addView(oneIconView);
            ret = true;
        }
        return ret;
    }

    private void refreshBadgesZombiePhase() {

        LayoutInflater layoutInflater = this.activity.getLayoutInflater();

        for (PlayerState player : this.activity.getGame().getPlayers()) {
            for (CharacterState character : player.getTeam().listAllTypesCharacters()) {
                if (!character.isOnMap()) {
                    continue;
                }

                int attacking = character.getAttackingZombies();
                int detected = character.getAttackingZombiesDetected();

                View view = layoutInflater.inflate(
                        R.layout.character_map_badge_zombie, mapBadgeLayer,
                        false);
                view.setTag(character);

                if (attacking <= 0 && detected <= 0) {
                    view.setVisibility(View.GONE);
                }

                ((TextView) view.findViewById(R.id.attacking))
                        .setText("Attacking: " + attacking);
                ((TextView) view.findViewById(R.id.detected))
                        .setText("Detected:  " + detected);

                mapBadgeLayer.addView(view);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);


        if (activity.getGame() == null) {
            activity.finish();
            return view;
        }

        mainContainer = view.findViewById(R.id.main_container);
        mapBackground = (ImageView) view.findViewById(R.id.map_background);

        mapContainer = view.findViewById(R.id.map_container);


        mapView = (MapView) view.findViewById(R.id.map);
        mapView.setGame(activity.getGame());
        mapView.setMap(activity.getGame().getMap());
        mapView.setListener(this);
        mapView.setOnTouchListener(new MapGestureListener(mapView, activity
                .getGame()));
        mapView.setOnDragListener(this);


        mapBadgeLayer = (MapBadgeLayout) view
                .findViewById(R.id.map_badge_layer);
        mapBadgeLayer.setMap(mapView);

        mapView.setViewChangedListener(new MapView.ViewChangedListener() {

            @Override
            public void onViewChanged() {
                mapBadgeLayer.requestLayout();
            }
        });

        GameState game = ((GameActivity) getActivity()).getGame();

        if (game.getMap().getBackgroundImageURL() != null) {
            Picasso.with(getActivity()).load(game.getMap().getBackgroundImageURL()).into(mapBackground);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshMapBadgeInfos();
            }
        }, 1000);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) {
            return;
        }
        if (((BaseGameActivity) getActivity()).getGame() == null) {
            getActivity().finish();
            return;
        }

        PlayerState.StartingPosition startPosition = ((BaseGameActivity) getActivity()).getGame().getMe().getStartingPosition();
        if (startPosition == PlayerState.StartingPosition.NORTH || startPosition == PlayerState.StartingPosition.EAST) {
            if (!this.mapView.isFlipped()) {
                flip();
            }
        }


    }


    public void flip() {
        if (mainContainer.getRotation() != 0) {
            mainContainer.animate().rotation(0);
        } else {
            mainContainer.animate().rotation(180);
        }

        this.mapView.flip();

        refreshMapBadgeInfos();
    }

    @Subscribe
    public void onTargetSelectionModeStarted(
            TargetCharacterSelectionStateStartEvent event) {
        mapView.setTargetSelectionMode(true);

    }

    @Subscribe
    public void onTargetSelectionModeEnd(
            TargetCharacterSelectionStateEndEvent event) {
        mapView.setTargetSelectionMode(false);

    }

    @Subscribe
    public void onTargetSelectionChanged(
            TargetCharactersSelectionChangedEvent event) {
        mapView.setSelectedTargets(event.getCharacters());
    }

    @Subscribe
    public void onRotateMapEventsEvent(
            RotateMapEventsEvent event) {
        flip();
    }

    @Subscribe
    public void onBlinkCharactersEvent(
            BlinkCharactersEvent event) {
        mapView.blinkCharacterOnMap(event.getCharacter());
    }

    @Override
    public void onCharacterMoved(CharacterState character) {
        GameState gameState = ((GameActivity) this.getActivity()).getGame();

        if (character.isHidden() && gameState.getPhase().getPrimaryPhase() != PrimaryPhase.PRE_GAME) {
            return;
        }

        if (((GameActivity) this.getActivity()).getGame().getPhase().isMine() || gameState.getPhase().getPrimaryPhase() == PrimaryPhase.PRE_GAME) {
            MoveCharacterTransition transition = new MoveCharacterTransition(
                    character.getIdentifier(), character.getRegions(),
                    character.getCenterPoint());

            activity.sendToServer(transition);
        }
    }

    @Override
    public void onCharacterMovedOffTable(CharacterState character) {
        GameState gameState = ((GameActivity) this.getActivity()).getGame();

        if (character.isHidden() && gameState.getPhase().getPrimaryPhase() != PrimaryPhase.PRE_GAME) {
            return;
        }

        MoveCharacterOffTableTransition transition = new MoveCharacterOffTableTransition(
                character.getIdentifier());

        activity.sendToServer(transition);
    }

    @Override
    public void onThrowableCreated(int wargearId, PointState point,
                                   String character, int actionId) {
        CreateThrowableTransition transition = new CreateThrowableTransition(
                wargearId, point, character, actionId);

        BusProvider.getInstance().post(
                new ThrowableDragCompleMapEvent(transition));
    }

    @Subscribe
    public void onGameStateChangedEvent(GameStateChangedEvent event) {

        mapView.invalidate();
        refreshMapBadgeInfos();

        GameState game = ((GameActivity) getActivity()).getGame();


        if (previousPhase == -1
                || previousPhase != game.getPhase().getPrimaryPhase()) {
            previousPhase = game.getPhase().getPrimaryPhase();

            mapToLarge();
        }


    }

    private void mapToLarge() {

        if (isLarge) {
            return;
        }
        isSmall = false;
        isLarge = true;
        isMedium = false;

        mapContainer.setPivotY(mapBackground.getHeight());
        mapContainer.setPivotX(mapBackground.getWidth() / 2);

        int targetDelta = 0;


        mapBackground.animate().rotationX(0f);
        mapContainer.animate().rotationX(0f).yBy(targetDelta - currentYDelta);
        currentYDelta = targetDelta;

        mapBackground.setPivotX(mapBackground.getWidth() / 2);
        mapBackground.setPivotY(mapBackground.getHeight() / 2);


    }


    @Override
    public void onResume() {
        super.onResume();
//        onGameStateChangedEvent(null);
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {


        if (event.getClipDescription() != null && event.getClipDescription().getLabel() != null && !event.getClipDescription().getLabel()
                .toString().startsWith(ThrowableState.THROWABLE_DRAG_IDENTIFIER_PREFIX)) {

            if (activity.getGame().getPhase().getPrimaryPhase() != PrimaryPhase.PRE_GAME && activity.getGame().getPhase().getPrimaryPhase() != PrimaryPhase.MOVE) {
                return true;
            }


            if (activity.getGame().getPhase().getPrimaryPhase() == PrimaryPhase.MOVE && !activity.getGame().getPhase().isMine()) {
                return true;
            }

        }


        MapState map = activity.getGame().getMap();

        int action = event.getAction();

        if (action == DragEvent.ACTION_DRAG_EXITED
                || action == DragEvent.ACTION_DRAG_ENDED) {
            lastAcceptableDrapPoint = null;
            for (RegionState region : mapView.getMap().getRegionsWithSpecials()) {
                region.setSelected(false);
            }
            mapView.invalidate();
            return false;
        } else if (action == DragEvent.ACTION_DRAG_LOCATION) {
            for (RegionState region : mapView.getMap().getRegionsWithSpecials()) {
                region.setSelected(false);
            }

            PointState point = mapView.eventToPoint(event);
            String identifier = event.getClipDescription().getLabel()
                    .toString();
            if (!identifier
                    .startsWith(ThrowableState.THROWABLE_DRAG_IDENTIFIER_PREFIX)) {

                boolean isStacked = false;
                CharacterState movingCharacter = activity.getGame()
                        .findCharacterByIdentifier(identifier);
                for (PlayerState player : activity.getGame().getPlayers()) {
                    for (CharacterState character : player.getTeam()
                            .listAllTypesCharacters()) {
                        if (!character.isOnMap()
                                || character.isSame(movingCharacter)) {
                            continue;
                        }
                        if (mapView.getBoundingBox(character).contains(point.x,
                                point.y)) {
                            isStacked = true;
                        }
                    }
                }
                if (!isStacked) {
                    lastAcceptableDrapPoint = point;
                }
            } else {
                lastAcceptableDrapPoint = point;
            }


            List<RegionState> regions = map.findRegionsByPoint(point);
            if (regions.size() > 0) {
                Log.d("drag", "point " + point);
                for (RegionState regionState : regions) {
                    regionState.setSelected(true);
                }
            }
            mapView.invalidate();
            return false;
        } else if (action == DragEvent.ACTION_DROP) {
            String identifier = event.getClipDescription().getLabel()
                    .toString();

            PointState point = null;
            if (lastAcceptableDrapPoint != null) {
                point = lastAcceptableDrapPoint;
            } else {
                mapView.eventToPoint(event);
            }

            lastAcceptableDrapPoint = null;

            List<RegionState> regions = map.findRegionsByPoint(point);
            if (regions.size() == 0) {
                Toast.makeText(getActivity(), "Outside regions",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            for (RegionState region : mapView.getMap().getRegionsWithSpecials()) {
                region.setSelected(false);
            }

            List<String> regionIdentifiers = new ArrayList<String>();
            for (RegionState region : regions) {
                if (!region.isSpecialDragOutSection()) {
                    regionIdentifiers.add(region.getIdentifier());
                }

            }

            if (regionIdentifiers.size() <= 0) {
                return false;
            }

            if (identifier
                    .startsWith(ThrowableState.THROWABLE_DRAG_IDENTIFIER_PREFIX)) {

                String infoString = identifier
                        .substring(ThrowableState.THROWABLE_DRAG_IDENTIFIER_PREFIX
                                .length());

                StringTokenizer st = new StringTokenizer(infoString, ",");
                int index = 0;
                int wargearId = -1;
                int actionId = -1;
                String characterId = null;
                while (st.hasMoreTokens()) {

                    if (index == 0) {
                        wargearId = Integer.parseInt(st.nextToken());
                    } else if (index == 2) {
                        characterId = st.nextToken();
                    } else if (index == 1) {
                        actionId = Integer.parseInt(st.nextToken());
                    }

                    ++index;
                }

                onThrowableCreated(wargearId, point, characterId, actionId);


            } else {
                CharacterState character = activity.getGame()
                        .findCharacterByIdentifier(identifier);

                character.updatePosition(point, regionIdentifiers);

                onCharacterMoved(character);

                mapView.setSelectedCharacter(character);

            }

            mapView.invalidate();
        }

        return true;
    }

}
