package com.example.outera;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LaporanActivity extends AppCompatActivity {

    private TextView tvTotalPendapatan;
    private TextView tvTotalDisewa;
    private LinearLayout llGraphContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        // Inisialisasi views
        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan);
        tvTotalDisewa = findViewById(R.id.tvTotalDisewa);
        llGraphContainer = findViewById(R.id.llGraphContainer);

        muatDataDanTampilkanGrafik();
    }

    private void muatDataDanTampilkanGrafik() {
        HashMap<String, Integer> itemCounts = new HashMap<>();
        int totalBarangDisewa = 0;

        try {
            FileInputStream fis = openFileInput("transaksi.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while ((line = reader.readLine()) != null) {
                // Format: ID|Nama|Alat|Durasi(Jumlah)|Total
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String namaAlat = parts[2];
                    int jumlah = 0;
                    try {
                        jumlah = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        // Ignore parse errors for quantity
                    }
                    
                    itemCounts.put(namaAlat, itemCounts.getOrDefault(namaAlat, 0) + jumlah);
                    totalBarangDisewa += jumlah;
                }
            }
            reader.close();
            fis.close();
            
        } catch (FileNotFoundException e) {
            // File belum ada, biarkan kosong atau tampilkan info
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update Total Barang Disewa TextView
        tvTotalDisewa.setText(totalBarangDisewa + " Item");

        // Konversi 50dp ke pixels
        int scalePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        
        int heightPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        int marginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        // Membuat grafik batang
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            String namaAlat = entry.getKey();
            int jumlah = entry.getValue();

            // 1. TextView untuk nama alat
            TextView tvAlat = new TextView(this);
            tvAlat.setText(namaAlat + " (" + jumlah + ")");
            tvAlat.setTextColor(Color.parseColor("#1A1C1C"));
            tvAlat.setTextSize(14f);
            
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.setMargins(0, marginPx, 0, 4);
            tvAlat.setLayoutParams(textParams);
            
            // 2. View untuk batang grafik
            View barView = new View(this);
            barView.setBackgroundColor(Color.parseColor("#4B5320")); // Warna Hijau Army
            
            int barWidth = jumlah * scalePx;
            // Minimal width agar batang tetap terlihat jika nilainya 0 atau error
            if (barWidth == 0) barWidth = 10; 
            
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(barWidth, heightPx);
            barParams.setMargins(0, 0, 0, marginPx);
            barView.setLayoutParams(barParams);

            // Tambahkan ke container
            llGraphContainer.addView(tvAlat);
            llGraphContainer.addView(barView);
        }
        
        if (itemCounts.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Belum ada data penyewaan.");
            llGraphContainer.addView(emptyText);
        }
    }
}
