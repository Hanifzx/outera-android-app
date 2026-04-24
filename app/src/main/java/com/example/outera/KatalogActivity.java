package com.example.outera;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class KatalogActivity extends AppCompatActivity {

    private LinearLayout llKatalogContainer;

    private static final String[] NAMA_ALAT = {
        "Tenda", "Carrier", "Matras", "Kompor", "Sleeping Bag", 
        "Lampu Tenda", "Kursi Lipat", "Meja Lipat", "Alat Masak", "Trekking Pole"
    };
    private static final int[] HARGA_ALAT = {
        50000, 30000, 10000, 15000, 20000, 
        10000, 15000, 25000, 20000, 15000
    };
    private static final int[] STOK_AWAL = {
        10, 8, 15, 12, 15, 
        20, 10, 5, 8, 12
    };
    private static final int[] GAMBAR_ALAT = {
        R.drawable.img_tenda, R.drawable.img_carrier, R.drawable.img_matras, R.drawable.img_kompor, R.drawable.img_sleeping_bag,
        R.drawable.img_lampu_tenda, R.drawable.img_kursi_lipat, R.drawable.img_meja_lipat, R.drawable.img_alat_masak, R.drawable.img_trekking_pole
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);

        llKatalogContainer = findViewById(R.id.llKatalogContainer);
        NavigationUtils.setupBottomNav(this, R.id.nav_katalog);

        EditText etSearchKatalog = findViewById(R.id.etSearchKatalog);
        etSearchKatalog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                tampilkanKatalog(s.toString().toLowerCase(Locale.getDefault()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilkanKatalog("");
    }

    private void tampilkanKatalog(String query) {
        llKatalogContainer.removeAllViews();

        HashMap<String, Integer> sewaPerAlat = bacaDataSewa();
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        for (int i = 0; i < NAMA_ALAT.length; i++) {
            String nama = NAMA_ALAT[i];
            
            if (!query.isEmpty() && !nama.toLowerCase(Locale.getDefault()).contains(query)) {
                continue;
            }

            int harga = HARGA_ALAT[i];
            int stokAwal = STOK_AWAL[i];
            int terpakai = sewaPerAlat.getOrDefault(nama, 0);
            int sisaStok = Math.max(0, stokAwal - terpakai);
            int gambar = GAMBAR_ALAT[i];

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setBackgroundResource(R.drawable.bg_bento_card);
            int pad = dpToPx(16);
            card.setPadding(pad, pad, pad, pad);
            card.setElevation(dpToPx(2));
            card.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, dpToPx(16));
            card.setLayoutParams(cardParams);

            androidx.cardview.widget.CardView imageCard = new androidx.cardview.widget.CardView(this);
            LinearLayout.LayoutParams imageCardParams = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(80));
            imageCardParams.setMargins(0, 0, dpToPx(16), 0);
            imageCard.setLayoutParams(imageCardParams);
            imageCard.setRadius(dpToPx(8));
            imageCard.setCardElevation(0);

            android.widget.ImageView ivAlat = new android.widget.ImageView(this);
            ivAlat.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
            ivAlat.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            ivAlat.setImageResource(gambar);
            imageCard.addView(ivAlat);
            card.addView(imageCard);

            LinearLayout rightSide = new LinearLayout(this);
            rightSide.setOrientation(LinearLayout.VERTICAL);
            rightSide.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            LinearLayout rowAtas = new LinearLayout(this);
            rowAtas.setOrientation(LinearLayout.HORIZONTAL);
            rowAtas.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvNama = new TextView(this);
            tvNama.setText(nama);
            tvNama.setTextSize(16f);
            tvNama.setTextColor(Color.parseColor("#1A1C1C"));
            tvNama.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams namaParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvNama.setLayoutParams(namaParams);

            TextView tvStatus = new TextView(this);
            if (sisaStok > 0) {
                tvStatus.setText("TERSEDIA");
                tvStatus.setTextColor(getResources().getColor(R.color.status_available));
            } else {
                tvStatus.setText("HABIS");
                tvStatus.setTextColor(getResources().getColor(R.color.status_empty));
            }
            tvStatus.setTextSize(11f);
            tvStatus.setTypeface(null, Typeface.BOLD);

            rowAtas.addView(tvNama);
            rowAtas.addView(tvStatus);
            rightSide.addView(rowAtas);

            TextView tvHarga = new TextView(this);
            tvHarga.setText(formatRupiah.format(harga) + " / hari");
            tvHarga.setTextSize(13f);
            tvHarga.setTextColor(getResources().getColor(R.color.primary_container));
            tvHarga.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams hargaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            hargaParams.setMargins(0, dpToPx(6), 0, 0);
            tvHarga.setLayoutParams(hargaParams);
            rightSide.addView(tvHarga);

            TextView tvStok = new TextView(this);
            tvStok.setText("Stok tersedia: " + sisaStok + " unit");
            tvStok.setTextSize(12f);
            tvStok.setTextColor(getResources().getColor(R.color.outline));
            LinearLayout.LayoutParams stokParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            stokParams.setMargins(0, dpToPx(4), 0, 0);
            tvStok.setLayoutParams(stokParams);
            rightSide.addView(tvStok);

            card.addView(rightSide);
            llKatalogContainer.addView(card);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(KatalogActivity.this, SewaActivity.class);
                intent.putExtra("ALAT_PILIHAN", nama);
                startActivity(intent);
            });
        }
    }

    private HashMap<String, Integer> bacaDataSewa() {
        HashMap<String, Integer> result = new HashMap<>();
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
                        result.put(namaAlat, result.getOrDefault(namaAlat, 0) + jumlah);
                    } catch (NumberFormatException e) {}
                }
            }
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
