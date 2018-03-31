package com.landsofruin.companion.battlereport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.landsofruin.gametracker.R;

public class BattleReportTabsActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private String battleReportId;


    public static void startNewActivity(Context context, String battleReportId) {

        Intent intent = new Intent(context, BattleReportTabsActivity.class);
        intent.putExtra("battleReportId", battleReportId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_report_tabs);

        battleReportId = getIntent().getStringExtra("battleReportId");
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), battleReportId);

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        private String battleReportId;

        public SectionsPagerAdapter(FragmentManager fm, String battleReportId) {
            super(fm);
            this.battleReportId = battleReportId;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentBattleReportOverview.newInstance(this.battleReportId);
                case 1:
                    return FragmentBattleReportCharacters.newInstance(this.battleReportId);
                case 2:
                    return FragmentBattleReportTimeline.newInstance(this.battleReportId);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Overview";
                case 1:
                    return "Characters";
                case 2:
                    return "Timeline";
            }
            return null;
        }
    }

}
