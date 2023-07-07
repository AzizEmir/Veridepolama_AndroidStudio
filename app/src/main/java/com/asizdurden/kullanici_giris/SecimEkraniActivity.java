package com.asizdurden.kullanici_giris;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asizdurden.kullanici_giris.YapilacakListesi.SplashActivity;
import com.asizdurden.kullanici_giris.databinding.ActivitySecimEkraniBinding;

import java.util.Random;


public class SecimEkraniActivity extends AppCompatActivity {
    private ActivitySecimEkraniBinding binding;
    private String tercihYaptiMi = "yapmadı";
    Intent intent;
    String kullaniciKayitliMi;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secim_ekrani);
        // Kullanıcı hesabın hatırlanmasını istedi mi ( Giriş Yaparken )
        intent = getIntent();
        kullaniciKayitliMi = intent.getStringExtra("beni hatırla");

        // Viewbinding
        binding = ActivitySecimEkraniBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // sharedPreferences dosyasının okunması
        sharedPreferences = getSharedPreferences("oturum", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        binding.uzakVeritabani.setOnClickListener(v -> {
            tercihYaptiMi = "yaptı";
            ActivityeGonder(UzakNotlarActivity.class);
        });

        binding.yerelVeritabani.setOnClickListener(v -> {
            tercihYaptiMi = "yaptı";
            ActivityeGonder(YerelNotlarActivity.class);
        });

        binding.yapilacakListesi.setOnClickListener(v -> {
            tercihYaptiMi = "yaptı";
            startActivity(new Intent(this, SplashActivity.class));
        });
    }

    void ActivityeGonder(Class gidecekSinif) {
        intent = new Intent(SecimEkraniActivity.this, gidecekSinif);
        intent.putExtra("beni hatırla", kullaniciKayitliMi);
        startActivity(intent);
    }

    Handler handler = new Handler();
    Runnable runnable;
    Runnable runnable2;
    boolean sureyiUzat = false;
    int sure = 1000; //ms

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        runnable2 = () -> {
            if (sharedPreferences.contains("email")) {
                editor.remove("email");
                editor.remove("sifre");
                editor.apply();

                Toast.makeText(SecimEkraniActivity.this, "Hesap unutuldu.", Toast.LENGTH_SHORT).show();
                sureyiUzat = true;
            }
        };
        handler.post(runnable2);

        runnable = () -> {
            if (tercihYaptiMi.equals("yapmadı")) {
                // 1 yada 2 olmak üzere random sayı tutulması
                Random random = new Random();
                int randomNumber = random.nextInt(2) + 1;
                // trinity_dodge_this.jpeg yada ajan_smith_secim.jpg yolunun tutulması için değişken tanımlanması
                int yol = 0;
                // toast.xml çağırılması
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                // Cihaz ekran boyutunu alma
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                // bulunan ekran boyutunun layout'a atanması
                layout.setMinimumHeight(displayMetrics.heightPixels);
                layout.setMinimumWidth(displayMetrics.widthPixels);

                ImageView image = layout.findViewById(R.id.image);

                // Random sayıya göre hangi resim geliceğine karar veren kod bloğu
                if (randomNumber == 1)
                    yol = R.drawable.trinity_dodge_this;
                else
                    yol = R.drawable.ajansmith;

                image.setImageResource(yol);

                TextView text = layout.findViewById(R.id.text);
                text.setTextColor(Color.RED);
                text.setTextSize(20);
                text.setText("Seçim yapmaktan korkma !");

                Toast toast = new Toast(this);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        };
        // Hesap unutuldu ise "Hesap unutuldu" mesajı gosterildikten 1.5 saniye sonra runnable postlansın
        if (sureyiUzat)
            sure = 1500;

        handler.postDelayed(runnable, sure);
    }
}