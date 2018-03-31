package com.landsofruin.companion.map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener class to listen for map gestures. Helper class to wrap a
 * {@link ScaleGestureDetector} and {@link GestureDetector} into a single class.
 */
public class MapGestureListener implements OnTouchListener {
    private static final float MINIMUM_SCALE_FACTOR = 0.05f;
    private final Context context;
    private OnScaleGestureListener scaleGestureListener = new SimpleOnScaleGestureListener() {
        private float initialScale;
        private PointState scaleCenter;

        public boolean onScaleBegin(ScaleGestureDetector detector) {
//            initialScale = mapView.getZoomScale();
//
//            scaleCenter = new PointState(
//                    (mapView.getScrollX() + detector.getFocusX())
//                            / mapView.getZoomScale(),
//                    (mapView.getScrollY() + detector.getFocusY())
//                            / mapView.getZoomScale());

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
//            float scaleFactor = detector.getScaleFactor();
//
//            if (Math.abs(1.0f - scaleFactor) >= MINIMUM_SCALE_FACTOR) {
//                float newScale = scaleFactor * initialScale;
//
//                mapView.setZoomScale(newScale);
//                mapView.center(scaleCenter);
//
//                initialScale = newScale;
//
//                return true;
//            }

            return false;
        }
    };
    private MapView mapView;
    private GameState gameState;
    private List<RegionState> selectedRegions;
    private CharacterState movingCharacter;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean isBeingMovedOffTable = false;
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent event) {
            PointState point = mapView.eventToPoint(event);
//
//            mapView.zoomSmart();
//            mapView.center(point);

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {

            //
            //
            // if(game.getPhase().isMine() && game.getPhase().getPrimaryPhase()
            // == PrimaryPhase.ACTION){
            //
            // }
            //
            // if (movingCharacter != null) {
            //
            // if (isBeingMovedOffTable) {
            // mapView.getListener().onCharacterMovedOffTable(movingCharacter);
            // } else {
            // mapView.getListener().onCharacterMoved(movingCharacter);
            // }
            //
            // movingCharacter = null;
            // selectedRegions = new ArrayList<RegionState>();
            //
            // for (RegionState region :
            // mapView.getMap().getRegionsWithSpecials()) {
            // region.setSelected(false);
            // }
            //
            // mapView.invalidate();
            // }

            mapView.onMapTap(event);

            return super.onSingleTapUp(event);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
//            mapView.getScroller().fling(mapView.getScrollX(),
//                    mapView.getScrollY(), -1 * (int) velocityX,
//                    -1 * (int) velocityY, mapView.getMinOffsetX(),
//                    mapView.getMaxOffsetX(), mapView.getMinOffsetY(),
//                    mapView.getMaxOffsetY());
//
//            mapView.invalidate();

            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
//            mapView.getScroller().startScroll(mapView.getScrollX(),
//                    mapView.getScrollY(), (int) distanceX, (int) distanceY, 0);
//
//            mapView.invalidate();

            return true;
        }
    };

    public MapGestureListener(MapView mapView, GameState gameState) {
        this.mapView = mapView;
        this.gameState = gameState;

        context = mapView.getContext();

        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.scaleGestureDetector = new ScaleGestureDetector(context,
                scaleGestureListener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!handleMarkerMove(event)) {
            scaleGestureDetector.onTouchEvent(event);

            if (!scaleGestureDetector.isInProgress()) {
                gestureDetector.onTouchEvent(event);
            }
        }

        return true;
    }

    private boolean handleMarkerMove(MotionEvent event) {

        PhaseState phase = gameState.getPhase();

        if (phase.getPrimaryPhase() != PrimaryPhase.PRE_GAME && phase.getPrimaryPhase() != PrimaryPhase.MOVE) {
            movingCharacter = null;
            return false;

        }

        if (phase.getPrimaryPhase() == PrimaryPhase.MOVE && !phase.isMine()) {
            movingCharacter = null;
            return false;
        }


        if (event.getPointerCount() > 1) {
            movingCharacter = null;
            return false;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return onMarkerMoveActionDown(event);

            case MotionEvent.ACTION_UP:
                return onMarkerMoveActionUp(event);

            case MotionEvent.ACTION_MOVE:
                return onMarkerMoveActionMove(event);
        }

        return false;
    }

    private boolean onMarkerMoveActionUp(MotionEvent event) {
        if (movingCharacter != null) {

            if (isBeingMovedOffTable) {
                mapView.getListener().onCharacterMovedOffTable(movingCharacter);
            } else {
                if (mapView.getBoundingBox(movingCharacter) == null) {
                    return true;
                }
                mapView.getListener().onCharacterMoved(movingCharacter);
            }

            movingCharacter = null;
            selectedRegions = new ArrayList<>();

            for (RegionState region : mapView.getMap().getRegionsWithSpecials()) {
                region.setSelected(false);
            }

            mapView.invalidate();
        }

        return false;
    }

    private boolean onMarkerMoveActionDown(MotionEvent event) {

        if (!mapView.hasSelectedCharacter()) {
            return false;
        }

        CharacterState touchedCharacter = mapView.getSelectedCharacter();
        if (touchedCharacter == null) {
            return false;
        }


        if (touchedCharacter.isHidden() && gameState.getPhase().getPrimaryPhase() != PrimaryPhase.PRE_GAME) {
            Toast.makeText(context, "Hidden characters can't be moved", Toast.LENGTH_SHORT).show();
            return false;
        }


        movingCharacter = touchedCharacter;

        PhaseState phase = gameState.getPhase();
        if (phase.getPrimaryPhase() != PrimaryPhase.PRE_GAME && phase.getPrimaryPhase() != PrimaryPhase.MOVE) {
            if (movingCharacter.isHidden()) {
                movingCharacter = null;
                return false;
            }
        }


        if (mapView.getBoundingBox(movingCharacter) == null) {
            onMarkerMoveActionMove(event);
        }

        return true;
    }

    private boolean onMarkerMoveActionMove(MotionEvent event) {

        PhaseState phase = gameState.getPhase();
        if (phase.getPrimaryPhase() != PrimaryPhase.PRE_GAME && phase.getPrimaryPhase() != PrimaryPhase.MOVE) {
            return true;
        }

        if (movingCharacter != null) {
            MapState map = mapView.getMap();
            PointState point = mapView.eventToPoint(event);

            if (!map.contains(point)) {
                return true;
            }

            selectedRegions = map.findRegionsByPoint(point);

            if (selectedRegions.size() == 0) {
                return true;
            }

            for (PlayerState player : gameState.getPlayers()) {
                for (CharacterState character : player.getTeam()
                        .listAllTypesCharacters()) {
                    if ((!player.isMe() && character.isHidden() && !character
                            .isDetectedByPlayer(gameState.getMe()
                                    .getIdentifier()))
                            || !character.isOnMap()
                            || character.isSame(movingCharacter)) {
                        continue;
                    }
                    if (mapView.getBoundingBox(character).contains(point.x, point.y)) {
                        return true;
                    }
                }
            }

            isBeingMovedOffTable = false;
            List<String> regionIdentifiers = new ArrayList<String>();
            for (RegionState region : selectedRegions) {
                regionIdentifiers.add(region.getIdentifier());
                if (region.isSpecialDragOutSection()) {
                    isBeingMovedOffTable = true;
                }

            }

            movingCharacter.updatePosition(point, regionIdentifiers);

            for (RegionState region : map.getRegionsWithSpecials()) {
                region.setSelected(regionIdentifiers.contains(region
                        .getIdentifier()));
            }

            mapView.invalidate();
            return true;
        }

        return false;
    }
}
