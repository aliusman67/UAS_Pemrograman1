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

public class FormPelanggan extends JFrame {
    private JTextField txtIdPelanggan;
    private JTextField txtNamaPelanggan;
    private JTextField txtNoHp;
    private JTextField txtEmail;
    private JTextArea txtAlamat;
    private JTextField txtCariPelanggan;
    private JTable tblPelanggan;
    private DefaultTableModel model;
    private JButton btnSimpan;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnReset;
    private JButton btnCari;

    public FormPelanggan() {
        setTitle("Data Pelanggan");
        setSize(880, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Form Data Pelanggan");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        JLabel lblIdPelanggan = new JLabel("ID Pelanggan:");
        lblIdPelanggan.setBounds(20, 50, 110, 25);
        add(lblIdPelanggan);

        txtIdPelanggan = new JTextField();
        txtIdPelanggan.setBounds(140, 50, 160, 25);
        txtIdPelanggan.setEditable(false);
        add(txtIdPelanggan);

        JLabel lblNamaPelanggan = new JLabel("Nama Pelanggan:");
        lblNamaPelanggan.setBounds(20, 85, 110, 25);
        add(lblNamaPelanggan);

        txtNamaPelanggan = new JTextField();
        txtNamaPelanggan.setBounds(140, 85, 320, 25);
        add(txtNamaPelanggan);

        JLabel lblNoHp = new JLabel("No. HP:");
        lblNoHp.setBounds(20, 120, 110, 25);
        add(lblNoHp);

        txtNoHp = new JTextField();
        txtNoHp.setBounds(140, 120, 200, 25);
        add(txtNoHp);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(20, 155, 110, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(140, 155, 320, 25);
        add(txtEmail);

        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setBounds(20, 190, 110, 25);
        add(lblAlamat);

        txtAlamat = new JTextArea();
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        scrollAlamat.setBounds(140, 190, 320, 100);
        add(scrollAlamat);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(20, 310, 100, 30);
        add(btnSimpan);

        btnUbah = new JButton("Ubah");
        btnUbah.setBounds(130, 310, 100, 30);
        add(btnUbah);

        btnHapus = new JButton("Hapus");
        btnHapus.setBounds(240, 310, 100, 30);
        add(btnHapus);

        btnReset = new JButton("Reset");
        btnReset.setBounds(350, 310, 100, 30);
        add(btnReset);

        txtCariPelanggan = new JTextField();
        txtCariPelanggan.setBounds(520, 50, 240, 25);
        add(txtCariPelanggan);

        btnCari = new JButton("Cari");
        btnCari.setBounds(760, 50, 80, 25);
        add(btnCari);

        model = new DefaultTableModel(new String[]{"ID Pelanggan", "Nama", "No. HP", "Email", "Alamat"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPelanggan = new JTable(model);
        JScrollPane scroll = new JScrollPane(tblPelanggan);
        scroll.setBounds(20, 360, 820, 120);
        add(scroll);

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePelanggan();
            }
        });

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePelanggan();
            }
        });

        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePelanggan();
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
                searchPelanggan();
            }
        });

        tblPelanggan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblPelanggan.getSelectedRow();
                if (row >= 0) {
                    txtIdPelanggan.setText(model.getValueAt(row, 0).toString());
                    txtNamaPelanggan.setText(model.getValueAt(row, 1).toString());
                    txtNoHp.setText(model.getValueAt(row, 2).toString());
                    txtEmail.setText(model.getValueAt(row, 3).toString());
                    txtAlamat.setText(model.getValueAt(row, 4).toString());
                }
            }
        });

        loadPelanggan();
    }

    private void loadPelanggan() {
        model.setRowCount(0);
        String sql = "SELECT * FROM pelanggan";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("no_hp"),
                    rs.getString("email"),
                    rs.getString("alamat")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePelanggan() {
        String nama = txtNamaPelanggan.getText().trim();
        String noHp = txtNoHp.getText().trim();
        String email = txtEmail.getText().trim();
        String alamat = txtAlamat.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pelanggan harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO pelanggan (nama_pelanggan, no_hp, email, alamat) VALUES (?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, noHp);
            ps.setString(3, email);
            ps.setString(4, alamat);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil disimpan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadPelanggan();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePelanggan() {
        String idText = txtIdPelanggan.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pelanggan terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = txtNamaPelanggan.getText().trim();
        String noHp = txtNoHp.getText().trim();
        String email = txtEmail.getText().trim();
        String alamat = txtAlamat.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pelanggan harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID harus angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE pelanggan SET nama_pelanggan = ?, no_hp = ?, email = ?, alamat = ? WHERE id_pelanggan = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, noHp);
            ps.setString(3, email);
            ps.setString(4, alamat);
            ps.setInt(5, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diubah", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadPelanggan();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat mengubah pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePelanggan() {
        String idText = txtIdPelanggan.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pelanggan terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID harus angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM pelanggan WHERE id_pelanggan = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil dihapus", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadPelanggan();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menghapus pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPelanggan() {
        String keyword = txtCariPelanggan.getText().trim();
        model.setRowCount(0);
        String sql = "SELECT * FROM pelanggan WHERE nama_pelanggan LIKE ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_pelanggan"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("no_hp"),
                        rs.getString("email"),
                        rs.getString("alamat")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat mencari pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtIdPelanggan.setText("");
        txtNamaPelanggan.setText("");
        txtNoHp.setText("");
        txtEmail.setText("");
        txtAlamat.setText("");
    }
}
