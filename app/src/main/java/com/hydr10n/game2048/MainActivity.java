/*
 * Project: Game 2048
 * Last Modified: 7/1/20 10:15 PM
 *
 * Copyright (C) 2020 Programmer-Yang_Xun@outlook.com. All Rights Reserved.
 * Welcome to visit https://GitHub.com/Hydr10n
 */

package com.hydr10n.game2048;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hydr10n.game2048.databinding.ActivityMainBinding;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private enum Direction {Left, Up, Right, Down}

    private static final int GAME_SAVE_KEY_SCORE_INDEX = 0, GAME_SAVE_KEY_BEST_SCORE_INDEX = 1, GAME_SAVE_KEY_TILES_NumberS_INDEX = 2;
    private static final float PADDING_SCALE = 0.06f, TILE_SCALE = 1 - 2 * PADDING_SCALE;
    private static final String[][] GAME_SAVE_KEYS = {
            {"Layout4Score", "Layout4BestScore", "Layout4TilesNumbers"},
            {"Layout5Score", "Layout5BestScore", "Layout5TilesNumbers"},
            {"Layout6Score", "Layout6BestScore", "Layout6TilesNumbers"}
    };

    private final ViewModel viewModel = new ViewModel();

    private int tilesCountPerSide, maxTextSize, gameSaveKeyIndex;
    private float tileFullSideLength;
    private GameSave gameSave;
    private RelativeLayout gameLayout;
    private Tile[][] tiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setViewModel(viewModel);
        gameSave = new GameSave(this, "data");
        gameLayout = findViewById(R.id.layout_game);
        gameLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                moveTiles(Direction.Left);
                saveGameProgress();
            }

            @Override
            public void onSwipeUp() {
                moveTiles(Direction.Up);
                saveGameProgress();
            }

            @Override
            public void onSwipeRight() {
                moveTiles(Direction.Right);
                saveGameProgress();
            }

            @Override
            public void onSwipeDown() {
                moveTiles(Direction.Down);
                saveGameProgress();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            final SpannableString spannableString = new SpannableString("Current Version: " + BuildConfig.VERSION_NAME + '\n' + getString(R.string.copyright));
            Linkify.addLinks(spannableString, Linkify.WEB_URLS);
            final TextView textView = new TextView(this);
            textView.setText(spannableString);
            textView.setPadding(0, 10, 0, 0);
            textView.setGravity(Gravity.CENTER);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.about))
                    .setView(textView)
                    .setCancelable(true)
                    .setPositiveButton(R.string.confirm, null)
                    .create().show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectLayoutButton_onClick(View view) {
        final int count;
        switch (view.getId()) {
            case R.id.button_layout4:
                count = 4;
                maxTextSize = 42;
                gameSaveKeyIndex = 0;
                break;
            case R.id.button_layout5:
                count = 5;
                maxTextSize = 40;
                gameSaveKeyIndex = 1;
                break;
            case R.id.button_layout6:
                count = 6;
                maxTextSize = 38;
                gameSaveKeyIndex = 2;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (tilesCountPerSide == count)
            return;
        tilesCountPerSide = count;
        viewModel.setGameState(GameState.NotStarted);
        gameLayout.removeAllViews();
        final int gameLayoutSideLength = Math.min(gameLayout.getWidth(), gameLayout.getHeight());
        tileFullSideLength = gameLayoutSideLength / (tilesCountPerSide + PADDING_SCALE * 2);
        final int padding = (int) (tileFullSideLength * PADDING_SCALE);
        gameLayout.setPadding(padding, padding, padding, padding);
        viewModel.setScore(gameSave.loadIntData(GAME_SAVE_KEYS[gameSaveKeyIndex][GAME_SAVE_KEY_SCORE_INDEX]));
        viewModel.setBestScore(gameSave.loadIntData(GAME_SAVE_KEYS[gameSaveKeyIndex][GAME_SAVE_KEY_BEST_SCORE_INDEX]));
        if (initializeGameLayout())
            viewModel.setGameState(GameState.Started);
        viewModel.setLayoutReady(true);
    }

    public void newGameButton_onClick(View view) {
        viewModel.setGameState(GameState.NotStarted);
        saveGameProgress();
        startNewGame();
    }

    private Tile addTile(int row, int column, int tileNumber) {
        final Tile tile = new Tile(gameLayout, row, column, tileNumber, tileFullSideLength, TILE_SCALE, maxTextSize);
        tile.updateAppearance();
        return tile;
    }

    private void addRandomTile() {
        final Random random = new Random();
        int i, j;
        do {
            i = random.nextInt(tilesCountPerSide);
            j = random.nextInt(tilesCountPerSide);
        } while (tiles[i][j] != null);
        tiles[i][j] = addTile(i, j, 2);
    }

    private void removeAllTiles() {
        for (int i = 0; i < tilesCountPerSide; i++)
            for (int j = 0; j < tilesCountPerSide; j++)
                if (tiles[i][j] != null) {
                    tiles[i][j].removeSelf();
                    tiles[i][j] = null;
                }
    }

    private void moveTile(Cell fromCell, Cell toCell) {
        final Tile tile = tiles[fromCell.row][fromCell.column];
        tile.setCell(toCell.row, toCell.column);
        tiles[fromCell.row][fromCell.column] = null;
        tiles[toCell.row][toCell.column] = tile;
    }

    private void mergeTiles(Cell fromCell1, Cell fromCell2, Cell toCell) {
        final Tile fromTile2 = tiles[fromCell2.row][fromCell2.column];
        fromTile2.mergeTo(tiles[fromCell1.row][fromCell1.column], toCell);
        tiles[fromCell1.row][fromCell1.column] = null;
        tiles[fromCell2.row][fromCell2.column] = null;
        tiles[toCell.row][toCell.column] = fromTile2;
    }

    private Cell rotateCell(int row, int column, int angle) {
        if (angle == 0)
            return new Cell(row, column);
        int offsetRow = tilesCountPerSide - 1, offsetColumn = offsetRow;
        if (angle == 90)
            offsetRow = 0;
        else if (angle == 270)
            offsetColumn = 0;
        final double radians = Math.toRadians(angle);
        final int cos = (int) Math.cos(radians), sin = (int) Math.sin(radians);
        return new Cell(offsetRow + column * sin + row * cos, offsetColumn + column * cos - row * sin);
    }

    private boolean isGameOver() {
        final int[][] directions = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}};
        for (int i = 0; i < tilesCountPerSide; i++)
            for (int j = 0; j < tilesCountPerSide; j++) {
                if (tiles[i][j] == null)
                    return false;
                for (int[] direction : directions) {
                    final int newI = i + direction[0], newJ = j + direction[1];
                    if (newI >= 0 && newI < tilesCountPerSide && newJ >= 0 && newJ < tilesCountPerSide &&
                            (tiles[newI][newJ] == null || tiles[i][j].getNumber() == tiles[newI][newJ].getNumber()))
                        return false;
                }
            }
        return true;
    }

    private void saveGameProgress() {
        int score = 0;
        int[][] tilesNumbers = null;
        if (viewModel.getGameState() == GameState.Started) {
            score = viewModel.getScore();
            tilesNumbers = new int[tilesCountPerSide][tilesCountPerSide];
            for (int i = 0; i < tilesCountPerSide; i++)
                for (int j = 0; j < tilesCountPerSide; j++)
                    if (tiles[i][j] != null)
                        tilesNumbers[i][j] = tiles[i][j].getNumber();
        }
        gameSave.saveData(GAME_SAVE_KEYS[gameSaveKeyIndex][GAME_SAVE_KEY_SCORE_INDEX], score);
        gameSave.saveData(GAME_SAVE_KEYS[gameSaveKeyIndex][GAME_SAVE_KEY_BEST_SCORE_INDEX], Math.max(score, viewModel.getBestScore()));
        gameSave.saveData(GAME_SAVE_KEYS[gameSaveKeyIndex][GAME_SAVE_KEY_TILES_NumberS_INDEX], tilesNumbers);
    }

    private boolean loadGameProgress() {
        final int[][] tilesNumbers = gameSave.loadInt2DData(GAME_SAVE_KEYS[gameSaveKeyIndex][GAME_SAVE_KEY_TILES_NumberS_INDEX]);
        if (tilesNumbers != null)
            for (int i = 0; i < tilesCountPerSide; i++)
                for (int j = 0; j < tilesCountPerSide; j++)
                    if (tilesNumbers[i][j] != 0)
                        tiles[i][j] = addTile(i, j, tilesNumbers[i][j]);
        return tilesNumbers != null;
    }

    private void moveTiles(Direction direction) {
        if (viewModel.getGameState() != GameState.Started)
            return;
        int angle;
        switch (direction) {
            case Left:
                angle = 0;
                break;
            case Up:
                angle = 90;
                break;
            case Right:
                angle = 180;
                break;
            case Down:
                angle = 270;
                break;
            default:
                throw new IllegalArgumentException();
        }
        boolean moved = false, won = false;
        int count = 0;
        for (int row = 0; row < tilesCountPerSide; row++) {
            int next = 0;
            for (int a = 0; a < tilesCountPerSide; a++) {
                final Cell cell1 = rotateCell(row, a, angle);
                if (tiles[cell1.row][cell1.column] != null) {
                    boolean foundSecond = false, merged = true;
                    for (int b = a + 1; b < tilesCountPerSide; b++) {
                        final Cell cell2 = rotateCell(row, b, angle);
                        if (tiles[cell2.row][cell2.column] != null) {
                            foundSecond = true;
                            int tileNumber = tiles[cell1.row][cell1.column].getNumber();
                            if (tileNumber == tiles[cell2.row][cell2.column].getNumber()) {
                                mergeTiles(cell1, cell2, rotateCell(row, next, angle));
                                moved = true;
                                a = b;
                                tileNumber <<= 1;
                                viewModel.setScore(tileNumber + viewModel.getScore());
                                if (tileNumber >= 2048)
                                    won = true;
                            } else {
                                if (a != next) {
                                    moveTile(cell1, rotateCell(row, next, angle));
                                    moved = true;
                                }
                                if (b != next + 1) {
                                    moveTile(cell2, rotateCell(row, next + 1, angle));
                                    moved = true;
                                }
                                a = next;
                                merged = false;
                            }
                            next++;
                            break;
                        }
                    }
                    if (!foundSecond && a > 0) {
                        if (!merged)
                            next++;
                        if (a != next) {
                            moveTile(cell1, rotateCell(row, next, angle));
                            moved = true;
                        }
                        break;
                    }
                }
            }
            final Cell cell = rotateCell(row, tilesCountPerSide - 1, angle);
            if (tiles[cell.row][cell.column] != null)
                count++;
        }
        final int score = viewModel.getScore();
        if (score > viewModel.getBestScore())
            viewModel.setBestScore(score);
        if (!moved)
            return;
        if (won) {
            viewModel.setGameState(GameState.Won);
            return;
        }
        if (count < tilesCountPerSide)
            addRandomTile();
        if (count >= tilesCountPerSide - 1 && isGameOver())
            viewModel.setGameState(GameState.Over);
    }

    private boolean initializeGameLayout() {
        for (int i = 0; i < tilesCountPerSide; i++)
            for (int j = 0; j < tilesCountPerSide; j++)
                addTile(i, j, 0);
        tiles = new Tile[tilesCountPerSide][tilesCountPerSide];
        return loadGameProgress();
    }

    private void startNewGame() {
        removeAllTiles();
        addRandomTile();
        addRandomTile();
        viewModel.setScore(0);
        viewModel.setGameState(GameState.Started);
    }
}