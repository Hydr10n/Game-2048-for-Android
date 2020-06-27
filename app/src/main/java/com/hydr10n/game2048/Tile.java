/*
 * Project: Game 2048
 * Last Modified: 6/27/20 12:19 PM
 *
 * Copyright (C) 2020 Programmer-Yang_Xun@outlook.com. All Rights Reserved.
 * Welcome to visit https://GitHub.com/Hydr10n
 */

package com.hydr10n.game2048;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import androidx.core.widget.TextViewCompat;

class Cell {
    public int row, column;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }
}

class Tile extends androidx.appcompat.widget.AppCompatTextView {
    private static final int TILE_FOREGROUND_INDEX = 0, TILE_BACKGROUND_INDEX = 1;
    private static final long ANIMATION_DURATION = 150;
    private static final float MIN_SCALE = 0.3f, MAX_SCALE = 1.2f;
    private static final int[][] TILE_COLORS = {    // [0]: text color; [1]: background color
            {0, Color.parseColor("#cdc1b4")},                                        // empty
            {Color.parseColor("#776e65"), Color.parseColor("#eee4da")},    // 2
            {Color.parseColor("#776e65"), Color.parseColor("#ede0c8")},    // 4
            {Color.parseColor("#f9f6f2"), Color.parseColor("#f2b179")},    // 8
            {Color.parseColor("#f9f6f2"), Color.parseColor("#f59563")},    // 16
            {Color.parseColor("#f9f6f2"), Color.parseColor("#f67c5f")},    // 32
            {Color.parseColor("#f9f6f2"), Color.parseColor("#f65e3b")},    // 64
            {Color.parseColor("#f9f6f2"), Color.parseColor("#edcf72")},    // 128
            {Color.parseColor("#f9f6f2"), Color.parseColor("#edcc61")},    // 256
            {Color.parseColor("#f9f6f2"), Color.parseColor("#edc850")},    // 512
            {Color.parseColor("#f9f6f2"), Color.parseColor("#edc53f")},    // 1024
            {Color.parseColor("#f9f6f2"), Color.parseColor("#edc22e")}     // 2048
    };

    private int number;
    private float maxSideLength, scale;
    private RelativeLayout parent;
    private RelativeLayout.LayoutParams layoutParams;

    public Tile(RelativeLayout parent, int row, int column, int number, float maxSideLength, float scale, int maxTextSize) {
        super(parent.getContext());
        setGravity(Gravity.CENTER);
        setTypeface(Typeface.DEFAULT_BOLD);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this, 1, maxTextSize, 1, TypedValue.COMPLEX_UNIT_DIP);
        this.parent = parent;
        setNumber(number);
        this.maxSideLength = maxSideLength;
        this.scale = scale;
        layoutParams = new RelativeLayout.LayoutParams((int) (maxSideLength * scale), (int) (maxSideLength * scale));
        parent.addView(this, layoutParams);
        setCell(row, column);
        playScaleAnimation(MIN_SCALE, 1, MIN_SCALE, 1, ANIMATION_DURATION, false);
    }

    @Override
    public void setBackgroundColor(int color) {
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(maxSideLength * scale * 0.05f);
        setBackground(gradientDrawable);
    }

    public void updateAppearance() {
        final int tileColorIndex = getTileColorIndex(number);
        setBackgroundColor(TILE_COLORS[tileColorIndex][TILE_BACKGROUND_INDEX]);
        setTextColor(TILE_COLORS[tileColorIndex][TILE_FOREGROUND_INDEX]);
    }

    private int getTileColorIndex(int tileNumber) {
        if (tileNumber == 0)
            return 0;
        else if (tileNumber < 0)
            throw new IllegalArgumentException();
        final int index = (int) (Math.log(tileNumber) / Math.log(2));
        if (index >= TILE_COLORS.length)
            throw new IllegalArgumentException();
        return index;
    }

    private void playScaleAnimation(float fromX, float toX, float fromY, float toY, long duration, boolean reverse) {
        final ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        if (reverse) {
            scaleAnimation.setRepeatMode(Animation.REVERSE);
            scaleAnimation.setRepeatCount(1);
        }
        scaleAnimation.setDuration(duration);
        startAnimation(scaleAnimation);
    }

    private int calculateX(int column) {
        return (int) (maxSideLength * ((1 - scale) / 2 + column));
    }

    private int calculateY(int row) {
        return calculateX(row);
    }

    public void removeSelf() {
        parent.removeView(this);
    }

    public void setCell(int row, int column) {
        layoutParams.topMargin = calculateY(row);
        layoutParams.leftMargin = calculateX(column);
        parent.updateViewLayout(this, layoutParams);
    }

    public void mergeTo(Tile tile) {
        setNumber(getNumber() << 1);
        updateAppearance();
        tile.removeSelf();
        playScaleAnimation(1, MAX_SCALE, 1, MAX_SCALE, ANIMATION_DURATION / 2, true);
    }

    private void setNumber(int number) {
        if (number != 0)
            setText(String.valueOf(number));
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}