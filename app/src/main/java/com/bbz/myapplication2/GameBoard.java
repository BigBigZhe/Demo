package com.bbz.myapplication2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class GameBoard extends LinearLayout {
    public GameBoard(Context context) {
        super(context);
    }

    public GameBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return false;
    }
}
