package com.hiltiapp.hilti.BulkBuyer.BulkDevices;

import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class BuyerDeviceDetailsActivity extends AppCompatActivity {


    private TextView name, code, price, extraPrice, description;
     private ImageView image;
    private EditText notes, deviceModelTxt, height, width;
    private Button addToCartBtn;
    private String deviceProductId = "",deviceCategoryId, deviceCategory;
    private DatabaseReference DeviceProductsRef;
    private String woodName, woodCode, woodDescription, woodPrice,woodImage, woodExtraPrice, woodNotes, woodTotalPrice, deviceModel, deviceHeight, deviceWidth;
    private int totalPrice;

    //Quantity
    private EditText quantityEditText;
    private Button increment, decrement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_device_details);
        deviceProductId = getIntent().getStringExtra("deviceProductId");
        deviceCategoryId = getIntent().getStringExtra("deviceCategoryId");
        deviceCategory = getIntent().getStringExtra("deviceCategory");

        DeviceProductsRef = FirebaseDatabase.getInstance().getReference().child("Device Products").child(deviceCategoryId).child(deviceProductId);


        name = (TextView) findViewById(R.id.wood_details_name);
        code = (TextView) findViewById(R.id.wood_details_code);
        description = (TextView) findViewById(R.id.wood_details_description);
        price = (TextView) findViewById(R.id.wood_details_price);
        extraPrice = (TextView) findViewById(R.id.wood_details_extra_price);
        notes = (EditText) findViewById(R.id.wood_details_notes);
        deviceModelTxt = (EditText) findViewById(R.id.device_details_model);
        height = (EditText) findViewById(R.id.device_details_height);
        width = (EditText) findViewById(R.id.device_details_width);
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
                    Toast.makeText(BuyerDeviceDetailsActivity.this,getString(R.string.cart_label),Toast.LENGTH_SHORT).show();

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

        DeviceProductsRef.addValueEventListener(new ValueEventListener() {
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
            Toast.makeText(BuyerDeviceDetailsActivity.this,getString(R.string.quantity_toast),Toast.LENGTH_SHORT).show();

        }
        else if (Integer.valueOf(quantity) > 100 || Integer.valueOf(quantity) < 1) {

            Toast.makeText(BuyerDeviceDetailsActivity.this,getString(R.string.quantity_toast),Toast.LENGTH_SHORT).show();

        } else {
        woodNotes = notes.getText().toString();
        deviceModel = deviceModelTxt.getText().toString();
        deviceHeight = height.getText().toString();
        deviceWidth = width.getText().toString();

        if (TextUtils.isEmpty(deviceModel))
        {
           Toast.makeText(this,getString(R.string.toast_add_device_model) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(deviceHeight))
        {
            Toast.makeText(this,getString(R.string.toast_add_device_height) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(deviceWidth))
        {
            Toast.makeText(this,getString(R.string.toast_add_device_width) , Toast.LENGTH_SHORT).show();
        }
        else {

            String deviceInfo = deviceCategory + ", " + deviceModel + " :\n "+getString(R.string.height) + deviceHeight + " , " +getString(R.string.width) + deviceWidth;

            String android_id = MainActivity.android_id;

            if (TextUtils.isEmpty(woodNotes) || woodNotes.trim().length() == 0) {
                woodExtraPrice = "0";
            }
            totalPrice = (Integer.valueOf(quantity)) * ((Integer.valueOf(woodPrice))) + Integer.valueOf(woodExtraPrice);
            woodTotalPrice = String.valueOf(totalPrice);

            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Bulk Cart List").child(android_id);

            final HashMap<String, Object> cartMap = new HashMap<>();
            cartMap.put("id", deviceProductId);
            cartMap.put("name", woodName);
            cartMap.put("code", woodCode);
            cartMap.put("description", woodDescription);
            cartMap.put("price", woodPrice);
            cartMap.put("extraPrice", woodExtraPrice);
            cartMap.put("totalPrice", woodTotalPrice);
            cartMap.put("notes", woodNotes);
            cartMap.put("category", deviceInfo);
            cartMap.put("quantity", quantity);


            cartListRef.child("User View").child("Products").child(deviceProductId)
                    .updateChildren(cartMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                cartListRef.child("Admin View").child("Products").child(deviceProductId)
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Toast.makeText(BuyerDeviceDetailsActivity.this, getString(R.string.toast_added_to_cart), Toast.LENGTH_SHORT).show();


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

}