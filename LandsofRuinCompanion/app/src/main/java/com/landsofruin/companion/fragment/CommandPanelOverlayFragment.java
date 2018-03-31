package com.landsofruin.companion.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;

import com.landsofruin.companion.state.CharacterState;

/**
 * Created by juhani on 09/10/15.
 */
public abstract class CommandPanelOverlayFragment extends Fragment {
    protected boolean manuallyHidden = true;
    private CharacterState character;


    public void setCharacter(CharacterState character) {
        this.character = character;
    }

    public CharacterState getCharacter() {
        return character;
    }


    abstract View getChangeOverlayView();

    abstract void updateUI();

    abstract View getRootView();


    void hideOverlay() {
        hideOverlay(null);
    }

    void hideOverlay(final CharacterState setToCharacterAfter) {
        if (getRootView().getVisibility() == View.GONE) {
            if (setToCharacterAfter != null) {
                character = setToCharacterAfter;
                updateUI();
            }
            return;
        }

        getChangeOverlayView().setAlpha(0f);
        getChangeOverlayView().setVisibility(View.VISIBLE);

        final ViewPropertyAnimator animator = getChangeOverlayView().animate();
        animator.alpha(1f).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getRootView().setVisibility(View.GONE);
                if (setToCharacterAfter != null) {
                    character = setToCharacterAfter;
                    updateUI();
                }
                animator.setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animator.setListener(null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        } );


    }


    void showOverlay() {
        if (getRootView().getVisibility() == View.VISIBLE) {
            return;
        }
        getChangeOverlayView().setAlpha(1f);
        getChangeOverlayView().setVisibility(View.VISIBLE);

        getRootView().setVisibility(View.VISIBLE);


        final ViewPropertyAnimator animator = getChangeOverlayView().animate();
        animator.alpha(0).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getChangeOverlayView().setVisibility(View.GONE);
                animator.setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animator.setListener(null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }
}
