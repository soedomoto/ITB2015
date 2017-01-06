package com.soedomoto.sakernas.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.model.ART;
import com.soedomoto.sakernas.questionaire.QBlockI;
import com.soedomoto.sakernas.questionaire.QBlockIV;
import com.soedomoto.sakernas.questionaire.QBlockVA;
import com.soedomoto.sakernas.questionaire.QBlockVB;
import com.soedomoto.sakernas.questionaire.QBlockVC;
import com.soedomoto.sakernas.questionaire.QBlockVD;
import com.soedomoto.sakernas.questionaire.QBlockVE;
import com.soedomoto.sakernas.questionaire.QBlockVF;
import com.soedomoto.sakernas.questionaire.QBlockVG;
import com.soedomoto.sakernas.questionaire.QBlockVH;
import com.soedomoto.sakernas.widget.QEditText;
import com.soedomoto.sakernas.widget.QRadioButton;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class QuestionaireARTActivity extends AppCompatActivity {
    private ART art;

    private MaterialTabHost tabQuest;
    private TabQuestionaireContentAdapter tabQuestContentAdapter;
    private ViewPager tabQuestContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionaire_art_activity);

        configureDrawer();
        configureTabQuestionaireBlock();

        //  Handle Data
        Intent i = getIntent();
        art = (ART) i.getSerializableExtra("art");

        findViewById(R.id.fab_save_art).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                QuestionaireARTActivity.this.onBackPressed();
            }
        });
    }

    private void load() {

    }

    private void save() {
        QBlockIV qBlockIV = (QBlockIV) tabQuestContentAdapter.getLoadedItem(0);
        QBlockVA qBlockVA = (QBlockVA) tabQuestContentAdapter.getLoadedItem(1);
        QBlockVB qBlockVB = (QBlockVB) tabQuestContentAdapter.getLoadedItem(2);
        QBlockVC qBlockVC = (QBlockVC) tabQuestContentAdapter.getLoadedItem(3);



        QBlockVD qBlockVD = (QBlockVD) tabQuestContentAdapter.getLoadedItem(4);
        art.setB5r19(cast(qBlockVD.getB5r19()));
        art.setB5r20(cast(qBlockVD.getB5r20()));
        art.setB5r21a(cast(qBlockVD.getB5r21a()));
        art.setB5r21atahun(cast(qBlockVD.getB5r21atahun()));
        art.setB5r21abulan(cast(qBlockVD.getB5r21abulan()));
        art.setB5r21b(cast(qBlockVD.getB5r21b()));
        art.setB5r22asen(cast(qBlockVD.getB5r22asen()));
        art.setB5r22asel(cast(qBlockVD.getB5r22asel()));
        art.setB5r22arab(cast(qBlockVD.getB5r22arab()));
        art.setB5r22akam(cast(qBlockVD.getB5r22akam()));
        art.setB5r22ajum(cast(qBlockVD.getB5r22ajum()));
        art.setB5r22asab(cast(qBlockVD.getB5r22asab()));
        art.setB5r22amin(cast(qBlockVD.getB5r22amin()));
        art.setB5r22b(cast(qBlockVD.getB5r22b()));
        art.setB5r23(cast(qBlockVD.getB5r23()));
        art.setB5r24(cast(qBlockVD.getB5r24()));
        art.setB5r25(cast(qBlockVD.getB5r25()));
        art.setB5r26uang(cast(qBlockVD.getB5r26uang()));
        art.setB5r26barang(cast(qBlockVD.getB5r26barang()));
        art.setB5r27(cast(qBlockVD.getB5r27()));
        art.setB5r28a(cast(qBlockVD.getB5r28a()));
        art.setB5r28b(cast(qBlockVD.getB5r28b()));
        art.setB5r28c(cast(qBlockVD.getB5r28c()));
        art.setB5r28d(cast(qBlockVD.getB5r28d()));
        art.setB5r28e(cast(qBlockVD.getB5r28e()));
        art.setB5r28f(cast(qBlockVD.getB5r28f()));
        art.setB5r28g(cast(qBlockVD.getB5r28g()));
        art.setB5r29(cast(qBlockVD.getB5r29()));
        art.setB5r30(cast(qBlockVD.getB5r30()));
        art.setB5r31(cast(qBlockVD.getB5r31()));
        art.setB5r31lainnya(qBlockVD.getB5r31lainnya());
        art.setB5r32(cast(qBlockVD.getB5r32()));
        art.setB5r32lainnya(qBlockVD.getB5r32lainnya());
        art.setB5r33aprov(qBlockVD.getB5r33aprov());
        art.setB5r33akab(qBlockVD.getB5r33akab());
        art.setB5r33b(cast(qBlockVD.getB5r33b()));
        art.setB5r33c(cast(qBlockVD.getB5r33c()));
        art.setB5r33d(cast(qBlockVD.getB5r33d()));
        art.setB5r33e(cast(qBlockVD.getB5r33e()));

        //  Blok V.E
        QBlockVE qBlockVE = (QBlockVE) tabQuestContentAdapter.getLoadedItem(5);
        art.setB5r34(cast(qBlockVE.getB5r34()));
        art.setB5r35(cast(qBlockVE.getB5r35()));
        art.setB5r36(cast(qBlockVE.getB5r36()));

        //  Blok V.F
        QBlockVF qBlockVF = (QBlockVF) tabQuestContentAdapter.getLoadedItem(6);
        art.setB5r37asen(cast(qBlockVF.getB5r37asen()));
        art.setB5r37asel(cast(qBlockVF.getB5r37asel()));
        art.setB5r37arab(cast(qBlockVF.getB5r37arab()));
        art.setB5r37akam(cast(qBlockVF.getB5r37akam()));
        art.setB5r37ajum(cast(qBlockVF.getB5r37ajum()));
        art.setB5r37asab(cast(qBlockVF.getB5r37asab()));
        art.setB5r37amin(cast(qBlockVF.getB5r37amin()));
        // art.setB5r37ajumlah(cast(qBlockVF.b5r37ajumlah()));
        art.setB5r37b(cast(qBlockVF.getB5r37b()));
        art.setB5r38a(cast(qBlockVF.getB5r38a()));
        art.setB5r38b(cast(qBlockVF.getB5r38b()));
        art.setB5r39(cast(qBlockVF.getB5r39()));

        //  Blok V.G
        QBlockVG qBlockVG = (QBlockVG) tabQuestContentAdapter.getLoadedItem(7);
        art.setB5r40(cast(qBlockVG.getB5r40()));
        art.setB5r41(cast(qBlockVG.getB5r41()));
        art.setB5r42(cast(qBlockVG.getB5r42()));
        art.setB5r42lainnya(qBlockVG.getB5r42lainnya());
        art.setB5r43(cast(qBlockVG.getB5r43()));
        art.setB5r44(cast(qBlockVG.getB5r44()));
        art.setB5r45(cast(qBlockVG.getB5r45()));
        art.setB5r45negara(qBlockVG.getB5r45negara());

        //  Blok V.H
        QBlockVH qBlockVH = (QBlockVH) tabQuestContentAdapter.getLoadedItem(8);
        art.setB5r46(cast(qBlockVH.getB5r46()));
        art.setB5r47a(cast(qBlockVH.getB5r47a()));
        art.setB5r47b(cast(qBlockVH.getB5r47b()));
        art.setB5r47c(cast(qBlockVH.getB5r47c()));
        art.setB5r47d(cast(qBlockVH.getB5r47d()));
        art.setB5r48a(cast(qBlockVH.getB5r48a()));
        art.setB5r48b(cast(qBlockVH.getB5r48b()));
        art.setB5r49(cast(qBlockVH.getB5r49()));
        art.setB5r50(cast(qBlockVH.getB5r50()));
        art.setB5r51(cast(qBlockVH.getB5r51()));
        art.setB5r52(cast(qBlockVH.getB5r52()));
    }

    private Integer cast(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {}

        return null;
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("art", art);
        setResult(Activity.RESULT_OK, data);

        finish();
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
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureTabQuestionaireBlock() {
        tabQuest = (MaterialTabHost) findViewById(R.id.tab_questionaire_art);
        tabQuestContent = (ViewPager) findViewById(R.id.tab_questionaire_art_content);

        tabQuestContentAdapter = new TabQuestionaireContentAdapter(getSupportFragmentManager());
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
                    public void onTabReselected(MaterialTab tab) {}

                    @Override
                    public void onTabUnselected(MaterialTab tab) {}
                });

            tabQuest.addTab(newTab);
        }
    }

    private class TabQuestionaireContentAdapter extends FragmentStatePagerAdapter {

        private int questionaire_tabs_art_icons[] = {
            R.drawable.ic_action_search, R.drawable.ic_action_search, R.drawable.ic_action_search,
            R.drawable.ic_action_search, R.drawable.ic_action_search, R.drawable.ic_action_search,
            R.drawable.ic_action_search, R.drawable.ic_action_search, R.drawable.ic_action_search,
        };

        private SparseArray<Fragment> fragments;

        public TabQuestionaireContentAdapter(FragmentManager fm) {
            super(fm);

            fragments = new SparseArray<Fragment>(getCount());
            fragments.put(0, QBlockIV.newInstance());
            fragments.put(1, QBlockVA.newInstance());
            fragments.put(2, QBlockVB.newInstance());
            fragments.put(3, QBlockVC.newInstance());
            fragments.put(4, QBlockVD.newInstance());
            fragments.put(5, QBlockVE.newInstance());
            fragments.put(6, QBlockVF.newInstance());
            fragments.put(7, QBlockVG.newInstance());
            fragments.put(8, QBlockVH.newInstance());
        }

        public Fragment getLoadedItem(int num) {
            tabQuestContent.setCurrentItem(num);
            return getItem(num);
        }

        public Fragment getItem(int num) {
            return fragments.get(num, null);
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
