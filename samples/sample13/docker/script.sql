CREATE DATABASE testdb;
USE testdb;

CREATE TABLE student(id INT AUTO_INCREMENT,
                         age INT, name VARCHAR(255), PRIMARY KEY (id));

INSERT INTO student(age, name) values(28, 'Anuruddha');
