package com.example.groceryproject.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
private TextView forgotpass,register;
private Button login;
private EditText email,password;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgotpass = findViewById(R.id.forgot);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


login.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        loginuser();
    }
});
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));

            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , ForgotPasswordActivity.class));

            }
        });
    }
    private String emai,pass;
    private  void loginuser(){
        emai=email.getText().toString().trim();
        pass = password.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(emai).matches()){
            Toast.makeText(this,"Invalid email pattern..",Toast.LENGTH_SHORT).show();
        return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Enter pass..",Toast.LENGTH_SHORT).show();
            return;

        }
        progressDialog.setMessage("Logging In....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emai , pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        makemeonline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void makemeonline(){
        progressDialog.setMessage("Checking user.....");
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("online" , "true");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void checkUserType(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                           String accountType = ""+ds.child("accountType").getValue();
                           if(accountType.equals("seller")){
                               progressDialog.dismiss();
                               startActivity(new Intent(LoginActivity.this, SellerActivity.class));
                           }
                           else{
                               progressDialog.dismiss();
                               startActivity(new Intent(LoginActivity.this, UserActivity.class));

                           }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}