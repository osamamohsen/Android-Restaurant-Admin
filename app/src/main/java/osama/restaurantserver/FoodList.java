package osama.restaurantserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import Interface.ItemClickListner;
import Models.Category;
import Models.Food;
import ViewHolder.FoodViewHolder;
import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RelativeLayout rootLayout;
    RecyclerView.LayoutManager layoutManager;
    AlertDialog.Builder alertDialog;
    MaterialEditText edtName,edtDescription,edtDiscount,edtPrice;
    FButton btnSelect,btnUpload;

    FloatingActionButton fab;
    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    Food newFood;
    private final  int PICK_IMAGE_REQUEST = 91;
    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        btnSelect = (FButton) findViewById(R.id.btnSelect);
        btnUpload = (FButton) findViewById(R.id.btnUpload);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFoodDialog();
            }
        });

        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty()){
            loadListFood(categoryId);
        }
    }

    private void showAddFoodDialog() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add New Food");
        alertDialog.setMessage("Please Fill Full Information");

        LayoutInflater inflater = getLayoutInflater();
        View add_menu_inflator = inflater.inflate(R.layout.add_new_food_layout,null);

        edtName = (MaterialEditText) add_menu_inflator.findViewById(R.id.edtName);
        edtDescription = (MaterialEditText) add_menu_inflator.findViewById(R.id.edtDescription);
        edtPrice = (MaterialEditText) add_menu_inflator.findViewById(R.id.edtPrice);
        edtDiscount = (MaterialEditText) add_menu_inflator.findViewById(R.id.edtDiscount);

        btnSelect = (FButton) add_menu_inflator.findViewById(R.id.btnSelect);
        btnUpload = (FButton) add_menu_inflator.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_inflator);
        alertDialog.setIcon(R.drawable.cart);

        //set Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //Connect to Firebase
                if(newFood != null){
                    String message = "";
                    if(newFood.getName().toString() == "")
                        message +="food name is required ";
                    if(newFood.getImage().toString() == "")
                        message +="food image is required ";
                    if(newFood.getDescription().toString() == "")
                        message +="food description is required ";
                    if(newFood.getDiscount().toString() == "")
                        message +="food discount is required ";
                    if(newFood.getPrice().toString() == "")
                        message +="food price is required ";
                    if(message.isEmpty()){
                        foodList.push().setValue(newFood);
                        Snackbar.make(rootLayout,"Food "+newFood.getName()+" was added",Snackbar.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(FoodList.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();

        alert.show();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));


    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading....");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newFood = new Food(
                                            edtName.getText().toString(),
                                            uri.toString(),
                                            edtDescription.getText().toString(),
                                            edtPrice.getText().toString(),
                                            edtDiscount.getText().toString(),
                                            categoryId
                                    );
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Failed!!! "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double uploaded = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded "+(int)uploaded+" %");
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }//end if
    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();//refresh data
        recyclerView.setAdapter(adapter);
    }

}
