package com.example.myapplication.app.common.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.app.client.fragment.ClientOrderStatusFragment;
import com.example.myapplication.app.common.fragment.AccountFragment;
import com.example.myapplication.app.client.fragment.CartFragment;
import com.example.myapplication.app.client.activity.ClientMainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    private  BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        bottomNavView = findViewById(R.id.bottomNaV);




        bottomNavView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectFragment = null;

            if (id == R.id.action_home){
                selectFragment = new ClientMainActivity();

            } else if (id == R.id.action_product) {
                if (user == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return false;
                }
                selectFragment = new ClientOrderStatusFragment();

            } else if (id == R.id.action_car) {
                if (user == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return false;
                }
                selectFragment = new CartFragment();

            } else if (id == R.id.action_account) {
                if (user == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return false;
                }
                selectFragment = new AccountFragment();
            }

            if (selectFragment != null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, selectFragment)
                        .commit();
                return true;
            }

            return false;
        });

        if (savedInstanceState == null) {
            bottomNavView.setSelectedItemId(R.id.action_home);
        }

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}