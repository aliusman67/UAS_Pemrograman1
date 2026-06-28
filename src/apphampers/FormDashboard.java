package apphampers;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FormDashboard extends JFrame {
    private JLabel lblTotalProduk;
    private JLabel lblTotalHampers;
    private JLabel lblTotalPelanggan;
    private JLabel lblTotalPesanan;
    private JButton btnProduk;
    private JButton btnHampers;
    private JButton btnPelanggan;
    private JButton btnTransaksi;
    private JButton btnPembayaran;
    private JButton btnLaporan;
    private JButton btnKeuangan;
    private JButton btnMutasiBarang;
    private JButton btnPengiriman;
    private JButton btnPengguna;
    private JButton btnLogout;

    public FormDashboard(String namaLengkap, String role) {
        setTitle("Dashboard - Aplikasi Penjualan Hampers");
        setSize(600, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Dashboard Aplikasi Penjualan Hampers");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 420, 30);
        add(lblTitle);

        JLabel lblPenggunaAktif = new JLabel("Masuk sebagai: " + namaLengkap + " (" + role + ")");
        lblPenggunaAktif.setBounds(20, 40, 540, 25);
        add(lblPenggunaAktif);

        JPanel panelSummary = new JPanel(new GridLayout(2, 2, 10, 10));
        panelSummary.setBounds(20, 70, 540, 110);
        add(panelSummary);

        lblTotalProduk = new JLabel("Total Produk: 0");
        lblTotalProduk.setFont(new Font("Arial", Font.PLAIN, 14));
        panelSummary.add(lblTotalProduk);

        lblTotalHampers = new JLabel("Total Hampers: 0");
        lblTotalHampers.setFont(new Font("Arial", Font.PLAIN, 14));
        panelSummary.add(lblTotalHampers);

        lblTotalPelanggan = new JLabel("Total Pelanggan: 0");
        lblTotalPelanggan.setFont(new Font("Arial", Font.PLAIN, 14));
        panelSummary.add(lblTotalPelanggan);

        lblTotalPesanan = new JLabel("Total Pesanan: 0");
        lblTotalPesanan.setFont(new Font("Arial", Font.PLAIN, 14));
        panelSummary.add(lblTotalPesanan);

        btnProduk = new JButton("Data Produk");
        btnProduk.setBounds(20, 200, 160, 40);
        add(btnProduk);

        btnHampers = new JButton("Data Hampers");
        btnHampers.setBounds(200, 200, 160, 40);
        add(btnHampers);

        btnPelanggan = new JButton("Data Pelanggan");
        btnPelanggan.setBounds(380, 200, 160, 40);
        add(btnPelanggan);

        btnTransaksi = new JButton("Transaksi");
        btnTransaksi.setBounds(20, 260, 160, 40);
        add(btnTransaksi);

        btnPembayaran = new JButton("Pembayaran");
        btnPembayaran.setBounds(200, 260, 160, 40);
        add(btnPembayaran);

        btnLaporan = new JButton("Laporan");
        btnLaporan.setBounds(380, 260, 160, 40);
        add(btnLaporan);

        btnKeuangan = new JButton("Keuangan");
        btnKeuangan.setBounds(20, 315, 160, 40);
        add(btnKeuangan);

        btnMutasiBarang = new JButton("Mutasi Stok");
        btnMutasiBarang.setBounds(200, 315, 160, 40);
        add(btnMutasiBarang);

        btnPengiriman = new JButton("Pengiriman");
        btnPengiriman.setBounds(380, 315, 160, 40);
        add(btnPengiriman);

        btnPengguna = new JButton("Tambah Pengguna");
        btnPengguna.setBounds(20, 370, 520, 35);
        btnPengguna.setVisible("admin".equalsIgnoreCase(role));
        add(btnPengguna);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(20, 420, 520, 30);
        add(btnLogout);

        btnProduk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormProduk().setVisible(true);
            }
        });

        btnHampers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormHampers().setVisible(true);
            }
        });

        btnPelanggan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormPelanggan().setVisible(true);
            }
        });

        btnTransaksi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormTransaksi().setVisible(true);
            }
        });

        btnPembayaran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormPembayaran().setVisible(true);
            }
        });

        btnLaporan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormLaporan().setVisible(true);
            }
        });

        btnKeuangan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormKeuangan().setVisible(true);
            }
        });

        btnMutasiBarang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormMutasiBarang().setVisible(true);
            }
        });

        btnPengiriman.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormPengiriman().setVisible(true);
            }
        });

        btnPengguna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormPengguna().setVisible(true);
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormLogin().setVisible(true);
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                loadSummary();
            }
        });

        loadSummary();
    }

    private void loadSummary() {
        lblTotalProduk.setText("Total Produk: " + getCount("SELECT COUNT(*) FROM produk"));
        lblTotalHampers.setText("Total Hampers: " + getCount("SELECT COUNT(*) FROM hampers"));
        lblTotalPelanggan.setText("Total Pelanggan: " + getCount("SELECT COUNT(*) FROM pelanggan"));
        lblTotalPesanan.setText("Total Pesanan: " + getCount("SELECT COUNT(*) FROM pesanan"));
    }

    private int getCount(String sql) {
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saat menghitung data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
}
