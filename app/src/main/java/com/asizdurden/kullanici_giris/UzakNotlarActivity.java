package com.asizdurden.kullanici_giris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.asizdurden.kullanici_giris.databinding.ActivityNotlarBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UzakNotlarActivity extends AppCompatActivity {
    private ActivityNotlarBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore dataBase;
    String userId;
    String Baslik_data;
    String Icerik_data;
    private final String FIELD_1 = "Baslik";
    private final String FIELD_2 = "İcerik";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notlar);

        // FirebaseFirestore
        dataBase = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        // Kullanıcı bilgisi
        userId = firebaseUser.getUid();
        // Daha önceden kayıtlı dataları var ise getirsin
        dataBase
                .collection("kullanicilar")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // İlgili işlemler
                            Baslik_data = document.getString(FIELD_1);
                            Icerik_data = document.getString(FIELD_2);
                            binding.baslik.setText(Baslik_data);
                            binding.icerik.setText(Icerik_data);
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
                });


        // sharedPreferences dosyasının okunması
        sharedPreferences = getSharedPreferences("oturum", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Viewbinging
        binding = ActivityNotlarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String kullaniciKayitliMi = intent.getStringExtra("beni hatırla");

        if (kullaniciKayitliMi == null) {
            binding.beniUnut.setVisibility(View.GONE);
        }

        binding.beniUnut.setOnClickListener(v -> {
            editor.remove("email");
            editor.remove("sifre");
            editor.apply();
            Toast.makeText(this, "Hesap unutuldu.", Toast.LENGTH_SHORT).show();
        });
    }

    String baslik;
    String icerik;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        baslik = binding.baslik.getText().toString();
        icerik = binding.icerik.getText().toString();

        bilgileriGuncelle(userId);
    }

    private void bilgileriGuncelle(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_1, baslik);
        data.put(FIELD_2, icerik);

        dataBase
                .collection("kullanicilar")
                .document(userId)
                .set(data)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Bilgiler kayıt edildi", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Bilgiler kayıt edilemedi !", Toast.LENGTH_LONG).show());
    }
}