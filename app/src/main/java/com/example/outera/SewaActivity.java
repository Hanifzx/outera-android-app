package com.example.outera;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SewaActivity extends AppCompatActivity {

    private EditText etNamaPenyewa, etEmail, etJumlah;
    private Spinner spinnerAlat;
    private TextView tvTanggalPinjam, tvTanggalSelesai, tvTotalBiaya;
    private Button btnSubmitSewa;
    private LinearLayout llTglPinjam, llTglSelesai;

    private final String[] ALAT_PILIHAN = {
        "Tenda", "Carrier", "Matras", "Kompor", "Sleeping Bag", 
        "Lampu Tenda", "Kursi Lipat", "Meja Lipat", "Alat Masak", "Trekking Pole"
    };
    private final long[] HARGA_PILIHAN = {
        50000, 30000, 10000, 15000, 20000, 
        10000, 15000, 25000, 20000, 15000
    };

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("id", "ID"));
    private NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private Calendar calPinjam = Calendar.getInstance();
    private Calendar calSelesai = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewa);

        etNamaPenyewa = findViewById(R.id.etNamaPenyewa);
        etEmail = findViewById(R.id.etEmail);
        spinnerAlat = findViewById(R.id.spinnerAlat);
        etJumlah = findViewById(R.id.etJumlah);
        tvTanggalPinjam = findViewById(R.id.tvTanggalPinjam);
        tvTanggalSelesai = findViewById(R.id.tvTanggalSelesai);
        tvTotalBiaya = findViewById(R.id.tvTotalBiaya);
        btnSubmitSewa = findViewById(R.id.btnSubmitSewa);
        llTglPinjam = findViewById(R.id.llTglPinjam);
        llTglSelesai = findViewById(R.id.llTglSelesai);

        NavigationUtils.setupBottomNav(this, R.id.nav_sewa);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, ALAT_PILIHAN);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlat.setAdapter(adapter);

        String pilihanDariKatalog = getIntent().getStringExtra("ALAT_PILIHAN");
        if (pilihanDariKatalog != null) {
            for (int i = 0; i < ALAT_PILIHAN.length; i++) {
                if (ALAT_PILIHAN[i].equalsIgnoreCase(pilihanDariKatalog)) {
                    spinnerAlat.setSelection(i);
                    break;
                }
            }
        }

        calSelesai.add(Calendar.DAY_OF_YEAR, 1);
        tvTanggalPinjam.setText(sdf.format(calPinjam.getTime()));
        tvTanggalSelesai.setText(sdf.format(calSelesai.getTime()));

        llTglPinjam.setOnClickListener(v -> showDatePicker(true));
        llTglSelesai.setOnClickListener(v -> showDatePicker(false));

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) { updateRingkasan(); }
        };

        etJumlah.addTextChangedListener(watcher);
        spinnerAlat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { updateRingkasan(); }
            public void onNothingSelected(AdapterView<?> p) {}
        });

        btnSubmitSewa.setOnClickListener(v -> simpanDataSewa());
        
        updateRingkasan();
    }

    private void showDatePicker(boolean isPinjam) {
        Calendar targetCal = isPinjam ? calPinjam : calSelesai;
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    targetCal.set(year, month, dayOfMonth);
                    if (isPinjam) {
                        tvTanggalPinjam.setText(sdf.format(targetCal.getTime()));
                        if (calPinjam.after(calSelesai)) {
                            calSelesai.setTime(calPinjam.getTime());
                            calSelesai.add(Calendar.DAY_OF_YEAR, 1);
                            tvTanggalSelesai.setText(sdf.format(calSelesai.getTime()));
                        }
                    } else {
                        if (targetCal.before(calPinjam)) {
                            Toast.makeText(SewaActivity.this, "Tanggal selesai tidak boleh sebelum tanggal pinjam", Toast.LENGTH_SHORT).show();
                            calSelesai.setTime(calPinjam.getTime());
                            calSelesai.add(Calendar.DAY_OF_YEAR, 1);
                        }
                        tvTanggalSelesai.setText(sdf.format(calSelesai.getTime()));
                    }
                    updateRingkasan();
                },
                targetCal.get(Calendar.YEAR),
                targetCal.get(Calendar.MONTH),
                targetCal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private long hitungDurasi() {
        long diffMillis = calSelesai.getTimeInMillis() - calPinjam.getTimeInMillis();
        long diffDays = diffMillis / (1000 * 60 * 60 * 24);
        return Math.max(1, diffDays); // Minimal 1 hari
    }

    private void updateRingkasan() {
        try {
            String j = etJumlah.getText().toString().trim();

            if (j.isEmpty()) {
                tvTotalBiaya.setText("Rp 0");
                return;
            }

            long jumlah = Long.parseLong(j);

            if (jumlah <= 0) {
                tvTotalBiaya.setText("Rp 0");
                return;
            }

            long durasi = hitungDurasi();
            long harga = HARGA_PILIHAN[spinnerAlat.getSelectedItemPosition()];
            long total = harga * jumlah * durasi;

            tvTotalBiaya.setText(formatRupiah.format(total));
        } catch (NumberFormatException e) {
            tvTotalBiaya.setText("Rp 0");
        }
    }

    private void simpanDataSewa() {
        String nama = etNamaPenyewa.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String j = etJumlah.getText().toString().trim();

        if (nama.isEmpty() || email.isEmpty() || j.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long jumlah = Long.parseLong(j);
            long durasiHari = hitungDurasi();

            if (jumlah <= 0) {
                Toast.makeText(this, "Jumlah minimal 1", Toast.LENGTH_SHORT).show();
                return;
            }

            String namaAlat = spinnerAlat.getSelectedItem().toString();
            String tglSewa = sdf.format(calPinjam.getTime());
            String tglSelesai = sdf.format(calSelesai.getTime());

            long totalBiaya = HARGA_PILIHAN[spinnerAlat.getSelectedItemPosition()] * jumlah * durasiHari;
            long id = System.currentTimeMillis();

            // ID|Nama|Alat|Jumlah|Durasi|TglSewa|TglSelesai|TotalBiaya
            String dataToSave = id + "|" + nama + "|" + namaAlat + "|" + jumlah + "|" + durasiHari + "|" + tglSewa + "|" + tglSelesai + "|" + totalBiaya + "\n";

            FileOutputStream fos = openFileOutput("transaksi.txt", MODE_APPEND);
            fos.write(dataToSave.getBytes());
            fos.close();

            FileOutputStream fosHistory = openFileOutput("transaksi_history.txt", MODE_APPEND);
            fosHistory.write(dataToSave.getBytes());
            fosHistory.close();

            Toast.makeText(this, "Pesanan berhasil dikonfirmasi!", Toast.LENGTH_SHORT).show();

            etNamaPenyewa.setText("");
            etEmail.setText("");
            spinnerAlat.setSelection(0);
            etJumlah.setText("");
            
            calPinjam = Calendar.getInstance();
            calSelesai = Calendar.getInstance();
            calSelesai.add(Calendar.DAY_OF_YEAR, 1);
            tvTanggalPinjam.setText(sdf.format(calPinjam.getTime()));
            tvTanggalSelesai.setText(sdf.format(calSelesai.getTime()));
            
            updateRingkasan();

        } catch (Exception e) {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
