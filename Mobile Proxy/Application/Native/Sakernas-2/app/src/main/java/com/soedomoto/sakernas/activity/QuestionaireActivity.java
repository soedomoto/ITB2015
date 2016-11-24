package com.soedomoto.sakernas.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.widget.QDrawer;
import com.soedomoto.sakernas.questionaire.QBlockI;
import com.soedomoto.sakernas.questionaire.QBlockII_III;
import com.soedomoto.sakernas.questionaire.QBlockIVList;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class QuestionaireActivity extends AppCompatActivity {

    private MaterialTabHost tabQuest;
    private ViewPager tabQuestContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionaire_activity);

        configureDrawer();
        configureTabQuestionaireBlock();
    }

    private void configureDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.application_toolbar_ruta);

        //set the Toolbar as ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //setup the NavigationDrawer
        QDrawer drawer = (QDrawer) getSupportFragmentManager().findFragmentById(R.id.qdrawer);
        drawer.setUp(R.id.qdrawer, (DrawerLayout) findViewById(R.id.questionaire_activity), toolbar);
    }

    private void configureTabQuestionaireBlock() {
        tabQuest = (MaterialTabHost) findViewById(R.id.tab_questionaire_ruta);

        tabQuestContent = (ViewPager) findViewById(R.id.tab_questionaire_ruta_content);
        TabQuestionaireContentAdapter tabQuestContentAdapter = new TabQuestionaireContentAdapter(getSupportFragmentManager());
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
        int questionaire_tab_icons[] = {
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
                    fragment = QBlockI.newInstance();
                    break;
                case 1:
                    fragment = QBlockII_III.newInstance();
                    break;
                case 2:
                    fragment = QBlockIVList.newInstance();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.questionaire_tabs).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.questionaire_tabs)[position];
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(questionaire_tab_icons[position]);
        }
    }
}
