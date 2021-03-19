package com.example.android.trivia.API;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TriviaResult {
    @SerializedName("results")
    ArrayList<TriviaQuestion> questions;

    public ArrayList<TriviaQuestion> getQuestions() {
        return questions;
    }
}
