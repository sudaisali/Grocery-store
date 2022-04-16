package com.example.groceryproject.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.groceryproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterSellerActivity extends AppCompatActivity implements LocationListener {
    ImageView back, gps, image;
    EditText name, shopname, shippingfee, phone, country, state, city, fulladdress, email;
    EditText password, conpassword;
    Button register;
    //Permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;


    //permission string array

    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //image pick url
    private Uri image_uri;


    private double latitude = 0.0, longitude = 0.0;
    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);
        back = findViewById(R.id.back);
        gps = findViewById(R.id.gps);
        shopname = findViewById(R.id.shopname);
        shippingfee = findViewById(R.id.shippingfee);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        fulladdress = findViewById(R.id.fulladress);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        conpassword = findViewById(R.id.conpassword);
        register = findViewById(R.id.register);

        // init permission array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gps code
                if (checkLocationPermission()) {
                    //alreadyallowed
                    detectLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register seller
                inputdata();
            }
        });
    }

    private String fullname, shop, phonenumber, deliverycharges, count, stat, cit, addrs, emai, pass, conpass;


    private void inputdata() {
        fullname = name.getText().toString().trim();
        shop = shopname.getText().toString().trim();
        phonenumber = phone.getText().toString().trim();
        deliverycharges = shippingfee.getText().toString().trim();
        count = country.getText().toString().trim();
        stat = state.getText().toString().trim();
        cit = city.getText().toString().trim();
        addrs = fulladdress.getText().toString().trim();
        emai = email.getText().toString().trim();
        pass = password.getText().toString().trim();
        conpass = conpassword.getText().toString().trim();
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(shop)) {
            Toast.makeText(this, "Enter shop name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(phonenumber)) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(deliverycharges)) {
            Toast.makeText(this, "Enter Delivery charges", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(this, "Please click gps button to get location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emai).matches()) {
            Toast.makeText(this, "Invalid email pattern", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Password must be atleast 6 character", Toast.LENGTH_SHORT).show();
            return;

        }
        if (!pass.equals(conpass)) {
            Toast.makeText(this, "Password doesnot match", Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();


    }

    private void createAccount() {
        progressDialog.setMessage("Account Creating...");
        progressDialog.show();

        //create account

        firebaseAuth.createUserWithEmailAndPassword(emai, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saveFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Failed creating Account
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("saving Account Info");
        String timestamp = "" + System.currentTimeMillis();
        if (image_uri == null) {
            //save info without image

            //setupdata to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + firebaseAuth.getUid());
            hashMap.put("email", "" + emai);
            hashMap.put("name", "" + fullname);
            hashMap.put("shopname", "" + shop);
            hashMap.put("phone", "" + phonenumber);
            hashMap.put("delivery fee", "" + deliverycharges);
            hashMap.put("country", "" + count);
            hashMap.put("state", "" + stat);
            hashMap.put("city", "" + cit);
            hashMap.put("address", "" + addrs);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accountType", "seller");
            hashMap.put("online", "true");
            hashMap.put("shopOpen", "true");
            hashMap.put("ProfileImage", "");

            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(RegisterSellerActivity.this, SellerActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this, SellerActivity.class));
                            finish();
                        }
                    });

        }
        else {
            //save info with image
            String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()) {


                                //setupdata to save
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("email", "" + emai);
                                hashMap.put("name", "" + fullname);
                                hashMap.put("shopname", "" + shop);
                                hashMap.put("phone", "" + phonenumber);
                                hashMap.put("deliveryfee", "" + deliverycharges);
                                hashMap.put("country", "" + count);
                                hashMap.put("state", "" + stat);
                                hashMap.put("city", "" + cit);
                                hashMap.put("address", "" + addrs);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("accountType", "seller");
                                hashMap.put("online", "true");
                                hashMap.put("shopOpen", "true");
                                hashMap.put("ProfileImage", "" + downloadImageUri);

                                //save to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                startActivity(new Intent(RegisterSellerActivity.this, SellerActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this, SellerActivity.class));
                                                finish();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //camera clicked
                            if (checkStoragePermission()) {
                                //camera permission allowes
                                pickFromCamera();
                            } else {
                                //not allow
                                requestCameraPermission();
                            }
                        } else {
                            //gallery clicked
                            if (checkStoragePermission()) {
                                //storage Permission Allowed
                                pickFromGallery();
                            } else {
                                //not allowed
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    public void detectLocation() {

        Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    private boolean checkLocationPermission(){
           boolean result = ContextCompat.checkSelfPermission(this
           ,Manifest.permission.ACCESS_FINE_LOCATION)==
                   (PackageManager.PERMISSION_GRANTED);
           return result;
        }
        private  void requestLocationPermission(){
            ActivityCompat.requestPermissions(this , locationPermissions,LOCATION_REQUEST_CODE);
        }
        //CAMERA
    private  boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
   }
    private  void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
   }
    private  boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private  void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detect
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();
    }
    private void findAddress(){
        //find address , country , state , city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this , Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            String gtaddress = addresses.get(0).getAddressLine(0);
            String gtcity = addresses.get(0).getLocality();
            String gtstate = addresses.get(0).getAdminArea();
            String gtcountry = addresses.get(0).getCountryName();

            //setAddress
            city.setText(gtcity);
            state.setText(gtstate);
            fulladdress.setText(gtaddress);
            country.setText(gtcountry);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
     //gps/Location disabled
        Toast.makeText(this, "Please turn on location", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permissionAllowwed
                        detectLocation();
                    }
                    else {
                        //PermissionDenied
                        Toast.makeText(this,"Location permission is necessary" , Toast.LENGTH_SHORT).show();
                    }
                }
            } break;
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        //permissionAllowwed
                       pickFromCamera();
                    }
                    else {
                        //PermissionDenied
                        Toast.makeText(this,"Camera permission is necessary" , Toast.LENGTH_SHORT).show();
                    }
                }
            } break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean storageAccepted = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        //permissionAllowwed
                        pickFromGallery();
                    }
                    else {
                        //PermissionDenied
                        Toast.makeText(this,"Storage permission is necessary" , Toast.LENGTH_SHORT).show();
                    }
                }
            } break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

           if(requestCode == IMAGE_PICK_GALLERY_CODE){
               //getpicked image
               image_uri = data.getData();
               //set to imageview
               image.setImageURI(image_uri);
           }
           else if(requestCode == IMAGE_PICK_CAMERA_CODE){
               //SET TO IMAGE VIEW
               image.setImageURI(image_uri);
           }

        super.onActivityResult(requestCode, resultCode, data);
    }
}