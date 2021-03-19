package com.example.android.trivia.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.trivia.API.TriviaQuestion;
import com.example.android.trivia.API.TriviaResult;
import com.example.android.trivia.API.WebServiceRepository;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GameViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<TriviaQuestion>> mQuestions;
    private String category;
    private int correctAnswerCount;
    private MutableLiveData<Integer> currentQuestionIndex;
    private String difficulty;
    private WebServiceRepository mWebServiceRepository;
    private boolean hasCalledLoadQuestions;
    private MutableLiveData<Boolean> incrementPerfectScoreWasSuccessful;
    private boolean mCallingDbFlag;

    public GameViewModel(@NonNull Application application) {
        super(application);
        mQuestions = new MutableLiveData<>();
        currentQuestionIndex = new MutableLiveData<>();
        currentQuestionIndex.setValue(0);
        correctAnswerCount = 0;

        hasCalledLoadQuestions = false;
        mCallingDbFlag = false;

        mWebServiceRepository = new WebServiceRepository();
        incrementPerfectScoreWasSuccessful = new MutableLiveData<>();
    }

    public void resetGame() {
        mQuestions = new MutableLiveData<>();
        currentQuestionIndex.setValue(0);
        correctAnswerCount = 0;
        hasCalledLoadQuestions = false;
        incrementPerfectScoreWasSuccessful = new MutableLiveData<>();
    }

    public boolean isCallingDb() {
        return mCallingDbFlag;
    }

    public boolean isPerfectScore(int numberOfQuestions) {
        return correctAnswerCount / numberOfQuestions == 1;
    }

    public void setDbCallFlag(boolean bool) {
        mCallingDbFlag = bool;
    }

    public LiveData<Boolean> getIncrementScoreCountStatus() {
        return incrementPerfectScoreWasSuccessful;
    }

    public boolean hasCalledLoadQuestions() {
        return hasCalledLoadQuestions;
    }

    public void loadQuestionsHasBeenCalled() {
        hasCalledLoadQuestions = true;
    }

    public void incrementCorrectAnswerCount() {
        correctAnswerCount++;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public LiveData<ArrayList<TriviaQuestion>> getQuestionsLiveData() {
        return mQuestions;
    }

    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void incrementCurrentQuestionIndex() {
        int newValue = currentQuestionIndex.getValue() + 1;
        currentQuestionIndex.setValue(newValue);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String categoryName) {
        category = categoryName;
    }

    public void loadQuestions() {
        Single<TriviaResult> apiObservable =
                mWebServiceRepository.getTriviaResult(category, difficulty);

        apiObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TriviaResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(TriviaResult triviaResult) {
                        mQuestions.postValue(triviaResult.getQuestions());
                        Log.i("test", mQuestions + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }
}
