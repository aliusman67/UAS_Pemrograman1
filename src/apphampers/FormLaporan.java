package apphampers;

import java.awt.Font;
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

public class FormLaporan extends JFrame {
    private JTextField txtTanggalAwal;
    private JTextField txtTanggalAkhir;
    private JButton btnTampilkan;
    private JButton btnCetak;
    private JTable tblLaporan;
    private DefaultTableModel model;
    private JLabel lblTotalPendapatan;

    public FormLaporan() {
        setTitle("Laporan Transaksi");
        setSize(1020, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Form Laporan Transaksi");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 320, 30);
        add(lblTitle);

        JLabel lblTanggalAwal = new JLabel("Tanggal Awal (yyyy-MM-dd):");
        lblTanggalAwal.setBounds(20, 55, 180, 25);
        add(lblTanggalAwal);

        txtTanggalAwal = new JTextField();
        txtTanggalAwal.setBounds(200, 55, 170, 25);
        add(txtTanggalAwal);

        JLabel lblTanggalAkhir = new JLabel("Tanggal Akhir (yyyy-MM-dd):");
        lblTanggalAkhir.setBounds(390, 55, 180, 25);
        add(lblTanggalAkhir);

        txtTanggalAkhir = new JTextField();
        txtTanggalAkhir.setBounds(570, 55, 170, 25);
        add(txtTanggalAkhir);

        btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.setBounds(760, 55, 110, 25);
        add(btnTampilkan);

        btnCetak = new JButton("Cetak");
        btnCetak.setBounds(880, 55, 100, 25);
        add(btnCetak);

        model = new DefaultTableModel(new String[]{
            "Kode Pesanan", "Nama Pelanggan", "Tanggal", "Detail Hampers", "Total", "Status Pembayaran"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblLaporan = new JTable(model);
        tblLaporan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblLaporan.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblLaporan.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblLaporan.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblLaporan.getColumnModel().getColumn(3).setPreferredWidth(300);
        tblLaporan.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblLaporan.getColumnModel().getColumn(5).setPreferredWidth(150);
        JScrollPane scroll = new JScrollPane(tblLaporan);
        scroll.setBounds(20, 100, 960, 390);
        add(scroll);

        lblTotalPendapatan = new JLabel("Total Pendapatan (Lunas): 0.00");
        lblTotalPendapatan.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalPendapatan.setBounds(20, 505, 420, 25);
        add(lblTotalPendapatan);

        btnTampilkan.addActionListener(e -> showReport());
        btnCetak.addActionListener(e -> printReport());

        showReport();
    }

    private void showReport() {
        String tanggalAwal = txtTanggalAwal.getText().trim();
        String tanggalAkhir = txtTanggalAkhir.getText().trim();
        boolean pakaiFilter = !tanggalAwal.isEmpty() || !tanggalAkhir.isEmpty();

        if (pakaiFilter && (tanggalAwal.isEmpty() || tanggalAkhir.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Tanggal awal dan tanggal akhir harus diisi bersama-sama",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pakaiFilter) {
            try {
                java.sql.Date awal = java.sql.Date.valueOf(tanggalAwal);
                java.sql.Date akhir = java.sql.Date.valueOf(tanggalAkhir);
                if (awal.after(akhir)) {
                    JOptionPane.showMessageDialog(this, "Tanggal awal tidak boleh melewati tanggal akhir",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String sql = "SELECT pes.kode_pesanan, pel.nama_pelanggan, pes.tanggal_pesanan, "
                + "COALESCE(GROUP_CONCAT(CONCAT(h.nama_hampers, ' x', dp.jumlah) "
                + "ORDER BY h.nama_hampers SEPARATOR ', '), '-') AS detail_hampers, "
                + "pes.total, COALESCE((SELECT pay.status_pembayaran FROM pembayaran pay "
                + "WHERE pay.id_pesanan = pes.id_pesanan ORDER BY pay.id_pembayaran DESC LIMIT 1), "
                + "'Belum Lunas') AS status_pembayaran "
                + "FROM pesanan pes "
                + "JOIN pelanggan pel ON pes.id_pelanggan = pel.id_pelanggan "
                + "LEFT JOIN detail_pesanan dp ON pes.id_pesanan = dp.id_pesanan "
                + "LEFT JOIN hampers h ON dp.id_hampers = h.id_hampers ";
        if (pakaiFilter) {
            sql += "WHERE pes.tanggal_pesanan BETWEEN ? AND ? ";
        }
        sql += "GROUP BY pes.id_pesanan, pes.kode_pesanan, pel.nama_pelanggan, "
                + "pes.tanggal_pesanan, pes.total ORDER BY pes.tanggal_pesanan, pes.id_pesanan";

        model.setRowCount(0);
        double totalPendapatan = 0;
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (pakaiFilter) {
                ps.setDate(1, java.sql.Date.valueOf(tanggalAwal));
                ps.setDate(2, java.sql.Date.valueOf(tanggalAkhir));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status_pembayaran");
                    double total = rs.getDouble("total");
                    model.addRow(new Object[]{
                        rs.getString("kode_pesanan"),
                        rs.getString("nama_pelanggan"),
                        rs.getDate("tanggal_pesanan"),
                        rs.getString("detail_hampers"),
                        total,
                        status
                    });
                    if ("Lunas".equalsIgnoreCase(status)) {
                        totalPendapatan += total;
                    }
                }
            }
            lblTotalPendapatan.setText(String.format("Total Pendapatan (Lunas): %.2f", totalPendapatan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat laporan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReport() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data laporan untuk dicetak",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("LAPORAN TRANSAKSI HAMPERS\n");
        report.append("Periode: ")
                .append(txtTanggalAwal.getText().trim().isEmpty() ? "Semua tanggal" : txtTanggalAwal.getText().trim())
                .append(txtTanggalAkhir.getText().trim().isEmpty() ? "" : " s.d. " + txtTanggalAkhir.getText().trim())
                .append("\n\n");
        for (int i = 0; i < model.getRowCount(); i++) {
            report.append(model.getValueAt(i, 0)).append(" | ")
                    .append(model.getValueAt(i, 1)).append(" | ")
                    .append(model.getValueAt(i, 2)).append("\n  ")
                    .append(model.getValueAt(i, 3)).append("\n  Total: ")
                    .append(model.getValueAt(i, 4)).append(" | ")
                    .append(model.getValueAt(i, 5)).append("\n\n");
        }
        report.append(lblTotalPendapatan.getText());

        JTextArea area = new JTextArea(report.toString(), 24, 72);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Cetak Laporan",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
