package com.example.myapplication.features.auth.repository;

import com.example.myapplication.features.auth.firebase.AuthDataSource;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import okhttp3.Callback;

public class AuthRepository {
    private final AuthDataSource authDataSource;

    @Inject

    public AuthRepository(AuthDataSource authDataSource){
        this.authDataSource = authDataSource;
    }

    public String getCurrentUserId(){

        return authDataSource.getCurrentUserId();
    }

    public void logout(){
        authDataSource.logout();
    }

    public void sendOtp(String email, Callback callback){
        authDataSource.sendOtp(email, callback);
    }

    public Task<AuthResult> login(String email, String password) {
        return authDataSource.login(email, password);
    }

    public Task<AuthResult> register(String email, String password) {
        return authDataSource.register(email,password).continueWithTask(task -> {

            if(!task.isSuccessful()){

                Exception error = task.getException();

                if(error instanceof FirebaseAuthUserCollisionException){

                    return Tasks.forException(
                            new EmailAlreadyExistsException()
                    );
                }

                return Tasks.forException(error);
            }

            return Tasks.forResult(
                    task.getResult()
            );
        });

    }

    public class EmailAlreadyExistsException extends Exception {

        public EmailAlreadyExistsException(){
            super("EMAIL_ALREADY_EXISTS");
        }
    }
}
