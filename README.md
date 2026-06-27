# Aplikasi Penjualan Hampers

## Persiapan database

1. Jalankan MySQL/MariaDB (misalnya melalui XAMPP). Apache tidak diperlukan.
2. Buka phpMyAdmin, pilih tab **Import**, lalu impor `db_hampers.sql`.
   Alternatif terminal: `mysql -u root -p < db_hampers.sql`.
3. Konfigurasi bawaan aplikasi adalah database `db_hampers`, pengguna `root`, dan
   password kosong. Jika konfigurasi server berbeda, ubah konstanta di
   `src/apphampers/Koneksi.java`.

## Membuka proyek di NetBeans

1. Pilih **File > Open Project**.
2. Pilih folder `Hampers_Uas`, lalu klik **Open Project**.
3. Pastikan Java Platform proyek menggunakan OpenJDK 22.

## Menambahkan MySQL Connector/J

1. Unduh dan ekstrak MySQL Connector/J yang sesuai dengan JDK Anda.
2. Klik kanan proyek > **Properties > Libraries**.
3. Pada tab **Compile**, klik **+** atau **Add JAR/Folder**.
4. Pilih file JAR Connector/J, lalu klik **OK**.

Connector/J wajib ada di runtime walaupun source bisa dikompilasi hanya dengan API
`java.sql` bawaan Java.

## Menjalankan aplikasi

1. Pilih **Run > Clean and Build Project**.
2. Klik **Run Project** atau tekan F6.
3. Main Class proyek sudah diatur ke `apphampers.Main`.
4. Login dengan username `admin` dan password `admin123`.
