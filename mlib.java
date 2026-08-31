package musiclib;

import java.sql.*;
import java.util.Scanner;

public class mlib {
   
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/mlib_manager";
    private static final String USER = "c##rama";
    private static final String PASS = "rama";

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found. Add ojdbc jar to classpath.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database 26ai successfully.");
            boolean running = true;

            while (running) {
                printMenu();
                try {
                    int choice = readInt();
                    switch (choice) {
                        case 1 -> addSong(conn);
                        case 2 -> modifyEntry(conn);
                        case 3 -> removeEntry(conn);
                        case 4 -> searchMenu(conn);
                        case 5 -> {
                            running = false;
                            System.out.println("Exiting application...");
                        }
                        default -> System.out.println("Invalid choice. Please select 1-5.");
                    }
                } catch (SQLException e) {
                    // Moving this inside the loop prevents the app from exiting on a single DB error
                    System.err.println("Database Error: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n--- MUSIC LIBRARY MANAGER ---");
        System.out.println("1. Add Song (Artist/Album are created or updated automatically)");
        System.out.println("2. Modify Entry");
        System.out.println("3. Remove Entry");
        System.out.println("4. Search Library");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    // Helper method to safely read integers and prevent NumberFormatException crashes
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    // 1. ADD SONG
 
    private static void addSong(Connection conn) throws SQLException {
        System.out.println("\n--- Add Song ---");

        System.out.print("Artist ID/Key: ");
        String artistKey = scanner.nextLine();
        System.out.print("Artist Name: ");
        String artistName = scanner.nextLine();
        System.out.print("Country: ");
        String country = scanner.nextLine();

        System.out.print("Album ID/Key: ");
        String albumKey = scanner.nextLine();
        System.out.print("Release Year: ");
        int year = readInt();

        System.out.print("Song ID/Key: ");
        String songKey = scanner.nextLine();
        System.out.print("Duration (seconds): ");
        int duration = readInt();
        System.out.print("Track Number: ");
        int track = readInt();

        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            upsertArtist(conn, artistKey, artistName, country);
            upsertAlbum(conn, albumKey, year, artistKey);
            insertSong(conn, songKey, duration, track, albumKey, artistKey);

            conn.commit();
            System.out.println("Song added successfully (Artist/Album ensured)!");
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Failed to add song, no changes were saved: " + e.getMessage());
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    private static void upsertArtist(Connection conn, String artistKey, String name, String country) throws SQLException {
        String sql = """
            MERGE INTO artists a
            USING (SELECT ? AS artist FROM dual) src
            ON (a.artist = src.artist)
            WHEN MATCHED THEN UPDATE SET a.name = ?, a.country = ?
            WHEN NOT MATCHED THEN INSERT (artist, name, country) VALUES (src.artist, ?, ?)
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, artistKey);
            stmt.setString(2, name);
            stmt.setString(3, country);
            stmt.setString(4, name);
            stmt.setString(5, country);
            stmt.executeUpdate();
        }
    }


    private static void upsertAlbum(Connection conn, String albumKey, int year, String artistKey) throws SQLException {
        String sql = """
            MERGE INTO albums a
            USING (SELECT ? AS album FROM dual) src
            ON (a.album = src.album)
            WHEN MATCHED THEN UPDATE SET a.release_year = ?, a.artist = ?
            WHEN NOT MATCHED THEN INSERT (album, release_year, artist) VALUES (src.album, ?, ?)
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, albumKey);
            stmt.setInt(2, year);
            stmt.setString(3, artistKey);
            stmt.setInt(4, year);
            stmt.setString(5, artistKey);
            stmt.executeUpdate();
        }
    }

    private static void insertSong(Connection conn, String song, int duration, int track, String album, String artist) throws SQLException {
        String sql = "INSERT INTO songs (song, duration_seconds, track_number, album, artist) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, song);
            stmt.setInt(2, duration);
            stmt.setInt(3, track);
            stmt.setString(4, album);
            stmt.setString(5, artist);
            stmt.executeUpdate();
        }
    }

    // 2. MODIFY ENTRIES
    private static void modifyEntry(Connection conn) throws SQLException {
        System.out.println("\nModify: 1. Artist Country | 2. Album Year | 3. Song Duration");
        int choice = readInt();

        if (choice == 1) {
            System.out.print("Enter Artist Key: ");
            String artist = scanner.nextLine();
            System.out.print("Enter New Country: ");
            String country = scanner.nextLine();

            String sql = "UPDATE artists SET country = ? WHERE artist = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, country);
                stmt.setString(2, artist);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Artist updated!" : "Artist not found.");
            }
        } else if (choice == 2) {
            System.out.print("Enter Album Key: ");
            String album = scanner.nextLine();
            System.out.print("Enter New Release Year: ");
            int year = readInt();

            String sql = "UPDATE albums SET release_year = ? WHERE album = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, year);
                stmt.setString(2, album);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Album updated!" : "Album not found.");
            }
        } else if (choice == 3) {
            System.out.print("Enter Song Key: ");
            String song = scanner.nextLine();
            System.out.print("Enter New Duration (seconds): ");
            int duration = readInt();

            String sql = "UPDATE songs SET duration_seconds = ? WHERE song = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, duration);
                stmt.setString(2, song);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Song updated!" : "Song not found.");
            }
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // 3. REMOVE ENTRIES
    private static void removeEntry(Connection conn) throws SQLException {
        System.out.println("\nRemove: 1. Artist | 2. Album | 3. Song");
        int choice = readInt();

        String table = switch (choice) {
            case 1 -> "artists";
            case 2 -> "albums";
            case 3 -> "songs";
            default -> null;
        };

        if (table == null) {
            System.out.println("Invalid selection.");
            return;
        }

        String keyColumn = switch (choice) {
            case 1 -> "artist";
            case 2 -> "album";
            case 3 -> "song";
            default -> "";
        };

        System.out.print("Enter key to delete: ");
        String key = scanner.nextLine();

        String sql = "DELETE FROM " + table + " WHERE " + keyColumn + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Record deleted successfully!" : "Record not found.");
        }
    }

    // 4. SEARCH OPTIONS
    private static void searchMenu(Connection conn) throws SQLException {
        System.out.println("\nSearch by: 1. Song Keyword | 2. Songs by Artist | 3. List All Songs");
        int choice = readInt();

        if (choice == 1) {
            System.out.print("Enter search term for song: ");
            String term = scanner.nextLine();
            String sql = """
                SELECT s.song, s.duration_seconds, s.track_number, s.album, a.name AS artist_name
                FROM songs s
                LEFT JOIN artists a ON s.artist = a.artist
                WHERE LOWER(s.song) LIKE LOWER(?)
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + term + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    printSongResults(rs);
                }
            }
        } else if (choice == 2) {
            System.out.print("Enter Artist Key or Name: ");
            String term = scanner.nextLine();
            String sql = """
                SELECT s.song, s.duration_seconds, s.track_number, s.album, a.name AS artist_name
                FROM songs s
                JOIN artists a ON s.artist = a.artist
                WHERE LOWER(a.artist) = LOWER(?) OR LOWER(a.name) LIKE LOWER(?)
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, term);
                stmt.setString(2, "%" + term + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    printSongResults(rs);
                }
            }
        } else if (choice == 3) {
            String sql = """
                SELECT s.song, s.duration_seconds, s.track_number, s.album, a.name AS artist_name
                FROM songs s
                LEFT JOIN artists a ON s.artist = a.artist
            """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                printSongResults(rs);
            }
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private static void printSongResults(ResultSet rs) throws SQLException {
        System.out.printf("\n%-25s %-25s %-20s %-10s %-10s\n", "Song", "Artist Name", "Album", "Track", "Duration(s)");
        System.out.println("-----------------------------------------------------------------------------------------");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-25s %-25s %-20s %-10d %-10d\n",
                    rs.getString("song"),
                    rs.getString("artist_name") != null ? rs.getString("artist_name") : "Unknown",
                    rs.getString("album"),
                    rs.getInt("track_number"),
                    rs.getInt("duration_seconds"));
        }
        if (!found) {
            System.out.println("No matching records found.");
        }
    }
}
