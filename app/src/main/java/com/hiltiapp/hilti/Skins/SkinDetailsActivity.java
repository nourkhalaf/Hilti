package com.hiltiapp.hilti.Skins;

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
import com.hiltiapp.hilti.Screens.ScreenDetailsActivity;
import com.hiltiapp.hilti.Screens.ScreensProductsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkinDetailsActivity extends AppCompatActivity {

    private TextView name, code, price,extraPrice, description;
    private EditText notes;
    private ImageView image;
    private Button addToCartBtn;

    private String skinId = "", selectedBrand, selectedModel;
    private DatabaseReference SkinsRef, ModelsRef, BrandsRef;

    String skinName, skinCode, skinDescription, skinPrice, skinExtraPrice, skinImage, skinNotes, skinTotalPrice;
    private int totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_details);
        skinId = getIntent().getStringExtra("skinId");


        SkinsRef = FirebaseDatabase.getInstance().getReference().child("Skin Products").child(skinId);
        BrandsRef = FirebaseDatabase.getInstance().getReference().child("Screen Brands");
        ModelsRef = FirebaseDatabase.getInstance().getReference().child("Screen Models");


        name = (TextView) findViewById(R.id.skin_details_name);
        code = (TextView) findViewById(R.id.skin_details_code);
        description = (TextView) findViewById(R.id.skin_details_description);
        price = (TextView) findViewById(R.id.skin_details_price);
        extraPrice = (TextView) findViewById(R.id.skin_details_extra_price);
        notes = (EditText) findViewById(R.id.skin_details_notes);
        image = (ImageView) findViewById(R.id.skin_details_image);

        addToCartBtn = (Button) findViewById(R.id.skin_add_to_cart_btn);
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
                    Toast.makeText(SkinDetailsActivity.this,getString(R.string.cart_label),Toast.LENGTH_SHORT).show();

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

        SkinsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    skinName = snapshot.child("name").getValue().toString();
                    skinCode = snapshot.child("code").getValue().toString();
                    skinDescription = snapshot.child("description").getValue().toString();
                    skinPrice = snapshot.child("price").getValue().toString();
                    skinExtraPrice = snapshot.child("extraPrice").getValue().toString();
                    skinImage = snapshot.child("image").getValue().toString();


                    name.setText(skinName);
                    code.setText(skinCode);
                    description.setText(skinDescription);
                    price.setText(skinPrice);
                    extraPrice.setText(skinExtraPrice);

                    Picasso.get().load(skinImage).into(image);

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
                            ArrayAdapter<String> brandsAdapter = new ArrayAdapter<String>(SkinDetailsActivity.this, android.R.layout.simple_spinner_item, brands);
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
                                                ArrayAdapter<String> modelsAdapter = new ArrayAdapter<String>(SkinDetailsActivity.this, android.R.layout.simple_spinner_item, models);
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
        skinNotes = notes.getText().toString();



        String android_id = MainActivity.android_id;



        if (TextUtils.isEmpty(skinNotes) || skinNotes.trim().length() == 0)
        {
            skinExtraPrice = "0";
        }
        totalPrice = ((Integer.valueOf(skinPrice))) + Integer.valueOf(skinExtraPrice);
        skinTotalPrice = String.valueOf(totalPrice);
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child(android_id);

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("id", skinId);
        cartMap.put("name", skinName);
        cartMap.put("code", skinCode);
        cartMap.put("description", skinDescription);
        cartMap.put("price", skinPrice);
        cartMap.put("extraPrice", skinExtraPrice);
        cartMap.put("totalPrice", skinTotalPrice);
        cartMap.put("model", selectedModel);
        cartMap.put("brand", selectedBrand);
        cartMap.put("notes", skinNotes);



        cartListRef.child("User View").child("Products").child(skinId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child("Products").child(skinId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(SkinDetailsActivity.this,getString(R.string.toast_added_to_cart),Toast.LENGTH_SHORT).show();

//                                                Intent intent = new Intent(SkinDetailsActivity.this , ScreensProductsActivity.class);
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