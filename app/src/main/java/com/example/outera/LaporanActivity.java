package com.example.outera;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LaporanActivity extends AppCompatActivity {

    private TextView tvTotalPendapatan;
    private TextView tvTotalDisewa;
    private LinearLayout llGraphContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan);
        tvTotalDisewa = findViewById(R.id.tvTotalDisewa);
        llGraphContainer = findViewById(R.id.llGraphContainer);
        
        NavigationUtils.setupBottomNav(this, R.id.nav_laporan);

        loadDataAndShowGraph();
    }

    private void loadDataAndShowGraph() {
        HashMap<String, Integer> itemCounts = new HashMap<>();
        long totalPendapatan = 0;

        try {
            FileInputStream fis = openFileInput("transaksi_history.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    try {
                        String namaAlat = parts[2];
                        int jumlah = Integer.parseInt(parts[3]);
                        long totalBiaya = Long.parseLong(parts[7]);

                        totalPendapatan += totalBiaya;
                        itemCounts.put(namaAlat, itemCounts.getOrDefault(namaAlat, 0) + jumlah);
                    } catch (NumberFormatException e) {}
                }
            }
            reader.close();
            fis.close();
        } catch (Exception e) {}

        int totalBarangAktifDisewa = 0;
        try {
            FileInputStream fis = openFileInput("transaksi.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    try {
                        int jumlah = Integer.parseInt(parts[3]);
                        totalBarangAktifDisewa += jumlah;
                    } catch (NumberFormatException e) {}
                }
            }
            reader.close();
            fis.close();
        } catch (Exception e) {}

        tvTotalDisewa.setText(totalBarangAktifDisewa + " Item");

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvTotalPendapatan.setText(formatRupiah.format(totalPendapatan));

        int scalePx = dpToPx(30);
        int barHeightPx = dpToPx(32);
        int marginPx = dpToPx(8);
        int cornerPx = dpToPx(6);

        llGraphContainer.removeAllViews();

        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            String namaAlat = entry.getKey();
            int jumlahSewa = entry.getValue();

            TextView tvAlat = new TextView(this);
            tvAlat.setText(namaAlat + " (" + jumlahSewa + ")");
            tvAlat.setTextColor(Color.parseColor("#1A1C1C"));
            tvAlat.setTextSize(12f);
            tvAlat.setTypeface(null, Typeface.BOLD);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.setMargins(0, marginPx, 0, dpToPx(4));
            tvAlat.setLayoutParams(textParams);

            View barView = new View(this);
            GradientDrawable barBg = new GradientDrawable();
            barBg.setShape(GradientDrawable.RECTANGLE);
            barBg.setColor(Color.parseColor("#4B5320")); // Army Green
            barBg.setCornerRadius(cornerPx);
            barView.setBackground(barBg);

            int barWidth = jumlahSewa * scalePx;
            if (barWidth < dpToPx(20)) barWidth = dpToPx(20);

            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(barWidth, barHeightPx);
            barParams.setMargins(0, 0, 0, marginPx);
            barView.setLayoutParams(barParams);

            llGraphContainer.addView(tvAlat);
            llGraphContainer.addView(barView);
        }

        if (itemCounts.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Belum ada data penyewaan.");
            emptyText.setTextColor(Color.parseColor("#77786B"));
            emptyText.setGravity(Gravity.CENTER);
            llGraphContainer.addView(emptyText);
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
