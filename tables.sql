CREATE TABLE artists (
    artist VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(100)
);

CREATE TABLE albums (
    album VARCHAR(255) PRIMARY KEY,
    release_year INT,
    artist VARCHAR(255),
    FOREIGN KEY (artist) REFERENCES artists(artist) ON DELETE CASCADE
);

CREATE TABLE songs (
    song VARCHAR(255) PRIMARY KEY,
    duration_seconds INT,
    track_number INT,
    album VARCHAR(255),
    artist VARCHAR(255),
    FOREIGN KEY (album) REFERENCES albums(album) ON DELETE CASCADE,
    FOREIGN KEY (artist) REFERENCES artists(artist) ON DELETE CASCADE
);
