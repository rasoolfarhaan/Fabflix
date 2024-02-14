<<<<<<< HEAD
-- Create the "moviedb" database
CREATE DATABASE moviedb;

-- Use the "moviedb" database
USE moviedb;

-- Create the "movies" table
CREATE TABLE movies (
                        id VARCHAR(10) PRIMARY KEY NOT NULL,
                        title VARCHAR(100) NOT NULL,
                        year INTEGER NOT NULL,
                        director VARCHAR(100) NOT NULL
);

-- Create the "stars" table
CREATE TABLE stars (
                       id VARCHAR(10) PRIMARY KEY NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       birthYear INTEGER
);

-- Create the "stars_in_movies" table with foreign key references
CREATE TABLE stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    PRIMARY KEY (starId, movieId),
    FOREIGN KEY (starId) REFERENCES stars (id),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

-- Create the "genres" table with an auto-incrementing primary key
CREATE TABLE genres (
                        id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                        name VARCHAR(32) NOT NULL
);

-- Create the "genres_in_movies" table with foreign key references
CREATE TABLE genres_in_movies (
                                  genreId INTEGER NOT NULL,
                                  movieId VARCHAR(10) NOT NULL,
                                  PRIMARY KEY (genreId, movieId),
                                  FOREIGN KEY (genreId) REFERENCES genres (id),
                                  FOREIGN KEY (movieId) REFERENCES movies (id)
);

-- Create the "creditcards" table
CREATE TABLE creditcards (
                             id VARCHAR(20) PRIMARY KEY NOT NULL,
                             firstName VARCHAR(50) NOT NULL,
                             lastName VARCHAR(50) NOT NULL,
                             expiration DATE NOT NULL
);

-- Create the "customers" table with an auto-incrementing primary key and foreign key reference
CREATE TABLE customers (
                           id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                           firstName VARCHAR(50) NOT NULL,
                           lastName VARCHAR(50) NOT NULL,
                           ccId VARCHAR(20) NOT NULL,
                           address VARCHAR(200) NOT NULL,
                           email VARCHAR(50) NOT NULL,
                           password VARCHAR(20) NOT NULL,
                           FOREIGN KEY (ccId) REFERENCES creditcards (id)
);

-- Create the "sales" table with an auto-incrementing primary key and foreign key references
CREATE TABLE sales (
                       id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                       customerId INTEGER NOT NULL,
                       movieId VARCHAR(10) NOT NULL,
                       saleDate DATE NOT NULL,
                       FOREIGN KEY (customerId) REFERENCES customers (id),
                       FOREIGN KEY (movieId) REFERENCES movies (id)
);

-- Create the "ratings" table with a foreign key reference
CREATE TABLE ratings (
                         movieId VARCHAR(10) NOT NULL,
                         rating FLOAT NOT NULL,
                         numVotes INTEGER NOT NULL,
                         PRIMARY KEY (movieId),
                         FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE TABLE employees (
        email VARCHAR(50) PRIMARY KEY,
        password VARCHAR(20) NOT NULL,
        fullname VARCHAR(100)
=======
-- Create the "moviedb" database
CREATE DATABASE moviedb;

-- Use the "moviedb" database
USE moviedb;

-- Create the "movies" table
CREATE TABLE movies (
                        id VARCHAR(10) PRIMARY KEY NOT NULL,
                        title VARCHAR(100) NOT NULL,
                        year INTEGER NOT NULL,
                        director VARCHAR(100) NOT NULL
);

-- Create the "stars" table
CREATE TABLE stars (
                       id VARCHAR(10) PRIMARY KEY NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       birthYear INTEGER
);

-- Create the "stars_in_movies" table with foreign key references
CREATE TABLE stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    PRIMARY KEY (starId, movieId),
    FOREIGN KEY (starId) REFERENCES stars (id),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

-- Create the "genres" table with an auto-incrementing primary key
CREATE TABLE genres (
                        id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                        name VARCHAR(32) NOT NULL
);

-- Create the "genres_in_movies" table with foreign key references
CREATE TABLE genres_in_movies (
                                  genreId INTEGER NOT NULL,
                                  movieId VARCHAR(10) NOT NULL,
                                  PRIMARY KEY (genreId, movieId),
                                  FOREIGN KEY (genreId) REFERENCES genres (id),
                                  FOREIGN KEY (movieId) REFERENCES movies (id)
);

-- Create the "creditcards" table
CREATE TABLE creditcards (
                             id VARCHAR(20) PRIMARY KEY NOT NULL,
                             firstName VARCHAR(50) NOT NULL,
                             lastName VARCHAR(50) NOT NULL,
                             expiration DATE NOT NULL
);

-- Create the "customers" table with an auto-incrementing primary key and foreign key reference
CREATE TABLE customers (
                           id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                           firstName VARCHAR(50) NOT NULL,
                           lastName VARCHAR(50) NOT NULL,
                           ccId VARCHAR(20) NOT NULL,
                           address VARCHAR(200) NOT NULL,
                           email VARCHAR(50) NOT NULL,
                           password VARCHAR(20) NOT NULL,
                           FOREIGN KEY (ccId) REFERENCES creditcards (id)
);

-- Create the "sales" table with an auto-incrementing primary key and foreign key references
CREATE TABLE sales (
                       id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                       customerId INTEGER NOT NULL,
                       movieId VARCHAR(10) NOT NULL,
                       saleDate DATE NOT NULL,
                       FOREIGN KEY (customerId) REFERENCES customers (id),
                       FOREIGN KEY (movieId) REFERENCES movies (id)
);

-- Create the "ratings" table with a foreign key reference
CREATE TABLE ratings (
                         movieId VARCHAR(10) NOT NULL,
                         rating FLOAT NOT NULL,
                         numVotes INTEGER NOT NULL,
                         PRIMARY KEY (movieId),
                         FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE TABLE employees (
        email VARCHAR(50) PRIMARY KEY,
        password VARCHAR(20) NOT NULL,
        fullname VARCHAR(100)
>>>>>>> 949d40c754f51f00087c1a4884d58820ce2f6673
);