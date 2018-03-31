package com.landsofruin.companion.fragment;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.NewGameLogItemEvent;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class GameLogOverlayFragment extends Fragment {


    private LayoutInflater inflater;

    private ViewGroup logsContainer;

    private View expandedView;
    private ListView expandedViewLogsContainer;

    private LinkedList<GameLogItem> lastItems = new LinkedList<>();
    private ListArrayAdapter adapter;
    private View closeButton;
    private ViewPropertyAnimator buttonAnimation;
    private boolean buttonAnimationInterrupted = false;
    private View logContainerBg;


    private boolean isTipShown() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean("log_tip_shown", false);
    }

    private void setTipShown() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("log_tip_shown", true);
        editor.apply();
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.gamelog_overlay_fragment,
                parent, false);

        logsContainer = (ViewGroup) rootView.findViewById(R.id.logs_container);
        expandedView = rootView.findViewById(R.id.expanded_view);


        expandedViewLogsContainer = (ListView) rootView.findViewById(R.id.expanded_logs_container);

        logContainerBg = rootView.findViewById(R.id.logs_container_background);


        closeButton = rootView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideExtendedContainer(true);
            }
        });

        adapter = new ListArrayAdapter();
        expandedViewLogsContainer.setAdapter(adapter);

        this.inflater = inflater;


        logContainerBg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showExtendedContainer();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideExtendedContainer(false);

                }

                return false;
            }
        });

        return rootView;
    }


    private boolean expandedViewLocked = false;

    private void showExtendedContainer() {


        if (!isTipShown()) {
            Toast.makeText(getActivity(), "Long press to lock the lock the log open.", Toast.LENGTH_LONG).show();
        }


        closeButton.setAlpha(0f);

        final ViewPropertyAnimator animator = closeButton.animate();
        buttonAnimation = animator.setStartDelay(700).setDuration(500).alpha(1f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                buttonAnimationInterrupted = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (!buttonAnimationInterrupted) {
                    expandedViewLocked = true;
                    buttonAnimation = null;
                    setTipShown();
                }

                animator.setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                expandedViewLocked = false;
                buttonAnimation = null;
                animator.setListener(null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        adapter.setObjects(this.lastItems);

        expandedViewLogsContainer.smoothScrollToPosition(0);

        expandedView.setVisibility(View.VISIBLE);

        expandedView.animate().scaleY(1f).alpha(1);

    }

    private void hideExtendedContainer(boolean fromButton) {

        if (!fromButton && expandedViewLocked) {
            return;
        }


        if (buttonAnimation != null) {
            buttonAnimationInterrupted = true;
            buttonAnimation.cancel();
        }

        expandedViewLocked = false;
        final ViewPropertyAnimator animator = expandedView.animate();
        animator.scaleY(0.05f).alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                expandedView.setVisibility(View.GONE);
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

    private long lastLogAdded = System.currentTimeMillis();
    private View previousLogView = null;

    private boolean queueAlreadyBeingHandled = false;

    private LinkedList<GameLogItem> queue = new LinkedList<>();

    private void handleQueue() {
        synchronized (this) {
            if (queueAlreadyBeingHandled) {
                return;
            }


            queueAlreadyBeingHandled = true;
            new Thread(new Runnable() {
                @Override
                public void run() {


                    while (!queue.isEmpty()) {

                        final GameLogItem item = queue.pop();


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lastItems.add(0, item);
                                adapter.notifyDataSetChanged();
                            }
                        });


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View logItem = inflater.inflate(R.layout.gamelog_item, logsContainer, false);

                                ((TextView) logItem.findViewById(R.id.title)).setText(item.getShortText());
                                ((ImageView) logItem.findViewById(R.id.icon)).setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(item.getIconResourceId()));

                                logsContainer.addView(logItem);

                                long timediff = System.currentTimeMillis() - lastLogAdded;


                                timediff = 2000 - timediff;
                                if (timediff < 0) {
                                    timediff = 0;
                                }

                                lastLogAdded = System.currentTimeMillis() + timediff;


                                if (previousLogView != null) {
                                    final View thisPreviousView = previousLogView;

                                    final ViewPropertyAnimator animator = thisPreviousView.animate();
                                    animator.alpha(0f).setInterpolator(new AccelerateInterpolator()).scaleY(0f).scaleX(0f).setStartDelay(timediff).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                TransitionManager.beginDelayedTransition(logsContainer);
                                            }

                                            logsContainer.removeView(thisPreviousView);
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

                                previousLogView = logItem;

                            }
                        });


                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    queueAlreadyBeingHandled = false;
                }
            }).start();

        }
    }


    @Subscribe
    public void onNewGameLogItemEvent(NewGameLogItemEvent event) {
        queue.add(event.getItem());
        handleQueue();


    }


    private class ListArrayAdapter extends BaseAdapter {

        private List<GameLogItem> objects = new LinkedList<>();


        public void setObjects(List<GameLogItem> objects) {
            this.objects = objects;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int arg0, View logItemView, ViewGroup arg2) {

            if (logItemView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                logItemView = inflater.inflate(R.layout.extended_gamelog_item, arg2, false);
            }


            GameLogItem logItem = objects.get(arg0);


            ((TextView) logItemView.findViewById(R.id.title)).setText(logItem.getShortText());
            ((ImageView) logItemView.findViewById(R.id.icon)).setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(logItem.getIconResourceId()));


            if (logItem.getLongText() != null) {
                logItemView.findViewById(R.id.description).setVisibility(View.VISIBLE);
                ((TextView) logItemView.findViewById(R.id.description)).setText(logItem.getLongText());
            } else {
                logItemView.findViewById(R.id.description).setVisibility(View.GONE);
            }


            ImageView targetCharacterPic = ((ImageView) logItemView.findViewById(R.id.target_character_pic));
            if (logItem.getTargetCharacterState() == null) {
                targetCharacterPic.setVisibility(View.GONE);
            } else {
                targetCharacterPic.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(logItem.getTargetCharacterState().getProfilePictureUri()).into(targetCharacterPic);
            }

            ImageView characterPic = ((ImageView) logItemView.findViewById(R.id.character_pic));
            if (logItem.getCharacterState() == null) {
                characterPic.setVisibility(View.GONE);
            } else {
                characterPic.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(logItem.getCharacterState().getProfilePictureUri()).into(characterPic);
            }


            return logItemView;
        }


    }
}
