package ca.uqac.lecitoyen.auth;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.activities.MainActivity;
import ca.uqac.lecitoyen.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotUserFragment extends BaseFragment implements View.OnClickListener {


    private static final String TAG = "ForgotUserFragment";

    private iHandleFragment mHandleFragment;

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;

    private FirebaseAuth mAuth;

    boolean isEmailSent = false;

    MainActivity mParentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParentActivity = (MainActivity) getActivity();
        mHandleFragment.setToolbarTitle(getTag());

        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_account, container, false);
        Log.d(TAG, "onCreateView");

        setFragmentToolbar(mParentActivity, R.drawable.ic_arrow_back_white_24dp, true, true);

        //  View
        mTextInputLayout = view.findViewById(R.id.forgot_account_frag_text_input_layout);
        mEmailField = view.findViewById(R.id.forgot_account_frag_text_input_email);

        //  Buttons
        view.findViewById(R.id.forgot_account_frag_send_email_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mHandleFragment.inflateFragment(R.string.fragment_main_auth,"");
                break;
            default:
                Log.e(TAG, "This onClick doesn't exist");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mParentActivity.updateUI(currentUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (iHandleFragment) getActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.forgot_account_frag_send_email_button:
                if(resetEmailPassword(mEmailField.getText().toString()))
                    mHandleFragment.inflateFragment(R.string.fragment_main_auth,"");
                break;
            default:
                break;
        }
    }

    private boolean resetEmailPassword(String email) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), R.string.toast_email_incorrect, Toast.LENGTH_SHORT).show();
            return isEmailSent;
        }

        mParentActivity.showProgressDialog();

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.w(TAG, "Email sent");
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getContext(), R.string.toast_reset_password_sent, Toast.LENGTH_SHORT).show();
                            isEmailSent = true;
                        }
                        else
                        {
                            Toast.makeText(getContext(), R.string.toast_reset_password_fail, Toast.LENGTH_SHORT).show();
                            isEmailSent = false;
                        }
                        mParentActivity.hideProgressDialog();
                    }
                });
        return isEmailSent;
    }
}
