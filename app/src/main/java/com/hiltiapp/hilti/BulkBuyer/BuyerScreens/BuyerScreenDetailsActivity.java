package com.hiltiapp.hilti.BulkBuyer.BuyerScreens;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hilti.BulkBuyer.BuyerSkins.BuyerSkinDetailsActivity;
import com.hiltiapp.hilti.MainActivity;
import com.hiltiapp.hilti.R;
import com.hiltiapp.hilti.Screens.ScreenDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuyerScreenDetailsActivity extends AppCompatActivity {
    private TextView name, code, price, extraPrice, description;
    private EditText notes;

    private ImageView image;
    private Button addToCartBtn;
     private String screenId = "", selectedBrand, selectedModel;
    private DatabaseReference ScreensRef, ModelsRef, BrandsRef;
    private String screenName, screenCode, screenDescription, screenPrice, screenExtraPrice, screenImage, screenNotes, screenTotalPrice;
    private int totalPrice;

    //Quantity
    private EditText quantityEditText;
    private Button increment, decrement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_screen_details);
        screenId = getIntent().getStringExtra("screenId");
        ScreensRef = FirebaseDatabase.getInstance().getReference().child("Screen Products").child(screenId);
        BrandsRef = FirebaseDatabase.getInstance().getReference().child("Screen Brands");
        ModelsRef = FirebaseDatabase.getInstance().getReference().child("Screen Models");


        name = (TextView) findViewById(R.id.screen_details_name);
        code = (TextView) findViewById(R.id.screen_details_code);
        description = (TextView) findViewById(R.id.screen_details_description);
        price = (TextView) findViewById(R.id.screen_details_price);
        notes = (EditText) findViewById(R.id.screen_details_notes);
        extraPrice = (TextView) findViewById(R.id.screen_details_extra_price);
        image = (ImageView) findViewById(R.id.screen_details_image);


        //Quantity
        quantityEditText = (EditText) findViewById(R.id.quantity_number);
        increment = findViewById(R.id.increment);
        decrement = findViewById(R.id.decrement);

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantityEditText.getText().toString().equals("100"))
                {}
                else {
                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantityEditText.setText(String.valueOf(quantity + 1));
                }
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantityEditText.getText().toString().equals("1"))
                {}
                else {
                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantityEditText.setText(String.valueOf(quantity - 1));
                }
            }
        });


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
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Bulk Orders").child(android_id);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    Toast.makeText(BuyerScreenDetailsActivity.this,getString(R.string.cart_label),Toast.LENGTH_SHORT).show();

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
                    screenPrice = snapshot.child("bulkPrice").getValue().toString();
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
                            ArrayAdapter<String> brandsAdapter = new ArrayAdapter<String>(BuyerScreenDetailsActivity.this, android.R.layout.simple_spinner_item, brands);
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
                                                ArrayAdapter<String> modelsAdapter = new ArrayAdapter<String>(BuyerScreenDetailsActivity.this, android.R.layout.simple_spinner_item, models);
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

        String quantity = quantityEditText.getText().toString();
        if (TextUtils.isEmpty(quantity)){
            Toast.makeText(BuyerScreenDetailsActivity.this,getString(R.string.quantity_toast),Toast.LENGTH_SHORT).show();

        }
        else if (Integer.valueOf(quantity) > 100 || Integer.valueOf(quantity) < 1) {

            Toast.makeText(BuyerScreenDetailsActivity.this,getString(R.string.quantity_toast),Toast.LENGTH_SHORT).show();

        } else {
            screenNotes = notes.getText().toString();

            if (TextUtils.isEmpty(screenNotes) || screenNotes.trim().length() == 0) {
                screenExtraPrice = "0";
            }
            totalPrice = (Integer.valueOf(quantity)) * (((Integer.valueOf(screenPrice))) + Integer.valueOf(screenExtraPrice));
            screenTotalPrice = String.valueOf(totalPrice);

            String android_id = MainActivity.android_id;

            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Bulk Cart List").child(android_id);

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
            cartMap.put("quantity", quantity);


            cartListRef.child("User View").child("Products").child(screenId)
                    .updateChildren(cartMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                cartListRef.child("Admin View").child("Products").child(screenId)
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Toast.makeText(BuyerScreenDetailsActivity.this, getString(R.string.toast_added_to_cart), Toast.LENGTH_SHORT).show();

//                                                Intent intent = new Intent(BuyerScreenDetailsActivity.this , ScreensProductsActivity.class);
//                                                startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                            }
                        }
                    });

        }
    }
}