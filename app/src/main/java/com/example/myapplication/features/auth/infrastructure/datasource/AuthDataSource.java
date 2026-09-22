package com.example.myapplication.features.auth.infrastructure.datasource;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import javax.inject.Inject;
public class AuthDataSource {

    private final FirebaseAuth auth;

    @Inject
    public AuthDataSource(
            FirebaseAuth auth
    ) {
        this.auth = auth;
    }

    public String getCurrentUserId() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user != null) {
            return user.getUid();
        }

        return null;
    }

    public void logout() {

        auth.signOut();
    }

    public Task<AuthResult> login(
            String email,
            String password
    ) {

        return auth.signInWithEmailAndPassword(
                email,
                password
        );
    }

    public Task<AuthResult> register(
            String email,
            String password
    ) {

        return auth.createUserWithEmailAndPassword(
                email,
                password
        );
    }

    public Task<Void> deleteUser() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {
            return Tasks.forException(
                    new IllegalStateException(
                            "USER_NOT_AUTHENTICATED"
                    )
            );
        }

        return user.delete();
    }


}
