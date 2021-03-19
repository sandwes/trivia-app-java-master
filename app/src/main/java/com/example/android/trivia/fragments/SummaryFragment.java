package com.example.android.trivia.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android.trivia.R;
import com.example.android.trivia.viewmodels.SummaryViewModel;

public class SummaryFragment extends Fragment {
    private SummaryFragmentCallback mCallback;
    private Button mRestartButton;
    private TextView mScoreTextView;
    private SummaryViewModel mSummaryViewModel;

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSummaryViewModel = ViewModelProviders.of(getActivity()).get(SummaryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summary_fragment, container, false);

        mRestartButton = view.findViewById(R.id.restart_button);
        mScoreTextView = view.findViewById(R.id.trivia_score);
        displayFinalScore();

        mRestartButton.setOnClickListener(v -> mCallback.onSetupNewGame());
        return view;
    }

    private void displayFinalScore() {
        String finalScore = mSummaryViewModel.getFinalScore();
        mScoreTextView.setText(finalScore);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (SummaryFragment.SummaryFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /*Hosting activity must implement - see onAttach*/
    public interface SummaryFragmentCallback {
        void onSetupNewGame();
    }
}
