package com.holarin.monefy;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;



public class SpacerItem extends RecyclerView.ItemDecoration
{
    private int space;

    public SpacerItem(int space)
    {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        outRect.bottom = space;
    }
}