package com.hiltiapp.hilti.WoodDesigns;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiltiapp.hilti.Interface.ItemClickListener;
import com.hiltiapp.hilti.Model.Product;
import com.hiltiapp.hilti.R;
import com.hiltiapp.hilti.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class WoodProductsActivity extends AppCompatActivity {


    private DatabaseReference WoodProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

     private TextView productsTitle;
    private String categoryId, categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wood_products);

        categoryId = getIntent().getStringExtra("woodCategoryId");
        categoryName = getIntent().getStringExtra("woodCategoryName");


        productsTitle = findViewById(R.id.wood_products_title);
        productsTitle.setText(categoryName);


        WoodProductsRef = FirebaseDatabase.getInstance().getReference().child("Wood Products").child(categoryId);

        recyclerView = findViewById(R.id.wood_categories_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(WoodProductsRef, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                        holder.productName.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.productImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WoodProductsActivity.this, WoodDetailsActivity.class);
                                intent.putExtra("woodProductId",model.getId());
                                intent.putExtra("woodCategoryId",categoryId);
                                intent.putExtra("woodCategory",categoryName);
                                  startActivity(intent);
                            }
                        });




                    }


                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }




}