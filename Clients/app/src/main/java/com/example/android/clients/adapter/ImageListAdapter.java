package com.example.android.clients.adapter;

import android.content.Context;

import com.example.android.clients.R;
import com.squareup.picasso.Picasso;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ImageListAdapter extends BaseAdapter {

    private Context mContext;
    private int mLayoutId;
    private List<String> mItemTitles;
    private int[] mItemIds;
    private String[] mItemImages;

    public ImageListAdapter(Context context, int layoutId, List<String> itemTitles, int[] itemIds) {
        mContext = context;
        mLayoutId = layoutId;
        mItemTitles = itemTitles;
        mItemIds = itemIds;
    }

    public ImageListAdapter(Context context, int layoutId, List<String> itemTitles, String[] itemImages) {
        mContext = context;
        mLayoutId = layoutId;
        mItemTitles = itemTitles;
        mItemImages = itemImages;
    }

    @Override
    public int getCount() {
        if(mItemTitles == null)
            return 0;

        return mItemTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            item = inflater.inflate(mLayoutId, parent, false);
            ImageView imageView = (ImageView) item.findViewById(R.id.item_image);
            TextView textView = (TextView) item.findViewById(R.id.item_text);
            if(imageView != null) {
                if(mItemIds != null) {
                    Picasso.with(mContext).load(mItemIds[position]).placeholder(R.drawable.no_image).into(imageView);
                } else if(mItemImages != null) {
                    String imageUrl = mItemImages[position];
                    try {
                        URL url = new URL(imageUrl);
                    } catch (MalformedURLException e) {
                        // Not an URL.  It must be a local file from the data folder.
                        imageUrl = "file://" + mContext.getFilesDir() + File.separator + imageUrl;
                    }
                    Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.no_image).into(imageView);
                }
            }
            if(textView != null) {
                textView.setText(mItemTitles.get(position));
            }
        } else {
            item = (View) convertView;
        }

        return item;
    }
}
