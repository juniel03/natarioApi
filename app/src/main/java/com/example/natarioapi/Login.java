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

public class Login extends AppCompatActivity {

    private EditText etemail, etpassword;
    private Button btnlogin, btntoreg;
    private ProgressBar loginprogress;
    private FirebaseAuth fAuth;
    private CheckBox logincheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etemail = findViewById(R.id.login_email);
        etpassword = findViewById(R.id.login_password);
        btnlogin = findViewById(R.id.login_button);
        btntoreg = findViewById(R.id.to_register_button);
        loginprogress = findViewById(R.id.login_progressbar);
        logincheckbox = findViewById(R.id.login_checkbox);
        fAuth =  FirebaseAuth.getInstance();
        logincheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etemail.getText().toString();
                String password = etpassword.getText().toString();
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    loginprogress.setVisibility(View.VISIBLE);
                    fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }else {
                                String Error = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), Error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "PLEASE FILL IN ALL FIELDS", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btntoreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}