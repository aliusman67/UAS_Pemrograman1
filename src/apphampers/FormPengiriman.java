package apphampers;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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

public class FormPengiriman extends JFrame {
    private final JTextField txtIdPengiriman;
    private final JComboBox<String> cmbPesanan;
    private final JTextField txtNamaPelanggan;
    private final JTextArea txtAlamat;
    private final JTextField txtKurir;
    private final JTextField txtNomorResi;
    private final JTextField txtTanggalKirim;
    private final JTextField txtEstimasiTiba;
    private final JComboBox<String> cmbStatus;
    private final JTextField txtKeterangan;
    private final JButton btnSimpan;
    private final JButton btnUbah;
    private final DefaultTableModel modelPengiriman;
    private final DefaultTableModel modelRiwayat;
    private final JTable tblPengiriman;
    private boolean loadingForm;

    public FormPengiriman() {
        setTitle("Tracking Pengiriman");
        setSize(1180, 780);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Tracking Pengiriman Pesanan");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 350, 30);
        add(lblTitle);

        addLabel("ID Pengiriman:", 20, 50, 110);
        txtIdPengiriman = new JTextField();
        txtIdPengiriman.setEditable(false);
        txtIdPengiriman.setBounds(130, 50, 100, 25);
        add(txtIdPengiriman);

        addLabel("Kode Pesanan:", 260, 50, 110);
        cmbPesanan = new JComboBox<>();
        cmbPesanan.setBounds(370, 50, 210, 25);
        add(cmbPesanan);

        addLabel("Pelanggan:", 620, 50, 90);
        txtNamaPelanggan = new JTextField();
        txtNamaPelanggan.setEditable(false);
        txtNamaPelanggan.setBounds(710, 50, 400, 25);
        add(txtNamaPelanggan);

        addLabel("Alamat Kirim:", 20, 90, 110);
        txtAlamat = new JTextArea();
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        scrollAlamat.setBounds(130, 90, 450, 65);
        add(scrollAlamat);

        addLabel("Kurir:", 620, 90, 90);
        txtKurir = new JTextField();
        txtKurir.setBounds(710, 90, 400, 25);
        add(txtKurir);

        addLabel("Nomor Resi:", 620, 130, 90);
        txtNomorResi = new JTextField();
        txtNomorResi.setBounds(710, 130, 400, 25);
        add(txtNomorResi);

        addLabel("Tanggal Kirim:", 20, 170, 110);
        txtTanggalKirim = new JTextField(today());
        txtTanggalKirim.setToolTipText("Boleh dikosongkan jika pesanan belum dikirim");
        txtTanggalKirim.setBounds(130, 170, 160, 25);
        add(txtTanggalKirim);

        addLabel("Estimasi Tiba:", 320, 170, 100);
        txtEstimasiTiba = new JTextField();
        txtEstimasiTiba.setBounds(420, 170, 160, 25);
        add(txtEstimasiTiba);

        addLabel("Status:", 620, 170, 90);
        cmbStatus = new JComboBox<>(new String[]{
            "Menunggu Diproses", "Dikemas", "Diserahkan ke Kurir",
            "Dalam Perjalanan", "Terkirim", "Dibatalkan"
        });
        cmbStatus.setBounds(710, 170, 250, 25);
        add(cmbStatus);

        addLabel("Keterangan:", 20, 210, 110);
        txtKeterangan = new JTextField();
        txtKeterangan.setBounds(130, 210, 830, 25);
        add(txtKeterangan);

        btnSimpan = new JButton("Buat Pengiriman");
        btnSimpan.setBounds(130, 250, 150, 32);
        add(btnSimpan);

        btnUbah = new JButton("Update Status");
        btnUbah.setBounds(295, 250, 140, 32);
        btnUbah.setEnabled(false);
        add(btnUbah);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(450, 250, 110, 32);
        add(btnReset);

        modelPengiriman = new DefaultTableModel(new String[]{
            "ID", "Kode Pesanan", "Pelanggan", "Kurir", "Nomor Resi",
            "Tanggal Kirim", "Estimasi", "Status", "Diperbarui", "Keterangan"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPengiriman = new JTable(modelPengiriman);
        tblPengiriman.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] shipmentWidths = {50, 110, 150, 120, 130, 100, 100, 145, 145, 220};
        for (int i = 0; i < shipmentWidths.length; i++) {
            tblPengiriman.getColumnModel().getColumn(i).setPreferredWidth(shipmentWidths[i]);
        }
        JScrollPane scrollPengiriman = new JScrollPane(tblPengiriman);
        scrollPengiriman.setBounds(20, 300, 1120, 220);
        add(scrollPengiriman);

        JLabel lblRiwayat = new JLabel("Riwayat Status Pengiriman");
        lblRiwayat.setFont(new Font("Arial", Font.BOLD, 14));
        lblRiwayat.setBounds(20, 535, 250, 25);
        add(lblRiwayat);

        modelRiwayat = new DefaultTableModel(new String[]{
            "ID", "Waktu", "Status", "Keterangan"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tblRiwayat = new JTable(modelRiwayat);
        tblRiwayat.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblRiwayat.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblRiwayat.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblRiwayat.getColumnModel().getColumn(3).setPreferredWidth(650);
        JScrollPane scrollRiwayat = new JScrollPane(tblRiwayat);
        scrollRiwayat.setBounds(20, 565, 1120, 145);
        add(scrollRiwayat);

        cmbPesanan.addActionListener(e -> {
            if (!loadingForm) {
                loadDetailPesanan();
            }
        });
        btnSimpan.addActionListener(e -> simpanPengiriman());
        btnUbah.addActionListener(e -> updatePengiriman());
        btnReset.addActionListener(e -> resetForm());
        tblPengiriman.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPengiriman.getSelectedRow();
                if (row >= 0) {
                    loadPengirimanById(Integer.parseInt(modelPengiriman.getValueAt(row, 0).toString()));
                }
            }
        });

        loadPesanan();
        loadPengiriman();
    }

    private void addLabel(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, 25);
        add(label);
    }

    private void loadPesanan() {
        loadingForm = true;
        cmbPesanan.removeAllItems();
        String sql = "SELECT id_pesanan, kode_pesanan FROM pesanan ORDER BY id_pesanan DESC";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbPesanan.addItem(rs.getInt("id_pesanan") + " - " + rs.getString("kode_pesanan"));
            }
        } catch (Exception ex) {
            showError("Gagal memuat pesanan", ex);
        } finally {
            loadingForm = false;
        }
        loadDetailPesanan();
    }

    private void loadDetailPesanan() {
        Integer idPesanan = selectedPesananId();
        if (idPesanan == null) {
            txtNamaPelanggan.setText("");
            txtAlamat.setText("");
            return;
        }
        String sql = "SELECT pel.nama_pelanggan, pel.alamat FROM pesanan pes "
                + "JOIN pelanggan pel ON pes.id_pelanggan = pel.id_pelanggan "
                + "WHERE pes.id_pesanan = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPesanan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtNamaPelanggan.setText(rs.getString("nama_pelanggan"));
                    txtAlamat.setText(rs.getString("alamat") == null ? "" : rs.getString("alamat"));
                }
            }
        } catch (Exception ex) {
            showError("Gagal memuat detail pesanan", ex);
        }
    }

    private void simpanPengiriman() {
        Integer idPesanan = selectedPesananId();
        if (idPesanan == null) {
            showWarning("Data pesanan belum tersedia");
            return;
        }
        if (!validasiForm()) {
            return;
        }

        String sql = "INSERT INTO pengiriman (id_pesanan, kurir, nomor_resi, "
                + "alamat_pengiriman, tanggal_kirim, estimasi_tiba, status_pengiriman, "
                + "keterangan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                isiParameterPengiriman(ps, idPesanan);
                ps.executeUpdate();
                int idPengiriman;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("ID pengiriman tidak berhasil diperoleh");
                    }
                    idPengiriman = keys.getInt(1);
                }
                insertRiwayat(conn, idPengiriman, "Pengiriman dibuat");
                updateStatusPesanan(conn, idPesanan);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

            JOptionPane.showMessageDialog(this, "Data pengiriman berhasil dibuat",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadPengiriman();
            resetForm();
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState())) {
                showWarning("Pesanan sudah memiliki data pengiriman atau nomor resi sudah digunakan");
            } else {
                showError("Gagal membuat data pengiriman", ex);
            }
        } catch (Exception ex) {
            showError("Gagal membuat data pengiriman", ex);
        }
    }

    private void updatePengiriman() {
        if (txtIdPengiriman.getText().trim().isEmpty()) {
            showWarning("Pilih data pengiriman pada tabel terlebih dahulu");
            return;
        }
        Integer idPesanan = selectedPesananId();
        if (idPesanan == null || !validasiForm()) {
            return;
        }

        int idPengiriman = Integer.parseInt(txtIdPengiriman.getText());
        String sql = "UPDATE pengiriman SET id_pesanan = ?, kurir = ?, nomor_resi = ?, "
                + "alamat_pengiriman = ?, tanggal_kirim = ?, estimasi_tiba = ?, "
                + "status_pengiriman = ?, keterangan = ? WHERE id_pengiriman = ?";
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                isiParameterPengiriman(ps, idPesanan);
                ps.setInt(9, idPengiriman);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Data pengiriman tidak ditemukan");
                }
                insertRiwayat(conn, idPengiriman, "Status pengiriman diperbarui");
                updateStatusPesanan(conn, idPesanan);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

            JOptionPane.showMessageDialog(this, "Status pengiriman berhasil diperbarui",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadPengiriman();
            loadRiwayat(idPengiriman);
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState())) {
                showWarning("Nomor resi sudah digunakan oleh pengiriman lain");
            } else {
                showError("Gagal memperbarui pengiriman", ex);
            }
        } catch (Exception ex) {
            showError("Gagal memperbarui pengiriman", ex);
        }
    }

    private boolean validasiForm() {
        if (txtAlamat.getText().trim().isEmpty()) {
            showWarning("Alamat pengiriman harus diisi");
            return false;
        }

        String status = (String) cmbStatus.getSelectedItem();
        if (("Diserahkan ke Kurir".equals(status) || "Dalam Perjalanan".equals(status)
                || "Terkirim".equals(status)) && txtKurir.getText().trim().isEmpty()) {
            showWarning("Nama kurir harus diisi untuk status " + status);
            return false;
        }

        try {
            java.sql.Date tanggalKirim = parseOptionalDate(txtTanggalKirim.getText());
            java.sql.Date estimasi = parseOptionalDate(txtEstimasiTiba.getText());
            if (tanggalKirim != null && estimasi != null && estimasi.before(tanggalKirim)) {
                showWarning("Estimasi tiba tidak boleh sebelum tanggal kirim");
                return false;
            }
        } catch (IllegalArgumentException ex) {
            showWarning("Tanggal kirim dan estimasi harus berformat yyyy-MM-dd");
            return false;
        }
        return true;
    }

    private void isiParameterPengiriman(PreparedStatement ps, int idPesanan)
            throws SQLException {
        ps.setInt(1, idPesanan);
        setNullableString(ps, 2, txtKurir.getText());
        setNullableString(ps, 3, txtNomorResi.getText());
        ps.setString(4, txtAlamat.getText().trim());
        setNullableDate(ps, 5, parseOptionalDate(txtTanggalKirim.getText()));
        setNullableDate(ps, 6, parseOptionalDate(txtEstimasiTiba.getText()));
        ps.setString(7, (String) cmbStatus.getSelectedItem());
        setNullableString(ps, 8, txtKeterangan.getText());
    }

    private void insertRiwayat(Connection conn, int idPengiriman, String defaultMessage)
            throws SQLException {
        String sql = "INSERT INTO riwayat_pengiriman "
                + "(id_pengiriman, status_pengiriman, keterangan) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengiriman);
            ps.setString(2, (String) cmbStatus.getSelectedItem());
            String note = txtKeterangan.getText().trim();
            ps.setString(3, note.isEmpty() ? defaultMessage : note);
            ps.executeUpdate();
        }
    }

    private void updateStatusPesanan(Connection conn, int idPesanan) throws SQLException {
        String sql = "UPDATE pesanan SET status_pesanan = ? WHERE id_pesanan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, (String) cmbStatus.getSelectedItem());
            ps.setInt(2, idPesanan);
            ps.executeUpdate();
        }
    }

    private void loadPengiriman() {
        modelPengiriman.setRowCount(0);
        String sql = "SELECT kir.id_pengiriman, pes.kode_pesanan, pel.nama_pelanggan, "
                + "kir.kurir, kir.nomor_resi, kir.tanggal_kirim, kir.estimasi_tiba, "
                + "kir.status_pengiriman, kir.diperbarui_pada, kir.keterangan "
                + "FROM pengiriman kir JOIN pesanan pes ON kir.id_pesanan = pes.id_pesanan "
                + "JOIN pelanggan pel ON pes.id_pelanggan = pel.id_pelanggan "
                + "ORDER BY kir.diperbarui_pada DESC, kir.id_pengiriman DESC";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelPengiriman.addRow(new Object[]{
                    rs.getInt("id_pengiriman"), rs.getString("kode_pesanan"),
                    rs.getString("nama_pelanggan"), rs.getString("kurir"),
                    rs.getString("nomor_resi"), rs.getDate("tanggal_kirim"),
                    rs.getDate("estimasi_tiba"), rs.getString("status_pengiriman"),
                    rs.getTimestamp("diperbarui_pada"), rs.getString("keterangan")
                });
            }
        } catch (Exception ex) {
            showError("Gagal memuat data pengiriman", ex);
        }
    }

    private void loadPengirimanById(int idPengiriman) {
        String sql = "SELECT kir.*, pel.nama_pelanggan FROM pengiriman kir "
                + "JOIN pesanan pes ON kir.id_pesanan = pes.id_pesanan "
                + "JOIN pelanggan pel ON pes.id_pelanggan = pel.id_pelanggan "
                + "WHERE kir.id_pengiriman = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengiriman);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return;
                }
                loadingForm = true;
                selectPesanan(rs.getInt("id_pesanan"));
                txtIdPengiriman.setText(String.valueOf(idPengiriman));
                txtNamaPelanggan.setText(rs.getString("nama_pelanggan"));
                txtAlamat.setText(rs.getString("alamat_pengiriman"));
                txtKurir.setText(valueOrEmpty(rs.getString("kurir")));
                txtNomorResi.setText(valueOrEmpty(rs.getString("nomor_resi")));
                txtTanggalKirim.setText(rs.getDate("tanggal_kirim") == null
                        ? "" : rs.getDate("tanggal_kirim").toString());
                txtEstimasiTiba.setText(rs.getDate("estimasi_tiba") == null
                        ? "" : rs.getDate("estimasi_tiba").toString());
                cmbStatus.setSelectedItem(rs.getString("status_pengiriman"));
                txtKeterangan.setText(valueOrEmpty(rs.getString("keterangan")));
                cmbPesanan.setEnabled(false);
                btnSimpan.setEnabled(false);
                btnUbah.setEnabled(true);
                loadingForm = false;
                loadRiwayat(idPengiriman);
            }
        } catch (Exception ex) {
            loadingForm = false;
            showError("Gagal memuat detail pengiriman", ex);
        }
    }

    private void loadRiwayat(int idPengiriman) {
        modelRiwayat.setRowCount(0);
        String sql = "SELECT id_riwayat, waktu_status, status_pengiriman, keterangan "
                + "FROM riwayat_pengiriman WHERE id_pengiriman = ? "
                + "ORDER BY waktu_status DESC, id_riwayat DESC";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengiriman);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelRiwayat.addRow(new Object[]{
                        rs.getInt("id_riwayat"), rs.getTimestamp("waktu_status"),
                        rs.getString("status_pengiriman"), rs.getString("keterangan")
                    });
                }
            }
        } catch (Exception ex) {
            showError("Gagal memuat riwayat pengiriman", ex);
        }
    }

    private void resetForm() {
        loadingForm = true;
        txtIdPengiriman.setText("");
        txtKurir.setText("");
        txtNomorResi.setText("");
        txtTanggalKirim.setText(today());
        txtEstimasiTiba.setText("");
        cmbStatus.setSelectedIndex(0);
        txtKeterangan.setText("");
        cmbPesanan.setEnabled(true);
        btnSimpan.setEnabled(true);
        btnUbah.setEnabled(false);
        tblPengiriman.clearSelection();
        modelRiwayat.setRowCount(0);
        loadingForm = false;
        loadDetailPesanan();
    }

    private Integer selectedPesananId() {
        String item = (String) cmbPesanan.getSelectedItem();
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

    private void selectPesanan(int idPesanan) {
        for (int i = 0; i < cmbPesanan.getItemCount(); i++) {
            String item = cmbPesanan.getItemAt(i);
            if (item.startsWith(idPesanan + " - ")) {
                cmbPesanan.setSelectedIndex(i);
                return;
            }
        }
    }

    private java.sql.Date parseOptionalDate(String value) {
        String text = value == null ? "" : value.trim();
        return text.isEmpty() ? null : java.sql.Date.valueOf(text);
    }

    private void setNullableString(PreparedStatement ps, int index, String value)
            throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }

    private void setNullableDate(PreparedStatement ps, int index, java.sql.Date value)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, value);
        }
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
