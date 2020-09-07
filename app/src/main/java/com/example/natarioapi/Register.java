package com.example.natarioapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText etemail, etusername, etpassword, etconfirmpassword;
    private Button btnregister, btnlogin;
    private ProgressBar regprogressbar;
    private FirebaseAuth fAuth;
    private CheckBox regcheckbox;
    private DatabaseReference databaseReference;
    Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        member = new Member();

        etemail = findViewById(R.id.register_email);
        etusername = findViewById(R.id.register_username);
        etpassword = findViewById(R.id.register_password);
        etconfirmpassword = findViewById(R.id.register_confirm_password);
        btnregister = findViewById(R.id.register_button);
        btnlogin = findViewById(R.id.to_login_button);
        regprogressbar = findViewById(R.id.register_progressbar);
        regcheckbox = findViewById(R.id.register_checkbox);
        fAuth =  FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        regcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etconfirmpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etconfirmpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etemail.getText().toString();
                final String username = etusername.getText().toString();
                String password = etpassword.getText().toString();
                String confirmpassword = etconfirmpassword.getText().toString();
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(username) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmpassword)){
                    if (password.equals(confirmpassword)) {
                        regprogressbar.setVisibility(View.VISIBLE);
                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "LOGGING IN", Toast.LENGTH_SHORT).show();

                                    member.setUname(username);
                                    FirebaseUser user = fAuth.getCurrentUser();
                                    databaseReference.child(user.getUid()).setValue(member);
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }else {
                                    String Error = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), Error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(),"Password do not match", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "PLEASE FILL IN ALL FIELDS", Toast.LENGTH_SHORT).show();
                }


            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}