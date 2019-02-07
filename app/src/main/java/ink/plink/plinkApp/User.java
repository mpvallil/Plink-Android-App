package ink.plink.plinkApp;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import java.io.Serializable;

public class User implements Serializable {
    private transient GoogleSignInAccount userAccount;
    private String token_payment;
    private boolean is_owner;

    User () {
        is_owner = false;
        token_payment = "";
    }

    void setUserAccount(GoogleSignInAccount account) {
        this.userAccount = account;
    }

    GoogleSignInAccount getUserAccount() {
        return this.userAccount;
    }

    boolean isOwner() {
        return is_owner;
    }

    void setOwner(boolean b) {
        if(b) {
            is_owner = true;
        } else {
            is_owner = false;
        }
    }

    String getTokenBraintree() {
        return this.token_payment;
    }

    static User createUserInstance(String json, GoogleSignInAccount account) {
        Gson gson = new Gson();
        User user;
        try {
            user = gson.fromJson(json, User.class);
        } catch (Exception e) {
            user = new User();
        }
        return user;
    }
}
