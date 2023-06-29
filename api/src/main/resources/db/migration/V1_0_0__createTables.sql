CREATE TABLE Author (
    id int NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE author_unique_index (name)
) CHARACTER SET utf8mb4;

CREATE TABLE Book (
    id int NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author_id int NOT NULL,
    published_at date NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES Author(id),
    UNIQUE book_unique_index (title, author_id),
    INDEX published_at_index (published_at)
) CHARACTER SET utf8mb4;
