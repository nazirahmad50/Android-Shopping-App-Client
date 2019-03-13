package com.nazir.shopping.Helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.nazir.shopping.Interface.RecyclerItemTouchHelperListener;
import com.nazir.shopping.ViewHolder.CartViewHolder;
import com.nazir.shopping.ViewHolder.HookahViewHolder;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {


    private RecyclerItemTouchHelperListener listenerInterface;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listenerInterface) {
        super(dragDirs, swipeDirs);
        this.listenerInterface = listenerInterface;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        if (listenerInterface != null){

            listenerInterface.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        View foregroundView = ((CartViewHolder)viewHolder).view_foreground;


                    getDefaultUIUtil().clearView(foregroundView);

        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = ((CartViewHolder)viewHolder).view_foreground;
        getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dX,actionState,isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (viewHolder != null){

            View foregroundView = ((CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onSelected(foregroundView);

        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = ((CartViewHolder)viewHolder).view_foreground;
        getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dX,actionState,isCurrentlyActive);
    }
}
