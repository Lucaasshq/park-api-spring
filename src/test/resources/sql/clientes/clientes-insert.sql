insert into USUARIOS  (id, username, password, role) values (100, 'admin@gmail.com', '$2a$12$Y3ypwyTD5Czhu7/sWaCU3uNa9VWk9vjIg6pTDelK1tDFdj/M8NIM6', 'ADMIN');
insert into USUARIOS  (id, username, password, role) values (101, 'bob@gmail.com', '$2a$12$UmfBvcgfNF3rCI9mj2kTVe5NpW0IMKQYoeOwBIvGk1nQpvW.XcX3C', 'USER');
insert into USUARIOS  (id, username, password, role) values (102, 'raissa@gmail.com', '$2a$12$Y3ypwyTD5Czhu7/sWaCU3uNa9VWk9vjIg6pTDelK1tDFdj/M8NIM6', 'USER');
insert into USUARIOS  (id, username, password, role) values (103, 'toby@gmail.com', '$2a$12$Y3ypwyTD5Czhu7/sWaCU3uNa9VWk9vjIg6pTDelK1tDFdj/M8NIM6', 'USER');

insert into CLIENTES (id, nome, cpf, id_usuario) values (10, 'bob souza', '38352600060', 101);
insert into CLIENTES (id, nome, cpf, id_usuario) values (20, 'Raissa Mendes', '17908922015', 102);