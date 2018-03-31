package com.landsofruin.companion.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.WorldState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.utils.UIUtils;
import com.landsofruin.gametracker.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapDrawer {

    public static final int MOLOTOV_ID = 8000;

    private static final int ZOMBIE_DIRECTION_TOP = 0;
    private static final int ZOMBIE_DIRECTION_LEFT = 1;
    private static final int ZOMBIE_DIRECTION_RIGHT = 2;
    private static final int ZOMBIE_DIRECTION_BOTTOM = 3;


    private static float ZOMBIE_CIRCLE_RADIUS;
    private static float ZOMBIE_CIRCLE_RADIUS_INCREMENT;
    private static final int BASE_RADIUS = 5;
    private static final int BASE_RADIUS_HISTORY = 3;
    private static final int BASE_FONT_SIZE = 8;
    private static final int THROWABLE_FONT_SIZE = 12;
    private static final int LABEL_COLOR = 0xFFffffff;

    private static final int FRIENDLY_BACKGROUND_COLOR = 0xff0091EA;
    private static final int FRIENDLY_BACKGROUND_COLOR_DONE = 0xff0091EA;

    private static final int ENEMY_BACKGROUND_COLOR = 0xffE65100;
    private static final int ENEMY_SELECTED_COLOR = 0xffE65100;

    private static final int MOVEMENT_HISTORY_BACKGROUND_COLOR = 0x770076a3;
    private static final int MOVEMENT_HISTORY_ENEMY_BACKGROUND_COLOR = 0x77E65100;
    private static final int MOVEMENT_HISTORY_PATH_COLOR = 0xFF0076a3;

    private static final int ZOMBIE_RADIUS_COLOR = 0XCCF44336;
    private static final int ZOMBIE_BG_COLOR = 0XAAF44336;
    private static final int ZOMBIE_STROKE_COLOR = 0XFFF44336;


    private static final Paint FRIENDLY_BACKGROUND_COLOR_PAINT = new Paint();
    private static final Paint MOVEMENT_HISTORY_BACKGROUND_COLOR_PAINT = new Paint();
    private static final Paint MOVEMENT_HISTORY_BACKGROUND_ENEMY_COLOR_PAINT = new Paint();
    private static final Paint MOVEMENT_HISTORY_BACKGROUND_LINE_PAINT = new Paint();
    private static final Paint MOVEMENT_HISTORY_BACKGROUND_ENEMY_LINE_PAINT = new Paint();
    private static final Paint MOVEMENT_HISTORY_PATH_COLOR_PAINT = new Paint();
    private static final Paint CHARACTER_BLINK_PAINT = new Paint();

    private static final Paint FRIENDLY_BACKGROUND_COLOR_DONE_PAINT = new Paint();

    private static final Paint ENEMY_BACKGROUND_COLOR_PAINT = new Paint();
    private static final int ENEMY_DETECTED_HIDDEN_BACKGROUND_COLOR = 0xFFFFc739;
    private static final Paint ENEMY_DETECTED_HIDDEN_BACKGROUND_COLOR_PAINT = new Paint();
    private static final int THROWABLE_BACKGROUND_COLOR = 0xFF1A237E;
    private static final int THROWABLE_BACKGROUND_EDGE_COLOR = 0x301A237E;

    private static final int THROWABLE_BACKGROUND_COLOR_FLAME = 0xFFB71C1C;
    private static final int THROWABLE_BACKGROUND_EDGE_COLOR_FLAME = 0x30FBC02D;


    private static final Paint THROWABLE_BACKGROUND_COLOR_PAINT = new Paint();
    private static final int HIGHLIGHT_COLOR = 0xFFffffff;

    private static final int THROWABLE_RADIUS_MULTIPLIER = 13;
    private static final Paint THROWABLE_LABEL_PAINT = new Paint();
    private static final Paint CHARACTER_SELECTION_PAINT = new Paint();
    private static final Paint CHARACTER_TARGET_LINE_PAINT = new Paint();
    private static final Paint CHARACTER_LABEL_PAINT = new Paint();
    private static final Paint ZOMBIE_TARGET_REGION_PAINT = new Paint();
    private static final Paint ZOMBIE_SPAWN_PAINT = new Paint();
    private static final Paint ZOMBIE_SPAWN_BG_PAINT = new Paint();
    private static final Paint ZOMBIE_SPAWN_STROKE_PAINT = new Paint();
    private static Bitmap normalZombieBitmap;
    private static Bitmap fastZombieBitmap;
    private static Bitmap fatZombieBitmap;


    private static int ZOMBIE_SPAWN_TYPE_OFFSET;
    private static int ZOMBIE_SPAWN_LEFT_OFFSET;
    private static int ZOMBIE_SPAWN_RIGHT_OFFSET;
    private static int ZOMBIE_SPAWN_TOP_OFFSET;
    private static int ZOMBIE_SPAWN_BOTTOM_OFFSET;
    private static int ZOMBIE_SPAWN_BOX_WIDTH;
    private static int ZOMBIE_SPAWN_BOX_HEIGHT;
    private static int ZOMBIE_SPAWN_ICON_MARGIN;
    private static int ZOMBIE_SPAWN_TEXT_ALIGN_OFFSET;
    private static int ZOMBIE_SPAWN_TEXT_SIZE;
    private static int ZOMBIE_SPAWN_DESCRIPTION_TEXT_SIZE;
    private static int ZOMBIE_SPAWN_TEXT_SIZE_OPPONENT;
    private static int ZOMBIE_SPAWN_OPPONENT_OFFSET;

    private static HashMap<String, RectF> regionBoundingBoxCache = new HashMap<>();
    private static HashMap<String, Path> regionPathCache = new HashMap<>();
    private static int baseRadius;
    private static int baseHistoryRadius;
    private static Bitmap fastZombieSmallBitmap;
    private static Bitmap normalZombieSmallBitmap;
    private static Bitmap fatZombieSmallBitmap;
    private static int ZOMBIE_SPAWN_DESCRIPTION_TEXT_OFFSET;

    static {
        FRIENDLY_BACKGROUND_COLOR_PAINT.setColor(FRIENDLY_BACKGROUND_COLOR);
        MOVEMENT_HISTORY_BACKGROUND_COLOR_PAINT.setColor(MOVEMENT_HISTORY_BACKGROUND_COLOR);
        MOVEMENT_HISTORY_BACKGROUND_ENEMY_COLOR_PAINT.setColor(MOVEMENT_HISTORY_ENEMY_BACKGROUND_COLOR);
        MOVEMENT_HISTORY_BACKGROUND_LINE_PAINT.setColor(MOVEMENT_HISTORY_BACKGROUND_COLOR);


        MOVEMENT_HISTORY_BACKGROUND_ENEMY_LINE_PAINT.setColor(MOVEMENT_HISTORY_ENEMY_BACKGROUND_COLOR);


        ENEMY_BACKGROUND_COLOR_PAINT.setColor(ENEMY_BACKGROUND_COLOR);
        ENEMY_DETECTED_HIDDEN_BACKGROUND_COLOR_PAINT.setColor(ENEMY_DETECTED_HIDDEN_BACKGROUND_COLOR);
        THROWABLE_BACKGROUND_COLOR_PAINT.setColor(THROWABLE_BACKGROUND_COLOR);
        FRIENDLY_BACKGROUND_COLOR_DONE_PAINT.setColor(FRIENDLY_BACKGROUND_COLOR_DONE);


        THROWABLE_LABEL_PAINT.setColor(LABEL_COLOR);
        THROWABLE_LABEL_PAINT.setTextAlign(Align.CENTER);
//        THROWABLE_LABEL_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);


        CHARACTER_SELECTION_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        CHARACTER_SELECTION_PAINT.setColor(Color.BLACK);

        CHARACTER_TARGET_LINE_PAINT.setAntiAlias(true);
        CHARACTER_TARGET_LINE_PAINT.setColor(ENEMY_SELECTED_COLOR);


        MOVEMENT_HISTORY_PATH_COLOR_PAINT.setAntiAlias(true);
        MOVEMENT_HISTORY_PATH_COLOR_PAINT.setColor(MOVEMENT_HISTORY_PATH_COLOR);


        CHARACTER_LABEL_PAINT.setColor(LABEL_COLOR);
        CHARACTER_LABEL_PAINT.setTextAlign(Align.CENTER);


        ZOMBIE_TARGET_REGION_PAINT.setColor(ZOMBIE_BG_COLOR);

        ZOMBIE_SPAWN_BG_PAINT.setColor(ZOMBIE_BG_COLOR);
        ZOMBIE_SPAWN_BG_PAINT.setStyle(Paint.Style.FILL);

        ZOMBIE_SPAWN_STROKE_PAINT.setColor(ZOMBIE_STROKE_COLOR);
        ZOMBIE_SPAWN_STROKE_PAINT.setStyle(Paint.Style.STROKE);

        ZOMBIE_SPAWN_PAINT.setTextAlign(Align.CENTER);
        ZOMBIE_SPAWN_PAINT.setTypeface(Typeface.DEFAULT_BOLD);


        CHARACTER_BLINK_PAINT.setColor(0X550000FF);
    }

    static boolean isInitialised = false;

    public static void initialise(Resources resources, Context context) {
        if (isInitialised) {
            return;
        }
        isInitialised = true;

        normalZombieBitmap = BitmapFactory.decodeResource(resources, R.drawable.zombie_spawn_normal);
        fastZombieBitmap = BitmapFactory.decodeResource(resources, R.drawable.zombie_spawn_fast);
        fatZombieBitmap = BitmapFactory.decodeResource(resources, R.drawable.zombie_spawn_fat);

        normalZombieSmallBitmap = BitmapFactory.decodeResource(resources, R.drawable.zombie_spawn_normal_small);
        fastZombieSmallBitmap = BitmapFactory.decodeResource(resources, R.drawable.zombie_spawn_fast_small);
        fatZombieSmallBitmap = BitmapFactory.decodeResource(resources, R.drawable.zombie_spawn_fat_small);

        baseRadius = (int) UIUtils.convertDpToPixel(BASE_RADIUS, context);
        baseHistoryRadius = (int) UIUtils.convertDpToPixel(BASE_RADIUS_HISTORY, context);

        ZOMBIE_SPAWN_TYPE_OFFSET = (int) UIUtils.convertDpToPixel(35, context);
        ZOMBIE_SPAWN_LEFT_OFFSET = (int) UIUtils.convertDpToPixel(35, context);
        ZOMBIE_SPAWN_RIGHT_OFFSET = (int) UIUtils.convertDpToPixel(-50, context);
        ZOMBIE_SPAWN_BOTTOM_OFFSET = (int) UIUtils.convertDpToPixel(-50, context);
        ZOMBIE_SPAWN_TOP_OFFSET = (int) UIUtils.convertDpToPixel(10, context);
        ZOMBIE_SPAWN_BOX_HEIGHT = (int) UIUtils.convertDpToPixel(34, context);
        ZOMBIE_SPAWN_BOX_WIDTH = (int) UIUtils.convertDpToPixel(70, context);
        ZOMBIE_SPAWN_ICON_MARGIN = (int) UIUtils.convertDpToPixel(45, context);
        ZOMBIE_SPAWN_TEXT_ALIGN_OFFSET = (int) UIUtils.convertDpToPixel(25, context);
        ZOMBIE_SPAWN_TEXT_SIZE = (int) UIUtils.convertDpToPixel(23, context);
        ZOMBIE_SPAWN_TEXT_SIZE_OPPONENT = (int) UIUtils.convertDpToPixel(9, context);
        ZOMBIE_SPAWN_DESCRIPTION_TEXT_SIZE = (int) UIUtils.convertDpToPixel(8, context);
        ZOMBIE_SPAWN_DESCRIPTION_TEXT_OFFSET = (int) UIUtils.convertDpToPixel(9, context);
        ZOMBIE_SPAWN_OPPONENT_OFFSET = (int) UIUtils.convertDpToPixel(10, context);

        ZOMBIE_CIRCLE_RADIUS = (int) UIUtils.convertDpToPixel(1, context);
        ZOMBIE_CIRCLE_RADIUS_INCREMENT = (int) UIUtils.convertDpToPixel(3, context);

        MOVEMENT_HISTORY_BACKGROUND_ENEMY_LINE_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(6, context));
        MOVEMENT_HISTORY_BACKGROUND_LINE_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(6, context));
        CHARACTER_BLINK_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(3, context));
        MOVEMENT_HISTORY_PATH_COLOR_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(3, context));
        CHARACTER_TARGET_LINE_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(3, context));
//        THROWABLE_LABEL_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(3, context));

        ZOMBIE_SPAWN_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(1, context));
        ZOMBIE_SPAWN_STROKE_PAINT.setStrokeWidth((int) UIUtils.convertDpToPixel(2, context));
    }

    public static void drawFriendlyCharacter(Canvas canvas, MapView mapView,
                                             CharacterState character, Integer blinkState, Bitmap roleImage) {
        float scale = mapView.getZoomScale();


        PointState point = character.getCenterPoint();

        Paint paint = FRIENDLY_BACKGROUND_COLOR_PAINT;
        if (!character.hasActionsToPerform()) {
            paint = FRIENDLY_BACKGROUND_COLOR_DONE_PAINT;
        }

        drawCharacterCircle(canvas, paint, point, scale, baseRadius, roleImage);
        drawCharacterLabel(canvas, mapView, character);
        if (blinkState != null) {
            drawCharacterBlink(canvas, paint, point, scale, baseRadius, blinkState);
        }


    }


    public static void drawCharacterPath(Canvas canvas, MapView mapView,
                                         LinkedList<PointState> movementHistoryPoints, boolean isEnemy) {
        float scale = mapView.getZoomScale();


        PointState previousPoint = null;
        for (PointState point : movementHistoryPoints) {

            drawCharacterCircle(canvas, (isEnemy ? MOVEMENT_HISTORY_BACKGROUND_ENEMY_COLOR_PAINT : MOVEMENT_HISTORY_BACKGROUND_COLOR_PAINT), point, scale, baseHistoryRadius, null);

            if (previousPoint != null) {

                canvas.drawLine(previousPoint.x * scale, previousPoint.y * scale, point.x
                        * scale, point.y * scale, (isEnemy ? MOVEMENT_HISTORY_BACKGROUND_ENEMY_LINE_PAINT : MOVEMENT_HISTORY_BACKGROUND_LINE_PAINT));
            }

            previousPoint = point;
        }


    }

    public static void drawEnemyCharacters(GameState gameState, Canvas canvas,
                                           MapView mapView, CharacterState character, boolean isSelected) {

        boolean detected = false;
        if (character.isHidden()) {

            for (String playerId : character.getDetectedByPlayers()) {
                if (playerId.equals(gameState.getMe().getIdentifier())) {
                    detected = true;
                }
            }

            if (!detected) {
                return;
            }
        }

        float scale = mapView.getZoomScale();

        Paint paint;

        if (detected) {
            paint = ENEMY_DETECTED_HIDDEN_BACKGROUND_COLOR_PAINT;
        } else {
            paint = ENEMY_BACKGROUND_COLOR_PAINT;

        }

        PointState point = character.getCenterPoint();

        drawCharacterCircle(canvas, paint, point, scale, baseRadius, null);
        drawCharacterLabel(canvas, mapView, character);

        if (isSelected) {
            paint.setColor(ENEMY_SELECTED_COLOR);
            drawCharacterCircle(canvas, paint, point, scale, baseRadius * 1.5f, null);
        }

    }

    public static void drawThrowable(GameState gameState, Canvas canvas, MapView mapView,
                                     ThrowableState throwable) {
        float scale = mapView.getZoomScale();


        if (WargearConsumable.THROWABLE_CATEGORY_ARTILLERY.equals(LookupHelper.getInstance().getWargearFor(throwable).getCategory())) {

            if (throwable.getTemplateSize() == 0) {
                if (gameState.getMe().getIdentifier().equals(throwable.getOwningPlayerId())) {
                    PointState point = throwable.getCenterPoint();

                    drawThrowableCircle(canvas, THROWABLE_BACKGROUND_COLOR_PAINT, point, scale,
                            throwable.getTemplateSize(), mapView, false);
                }
            } else {
                PointState point = throwable.getCenterPoint();

                drawThrowableCircle(canvas, THROWABLE_BACKGROUND_COLOR_PAINT, point, scale,
                        throwable.getTemplateSize(), mapView, false);

            }
        } else {
            PointState point = throwable.getCenterPoint();

            drawThrowableCircle(canvas, THROWABLE_BACKGROUND_COLOR_PAINT, point, scale,
                    throwable.getTemplateSize(), mapView, throwable.getWargearId() == MOLOTOV_ID);
        }


    }

    public static void drawTempThrowable(Canvas canvas, MapView mapView,
                                         ThrowableState throwable) {
        float scale = mapView.getZoomScale();


        PointState point = throwable.getCenterPoint();

        drawTempThrowableCircle(canvas, THROWABLE_BACKGROUND_COLOR_PAINT, point, scale,
                throwable.getTemplateSize(), mapView);

    }


    private static void drawTempThrowableCircle(Canvas canvas, Paint paint,
                                                PointState point, float scale, int templateradius, MapView mapView) {

        float radius = THROWABLE_RADIUS_MULTIPLIER;
        if (templateradius > 0) {
            radius = templateradius * THROWABLE_RADIUS_MULTIPLIER;
        }

        paint.setColor(THROWABLE_BACKGROUND_COLOR);
        RadialGradient gradient = new RadialGradient(scale * point.x, scale * point.y, radius * scale, THROWABLE_BACKGROUND_COLOR,
                THROWABLE_BACKGROUND_EDGE_COLOR, android.graphics.Shader.TileMode.CLAMP);

        paint.setDither(true);
        paint.setShader(gradient);
        canvas.drawCircle(scale * point.x, scale * point.y,
                (int) (radius * scale), paint);


    }

    private static void drawThrowableCircle(Canvas canvas, Paint paint,
                                            PointState point, float scale, int templateradius, MapView mapView, boolean isFlame) {


        int colourCenter = isFlame ? THROWABLE_BACKGROUND_COLOR_FLAME : THROWABLE_BACKGROUND_COLOR;
        int colourEdge = isFlame ? THROWABLE_BACKGROUND_EDGE_COLOR_FLAME : THROWABLE_BACKGROUND_EDGE_COLOR;


        float radius = THROWABLE_RADIUS_MULTIPLIER;
        if (templateradius > 0) {
            radius = templateradius * THROWABLE_RADIUS_MULTIPLIER;
        }

        if (templateradius == WargearConsumable.TEMPLATE_SIZE_SECTION) {
            radius = 5 * THROWABLE_RADIUS_MULTIPLIER;
        }

        paint.setColor(colourCenter);
        RadialGradient gradient = new RadialGradient(scale * point.x, scale * point.y, radius * scale, colourCenter,
                colourEdge, android.graphics.Shader.TileMode.CLAMP);

        paint.setDither(true);
        paint.setShader(gradient);
        canvas.drawCircle(scale * point.x, scale * point.y,
                (int) (radius * scale), paint);


        gradient = new RadialGradient(scale * point.x, scale * point.y, (radius * scale) / 2, colourCenter,
                colourEdge, android.graphics.Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        canvas.drawCircle(scale * point.x, scale * point.y,
                (int) (radius * scale) / 2, paint);


        paint = THROWABLE_LABEL_PAINT;
        paint.setTextSize(THROWABLE_FONT_SIZE * scale);

        float x = point.x * scale;
        float y = point.y * scale;

        if (mapView.isFlipped()) {
            canvas.save();
            canvas.rotate(-180, x, y);

            if (templateradius == WargearConsumable.TEMPLATE_SIZE_SECTION) {
                canvas.drawText("map section", x, y - 3, paint);
            } else {
                canvas.drawText("" + templateradius + "\"", x, y + 3, paint);
            }

            canvas.restore();
        } else {

            if (templateradius == WargearConsumable.TEMPLATE_SIZE_SECTION) {
                canvas.drawText("map section", x, y - 3, paint);
            } else {
                canvas.drawText("" + templateradius + "\"", x, y - 3, paint);
            }
        }

    }

    private static void drawCharacterBlink(Canvas canvas, Paint paint,
                                           PointState point, float scale, float radius, int animationState) {
        canvas.drawCircle(scale * point.x, scale * point.y,
                (int) ((radius + (animationState * 10) * scale)), CHARACTER_BLINK_PAINT);
    }

    private static void drawCharacterCircle(Canvas canvas, Paint paint,
                                            PointState point, float scale, float radius, Bitmap roleImage) {

        int scaledRadius = (int) (radius * scale);

        canvas.drawCircle(scale * point.x, scale * point.y,
                scaledRadius, paint);

        if (roleImage != null) {
            int imageSize = (int) (scaledRadius * 1.5);
            canvas.drawBitmap(getResizedBitmap(roleImage, imageSize, imageSize), scale * point.x - (imageSize / 2), scale * point.y - (imageSize / 2), paint);
        }
    }


    private static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static void drawCharacterSelection(Canvas canvas, MapView mapView,
                                              CharacterState character) {
        float scale = mapView.getZoomScale();

        Paint paint = CHARACTER_SELECTION_PAINT;

        PointState point = character.getCenterPoint();
        if (point == null) {
            return;
        }

        CHARACTER_SELECTION_PAINT.setColor(Color.BLACK);
        canvas.drawCircle(scale * point.x, scale * point.y,
                (int) ((baseRadius + 3) * scale), paint);

        paint.setColor(HIGHLIGHT_COLOR);

        canvas.drawCircle(scale * point.x, scale * point.y,
                (int) ((baseRadius + 2) * scale), paint);
//        if (character.getAttackingZombies() > 0) {
//            drawZombies(canvas, point.x * scale, point.y * scale, scale,
//                    character.getAttackingZombies());
//        }
    }

    public static void drawTargetLine(Canvas canvas, MapView mapView,
                                      CharacterState actor, List<CharacterState> targets) {
        float scale = mapView.getZoomScale();


        PointState origin = actor.getCenterPoint();

        for (CharacterState character : targets) {
            if (!character.isOnMap()) {
                continue;
            }

            PointState target = character.getCenterPoint();

            if (origin == null) {
                continue;
            }

            canvas.drawLine(origin.x * scale, origin.y * scale, target.x
                    * scale, target.y * scale, CHARACTER_TARGET_LINE_PAINT);
        }
    }

    public static void drawCharacterLabel(Canvas canvas, MapView mapView,
                                          CharacterState character) {
        canvas.save();

        if (mapView.isFlipped()) {
            float centerX = mapView.getMeasuredWidth() / 2;
            float centerY = mapView.getMeasuredHeight() / 2;

            canvas.scale(-1, -1, centerX, centerY);
        }

        float scale = mapView.getZoomScale();

        Paint paint = CHARACTER_LABEL_PAINT;
        paint.setTextSize(BASE_FONT_SIZE * scale);

        RectF boundingBox = mapView.getBoundingBox(character);
        PointState point = character.getCenterPoint();
        String label = character.getName();

        float x = point.x * scale;
        float y = scale
                * (mapView.isFlipped() ? boundingBox.bottom : boundingBox.top);

        if (mapView.isFlipped()) {
            y = mapView.getHeight() - y;
            x = mapView.getWidth() - x;
        }

        canvas.drawText(label, x, y, paint);

        canvas.restore();
    }

    public static void drawPregameZombies(Canvas canvas, MapView mapView,
                                          GameState game) {
        float scale = mapView.getZoomScale();
        for (RegionState region : mapView.getMap().getRegionsWithoutSpecials()) {

            int myZombies = 0;
            int otherZombies = 0;
            for (String player : game.getWorld().getPreGameZombiesPerPlayerPerArea().keySet()) {

                HashMap<String, Integer> zombies = game.getWorld().getPreGameZombiesPerPlayerPerArea().get(player);
                Integer zombiesInt = zombies.get(region.getIdentifier());
                if (zombiesInt != null) {
                    if (player.equals(game.getMe().getIdentifier())) {
                        myZombies += zombiesInt;
                    } else {
                        otherZombies += zombiesInt;
                    }
                }
            }

            if (myZombies == 0 && otherZombies == 0) {
                continue;
            }

            drawPregameZombiesOneSection(canvas, getBoundingBox(region)
                    .centerX() * scale, getBoundingBox(region).centerY()
                    * scale, scale, myZombies, otherZombies, mapView);

        }


    }


    public static void drawZombies(Canvas canvas, MapView mapView,
                                   GameState game, Paint zombieHighlightPaint) {


        float scale = mapView.getZoomScale();

        MapState map = mapView.getMap();
        WorldState world = game.getWorld();

        if (world.getZombieTargetRegion() != null) {

            RegionState region = map.findRegionByIdentifier(world
                    .getZombieTargetRegion());
            drawZombieHightlight(region, canvas, mapView, zombieHighlightPaint);

            if (world.hasZombieTargetCharacter()) {
                CharacterState character = game.findCharacterByIdentifier(world
                        .getZombieTargetCharacter());


                PointState point = character.getCenterPoint();
                canvas.drawCircle(scale * point.x, scale * point.y,
                        (int) ((5 + baseRadius) * scale), ZOMBIE_TARGET_REGION_PAINT);

            }
        }


        float w = map.getWidth() * scale;
        float h = map.getHeight() * scale;


        List<HashMap<String, Integer>> zombies = world.getZombieSpawns();


        canvas.save();

        int topLeft = WorldState.SPAWN_DIRECTION_topLeft;
        int topCenter = WorldState.SPAWN_DIRECTION_topCenter;
        int topRight = WorldState.SPAWN_DIRECTION_topRight;

        int bottomLeft = WorldState.SPAWN_DIRECTION_bottomLeft;
        int bottomCenter = WorldState.SPAWN_DIRECTION_bottomCenter;
        int bottomRight = WorldState.SPAWN_DIRECTION_bottomRight;

        int rightTop = WorldState.SPAWN_DIRECTION_rightTop;
        int rightCenter = WorldState.SPAWN_DIRECTION_rightCenter;
        int rightBottom = WorldState.SPAWN_DIRECTION_rightBottom;

        int leftTop = WorldState.SPAWN_DIRECTION_leftTop;
        int leftCenter = WorldState.SPAWN_DIRECTION_leftCenter;
        int leftBottom = WorldState.SPAWN_DIRECTION_leftBottom;

        float offsetX = 0;
        float offsetY = 0;

        if (mapView.isFlipped()) {
            float centerX = mapView.getMeasuredWidth() / 2;
            float centerY = mapView.getMeasuredHeight() / 2;

            canvas.scale(-1, -1, centerX, centerY);

            offsetX = mapView.getWidth() - map.getWidth() * scale;
            offsetY = mapView.getHeight() - map.getHeight() * scale;

            topLeft = WorldState.SPAWN_DIRECTION_bottomRight;
            topCenter = WorldState.SPAWN_DIRECTION_bottomCenter;
            topRight = WorldState.SPAWN_DIRECTION_bottomLeft;

            bottomLeft = WorldState.SPAWN_DIRECTION_topRight;
            bottomCenter = WorldState.SPAWN_DIRECTION_topCenter;
            bottomRight = WorldState.SPAWN_DIRECTION_topLeft;

            rightTop = WorldState.SPAWN_DIRECTION_leftBottom;
            rightCenter = WorldState.SPAWN_DIRECTION_leftCenter;
            rightBottom = WorldState.SPAWN_DIRECTION_leftTop;

            leftTop = WorldState.SPAWN_DIRECTION_rightBottom;
            leftCenter = WorldState.SPAWN_DIRECTION_rightCenter;
            leftBottom = WorldState.SPAWN_DIRECTION_rightTop;
        }


        // Top
        drawZombies(canvas, offsetX + w * 0.25f,
                offsetY + ZOMBIE_CIRCLE_RADIUS, scale, zombies.get(topLeft), ZOMBIE_DIRECTION_TOP);
        drawZombies(canvas, offsetX + w * 0.5f, offsetY + ZOMBIE_CIRCLE_RADIUS,
                scale, zombies.get(topCenter), ZOMBIE_DIRECTION_TOP);
        drawZombies(canvas, offsetX + w * 0.75f,
                offsetY + ZOMBIE_CIRCLE_RADIUS, scale, zombies.get(topRight), ZOMBIE_DIRECTION_TOP);

        // Bottom
        drawZombies(canvas, offsetX + w * 0.25f, offsetY + h
                - ZOMBIE_CIRCLE_RADIUS, scale, zombies.get(bottomLeft), ZOMBIE_DIRECTION_BOTTOM);
        drawZombies(canvas, offsetX + w * 0.5f, offsetY + h
                - ZOMBIE_CIRCLE_RADIUS, scale, zombies.get(bottomCenter), ZOMBIE_DIRECTION_BOTTOM);
        drawZombies(canvas, offsetX + w * 0.75f, offsetY + h
                - ZOMBIE_CIRCLE_RADIUS, scale, zombies.get(bottomRight), ZOMBIE_DIRECTION_BOTTOM);

        // Right
        drawZombies(canvas, offsetX + w - ZOMBIE_CIRCLE_RADIUS, offsetY + h
                * 0.25f, scale, zombies.get(rightTop), ZOMBIE_DIRECTION_RIGHT);

        drawZombies(canvas, offsetX + w - ZOMBIE_CIRCLE_RADIUS, offsetY + h
                * 0.5f, scale, zombies.get(rightCenter), ZOMBIE_DIRECTION_RIGHT);
        drawZombies(canvas, offsetX + w - ZOMBIE_CIRCLE_RADIUS, offsetY + h
                * 0.75f, scale, zombies.get(rightBottom), ZOMBIE_DIRECTION_RIGHT);

        // Left
        drawZombies(canvas, offsetX + ZOMBIE_CIRCLE_RADIUS,
                offsetY + h * 0.25f, scale, zombies.get(leftTop), ZOMBIE_DIRECTION_LEFT);
        drawZombies(canvas, offsetX + ZOMBIE_CIRCLE_RADIUS, offsetY + h * 0.5f,
                scale, zombies.get(leftCenter), ZOMBIE_DIRECTION_LEFT);
        drawZombies(canvas, offsetX + ZOMBIE_CIRCLE_RADIUS,
                offsetY + h * 0.75f, scale, zombies.get(leftBottom), ZOMBIE_DIRECTION_LEFT);

        canvas.restore();


    }


    private static void drawZombies(Canvas canvas, float x, float y,
                                    float scale, HashMap<String, Integer> count, int direction) {

        if (count == null || count.size() <= 0 || count.values().isEmpty()) {
            return;
        }
        int totalTypeCount = 0;
        for (String key : count.keySet()) {
            int countForType = count.get(key);
            if (countForType > 0) {
                ++totalTypeCount;
            }
        }

        if (totalTypeCount == 0) {
            return;
        }


        Paint paint = ZOMBIE_SPAWN_PAINT;
        paint.setColor(ZOMBIE_BG_COLOR);

        int xOffset = 0;
        int yOffset = 0;

        int offsetBetweenTypes = ZOMBIE_SPAWN_TYPE_OFFSET;

        boolean reverse = false;
        switch (direction) {
            case ZOMBIE_DIRECTION_LEFT:
                xOffset = ZOMBIE_SPAWN_LEFT_OFFSET;
                yOffset = -ZOMBIE_SPAWN_TYPE_OFFSET;
                break;
            case ZOMBIE_DIRECTION_RIGHT:
                xOffset = ZOMBIE_SPAWN_RIGHT_OFFSET;
                offsetBetweenTypes = -ZOMBIE_SPAWN_TYPE_OFFSET;
                yOffset = -ZOMBIE_SPAWN_TYPE_OFFSET;
                reverse = true;
                break;
            case ZOMBIE_DIRECTION_TOP:
                yOffset = ZOMBIE_SPAWN_TOP_OFFSET;
                break;
            case ZOMBIE_DIRECTION_BOTTOM:
                yOffset = ZOMBIE_SPAWN_BOTTOM_OFFSET;
                offsetBetweenTypes = -ZOMBIE_SPAWN_TYPE_OFFSET;
                reverse = true;
                break;
        }


        paint.setColor(ZOMBIE_RADIUS_COLOR);
        paint.setStyle(Paint.Style.STROKE);

        for (int j = 0; j <= 3; ++j) {
            canvas.drawCircle(x, y, ZOMBIE_CIRCLE_RADIUS * scale + (ZOMBIE_CIRCLE_RADIUS_INCREMENT * j), paint);
        }


        Rect rect;
        if (reverse) {
            rect = new Rect(
                    (int) (x + xOffset) - (ZOMBIE_SPAWN_ICON_MARGIN / 2),
                    (int) (y + yOffset) - (ZOMBIE_SPAWN_BOX_HEIGHT * (totalTypeCount - 1)),
                    (int) (x + xOffset) + ZOMBIE_SPAWN_BOX_WIDTH - (ZOMBIE_SPAWN_ICON_MARGIN / 2),
                    (int) (y + yOffset) + (ZOMBIE_SPAWN_BOX_HEIGHT * totalTypeCount) - (ZOMBIE_SPAWN_BOX_HEIGHT * (totalTypeCount - 1)));
        } else {
            rect = new Rect(
                    (int) (x + xOffset) - (ZOMBIE_SPAWN_ICON_MARGIN / 2),
                    (int) (y + yOffset),
                    (int) (x + xOffset) + ZOMBIE_SPAWN_BOX_WIDTH - (ZOMBIE_SPAWN_ICON_MARGIN / 2),
                    (int) (y + yOffset) + (ZOMBIE_SPAWN_BOX_HEIGHT * totalTypeCount));
        }


        canvas.drawRect(rect, ZOMBIE_SPAWN_BG_PAINT);
        canvas.drawRect(rect, ZOMBIE_SPAWN_STROKE_PAINT);


        int i = 0;
        paint.setTextSize(ZOMBIE_SPAWN_TEXT_SIZE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);


        for (String key : count.keySet()) {

            int zombieType = Integer.parseInt(key);
            int countForType = count.get(""+zombieType);
            if (countForType == 0) {
                continue;
            }

            Bitmap icon = normalZombieBitmap;
            if (zombieType == Zombie.ZOMBIE_ID_FAST) {
                icon = fastZombieBitmap;
            } else if (zombieType == Zombie.ZOMBIE_ID_FAT) {
                icon = fatZombieBitmap;
            }


            String text = String.valueOf(countForType);


            canvas.drawBitmap(icon, x + xOffset - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i), paint);
            canvas.drawText(text, x + xOffset + ZOMBIE_SPAWN_ICON_MARGIN - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i) + ZOMBIE_SPAWN_TEXT_ALIGN_OFFSET, paint);
            ++i;

        }


    }

    private static void drawPregameZombiesOneSection(Canvas canvas, float x,
                                                     float y, float scale, int count, int opponentCount, MapView mapView) {

        Paint paint = ZOMBIE_SPAWN_PAINT;
        paint.setColor(ZOMBIE_BG_COLOR);

        int offsetBetweenTypes = ZOMBIE_SPAWN_TYPE_OFFSET;


        int xOffset = ZOMBIE_SPAWN_LEFT_OFFSET;
        int yOffset = -ZOMBIE_SPAWN_TYPE_OFFSET;


        paint.setColor(ZOMBIE_RADIUS_COLOR);
        paint.setStyle(Paint.Style.STROKE);

        for (int j = 0; j <= 3; ++j) {
            canvas.drawCircle(x, y, ZOMBIE_CIRCLE_RADIUS * scale + (ZOMBIE_CIRCLE_RADIUS_INCREMENT * j), paint);
        }


        canvas.save();
        if (mapView.isFlipped()) {
            canvas.rotate(180, x, y);
        }


        Rect rect = new Rect(
                (int) (x + xOffset) - (ZOMBIE_SPAWN_ICON_MARGIN / 2),
                (int) (y + yOffset),
                (int) (x + xOffset) + ZOMBIE_SPAWN_BOX_WIDTH - (ZOMBIE_SPAWN_ICON_MARGIN / 2),
                (int) ((int) (y + yOffset) + (ZOMBIE_SPAWN_BOX_HEIGHT * 2)));


        canvas.drawRect(rect, ZOMBIE_SPAWN_BG_PAINT);
        canvas.drawRect(rect, ZOMBIE_SPAWN_STROKE_PAINT);


        int i = 0;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);


        Bitmap icon = normalZombieBitmap;


        paint.setTextSize(ZOMBIE_SPAWN_DESCRIPTION_TEXT_SIZE);
        String text = String.valueOf(count);
        canvas.drawText("you", x + xOffset + ZOMBIE_SPAWN_ICON_MARGIN - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i) + ZOMBIE_SPAWN_TEXT_ALIGN_OFFSET - ZOMBIE_SPAWN_DESCRIPTION_TEXT_OFFSET - ZOMBIE_SPAWN_DESCRIPTION_TEXT_OFFSET, paint);

        paint.setTextSize(ZOMBIE_SPAWN_TEXT_SIZE);
        canvas.drawBitmap(icon, x + xOffset - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i), paint);
        canvas.drawText(text, x + xOffset + ZOMBIE_SPAWN_ICON_MARGIN - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i) + ZOMBIE_SPAWN_TEXT_ALIGN_OFFSET + (ZOMBIE_SPAWN_OPPONENT_OFFSET / 5f), paint);
        ++i;

        icon = normalZombieSmallBitmap;

        paint.setTextSize(ZOMBIE_SPAWN_DESCRIPTION_TEXT_SIZE);
        text = String.valueOf(opponentCount);
        canvas.drawText("opponent", x + xOffset + ZOMBIE_SPAWN_ICON_MARGIN - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i) + ZOMBIE_SPAWN_TEXT_ALIGN_OFFSET - ZOMBIE_SPAWN_OPPONENT_OFFSET - ZOMBIE_SPAWN_DESCRIPTION_TEXT_OFFSET, paint);

        paint.setTextSize(ZOMBIE_SPAWN_TEXT_SIZE_OPPONENT);
        canvas.drawBitmap(icon, ZOMBIE_SPAWN_OPPONENT_OFFSET + x + xOffset - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i) + ZOMBIE_SPAWN_OPPONENT_OFFSET, paint);
        canvas.drawText(text, x + xOffset + ZOMBIE_SPAWN_ICON_MARGIN - (ZOMBIE_SPAWN_ICON_MARGIN / 2), y + yOffset + (offsetBetweenTypes * i) + ZOMBIE_SPAWN_TEXT_ALIGN_OFFSET - (ZOMBIE_SPAWN_OPPONENT_OFFSET / 5f), paint);
        ++i;

        if (mapView.isFlipped()) {
            canvas.restore();
        }

    }


    private static RectF getBoundingBox(RegionState regionState) {
        RectF boundingBox = regionBoundingBoxCache.get(regionState.getIdentifier());
        if (boundingBox == null) {
            boundingBox = calculateBoundingBox(regionState);
            regionBoundingBoxCache.put(regionState.getIdentifier(), boundingBox);
        }

        return boundingBox;
    }


    private static RectF calculateBoundingBox(RegionState regionState) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (PointState point : regionState.getPointsAsArray()) {
            minX = Math.min(minX, point.x);
            maxX = Math.max(maxX, point.x);
            minY = Math.min(minY, point.y);
            maxY = Math.max(maxY, point.y);
        }

        return new RectF(minX, minY, maxX, maxY);
    }


    public static void drawZombieHightlight(RegionState regionState, Canvas canvas, MapView mapView, Paint zombieHighlightPaint) {
        Path path = generatePath(regionState, mapView);
        canvas.drawPath(path, zombieHighlightPaint);
    }

    public static void prepareForDrawing(RegionState regionState, MapView mapView) {
        if (regionPathCache.get(regionState.getIdentifier()) == null) {
            regionPathCache.put(regionState.getIdentifier(), generatePath(regionState, mapView));
        }
    }


    public static void drawOutline(RegionState regionState, Canvas canvas, Paint outlinePaint) {
        canvas.drawPath(regionPathCache.get(regionState.getIdentifier()), outlinePaint);

    }


    public static void drawHighlightBackground(RegionState regionState, Canvas canvas, Paint highlightPaint) {
        if (!regionState.isSelected()) {
            return;
        }

        canvas.drawPath(regionPathCache.get(regionState.getIdentifier()), highlightPaint);
    }

    public static void update(RegionState regionState, MapView mapView) {
        regionPathCache.put(regionState.getIdentifier(), generatePath(regionState, mapView));

    }

    private static Path generatePath(RegionState regionState, MapView mapView) {
        float scale = mapView.getZoomScale();

        PointState lastPoint = null;

        Path path = new Path();

        for (PointState currentPoint : regionState.getPointsAsArray()) {
            if (lastPoint != null) {
                path.lineTo(scale * currentPoint.x, scale * currentPoint.y);
            } else {
                path.moveTo(scale * currentPoint.x, scale * currentPoint.y);
            }

            lastPoint = currentPoint;
        }

        path.close();

        return path;
    }

    public static boolean isPointInBoundingBox(RegionState regionState, PointState point) {
        RectF boundingBox = getBoundingBox(regionState);

        if (point == null || boundingBox == null) {
            return false;
        }

        return point.x >= boundingBox.left && point.x <= boundingBox.right
                && point.y >= boundingBox.top && point.y <= boundingBox.bottom;
    }


    public static boolean contains(RegionState regionState, PointState point) {
        if (!isPointInBoundingBox(regionState, point)) {
            return false;
        }

        boolean inRegion = false;

        for (int i = 0, j = regionState.getPointsAsArray().length - 1; i < regionState.getPointsAsArray().length; j = i++) {
            if ((regionState.getPointsAsArray()[i].y > point.y) != (regionState.getPointsAsArray()[j].y > point.y)
                    && (point.x < (regionState.getPointsAsArray()[j].x - regionState.getPointsAsArray()[i].x)
                    * (point.y - regionState.getPointsAsArray()[i].y)
                    / (regionState.getPointsAsArray()[j].y - regionState.getPointsAsArray()[i].y) + regionState.getPointsAsArray()[i].x)) {
                inRegion = !inRegion;
            }
        }

        return inRegion;
    }

}
