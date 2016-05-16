package com.example.android.clients.ui;

import com.example.android.clients.db.Contact;

import java.util.List;

public class DisplayCurrentClientsFragment extends DisplayClientsFragment {
    @Override
    List<String> getClientNames() {
        Contact.createContactWithUsername(getActivity().getApplicationContext(),"max");
        return Contact.getUsernames(getActivity().getApplicationContext());
    }

    @Override
    String[] getClientPhotos() {
        return getCurrentClientPhotos();
    }
}
