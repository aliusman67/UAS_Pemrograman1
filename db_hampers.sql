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

UPDATE users
SET nama_lengkap = 'Administrator',
    password = 'pbkdf2_sha256$210000$32WgGAWcoyyLhHMcEf56gw==$cVjYD0H6+L/mxXNBr6Yxq82GjmOni5HVqkWv2C7DC2s=',
    role = 'admin'
WHERE username = 'admin';

INSERT INTO users (nama_lengkap, username, password, role)
SELECT 'Administrator', 'admin', 'pbkdf2_sha256$210000$32WgGAWcoyyLhHMcEf56gw==$cVjYD0H6+L/mxXNBr6Yxq82GjmOni5HVqkWv2C7DC2s=', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
