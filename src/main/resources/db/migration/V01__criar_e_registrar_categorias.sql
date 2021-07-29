create  table categoria (

    id bigint(20) primary key auto_increment,
    name varchar(50) not null

) ENGINE=InnoDB default charset=utf8;

insert into categoria (name) values ('Lazer');
insert into categoria (name) values ('Alimentação');
insert into categoria (name) values ('Supermercado');
insert into categoria (name) values ('Farmácia');
insert into categoria (name) values ('Outros');
