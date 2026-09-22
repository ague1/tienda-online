package com.example.myapplication.features.auth.infrastructure.repository;


import com.example.myapplication.features.auth.error.EmailAlreadyExistsException;
import com.example.myapplication.features.auth.error.InvalidCredentialsException;
import com.example.myapplication.features.auth.infrastructure.datasource.AuthDataSource;
import com.example.myapplication.features.auth.domain.port.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class AuthRepositoryImp implements AuthRepository {

    private final AuthDataSource authDataSource;

    @Inject
    public AuthRepositoryImp(
            AuthDataSource authDataSource
    ) {
        this.authDataSource = authDataSource;
    }

    @Override
    public String getCurrentUserId() {

        return authDataSource.getCurrentUserId();
    }

    @Override
    public void logout() {

        authDataSource.logout();
    }

    @Override
    public Task<Void> login(
            String email,
            String password
    ) {

        return authDataSource
                .login(email, password)
                .continueWithTask(task -> {

                    if (task.isSuccessful()) {
                        return Tasks.forResult(null);
                    }

                    Exception error =
                            task.getException();

                    if (error instanceof FirebaseAuthException) {

                        String errorCode =
                                ((FirebaseAuthException) error)
                                        .getErrorCode();

                        if (
                                "ERROR_INVALID_EMAIL".equals(errorCode) ||
                                        "ERROR_WRONG_PASSWORD".equals(errorCode) ||
                                        "ERROR_USER_NOT_FOUND".equals(errorCode) ||
                                        "ERROR_INVALID_CREDENTIAL".equals(errorCode)
                        ) {

                            return Tasks.forException(
                                    new InvalidCredentialsException()
                            );
                        }
                    }

                    return Tasks.forException(error);
                });
    }

    @Override
    public Task<String> register(String email, String password) {

        return authDataSource
                .register(email, password)
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {

                        Exception error = task.getException();

                        if (
                                error instanceof
                                        FirebaseAuthUserCollisionException
                        ) {

                            return Tasks.forException(
                                    new EmailAlreadyExistsException()
                            );
                        }

                        return Tasks.forException(error);
                    }

                    AuthResult authResult = task.getResult();

                    if (
                            authResult == null || authResult.getUser() == null
                    ) {

                        return Tasks.forException(
                                new IllegalStateException(
                                        "USER_CREATION_FAILED"
                                )
                        );
                    }

                    return Tasks.forResult(
                            authResult.getUser().getUid()
                    );
                });
    }

    @Override
    public Task<Void> deleteUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return Tasks.forException(
                    new IllegalStateException(
                            "USER_NOT_AUTHENTICATED"
                    )
            );
        }

        return authDataSource.deleteUser();
    }
}


