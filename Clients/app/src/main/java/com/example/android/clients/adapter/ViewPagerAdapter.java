package com.example.android.clients.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Pair;

import static com.example.android.clients.util.LogUtils.makeLogTag;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = makeLogTag(ViewPagerAdapter.class);

    // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    // and the names of the corresponding fragments
    private Pair<String, String>[] mTitlesAndFragments;

    private Context mContext;

    // Build the default constructor
    public ViewPagerAdapter(FragmentManager fm, Context context, String[] titles, String[] fragmentClassNames) {
        super(fm);
        mContext = context;
        mTitlesAndFragments = new Pair[titles.length];
        for(int i = 0; i < titles.length; ++ i) {
            String frName = null;
            if(fragmentClassNames != null && fragmentClassNames.length > i)
                frName = fragmentClassNames[i];
            mTitlesAndFragments[i] = new Pair(titles[i], frName);
        }
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        if(mTitlesAndFragments == null)
            return null;

        if(position >= mTitlesAndFragments.length)
            return null;

        String fragmentClassName = mTitlesAndFragments[position].second;
        if(fragmentClassName == null)
            return null;

        try {
            return Fragment.instantiate(mContext, fragmentClassName);
        } catch (Fragment.InstantiationException ex) {
            return null;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public String getPageTitle(int position) {
        if(mTitlesAndFragments == null)
            return null;

        if(position >= mTitlesAndFragments.length)
            return null;

        return mTitlesAndFragments[position].first;
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        if(mTitlesAndFragments != null)
            return mTitlesAndFragments.length;
        else
            return 0;
    }
}
