package apphampers;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FormMutasiBarang extends JFrame {
    private final JTextField txtTanggal;
    private final JComboBox<String> cmbJenisBarang;
    private final JComboBox<String> cmbBarang;
    private final JTextField txtStokSaatIni;
    private final JComboBox<String> cmbJenisMutasi;
    private final JTextField txtJumlah;
    private final JTextField txtReferensi;
    private final JTextArea txtKeterangan;
    private final DefaultTableModel model;

    public FormMutasiBarang() {
        setTitle("Pencatatan Barang Masuk dan Keluar");
        setSize(1120, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Pencatatan Barang Masuk dan Keluar");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 420, 30);
        add(lblTitle);

        addLabel("Tanggal (yyyy-MM-dd):", 20, 50, 150);
        txtTanggal = new JTextField(today());
        txtTanggal.setBounds(170, 50, 210, 25);
        add(txtTanggal);

        addLabel("Jenis Barang:", 430, 50, 110);
        cmbJenisBarang = new JComboBox<>(new String[]{"Produk", "Hampers"});
        cmbJenisBarang.setBounds(540, 50, 220, 25);
        add(cmbJenisBarang);

        addLabel("Barang:", 20, 85, 150);
        cmbBarang = new JComboBox<>();
        cmbBarang.setBounds(170, 85, 360, 25);
        add(cmbBarang);

        addLabel("Stok Saat Ini:", 570, 85, 110);
        txtStokSaatIni = new JTextField("0");
        txtStokSaatIni.setEditable(false);
        txtStokSaatIni.setBounds(680, 85, 160, 25);
        add(txtStokSaatIni);

        addLabel("Jenis Mutasi:", 20, 120, 150);
        cmbJenisMutasi = new JComboBox<>(new String[]{"Masuk", "Keluar"});
        cmbJenisMutasi.setBounds(170, 120, 210, 25);
        add(cmbJenisMutasi);

        addLabel("Jumlah:", 430, 120, 110);
        txtJumlah = new JTextField();
        txtJumlah.setBounds(540, 120, 220, 25);
        add(txtJumlah);

        addLabel("Referensi:", 20, 155, 150);
        txtReferensi = new JTextField();
        txtReferensi.setBounds(170, 155, 210, 25);
        add(txtReferensi);

        addLabel("Keterangan:", 430, 155, 110);
        txtKeterangan = new JTextArea();
        txtKeterangan.setLineWrap(true);
        txtKeterangan.setWrapStyleWord(true);
        JScrollPane scrollKeterangan = new JScrollPane(txtKeterangan);
        scrollKeterangan.setBounds(540, 155, 400, 55);
        add(scrollKeterangan);

        JButton btnSimpan = new JButton("Simpan Mutasi");
        btnSimpan.setBounds(170, 205, 150, 32);
        add(btnSimpan);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(335, 205, 110, 32);
        add(btnReset);

        JLabel lblInfo = new JLabel("Catatan otomatis dari transaksi juga ditampilkan pada tabel ini.");
        lblInfo.setBounds(540, 215, 500, 20);
        add(lblInfo);

        model = new DefaultTableModel(new String[]{
            "ID", "Tanggal", "Jenis Barang", "Nama Barang", "Mutasi", "Jumlah",
            "Stok Awal", "Stok Akhir", "Sumber", "Referensi", "Keterangan"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tblMutasi = new JTable(model);
        tblMutasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {55, 90, 95, 170, 75, 70, 80, 80, 90, 105, 230};
        for (int i = 0; i < widths.length; i++) {
            tblMutasi.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        JScrollPane scrollTable = new JScrollPane(tblMutasi);
        scrollTable.setBounds(20, 255, 1060, 360);
        add(scrollTable);

        cmbJenisBarang.addActionListener(e -> loadBarang());
        cmbBarang.addActionListener(e -> loadStokSaatIni());
        btnSimpan.addActionListener(e -> simpanMutasi());
        btnReset.addActionListener(e -> resetForm());

        loadBarang();
        loadMutasi();
    }

    private void addLabel(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, 25);
        add(label);
    }

    private void loadBarang() {
        cmbBarang.removeAllItems();
        String jenis = (String) cmbJenisBarang.getSelectedItem();
        String sql = "Produk".equals(jenis)
                ? "SELECT id_produk AS id, nama_produk AS nama FROM produk ORDER BY nama_produk"
                : "SELECT id_hampers AS id, nama_hampers AS nama FROM hampers ORDER BY nama_hampers";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbBarang.addItem(rs.getInt("id") + " - " + rs.getString("nama"));
            }
            loadStokSaatIni();
        } catch (Exception ex) {
            showError("Gagal memuat data barang", ex);
        }
    }

    private void loadStokSaatIni() {
        Integer idBarang = selectedBarangId();
        if (idBarang == null) {
            txtStokSaatIni.setText("0");
            return;
        }

        String jenis = (String) cmbJenisBarang.getSelectedItem();
        String sql = "Produk".equals(jenis)
                ? "SELECT stok FROM produk WHERE id_produk = ?"
                : "SELECT stok FROM hampers WHERE id_hampers = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBarang);
            try (ResultSet rs = ps.executeQuery()) {
                txtStokSaatIni.setText(rs.next() ? String.valueOf(rs.getInt("stok")) : "0");
            }
        } catch (Exception ex) {
            showError("Gagal memuat stok", ex);
        }
    }

    private void simpanMutasi() {
        Integer idBarang = selectedBarangId();
        String item = (String) cmbBarang.getSelectedItem();
        if (idBarang == null || item == null) {
            showWarning("Data barang belum tersedia");
            return;
        }

        java.sql.Date tanggal;
        int jumlah;
        try {
            tanggal = java.sql.Date.valueOf(txtTanggal.getText().trim());
        } catch (IllegalArgumentException ex) {
            showWarning("Tanggal harus berformat yyyy-MM-dd");
            return;
        }
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showWarning("Jumlah harus berupa bilangan bulat lebih dari 0");
            return;
        }

        String jenisBarang = (String) cmbJenisBarang.getSelectedItem();
        String jenisMutasi = (String) cmbJenisMutasi.getSelectedItem();
        String namaBarang = item.substring(item.indexOf(" - ") + 3);

        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int stokSebelum = getStokForUpdate(conn, jenisBarang, idBarang);
                int stokSesudah = "Masuk".equals(jenisMutasi)
                        ? stokSebelum + jumlah : stokSebelum - jumlah;
                if (stokSesudah < 0) {
                    throw new IllegalArgumentException(
                            "Stok tidak cukup. Stok saat ini hanya " + stokSebelum + ".");
                }

                updateStok(conn, jenisBarang, idBarang, stokSesudah);
                PencatatanService.catatMutasi(conn, tanggal, jenisBarang, idBarang,
                        namaBarang, jenisMutasi, jumlah, stokSebelum, stokSesudah,
                        "Manual", txtReferensi.getText(), txtKeterangan.getText(), null);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

            JOptionPane.showMessageDialog(this, "Mutasi barang berhasil disimpan",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadMutasi();
            loadBarang();
            resetForm();
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        } catch (Exception ex) {
            showError("Gagal menyimpan mutasi barang", ex);
        }
    }

    private int getStokForUpdate(Connection conn, String jenisBarang, int idBarang)
            throws SQLException {
        String sql = "Produk".equals(jenisBarang)
                ? "SELECT stok FROM produk WHERE id_produk = ? FOR UPDATE"
                : "SELECT stok FROM hampers WHERE id_hampers = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBarang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stok");
                }
            }
        }
        throw new SQLException("Barang tidak ditemukan");
    }

    private void updateStok(Connection conn, String jenisBarang, int idBarang, int stok)
            throws SQLException {
        String sql = "Produk".equals(jenisBarang)
                ? "UPDATE produk SET stok = ? WHERE id_produk = ?"
                : "UPDATE hampers SET stok = ? WHERE id_hampers = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stok);
            ps.setInt(2, idBarang);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Stok barang tidak berhasil diperbarui");
            }
        }
    }

    private void loadMutasi() {
        model.setRowCount(0);
        String sql = "SELECT id_mutasi, tanggal, jenis_barang, nama_barang, "
                + "jenis_mutasi, jumlah, stok_sebelum, stok_sesudah, sumber, "
                + "referensi, keterangan FROM mutasi_barang "
                + "ORDER BY tanggal DESC, id_mutasi DESC";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_mutasi"), rs.getDate("tanggal"),
                    rs.getString("jenis_barang"), rs.getString("nama_barang"),
                    rs.getString("jenis_mutasi"), rs.getInt("jumlah"),
                    rs.getInt("stok_sebelum"), rs.getInt("stok_sesudah"),
                    rs.getString("sumber"), rs.getString("referensi"),
                    rs.getString("keterangan")
                });
            }
        } catch (Exception ex) {
            showError("Gagal memuat riwayat mutasi", ex);
        }
    }

    private Integer selectedBarangId() {
        String item = (String) cmbBarang.getSelectedItem();
        if (item == null) {
            return null;
        }
        int separator = item.indexOf(" - ");
        if (separator < 1) {
            return null;
        }
        try {
            return Integer.valueOf(item.substring(0, separator));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void resetForm() {
        txtTanggal.setText(today());
        cmbJenisMutasi.setSelectedIndex(0);
        txtJumlah.setText("");
        txtReferensi.setText("");
        txtKeterangan.setText("");
        loadStokSaatIni();
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
