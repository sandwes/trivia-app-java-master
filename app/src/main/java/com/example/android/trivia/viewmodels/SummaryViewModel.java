package com.example.android.trivia.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class SummaryViewModel extends AndroidViewModel {
    private double finalScore;

    public SummaryViewModel(@NonNull Application application) {
        super(application);
        finalScore = -1;
    }

    public void setFinalScore(double numberOfQuestions, double correctAnswerCount) {
        finalScore = correctAnswerCount / numberOfQuestions;
    }

    public String getFinalScore() {
        int percentageNumber = (int) Math.round(finalScore * 100);
        return percentageNumber + "%";
    }

    public void resetFinalScore() {
        finalScore = -1;
    }
}