package com.example.groceryproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Locale;

public class Edit_seller_profile extends AppCompatActivity  implements LocationListener {
    ImageView back,image,gps;
    EditText name , phone ,country , state,city,shopname,shippingfee;
    SwitchCompat shopopenswitch;
    EditText address;
    Button update;
//permissions constants
private static final int LOCATION_REQUEST_CODE=100;
private static final int CAMERA_REQUEST_CODE=200;
private static final int STORAGE_REQUEST_CODE=300;
//image location pick constants
    private  static  final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;
    //permission arrays
    private String[] locationpermissions;
    private String[] camerapermissions;
    private String[] storagepermissions;

    //pick image uri

    private Uri image_uri;


    private double latitude = 0.0, longitude = 0.0;
    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seller_profile);
        back =findViewById(R.id.back);
        gps =findViewById(R.id.gps);
        shopname=findViewById(R.id.shopname);
        shippingfee=findViewById(R.id.deliveryfee);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        address = findViewById(R.id.address);
        shopopenswitch =findViewById(R.id.shopopenswitch);
        update = findViewById(R.id.update);
        //init permission arrays
        locationpermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        camerapermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        checkuser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

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
                if (!checkLocationPermission()) {
                    //alreadyallowed
                    detectLocation();
                } else {
                    requestLocationPermission();
                }

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImagePickDialog();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdata();
            }
        });

    }
    private String ename , eshopname , ephonenumber , echarges , ecountry , estate , ecity , eaddress;
    private boolean shopopen;
    private void inputdata(){
        ename = name.getText().toString().trim();
        eshopname = shopname.getText().toString().trim();
        ephonenumber = phone.getText().toString().trim();
        echarges = shippingfee.getText().toString().trim();
        ecountry = country.getText().toString().trim();

        estate = state.getText().toString().trim();
        ecity = city.getText().toString().trim();
        eaddress = address.getText().toString().trim();
        shopopen = shopopenswitch.isChecked();
        updateprofile();

    }

    private void updateprofile() {
        progressDialog.setMessage("Updating Profile......");
        progressDialog.show();
        if(image_uri == null){
            HashMap<String , Object> hashMap = new HashMap<>();
            hashMap.put("name",""+ename);
            hashMap.put("shopname",""+eshopname);
            hashMap.put("phone",""+ephonenumber);
            hashMap.put("delivery fee",""+echarges);
            hashMap.put("country",""+ecountry);
            hashMap.put("state",""+estate);
            hashMap.put("city",""+ecity);
            hashMap.put("address",""+eaddress);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("shopOpen",""+shopopen);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(Edit_seller_profile.this, "Profile Updated....", Toast.LENGTH_SHORT).show();
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   progressDialog.dismiss();
                    Toast.makeText(Edit_seller_profile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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


                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("name", "" + ename);
                                hashMap.put("shopname", "" + eshopname);
                                hashMap.put("phone", "" + ephonenumber);
                                hashMap.put("deliveryFee", "" + echarges);
                                hashMap.put("country", "" + ecountry);
                                hashMap.put("state", "" + estate);
                                hashMap.put("city", "" + ecity);
                                hashMap.put("address", "" + eaddress);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("shopOpen", "" + shopopen);
                                hashMap.put("ProfileImage", "" + downloadImageUri);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(Edit_seller_profile.this, "Profile Updated....", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(Edit_seller_profile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
        }
    }


    private void checkuser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
        else{
            loadmyinfo();
        }
    }

    private void loadmyinfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String accountType = ""+ds.child("accountType").getValue();
                    String nameg= ""+ds.child("name").getValue();
                    String phoneg= ""+ds.child("phone").getValue();
                    String deliveryfeeg = ""+ds.child("delivery fee").getValue();
                    String countryg = ""+ds.child("country").getValue();
                    String stateg= ""+ds.child("state").getValue();
                    String cityg= ""+ds.child("city").getValue();
                    String addressg= ""+ds.child("address").getValue();
                     longitude= Double.parseDouble(""+ds.child("longitude").getValue());
                    latitude= Double.parseDouble(""+ds.child("latitude").getValue());
                    String timestampg= ""+ds.child("timestamp").getValue();
                    String onlineg= ""+ds.child("online").getValue();
                    String shopimageg= ""+ds.child("ProfileImage").getValue();
                    String shopopeng= ""+ds.child("shopOpen").getValue();
                    String uidg= ""+ds.child("uid").getValue();
                    String shopnameg= ""+ds.child("shopname").getValue();


                    name.setText(nameg);
                    shopname.setText(shopnameg);
                    phone.setText(phoneg);
                    shippingfee.setText(deliveryfeeg);
                    country.setText(countryg);
                    state.setText(stateg);
                    city.setText(cityg);
                    address.setText(addressg);
                    if(shopopeng.equals("true")){
                        shopopenswitch.setChecked(true);
                    }
                    else{
                        shopopenswitch.setChecked(false);
                    }
                    try {
                        Picasso.get().load(shopimageg).placeholder(R.drawable.ic_store_gray).into(image);
                    }
                    catch (Exception e){
                        image.setImageResource(R.drawable.ic_store_gray);

                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        ActivityCompat.requestPermissions(this , locationpermissions,LOCATION_REQUEST_CODE);
    }
    //CAMERA
    private  boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private  void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagepermissions,STORAGE_REQUEST_CODE);
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
        ActivityCompat.requestPermissions(this,camerapermissions,CAMERA_REQUEST_CODE);
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
            address.setText(gtaddress);
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
        if(requestCode == RESULT_OK){
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}