/*
 * Project: Game 2048
 * Last Modified: 6/18/20 4:05 PM
 *
 * Copyright (C) 2020 Programmer-Yang_Xun@outlook.com. All Rights Reserved.
 * Welcome to visit https://GitHub.com/Hydr10n
 */

package com.hydr10n.game2048;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ViewModel extends BaseObservable {
    private boolean layoutReady;
    private int score, bestScore;
    private GameState gameState = GameState.NotStarted;

    @Bindable
    public boolean isLayoutReady() {
        return layoutReady;
    }

    public void setLayoutReady(boolean layoutReady) {
        this.layoutReady = layoutReady;
        notifyPropertyChanged(BR.layoutReady);
    }

    @Bindable
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        notifyPropertyChanged(BR.score);
    }

    @Bindable
    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
        notifyPropertyChanged(BR.bestScore);
    }

    @Bindable
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        notifyPropertyChanged(BR.gameState);
    }
}