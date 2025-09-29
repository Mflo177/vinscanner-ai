package com.marioflo.vinscannerapp.ui.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.entities.VinInfo;
import com.marioflo.vinscannerapp.viewmodel.VinViewModel;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private VinInfoAdapter mAdapter;
    private VinViewModel vinViewModel;



    public SwipeToDeleteCallback(VinInfoAdapter adapter, VinViewModel vinViewModel) {
        super(0, ItemTouchHelper.LEFT);
        mAdapter = adapter;
        this.vinViewModel = vinViewModel;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        final VinInfo vinInfo = mAdapter.getVinInfos().get(position);

        // Show confirmation dialog
        new AlertDialog.Builder(mAdapter.getContext())
                .setTitle("Delete VIN Info")
                .setMessage("Are you sure you want to delete this VIN info?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vinViewModel.deleteVinInfo(vinInfo);
                        mAdapter.deleteVinInfo(position);                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.notifyItemChanged(position);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        Paint p = new Paint();
        if (dX < 0) {
            p.setColor(Color.RED);
            RectF background = new RectF(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            c.drawRect(background, p);

            Drawable icon = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.delete_icon);
            if (icon != null) {
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.draw(c);
            }
        }
    }

}
