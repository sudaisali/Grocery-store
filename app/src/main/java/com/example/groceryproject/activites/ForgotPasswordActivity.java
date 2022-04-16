package com.example.groceryproject.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.groceryproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
EditText email;
ImageView back;
Button recover;
private FirebaseAuth firebaseAuth;
private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.email);
        back = findViewById(R.id.back);
        recover = findViewById(R.id.Forgot);
         firebaseAuth = FirebaseAuth.getInstance();
         progressDialog = new ProgressDialog(this);
         progressDialog.setTitle("Please wait");
         progressDialog.setCanceledOnTouchOutside(false);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });
    }
    private String emai;
      private void recoverPassword(){
emai = email.getText().toString().trim();
if(!Patterns.EMAIL_ADDRESS.matcher(emai).matches()){
    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
    return;
}
progressDialog.setMessage("Sending Instruction to reset password");
progressDialog.show();
firebaseAuth.sendPasswordResetEmail(emai)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, "Password reset instruction sent to your mail", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
}
}