package apphampers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class FormPembayaran extends JFrame {
    private JComboBox<String> cmbKodePesanan;
    private JTextField txtNamaPelanggan;
    private JTextField txtTotalTagihan;
    private JTextField txtTanggalBayar;
    private JComboBox<String> cmbMetodePembayaran;
    private JTextField txtJumlahBayar;
    private JTextField txtKembalian;
    private JTextField txtSisaTagihan;
    private JComboBox<String> cmbStatusPembayaran;
    private JTable tblPembayaran;
    private DefaultTableModel model;
    private JButton btnSimpan;
    private JButton btnReset;
    private double totalSudahDibayar;

    public FormPembayaran() {
        setTitle("Pembayaran");
        setSize(880, 610);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Form Pembayaran");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        JLabel lblKodePesanan = new JLabel("Kode Pesanan:");
        lblKodePesanan.setBounds(20, 50, 110, 25);
        add(lblKodePesanan);

        cmbKodePesanan = new JComboBox<>();
        cmbKodePesanan.setBounds(140, 50, 240, 25);
        add(cmbKodePesanan);

        JLabel lblNamaPelanggan = new JLabel("Nama Pelanggan:");
        lblNamaPelanggan.setBounds(420, 50, 120, 25);
        add(lblNamaPelanggan);

        txtNamaPelanggan = new JTextField();
        txtNamaPelanggan.setBounds(540, 50, 260, 25);
        txtNamaPelanggan.setEditable(false);
        add(txtNamaPelanggan);

        JLabel lblTotalTagihan = new JLabel("Total Tagihan:");
        lblTotalTagihan.setBounds(20, 90, 110, 25);
        add(lblTotalTagihan);

        txtTotalTagihan = new JTextField();
        txtTotalTagihan.setBounds(140, 90, 240, 25);
        txtTotalTagihan.setEditable(false);
        add(txtTotalTagihan);

        JLabel lblTanggalBayar = new JLabel("Tanggal Bayar:");
        lblTanggalBayar.setBounds(420, 90, 120, 25);
        add(lblTanggalBayar);

        txtTanggalBayar = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtTanggalBayar.setBounds(540, 90, 260, 25);
        add(txtTanggalBayar);

        JLabel lblMetode = new JLabel("Metode Pembayaran:");
        lblMetode.setBounds(20, 130, 140, 25);
        add(lblMetode);

        cmbMetodePembayaran = new JComboBox<>(new String[]{"Cash", "Transfer Bank", "QRIS", "E-Wallet"});
        cmbMetodePembayaran.setBounds(160, 130, 220, 25);
        add(cmbMetodePembayaran);

        JLabel lblJumlahBayar = new JLabel("Jumlah Bayar:");
        lblJumlahBayar.setBounds(420, 130, 120, 25);
        add(lblJumlahBayar);

        txtJumlahBayar = new JTextField();
        txtJumlahBayar.setBounds(540, 130, 260, 25);
        add(txtJumlahBayar);

        JLabel lblKembalian = new JLabel("Kembalian:");
        lblKembalian.setBounds(20, 170, 110, 25);
        add(lblKembalian);

        txtKembalian = new JTextField();
        txtKembalian.setBounds(140, 170, 240, 25);
        txtKembalian.setEditable(false);
        add(txtKembalian);

        JLabel lblStatus = new JLabel("Status Pembayaran:");
        lblStatus.setBounds(420, 170, 140, 25);
        add(lblStatus);

        cmbStatusPembayaran = new JComboBox<>(new String[]{"Belum Lunas", "DP", "Lunas"});
        cmbStatusPembayaran.setBounds(560, 170, 240, 25);
        cmbStatusPembayaran.setEnabled(false);
        add(cmbStatusPembayaran);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(140, 210, 120, 30);
        add(btnSimpan);

        btnReset = new JButton("Reset");
        btnReset.setBounds(280, 210, 120, 30);
        add(btnReset);

        JLabel lblSisaTagihan = new JLabel("Sisa Tagihan:");
        lblSisaTagihan.setBounds(420, 210, 120, 25);
        add(lblSisaTagihan);

        txtSisaTagihan = new JTextField("0.00");
        txtSisaTagihan.setBounds(540, 210, 260, 25);
        txtSisaTagihan.setEditable(false);
        add(txtSisaTagihan);

        model = new DefaultTableModel(new String[]{"ID Pembayaran", "Kode Pesanan", "Nama Pelanggan", "Total", "Tanggal Bayar", "Metode", "Jumlah Bayar", "Kembalian", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPembayaran = new JTable(model);
        JScrollPane scroll = new JScrollPane(tblPembayaran);
        scroll.setBounds(20, 260, 820, 260);
        add(scroll);

        cmbKodePesanan.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    loadOrderDetails();
                }
            }
        });

        txtJumlahBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateKembalian();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateKembalian();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateKembalian();
            }
        });

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePayment();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        loadKodePesanan();
        loadPembayaran();
        loadOrderDetails();
    }

    private void loadKodePesanan() {
        cmbKodePesanan.removeAllItems();
        String sql = "SELECT kode_pesanan FROM pesanan ORDER BY id_pesanan DESC";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbKodePesanan.addItem(rs.getString("kode_pesanan"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat kode pesanan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrderDetails() {
        String kode = (String) cmbKodePesanan.getSelectedItem();
        if (kode == null) {
            return;
        }
        String sql = "SELECT p.total, pel.nama_pelanggan, "
                + "COALESCE((SELECT SUM(pay.jumlah_bayar - GREATEST(pay.kembalian, 0)) "
                + "FROM pembayaran pay WHERE pay.id_pesanan = p.id_pesanan), 0) AS sudah_dibayar "
                + "FROM pesanan p JOIN pelanggan pel ON p.id_pelanggan = pel.id_pelanggan "
                + "WHERE p.kode_pesanan = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtTotalTagihan.setText(String.format("%.2f", rs.getDouble("total")));
                    txtNamaPelanggan.setText(rs.getString("nama_pelanggan"));
                    totalSudahDibayar = rs.getDouble("sudah_dibayar");
                    txtSisaTagihan.setText(String.format("%.2f",
                            Math.max(0, rs.getDouble("total") - totalSudahDibayar)));
                    calculateKembalian();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat detail pesanan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateKembalian() {
        try {
            double total = Double.parseDouble(txtTotalTagihan.getText().trim());
            double bayar = Double.parseDouble(txtJumlahBayar.getText().trim());
            if (Double.isNaN(bayar) || Double.isInfinite(bayar) || bayar < 0) {
                throw new NumberFormatException();
            }
            double sisaTagihan = Math.max(0, total - totalSudahDibayar);
            double kembalian = Math.max(0, bayar - sisaTagihan);
            txtKembalian.setText(String.format("%.2f", kembalian));
            double pembayaranDiterapkan = Math.min(bayar, sisaTagihan);
            double totalSetelahBayar = totalSudahDibayar + pembayaranDiterapkan;
            if (totalSetelahBayar >= total) {
                cmbStatusPembayaran.setSelectedItem("Lunas");
            } else if (totalSetelahBayar > 0) {
                cmbStatusPembayaran.setSelectedItem("DP");
            } else {
                cmbStatusPembayaran.setSelectedItem("Belum Lunas");
            }
        } catch (NumberFormatException ex) {
            txtKembalian.setText("0.00");
            try {
                double total = Double.parseDouble(txtTotalTagihan.getText().trim());
                if (total > 0 && totalSudahDibayar >= total) {
                    cmbStatusPembayaran.setSelectedItem("Lunas");
                } else if (totalSudahDibayar > 0) {
                    cmbStatusPembayaran.setSelectedItem("DP");
                } else {
                    cmbStatusPembayaran.setSelectedItem("Belum Lunas");
                }
            } catch (NumberFormatException ignored) {
                cmbStatusPembayaran.setSelectedItem("Belum Lunas");
            }
        }
    }

    private void savePayment() {
        String kode = (String) cmbKodePesanan.getSelectedItem();
        if (kode == null || kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih kode pesanan terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nama = txtNamaPelanggan.getText().trim();
        String totalText = txtTotalTagihan.getText().trim();
        String tanggal = txtTanggalBayar.getText().trim();
        String metode = (String) cmbMetodePembayaran.getSelectedItem();
        String jumlahBayarText = txtJumlahBayar.getText().trim();
        calculateKembalian();
        String status = (String) cmbStatusPembayaran.getSelectedItem();

        if (nama.isEmpty() || totalText.isEmpty() || jumlahBayarText.isEmpty() || tanggal.isEmpty() || status == null) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data pembayaran", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double jumlahBayar;
        double kembalian;
        double jumlahDiterapkan;
        try {
            double total = Double.parseDouble(totalText);
            jumlahBayar = Double.parseDouble(jumlahBayarText);
            if (Double.isNaN(jumlahBayar) || Double.isInfinite(jumlahBayar) || jumlahBayar < 0) {
                throw new NumberFormatException();
            }
            double sisaTagihan = Math.max(0, total - totalSudahDibayar);
            if (sisaTagihan <= 0) {
                JOptionPane.showMessageDialog(this, "Pesanan ini sudah lunas", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            kembalian = Math.max(0, jumlahBayar - sisaTagihan);
            jumlahDiterapkan = jumlahBayar - kembalian;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar harus berupa angka nol atau lebih", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.sql.Date.valueOf(tanggal);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Tanggal bayar harus berformat yyyy-MM-dd", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO pembayaran (id_pesanan, tanggal_bayar, metode_pembayaran, jumlah_bayar, kembalian, status_pembayaran) VALUES ((SELECT id_pesanan FROM pesanan WHERE kode_pesanan = ?), ?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, kode);
                ps.setDate(2, java.sql.Date.valueOf(tanggal));
                ps.setString(3, metode);
                ps.setDouble(4, jumlahBayar);
                ps.setDouble(5, kembalian);
                ps.setString(6, status);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new java.sql.SQLException("ID pembayaran tidak berhasil diperoleh");
                    }
                    PencatatanService.catatPemasukanPembayaran(conn,
                            java.sql.Date.valueOf(tanggal), keys.getInt(1), kode,
                            BigDecimal.valueOf(jumlahDiterapkan));
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil disimpan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadPembayaran();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan pembayaran: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPembayaran() {
        model.setRowCount(0);
        String sql = "SELECT pay.id_pembayaran, pes.kode_pesanan, pel.nama_pelanggan, pay.jumlah_bayar, pay.kembalian, pay.status_pembayaran, pay.tanggal_bayar, pay.metode_pembayaran, pes.total FROM pembayaran pay JOIN pesanan pes ON pay.id_pesanan = pes.id_pesanan JOIN pelanggan pel ON pes.id_pelanggan = pel.id_pelanggan";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_pembayaran"),
                    rs.getString("kode_pesanan"),
                    rs.getString("nama_pelanggan"),
                    rs.getDouble("total"),
                    rs.getString("tanggal_bayar"),
                    rs.getString("metode_pembayaran"),
                    rs.getDouble("jumlah_bayar"),
                    rs.getDouble("kembalian"),
                    rs.getString("status_pembayaran")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data pembayaran: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtJumlahBayar.setText("");
        txtKembalian.setText("0.00");
        cmbMetodePembayaran.setSelectedIndex(0);
        cmbStatusPembayaran.setSelectedIndex(0);
        txtTanggalBayar.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        loadOrderDetails();
    }
}
