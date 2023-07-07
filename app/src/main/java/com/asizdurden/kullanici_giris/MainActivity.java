package com.asizdurden.kullanici_giris;

import static com.asizdurden.kullanici_giris.Utils.NetworkUtils.isNetworkAvailable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.asizdurden.kullanici_giris.databinding.ActivityGirisYapBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {
    private ActivityGirisYapBinding binding;
    FirebaseAuth authentication;
    private GoogleSignInClient googleSignInClient;
    private final static int RC_SIGN_IN = 1355;
    Handler handler = new Handler();
    Runnable internetiBaglantisiniKontrolEt_Runnable;
    int delay;
    private boolean isCheckingNetwork = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String kayitli_Email;
    String kayitli_Sifre;
    boolean tiklansinMi;

    @Override
    protected void onPause() {
        super.onPause();
        isCheckingNetwork = false;
        handler.removeCallbacks(internetiBaglantisiniKontrolEt_Runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCheckingNetwork = true;
        handler.postDelayed(internetiBaglantisiniKontrolEt_Runnable, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Viewbinding
        binding = ActivityGirisYapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        // Firebase Authentication
        authentication = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Google ile giriş
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Beni hatırla seçeneği işaretli ise
        sharedPreferences = getSharedPreferences("oturum", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        kayitli_Email = sharedPreferences.getString("email", "");
        kayitli_Sifre = sharedPreferences.getString("sifre", "");

        if (!kayitli_Email.equals("") && !kayitli_Sifre.equals("")) {
            SignIn(kayitli_Email, kayitli_Sifre);
            tiklansinMi = false;
        }

        // İnternet Bağlantısı Kontrolü (Tek Seferlik)
        if (isNetworkAvailable(this)) {
            // İnternet bağlantısı var, işlemleri gerçekleştir
        } else {
            // İnternet bağlantısı yok, kullanıcıyı uygun bir şekilde bilgilendir
            Toast.makeText(this, "İnternet Bağlantısı Gereklidir !", Toast.LENGTH_LONG).show();
        }

        // Belli Aralıklar ile Aktif Kontrol yapmak için
        delay = 5000; // 5 saniyelik gecikme süresi (milisaniye cinsinden)
        internetiBaglantisiniKontrolEt_Runnable = new Runnable() {
            @Override
            public void run() {
                // Gecikmeli olarak başlatılan işlemler
                if (!isNetworkAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "İnternet bağlantısı yok", Toast.LENGTH_SHORT).show();
                }
                if (isCheckingNetwork) {
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.postDelayed(internetiBaglantisiniKontrolEt_Runnable, delay);

        // Click Olayları
        binding.kayitOl.setOnClickListener(v -> {
            binding.kayitOl.setClickable(false);
            Intent intent = new Intent(this, KayitOlActivity.class);
            startActivity(intent);

            // Timer işe yaramadı - CountDownTimer işe yaradı
            new Handler().postDelayed(() -> {
                binding.kayitOl.setClickable(true);
            }, 3000); // 3000 milisaniye (3 saniye) gecikme
        });

        binding.googleGirisButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN, null); // 3 parametre olarak yapmak, gelecek android sürümlerinde desteklenmeme ihtimali göz önüne alınarak yapıldı.
        });

        binding.girisYap.setOnClickListener(v -> {
            girisKontrol();
        });

        binding.facebookGirisButton.setOnClickListener(v -> {
            Toast.makeText(this, "Bu Özellik Yakında Eklenecek :))", Toast.LENGTH_SHORT).show();
        });


    }

    private void girisKontrol() {
        String email = binding.email.getText().toString();
        String sifre = binding.sifre.getText().toString();

        if (email.equals("")) {
            binding.email.setError("Email Boş");
            binding.email.requestFocus();
        } else if (sifre.equals("")) {
            binding.sifre.setError("Şifre Boş");
            binding.sifre.requestFocus();
        } else {
            if (binding.beniHatirla.isChecked()) {
                editor.putString("email", email);
                editor.putString("sifre", sifre);
                editor.apply();

                SignIn(email, sifre);
            } else {
                SignIn(email, sifre);
            }

        }
    }


    public void SignIn(String email, String password) {
        authentication
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Hoş Geldiniz" + authentication.getUid(), Toast.LENGTH_SHORT).show();
                    // Butona birden fazla basıldığında çok kez activity açılıyor engellemek adına yapıldı.
                    binding.girisYap.setClickable(true);
                    Intent intent = new Intent(this, SecimEkraniActivity.class);
                    if (!kayitli_Email.equals("")) {
                        intent.putExtra("beni hatırla", "beni unut");
                    }
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Böyle bir kullanıcı kayıtlı değil.", Toast.LENGTH_SHORT).show();
                    binding.girisYap.setClickable(true);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleGiris(account);
            } catch (ApiException e) {
                // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void googleGiris(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        authentication
                .signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(MainActivity.this, SecimEkraniActivity.class);
                        // İşlem bittiğinde button tekrar tıklanabilir olacaktır.
                        binding.googleGirisButton.setClickable(true);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Sorry authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}