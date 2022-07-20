package com.hiltiapp.hilti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hilti.BulkBuyer.BuyerScreens.BuyerScreenDetailsActivity;
import com.hiltiapp.hilti.Model.Product;
import com.hiltiapp.hilti.ViewHolder.ProductViewHolder;
import com.hiltiapp.hilti.ViewHolder.SliderViewHolder;
import com.hiltiapp.hilti.WoodDesigns.WoodDetailsActivity;
import com.hiltiapp.hilti.WoodDesigns.WoodProductsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SliderActivity extends AppCompatActivity {

    private List<The_Slide_Items_Model_Class> listItems;
    private ViewPager page;
    private TabLayout tabLayout;

    private DatabaseReference SliderImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        page = findViewById(R.id.my_pager) ;
        tabLayout = findViewById(R.id.my_tablayout);

        SliderImagesRef = FirebaseDatabase.getInstance().getReference().child("Slider Images");

        // Make a copy of the slides you'll be presenting.
        listItems = new ArrayList<>() ;





        SliderImagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot imageSnapshot: dataSnapshot.getChildren()) {
                    String image = imageSnapshot.getValue(String.class);
                    listItems.add(new The_Slide_Items_Model_Class(image));
                }
                The_Slide_items_Pager_Adapter  adapter = new The_Slide_items_Pager_Adapter(SliderActivity.this, listItems);
                page.setAdapter(adapter);

                tabLayout.setupWithViewPager(page,true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}