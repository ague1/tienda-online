package com.example.myapplication.features.main.presentation;

import android.content.Intent;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.features.auth.presentation.activity.LoginActivity;
import com.example.myapplication.features.auth.application.usecase.AuthUseCase;
import com.example.myapplication.features.order.presentation.fragment.ClientOrderStatusFragment;
import com.example.myapplication.features.profiles.presentation.fragment.AccountFragment;
import com.example.myapplication.features.cart.presentation.fragment.CartFragment;
import com.example.myapplication.features.product.presentation.home.ClientHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private  BottomNavigationView bottomNavView;
    @Inject
    AuthUseCase authUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavView = findViewById(R.id.bottomNaV);
        initNavigation();

        if (savedInstanceState == null) {
            bottomNavView.setSelectedItemId(R.id.action_home);
        }

        checkAuthentication();
        
    }

    private void checkAuthentication() {

        if (!authUseCase.isLogged()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initNavigation() {
        bottomNavView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.action_home) {
                selectedFragment = new ClientHomeFragment();
            } else if (id == R.id.action_product) {

                if (!authUseCase.isLogged()) {
                    goToLogin();
                    return false;
                }

                selectedFragment = new ClientOrderStatusFragment();

            } else if (id == R.id.action_car) {

                if (!authUseCase.isLogged()) {
                    goToLogin();
                    return false;
                }

                selectedFragment = new CartFragment();

            } else if (id == R.id.action_account) {

                if (!authUseCase.isLogged()) {
                    goToLogin();
                    return false;
                }

                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.frame_layout,
                                selectedFragment
                        )
                        .commit();
                return true;
            }
            return false;
        });
    }
    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}