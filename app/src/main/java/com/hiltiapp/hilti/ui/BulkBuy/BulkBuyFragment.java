package com.hiltiapp.hilti.ui.BulkBuy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hilti.BulkBuyer.BuyerActivity;
import com.hiltiapp.hilti.Model.Buyer;
import com.hiltiapp.hilti.Prevalent.Prevalent;
import com.hiltiapp.hilti.R;

import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;

public class BulkBuyFragment extends Fragment {

    private final static int LOGIN_REQUEST_CODE = 7171;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    private Button signBtn, registerBtn;
    private LinearLayout registerLayout, homeLayout;
    private TextView label, homePhone, homeName, homeAddress, homeEmail, buyerPhone;
    private EditText buyerName, buyerEmail, buyerAddress , buyerShop;




    FirebaseDatabase database;
    DatabaseReference userRef;


    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bulk_buy, container, false);

        Paper.init(getActivity());

        signBtn = (Button) root.findViewById(R.id.sign_with_phone_number_btn);
        registerBtn = (Button) root.findViewById(R.id.register_btn);
        registerLayout = (LinearLayout) root.findViewById(R.id.buyer_register_layout);
        label = (TextView) root.findViewById(R.id.bulk_buy_label);
        buyerName = (EditText) root.findViewById(R.id.buyer_name);
        buyerPhone= (TextView) root.findViewById(R.id.buyer_phone);
        buyerEmail = (EditText) root.findViewById(R.id.buyer_email);
        buyerAddress = (EditText) root.findViewById(R.id.buyer_address);
        buyerShop = (EditText) root.findViewById(R.id.buyer_shop);



        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");


        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());

        firebaseAuth = FirebaseAuth.getInstance();



        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {
            showRegisterLayout();
        }


            signBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                            .Builder(R.layout.activity_main)
                            .setPhoneButtonId(R.id.sign_with_phone_number_btn)
                            .build();

                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAuthMethodPickerLayout(authMethodPickerLayout)
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(providers)
                            .build(), LOGIN_REQUEST_CODE);
                }
            });

        return root;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST_CODE)
        {
            if (data!=null) {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (resultCode == RESULT_OK) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        buyerPhone.setText(user.getPhoneNumber());
                        checkRegistration(user.getPhoneNumber());

                    }



                } else {
                    Toast.makeText(getActivity(), "[ERROR:]" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);

            }
        }
    }

    private void checkRegistration(String phoneNumber) {

        userRef.child(phoneNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    Buyer buyer = snapshot.getValue(Buyer.class);

                    Prevalent.currentBuyer = buyer;
                    Paper.book().write(Prevalent.BuyerNameKey, buyer.getName());
                    Paper.book().write(Prevalent.BuyerPhoneKey, buyer.getPhone());

                    showHomeFragment();

                }

                else {
                    showRegisterLayout();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void showRegisterLayout() {

        signBtn.setEnabled(false);
        signBtn.setVisibility(View.GONE);
        label.setText(getString(R.string.bulk_register_label));

        registerLayout.setVisibility(View.VISIBLE);



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(buyerName.getText().toString()))
                {
                    Toast.makeText(getActivity(),getString(R.string.bulk_register_name),Toast.LENGTH_SHORT).show();
                    return;
                }else  if(TextUtils.isEmpty(buyerEmail.getText().toString()))
                {
                    Toast.makeText(getActivity(),getString(R.string.bulk_register_email),Toast.LENGTH_SHORT).show();
                    return;
                }
                else  if(TextUtils.isEmpty(buyerAddress.getText().toString()))
                {
                    Toast.makeText(getActivity(),getString(R.string.bulk_register_address),Toast.LENGTH_SHORT).show();
                    return;
                }
                else  if(TextUtils.isEmpty(buyerShop.getText().toString()))
                {
                    Toast.makeText(getActivity(),getString(R.string.bulk_register_shop),Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    final Buyer model = new Buyer();
                    model.setName(buyerName.getText().toString());
                    model.setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    model.setEmail(buyerEmail.getText().toString());
                    model.setAddress(buyerAddress.getText().toString());
                    model.setShopName(buyerShop.getText().toString());
                    model.setState("notActive");

                    Prevalent.currentBuyer = model;
                    Paper.book().write(Prevalent.BuyerNameKey, buyerName.getText().toString());
                    Paper.book().write(Prevalent.BuyerPhoneKey, buyerPhone.getText().toString());


                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .setValue(model)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                     Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                             Toast.makeText(getActivity(),getString(R.string.toast_register_successfully), Toast.LENGTH_SHORT)
                                    .show();

                            showHomeFragment();

                        }
                    });


                }
            }
        });
    }

    public void showHomeFragment(){
        Intent intent = new Intent(getActivity(), BuyerActivity.class);
        startActivity(intent);





    }

}