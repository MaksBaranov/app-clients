package com.example.android.clients.ui;

import java.util.List;

public class DisplayClientProspectsFragment extends DisplayClientsFragment {
    @Override
    List<String> getClientNames() {
        return getProspectClientNames();
    }

    @Override
    String[] getClientPhotos() {
        return getProspectClientPhotos();
    }
}
