/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;


/**
 *
 * @author ghroot67
 */
public class koneksi {
    
    private static Connection koneksi;

    public static Connection getConnection() {
        try {
            if (koneksi == null || koneksi.isClosed()) {

                String url = "jdbc:mysql://localhost:3306/db_hampers";
                String user = "root";
                String pass = "root";

                koneksi = DriverManager.getConnection(url, user, pass);
            }

            return koneksi;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Koneksi Gagal : " + e.getMessage()
            );
            return null;
        }
    }
    
}
