package com.example.android.clients.util;

import com.google.android.gms.plus.model.people.Person;

public class UserInfo {
    //
    private String mName;

    //
    private int mAge, mAgeMax, mAgeMin;

    //
    private static final int sAgeUnknown = -1;

    //
    private int mGender;

    //
    private static final int UNKNOWN = 0;

    //
    private static final int MALE = 1;

    //
    private static final int FEMALE = 2;

    //
    private static final int NEUTRAL = 3;

    //
    private String mNickName;

    //
    private String mProfileImageUrl;

    public class GooglePlusProfileInfo {
        public String id;
        public String url;
        public String email;
    };

    private GooglePlusProfileInfo[] mGooglePlusProfiles;

    void setFromGooglePlusProfile(Person user, String email) {
        if (user == null)
            return;

        mName = null;
        if (user.hasDisplayName())
            mName = user.getDisplayName();

        mAge = sAgeUnknown;
        mAgeMax = sAgeUnknown;
        mAgeMin = sAgeUnknown;
        if (user.hasAgeRange()) {
            Person.AgeRange ageRange = user.getAgeRange();
            if(ageRange.hasMax())
                mAgeMax = ageRange.getMax();
            if(ageRange.hasMin())
                mAgeMin = ageRange.getMin();
        }

        mGender = UNKNOWN;
        if (user.hasGender()) {
            int gender = user.getGender();
            switch (gender) {
                case Person.Gender.MALE:
                    mGender = MALE;
                    break;
                case Person.Gender.FEMALE:
                    mGender = FEMALE;
                    break;
                default:
                    mGender = UNKNOWN;
            }
        }

        mNickName = null;
        if(user.hasNickname())
            mNickName = user.getNickname();

        mProfileImageUrl = null;
        if (user.hasImage()) {
            Person.Image image = user.getImage();
            if (image.hasUrl())
                mProfileImageUrl = user.getImage().getUrl();
        }

        mGooglePlusProfiles = new GooglePlusProfileInfo[1];

        mGooglePlusProfiles[0].id = null;
        if(user.hasId())
            mGooglePlusProfiles[0].id = user.getId();

        mGooglePlusProfiles[0].url = null;
        if (user.hasUrl())
            mGooglePlusProfiles[0].url = user.getUrl();

        mGooglePlusProfiles[0].email = email;
    }
}