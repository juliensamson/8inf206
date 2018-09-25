package ca.uqac.lecitoyen.Interface;

import com.google.firebase.auth.FirebaseUser;

public interface iUpdate {

    void updateUI(FirebaseUser user);

    void updateDB(FirebaseUser user);

}
