package com.example.android.clients.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.clients.R;
import com.example.android.clients.adapter.ImageListAdapter;
import com.example.android.clients.db.Contact;


import java.util.List;

import static com.example.android.clients.util.LogUtils.makeLogTag;

public abstract class DisplayClientsFragment extends Fragment {

    private static final String TAG = makeLogTag(DisplayClientsFragment.class);
    private GridView mGridView;

    List<String> getCurrentClientNames() {

        return Contact.getUsernamesByCategory(getActivity().getBaseContext(),"current");
    }

    String[] getCurrentClientPhotos() {
        String[] photos = {
                "http://fc05.deviantart.net/fs70/f/2012/106/5/3/elegant_user_icon_2_by_ornorm-d4weel7.png",
                "http://icons.iconarchive.com/icons/visualpharm/must-have/256/User-icon.png",
                "http://files.softicons.com/download/web-icons/free-icon-set-by-eclipse-saitex/png/256/User.png",
                "http://www.icone-png.com/png/29/28744.png",
                "https://cdn4.iconfinder.com/data/icons/general13/png/256/administrator.png"
        };

        return photos;
    }

    List<String> getProspectClientNames() {
       /* String[] names = {
                "Linda",
                "Harry"
        };
*/
        return Contact.getUsernamesByCategory(getActivity().getBaseContext(),"prospect");
    }

    String[] getProspectClientPhotos() {
        String[] photos = {
                "http://www.ronglozman.com/img/testimonial/stock.png",
                "http://c.dryicons.com/images/icon_sets/shine_icon_set/png/128x128/business_user.png"
        };

        return photos;
    }

   abstract List<String> getClientNames();
    abstract String[] getClientPhotos();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_clients, container, false);
        mGridView = (GridView) view.findViewById(R.id.device_grid);

        final Context context = getActivity().getApplicationContext();
        List<String> itemTitles = getClientNames();
        String[] itemImagePaths = getClientPhotos();
        ImageListAdapter adapter = new ImageListAdapter(context, R.layout.client_display_item, itemTitles, itemImagePaths);
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ClientActionActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
