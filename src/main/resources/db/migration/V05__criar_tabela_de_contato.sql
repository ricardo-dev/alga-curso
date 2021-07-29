create table contato (
	id bigint(20) primary key auto_increment,
	id_pessoa bigint(20) not null,
	nome varchar(50) not null,
	email varchar(100) not null,
	telefone varchar(20) not null,
	foreign key (id_pessoa) references pessoa(id)
) engine=InnoDB default charset=utf8;

insert into contato (id, id_pessoa, nome, email, telefone) values (1,1,'Marcos Henrique', 'kerittypereira@gmail.com',
		'00 0000-0000');
