package com.landsofruin.companion.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.cards.CardLocationsHolder;
import com.landsofruin.companion.cards.events.BlinkCardEvent;
import com.landsofruin.companion.cards.events.UpdateCardPositionToHolderEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.QueueAnimationsEvent;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.utils.UIUtils;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;

/**
 *
 */
public class NewEffectAnimationOverlayFragment extends Fragment {

    private BaseGameActivity activity;
    private ViewGroup rootView;
//    private ImageView effectView;

    private int xOffset = 0;
    private int yOffset = 0;


    private LinkedList<MultipleAnimationsHolder> pendingAnimations = new LinkedList<>();
    private LayoutInflater inflater;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;
        startNextAnimationIfNeeded();

        xOffset = (int) UIUtils.convertDpToPixel(50, activity);
        yOffset = (int) UIUtils.convertDpToPixel(-100, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.new_effect_animation_overlay_fragment,
                parent, false);

        this.inflater = inflater;

        return rootView;
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


    private void animateSequentally(final MultipleAnimationsHolder nextAnimation) {

        LinkedList<AnimationHolder> animations = nextAnimation.getAnimations();
        animateNext(animations, true);


    }


    private void animateSimultaneusly(final MultipleAnimationsHolder nextAnimation) {

        LinkedList<AnimationHolder> animations = nextAnimation.getAnimations();


        if (animations.isEmpty()) {
            startNextAnimationIfNeeded();
        }

        for (AnimationHolder anim : animations) {
            LinkedList<AnimationHolder> tmp = new LinkedList<>();
            tmp.add(anim);
            animateNext(tmp, false);
        }


    }


    private void animateNext(final LinkedList<AnimationHolder> animations, final boolean continueToNext) {

        if (animations.isEmpty()) {
            startNextAnimationIfNeeded();
            return;
        }
        final AnimationHolder nextAnimation = animations.pop();

        final ImageView effectView = (ImageView) inflater.inflate(R.layout.one_effect_animation,
                rootView, false);

        effectView.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(nextAnimation.getIconId()));
        effectView.setX(rootView.getWidth() / 2);
        effectView.setY(rootView.getHeight() / 2);
        effectView.setAlpha(0f);


        rootView.addView(effectView);
        int[] coordinates = CardLocationsHolder.getCoordinatesFor(nextAnimation.getTargetCharacter());
        if (coordinates == null) {

            coordinates = new int[2];
            coordinates[0] = 10;
            coordinates[1] = 100;
        }

        final ViewPropertyAnimator animator = effectView.animate();
        animator.alpha(1f).x(coordinates[0] + xOffset).y(coordinates[1] + yOffset).setStartDelay(100).setDuration(500).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator.setListener(null);

                final ViewPropertyAnimator innerAnimation = effectView.animate();
                innerAnimation.scaleY(0.1f).scaleX(0.1f).alpha(0.3f).setInterpolator(new AnticipateInterpolator()).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rootView.removeView(effectView);
                        BusProvider.getInstance().post(new BlinkCardEvent(nextAnimation.getTargetCharacter()));

                        if (continueToNext) {
                            animateNext(animations, continueToNext);
                        } else {
                            startNextAnimationIfNeeded();
                        }
                        innerAnimation.setListener(null);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        innerAnimation.setListener(null);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });


            }
        });
    }


    private void startNextAnimationIfNeeded() {

        if (rootView == null || pendingAnimations.isEmpty()) {
            return;
        }


        MultipleAnimationsHolder nextAnimation = pendingAnimations.pop();

        if (nextAnimation == null) {
            rootView.setVisibility(View.GONE);
            return;
        } else {
            rootView.setVisibility(View.VISIBLE);
        }


        if (nextAnimation.isSequential()) {
            animateSequentally(nextAnimation);
        } else {
            animateSimultaneusly(nextAnimation);
        }
    }

    @Subscribe
    public void onQueueAnimationsEvent(QueueAnimationsEvent event) {
        pendingAnimations.add(event.getAnimations());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BusProvider.getInstance().post(new UpdateCardPositionToHolderEvent());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startNextAnimationIfNeeded();
                    }
                }, 300);

            }
        }, 500);
    }

}
