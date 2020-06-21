/*
 * Project: Game 2048
 * Last Modified: 6/21/20 10:20 PM
 *
 * Copyright (C) 2020 Programmer-Yang_Xun@outlook.com. All Rights Reserved.
 * Welcome to visit https://GitHub.com/Hydr10n
 */

package com.hydr10n.game2048;

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
    private static final long ANIMATION_DURATION = 150;
    private static final float MIN_SCALE = 0.3f, MAX_SCALE = 1.2f;

    private int value;
    private float maxSideLength, scale;
    private RelativeLayout parent;
    private RelativeLayout.LayoutParams layoutParams;

    public Tile(RelativeLayout parent, float maxSideLength, float scale) {
        super(parent.getContext());
        setGravity(Gravity.CENTER);
        setTypeface(Typeface.DEFAULT_BOLD);
        layoutParams = new RelativeLayout.LayoutParams((int) (maxSideLength * scale), (int) (maxSideLength * scale));
        parent.addView(this, layoutParams);
        this.maxSideLength = maxSideLength;
        this.scale = scale;
        this.parent = parent;
    }

    @Override
    public void setBackgroundColor(int color) {
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(maxSideLength * scale * 0.05f);
        setBackground(gradientDrawable);
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

    public void setMaxTextSize(int size) {
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this, 1, size, 1, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void scaleFromSmallToNormal() {
        playScaleAnimation(MIN_SCALE, 1, MIN_SCALE, 1, ANIMATION_DURATION, false);
    }

    public void setCell(int row, int column) {
        layoutParams.topMargin = calculateY(row);
        layoutParams.leftMargin = calculateX(column);
        parent.updateViewLayout(this, layoutParams);
    }

    public void setValue(int value) {
        setText(String.valueOf(value));
        this.value = value;
    }

    public void setValueAndScale(int value) {
        setValue(value);
        playScaleAnimation(1, MAX_SCALE, 1, MAX_SCALE, ANIMATION_DURATION / 2, true);
    }

    public int getValue() {
        return value;
    }
}