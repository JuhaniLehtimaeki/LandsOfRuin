package com.landsofruin.companion.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.ThrowableState;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * The view class that does the actual drawing of a {@link MapState}.
 */
public class MapView extends View {
    private static final int CHARACTER_BOUNDING_BOX_SIZE = 15;

    private static final int ZOMBIE_BG_COLOR = 0XFFF44336;
    private static final int BACKGROUND_COLOR_SELECTED_DEFAULT = 0x665d5d5d;
    private static final int OUTLINE_COLOR_DEFAULT = 0xFF16cefc;
    private static final float SMART_ZOOM_THRESHOLD = 0.2f;
    private static final int SCROLL_ANIMATION_DURATION = 500;
    private static final int SLEEP_TIME_BLINK = 30;
    private Scroller scroller;
    private GameState game;
    private MapState map;
    private MapViewListener listener;
    private float maxZoomScale;
    private float minZoomScale;
    private float initialZoomScale;
    private float smartZoomScale;
    private int minOffsetX;
    private int maxOffsetX;
    private int maxOffsetY;
    private int minOffsetY;
    private float zoomScale;
    private boolean isFlipped;
    private boolean isInTargetSelectionMode;
    private List<CharacterState> selectedTargets;
    private CharacterState selectedCharacter;
    private ViewChangedListener viewChangedListener;
    private HashMap<CharacterState, Integer> characterAnimationStates = new HashMap<>();
    private LinkedList<PointState> movementHistoryPoints;

    private Paint outlinePaint;
    private Paint highlightPaint;
    private Paint zombieHighlightPaint;
    private int backgroundColorSelected = BACKGROUND_COLOR_SELECTED_DEFAULT;
    private HashMap<String, Bitmap> cachedRoleBitmaps = new HashMap<>();
    private LinkedList<PointState> lastTurnMovement;

    public MapView(Context context) {
        super(context);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    public void initialisePaints() {
        if (outlinePaint == null) {
            outlinePaint = new Paint();
            outlinePaint.setStyle(Paint.Style.STROKE);
            outlinePaint.setStrokeWidth(3);
            outlinePaint.setColor(OUTLINE_COLOR_DEFAULT);

            highlightPaint = new Paint();
            highlightPaint.setColor(backgroundColorSelected);
            highlightPaint.setStyle(Paint.Style.FILL);

            zombieHighlightPaint = new Paint();
            zombieHighlightPaint.setColor(ZOMBIE_BG_COLOR);
            zombieHighlightPaint.setStrokeWidth(15);
            zombieHighlightPaint.setStyle(Paint.Style.STROKE);
        }
    }

    public GameState getGame() {
        return game;
    }

    public void setGame(GameState game) {
        this.game = game;
    }

    public MapState getMap() {
        return map;
    }

    public void setMap(MapState map) {
        this.map = map;

        initZoom();
    }

    public MapViewListener getListener() {
        return listener;
    }

    public void setListener(MapViewListener listener) {
        this.listener = listener;
    }

    public boolean hasSelectedCharacter() {
        return selectedCharacter != null;
    }

    public CharacterState getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(CharacterState character) {
        this.selectedCharacter = character;
    }


    public void setMovementHistoryPath(LinkedList<PointState> points) {
        this.movementHistoryPoints = points;
    }

    public void setTargetSelectionMode(boolean isInTargetSelectionMode) {
        this.isInTargetSelectionMode = isInTargetSelectionMode;

        if (!isInTargetSelectionMode) {
            selectedTargets.clear();
            invalidate();
        }
    }

    public void setSelectedTargets(List<CharacterState> targets) {
        selectedTargets = targets;
    }


    public float getZoomScale() {
        return zoomScale;
    }

    private void setZoomScale(float scale) {
        if (scale > maxZoomScale) {
            scale = maxZoomScale;
        } else if (scale < minZoomScale) {
            scale = minZoomScale;
        }

        zoomScale = scale;

        for (RegionState region : map.getRegionsWithSpecials()) {
            MapDrawer.update(region, this);
        }

        calculateOffsets();

        scrollBy(0, 0);

        invalidate();
    }

    private Scroller getScroller() {
        return scroller;
    }

    /**
     * NEVER call this outside map fragment, wanna flip the map? Call the mapfragment flip()
     */
    protected void flip() {
        isFlipped = !isFlipped;

        invalidate();
    }

    public boolean isFlipped() {
        return isFlipped;
    }


    public void onMapTap(MotionEvent event) {
        PointState point = eventToPoint(event);

        if (game.getPhase().isMine()
                && game.getPhase().getPrimaryPhase() == PrimaryPhase.MOVE) {
            if (selectedCharacter != null) {


                MapState map = getMap();

                if (!map.contains(point)) {
                    return;
                }

                List<RegionState> selectedRegions = map
                        .findRegionsByPoint(point);

                if (selectedRegions.size() == 0) {
                    return;
                }

                for (PlayerState player : game.getPlayers()) {
                    for (CharacterState character : player.getTeam()
                            .listAllTypesCharacters()) {
                        if ((!player.isMe() && character.isHidden() && !character
                                .isDetectedByPlayer(game.getMe()
                                        .getIdentifier()))
                                || !character.isOnMap()
                                || character.isSame(selectedCharacter)) {
                            continue;
                        }
                        if (getBoundingBox(character).contains(point.x,
                                point.y)) {
                            return;
                        }
                    }
                }


                boolean isBeingMovedOffTable = false;
                List<String> regionIdentifiers = new ArrayList<>();
                for (RegionState region : selectedRegions) {
                    regionIdentifiers.add(region.getIdentifier());
                    if (region.isSpecialDragOutSection()) {
                        isBeingMovedOffTable = true;
                    }

                }

                selectedCharacter.updatePosition(point, regionIdentifiers);

                if (isBeingMovedOffTable) {
                    getListener().onCharacterMovedOffTable(selectedCharacter);
                } else {
                    getListener().onCharacterMoved(selectedCharacter);
                }
                invalidate();
            }
        }


        CharacterState character = findCharacterByPoint(point);

        if (character != null) {
            TeamState ownTeam = game.getMe().getTeam();
            if (ownTeam.listAllTypesCharacters().contains(character)) {
                selectedCharacter = character;
                BusProvider.getInstance().post(
                        new CharacterSelectedEvent(character));
            }
            //            } else if (isInTargetSelectionMode) {
            //                BusProvider.getInstance().post(
            //                        new TargetCharacterTappedOnMapEvent(character));
            //            }
        }

        invalidate();
    }

    public void center(PointState point) {
        int x = (int) (point.x * zoomScale) - getWidth() / 2;
        int y = (int) (point.y * zoomScale) - getHeight() / 2;

        scrollTo(x, y);
    }


    public PointState eventToPoint(MotionEvent event) {
        return new PointState((getScrollX() + event.getX()) / getZoomScale(),
                (getScrollY() + event.getY()) / getZoomScale());
    }

    public PointState eventToPoint(DragEvent event) {
        return new PointState((getScrollX() + event.getX()) / getZoomScale(),
                (getScrollY() + event.getY()) / getZoomScale());
    }

    public CharacterState findCharacterByEvent(MotionEvent event) {
        PointState point = eventToPoint(event);

        return findCharacterByPoint(point);
    }

    public CharacterState findCharacterByPoint(PointState point) {
        CharacterState character = null;

        for (PlayerState player : game.getPlayers()) {
            for (CharacterState currentCharacter : player.getTeam()
                    .listAllTypesCharacters()) {
                if (!player.isMe()
                        && currentCharacter.isHidden()
                        && !currentCharacter.isDetectedByPlayer(game.getMe()
                        .getIdentifier())) {
                    continue;
                }

                if (currentCharacter.isOnMap()
                        && isPointInBoundingBox(point, currentCharacter)) {
                    character = currentCharacter;
                    break;
                }
            }
        }

        return character;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (x > maxOffsetX) {
            x = maxOffsetX;
        } else if (x < minOffsetX) {
            x = minOffsetX;
        }

        if (map.getWidth() * zoomScale < getWidth()) {
            x = 0;
        }

        if (y > maxOffsetY) {
            y = maxOffsetY;
        } else if (y < minOffsetY) {
            y = minOffsetY;
        }

        if (map.getHeight() * zoomScale < getHeight()) {
            y = 0;
        }

        super.scrollTo(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (game == null || map == null) {
            return;
        }

        MapDrawer.initialise(getResources(), getContext());

//        for (RegionState region : map.getRegionsWithoutSpecials()) {
//            region.prepareForDrawing(this);
//            region.drawBackground(canvas);
//        }

        for (RegionState region : map.getRegionsWithoutSpecials()) {
            MapDrawer.prepareForDrawing(region, this);
            MapDrawer.drawHighlightBackground(region, canvas, highlightPaint);
        }

        for (RegionState region : map.getRegionsWithoutSpecials()) {
            MapDrawer.drawOutline(region, canvas, outlinePaint);
        }

        //        for (RegionState region : map.getRegionsWithoutSpecials()) {
        //            region.drawLabel(canvas, this);
        //        }

        if (game.getPhase().getPrimaryPhase() == PrimaryPhase.GAME_SETUP) {
            return; // Don't draw more in game setup
        }

        if (isInTargetSelectionMode && selectedCharacter != null) {
            MapDrawer.drawTargetLine(canvas, this, selectedCharacter,
                    selectedTargets);
        }

        for (CharacterState character : game.getOwnCharacters()) {
            if (character.isOnMap()) {


                if (this.movementHistoryPoints == null || this.movementHistoryPoints.isEmpty()) {

                    lastTurnMovement.clear();

                    PointState point = character.getBattleLogState().getMovementHistory().get("turn_"+(game.getPhase().getGameTurn() - 2));
                    if (point != null) {
                        lastTurnMovement.add(point);
                    }
                    point = character.getBattleLogState().getMovementHistory().get("turn_"+(game.getPhase().getGameTurn() - 1));
                    if (point != null && !point.equals(character.getCenterPoint())) {
                        lastTurnMovement.add(point);
                    }

                    if (lastTurnMovement.size() > 1) {
                        lastTurnMovement.removeFirst();
                    }

                    if (!lastTurnMovement.isEmpty()) {
                        lastTurnMovement.add(character.getCenterPoint());
                        MapDrawer.drawCharacterPath(canvas, this, lastTurnMovement, false);
                    }
                }
                if (character.isSame(selectedCharacter)) {
                    MapDrawer.drawCharacterSelection(canvas, this, character);
                }
                Integer blinkState = this.characterAnimationStates.get(character);


                MapDrawer.drawFriendlyCharacter(canvas, this, character, blinkState, getCachedRoleBitmap(getContext(), character));


            }
        }

        for (CharacterState character : game.getEnemyCharacters()) {
            if (character.isOnMap()) {

                if (this.movementHistoryPoints == null || this.movementHistoryPoints.isEmpty()) {

                    lastTurnMovement.clear();

                    PointState point = character.getBattleLogState().getMovementHistory().get("turn_"+(game.getPhase().getGameTurn() - 2));
                    if (point != null) {
                        lastTurnMovement.add(point);
                    }
                    point = character.getBattleLogState().getMovementHistory().get("turn_"+(game.getPhase().getGameTurn() - 1));
                    if (point != null && !point.equals(character.getCenterPoint())) {
                        lastTurnMovement.add(point);
                    }

                    if (lastTurnMovement.size() > 1) {
                        lastTurnMovement.removeFirst();
                    }

                    if (!lastTurnMovement.isEmpty()) {
                        lastTurnMovement.add(character.getCenterPoint());
                        MapDrawer.drawCharacterPath(canvas, this, lastTurnMovement, true);
                    }

                }
                boolean isSelected = selectedTargets.contains(character);
                MapDrawer.drawEnemyCharacters(game, canvas, this, character,
                        isSelected);

            }
        }

        for (ThrowableState throwable : game.getWorld().getThrowableStates()) {
            MapDrawer.drawThrowable(game, canvas, this, throwable);
        }

        for (ThrowableState throwable : game.getWorld().getTemporarThrowableStates()) {
            MapDrawer.drawTempThrowable(canvas, this, throwable);
        }

        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
        }

        if (game.getPhase().getPrimaryPhase() == PrimaryPhase.PRE_GAME) {
            MapDrawer.drawPregameZombies(canvas, this, game);

        }

        if (game.getPhase().isZombiePhase()) {
            MapDrawer.drawZombies(canvas, this, game, zombieHighlightPaint);
        }

        if (this.movementHistoryPoints != null && !this.movementHistoryPoints.isEmpty()) {
            MapDrawer.drawCharacterPath(canvas, this, this.movementHistoryPoints, false);

        }

        notifyViewChangedListener();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed) {
            return; // If the size did not change we don't need to adjust the
            // zoom levels
        }

        initZoom();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int desiredWidth = -1;
        int desiredHeight = -1;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = widthSize;
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = heightSize;
        } else {
            //Be whatever you want
            height = desiredHeight;
        }


        if (height < 0) {
            height = map.getHeight();
            width = map.getWidth();
        }


        float mapAspectRatio = (float) map.getHeight() / (float) map.getWidth();


        int heightFromAspectRatio = (int) (width * mapAspectRatio);
        int widthFromAspectRatio = (int) (height / mapAspectRatio);


        if (heightFromAspectRatio > height) {
            setMeasuredDimension(widthFromAspectRatio, width);


        } else {
            setMeasuredDimension(width, heightFromAspectRatio);

        }


    }

    public void setViewChangedListener(ViewChangedListener viewChangedListener) {
        this.viewChangedListener = viewChangedListener;
    }

    public void blinkCharacterOnMap(final CharacterState character) {
        if (!character.isOnMap()) {
            return;
        }

        characterAnimationStates.put(character, 10);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Integer integer = characterAnimationStates.get(character);
                    if (integer != null) {
                        int newValue = integer - 1;
                        if (newValue <= 0) {
                            characterAnimationStates.remove(character);
                            Handler handler = new Handler(Looper.getMainLooper());

                            handler.post(new Runnable() {
                                public void run() {
                                    invalidate();
                                }
                            });

                            return;
                        }
                        characterAnimationStates.put(character, newValue);
                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(new Runnable() {
                            public void run() {
                                invalidate();
                            }
                        });

                        try {
                            Thread.sleep(SLEEP_TIME_BLINK);
                        } catch (InterruptedException e) {
                        }
                    }
                }

            }
        }).start();
    }

    private void init(Context context) {
        map = new MapState();
        scroller = new Scroller(context);
        zoomScale = 1.0f;
        selectedTargets = new ArrayList<CharacterState>();
        initialisePaints();

        lastTurnMovement = new LinkedList<>();
    }

    private void initZoom() {
        float scaleWidth = Float.valueOf(getWidth())
                / Float.valueOf(map.getWidth());
        float scaleHeight = Float.valueOf(getHeight())
                / Float.valueOf(map.getHeight());

        initialZoomScale = Math.min(scaleWidth, scaleHeight);
        minZoomScale = initialZoomScale;
        maxZoomScale = initialZoomScale * 3;
        smartZoomScale = initialZoomScale * 2;

        setZoomScale(initialZoomScale);
    }

    private void calculateOffsets() {
        maxOffsetX = (int) (Float.valueOf(map.getWidth()) * zoomScale - getWidth());
        maxOffsetY = (int) (Float.valueOf(map.getHeight()) * zoomScale - getHeight());
        minOffsetX = 0;
        minOffsetY = 0;
    }

    private void notifyViewChangedListener() {
        if (this.viewChangedListener != null) {
            this.viewChangedListener.onViewChanged();
        }
    }

    public interface ViewChangedListener {
        void onViewChanged();
    }


    private RectF calculateBoundingBox(CharacterState characterState) {
        if (characterState.getCenterPoint() == null) {
            return null;
        }
        return new RectF(characterState.getCenterPoint().x - CHARACTER_BOUNDING_BOX_SIZE, characterState.getCenterPoint().y
                - CHARACTER_BOUNDING_BOX_SIZE, characterState.getCenterPoint().x + CHARACTER_BOUNDING_BOX_SIZE, characterState.getCenterPoint().y
                + CHARACTER_BOUNDING_BOX_SIZE);
    }

    private boolean isPointInBoundingBox(PointState point, CharacterState characterState) {
        RectF boundingBox = getBoundingBox(characterState);

        return point.x >= boundingBox.left && point.x <= boundingBox.right
                && point.y >= boundingBox.top && point.y <= boundingBox.bottom;
    }

    private HashMap<String, RectF> characterBoundingBoxCache = new HashMap<>();

    //TODO: can this be cached? how do we know when a character bounding box has to be recalculated?
    public RectF getBoundingBox(CharacterState characterState) {
        return calculateBoundingBox(characterState);
//        RectF boundingBox = characterBoundingBoxCache.get(characterState.getIdentifier());
//        if (boundingBox == null) {
//
//            boundingBox = calculateBoundingBox(characterState);
//            characterBoundingBoxCache.put(characterState.getIdentifier(), boundingBox);
//        }
//
//        return boundingBox;
    }

    public Bitmap getCachedRoleBitmap(Context context, final CharacterState characterState) {
        Bitmap cachedRoleBitmap = cachedRoleBitmaps.get(characterState.getIdentifier());

        if (cachedRoleBitmap == null) {
            Target loadtarget = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    cachedRoleBitmaps.put(characterState.getIdentifier(), bitmap);
                    invalidate();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }

            };

            Picasso.with(context).load(characterState.getRoleIconURL()).into(loadtarget);


        }
        return cachedRoleBitmap;
    }

}
