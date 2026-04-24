package com.example.outera;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.content.Intent;
import android.widget.LinearLayout;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvStokAlat;
    private TextView tvPenyewaanAktif;
    private TextView tvPenyewaanInfo;

    private static final String[] NAMA_ALAT = {
        "Tenda", "Carrier", "Matras", "Kompor", "Sleeping Bag", 
        "Lampu Tenda", "Kursi Lipat", "Meja Lipat", "Alat Masak", "Trekking Pole"
    };
    private static final int[] STOK_AWAL = {
        10, 8, 15, 12, 15, 
        20, 10, 5, 8, 12
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvStokAlat = findViewById(R.id.tvStokAlat);
        tvPenyewaanAktif = findViewById(R.id.tvPenyewaanAktif);
        tvPenyewaanInfo = findViewById(R.id.tvPenyewaanInfo);

        LinearLayout llCardPenyewaan = findViewById(R.id.llCardPenyewaan);
        LinearLayout llCardStok = findViewById(R.id.llCardStok);

        llCardPenyewaan.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, RiwayatActivity.class);
            startActivity(intent);
        });

        llCardStok.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, KatalogActivity.class);
            startActivity(intent);
        });

        NavigationUtils.setupBottomNav(this, R.id.nav_beranda);
    }

    @Override
    protected void onResume() {
        super.onResume();
        muatRingkasan();
    }

    private void muatRingkasan() {
        HashMap<String, Integer> sewaPerAlat = new HashMap<>();
        int totalTransaksi = 0;
        long tanggalSelesaiTerdekat = Long.MAX_VALUE;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("id", "ID"));

        try {
            FileInputStream fis = openFileInput("transaksi.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    try {
                        String namaAlat = parts[2];
                        int jumlah = Integer.parseInt(parts[3]);
                        String tglSelesaiStr = parts[6];
                        
                        sewaPerAlat.put(namaAlat, sewaPerAlat.getOrDefault(namaAlat, 0) + jumlah);
                        totalTransaksi++;

                        try {
                            Date dateSelesai = sdf.parse(tglSelesaiStr);
                            if (dateSelesai != null) {
                                long time = dateSelesai.getTime();
                                if (time < tanggalSelesaiTerdekat) {
                                    tanggalSelesaiTerdekat = time;
                                }
                            }
                        } catch (Exception e) {}
                    } catch (NumberFormatException e) {}
                }
            }
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            // File not found, ignore
        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalStok = 0;
        for (int i = 0; i < NAMA_ALAT.length; i++) {
            String nama = NAMA_ALAT[i];
            int stokAwal = STOK_AWAL[i];
            int terpakai = sewaPerAlat.getOrDefault(nama, 0);
            totalStok += Math.max(0, stokAwal - terpakai);
        }

        tvStokAlat.setText(String.valueOf(totalStok));
        tvPenyewaanAktif.setText(String.valueOf(totalTransaksi));

        if (totalTransaksi > 0 && tanggalSelesaiTerdekat != Long.MAX_VALUE) {
            long diffMillis = tanggalSelesaiTerdekat - System.currentTimeMillis();
            long diffDays = diffMillis / (1000 * 60 * 60 * 24);
            if (diffDays <= 0) {
                tvPenyewaanInfo.setText("Ada alat yang harus kembali hari ini");
            } else {
                tvPenyewaanInfo.setText("Kembali dalam " + diffDays + " hari");
            }
        } else {
            tvPenyewaanInfo.setText("Belum ada alat yang disewa saat ini");
        }
    }
}
