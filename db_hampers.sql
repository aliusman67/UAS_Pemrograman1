CREATE DATABASE IF NOT EXISTS db_hampers
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE db_hampers;

CREATE TABLE IF NOT EXISTS users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB;

-- Menyediakan ruang yang cukup untuk hash pada database versi lama.
ALTER TABLE users MODIFY password VARCHAR(255) NOT NULL;

CREATE TABLE IF NOT EXISTS produk (
    id_produk INT AUTO_INCREMENT PRIMARY KEY,
    nama_produk VARCHAR(100) NOT NULL,
    kategori VARCHAR(50) NOT NULL,
    harga DECIMAL(12,2) NOT NULL DEFAULT 0,
    stok INT NOT NULL DEFAULT 0,
    satuan VARCHAR(30) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS hampers (
    id_hampers INT AUTO_INCREMENT PRIMARY KEY,
    nama_hampers VARCHAR(100) NOT NULL,
    kategori VARCHAR(50) NOT NULL,
    harga DECIMAL(12,2) NOT NULL DEFAULT 0,
    stok INT NOT NULL DEFAULT 0,
    deskripsi TEXT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pelanggan (
    id_pelanggan INT AUTO_INCREMENT PRIMARY KEY,
    nama_pelanggan VARCHAR(100) NOT NULL,
    no_hp VARCHAR(20),
    email VARCHAR(100),
    alamat TEXT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pesanan (
    id_pesanan INT AUTO_INCREMENT PRIMARY KEY,
    kode_pesanan VARCHAR(30) NOT NULL,
    id_pelanggan INT NOT NULL,
    tanggal_pesanan DATE NOT NULL,
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    status_pesanan VARCHAR(30) NOT NULL,
    UNIQUE KEY uk_pesanan_kode (kode_pesanan),
    CONSTRAINT fk_pesanan_pelanggan FOREIGN KEY (id_pelanggan)
        REFERENCES pelanggan (id_pelanggan)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detail_pesanan (
    id_detail_pesanan INT AUTO_INCREMENT PRIMARY KEY,
    id_pesanan INT NOT NULL,
    id_hampers INT NOT NULL,
    harga DECIMAL(12,2) NOT NULL DEFAULT 0,
    jumlah INT NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_detail_pesanan FOREIGN KEY (id_pesanan)
        REFERENCES pesanan (id_pesanan)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detail_hampers FOREIGN KEY (id_hampers)
        REFERENCES hampers (id_hampers)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pembayaran (
    id_pembayaran INT AUTO_INCREMENT PRIMARY KEY,
    id_pesanan INT NOT NULL,
    tanggal_bayar DATE NOT NULL,
    metode_pembayaran VARCHAR(50) NOT NULL,
    jumlah_bayar DECIMAL(12,2) NOT NULL DEFAULT 0,
    kembalian DECIMAL(12,2) NOT NULL DEFAULT 0,
    status_pembayaran VARCHAR(30) NOT NULL,
    CONSTRAINT fk_pembayaran_pesanan FOREIGN KEY (id_pesanan)
        REFERENCES pesanan (id_pesanan)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS keuangan (
    id_keuangan INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATE NOT NULL,
    jenis_transaksi VARCHAR(20) NOT NULL,
    kategori VARCHAR(50) NOT NULL,
    deskripsi VARCHAR(255) NOT NULL,
    jumlah DECIMAL(12,2) NOT NULL DEFAULT 0,
    referensi VARCHAR(50),
    sumber VARCHAR(30) NOT NULL DEFAULT 'Manual',
    id_pembayaran INT,
    dibuat_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_keuangan_pembayaran (id_pembayaran),
    KEY idx_keuangan_tanggal (tanggal),
    CONSTRAINT fk_keuangan_pembayaran FOREIGN KEY (id_pembayaran)
        REFERENCES pembayaran (id_pembayaran)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS mutasi_barang (
    id_mutasi INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATE NOT NULL,
    jenis_barang VARCHAR(20) NOT NULL,
    id_barang INT NOT NULL,
    nama_barang VARCHAR(100) NOT NULL,
    jenis_mutasi VARCHAR(20) NOT NULL,
    jumlah INT NOT NULL,
    stok_sebelum INT NOT NULL,
    stok_sesudah INT NOT NULL,
    sumber VARCHAR(30) NOT NULL DEFAULT 'Manual',
    referensi VARCHAR(50),
    keterangan VARCHAR(255),
    id_pesanan INT,
    dibuat_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_mutasi_tanggal (tanggal),
    KEY idx_mutasi_barang (jenis_barang, id_barang),
    CONSTRAINT fk_mutasi_pesanan FOREIGN KEY (id_pesanan)
        REFERENCES pesanan (id_pesanan)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pengiriman (
    id_pengiriman INT AUTO_INCREMENT PRIMARY KEY,
    id_pesanan INT NOT NULL,
    kurir VARCHAR(100),
    nomor_resi VARCHAR(100),
    alamat_pengiriman TEXT NOT NULL,
    tanggal_kirim DATE,
    estimasi_tiba DATE,
    status_pengiriman VARCHAR(30) NOT NULL DEFAULT 'Menunggu Diproses',
    keterangan VARCHAR(255),
    diperbarui_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pengiriman_pesanan (id_pesanan),
    UNIQUE KEY uk_pengiriman_resi (nomor_resi),
    KEY idx_pengiriman_status (status_pengiriman),
    CONSTRAINT fk_pengiriman_pesanan FOREIGN KEY (id_pesanan)
        REFERENCES pesanan (id_pesanan)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS riwayat_pengiriman (
    id_riwayat INT AUTO_INCREMENT PRIMARY KEY,
    id_pengiriman INT NOT NULL,
    status_pengiriman VARCHAR(30) NOT NULL,
    keterangan VARCHAR(255),
    waktu_status TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_riwayat_pengiriman (id_pengiriman, waktu_status),
    CONSTRAINT fk_riwayat_pengiriman FOREIGN KEY (id_pengiriman)
        REFERENCES pengiriman (id_pengiriman)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- Mengisi buku kas dari pembayaran lama tanpa membuat catatan ganda.
INSERT INTO keuangan (tanggal, jenis_transaksi, kategori, deskripsi, jumlah,
    referensi, sumber, id_pembayaran)
SELECT pay.tanggal_bayar, 'Pemasukan', 'Penjualan',
    CONCAT('Pembayaran pesanan ', pes.kode_pesanan),
    GREATEST(pay.jumlah_bayar - GREATEST(pay.kembalian, 0), 0),
    pes.kode_pesanan, 'Pembayaran', pay.id_pembayaran
FROM pembayaran pay
JOIN pesanan pes ON pay.id_pesanan = pes.id_pesanan
WHERE GREATEST(pay.jumlah_bayar - GREATEST(pay.kembalian, 0), 0) > 0
  AND NOT EXISTS (
      SELECT 1 FROM keuangan k WHERE k.id_pembayaran = pay.id_pembayaran
  );

UPDATE users
SET nama_lengkap = 'Administrator',
    password = 'pbkdf2_sha256$210000$32WgGAWcoyyLhHMcEf56gw==$cVjYD0H6+L/mxXNBr6Yxq82GjmOni5HVqkWv2C7DC2s=',
    role = 'admin'
WHERE username = 'admin';

INSERT INTO users (nama_lengkap, username, password, role)
SELECT 'Administrator', 'admin', 'pbkdf2_sha256$210000$32WgGAWcoyyLhHMcEf56gw==$cVjYD0H6+L/mxXNBr6Yxq82GjmOni5HVqkWv2C7DC2s=', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
