package com.hiltiapp.hilti.BulkBuyer.BuyerWoodDesigns;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.hiltiapp.hilti.BulkBuyer.BuyerScreens.BuyerScreenDetailsActivity;
import com.hiltiapp.hilti.MainActivity;
import com.hiltiapp.hilti.R;
import com.hiltiapp.hilti.Screens.ScreensProductsActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class BuyerWoodDetailsActivity extends AppCompatActivity {


    private TextView name, code, price, extraPrice, description;
    private ImageView image;
     private Button addToCartBtn;

    private String woodProductId = "",woodCategoryId, woodCategory;
    private DatabaseReference WoodProductsRef;
    private String woodName, woodCode, woodDescription, woodPrice,woodImage, woodExtraPrice, woodNotes, woodTotalPrice;
    private int totalPrice;
    private EditText notes;

    //Quantity
    private EditText quantityEditText;
    private Button increment, decrement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_wood_details);
        woodProductId = getIntent().getStringExtra("woodProductId");
        woodCategoryId = getIntent().getStringExtra("woodCategoryId");
        woodCategory = getIntent().getStringExtra("woodCategory");

        WoodProductsRef = FirebaseDatabase.getInstance().getReference().child("Wood Products").child(woodCategoryId).child(woodProductId);


        name = (TextView) findViewById(R.id.wood_details_name);
        code = (TextView) findViewById(R.id.wood_details_code);
        description = (TextView) findViewById(R.id.wood_details_description);
        price = (TextView) findViewById(R.id.wood_details_price);
        extraPrice = (TextView) findViewById(R.id.wood_details_extra_price);
        notes = (EditText) findViewById(R.id.wood_details_notes);
        image = (ImageView) findViewById(R.id.wood_details_image);



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

        addToCartBtn = (Button) findViewById(R.id.wood_add_to_cart_btn);
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
                    Toast.makeText(BuyerWoodDetailsActivity.this,getString(R.string.cart_label),Toast.LENGTH_SHORT).show();

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

        WoodProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    woodName = snapshot.child("name").getValue().toString();
                    woodCode = snapshot.child("code").getValue().toString();
                    woodDescription = snapshot.child("description").getValue().toString();
                    woodPrice = snapshot.child("bulkPrice").getValue().toString();
                    woodExtraPrice = snapshot.child("extraPrice").getValue().toString();
                    woodImage = snapshot.child("image").getValue().toString();


                    name.setText(woodName);
                    code.setText(woodCode);
                    description.setText(woodDescription);
                    price.setText(woodPrice);
                    extraPrice.setText(woodExtraPrice);

                    Picasso.get().load(woodImage).into(image);

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
            Toast.makeText(BuyerWoodDetailsActivity.this,getString(R.string.quantity_toast),Toast.LENGTH_SHORT).show();

        }
        else if (Integer.valueOf(quantity) > 100 || Integer.valueOf(quantity) < 1) {

            Toast.makeText(BuyerWoodDetailsActivity.this,getString(R.string.quantity_toast),Toast.LENGTH_SHORT).show();

        } else {
            woodNotes = notes.getText().toString();

            if (TextUtils.isEmpty(woodNotes) || woodNotes.trim().length() == 0) {
                woodExtraPrice = "0";
            }
            totalPrice = (Integer.valueOf(quantity)) * (((Integer.valueOf(woodPrice))) + Integer.valueOf(woodExtraPrice));
            woodTotalPrice = String.valueOf(totalPrice);


            String android_id = MainActivity.android_id;

            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Bulk Cart List").child(android_id);

            final HashMap<String, Object> cartMap = new HashMap<>();
            cartMap.put("id", woodProductId);
            cartMap.put("name", woodName);
            cartMap.put("code", woodCode);
            cartMap.put("description", woodDescription);
            cartMap.put("price", woodPrice);
            cartMap.put("extraPrice", woodExtraPrice);
            cartMap.put("totalPrice", woodTotalPrice);
            cartMap.put("notes", woodNotes);
            cartMap.put("category", woodCategory);
            cartMap.put("quantity", quantity);


            cartListRef.child("User View").child("Products").child(woodProductId)
                    .updateChildren(cartMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                cartListRef.child("Admin View").child("Products").child(woodProductId)
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(BuyerWoodDetailsActivity.this, getString(R.string.toast_added_to_cart), Toast.LENGTH_SHORT).show();

//                                                Intent intent = new Intent(BuyerWoodDetailsActivity.this , ScreensProductsActivity.class);
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