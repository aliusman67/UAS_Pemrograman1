USE db_hampers;

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

-- Migrasikan pembayaran yang sudah ada ke buku kas tanpa membuat duplikat.
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
