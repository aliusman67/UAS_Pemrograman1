package apphampers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class FormLogin extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnReset;

    public FormLogin() {
        setTitle("Aplikasi Penjualan Hampers");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Aplikasi Penjualan Hampers");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBounds(50, 15, 280, 30);
        add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(40, 65, 100, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(140, 65, 180, 25);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(40, 100, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 100, 180, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(70, 150, 100, 30);
        add(btnLogin);

        btnReset = new JButton("Reset");
        btnReset.setBounds(200, 150, 100, 30);
        add(btnReset);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsername.setText("");
                txtPassword.setText("");
                txtUsername.requestFocusInWindow();
            }
        });

        getRootPane().setDefaultButton(btnLogin);
    }

    private void login() {
        String username = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Username dan password harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
            Arrays.fill(password, '\0');
            return;
        }

        String sql = "SELECT id_user, nama_lengkap, role, password FROM users WHERE username = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && passwordCocok(conn, rs.getInt("id_user"), password, rs.getString("password"))) {
                    new FormDashboard(rs.getString("nama_lengkap"), rs.getString("role")).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau password salah", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                    txtPassword.setText("");
                    txtPassword.requestFocusInWindow();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private boolean passwordCocok(Connection conn, int idUser, char[] password, String passwordTersimpan)
            throws Exception {
        if (PasswordHasher.isHash(passwordTersimpan)) {
            return PasswordHasher.verify(password, passwordTersimpan);
        }

        // Kompatibilitas untuk database lama: setelah login berhasil, password
        // polos langsung diganti menjadi hash PBKDF2.
        char[] passwordLama = passwordTersimpan == null
                ? new char[0] : passwordTersimpan.toCharArray();
        boolean cocok = samaSecaraKonstan(password, passwordLama);
        Arrays.fill(passwordLama, '\0');

        if (cocok) {
            String sql = "UPDATE users SET password = ? WHERE id_user = ? AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, PasswordHasher.hash(password));
                ps.setInt(2, idUser);
                ps.setString(3, passwordTersimpan);
                ps.executeUpdate();
            }
        }
        return cocok;
    }

    private boolean samaSecaraKonstan(char[] nilaiPertama, char[] nilaiKedua) {
        int panjangMaksimal = Math.max(nilaiPertama.length, nilaiKedua.length);
        int perbedaan = nilaiPertama.length ^ nilaiKedua.length;
        for (int i = 0; i < panjangMaksimal; i++) {
            char karakterPertama = i < nilaiPertama.length ? nilaiPertama[i] : 0;
            char karakterKedua = i < nilaiKedua.length ? nilaiKedua[i] : 0;
            perbedaan |= karakterPertama ^ karakterKedua;
        }
        return perbedaan == 0;
    }
}
