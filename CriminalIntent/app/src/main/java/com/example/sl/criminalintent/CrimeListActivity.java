package com.example.sl.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Steffen on 2017-03-08.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
