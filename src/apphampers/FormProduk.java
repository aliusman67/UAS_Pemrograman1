package apphampers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FormProduk extends JFrame {
    private JTextField txtIdProduk;
    private JTextField txtNamaProduk;
    private JTextField txtKategoriProduk;
    private JTextField txtHargaProduk;
    private JTextField txtStokProduk;
    private JTextField txtSatuanProduk;
    private JTextField txtCariProduk;
    private JTable tblProduk;
    private DefaultTableModel model;
    private JButton btnSimpan;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnReset;
    private JButton btnCari;

    public FormProduk() {
        setTitle("Data Produk");
        setSize(840, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Form Data Produk");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        JLabel lblIdProduk = new JLabel("ID Produk:");
        lblIdProduk.setBounds(20, 50, 100, 25);
        add(lblIdProduk);

        txtIdProduk = new JTextField();
        txtIdProduk.setBounds(130, 50, 160, 25);
        txtIdProduk.setEditable(false);
        add(txtIdProduk);

        JLabel lblNamaProduk = new JLabel("Nama Produk:");
        lblNamaProduk.setBounds(20, 85, 100, 25);
        add(lblNamaProduk);

        txtNamaProduk = new JTextField();
        txtNamaProduk.setBounds(130, 85, 320, 25);
        add(txtNamaProduk);

        JLabel lblKategoriProduk = new JLabel("Kategori:");
        lblKategoriProduk.setBounds(20, 120, 100, 25);
        add(lblKategoriProduk);

        txtKategoriProduk = new JTextField();
        txtKategoriProduk.setBounds(130, 120, 200, 25);
        add(txtKategoriProduk);

        JLabel lblHargaProduk = new JLabel("Harga:");
        lblHargaProduk.setBounds(20, 155, 100, 25);
        add(lblHargaProduk);

        txtHargaProduk = new JTextField();
        txtHargaProduk.setBounds(130, 155, 200, 25);
        add(txtHargaProduk);

        JLabel lblStokProduk = new JLabel("Stok:");
        lblStokProduk.setBounds(20, 190, 100, 25);
        add(lblStokProduk);

        txtStokProduk = new JTextField();
        txtStokProduk.setBounds(130, 190, 200, 25);
        add(txtStokProduk);

        JLabel lblSatuanProduk = new JLabel("Satuan:");
        lblSatuanProduk.setBounds(20, 225, 100, 25);
        add(lblSatuanProduk);

        txtSatuanProduk = new JTextField();
        txtSatuanProduk.setBounds(130, 225, 200, 25);
        add(txtSatuanProduk);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(20, 270, 100, 30);
        add(btnSimpan);

        btnUbah = new JButton("Ubah");
        btnUbah.setBounds(130, 270, 100, 30);
        add(btnUbah);

        btnHapus = new JButton("Hapus");
        btnHapus.setBounds(240, 270, 100, 30);
        add(btnHapus);

        btnReset = new JButton("Reset");
        btnReset.setBounds(350, 270, 100, 30);
        add(btnReset);

        txtCariProduk = new JTextField();
        txtCariProduk.setBounds(520, 50, 200, 25);
        add(txtCariProduk);

        btnCari = new JButton("Cari");
        btnCari.setBounds(730, 50, 70, 25);
        add(btnCari);

        model = new DefaultTableModel(new String[]{"ID Produk", "Nama Produk", "Kategori", "Harga", "Stok", "Satuan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProduk = new JTable(model);
        JScrollPane scroll = new JScrollPane(tblProduk);
        scroll.setBounds(20, 320, 780, 160);
        add(scroll);

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProduk();
            }
        });

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduk();
            }
        });

        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduk();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProduk();
            }
        });

        tblProduk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblProduk.getSelectedRow();
                if (row >= 0) {
                    txtIdProduk.setText(model.getValueAt(row, 0).toString());
                    txtNamaProduk.setText(model.getValueAt(row, 1).toString());
                    txtKategoriProduk.setText(model.getValueAt(row, 2).toString());
                    txtHargaProduk.setText(model.getValueAt(row, 3).toString());
                    txtStokProduk.setText(model.getValueAt(row, 4).toString());
                    txtSatuanProduk.setText(model.getValueAt(row, 5).toString());
                }
            }
        });

        loadProduk();
    }

    private void loadProduk() {
        model.setRowCount(0);
        String sql = "SELECT * FROM produk";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_produk"),
                    rs.getString("nama_produk"),
                    rs.getString("kategori"),
                    rs.getDouble("harga"),
                    rs.getInt("stok"),
                    rs.getString("satuan")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProduk() {
        String nama = txtNamaProduk.getText().trim();
        String kategori = txtKategoriProduk.getText().trim();
        String hargaText = txtHargaProduk.getText().trim();
        String stokText = txtStokProduk.getText().trim();
        String satuan = txtSatuanProduk.getText().trim();

        if (nama.isEmpty() || kategori.isEmpty() || hargaText.isEmpty() || stokText.isEmpty() || satuan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double harga;
        int stok;
        try {
            harga = Double.parseDouble(hargaText);
            stok = Integer.parseInt(stokText);
            if (Double.isNaN(harga) || Double.isInfinite(harga) || harga < 0 || stok < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka nol atau lebih", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO produk (nama_produk, kategori, harga, stok, satuan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nama);
                ps.setString(2, kategori);
                ps.setDouble(3, harga);
                ps.setInt(4, stok);
                ps.setString(5, satuan);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("ID produk tidak berhasil diperoleh");
                    }
                    if (stok > 0) {
                        PencatatanService.catatMutasi(conn,
                                new java.sql.Date(System.currentTimeMillis()), "Produk",
                                keys.getInt(1), nama, "Masuk", stok, 0, stok,
                                "Data Master", null, "Stok awal produk", null);
                    }
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
            JOptionPane.showMessageDialog(this, "Data produk berhasil disimpan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadProduk();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduk() {
        String idText = txtIdProduk.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data produk terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = txtNamaProduk.getText().trim();
        String kategori = txtKategoriProduk.getText().trim();
        String hargaText = txtHargaProduk.getText().trim();
        String stokText = txtStokProduk.getText().trim();
        String satuan = txtSatuanProduk.getText().trim();

        if (nama.isEmpty() || kategori.isEmpty() || hargaText.isEmpty() || stokText.isEmpty() || satuan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double harga;
        int stok;
        int id;
        try {
            harga = Double.parseDouble(hargaText);
            stok = Integer.parseInt(stokText);
            id = Integer.parseInt(idText);
            if (Double.isNaN(harga) || Double.isInfinite(harga) || harga < 0 || stok < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka nol atau lebih", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE produk SET nama_produk = ?, kategori = ?, harga = ?, stok = ?, satuan = ? WHERE id_produk = ?";
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int stokSebelum;
                String selectStok = "SELECT stok FROM produk WHERE id_produk = ? FOR UPDATE";
                try (PreparedStatement psStok = conn.prepareStatement(selectStok)) {
                    psStok.setInt(1, id);
                    try (ResultSet rs = psStok.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Produk tidak ditemukan");
                        }
                        stokSebelum = rs.getInt("stok");
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nama);
                    ps.setString(2, kategori);
                    ps.setDouble(3, harga);
                    ps.setInt(4, stok);
                    ps.setString(5, satuan);
                    ps.setInt(6, id);
                    ps.executeUpdate();
                }
                if (stok != stokSebelum) {
                    String jenisMutasi = stok > stokSebelum ? "Masuk" : "Keluar";
                    PencatatanService.catatMutasi(conn,
                            new java.sql.Date(System.currentTimeMillis()), "Produk", id,
                            nama, jenisMutasi, Math.abs(stok - stokSebelum), stokSebelum,
                            stok, "Penyesuaian", null,
                            "Perubahan stok dari form Data Produk", null);
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
            JOptionPane.showMessageDialog(this, "Data produk berhasil diubah", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadProduk();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat mengubah produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduk() {
        String idText = txtIdProduk.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data produk terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID harus angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM produk WHERE id_produk = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data produk berhasil dihapus", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadProduk();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menghapus produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProduk() {
        String keyword = txtCariProduk.getText().trim();
        model.setRowCount(0);
        String sql = "SELECT * FROM produk WHERE nama_produk LIKE ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("kategori"),
                        rs.getDouble("harga"),
                        rs.getInt("stok"),
                        rs.getString("satuan")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat mencari produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtIdProduk.setText("");
        txtNamaProduk.setText("");
        txtKategoriProduk.setText("");
        txtHargaProduk.setText("");
        txtStokProduk.setText("");
        txtSatuanProduk.setText("");
    }
}
