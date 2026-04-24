package com.example.outera;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RiwayatActivity extends AppCompatActivity {

    private LinearLayout llRiwayatContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        llRiwayatContainer = findViewById(R.id.llRiwayatContainer);
        NavigationUtils.setupBottomNav(this, R.id.nav_riwayat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilkanRiwayat();
    }

    private void tampilkanRiwayat() {
        llRiwayatContainer.removeAllViews();

        ArrayList<String[]> transaksiList = new ArrayList<>();

        try {
            FileInputStream fis = openFileInput("transaksi.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    transaksiList.add(parts);
                }
            }
            reader.close();
            fis.close();
        } catch (Exception e) {
            // File might be empty or missing
        }

        if (transaksiList.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Belum ada riwayat penyewaan aktif.");
            emptyText.setTextColor(getResources().getColor(R.color.outline));
            emptyText.setGravity(Gravity.CENTER);
            llRiwayatContainer.addView(emptyText);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = transaksiList.size() - 1; i >= 0; i--) {
            String[] parts = transaksiList.get(i);
            try {
                String idTrans = parts[0];
                String namaPenyewa = parts[1];
                String namaAlat = parts[2];
                String jumlah = parts[3];
                String tglSelesai = parts[6];

                View card = inflater.inflate(R.layout.item_riwayat, llRiwayatContainer, false);

                TextView tvInisial = card.findViewById(R.id.tvInisial);
                TextView tvNama = card.findViewById(R.id.tvNamaPenyewa);
                TextView tvId = card.findViewById(R.id.tvIdTransaksi);
                TextView tvTenggat = card.findViewById(R.id.tvTenggatPill);
                ImageView ivAlat = card.findViewById(R.id.ivAlat);
                TextView tvNamaAlat = card.findViewById(R.id.tvNamaAlat);
                TextView tvDetailAlat = card.findViewById(R.id.tvDetailAlat);
                Button btnSelesaikan = card.findViewById(R.id.btnSelesaikan);

                tvInisial.setText(getInitials(namaPenyewa));
                tvNama.setText(namaPenyewa);
                tvId.setText("ID: #OUT-" + idTrans.substring(idTrans.length() - 4));
                tvTenggat.setText(tglSelesai);

                ivAlat.setImageResource(getGambarAlat(namaAlat));
                tvNamaAlat.setText(namaAlat);
                tvDetailAlat.setText(jumlah + "x Item disewa");

                btnSelesaikan.setOnClickListener(v -> hapusTransaksi(idTrans));

                llRiwayatContainer.addView(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hapusTransaksi(String targetId) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("transaksi.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    if (!parts[0].equals(targetId)) {
                        lines.add(line);
                    }
                }
            }
            reader.close();
            fis.close();

            FileOutputStream fos = openFileOutput("transaksi.txt", MODE_PRIVATE);
            for (String l : lines) {
                fos.write((l + "\n").getBytes());
            }
            fos.close();

            Toast.makeText(this, "Sewa diselesaikan!", Toast.LENGTH_SHORT).show();
            tampilkanRiwayat(); 
        } catch (Exception e) {
            Toast.makeText(this, "Gagal menyelesaikan sewa", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] words = name.trim().split("\\s+");
        if (words.length == 1) return String.valueOf(words[0].charAt(0)).toUpperCase();
        return (String.valueOf(words[0].charAt(0)) + String.valueOf(words[words.length - 1].charAt(0))).toUpperCase();
    }

    private int getGambarAlat(String namaAlat) {
        switch (namaAlat) {
            case "Tenda": return R.drawable.img_tenda;
            case "Carrier": return R.drawable.img_carrier;
            case "Matras": return R.drawable.img_matras;
            case "Kompor": return R.drawable.img_kompor;
            case "Sleeping Bag": return R.drawable.img_sleeping_bag;
            case "Lampu Tenda": return R.drawable.img_lampu_tenda;
            case "Kursi Lipat": return R.drawable.img_kursi_lipat;
            case "Meja Lipat": return R.drawable.img_meja_lipat;
            case "Alat Masak": return R.drawable.img_alat_masak;
            case "Trekking Pole": return R.drawable.img_trekking_pole;
            default: return R.drawable.img_tenda;
        }
    }
}
