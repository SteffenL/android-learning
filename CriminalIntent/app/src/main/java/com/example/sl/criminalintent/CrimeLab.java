package com.example.sl.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Steffen on 2017-03-08.
 */

class CrimeLab {
    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;

    static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        for (int i = 0; i < 100; ++i) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            crime.setRequiresPolice(i % 4 < 2);
            mCrimes.add(crime);
        }
    }

    List<Crime> getCrimes() {
        return mCrimes;
    }

    Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }

        return null;
    }
}
