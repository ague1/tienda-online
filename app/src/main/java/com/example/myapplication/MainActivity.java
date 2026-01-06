package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.account.AccountFragment;
import com.example.myapplication.carrito.CarFragment;
import com.example.myapplication.home.HomeFragment;
import com.example.myapplication.pedidos.Pedido_Fragment;
import com.example.myapplication.user_auth.Login;
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
                selectFragment = new HomeFragment();

            } else if (id == R.id.action_product) {
                if (user == null){
                    startActivity(new Intent(MainActivity.this, Login.class));
                    return false;
                }else {
                    selectFragment = new Pedido_Fragment();
                }

            } else if (id == R.id.action_car) {
                if (user == null){
                    startActivity(new Intent(MainActivity.this,Login.class));
                    return false;
                }else {
                    selectFragment = new CarFragment();
                }

            } else if (id == R.id.action_account) {
                if (user == null){
                    startActivity(new Intent(MainActivity.this, Login.class));
                    return false;
                }else {
                    selectFragment = new AccountFragment();
                }
            }

            if (selectFragment != null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, selectFragment)
                        .commit();
            }

            return false;
        });



        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }
}