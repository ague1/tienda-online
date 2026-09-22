package com.example.myapplication.features.auth.application.usecase;

import com.example.myapplication.features.auth.domain.port.AuthRepository;
import com.example.myapplication.features.profiles.domain.model.Profile;
import com.example.myapplication.features.profiles.domain.port.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import javax.inject.Inject;


public class SignupUseCase {

    private final AuthRepository authRepository;
    private final ProfileRepository profileRepository;

    @Inject
    public SignupUseCase(
            AuthRepository authRepository,
            ProfileRepository profileRepository
    ) {
        this.authRepository = authRepository;
        this.profileRepository = profileRepository;
    }

    public Task<Void> execute(
            String name,
            String email,
            String password
    ) {

        return authRepository.register(email, password)
                .onSuccessTask(uid -> {

                    Profile profile = new Profile();

                    profile.setUid(uid);
                    profile.setName(name);
                    profile.setEmail(email);

                    return profileRepository.createProfile(profile)
                            .continueWithTask(task -> {

                                if (task.isSuccessful()) {
                                    return Tasks.forResult(null);
                                }

                                Exception profileError = task.getException();

                                return authRepository.deleteUser()
                                        .continueWithTask(deleteTask ->
                                                Tasks.forException(
                                                        profileError != null
                                                                ? profileError
                                                                : new Exception(
                                                                "PROFILE_CREATION_FAILED"
                                                        )
                                                )
                                        );
                            });
                });
    }
}

