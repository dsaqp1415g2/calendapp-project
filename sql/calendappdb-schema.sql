drop database if exists calendappdb;
create database calendappdb;

use calendappdb;

create table users (
	userid	 		int not null auto_increment primary key,
	username 		varchar(30) not null unique key,
	userpass 		char(32) not null,
	name	 		varchar(70) not null,
	age			int not null,
	email 	 		varchar(70) not null
);

create table users_roles (
	username			varchar(20) not null,
	rolename 			varchar(20) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rolename)
);

create table groups (
	groupid 	 	int not null auto_increment primary key,
	name 		 	varchar(100) not null,
	admin 		 	varchar(30) not null,
	description	 	varchar (500) not null,
	shared		 	boolean not null,
	last_modified		timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	creation_timestamp	datetime not null default current_timestamp,
	foreign key(admin)  	references users(username) on delete cascade
);

create table group_users (
	groupid 		int not null,
	userid			int not null,
	state			varchar(30) not null,
	primary key (groupid, userid),
	foreign key (groupid) 	references groups(groupid) on delete cascade,
	foreign key (userid) 	references users(userid) on delete cascade	
);

create table events (
	eventid		 	int not null auto_increment primary key,
	userid		 	int,
	groupid		 	int,
	name			varchar(100) not null,
	dateInitial	 	datetime not null,
	dateFinish	 	datetime not null,
	last_modified		timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	foreign key (userid) 	references users (userid) on delete cascade,
	foreign key (groupid) 	references groups (groupid) on delete cascade
);

create table comments (
	commentid	 	int not null auto_increment primary key,
	username	 	varchar(30) not null,
	eventid		 	int not null,
	content		 	varchar(200) not null,
	likes			int not null default 0,
	dislikes		int not null default 0,
	last_modified		timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	creation_timestamp	datetime not null default current_timestamp,
	foreign key (username)  references users (username) on delete cascade,
	foreign key (eventid) 	references events (eventid) on delete cascade
);

create table state (
	userid 			int not null,
	eventid			int not null,
	state			varchar(20) not null,
	primary key (userid, eventid),
	foreign key (userid) 	references users(userid) on delete cascade,
	foreign key (eventid)	references events (eventid) on delete cascade
);

create table likes (
	likeid			int not null auto_increment primary key,
	commentid		int not null,
	username		varchar(20) not null,
	likeComment		boolean not null,
	dislikeComment		boolean not null,
	foreign key(username) 	references users(username) on delete cascade,
	foreign key(commentid)	references comments(commentid) on delete cascade
);
