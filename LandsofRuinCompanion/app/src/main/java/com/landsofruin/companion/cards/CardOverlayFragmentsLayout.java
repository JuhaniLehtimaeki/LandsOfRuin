package com.landsofruin.companion.cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.landsofruin.companion.cards.events.CardHorizontalMarginChangedEvent;
import com.landsofruin.companion.cards.events.CardMarginChangedEvent;
import com.landsofruin.companion.cards.events.EnemySingleCardModeSelectedEvent;
import com.landsofruin.companion.cards.events.SingleCardModeSelectedEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.utils.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.LinkedList;

public class CardOverlayFragmentsLayout extends FrameLayout {

    private static final int ONE_CARD_WIDTH = 825;//dp
    private static final int ONE_CARD_HEIGHT = 310;//dp
    private static final float ONE_CARD_WIDTH_PORTRAIT = 215.5f;//dp

    private static final int HORIZONTAL_SPREAD_TOP_Y = 100;//dp

    private static final int STACK_OFFSET = -130;//dp

    private float oneCardWidth;
    private float oneCardWidthPortrait;
    private float oneCardHeight;
    private int stackOffset;

    private int offset20; //10dp
    private int offset10; //5dp

    private int horizontalSpreadTopY;


    private LinkedList<View> order = new LinkedList<>();
    private HashMap<View, LocationPoint> locations = new HashMap<>();
    private HashMap<CharacterCardFragment, View> characterToView = new HashMap<>();


    private boolean isEnemyCardLayout = false;
    private CharacterCardFragment currentInFront = null;
    private boolean inScreenTakeOverMode = false;

    private boolean useElevation = false;

    public CardOverlayFragmentsLayout(Context context) {
        this(context, null);
        initDimensions();
    }

    public CardOverlayFragmentsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initDimensions();
    }

    public CardOverlayFragmentsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDimensions();
    }

    protected void setIsEnemyLayout() {
        isEnemyCardLayout = true;
    }

    private void initDimensions() {
        oneCardWidth = (int) UIUtils.convertDpToPixel(ONE_CARD_WIDTH, getContext());
        oneCardWidthPortrait = (int) UIUtils.convertDpToPixel(ONE_CARD_WIDTH_PORTRAIT, getContext());
        oneCardHeight = (int) UIUtils.convertDpToPixel(ONE_CARD_HEIGHT, getContext());
        stackOffset = (int) UIUtils.convertDpToPixel(STACK_OFFSET, getContext());

        horizontalSpreadTopY = (int) UIUtils.convertDpToPixel(HORIZONTAL_SPREAD_TOP_Y, getContext());

        offset10 = (int) UIUtils.convertDpToPixel(5, getContext());
        offset20 = (int) UIUtils.convertDpToPixel(10, getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            useElevation = true;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); ++i) {
            order.add(getChildAt(i));
        }
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.getInstance().unregister(this);
    }

    public void bringFront(CharacterCardFragment card) {
        currentInFront = card;
    }

    public void setupChildrenOrderTracking(LinkedList<CharacterCardFragment> characterCards) {
//        if (!order.isEmpty()) {
//            return;
//        }

        if (characterCards != null) {
            order.clear();
            characterToView.clear();
            for (int i = 0; i < getChildCount() && i < characterCards.size(); ++i) {
                order.add(getChildAt(i));
                characterToView.put(characterCards.get(i), getChildAt(i));

            }
        }
    }

    public boolean consumeBackPress() {

        if (inScreenTakeOverMode) {
            goBackFromScreenTakeOverMode(stackX, stackY);
            return true;
        }

        return false;
    }

    public void showHideOverScreenAndSpreadCards(int x, int y) {

        if (inScreenTakeOverMode) {
            goBackFromScreenTakeOverMode(x, y);
        } else {
            takeOverScreenAndSpreadCards(x, y);
        }
    }

    public void takeOverScreenAndSpreadCards(final int x, final int y) {
        inScreenTakeOverMode = true;
        animateToHorizontalSpread();

        this.setBackgroundColor(0xCC000000);
        BusProvider.getInstance().post(new SingleCardModeSelectedEvent());

        setClickable(true);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackFromScreenTakeOverMode(x, y);
            }
        });
    }

    private void goBackFromScreenTakeOverMode(int x, int y) {
        inScreenTakeOverMode = false;
        setBackgroundColor(0x00000000);

        setClickable(false);
        animateToStack(x, y);
    }


    public void animateToSide() {

        animateToLocations();

        BusProvider.getInstance().post(new EnemySingleCardModeSelectedEvent());

    }

    public void animateToLocations() {


        int divider = (order.size() - 1);
        if (divider <= 0) {
            divider = 1;
        }

        float spreadCardMargin = (getHeight() - oneCardHeight) / divider - offset20;

        Log.d("layoutdebug", "animateToLocations");

        float cumulativeY = offset20 + (spreadCardMargin * (order.size() - 1));

        float leftMargin = offset10;
        if (isEnemyCardLayout) {
            leftMargin = this.getWidth() - (oneCardWidth) - offset10;
        }
        int leftPool = offset20;

        int delayTimes = 0;

        for (int i = order.size() - 1; i >= 0; --i) {
            View view = order.get(i);

            view.animate().scaleX(1f).scaleY(1f);


            if (view == null) {
                Log.e("Card Layout", "Failed to layout cards. View order not correctly initialised. order:" + order);

                continue;
            }
            float targetY = cumulativeY;

            boolean wasAnimated = false;

            if (view.getTranslationY() != targetY) {
                view.animate().translationY(targetY).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }

            float leftX = leftPool + leftMargin;


            if (view.getTranslationX() != leftX) {
                view.animate().translationX(leftX).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }
            locations.put(view, new LocationPoint(leftX, targetY));

            if (view.getScaleX() != 1f) {
                view.animate().scaleX(1f).scaleY(1f).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }


            cumulativeY -= spreadCardMargin;

            if (wasAnimated) {
                ++delayTimes;
            }
        }

        fixDrawOrder();

        if (characterToView.get(currentInFront) != null) {
            characterToView.get(currentInFront).animate().scaleX(1.05f).scaleY(1.05f);
        }else {
        }



    }


    public void animateToHorizontalSpread() {


        int divider = (order.size() - 1);
        if (divider <= 0) {
            divider = 1;
        }

        float spreadCardMargin = (getWidth() - oneCardWidthPortrait) / divider - offset20;

        Log.d("layoutdebug", "animateToHorizontalSpread");

        float cumulativeX = offset20 + (spreadCardMargin * (order.size() - 1));


        int delayTimes = 0;


        for (int i = order.size() - 1; i >= 0; --i) {
            View view = order.get(i);

            view.animate().scaleX(1f).scaleY(1f);

            if (view == null) {
                Log.e("Card Layout", "Failed to layout cards. View order not correctly initialised. order:" + order);

                continue;
            }
            float targetX = cumulativeX;

            boolean wasAnimated = false;

            if (view.getY() != horizontalSpreadTopY) {
                view.animate().y(horizontalSpreadTopY).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }


            if (view.getTranslationX() != cumulativeX) {
                view.animate().translationX(cumulativeX).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }
            locations.put(view, new LocationPoint(targetX, horizontalSpreadTopY));

            if (view.getScaleX() != 1f) {
                view.animate().scaleX(1f).scaleY(1f).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }


            cumulativeX -= spreadCardMargin;

            if (wasAnimated) {
                ++delayTimes;
            }
        }
        if (characterToView.get(currentInFront) != null) {
            characterToView.get(currentInFront).animate().scaleX(1.05f).scaleY(1.05f);
        }else {
        }

        fixDrawOrder();

    }


    private int stackX = 0;
    private int stackY = 0;

    public void animateToStack(int x, int y) {
        stackX = x;
        stackY = y;
        Log.d("layoutdebug", "animateToStack");
        int delayTimes = 0;

        for (int i = 0; i < order.size(); ++i) {
            View view = order.get(i);

            boolean wasAnimated = false;

            if (view == null) {
                continue;
            }

            view.setPivotY(0);
            view.setPivotX(0);
            if (view.getY() != y) {

                view.animate().y(y).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }

            if (view.getX() != x) {
                view.animate().x(x).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }
            locations.put(view, new LocationPoint(x, y));

            if (view.getScaleX() != 0.1f) {
                view.animate().scaleX(0.1f).scaleY(0.1f).setStartDelay(50 * delayTimes);
                wasAnimated = true;
            }


            if (wasAnimated) {
                ++delayTimes;
            }
        }
        fixDrawOrder();

    }


    @SuppressLint("NewApi")
    private void fixDrawOrder() {

        int currentElevation = offset10;

        int selectedLocation = -1;
        for (int i = 0; i < order.size(); ++i) {
            View view = order.get(i);
            if (view.equals(characterToView.get(currentInFront))) {
                selectedLocation = i;
                break;
            } else {
                if (view != null && view.getParent() != null) {
                    view.getParent().bringChildToFront(view);

                    if (useElevation) {
                        view.setElevation(currentElevation);
                    }

                    currentElevation += offset10;
                }
            }
        }


        if (selectedLocation >= 0) {
            for (int i = order.size() - 1; i >= selectedLocation; --i) {
                View view = order.get(i);
                view.getParent().bringChildToFront(view);

                if (useElevation) {
                    view.setElevation(currentElevation);
                }

                currentElevation += offset10;
            }
        }

    }

    @Subscribe
    public void onCardMarginChangedEvent(CardMarginChangedEvent event) {

        Log.d("dragging", "speed: " + event.getSpeed());

    }

    @Subscribe
    public void onCardHorizontalMarginChangedEvent(CardHorizontalMarginChangedEvent event) {

    }

    public class LocationPoint {
        float x, y;


        public LocationPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

    }


}
