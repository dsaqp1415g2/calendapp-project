drop user 'calendapp'@'localhost';
create user 'calendapp'@'localhost' identified by 'calendapp';
grant all privileges on calendappdb.* to 'calendapp'@'localhost';
flush privileges;
