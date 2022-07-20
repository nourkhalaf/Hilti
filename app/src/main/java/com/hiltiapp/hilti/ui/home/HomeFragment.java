package com.hiltiapp.hilti.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hilti.Devices.DeviceCategoriesActivity;
import com.hiltiapp.hilti.MainActivity;
import com.hiltiapp.hilti.R;
import com.hiltiapp.hilti.Screens.ScreensProductsActivity;
import com.hiltiapp.hilti.Skins.SkinsProductsActivity;
import com.hiltiapp.hilti.The_Slide_Items_Model_Class;
import com.hiltiapp.hilti.The_Slide_items_Pager_Adapter;
import com.hiltiapp.hilti.WoodDesigns.WoodCategoriesActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimerTask;

public class HomeFragment extends Fragment {


    private HomeViewModel homeViewModel;
    private ImageView mobileScreens, mobileSkins, devicesScreen, woodDesigns;

    private List<The_Slide_Items_Model_Class> listItems;
    private ViewPager page;
    private TabLayout tabLayout;

    private DatabaseReference SliderImagesRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //Slider

        page = root.findViewById(R.id.my_pager) ;
        tabLayout = root.findViewById(R.id.my_tablayout);

        SliderImagesRef = FirebaseDatabase.getInstance().getReference().child("Slider Images");

        // Make a copy of the slides you'll be presenting.
        listItems = new ArrayList<>() ;
        SliderImagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //final List<String> brands = new ArrayList<String>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String image = snapshot.child("image").getValue(String.class);

                    listItems.add(new The_Slide_Items_Model_Class(image));
                }
                The_Slide_items_Pager_Adapter adapter = new The_Slide_items_Pager_Adapter(getActivity(), listItems);
                page.setAdapter(adapter);

                // The_slide_timer
                java.util.Timer timer = new java.util.Timer();
                timer.scheduleAtFixedRate(new The_slide_timer(),2000,3000);
                tabLayout.setupWithViewPager(page,true);
                tabLayout.setupWithViewPager(page,true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mobileScreens = root.findViewById(R.id.mobile_screens);
        mobileSkins = root.findViewById(R.id.mobile_skin);
        devicesScreen = root.findViewById(R.id.devices_screens);
        woodDesigns = root.findViewById(R.id.wood_designs);

        mobileScreens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ScreensProductsActivity.class);
                startActivity(intent);
            }
        });

        mobileSkins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SkinsProductsActivity.class);
                startActivity(intent);            }
        });

        devicesScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceCategoriesActivity.class);
                startActivity(intent);
            }
        });

        woodDesigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WoodCategoriesActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }


    public class The_slide_timer extends TimerTask {
        @Override
        public void run() {

            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (page.getCurrentItem() < listItems.size() - 1) {
                            page.setCurrentItem(page.getCurrentItem() + 1);
                        } else
                            page.setCurrentItem(0);
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.language, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.language_english)
        {
            setApplicationLocale("en");
        }
        if (id == R.id.language_arabic)
        {
            setApplicationLocale("ar");
        }
        return super.onOptionsItemSelected(item);
    }


    private void setApplicationLocale(String locale) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(locale.toLowerCase()));
        } else {
            config.locale = new Locale(locale.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
        Intent refresh = new Intent(getActivity(), MainActivity.class);
        startActivity(refresh);
        getActivity().finish();
    }

}