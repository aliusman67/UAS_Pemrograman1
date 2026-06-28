package apphampers;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FormKeuangan extends JFrame {
    private final JTextField txtTanggal;
    private final JComboBox<String> cmbJenis;
    private final JComboBox<String> cmbKategori;
    private final JTextField txtDeskripsi;
    private final JTextField txtJumlah;
    private final JTextField txtReferensi;
    private final JTextField txtTanggalAwal;
    private final JTextField txtTanggalAkhir;
    private final DefaultTableModel model;
    private final JTable tblKeuangan;
    private final JLabel lblTotalPemasukan;
    private final JLabel lblTotalPengeluaran;
    private final JLabel lblSaldo;

    public FormKeuangan() {
        setTitle("Pencatatan Keuangan");
        setSize(1120, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Pencatatan Keuangan");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        addLabel("Tanggal (yyyy-MM-dd):", 20, 50, 150);
        txtTanggal = new JTextField(today());
        txtTanggal.setBounds(170, 50, 200, 25);
        add(txtTanggal);

        addLabel("Jenis:", 410, 50, 80);
        cmbJenis = new JComboBox<>(new String[]{"Pemasukan", "Pengeluaran"});
        cmbJenis.setBounds(490, 50, 180, 25);
        add(cmbJenis);

        addLabel("Kategori:", 710, 50, 80);
        cmbKategori = new JComboBox<>();
        cmbKategori.setBounds(790, 50, 260, 25);
        add(cmbKategori);

        addLabel("Deskripsi:", 20, 90, 150);
        txtDeskripsi = new JTextField();
        txtDeskripsi.setBounds(170, 90, 500, 25);
        add(txtDeskripsi);

        addLabel("Jumlah (Rp):", 710, 90, 100);
        txtJumlah = new JTextField();
        txtJumlah.setBounds(810, 90, 240, 25);
        add(txtJumlah);

        addLabel("Referensi:", 20, 130, 150);
        txtReferensi = new JTextField();
        txtReferensi.setBounds(170, 130, 200, 25);
        add(txtReferensi);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(410, 130, 110, 30);
        add(btnSimpan);

        JButton btnHapus = new JButton("Hapus Manual");
        btnHapus.setBounds(530, 130, 140, 30);
        add(btnHapus);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(680, 130, 100, 30);
        add(btnReset);

        JLabel lblFilter = new JLabel("Filter periode:");
        lblFilter.setFont(new Font("Arial", Font.BOLD, 13));
        lblFilter.setBounds(20, 180, 110, 25);
        add(lblFilter);

        txtTanggalAwal = new JTextField();
        txtTanggalAwal.setToolTipText("Tanggal awal yyyy-MM-dd");
        txtTanggalAwal.setBounds(130, 180, 140, 25);
        add(txtTanggalAwal);

        JLabel lblSampai = new JLabel("s.d.");
        lblSampai.setBounds(280, 180, 35, 25);
        add(lblSampai);

        txtTanggalAkhir = new JTextField();
        txtTanggalAkhir.setToolTipText("Tanggal akhir yyyy-MM-dd");
        txtTanggalAkhir.setBounds(315, 180, 140, 25);
        add(txtTanggalAkhir);

        JButton btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.setBounds(470, 180, 120, 26);
        add(btnTampilkan);

        JButton btnSemua = new JButton("Semua Data");
        btnSemua.setBounds(600, 180, 120, 26);
        add(btnSemua);

        model = new DefaultTableModel(new String[]{
            "ID", "Tanggal", "Jenis", "Kategori", "Deskripsi", "Jumlah",
            "Referensi", "Sumber"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKeuangan = new JTable(model);
        tblKeuangan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {55, 95, 105, 130, 310, 130, 120, 100};
        for (int i = 0; i < widths.length; i++) {
            tblKeuangan.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        JScrollPane scroll = new JScrollPane(tblKeuangan);
        scroll.setBounds(20, 220, 1060, 330);
        add(scroll);

        lblTotalPemasukan = summaryLabel("Total Pemasukan: Rp0", new Color(0, 120, 40));
        lblTotalPemasukan.setBounds(20, 570, 330, 30);
        add(lblTotalPemasukan);

        lblTotalPengeluaran = summaryLabel("Total Pengeluaran: Rp0", new Color(190, 40, 40));
        lblTotalPengeluaran.setBounds(370, 570, 330, 30);
        add(lblTotalPengeluaran);

        lblSaldo = summaryLabel("Saldo: Rp0", new Color(30, 70, 170));
        lblSaldo.setBounds(720, 570, 350, 30);
        add(lblSaldo);

        cmbJenis.addActionListener(e -> loadKategori());
        btnSimpan.addActionListener(e -> simpanKeuangan());
        btnHapus.addActionListener(e -> hapusKeuanganManual());
        btnReset.addActionListener(e -> resetForm());
        btnTampilkan.addActionListener(e -> loadKeuangan());
        btnSemua.addActionListener(e -> {
            txtTanggalAwal.setText("");
            txtTanggalAkhir.setText("");
            loadKeuangan();
        });

        loadKategori();
        loadKeuangan();
    }

    private void addLabel(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, 25);
        add(label);
    }

    private JLabel summaryLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }

    private void loadKategori() {
        cmbKategori.removeAllItems();
        String[] categories = "Pemasukan".equals(cmbJenis.getSelectedItem())
                ? new String[]{"Penjualan", "Modal", "Piutang", "Lainnya"}
                : new String[]{"Pembelian Stok", "Operasional", "Pengiriman", "Gaji", "Lainnya"};
        for (String category : categories) {
            cmbKategori.addItem(category);
        }
    }

    private void simpanKeuangan() {
        java.sql.Date tanggal;
        BigDecimal jumlah;
        try {
            tanggal = java.sql.Date.valueOf(txtTanggal.getText().trim());
        } catch (IllegalArgumentException ex) {
            showWarning("Tanggal harus berformat yyyy-MM-dd");
            return;
        }
        try {
            jumlah = new BigDecimal(txtJumlah.getText().trim());
            if (jumlah.signum() <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showWarning("Jumlah harus berupa angka lebih dari 0 tanpa pemisah ribuan");
            return;
        }

        String deskripsi = txtDeskripsi.getText().trim();
        if (deskripsi.isEmpty()) {
            showWarning("Deskripsi transaksi keuangan harus diisi");
            return;
        }

        String sql = "INSERT INTO keuangan (tanggal, jenis_transaksi, kategori, "
                + "deskripsi, jumlah, referensi, sumber) VALUES (?, ?, ?, ?, ?, ?, 'Manual')";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tanggal);
            ps.setString(2, (String) cmbJenis.getSelectedItem());
            ps.setString(3, (String) cmbKategori.getSelectedItem());
            ps.setString(4, deskripsi);
            ps.setBigDecimal(5, jumlah);
            String referensi = txtReferensi.getText().trim();
            if (referensi.isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, referensi);
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Catatan keuangan berhasil disimpan",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadKeuangan();
        } catch (Exception ex) {
            showError("Gagal menyimpan catatan keuangan", ex);
        }
    }

    private void hapusKeuanganManual() {
        int row = tblKeuangan.getSelectedRow();
        if (row < 0) {
            showWarning("Pilih catatan manual yang ingin dihapus");
            return;
        }
        if (!"Manual".equals(model.getValueAt(row, 7))) {
            showWarning("Catatan otomatis dari pembayaran tidak dapat dihapus dari menu ini");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus catatan keuangan manual yang dipilih?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM keuangan WHERE id_keuangan = ? AND sumber = 'Manual'";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(model.getValueAt(row, 0).toString()));
            if (ps.executeUpdate() == 1) {
                loadKeuangan();
            }
        } catch (Exception ex) {
            showError("Gagal menghapus catatan keuangan", ex);
        }
    }

    private void loadKeuangan() {
        String awalText = txtTanggalAwal.getText().trim();
        String akhirText = txtTanggalAkhir.getText().trim();
        boolean filter = !awalText.isEmpty() || !akhirText.isEmpty();
        java.sql.Date awal = null;
        java.sql.Date akhir = null;
        if (filter) {
            if (awalText.isEmpty() || akhirText.isEmpty()) {
                showWarning("Tanggal awal dan akhir harus diisi bersama-sama");
                return;
            }
            try {
                awal = java.sql.Date.valueOf(awalText);
                akhir = java.sql.Date.valueOf(akhirText);
                if (awal.after(akhir)) {
                    showWarning("Tanggal awal tidak boleh melewati tanggal akhir");
                    return;
                }
            } catch (IllegalArgumentException ex) {
                showWarning("Format filter tanggal harus yyyy-MM-dd");
                return;
            }
        }

        String sql = "SELECT id_keuangan, tanggal, jenis_transaksi, kategori, "
                + "deskripsi, jumlah, referensi, sumber FROM keuangan ";
        if (filter) {
            sql += "WHERE tanggal BETWEEN ? AND ? ";
        }
        sql += "ORDER BY tanggal DESC, id_keuangan DESC";

        model.setRowCount(0);
        BigDecimal pemasukan = BigDecimal.ZERO;
        BigDecimal pengeluaran = BigDecimal.ZERO;
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (filter) {
                ps.setDate(1, awal);
                ps.setDate(2, akhir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal nilai = rs.getBigDecimal("jumlah");
                    String jenis = rs.getString("jenis_transaksi");
                    if ("Pemasukan".equals(jenis)) {
                        pemasukan = pemasukan.add(nilai);
                    } else {
                        pengeluaran = pengeluaran.add(nilai);
                    }
                    model.addRow(new Object[]{
                        rs.getInt("id_keuangan"), rs.getDate("tanggal"), jenis,
                        rs.getString("kategori"), rs.getString("deskripsi"), nilai,
                        rs.getString("referensi"), rs.getString("sumber")
                    });
                }
            }
            lblTotalPemasukan.setText("Total Pemasukan: " + rupiah(pemasukan));
            lblTotalPengeluaran.setText("Total Pengeluaran: " + rupiah(pengeluaran));
            lblSaldo.setText("Saldo: " + rupiah(pemasukan.subtract(pengeluaran)));
        } catch (Exception ex) {
            showError("Gagal memuat catatan keuangan", ex);
        }
    }

    private void resetForm() {
        txtTanggal.setText(today());
        cmbJenis.setSelectedIndex(0);
        txtDeskripsi.setText("");
        txtJumlah.setText("");
        txtReferensi.setText("");
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private String rupiah(BigDecimal value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        format.setMaximumFractionDigits(2);
        return format.format(value);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
