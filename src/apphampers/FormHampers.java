package apphampers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FormHampers extends JFrame {
    private JTextField txtIdHampers;
    private JTextField txtNamaHampers;
    private JTextField txtKategoriHampers;
    private JTextField txtHargaHampers;
    private JTextField txtStokHampers;
    private JTextArea txtDeskripsiHampers;
    private JTextField txtCariHampers;
    private JTable tblHampers;
    private DefaultTableModel model;
    private JButton btnSimpan;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnReset;
    private JButton btnCari;

    public FormHampers() {
        setTitle("Data Hampers");
        setSize(880, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Form Data Hampers");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        JLabel lblIdHampers = new JLabel("ID Hampers:");
        lblIdHampers.setBounds(20, 50, 100, 25);
        add(lblIdHampers);

        txtIdHampers = new JTextField();
        txtIdHampers.setBounds(130, 50, 160, 25);
        txtIdHampers.setEditable(false);
        add(txtIdHampers);

        JLabel lblNamaHampers = new JLabel("Nama Hampers:");
        lblNamaHampers.setBounds(20, 85, 100, 25);
        add(lblNamaHampers);

        txtNamaHampers = new JTextField();
        txtNamaHampers.setBounds(130, 85, 320, 25);
        add(txtNamaHampers);

        JLabel lblKategoriHampers = new JLabel("Kategori:");
        lblKategoriHampers.setBounds(20, 120, 100, 25);
        add(lblKategoriHampers);

        txtKategoriHampers = new JTextField();
        txtKategoriHampers.setBounds(130, 120, 200, 25);
        add(txtKategoriHampers);

        JLabel lblHargaHampers = new JLabel("Harga:");
        lblHargaHampers.setBounds(20, 155, 100, 25);
        add(lblHargaHampers);

        txtHargaHampers = new JTextField();
        txtHargaHampers.setBounds(130, 155, 200, 25);
        add(txtHargaHampers);

        JLabel lblStokHampers = new JLabel("Stok:");
        lblStokHampers.setBounds(20, 190, 100, 25);
        add(lblStokHampers);

        txtStokHampers = new JTextField();
        txtStokHampers.setBounds(130, 190, 200, 25);
        add(txtStokHampers);

        JLabel lblDeskripsiHampers = new JLabel("Deskripsi:");
        lblDeskripsiHampers.setBounds(20, 225, 100, 25);
        add(lblDeskripsiHampers);

        txtDeskripsiHampers = new JTextArea();
        JScrollPane scrollDesc = new JScrollPane(txtDeskripsiHampers);
        scrollDesc.setBounds(130, 225, 320, 100);
        add(scrollDesc);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(20, 350, 100, 30);
        add(btnSimpan);

        btnUbah = new JButton("Ubah");
        btnUbah.setBounds(130, 350, 100, 30);
        add(btnUbah);

        btnHapus = new JButton("Hapus");
        btnHapus.setBounds(240, 350, 100, 30);
        add(btnHapus);

        btnReset = new JButton("Reset");
        btnReset.setBounds(350, 350, 100, 30);
        add(btnReset);

        txtCariHampers = new JTextField();
        txtCariHampers.setBounds(520, 50, 240, 25);
        add(txtCariHampers);

        btnCari = new JButton("Cari");
        btnCari.setBounds(760, 50, 80, 25);
        add(btnCari);

        model = new DefaultTableModel(new String[]{"ID Hampers", "Nama Hampers", "Kategori", "Harga", "Stok", "Deskripsi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHampers = new JTable(model);
        JScrollPane scroll = new JScrollPane(tblHampers);
        scroll.setBounds(20, 400, 820, 120);
        add(scroll);

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveHampers();
            }
        });

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateHampers();
            }
        });

        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteHampers();
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
                searchHampers();
            }
        });

        tblHampers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblHampers.getSelectedRow();
                if (row >= 0) {
                    txtIdHampers.setText(model.getValueAt(row, 0).toString());
                    txtNamaHampers.setText(model.getValueAt(row, 1).toString());
                    txtKategoriHampers.setText(model.getValueAt(row, 2).toString());
                    txtHargaHampers.setText(model.getValueAt(row, 3).toString());
                    txtStokHampers.setText(model.getValueAt(row, 4).toString());
                    txtDeskripsiHampers.setText(model.getValueAt(row, 5).toString());
                }
            }
        });

        loadHampers();
    }

    private void loadHampers() {
        model.setRowCount(0);
        String sql = "SELECT * FROM hampers";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_hampers"),
                    rs.getString("nama_hampers"),
                    rs.getString("kategori"),
                    rs.getDouble("harga"),
                    rs.getInt("stok"),
                    rs.getString("deskripsi")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data hampers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveHampers() {
        String nama = txtNamaHampers.getText().trim();
        String kategori = txtKategoriHampers.getText().trim();
        String hargaText = txtHargaHampers.getText().trim();
        String stokText = txtStokHampers.getText().trim();
        String deskripsi = txtDeskripsiHampers.getText().trim();

        if (nama.isEmpty() || kategori.isEmpty() || hargaText.isEmpty() || stokText.isEmpty() || deskripsi.isEmpty()) {
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

        String sql = "INSERT INTO hampers (nama_hampers, kategori, harga, stok, deskripsi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, kategori);
            ps.setDouble(3, harga);
            ps.setInt(4, stok);
            ps.setString(5, deskripsi);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data hampers berhasil disimpan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadHampers();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan hampers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHampers() {
        String idText = txtIdHampers.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data hampers terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = txtNamaHampers.getText().trim();
        String kategori = txtKategoriHampers.getText().trim();
        String hargaText = txtHargaHampers.getText().trim();
        String stokText = txtStokHampers.getText().trim();
        String deskripsi = txtDeskripsiHampers.getText().trim();

        if (nama.isEmpty() || kategori.isEmpty() || hargaText.isEmpty() || stokText.isEmpty() || deskripsi.isEmpty()) {
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

        String sql = "UPDATE hampers SET nama_hampers = ?, kategori = ?, harga = ?, stok = ?, deskripsi = ? WHERE id_hampers = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, kategori);
            ps.setDouble(3, harga);
            ps.setInt(4, stok);
            ps.setString(5, deskripsi);
            ps.setInt(6, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data hampers berhasil diubah", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadHampers();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat mengubah hampers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteHampers() {
        String idText = txtIdHampers.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data hampers terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID harus angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM hampers WHERE id_hampers = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data hampers berhasil dihapus", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadHampers();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menghapus hampers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchHampers() {
        String keyword = txtCariHampers.getText().trim();
        model.setRowCount(0);
        String sql = "SELECT * FROM hampers WHERE nama_hampers LIKE ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_hampers"),
                        rs.getString("nama_hampers"),
                        rs.getString("kategori"),
                        rs.getDouble("harga"),
                        rs.getInt("stok"),
                        rs.getString("deskripsi")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat mencari hampers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtIdHampers.setText("");
        txtNamaHampers.setText("");
        txtKategoriHampers.setText("");
        txtHargaHampers.setText("");
        txtStokHampers.setText("");
        txtDeskripsiHampers.setText("");
    }
}
