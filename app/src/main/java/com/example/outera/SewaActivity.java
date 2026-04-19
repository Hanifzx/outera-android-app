package com.example.outera;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;

public class SewaActivity extends AppCompatActivity {

    private EditText etNamaPenyewa;
    private EditText etNamaBarang;
    private EditText etJumlah;
    private EditText etTanggalSewa;
    private Button btnSubmitSewa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewa);

        // Inisialisasi views
        etNamaPenyewa = findViewById(R.id.etNamaPenyewa);
        etNamaBarang = findViewById(R.id.etNamaBarang);
        etJumlah = findViewById(R.id.etJumlah);
        etTanggalSewa = findViewById(R.id.etTanggalSewa);
        btnSubmitSewa = findViewById(R.id.btnSubmitSewa);

        btnSubmitSewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanDataKeFile();
            }
        });
    }

    private void simpanDataKeFile() {
        String namaPenyewa = etNamaPenyewa.getText().toString();
        String namaBarang = etNamaBarang.getText().toString();
        String jumlahStr = etJumlah.getText().toString();
        String tanggalSewa = etTanggalSewa.getText().toString();

        if (namaPenyewa.isEmpty() || namaBarang.isEmpty() || jumlahStr.isEmpty() || tanggalSewa.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = System.currentTimeMillis();
        // Sesuai permintaan pengguna: ID|Nama|Alat|Durasi|Total (Menggunakan format
        // yang dikondisikan ke variabel yang ada)
        String dataToSave = id + "|" + namaPenyewa + "|" + namaBarang + "|" + jumlahStr + "|" + tanggalSewa + "\n";

        try {
            FileOutputStream fos = openFileOutput("transaksi.txt", MODE_APPEND);
            fos.write(dataToSave.getBytes());
            fos.close();

            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();

            // Kosongkan form kembali
            etNamaPenyewa.setText("");
            etNamaBarang.setText("");
            etJumlah.setText("");
            etTanggalSewa.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
