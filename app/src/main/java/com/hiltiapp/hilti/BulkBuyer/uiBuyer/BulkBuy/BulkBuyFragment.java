package com.hiltiapp.hilti.BulkBuyer.uiBuyer.BulkBuy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.hiltiapp.hilti.MainActivity;

import com.hiltiapp.hilti.Prevalent.Prevalent;
import com.hiltiapp.hilti.R;

import io.paperdb.Paper;



public class BulkBuyFragment extends Fragment {


    private Button logoutBtn;

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bulk_buy_home, container, false);

        Paper.init(getActivity());

        logoutBtn = (Button) root.findViewById(R.id.logout_btn);






        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                                getString(R.string.logout),
                                getString(R.string.cancel)
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(  getString(R.string.logout_alert));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0)
                        {

                            AuthUI.getInstance()
                                    .signOut(getActivity())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                           Toast.makeText(getActivity(),  getString(R.string.toast_logout),Toast.LENGTH_SHORT).show();

                                           Paper.book().write(Prevalent.BuyerPhoneKey,"");
                                           Paper.book().write(Prevalent.BuyerNameKey,"");


                                           Intent intent = new Intent(getActivity(), MainActivity.class);
                                           startActivity(intent);
                                           getActivity().finish();
                                        }
                                    });
                        }
                        if (i == 1)
                        {


                        }


                    }
                });
                builder.show();

            }
        });




        return root;
    }



}