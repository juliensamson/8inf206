package ca.uqac.lecitoyen.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.PostHistoryAdapter;
import ca.uqac.lecitoyen.models.Post;


public class DeletePostDialog implements DialogInterface {

    private AlertDialog.Builder mBuilder;

    public DeletePostDialog(final Context context) {

        //  Initialize alert dialog
        mBuilder = new AlertDialog.Builder(context);

        //  Set the builder
        mBuilder.setIcon(R.drawable.ic_delete_forever_black_24dp)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setCancelable(true)
                .create();

    }

    public DeletePostDialog delete(DialogInterface.OnClickListener onClickListener) {

        mBuilder.setPositiveButton(R.string.button_delete, onClickListener);

        return this;
    }

    public DeletePostDialog cancel(DialogInterface.OnClickListener onClickListener) {

        mBuilder.setNegativeButton(R.string.button_cancel, onClickListener);

        return this;
    }


    public DeletePostDialog show() {

        mBuilder.show();

        return this;
    }


    @Override
    public void cancel() {

    }

    @Override
    public void dismiss() {

    }
}
