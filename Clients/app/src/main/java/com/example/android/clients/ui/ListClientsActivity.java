package com.example.android.clients.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.android.clients.R;
import com.example.android.clients.adapter.ViewPagerAdapter;
import com.example.android.clients.ui.widget.SlidingTabLayout;

import static com.example.android.clients.util.LogUtils.makeLogTag;

public class ListClientsActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ListClientsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore the saved state if exists
        super.onCreate(savedInstanceState);

        // Set content view
        setContentView(R.layout.activity_list_clients);

        // Initialize UI elements
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_empty);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        String[] tabTitles = getResources().getStringArray(R.array.client_types_array);
        String[] pagerFragmentClasses = new String[]{
                DisplayCurrentClientsFragment.class.getCanonicalName(),
                DisplayClientProspectsFragment.class.getCanonicalName()
        };

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        ViewPagerAdapter viewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), this, tabTitles, pagerFragmentClasses);

        // Assigning ViewPager View and setting the adapter
        ViewPager viewPager = (ViewPager) findViewById(R.id.clients_list_pager);
        viewPager.setAdapter(viewPagerAdapter);

        // Assiging the Sliding Tab Layout View
        SlidingTabLayout slidingTabs = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabs.setDistributeEvenly(true);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        slidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(R.color.active_tab_color);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabs.setViewPager(viewPager);
    }
}
