package com.hiltiapp.hilti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.hiltiapp.hilti.Skins.SkinDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ConfirmOrderActivity extends AppCompatActivity {

    private EditText nameEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;
    private TextView phoneEditText, totalPrice, shippingExpensive, purchasesPrice;
    private String totalAmount = "", android_id;
    private String selectedShippingLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        totalAmount = getIntent().getStringExtra("Total Price");
        android_id = getIntent().getStringExtra("android_id");

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shipment_name);
        phoneEditText = (TextView) findViewById(R.id.shipment_phone_number);
        totalPrice = (TextView) findViewById(R.id.total_price_txt);
        shippingExpensive = (TextView) findViewById(R.id.shipping_expensive_txt);
        purchasesPrice = (TextView) findViewById(R.id.purchases_price_txt);
        addressEditText = (EditText) findViewById(R.id.shipment_address);
        cityEditText = (EditText) findViewById(R.id.shipment_city);


        purchasesPrice.setText(totalAmount);

        int price = Integer.valueOf(purchasesPrice.getText().toString())+Integer.valueOf(shippingExpensive.getText().toString());
        totalPrice.setText(getString(R.string.le)+String.valueOf(price));

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                check();
            }
        });



        final List<String> list = new ArrayList<String>();
        list.add(getString(R.string.location1));
        list.add(getString(R.string.location2));
        list.add(getString(R.string.location3));

        Spinner shippingLocation = (Spinner) findViewById(R.id.shipping_location);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(ConfirmOrderActivity.this, android.R.layout.simple_spinner_item, list);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shippingLocation.setAdapter(adapter1);
        shippingLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedShippingLocation = shippingLocation.getSelectedItem().toString();
                if(!TextUtils.isEmpty(selectedShippingLocation)) {
                    if(selectedShippingLocation.equals(getString(R.string.location1))) {
                        shippingExpensive.setText("20");
                        int price = Integer.valueOf(purchasesPrice.getText().toString())+Integer.valueOf(shippingExpensive.getText().toString());
                        totalPrice.setText(getString(R.string.le)+String.valueOf(price));
                    }
                    else if(selectedShippingLocation.equals(getString(R.string.location2))) {
                        shippingExpensive.setText("35");
                        int price = Integer.valueOf(purchasesPrice.getText().toString())+Integer.valueOf(shippingExpensive.getText().toString());
                        totalPrice.setText(getString(R.string.le)+String.valueOf(price));
                    }
                    else if(selectedShippingLocation.equals(getString(R.string.location3))) {
                        shippingExpensive.setText("40");
                        int price = Integer.valueOf(purchasesPrice.getText().toString())+Integer.valueOf(shippingExpensive.getText().toString());
                        totalPrice.setText(getString(R.string.le)+String.valueOf(price));
                    }



                }




                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void check() {
        if(TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(ConfirmOrderActivity.this,getString(R.string.toast_add_phone_number),Toast.LENGTH_SHORT).show();
        }
        else if( phoneEditText.getText().toString().length() != 11)
        {
            Toast.makeText(ConfirmOrderActivity.this,getString(R.string.toast_correct_phone_number),Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(ConfirmOrderActivity.this,getString(R.string.toast_add_name),Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(ConfirmOrderActivity.this,getString(R.string.toast_add_address),Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(ConfirmOrderActivity.this,getString(R.string.toast_add_city),Toast.LENGTH_SHORT).show();
        }
        else if(shippingExpensive.getText().toString().equals("00"))
        {
            Toast.makeText(ConfirmOrderActivity.this,getString(R.string.toast_select_shipping_method),Toast.LENGTH_SHORT).show();
        }
        else
        {
            confirmOrder();
        }
    }

    private void confirmOrder()
    {
        final String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(android_id);

        HashMap<String,Object> ordersMap = new HashMap<>();
        ordersMap.put("id", android_id);
        ordersMap.put("totalAmount", totalPrice.getText().toString());
        ordersMap.put("name",nameEditText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("city",cityEditText.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("shippingLocation",selectedShippingLocation);


        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child(android_id)
                            .child("User View")
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                       Toast.makeText(ConfirmOrderActivity.this,getString(R.string.toast_order_message),Toast.LENGTH_SHORT).show();

                                       finish();

                                    }

                                }
                            });
                }

            }
        });


    }

}