source calendappdb-schema.sql;
insert into users(username, userpass, name, age, email) values('angel', MD5('angel'), 'Angel Jara', '22', 'angel@angel.com');

insert into users(username, userpass, name, age, email) values('otal', MD5('otal'), 'Alejandro ', '22', 'alej@martinezotal.com');

insert into users(username, userpass, name, age, email) values('jordi', MD5('jordi'), 'Jordi Iglesias', '25', 'jordi@iglesias.com');

insert into users(username, userpass, name, age, email) values('pepito', MD5('pepito'), 'Pepito Grillo', '53', 'pepito@grillo.com');

insert into users(username, userpass, name, age, email) values('manolito', MD5('manolito'), 'Manolito Gafotas', '31', 'manolito@gafotas.com');

insert into users_roles values ('angel', 'registered');
insert into users_roles values ('otal', 'registered');
insert into users_roles values ('jordi', 'registered');
insert into users_roles values ('pepito', 'registered');
insert into users_roles values ('manolito', 'registered');

insert into groups(name, admin, description, shared) values('UPC-DSA', 'angel', 'grupo con los eventos de la asignatura DSA de la UPC', true);
select sleep(1);
insert into groups(name, admin, description, shared) values('Fiestas locas', 'jordi', 'Todas las fiestas brutales!!!', false);
select sleep(1);
insert into groups(name, admin, description, shared) values('Institucion Tecnologica Profesional SL', 'otal', 'Reuniones de la institucion', false);



insert into group_users values ('1', '1', 'accepted');
insert into group_users values ('1', '2', 'accepted');
insert into group_users values ('1', '3', 'accepted');
insert into group_users values ('2', '1', 'accepted');
insert into group_users values ('2', '2', 'accepted');
insert into group_users values ('2', '5', 'accepted');
insert into group_users values ('3', '2', 'accepted');
insert into group_users values ('3', '3', 'accepted');
insert into group_users values ('3', '4', 'accepted');
insert into group_users values ('3', '1', 'pending');



insert into events (userid, name, dateInitial, dateFinish) values ('1', 'empezar proyecto DSA, la api', '2015-4-4 10:00:00', '2015-7-10 10:00:00');
select sleep(1);
insert into events (userid, name, dateInitial, dateFinish) values ('2', 'ir al medico', '2015-7-2 10:00:00', '2015-7-2 11:00:00');
select sleep(1);
insert into events (userid, name, dateInitial, dateFinish) values ('4', 'partido con los amigos', '2015-8-2 17:00:00', '2015-8-2 19:30:00');
select sleep(1);
insert into events (groupid, name, dateInitial, dateFinish) values ('1', 'evento prueba', '2015-4-21 18:00:00', '2015-7-21 20:30:00');
select sleep(1);
insert into events (groupid, name, dateInitial, dateFinish) values ('2', 'Fiesta Maxima en Barcelona', '2015-7-21 18:00:00', '2015-7-22 23:00:00');
select sleep(1);
insert into events (groupid, name, dateInitial, dateFinish) values ('3', 'reunion de ejecutivos', '2015-9-25 08:00:00', '2015-9-25 10:30:00');
select sleep(1);
insert into events (groupid, name, dateInitial, dateFinish) values ('3', 'Visita a las nuevas instalaciones', '2015-10-19 18:00:00', '2015-10-19 21:00:00');
select sleep(1);
insert into events (groupid, name, dateInitial, dateFinish) values ('1', 'entrega final proyecto', '2015-11-15 09:00:00', '2015-11-15 10:00:00');




insert into comments (username, eventid, content, likes) values ('angel', '1', 'comentario de prueba', '1');
select sleep(1);
insert into comments (username, eventid, content, likes, dislikes) values ('angel', '5', 'ahi estare!!!', '1','1');
select sleep(1);
insert into comments (username, eventid, content, likes) values ('jordi', '6', 'Tener en cuenta de llevar todos los documentos', '1');
select sleep(1);
insert into comments (username, eventid, content) values ('angel', '8', 'ya falta poco para acabarlo');
select sleep(1);
insert into comments (username, eventid, content) values ('jordi', '8', 'pues si');


insert into state values ('1','4', 'pending');
insert into state values ('2','4', 'join');
insert into state values ('3','4', 'decline');
insert into state values ('1','5', 'pending');
insert into state values ('2','5', 'join');
insert into state values ('5','5', 'decline');
insert into state values ('2','6', 'pending');
insert into state values ('3','6', 'join');
insert into state values ('4','6', 'decline');
insert into state values ('2','7', 'join');
insert into state values ('3','7', 'join');
insert into state values ('4','7', 'pending');
insert into state values ('1','8', 'join');
insert into state values ('2','8', 'join');
insert into state values ('3','8', 'join');

insert into likes (commentid, username, likeComment, dislikeComment) values ('1','angel', true, false);
insert into likes (commentid, username, likeComment, dislikeComment) values ('2','jordi', true, false);
insert into likes (commentid, username, likeComment, dislikeComment) values ('2','manolito', false, true);
insert into likes (commentid, username, likeComment, dislikeComment) values ('3','otal', true, false);



