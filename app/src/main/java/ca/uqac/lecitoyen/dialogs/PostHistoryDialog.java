package ca.uqac.lecitoyen.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.PostHistoryAdapter;
import ca.uqac.lecitoyen.models.Post;


public class PostHistoryDialog implements DialogInterface {

    private AlertDialog.Builder mBuilder;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    public PostHistoryDialog(final Context context, final Post selectPost) {

        //  Initialize alert dialog
        mBuilder = new AlertDialog.Builder(context);

        //  Initialize recycler view
        mRecyclerView  = new RecyclerView(context);
        mLayoutManager = new LinearLayoutManager(context);
        mAdapter = new PostHistoryAdapter(
                context,
                selectPost.getHistories()
        );

        //  Set the recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //  Set the builder
        mBuilder.setView(mRecyclerView)
                .setTitle(R.string.dialog_post_history_title)
                .setIcon(R.drawable.ic_history_black_24dp)
                .setCancelable(true)
                .setPositiveButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();

    }

    public void show() {
        mBuilder.show();
    }

    @Override
    public void cancel() {

    }

    @Override
    public void dismiss() {

    }
}
