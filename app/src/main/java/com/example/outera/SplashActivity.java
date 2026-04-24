package com.example.outera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initDummyData();

        ImageView ivLogo = findViewById(R.id.ivSplashLogo);
        TextView tvAppName = findViewById(R.id.tvSplashAppName);
        TextView tvTagline = findViewById(R.id.tvSplashTagline);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        fadeIn.setFillAfter(true);

        ivLogo.startAnimation(fadeIn);
        tvAppName.startAnimation(fadeIn);

        AlphaAnimation fadeInDelay = new AlphaAnimation(0f, 1f);
        fadeInDelay.setDuration(600);
        fadeInDelay.setStartOffset(400);
        fadeInDelay.setFillAfter(true);
        tvTagline.startAnimation(fadeInDelay);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private void initDummyData() {
        File file = new File(getFilesDir(), "transaksi.txt");
        File fileHistory = new File(getFilesDir(), "transaksi_history.txt");
        
        if (!file.exists()) {
            try {
                // ID|Nama|Alat|Jumlah|Durasi|TglSewa|TglSelesai|TotalBiaya
                String data = "1713000000000|Andi Susanto|Tenda|2|2|20/04/2026|22/04/2026|200000\n" +
                              "1713100000000|Budi Raharjo|Carrier|1|3|21/04/2026|24/04/2026|90000\n" +
                              "1713200000000|Citra Dewi|Kompor|2|1|24/04/2026|25/04/2026|30000\n" +
                              "1713300000000|Doni Pratama|Sleeping Bag|3|2|23/04/2026|25/04/2026|120000\n" +
                              "1713400000000|Eka Putri|Lampu Tenda|2|2|24/04/2026|26/04/2026|40000\n";
                
                FileOutputStream fos = openFileOutput("transaksi.txt", MODE_PRIVATE);
                fos.write(data.getBytes());
                fos.close();
                
                FileOutputStream fosHistory = openFileOutput("transaksi_history.txt", MODE_PRIVATE);
                fosHistory.write(data.getBytes());
                fosHistory.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!fileHistory.exists()) {
            try {
                java.io.FileInputStream fis = openFileInput("transaksi.txt");
                FileOutputStream fos = openFileOutput("transaksi_history.txt", MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fis.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
