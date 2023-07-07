package com.asizdurden.kullanici_giris;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.asizdurden.kullanici_giris.databinding.ActivityKayitOlBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class KayitOlActivity extends AppCompatActivity {
    private ActivityKayitOlBinding binding;
    FirebaseAuth authentication;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Viewbinding

        binding = ActivityKayitOlBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Firebase Authentication

        authentication = FirebaseAuth.getInstance();

        binding.kayitOl.setOnClickListener(v -> {
            binding.kayitOl.setClickable(false);
            String email = binding.email.getText().toString();
            String sifre = binding.sifre.getText().toString();
            if (email.equals("")) {
                binding.email.setError("Email Boş");
                binding.email.requestFocus();
            } else if (sifre.equals("")) {
                binding.sifre.setError("Şifre Boş");
                binding.sifre.requestFocus();
            } else {
                authentication
                        .createUserWithEmailAndPassword(email, sifre)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(KayitOlActivity.this, "Hoş geldin " + binding.isim.getText().toString(), Toast.LENGTH_LONG).show();


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },2000);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(KayitOlActivity.this, "Başarısız. Kayıt oluşturulamadı.   " + e.getMessage(), Toast.LENGTH_LONG).show();
                                binding.kayitOl.setClickable(true);
                            }
                        });
            }
        });
        binding.sifreYenile.setOnClickListener(v -> {
            String girilenEmail = binding.email.getText().toString().trim();
            if(girilenEmail.equals("")){
                binding.email.requestFocus();
                binding.email.setError("Email girin");
            }else {
                authentication
                        .sendPasswordResetEmail(girilenEmail)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(this, "Email'inizi kontrol ediniz.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Bu Email kayıtlı değildir.", Toast.LENGTH_SHORT).show();
                        });
            }
        }); 
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
