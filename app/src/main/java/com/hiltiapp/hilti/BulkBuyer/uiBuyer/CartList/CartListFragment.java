package com.hiltiapp.hilti.BulkBuyer.uiBuyer.CartList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hilti.BulkBuyer.ConfirmBulkOrderActivity;
import com.hiltiapp.hilti.ConfirmOrderActivity;
import com.hiltiapp.hilti.MainActivity;
import com.hiltiapp.hilti.Model.Product;
import com.hiltiapp.hilti.R;
import com.hiltiapp.hilti.ViewHolder.CartViewHolder;

public class CartListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private String android_id;
    private int overTotalPrice = 0;
    private CartListViewModel cartListViewModel;
    private TextView cartLabel;
    private ImageView cartLabelImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartListViewModel =
                ViewModelProviders.of(this).get(CartListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart_list, container, false);

        android_id = MainActivity.android_id;

        recyclerView = root.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = (Button) root.findViewById(R.id.cart_next_process_btn);
        cartLabel = (TextView) root.findViewById(R.id.cart_label);
        cartLabelImage = (ImageView) root.findViewById(R.id.cart_label_image);


        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (overTotalPrice==0)
                {
                    Toast.makeText(getActivity(),getString(R.string.toast_empty_cart),Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), ConfirmBulkOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                    intent.putExtra("android_id",android_id);
                    startActivity(intent);
                }
            }
        });
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        checkOrderState();

        overTotalPrice = 0;

        String android_id = MainActivity.android_id;

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Bulk Cart List").child(android_id).child("User View").child("Products");

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(cartListRef ,Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull final Product model)
                    {

                        int totalPrice = ((Integer.valueOf(model.getTotalPrice())));

                        overTotalPrice= overTotalPrice + totalPrice;

                        holder.productName.setText(model.getName());
                        holder.productQuantity.setText(getString(R.string.product_details_quantity)+model.getQuantity());

                         holder.productCode.setText(getString(R.string.product_details_code) +model.getCode());

                        if (!TextUtils.isEmpty(model.getBrand()) && !TextUtils.isEmpty(model.getModel()))
                        {
                            holder.productBrand.setText(model.getBrand());
                            holder.productModel.setText(model.getModel());
                        }
                        if (!TextUtils.isEmpty(model.getCategory()))
                        {
                            holder.productCategory.setText(model.getCategory());

                        }
                        if (!TextUtils.isEmpty(model.getNotes()))
                        {
                            holder.productNotes.setText(model.getNotes());
                        }

                        holder.productTotalPrice.setText(model.getTotalPrice());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                getString(R.string.remove),
                                                getString(R.string.cancel)
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle( getString(R.string.alert_remove_item));
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i)
                                    {
                                        if(i == 0)
                                        {
                                            cartListRef.child(model.getId())

                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                final DatabaseReference adminView = FirebaseDatabase.getInstance().getReference().child("Cart List").child(android_id).child("Admin View").child("Products");
                                                                adminView.child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(getActivity(), getString(R.string.toast_remove_item),Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });


                                                            }

                                                        }
                                                    });
                                        }
                                        if(i == 1)
                                        {



                                        }

                                    }
                                });
                                builder.show();

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void checkOrderState()
    {


        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Bulk Orders").child(android_id);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    cartLabel.setVisibility(View.VISIBLE);
                    cartLabelImage.setVisibility(View.VISIBLE);
                    nextProcessBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}