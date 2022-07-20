package com.hiltiapp.hilti;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hiltiapp.hilti.BulkBuyer.BuyerActivity;
import com.hiltiapp.hilti.Model.Buyer;
import com.hiltiapp.hilti.Prevalent.Prevalent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Locale;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    public static String android_id;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_bulk_buyers)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
         NavigationUI.setupWithNavController(navView, navController);

        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);





        Paper.init(this);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {

            String UserPhoneKey = Paper.book().read(Prevalent.BuyerPhoneKey);
            String UserNameKey = Paper.book().read(Prevalent.BuyerNameKey);



            if (UserPhoneKey != "" && UserNameKey != "")
            {
                if (!TextUtils.isEmpty(UserPhoneKey)  &&  !TextUtils.isEmpty(UserNameKey))
                {
                    Buyer model = new Buyer();
                    model.setName(UserNameKey);
                    model.setPhone(UserPhoneKey);
                    Prevalent.currentBuyer = model;
                    showHomeFragment();

                }
            }
            else
            {

            }


        }

        else {
         }


    }
    public void showHomeFragment(){
        Intent intent = new Intent(MainActivity.this, BuyerActivity.class);
        startActivity(intent);
        finish();

    }



}