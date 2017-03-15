package com.example.sl.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Steffen on 2017-03-08.
 */

class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mRequiresPolice;

    private boolean mSolved;

    UUID getId() {
        return mId;
    }

    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        mTitle = title;
    }

    Date getDate() {
        return mDate;
    }

    void setDate(Date date) {
        mDate = date;
    }

    boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    boolean isSolved() {
        return mSolved;
    }

    void setSolved(boolean solved) {
        mSolved = solved;
    }

    Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();

    }
}
