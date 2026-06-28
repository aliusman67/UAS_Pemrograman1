package apphampers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Menyatukan pencatatan audit yang harus ikut dalam transaksi database utama.
 */
final class PencatatanService {
    private PencatatanService() {
    }

    static void catatMutasi(Connection conn, Date tanggal, String jenisBarang,
            int idBarang, String namaBarang, String jenisMutasi, int jumlah,
            int stokSebelum, int stokSesudah, String sumber, String referensi,
            String keterangan, Integer idPesanan) throws SQLException {
        String sql = "INSERT INTO mutasi_barang (tanggal, jenis_barang, id_barang, "
                + "nama_barang, jenis_mutasi, jumlah, stok_sebelum, stok_sesudah, "
                + "sumber, referensi, keterangan, id_pesanan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tanggal);
            ps.setString(2, jenisBarang);
            ps.setInt(3, idBarang);
            ps.setString(4, namaBarang);
            ps.setString(5, jenisMutasi);
            ps.setInt(6, jumlah);
            ps.setInt(7, stokSebelum);
            ps.setInt(8, stokSesudah);
            ps.setString(9, sumber);
            setNullableString(ps, 10, referensi);
            setNullableString(ps, 11, keterangan);
            if (idPesanan == null) {
                ps.setNull(12, Types.INTEGER);
            } else {
                ps.setInt(12, idPesanan);
            }
            ps.executeUpdate();
        }
    }

    static void catatPemasukanPembayaran(Connection conn, Date tanggal,
            int idPembayaran, String kodePesanan, BigDecimal jumlah) throws SQLException {
        if (jumlah == null || jumlah.signum() <= 0) {
            return;
        }

        String sql = "INSERT INTO keuangan (tanggal, jenis_transaksi, kategori, "
                + "deskripsi, jumlah, referensi, sumber, id_pembayaran) "
                + "VALUES (?, 'Pemasukan', 'Penjualan', ?, ?, ?, 'Pembayaran', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tanggal);
            ps.setString(2, "Pembayaran pesanan " + kodePesanan);
            ps.setBigDecimal(3, jumlah);
            ps.setString(4, kodePesanan);
            ps.setInt(5, idPembayaran);
            ps.executeUpdate();
        }
    }

    private static void setNullableString(PreparedStatement ps, int index, String value)
            throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }
}
