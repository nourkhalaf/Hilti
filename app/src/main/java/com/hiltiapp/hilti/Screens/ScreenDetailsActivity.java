package com.hiltiapp.hilti.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hilti.MainActivity;
import com.hiltiapp.hilti.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ScreenDetailsActivity extends AppCompatActivity {
    private TextView name, code, price, extraPrice, description;
    private EditText notes;
    private ImageView image;
    private Button addToCartBtn;
    private String screenId = "", selectedBrand, selectedModel;
    private DatabaseReference ScreensRef, ModelsRef, BrandsRef;
    private String screenName, screenCode, screenDescription, screenPrice, screenExtraPrice, screenImage, screenNotes, screenTotalPrice;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_details);
        screenId = getIntent().getStringExtra("screenId");

        ScreensRef = FirebaseDatabase.getInstance().getReference().child("Screen Products").child(screenId);
        BrandsRef = FirebaseDatabase.getInstance().getReference().child("Screen Brands");
        ModelsRef = FirebaseDatabase.getInstance().getReference().child("Screen Models");


        name = (TextView) findViewById(R.id.screen_details_name);
        code = (TextView) findViewById(R.id.screen_details_code);
        description = (TextView) findViewById(R.id.screen_details_description);
        price = (TextView) findViewById(R.id.screen_details_price);
        extraPrice = (TextView) findViewById(R.id.screen_details_extra_price);
        notes = (EditText) findViewById(R.id.screen_details_notes);
        image = (ImageView) findViewById(R.id.screen_details_image);

        addToCartBtn = (Button) findViewById(R.id.screen_add_to_cart_btn);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkOrderState();

            }
        });

         displaySpecificItemInfo();

    }

    private void checkOrderState()
    {
        DatabaseReference ordersRef;
        String android_id = MainActivity.android_id;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(android_id);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    Toast.makeText(ScreenDetailsActivity.this,getString(R.string.cart_label),Toast.LENGTH_SHORT).show();

                    finish();
                }
                else {
                    addToCart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displaySpecificItemInfo() {

        ScreensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    screenName = snapshot.child("name").getValue().toString();
                    screenCode = snapshot.child("code").getValue().toString();
                    screenDescription = snapshot.child("description").getValue().toString();
                    screenPrice = snapshot.child("price").getValue().toString();
                    screenExtraPrice = snapshot.child("extraPrice").getValue().toString();
                    screenImage = snapshot.child("image").getValue().toString();



                    name.setText(screenName);
                    code.setText(screenCode);
                    description.setText(screenDescription);
                    price.setText(screenPrice);
                    extraPrice.setText(screenExtraPrice);

                    Picasso.get().load(screenImage).into(image);

                    BrandsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Is better to use a List, because you don't know the size
                            // of the iterator returned by dataSnapshot.getChildren() to
                            // initialize the array
                            final List<String> brands = new ArrayList<String>();

                            for (DataSnapshot brandSnapshot: dataSnapshot.getChildren()) {
                                String brandName = brandSnapshot.child("name").getValue(String.class);
                                brands.add(brandName);
                            }

                            Spinner brandsSpinner = (Spinner) findViewById(R.id.brands_spinner);
                            ArrayAdapter<String> brandsAdapter = new ArrayAdapter<String>(ScreenDetailsActivity.this, android.R.layout.simple_spinner_item, brands);
                            brandsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            brandsSpinner.setAdapter(brandsAdapter);
                            brandsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedBrand = brandsSpinner.getSelectedItem().toString();

                                    if(!TextUtils.isEmpty(selectedBrand)) {
                                        ModelsRef.child(selectedBrand).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                // Is better to use a List, because you don't know the size
                                                // of the iterator returned by dataSnapshot.getChildren() to
                                                // initialize the array
                                                final List<String> models = new ArrayList<String>();

                                                for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                                    String modelName = modelSnapshot.child("name").getValue(String.class);
                                                    models.add(modelName);
                                                }

                                                Spinner modelsSpinner = (Spinner) findViewById(R.id.models_spinner);
                                                ArrayAdapter<String> modelsAdapter = new ArrayAdapter<String>(ScreenDetailsActivity.this, android.R.layout.simple_spinner_item, models);
                                                modelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                modelsSpinner.setAdapter(modelsAdapter);

                                                modelsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        selectedModel = modelsSpinner.getSelectedItem().toString();
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }



                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void addToCart()
    {
        screenNotes = notes.getText().toString();


       String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child(android_id);


        if (TextUtils.isEmpty(screenNotes) || screenNotes.trim().length() == 0)
        {
            screenExtraPrice = "0";
        }
        totalPrice = ((Integer.valueOf(screenPrice))) + Integer.valueOf(screenExtraPrice);
        screenTotalPrice = String.valueOf(totalPrice);

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("id", screenId);
        cartMap.put("name", screenName);
        cartMap.put("code", screenCode);
        cartMap.put("description", screenDescription);
        cartMap.put("price", screenPrice);
        cartMap.put("extraPrice", screenExtraPrice);
        cartMap.put("totalPrice", screenTotalPrice);
        cartMap.put("model", selectedModel);
        cartMap.put("brand", selectedBrand);
        cartMap.put("notes", screenNotes);



        cartListRef.child("User View").child("Products").child(screenId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {

                            cartListRef.child("Admin View").child("Products").child(screenId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                            Toast.makeText(ScreenDetailsActivity.this,getString(R.string.toast_added_to_cart),Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(ScreenDetailsActivity.this , ScreensProductsActivity.class);
//                                            startActivity(intent);
                                            finish();
                                            }
                                        }
                                    });




                        }
                    }
                });


    }
}