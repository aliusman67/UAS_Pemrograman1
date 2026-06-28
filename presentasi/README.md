# Panduan Menjalankan Presentasi Hampers

Presentasi ini dibuat sebagai halaman web interaktif menggunakan HTML, CSS,
dan JavaScript. Tidak ada package atau aplikasi khusus yang perlu diinstal.

## Persyaratan

- Browser modern, seperti Google Chrome, Microsoft Edge, atau Mozilla Firefox.
- Python 3 hanya diperlukan jika presentasi ingin dijalankan melalui server
  lokal (direkomendasikan).

## Cara cepat

Buka file [`index.html`](index.html) langsung menggunakan browser. Cara ini
cocok untuk melihat presentasi tanpa menjalankan perintah apa pun.

Pada Linux, file juga dapat dibuka dari folder utama proyek dengan perintah:

```bash
xdg-open presentasi/index.html
```

## Menjalankan melalui server lokal

Server lokal direkomendasikan agar seluruh gambar dan fitur browser dimuat
secara konsisten.

1. Buka terminal di folder utama proyek `Hampers_Uas`.
2. Jalankan salah satu perintah berikut.

   Linux/macOS:

   ```bash
   python3 -m http.server 8000
   ```

   Windows:

   ```powershell
   py -m http.server 8000
   ```

3. Buka alamat berikut di browser:

   <http://localhost:8000/presentasi/>

4. Untuk menghentikan server, kembali ke terminal lalu tekan `Ctrl+C`.

> Jalankan server dari folder utama proyek, bukan dari folder `presentasi`,
> karena beberapa gambar berada di folder `assets` pada level proyek.

## Kontrol presentasi

| Tombol/aksi | Fungsi |
| --- | --- |
| `→`, `↓`, `Page Down`, atau `Spasi` | Slide berikutnya |
| `←`, `↑`, atau `Page Up` | Slide sebelumnya |
| `Home` | Kembali ke slide pertama |
| `End` | Lompat ke slide terakhir |
| `N` | Buka/tutup catatan pembicara |
| `F` | Buka/tutup mode layar penuh |
| `Esc` | Tutup menu atau catatan |
| `?` | Tampilkan bantuan singkat |
| Usap kiri/kanan | Navigasi pada layar sentuh |

Daftar slide dapat dibuka melalui tombol menu di kiri atas. Tombol navigasi di
bagian bawah juga dapat digunakan dengan mouse.

## Menyimpan sebagai PDF

1. Buka presentasi di browser.
2. Tekan `Ctrl+P` (`Cmd+P` pada macOS).
3. Pilih **Save as PDF** atau **Simpan sebagai PDF**.
4. Gunakan orientasi **Landscape** dan aktifkan **Background graphics** agar
   warna serta desain slide ikut tercetak.
5. Klik **Save**.

## Mengubah isi presentasi

- Konten dan catatan pembicara: [`index.html`](index.html)
- Tampilan dan tata letak: [`style.css`](style.css)
- Navigasi dan interaksi: [`script.js`](script.js)
- Gambar aplikasi: folder [`../assets`](../assets)

Setelah menyimpan perubahan, muat ulang halaman dengan `Ctrl+R`. Gunakan
`Ctrl+Shift+R` jika browser masih menampilkan versi lama.

## Troubleshooting

### Gambar tidak tampil

Pastikan server dijalankan dari folder utama `Hampers_Uas` dan folder `assets`
tidak dipindahkan atau diubah namanya.

### Port 8000 sedang digunakan

Gunakan port lain, misalnya:

```bash
python3 -m http.server 8080
```

Kemudian buka <http://localhost:8080/presentasi/>.

### Mode layar penuh tidak aktif

Izinkan fullscreen pada browser, lalu tekan `F` atau klik ikon layar penuh di
kanan atas.
