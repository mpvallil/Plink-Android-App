package ink.plink.plinkApp.databaseObjects;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import java.io.Serializable;

public class User implements Serializable {
    private transient GoogleSignInAccount userAccount;
    private String token_payment;
    private boolean is_owner;

    public User () {
        is_owner = false;
        token_payment = "";
    }

    public void setUserAccount(GoogleSignInAccount account) {
        this.userAccount = account;
    }

    public GoogleSignInAccount getUserAccount() {
        return this.userAccount;
    }

    public boolean isOwner() {
        return is_owner;
    }

    public void setOwner(boolean b) {
        if(b) {
            is_owner = true;
        } else {
            is_owner = false;
        }
    }

    public String getTokenBraintree() {
        return this.token_payment;
    }

    public static User createUserInstance(String json, GoogleSignInAccount account) {
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
