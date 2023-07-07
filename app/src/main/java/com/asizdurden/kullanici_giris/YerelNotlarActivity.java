package com.asizdurden.kullanici_giris;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.asizdurden.kullanici_giris.Model.Not;
import com.asizdurden.kullanici_giris.databinding.ActivityNotlarBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class YerelNotlarActivity extends AppCompatActivity {
    private ActivityNotlarBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String kullaniciEmail;
    ArrayList<Not> Notlar = new ArrayList<>();
    // Table name
    public String TABLE_NAME = "";
    // Table columns
    public String TABLE_COLUMN_baslik = "baslik";
    public String TABLE_COLUMN_icerik = "icerik";
    SQLiteDatabase sqLiteDatabase;
    String kullaniciKayitliMi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notlar);

        // FirebaseFirestore
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        // Kullanıcı bilgisi
        kullaniciEmail = firebaseUser.getEmail().replace("@", "_");
        // Kullanıcı email'i tablo adı olacaktır.
        TABLE_NAME = kullaniciEmail;

        // sharedPreferences dosyasının okunması
        sharedPreferences = getSharedPreferences("oturum", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Viewbinging
        binding = ActivityNotlarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        kullaniciKayitliMi = intent.getStringExtra("beni hatırla");

        if (kullaniciKayitliMi == null) {
            binding.beniUnut.setVisibility(View.GONE);
        }

        sqLiteDatabase = this.openOrCreateDatabase(kullaniciEmail + ".db", MODE_PRIVATE, null);

        try {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS kullaniciNotlari (id INTEGER PRIMARY KEY, baslik VARCHAR, icerik VARCHAR)");


            Cursor cursor = sqLiteDatabase.rawQuery("Select * from  kullaniciNotlari", null);
            int baslikIndex = cursor.getColumnIndex(TABLE_COLUMN_baslik);
            int icerikIndex = cursor.getColumnIndex(TABLE_COLUMN_icerik);

            while (cursor.moveToNext()) {
                Notlar.add(new Not(cursor.getString(baslikIndex), cursor.getString(icerikIndex)));
            }
            cursor.close();


            binding.icerik.setText(Notlar.get(0).getIcerik());
            binding.baslik.setText(Notlar.get(0).getBaslik());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int rowCount = 0;


        String countQuery = "SELECT COUNT(*) FROM kullaniciNotlari";
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            rowCount = cursor.getInt(0);
            cursor.close();
            // rowCount değişkeni, tablodaki toplam satır sayısını içerir
            // Bu değeri kullanarak istediğiniz işlemi gerçekleştirebilirsiniz
        }

        if (rowCount > 0)
            sqLiteDatabase.execSQL("UPDATE kullaniciNotlari SET " + TABLE_COLUMN_baslik + " = '" + binding.baslik.getText().toString() + "', " + TABLE_COLUMN_icerik + " = '" + binding.icerik.getText().toString() + "' WHERE id = 1");
        else
            sqLiteDatabase.execSQL("INSERT INTO kullaniciNotlari (" + TABLE_COLUMN_baslik + ", " + TABLE_COLUMN_icerik + ") VALUES ('" + binding.baslik.getText().toString() + "', '" + binding.icerik.getText().toString() + "')");

        Toast.makeText(this, "Yerel Veriler Kayıt Edildi", Toast.LENGTH_SHORT).show();
    }
}
