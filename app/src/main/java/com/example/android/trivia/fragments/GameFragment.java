package com.example.android.trivia.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.android.trivia.R;
import com.example.android.trivia.API.TriviaQuestion;
import com.example.android.trivia.viewmodels.GameViewModel;
import com.example.android.trivia.viewmodels.SummaryViewModel;

import java.util.ArrayList;

public class GameFragment extends Fragment {
    private ProgressBar mProgressBar;
    private ProgressBar mDbProgressBar;
    private TextView mQuestionTextView;
    private GameFragmentCallback mCallback;
    private Button mSubmitAnswerButton;
    private RadioGroup mRadioGroup;
    private LiveData<ArrayList<TriviaQuestion>> mQuestionsLiveData;
    private GameViewModel mGameViewModel;
    private SummaryViewModel mSummaryViewModel;
    private LiveData<Integer> mCurrentQuestionIndex;
    private LinearLayout mGameLayout;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mGameViewModel = ViewModelProviders.of(getActivity()).get(GameViewModel.class);
        mSummaryViewModel = ViewModelProviders.of(getActivity())
                .get(SummaryViewModel.class);
        mCurrentQuestionIndex = mGameViewModel.getCurrentQuestionIndex();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (GameFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);
        mProgressBar = view.findViewById(R.id.progressBar);
        mSubmitAnswerButton = view.findViewById(R.id.submit_answer_button);
        mQuestionTextView = view.findViewById(R.id.questionText);
        mRadioGroup = view.findViewById(R.id.questions_radio_group);
        mGameLayout = view.findViewById(R.id.game_layout);
        mDbProgressBar = view.findViewById(R.id.db_update_progressbar);

        mQuestionsLiveData = mGameViewModel.getQuestionsLiveData();

        checkWhetherQuestionsAreLoading();

        if (mGameViewModel.isCallingDb()) {
            showDbProgressBar();
            observeDbUpdate();
        } else {
            setupGame();
        }

        return view;
    }

    private void checkWhetherQuestionsAreLoading() {
        if (!mGameViewModel.hasCalledLoadQuestions()) {
            mGameViewModel.loadQuestions();
            mGameViewModel.loadQuestionsHasBeenCalled();
        }
    }

    private void setupGame() {
        /*If the questions have not been loaded in the ViewModel yet: */
        if (mQuestionsLiveData.getValue() == null) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            /*Observe when the questions have been loaded by the ViewModel*/
            mQuestionsLiveData.observe(this, triviaQuestions -> {
                mProgressBar.setVisibility(ProgressBar.GONE);
                playGame();
            });
            /*If the questions have already been loaded in the ViewModel:*/
        } else {
            playGame();
        }
    }

    private void displayQuestionAndAnswers() {
        TriviaQuestion questionToDisplay = getCurrentQuestion();
        String questionString = questionToDisplay.getQuestion();
        mQuestionTextView.setText(Html.fromHtml(questionString));
        displayAnswers(questionToDisplay.getAnswers());
    }

    private void displayAnswers(ArrayList<String> answers) {
        for (String answer : answers) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(View.generateViewId());
            radioButton.setText(Html.fromHtml(answer));
            mRadioGroup.addView(radioButton);
        }
    }

    private void displayQuestionViews() {
        mQuestionTextView.setVisibility(View.VISIBLE);
        mRadioGroup.setVisibility(View.VISIBLE);
        mSubmitAnswerButton.setVisibility(View.VISIBLE);
    }

    private TriviaQuestion getCurrentQuestion() {
        ArrayList<TriviaQuestion> questionList = mQuestionsLiveData.getValue();
        int currentQuestionIndex = mCurrentQuestionIndex.getValue();
        return questionList.get(currentQuestionIndex);
    }

    private void playGame() {
        displayQuestionViews();
        displayQuestionAndAnswers();

        mSubmitAnswerButton.setOnClickListener(v -> {
            int selectedButtonId = mRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedButton = GameFragment.this.getView().
                    findViewById(selectedButtonId);
            String correctAnswer = getCurrentQuestion().getCorrectAnswer();
            String escapedCorrectAnswer = Html.fromHtml(correctAnswer).toString();

            String selectedButtonText = selectedButton.getText().toString();
            if (selectedButtonText.equals(escapedCorrectAnswer)) {
                mGameViewModel.incrementCorrectAnswerCount();
            }
            mRadioGroup.removeAllViews();
            mGameViewModel.incrementCurrentQuestionIndex();

            if (gameOver()) {
                int questionQuantity = mQuestionsLiveData.getValue().size();
                mSummaryViewModel.setFinalScore(questionQuantity,
                        mGameViewModel.getCorrectAnswerCount());

                if (mGameViewModel.isPerfectScore(questionQuantity)) {

                    showDbProgressBar();
                    observeDbUpdate();
                } else {
                    mCallback.onFinishGame();
                }
                mGameViewModel.resetGame();
            } else {
                displayQuestionAndAnswers();
            }
        });
    }

    private void observeDbUpdate() {
        mGameViewModel.getIncrementScoreCountStatus()
                .observe(this, updateWasSuccessful -> {
                    hideDbProgressBar();
                    mGameViewModel.setDbCallFlag(false);

                    if (updateWasSuccessful) {
                        mCallback.onFinishGame();
                    }
                });
    }

    private boolean gameOver() {
        return mCurrentQuestionIndex.getValue() == mQuestionsLiveData.getValue().size();
    }

    private void hideDbProgressBar() {
        mGameLayout.setVisibility(View.VISIBLE);
        mDbProgressBar.setVisibility(View.GONE);
    }

    private void showDbProgressBar() {
        mGameLayout.setVisibility(View.GONE);
        mDbProgressBar.setVisibility(View.VISIBLE);
    }

    /*Hosting activity must implement - see onAttach*/
    public interface GameFragmentCallback {
        void onFinishGame();
    }
}