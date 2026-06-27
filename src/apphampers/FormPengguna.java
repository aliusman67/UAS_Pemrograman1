package apphampers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class FormPengguna extends JFrame {
    private final JTextField txtNamaLengkap;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JPasswordField txtKonfirmasiPassword;
    private final JComboBox<String> cmbRole;
    private final JCheckBox chkTampilkanPassword;
    private final JButton btnSimpan;
    private final JButton btnReset;
    private final JButton btnTutup;

    public FormPengguna() {
        setTitle("Tambah Pengguna");
        setSize(500, 390);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel lblTitle = new JLabel("Form Tambah Pengguna");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(25, 15, 300, 30);
        add(lblTitle);

        JLabel lblNamaLengkap = new JLabel("Nama Lengkap:");
        lblNamaLengkap.setBounds(25, 65, 140, 25);
        add(lblNamaLengkap);

        txtNamaLengkap = new JTextField();
        txtNamaLengkap.setBounds(175, 65, 275, 25);
        add(txtNamaLengkap);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(25, 100, 140, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(175, 100, 275, 25);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(25, 135, 140, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(175, 135, 275, 25);
        add(txtPassword);

        JLabel lblKonfirmasi = new JLabel("Konfirmasi Password:");
        lblKonfirmasi.setBounds(25, 170, 145, 25);
        add(lblKonfirmasi);

        txtKonfirmasiPassword = new JPasswordField();
        txtKonfirmasiPassword.setBounds(175, 170, 275, 25);
        add(txtKonfirmasiPassword);

        chkTampilkanPassword = new JCheckBox("Tampilkan password");
        chkTampilkanPassword.setBounds(171, 200, 200, 25);
        add(chkTampilkanPassword);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setBounds(25, 235, 140, 25);
        add(lblRole);

        cmbRole = new JComboBox<>(new String[]{"kasir", "admin"});
        cmbRole.setBounds(175, 235, 160, 25);
        add(cmbRole);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(50, 295, 110, 32);
        add(btnSimpan);

        btnReset = new JButton("Reset");
        btnReset.setBounds(190, 295, 110, 32);
        add(btnReset);

        btnTutup = new JButton("Tutup");
        btnTutup.setBounds(330, 295, 110, 32);
        add(btnTutup);

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanPengguna();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        btnTutup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        chkTampilkanPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char echoChar = chkTampilkanPassword.isSelected() ? (char) 0 : '\u2022';
                txtPassword.setEchoChar(echoChar);
                txtKonfirmasiPassword.setEchoChar(echoChar);
            }
        });

        getRootPane().setDefaultButton(btnSimpan);
    }

    private void simpanPengguna() {
        String namaLengkap = txtNamaLengkap.getText().trim();
        String username = txtUsername.getText().trim();
        String role = (String) cmbRole.getSelectedItem();
        char[] password = txtPassword.getPassword();
        char[] konfirmasiPassword = txtKonfirmasiPassword.getPassword();

        try {
            if (namaLengkap.isEmpty() || username.isEmpty()
                    || password.length == 0 || konfirmasiPassword.length == 0) {
                tampilkanPeringatan("Semua data pengguna harus diisi");
                return;
            }
            if (namaLengkap.length() > 100) {
                tampilkanPeringatan("Nama lengkap maksimal 100 karakter");
                return;
            }
            if (username.length() > 50 || !username.matches("[A-Za-z0-9._-]+")) {
                tampilkanPeringatan("Username maksimal 50 karakter dan hanya boleh berisi huruf, angka, titik, garis bawah, atau tanda minus");
                return;
            }
            if (password.length < 8) {
                tampilkanPeringatan("Password minimal 8 karakter");
                return;
            }
            if (!Arrays.equals(password, konfirmasiPassword)) {
                tampilkanPeringatan("Konfirmasi password tidak sama");
                return;
            }

            String passwordHash = PasswordHasher.hash(password);
            String sql = "INSERT INTO users (nama_lengkap, username, password, role) VALUES (?, ?, ?, ?)";
            try (Connection conn = Koneksi.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, namaLengkap);
                ps.setString(2, username);
                ps.setString(3, passwordHash);
                ps.setString(4, role);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    "Pengguna berhasil ditambahkan. Password tersimpan dengan aman.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } catch (SQLIntegrityConstraintViolationException ex) {
            tampilkanPeringatan("Username sudah digunakan. Silakan pilih username lain.");
            txtUsername.requestFocusInWindow();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saat menyimpan pengguna: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(konfirmasiPassword, '\0');
        }
    }

    private void tampilkanPeringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void resetForm() {
        txtNamaLengkap.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtKonfirmasiPassword.setText("");
        cmbRole.setSelectedIndex(0);
        chkTampilkanPassword.setSelected(false);
        txtPassword.setEchoChar('\u2022');
        txtKonfirmasiPassword.setEchoChar('\u2022');
        txtNamaLengkap.requestFocusInWindow();
    }
}
