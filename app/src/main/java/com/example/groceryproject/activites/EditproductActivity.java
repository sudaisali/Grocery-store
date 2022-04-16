package com.example.groceryproject.activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.groceryproject.R;
import com.example.groceryproject.constants.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditproductActivity extends AppCompatActivity {
private  String productId;
    private CircleImageView productimage;
    private EditText title, description, quantity, price, discountedprice, discountednote;
    private SwitchCompat discountswitch;
    private Button updatebtn;
    private TextView category;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    // IMAGE PICK CONSTANTS
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //permisssion arrays
    private String[] camerapermissions;
    private String[] storagepermissions;
//imagepick uri

    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editproduct);
        productId = getIntent().getStringExtra("productId");
        productimage = findViewById(R.id.productimage);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        discountedprice = findViewById(R.id.discountedprice);
        discountednote = findViewById(R.id.discountednote);
        discountswitch = findViewById(R.id.discountswitch);
        category = findViewById(R.id.category);
        updatebtn = findViewById(R.id.addproduct);
        firebaseAuth = FirebaseAuth.getInstance();
        loadproductdetails();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //init permission arrays
        camerapermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //if discount switch checked show price and title
        discountswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discountedprice.setVisibility(View.VISIBLE);
                    discountednote.setVisibility(View.VISIBLE);

                } else {
                    discountedprice.setVisibility(View.GONE);
                    discountednote.setVisibility(View.GONE);

                }

            }
        });
        //onclicks
        productimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialouge to pick image
                showImagePickDialouge();
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcategory();
            }
        });
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdata();
            }
        });
    }

    private void loadproductdetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users");
        reference.child(firebaseAuth.getUid()).child("products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String productId = ""+snapshot.child("productId").getValue();
                        String productTitle = ""+snapshot.child("productTitle").getValue();
                        String productDescription = ""+snapshot.child("productDescription").getValue();
                        String productCategory = ""+snapshot.child("productCategory").getValue();
                        String productQuantity = ""+snapshot.child("productQuantity").getValue();
                        String productIcon = ""+snapshot.child("productIcon").getValue();
                        String originalPrice = ""+snapshot.child("originalPrice").getValue();
                        String discountPrice = ""+snapshot.child("discountPrice").getValue();
                        String discountnote = ""+snapshot.child("discountnote").getValue();
                        String discountAvaliable = ""+snapshot.child("discountAvaliable").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        if(discountAvaliable.equals("true")){
                            discountswitch.setChecked(true);
                            discountedprice.setVisibility(View.VISIBLE);
                            discountednote.setVisibility(View.VISIBLE);

                        }
                        else{
                            discountswitch.setChecked(false);
                            discountedprice.setVisibility(View.GONE);
                            discountednote.setVisibility(View.GONE);
                        }
                        title.setText(productTitle);
                        description.setText(productDescription);
                        category.setText(productCategory);
                        quantity.setText(productQuantity);
                        price.setText(originalPrice);
                        discountedprice.setText(discountPrice);
                        discountednote.setText(discountnote);
                        try {
                            Picasso.get().load(productIcon)
                                    .placeholder(R.drawable.ic_baseline_add_shopping_cart_24)
                                    .into(productimage);
                        }catch (Exception e){
                            productimage.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String producttitle, productdescription, productcategory, productprice, productquantity, productdiscount, productdiscountnote;
    private boolean discountavaliable = false;

    private void inputdata() {
        producttitle = title.getText().toString().trim();
        productdescription = description.getText().toString().trim();
        productcategory = category.getText().toString().trim();
        productprice = price.getText().toString().trim();
        productquantity = quantity.getText().toString().trim();
        discountavaliable = discountswitch.isChecked();
        if (TextUtils.isEmpty(producttitle)) {
            Toast.makeText(this, "Title is required....", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productcategory)) {
            Toast.makeText(this, "category is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productprice)) {
            Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (discountavaliable) {
            productdiscount = discountedprice.getText().toString().trim();
            productdiscountnote = discountednote.getText().toString().trim();
            if (TextUtils.isEmpty(productdiscount)) {
                Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            productdiscount = "0";
            productdiscountnote = "";

        }
        updateproduct();


    }



    private void updateproduct() {

        progressDialog.setMessage("Updating product ....");
        progressDialog.show();
        if(image_uri == null){
            //upload without image
            HashMap<String , Object> hashMap = new HashMap<>();
            hashMap.put("productTitle",""+producttitle);
            hashMap.put("productDescription",""+productdescription);
            hashMap.put("productCategory",""+productcategory);
            hashMap.put("productQuantity",""+productquantity);
            hashMap.put("productIcon",""); // no image set empty
            hashMap.put("originalPrice",""+productprice);
            hashMap.put("discountPrice",""+productdiscount);
            hashMap.put("discountnote",""+productdiscountnote);
            hashMap.put("discountAvaliable",""+discountavaliable);

            //update to database

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("products").child(productId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(EditproductActivity.this, "Product updated success fully", Toast.LENGTH_SHORT).show();
                            cleardata();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditproductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
        else{
            //upload with image
            String filePathAndName = "product_images/"+""+productId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadimageUri = uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                HashMap<String , Object> hashMap = new HashMap<>();
                                hashMap.put("productTitle",""+producttitle);
                                hashMap.put("productDescription",""+productdescription);
                                hashMap.put("productCategory",""+productcategory);
                                hashMap.put("productQuantity",""+productquantity);
                                hashMap.put("productIcon",""+downloadimageUri);
                                hashMap.put("originalPrice",""+productprice);
                                hashMap.put("discountPrice",""+productdiscount);
                                hashMap.put("discountnote",""+productdiscountnote);
                                hashMap.put("discountAvaliable",""+discountavaliable);

                                //add to database

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("products").child(productId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditproductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                                                cleardata();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditproductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditproductActivity.this , ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void cleardata() {
        title.setText("");
        description.setText("");
        category.setText("");
        quantity.setText("");
        price.setText("");
        discountedprice.setText("");
        discountednote.setText("");
    }

    private void showcategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(constants.productCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Category = constants.productCategory[which];
                        category.setText(Category);
                    }
                }).show();
    }

    private void showImagePickDialouge() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (checkCameraPermission()) {
                                pickfromcamera();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickfromGallery();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();
    }

    private void pickfromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickfromcamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagepermissions, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, camerapermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickfromcamera();

                    } else {
                        Toast.makeText(this, "Camera and Storage Permission Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickfromGallery();

                    } else {
                        Toast.makeText(this, "Storage Permission Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            image_uri = data.getData();
            productimage.setImageURI(image_uri);
        } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
            productimage.setImageURI(image_uri);
        }


    }
}