package com.hiltiapp.hilti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.travijuu.numberpicker.library.NumberPicker;

public class testNumber extends AppCompatActivity {

    Button d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        EditText number  = (EditText) findViewById(R.id.quantity_number);

        Button decrement = findViewById(R.id.decrement);
        Button increment = findViewById(R.id.increment);

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.getText().toString().equals("100"))
                {}
                else {
                    int quantity = Integer.parseInt(number.getText().toString());
                    number.setText(String.valueOf(quantity + 1));
                }
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.getText().toString().equals("1"))
                {}
                else {
                    int quantity = Integer.parseInt(number.getText().toString());
                    number.setText(String.valueOf(quantity - 1));
                }
            }
        });

        d = findViewById(R.id.test_btn);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(testNumber.this,number.getText().toString(),Toast.LENGTH_SHORT ).show();
            }
        });
    }
}