/*
 * Project: Game 2048
 * Last Modified: 6/21/20 9:50 PM
 *
 * Copyright (C) 2020 Programmer-Yang_Xun@outlook.com. All Rights Reserved.
 * Welcome to visit https://GitHub.com/Hydr10n
 */

package com.hydr10n.game2048;

import android.app.Activity;
import android.content.SharedPreferences;

class GameSave {
    private static final String ROW_DELIMITER = ";", COLUMN_DELIMITER = ",";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public GameSave(Activity activity, String fileName) {
        sharedPreferences = activity.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean saveData(String key, int data) {
        editor.putInt(key, data);
        return editor.commit();
    }

    public boolean saveData(String key, int[][] data) {
        StringBuilder stringBuilder = new StringBuilder();
        if (data != null)
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++)
                    stringBuilder.append(data[i][j]).append(j == data[i].length - 1 ? "" : COLUMN_DELIMITER);
                stringBuilder.append(i == data.length - 1 ? "" : ROW_DELIMITER);
            }
        editor.putString(key, stringBuilder.toString());
        return editor.commit();
    }

    public int loadIntData(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public int[][] loadInt2DData(String key) {
        final String rawData = sharedPreferences.getString(key, "");
        if (rawData.equals(""))
            return null;
        final String[] strings = rawData.split(ROW_DELIMITER);
        final int[][] data = new int[strings.length][];
        for (int i = 0; i < strings.length; i++) {
            final String[] temp = strings[i].split(COLUMN_DELIMITER);
            data[i] = new int[temp.length];
            for (int j = 0; j < temp.length; j++)
                data[i][j] = Integer.parseInt(temp[j]);
        }
        return data;
    }
}