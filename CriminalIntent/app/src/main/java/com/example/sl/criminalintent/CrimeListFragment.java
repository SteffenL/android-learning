package com.example.sl.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Steffen on 2017-03-08.
 */

public class CrimeListFragment extends Fragment {
    private class CrimeHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        private Crime mCrime;

        private TextView mCrimeTitleTextView;
        private TextView mCrimeDateTextView;
        private ImageView mCrimeSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int layout) {
            super(inflater.inflate(layout, parent, false));

            mCrimeTitleTextView = (TextView)itemView.findViewById(R.id.crime_title);
            mCrimeDateTextView = (TextView)itemView.findViewById(R.id.crime_date);
            mCrimeSolvedImageView = (ImageView)itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mCrimeTitleTextView.setText(mCrime.getTitle());
            mCrimeDateTextView.setText(DateFormat.format(getString(R.string.crime_date_format), mCrime.getDate()));
            mCrimeSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private static final String TAG = "CrimeAdapter";
        private List<Crime> mCrimes;

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(inflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            Log.d(TAG, String.format("onBindViewHolder: position = %d; crime title = %s", position, crime.getTitle()));
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            return (!crime.isSolved() && crime.isRequiresPolice()) ? R.layout.list_item_crime_police : R.layout.list_item_crime;
        }

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }

    private static final String TAG = "CrimeListFragment";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            Log.d(TAG, "Creating new adapter");
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "Notifying data set changed");
            mAdapter.notifyDataSetChanged();
        }
    }
}
