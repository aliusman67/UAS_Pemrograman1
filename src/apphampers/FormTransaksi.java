package apphampers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class FormTransaksi extends JFrame {
    private JTextField txtKodePesanan;
    private JTextField txtTanggal;
    private JComboBox<String> cmbPelanggan;
    private JComboBox<String> cmbHampers;
    private JTextField txtHarga;
    private JTextField txtJumlah;
    private JTextField txtSubtotal;
    private JTextField txtTotal;
    private JTable tblDetailTransaksi;
    private DefaultTableModel model;
    private JButton btnTambahItem;
    private JButton btnHapusItem;
    private JButton btnSimpanTransaksi;
    private JButton btnReset;
    private JButton btnCetakNota;

    public FormTransaksi() {
        setTitle("Transaksi");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Form Transaksi Pemesanan Hampers");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 380, 30);
        add(lblTitle);

        JLabel lblKodePesanan = new JLabel("Kode Pesanan:");
        lblKodePesanan.setBounds(20, 60, 100, 25);
        add(lblKodePesanan);

        txtKodePesanan = new JTextField();
        txtKodePesanan.setBounds(140, 60, 200, 25);
        txtKodePesanan.setEditable(false);
        add(txtKodePesanan);

        JLabel lblTanggal = new JLabel("Tanggal:");
        lblTanggal.setBounds(380, 60, 80, 25);
        add(lblTanggal);

        txtTanggal = new JTextField();
        txtTanggal.setBounds(450, 60, 160, 25);
        txtTanggal.setEditable(false);
        add(txtTanggal);

        JLabel lblPelanggan = new JLabel("Pelanggan:");
        lblPelanggan.setBounds(20, 95, 100, 25);
        add(lblPelanggan);

        cmbPelanggan = new JComboBox<>();
        cmbPelanggan.setBounds(140, 95, 300, 25);
        add(cmbPelanggan);

        JLabel lblHampers = new JLabel("Hampers:");
        lblHampers.setBounds(20, 130, 100, 25);
        add(lblHampers);

        cmbHampers = new JComboBox<>();
        cmbHampers.setBounds(140, 130, 300, 25);
        add(cmbHampers);

        JLabel lblHarga = new JLabel("Harga:");
        lblHarga.setBounds(20, 165, 100, 25);
        add(lblHarga);

        txtHarga = new JTextField();
        txtHarga.setBounds(140, 165, 200, 25);
        txtHarga.setEditable(false);
        add(txtHarga);

        JLabel lblJumlah = new JLabel("Jumlah:");
        lblJumlah.setBounds(20, 200, 100, 25);
        add(lblJumlah);

        txtJumlah = new JTextField();
        txtJumlah.setBounds(140, 200, 200, 25);
        add(txtJumlah);

        JLabel lblSubtotal = new JLabel("Subtotal:");
        lblSubtotal.setBounds(20, 235, 100, 25);
        add(lblSubtotal);

        txtSubtotal = new JTextField();
        txtSubtotal.setBounds(140, 235, 200, 25);
        txtSubtotal.setEditable(false);
        add(txtSubtotal);

        btnTambahItem = new JButton("Tambah Item");
        btnTambahItem.setBounds(380, 165, 120, 30);
        add(btnTambahItem);

        btnHapusItem = new JButton("Hapus Item");
        btnHapusItem.setBounds(520, 165, 120, 30);
        add(btnHapusItem);

        model = new DefaultTableModel(new String[]{"ID Hampers", "Nama Hampers", "Harga", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDetailTransaksi = new JTable(model);
        JScrollPane scroll = new JScrollPane(tblDetailTransaksi);
        scroll.setBounds(20, 270, 840, 220);
        add(scroll);

        JLabel lblTotal = new JLabel("Total:");
        lblTotal.setBounds(20, 505, 100, 25);
        add(lblTotal);

        txtTotal = new JTextField("0.00");
        txtTotal.setBounds(140, 505, 200, 25);
        txtTotal.setEditable(false);
        add(txtTotal);

        btnSimpanTransaksi = new JButton("Simpan Transaksi");
        btnSimpanTransaksi.setBounds(380, 505, 160, 30);
        add(btnSimpanTransaksi);

        btnReset = new JButton("Reset");
        btnReset.setBounds(560, 505, 120, 30);
        add(btnReset);

        btnCetakNota = new JButton("Cetak Nota");
        btnCetakNota.setBounds(700, 505, 160, 30);
        add(btnCetakNota);

        cmbHampers.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    loadHampersPrice();
                }
            }
        });

        txtJumlah.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateSubtotal();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateSubtotal();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateSubtotal();
            }
        });

        btnTambahItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });

        btnHapusItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeItem();
            }
        });

        btnSimpanTransaksi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTransaction();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        btnCetakNota.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printNota();
            }
        });

        tblDetailTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblDetailTransaksi.getSelectedRow();
                if (row >= 0) {
                    txtJumlah.setText(model.getValueAt(row, 3).toString());
                    txtSubtotal.setText(model.getValueAt(row, 4).toString());
                }
            }
        });

        initForm();
    }

    private void initForm() {
        txtKodePesanan.setText(generateKodePesanan());
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        loadPelanggan();
        loadHampers();
    }

    private String generateKodePesanan() {
        String kode = "INV-001";
        String sql = "SELECT MAX(id_pesanan) FROM pesanan";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (maxId > 0) {
                    kode = String.format("INV-%03d", maxId + 1);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat generate kode: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return kode;
    }

    private void loadPelanggan() {
        cmbPelanggan.removeAllItems();
        String sql = "SELECT id_pelanggan, nama_pelanggan FROM pelanggan";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbPelanggan.addItem(rs.getInt("id_pelanggan") + " - " + rs.getString("nama_pelanggan"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHampers() {
        cmbHampers.removeAllItems();
        String sql = "SELECT id_hampers, nama_hampers FROM hampers ORDER BY nama_hampers";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbHampers.addItem(rs.getInt("id_hampers") + " - " + rs.getString("nama_hampers"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat hampers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHampersPrice() {
        String item = (String) cmbHampers.getSelectedItem();
        if (item != null && !item.isEmpty()) {
            int separator = item.indexOf(" - ");
            if (separator > 0) {
                String sql = "SELECT harga FROM hampers WHERE id_hampers = ?";
                try (Connection conn = Koneksi.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(item.substring(0, separator)));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            txtHarga.setText(String.format("%.2f", rs.getDouble("harga")));
                            calculateSubtotal();
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal memuat harga hampers: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void calculateSubtotal() {
        try {
            double harga = Double.parseDouble(txtHarga.getText().trim());
            int jumlah = Integer.parseInt(txtJumlah.getText().trim());
            txtSubtotal.setText(String.valueOf(harga * jumlah));
        } catch (NumberFormatException ex) {
            txtSubtotal.setText("0.00");
        }
    }

    private void addItem() {
        String item = (String) cmbHampers.getSelectedItem();
        if (item == null || item.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih hampers terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus angka lebih dari 0", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int separator = item.indexOf(" - ");
        if (separator < 1) {
            JOptionPane.showMessageDialog(this, "Format data hampers tidak valid", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idHampers = Integer.parseInt(item.substring(0, separator));
        String nama = item.substring(separator + 3);
        double harga = Double.parseDouble(txtHarga.getText());
        double subtotal = harga * jumlah;

        int jumlahDalamKeranjang = jumlah;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (idHampers == Integer.parseInt(model.getValueAt(i, 0).toString())) {
                jumlahDalamKeranjang += Integer.parseInt(model.getValueAt(i, 3).toString());
            }
        }

        try (Connection conn = Koneksi.getConnection()) {
            int stok = getHampersStock(conn, idHampers);
            if (stok < jumlahDalamKeranjang) {
                JOptionPane.showMessageDialog(this,
                        "Stok " + nama + " hanya " + stok + ", sedangkan total yang dipilih " + jumlahDalamKeranjang,
                        "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memeriksa stok: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.addRow(new Object[]{idHampers, nama, harga, jumlah, subtotal});
        updateTotal();
        txtJumlah.setText("");
        txtSubtotal.setText("0.00");
    }

    private void removeItem() {
        int row = tblDetailTransaksi.getSelectedRow();
        if (row >= 0) {
            model.removeRow(row);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin dihapus", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            total += Double.parseDouble(model.getValueAt(i, 4).toString());
        }
        txtTotal.setText(String.format("%.2f", total));
    }

    private void saveTransaction() {
        if (cmbPelanggan.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Data pelanggan belum tersedia", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tambahkan minimal satu item transaksi", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = txtKodePesanan.getText().trim();
        String tanggal = txtTanggal.getText().trim();
        String pelangganItem = (String) cmbPelanggan.getSelectedItem();
        if (pelangganItem == null || pelangganItem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPelanggan = Integer.parseInt(pelangganItem.split(" - ")[0]);
        double total = Double.parseDouble(txtTotal.getText().trim());

        try {
            java.sql.Date.valueOf(tanggal);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Tanggal transaksi harus berformat yyyy-MM-dd",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            String insertPesanan = "INSERT INTO pesanan (kode_pesanan, id_pelanggan, tanggal_pesanan, total, status_pesanan) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertPesanan, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, kode);
                ps.setInt(2, idPelanggan);
                ps.setDate(3, java.sql.Date.valueOf(tanggal));
                ps.setDouble(4, total);
                ps.setString(5, "Pending");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idPesanan = rs.getInt(1);
                        String insertDetail = "INSERT INTO detail_pesanan (id_pesanan, id_hampers, harga, jumlah, subtotal) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement psDetail = conn.prepareStatement(insertDetail)) {
                            for (int i = 0; i < model.getRowCount(); i++) {
                                int idHampers = Integer.parseInt(model.getValueAt(i, 0).toString());
                                double harga = Double.parseDouble(model.getValueAt(i, 2).toString());
                                int jumlah = Integer.parseInt(model.getValueAt(i, 3).toString());
                                double subtotal = Double.parseDouble(model.getValueAt(i, 4).toString());

                                int stock = getHampersStock(conn, idHampers);
                                if (stock < jumlah) {
                                    conn.rollback();
                                    JOptionPane.showMessageDialog(this, "Stok hampers tidak cukup untuk ID " + idHampers, "Peringatan", JOptionPane.WARNING_MESSAGE);
                                    return;
                                }

                                psDetail.setInt(1, idPesanan);
                                psDetail.setInt(2, idHampers);
                                psDetail.setDouble(3, harga);
                                psDetail.setInt(4, jumlah);
                                psDetail.setDouble(5, subtotal);
                                psDetail.executeUpdate();
                                updateHampersStock(conn, idHampers, stock - jumlah);
                            }
                        }
                        conn.commit();
                        JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        resetForm();
                    } else {
                        throw new SQLException("ID pesanan baru tidak berhasil diperoleh");
                    }
                }
            } catch (Exception ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Error saat menyimpan transaksi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan transaksi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getHampersStock(Connection conn, int idHampers) throws Exception {
        String sql = "SELECT stok FROM hampers WHERE id_hampers = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHampers);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stok");
                }
            }
        }
        return 0;
    }

    private void updateHampersStock(Connection conn, int idHampers, int newStock) throws Exception {
        String sql = "UPDATE hampers SET stok = ? WHERE id_hampers = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, idHampers);
            ps.executeUpdate();
        }
    }

    private void resetForm() {
        txtJumlah.setText("");
        txtSubtotal.setText("0.00");
        txtTotal.setText("0.00");
        model.setRowCount(0);
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        loadPelanggan();
        loadHampers();
        txtKodePesanan.setText(generateKodePesanan());
    }

    private void printNota() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada item untuk dicetak", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder nota = new StringBuilder();
        nota.append("Nota Pesanan\n");
        nota.append("Kode: ").append(txtKodePesanan.getText()).append("\n");
        nota.append("Tanggal: ").append(txtTanggal.getText()).append("\n");
        nota.append("Pelanggan: ").append(cmbPelanggan.getSelectedItem()).append("\n\n");
        nota.append(String.format("%-30s %-8s %-8s %-10s\n", "Hampers", "Harga", "Qty", "Subtotal"));
        for (int i = 0; i < model.getRowCount(); i++) {
            nota.append(String.format("%s %-8s %-8s %-10s\n", model.getValueAt(i, 1), model.getValueAt(i, 2), model.getValueAt(i, 3), model.getValueAt(i, 4)));
        }
        nota.append("\nTotal: ").append(txtTotal.getText());
        JTextArea area = new JTextArea(nota.toString(), 20, 62);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Nota Transaksi", JOptionPane.INFORMATION_MESSAGE);
    }
}
