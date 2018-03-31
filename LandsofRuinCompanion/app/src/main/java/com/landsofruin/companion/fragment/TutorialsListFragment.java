package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.ReplaceTutorialEvent;
import com.landsofruin.companion.tutorial.TutorialUtils;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.utils.SimpleBindingAdapter;

/**

 */
public class TutorialsListFragment extends ListFragment {


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BusProvider.getInstance().post(new ReplaceTutorialEvent(i +2));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorials_list_fragment,
                parent, false);

        this.setListAdapter(new TutorialsListAdapter());

        return view;
    }

    private class TutorialsListAdapter extends SimpleBindingAdapter {


        @Override
        public View newView(ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.one_tutorial_item, parent, false);
            return v;

        }

        @Override
        public void bindView(int position, View view) {

            String key = TutorialUtils.getInstance(getActivity()).getAllTutorialKeys().get(position +2);
            ((TextView) view.findViewById(R.id.tutorial_name)).setText(TutorialUtils.getInstance(getActivity()).getTitleFor(key));
        }

        @Override
        public int getCount() {
            return TutorialUtils.getInstance(getActivity()).getAllTutorialKeys().size() -2;
        }

        @Override
        public Object getItem(int i) {
            return TutorialUtils.getInstance(getActivity()).getAllTutorialKeys().get(i + 2);
        }

        @Override
        public long getItemId(int i) {
            return i + 2;
        }
    }


}
