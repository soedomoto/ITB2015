package com.soedomoto.sakernas.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.questionaire.QBlockI;
import com.soedomoto.sakernas.questionaire.QBlockIV;
import com.soedomoto.sakernas.questionaire.QBlockVA;
import com.soedomoto.sakernas.questionaire.QBlockVB;
import com.soedomoto.sakernas.questionaire.QBlockVC;
import com.soedomoto.sakernas.questionaire.QBlockVD;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class QuestionaireARTActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionaire_art_activity);

        configureDrawer();
        configureTabQuestionaireBlock();
    }

    private void configureDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.application_toolbar_art);

        //set the Toolbar as ActionBar
        setSupportActionBar(toolbar);

        // getSupportActionBar().setNavigationIcon(R.drawable.left_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureTabQuestionaireBlock() {
        final MaterialTabHost tabQuest = (MaterialTabHost) findViewById(R.id.tab_questionaire_art);

        final ViewPager tabQuestContent = (ViewPager) findViewById(R.id.tab_questionaire_art_content);
        TabQuestionaireContentAdapter tabQuestContentAdapter =
                new TabQuestionaireContentAdapter(getSupportFragmentManager());
        tabQuestContent.setAdapter(tabQuestContentAdapter);

        //when the page changes in the ViewPager, update the Tabs accordingly
        tabQuestContent.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabQuest.setSelectedNavigationItem(position);
            }
        });

        //Add all the Tabs to the TabHost
        for (int i = 0; i < tabQuestContentAdapter.getCount(); i++) {
            MaterialTab newTab = tabQuest.newTab()
                    .setText(tabQuestContentAdapter.getPageTitle(i))
                    //.setIcon(tabQuestContentAdapter.getIcon(i))                    .
                    .setTabListener(new MaterialTabListener() {
                        @Override
                        public void onTabSelected(MaterialTab tab) {
                            tabQuestContent.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabReselected(MaterialTab tab) {
                        }

                        @Override
                        public void onTabUnselected(MaterialTab tab) {
                        }
                    });

            tabQuest.addTab(newTab);
        }
    }

    private class TabQuestionaireContentAdapter extends FragmentStatePagerAdapter {
        int questionaire_tabs_art_icons[] = {
                R.drawable.ic_action_search,
                R.drawable.ic_action_search,
                R.drawable.ic_action_search,
                R.drawable.ic_action_search,
                R.drawable.ic_action_search,
                R.drawable.ic_action_search,
                R.drawable.ic_action_search,
        };

        FragmentManager fragmentManager;

        public TabQuestionaireContentAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        public Fragment getItem(int num) {
            Fragment fragment = null;

            switch (num) {
                case 0:
                    fragment = QBlockIV.newInstance();
                    break;
                case 1:
                    fragment = QBlockVA.newInstance();
                    break;
                case 2:
                    fragment = QBlockVB.newInstance();
                    break;
                case 3:
                    fragment = QBlockVC.newInstance();
                    break;
                case 4:
                    fragment = QBlockVD.newInstance();
                    break;
                case 5:
                    fragment = QBlockI.newInstance();
                    break;
                case 6:
                    fragment = QBlockI.newInstance();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.questionaire_tabs_art).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.questionaire_tabs_art)[position];
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(questionaire_tabs_art_icons[position]);
        }
    }
}
