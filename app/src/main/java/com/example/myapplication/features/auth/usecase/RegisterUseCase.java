package com.example.myapplication.features.auth.usecase;

import com.example.myapplication.features.auth.repository.AuthRepository;
import com.example.myapplication.features.profiles.model.Profile;
import com.example.myapplication.features.profiles.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;



public class RegisterUseCase {

    private final AuthRepository authRepository;
    private final ProfileRepository profileRepository;

    @Inject
    public RegisterUseCase(
            AuthRepository authRepository,
            ProfileRepository profileRepository
    ){
        this.authRepository = authRepository;
        this.profileRepository = profileRepository;
    }


    public Task<Void> execute(
            String name,
            String email,
            String password
    ){

        return authRepository.register(email, password)
                .onSuccessTask(authResult -> {

                    FirebaseUser user = authResult.getUser();

                    Profile profile = new Profile();

                    profile.setUid(user.getUid());
                    profile.setName(name);
                    profile.setEmail(email);

                    return profileRepository.createProfile(profile);
                });
    }
}