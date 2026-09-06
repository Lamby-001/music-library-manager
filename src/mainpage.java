import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class mainpage extends JPanel {

	private static final long serialVersionUID = 1L;

	private CardLayout cardLayout;
	private JPanel cardPanel;

	// ---- Add Song fields ----
	private JTextField addArtistKeyField, addArtistNameField, addCountryField,
			addAlbumKeyField, addYearField, addSongKeyField, addDurationField, addTrackField;

	// ---- Remove fields ----
	private JComboBox<String> removeTypeCombo;
	private JTextField removeKeyField;

	// ---- Update fields ----
	private JComboBox<String> updateTableCombo;
	private JComboBox<String> updateColumnCombo;
	private JTextField updateWhereValueField;
	private JTextField updateSetValueField;

	private static final String[] ARTIST_COLUMNS = { "ARTIST", "NAME", "COUNTRY" };
	private static final String[] ALBUM_COLUMNS = { "ALBUM", "RELEASE_YEAR", "ARTIST" };
	private static final String[] SONG_COLUMNS = { "SONG", "DURATION_SECONDS", "TRACK_NUMBER", "ALBUM", "ARTIST" };

	// ---- View Tables ----
	private JTable artistsTable, albumsTable, songsTable;
	private DefaultTableModel artistsModel, albumsModel, songsModel;

	/**
	 * Create the panel.
	 */
	public mainpage() {
		setLayout(null);
		setPreferredSize(new Dimension(900, 600));

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.setBounds(0, 0, 900, 600);
		add(cardPanel);

		cardPanel.add(buildMenuPanel(), "MENU");
		cardPanel.add(buildAddSongPanel(), "ADD_SONG");
		cardPanel.add(buildRemovePanel(), "REMOVE");
		cardPanel.add(buildUpdatePanel(), "UPDATE");
		cardPanel.add(buildViewTablesPanel(), "VIEW_TABLES");

		cardLayout.show(cardPanel, "MENU");
	}

	// Reuses the JDBC connection that "login" already opened via window.conn
	private Connection getConn() {
		return window.conn;
	}

	// ================= MENU =================
	private JPanel buildMenuPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JButton addSongBtn = new JButton("1. Add Song");
		addSongBtn.setBounds(350, 150, 200, 40);
		addSongBtn.addActionListener(e -> cardLayout.show(cardPanel, "ADD_SONG"));

		JButton removeBtn = new JButton("2. Remove Entry");
		removeBtn.setBounds(350, 210, 200, 40);
		removeBtn.addActionListener(e -> cardLayout.show(cardPanel, "REMOVE"));

		JButton updateBtn = new JButton("3. Update Entry");
		updateBtn.setBounds(350, 270, 200, 40);
		updateBtn.addActionListener(e -> cardLayout.show(cardPanel, "UPDATE"));

		JButton viewTablesBtn = new JButton("4. View Tables");
		viewTablesBtn.setBounds(350, 330, 200, 40);
		viewTablesBtn.addActionListener(e -> {
			loadAllTables();
			cardLayout.show(cardPanel, "VIEW_TABLES");
		});

		panel.add(addSongBtn);
		panel.add(removeBtn);
		panel.add(updateBtn);
		panel.add(viewTablesBtn);

		return panel;
	}

	// ================= ADD SONG =================
	private JPanel buildAddSongPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel title = new JLabel("Add Song (Artist/Album are created or updated automatically)");
		title.setBounds(150, 10, 600, 20);
		panel.add(title);

		JLabel lblArtistKey = new JLabel("Artist ID/Key:");
		lblArtistKey.setBounds(50, 50, 120, 20);
		panel.add(lblArtistKey);
		addArtistKeyField = new JTextField();
		addArtistKeyField.setBounds(190, 50, 150, 25);
		panel.add(addArtistKeyField);

		JLabel lblArtistName = new JLabel("Artist Name:");
		lblArtistName.setBounds(50, 100, 120, 20);
		panel.add(lblArtistName);
		addArtistNameField = new JTextField();
		addArtistNameField.setBounds(190, 100, 150, 25);
		panel.add(addArtistNameField);

		JLabel lblCountry = new JLabel("Country:");
		lblCountry.setBounds(50, 150, 120, 20);
		panel.add(lblCountry);
		addCountryField = new JTextField();
		addCountryField.setBounds(190, 150, 150, 25);
		panel.add(addCountryField);

		JLabel lblAlbumKey = new JLabel("Album ID/Key:");
		lblAlbumKey.setBounds(50, 200, 120, 20);
		panel.add(lblAlbumKey);
		addAlbumKeyField = new JTextField();
		addAlbumKeyField.setBounds(190, 200, 150, 25);
		panel.add(addAlbumKeyField);

		JLabel lblYear = new JLabel("Release Year:");
		lblYear.setBounds(450, 50, 120, 20);
		panel.add(lblYear);
		addYearField = new JTextField();
		addYearField.setBounds(590, 50, 150, 25);
		panel.add(addYearField);

		JLabel lblSongKey = new JLabel("Song ID/Key:");
		lblSongKey.setBounds(450, 100, 120, 20);
		panel.add(lblSongKey);
		addSongKeyField = new JTextField();
		addSongKeyField.setBounds(590, 100, 150, 25);
		panel.add(addSongKeyField);

		JLabel lblDuration = new JLabel("Duration (seconds):");
		lblDuration.setBounds(450, 150, 120, 20);
		panel.add(lblDuration);
		addDurationField = new JTextField();
		addDurationField.setBounds(590, 150, 150, 25);
		panel.add(addDurationField);

		JLabel lblTrack = new JLabel("Track Number:");
		lblTrack.setBounds(450, 200, 120, 20);
		panel.add(lblTrack);
		addTrackField = new JTextField();
		addTrackField.setBounds(590, 200, 150, 25);
		panel.add(addTrackField);

		JButton submit = new JButton("Add Song");
		submit.setBounds(320, 280, 100, 30);
		submit.addActionListener(e -> handleAddSong());
		panel.add(submit);

		JButton back = new JButton("Back");
		back.setBounds(440, 280, 80, 30);
		back.addActionListener(e -> cardLayout.show(cardPanel, "MENU"));
		panel.add(back);

		return panel;
	}

	private void handleAddSong() {
		Connection conn = getConn();
		if (conn == null) {
			JOptionPane.showMessageDialog(this, "Not connected to the database.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String artistKey = addArtistKeyField.getText().trim();
		String artistName = addArtistNameField.getText().trim();
		String country = addCountryField.getText().trim();
		String albumKey = addAlbumKeyField.getText().trim();
		String songKey = addSongKeyField.getText().trim();

		int year, duration, track;
		try {
			year = Integer.parseInt(addYearField.getText().trim());
			duration = Integer.parseInt(addDurationField.getText().trim());
			track = Integer.parseInt(addTrackField.getText().trim());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Release Year, Duration and Track Number must be numbers.",
					"Invalid Input", JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean originalAutoCommit = true;
		try {
			originalAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			upsertArtist(conn, artistKey, artistName, country);
			upsertAlbum(conn, albumKey, year, artistKey);
			insertSong(conn, songKey, duration, track, albumKey, artistKey);

			conn.commit();
			JOptionPane.showMessageDialog(this, "Song added successfully (Artist/Album ensured)!");
			clearAddSongFields();
		} catch (SQLException ex) {
			try {
				conn.rollback();
			} catch (SQLException ignored) {
			}
			JOptionPane.showMessageDialog(this, "Failed to add song, no changes were saved: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				conn.setAutoCommit(originalAutoCommit);
			} catch (SQLException ignored) {
			}
		}
	}

	private void clearAddSongFields() {
		addArtistKeyField.setText("");
		addArtistNameField.setText("");
		addCountryField.setText("");
		addAlbumKeyField.setText("");
		addYearField.setText("");
		addSongKeyField.setText("");
		addDurationField.setText("");
		addTrackField.setText("");
	}

	private void upsertArtist(Connection conn, String artistKey, String name, String country) throws SQLException {
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

	private void upsertAlbum(Connection conn, String albumKey, int year, String artistKey) throws SQLException {
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

	private void insertSong(Connection conn, String song, int duration, int track, String album, String artist)
			throws SQLException {
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

	// ================= REMOVE ENTRY =================
	private JPanel buildRemovePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel lblRemove = new JLabel("Remove:");
		lblRemove.setBounds(50, 60, 100, 20);
		panel.add(lblRemove);

		removeTypeCombo = new JComboBox<>(new String[] { "Artist", "Album", "Song" });
		removeTypeCombo.setBounds(190, 60, 150, 25);
		panel.add(removeTypeCombo);

		JLabel lblKey = new JLabel("Key to delete:");
		lblKey.setBounds(50, 110, 120, 20);
		panel.add(lblKey);

		removeKeyField = new JTextField();
		removeKeyField.setBounds(190, 110, 150, 25);
		panel.add(removeKeyField);

		JButton submit = new JButton("Delete");
		submit.setBounds(190, 170, 100, 30);
		submit.addActionListener(e -> handleRemove());
		panel.add(submit);

		JButton back = new JButton("Back");
		back.setBounds(310, 170, 80, 30);
		back.addActionListener(e -> cardLayout.show(cardPanel, "MENU"));
		panel.add(back);

		return panel;
	}

	private void handleRemove() {
		Connection conn = getConn();
		if (conn == null) {
			JOptionPane.showMessageDialog(this, "Not connected to the database.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int choice = removeTypeCombo.getSelectedIndex();
		String table = switch (choice) {
			case 0 -> "artists";
			case 1 -> "albums";
			case 2 -> "songs";
			default -> null;
		};
		String keyColumn = switch (choice) {
			case 0 -> "artist";
			case 1 -> "album";
			case 2 -> "song";
			default -> "";
		};

		String key = removeKeyField.getText().trim();
		if (key.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a key to delete.", "Invalid Input",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String sql = "DELETE FROM " + table + " WHERE " + keyColumn + " = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, key);
			int rows = stmt.executeUpdate();
			JOptionPane.showMessageDialog(this, rows > 0 ? "Record deleted successfully!" : "Record not found.");
			removeKeyField.setText("");
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// ================= UPDATE ENTRY =================
	private JPanel buildUpdatePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel lblTable = new JLabel("Table:");
		lblTable.setBounds(50, 50, 130, 20);
		panel.add(lblTable);

		updateTableCombo = new JComboBox<>(new String[] { "Artists", "Albums", "Songs" });
		updateTableCombo.setBounds(220, 50, 180, 25);
		updateTableCombo.addActionListener(e -> refreshUpdateColumns());
		panel.add(updateTableCombo);

		JLabel lblColumn = new JLabel("Column to check (WHERE):");
		lblColumn.setBounds(50, 100, 160, 20);
		panel.add(lblColumn);

		updateColumnCombo = new JComboBox<>(ARTIST_COLUMNS);
		updateColumnCombo.setBounds(220, 100, 180, 25);
		panel.add(updateColumnCombo);

		JLabel lblWhereValue = new JLabel("Value to find:");
		lblWhereValue.setBounds(50, 150, 160, 20);
		panel.add(lblWhereValue);

		updateWhereValueField = new JTextField();
		updateWhereValueField.setBounds(220, 150, 180, 25);
		panel.add(updateWhereValueField);

		JLabel lblSetValue = new JLabel("New value to set (SET):");
		lblSetValue.setBounds(50, 200, 160, 20);
		panel.add(lblSetValue);

		updateSetValueField = new JTextField();
		updateSetValueField.setBounds(220, 200, 180, 25);
		panel.add(updateSetValueField);

		JButton submit = new JButton("Update");
		submit.setBounds(220, 260, 100, 30);
		submit.addActionListener(e -> handleUpdate());
		panel.add(submit);

		JButton back = new JButton("Back");
		back.setBounds(340, 260, 80, 30);
		back.addActionListener(e -> cardLayout.show(cardPanel, "MENU"));
		panel.add(back);

		return panel;
	}

	// Swaps the column dropdown's options to match whichever table is selected.
	private void refreshUpdateColumns() {
		String[] columns = switch (updateTableCombo.getSelectedIndex()) {
			case 0 -> ARTIST_COLUMNS;
			case 1 -> ALBUM_COLUMNS;
			default -> SONG_COLUMNS;
		};
		updateColumnCombo.setModel(new DefaultComboBoxModel<>(columns));
		updateWhereValueField.setText("");
		updateSetValueField.setText("");
	}

	// Builds "UPDATE <table> SET <column> = ? WHERE <column> = ?" from the
	// table/column picked in the combo boxes and the two typed-in values.
	private void handleUpdate() {
		Connection conn = getConn();
		if (conn == null) {
			JOptionPane.showMessageDialog(this, "Not connected to the database.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String table = switch (updateTableCombo.getSelectedIndex()) {
			case 0 -> "artists";
			case 1 -> "albums";
			default -> "songs";
		};
		String column = (String) updateColumnCombo.getSelectedItem();
		String whereValue = updateWhereValueField.getText().trim();
		String setValue = updateSetValueField.getText().trim();

		if (whereValue.isEmpty() || setValue.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please fill in both the value to find and the new value.",
					"Invalid Input", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String sql = "UPDATE " + table + " SET " + column + " = ? WHERE " + column + " = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, setValue);
			stmt.setString(2, whereValue);
			int rows = stmt.executeUpdate();
			JOptionPane.showMessageDialog(this, rows > 0 ? rows + " row(s) updated!" : "No matching entry found.");
			if (rows > 0) {
				updateWhereValueField.setText("");
				updateSetValueField.setText("");
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// ================= VIEW TABLES =================
	private JPanel buildViewTablesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		// Artists table — columns entered manually so the table (and its data)
		// shows up in the WindowBuilder Design tab instead of staying blank
		// until runtime.
		artistsModel = new DefaultTableModel(new Object[] { "ARTIST", "NAME", "COUNTRY" }, 0);
		artistsTable = new JTable(artistsModel);
		JScrollPane artistsScrollPane = new JScrollPane(artistsTable);
		artistsScrollPane.setBounds(20, 20, 280, 400);
		artistsScrollPane.setBorder(BorderFactory.createTitledBorder("Artists"));
		panel.add(artistsScrollPane);

		// Albums table
		albumsModel = new DefaultTableModel(new Object[] { "ALBUM", "RELEASE_YEAR", "ARTIST" }, 0);
		albumsTable = new JTable(albumsModel);
		JScrollPane albumsScrollPane = new JScrollPane(albumsTable);
		albumsScrollPane.setBounds(310, 20, 280, 400);
		albumsScrollPane.setBorder(BorderFactory.createTitledBorder("Albums"));
		panel.add(albumsScrollPane);

		// Songs table
		songsModel = new DefaultTableModel(
				new Object[] { "SONG", "DURATION_SECONDS", "TRACK_NUMBER", "ALBUM", "ARTIST" }, 0);
		songsTable = new JTable(songsModel);
		JScrollPane songsScrollPane = new JScrollPane(songsTable);
		songsScrollPane.setBounds(600, 20, 280, 400);
		songsScrollPane.setBorder(BorderFactory.createTitledBorder("Songs"));
		panel.add(songsScrollPane);

		JButton refresh = new JButton("Refresh");
		refresh.setBounds(360, 440, 100, 30);
		refresh.addActionListener(e -> loadAllTables());
		panel.add(refresh);

		JButton back = new JButton("Back");
		back.setBounds(480, 440, 80, 30);
		back.addActionListener(e -> cardLayout.show(cardPanel, "MENU"));
		panel.add(back);

		return panel;
	}

	// Runs SELECT * against each of the three tables and feeds the rows into
	// the manually-defined table models above (row count reset, rows re-added).
	private void loadAllTables() {
		Connection conn = getConn();
		if (conn == null) {
			JOptionPane.showMessageDialog(this, "Not connected to the database.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		loadArtists(conn);
		loadAlbums(conn);
		loadSongs(conn);
	}

	private void loadArtists(Connection conn) {
		artistsModel.setRowCount(0);
		String sql = "SELECT artist, name, country FROM artists";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				artistsModel.addRow(new Object[] {
						rs.getString("artist"), rs.getString("name"), rs.getString("country") });
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Failed to load artists: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadAlbums(Connection conn) {
		albumsModel.setRowCount(0);
		String sql = "SELECT album, release_year, artist FROM albums";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				albumsModel.addRow(new Object[] {
						rs.getString("album"), rs.getInt("release_year"), rs.getString("artist") });
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Failed to load albums: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadSongs(Connection conn) {
		songsModel.setRowCount(0);
		String sql = "SELECT song, duration_seconds, track_number, album, artist FROM songs";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				songsModel.addRow(new Object[] {
						rs.getString("song"), rs.getInt("duration_seconds"),
						rs.getInt("track_number"), rs.getString("album"), rs.getString("artist") });
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Failed to load songs: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
