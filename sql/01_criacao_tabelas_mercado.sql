CREATE DATABASE IF NOT EXISTS mercado_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mercado_db;

DROP TABLE IF EXISTS fornece;
DROP TABLE IF EXISTS pagamento;
DROP TABLE IF EXISTS item_venda;
DROP TABLE IF EXISTS venda;
DROP TABLE IF EXISTS gerente;
DROP TABLE IF EXISTS funcionario;
DROP TABLE IF EXISTS telefone;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS fornecedor;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS cliente;

CREATE TABLE cliente (
 cpf VARCHAR(11) PRIMARY KEY,
 nome VARCHAR(100) NOT NULL,
 data_nascimento DATE NOT NULL,
 rua VARCHAR(100) NOT NULL,
 numero VARCHAR(10) NOT NULL,
 cep VARCHAR(8) NOT NULL,
 bairro VARCHAR(60) NOT NULL,
 cidade VARCHAR(60) NOT NULL DEFAULT 'Recife',
 CONSTRAINT chk_cliente_cpf CHECK (CHAR_LENGTH(cpf)=11)
);

CREATE TABLE telefone (
 telefone_pk INT AUTO_INCREMENT PRIMARY KEY,
 telefone VARCHAR(20) NOT NULL,
 cpf_cliente VARCHAR(11) NOT NULL,
 CONSTRAINT uk_telefone_numero UNIQUE (telefone),
 CONSTRAINT fk_telefone_cliente FOREIGN KEY (cpf_cliente) REFERENCES cliente(cpf)
   ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE funcionario (
 matricula INT PRIMARY KEY,
 cpf VARCHAR(11) NOT NULL,
 nome VARCHAR(100) NOT NULL,
 salario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
 data_admissao DATE NOT NULL,
 telefone VARCHAR(20),
 matricula_supervisor INT NULL,
 CONSTRAINT uk_funcionario_cpf UNIQUE (cpf),
 CONSTRAINT chk_funcionario_cpf CHECK (CHAR_LENGTH(cpf)=11),
 CONSTRAINT chk_funcionario_salario CHECK (salario>=0),
 CONSTRAINT fk_funcionario_supervisor FOREIGN KEY (matricula_supervisor) REFERENCES funcionario(matricula)
   ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE gerente (
 matricula_funcionario INT PRIMARY KEY,
 nivel_gerencial VARCHAR(30) NOT NULL DEFAULT 'Gerente de Setor',
 setor_responsavel VARCHAR(50) NOT NULL,
 CONSTRAINT fk_gerente_funcionario FOREIGN KEY (matricula_funcionario) REFERENCES funcionario(matricula)
   ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE categoria (
 codigo_categoria INT AUTO_INCREMENT PRIMARY KEY,
 nome VARCHAR(50) NOT NULL,
 descricao VARCHAR(255),
 CONSTRAINT uk_categoria_nome UNIQUE (nome)
);

CREATE TABLE produto (
 codigo_produto INT PRIMARY KEY,
 marca VARCHAR(50) NOT NULL,
 nome VARCHAR(100) NOT NULL,
 preco DECIMAL(10,2) NOT NULL,
 unidade_medida VARCHAR(10) NOT NULL DEFAULT 'un',
 codigo_categoria INT NOT NULL,
 CONSTRAINT uk_produto_nome_marca UNIQUE (nome,marca),
 CONSTRAINT chk_produto_preco CHECK (preco>=0),
 CONSTRAINT fk_produto_categoria FOREIGN KEY (codigo_categoria) REFERENCES categoria(codigo_categoria)
   ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE fornecedor (
 cnpj VARCHAR(14) PRIMARY KEY,
 nome_fantasia VARCHAR(100) NOT NULL,
 razao_social VARCHAR(100) NOT NULL,
 rua VARCHAR(100) NOT NULL,
 cep VARCHAR(8) NOT NULL,
 numero VARCHAR(10) NOT NULL,
 bairro VARCHAR(60) NOT NULL,
 cidade VARCHAR(60) NOT NULL DEFAULT 'Recife',
 telefone VARCHAR(20),
 CONSTRAINT uk_fornecedor_razao_social UNIQUE (razao_social),
 CONSTRAINT chk_fornecedor_cnpj CHECK (CHAR_LENGTH(cnpj)=14)
);

CREATE TABLE venda (
 numero_venda INT AUTO_INCREMENT PRIMARY KEY,
 valor_total DECIMAL(10,2) NOT NULL,
 data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 cpf_cliente VARCHAR(11) NULL,
 matricula_funcionario INT NOT NULL,
 CONSTRAINT chk_venda_valor_total CHECK (valor_total>=0),
 CONSTRAINT fk_venda_cliente FOREIGN KEY (cpf_cliente) REFERENCES cliente(cpf)
   ON UPDATE RESTRICT ON DELETE SET NULL,
 CONSTRAINT fk_venda_funcionario FOREIGN KEY (matricula_funcionario) REFERENCES funcionario(matricula)
   ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE item_venda (
 numero_venda INT NOT NULL,
 numero_item INT NOT NULL,
 subtotal DECIMAL(10,2) NOT NULL,
 quantidade DECIMAL(10,3) NOT NULL,
 codigo_produto INT NOT NULL,
 PRIMARY KEY (numero_venda,numero_item),
 CONSTRAINT chk_item_quantidade CHECK (quantidade>0),
 CONSTRAINT chk_item_subtotal CHECK (subtotal>=0),
 CONSTRAINT fk_item_venda_venda FOREIGN KEY (numero_venda) REFERENCES venda(numero_venda)
   ON UPDATE CASCADE ON DELETE CASCADE,
 CONSTRAINT fk_item_venda_produto FOREIGN KEY (codigo_produto) REFERENCES produto(codigo_produto)
   ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE pagamento (
 codigo_pagamento INT AUTO_INCREMENT PRIMARY KEY,
 data_pagamento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 valor DECIMAL(10,2) NOT NULL,
 forma_pagamento VARCHAR(30) NOT NULL DEFAULT 'Dinheiro',
 numero_venda INT NOT NULL,
 cpf_cliente VARCHAR(11) NULL,
 CONSTRAINT chk_pagamento_valor CHECK (valor>0),
 CONSTRAINT fk_pagamento_venda FOREIGN KEY (numero_venda) REFERENCES venda(numero_venda)
   ON UPDATE CASCADE ON DELETE CASCADE,
 CONSTRAINT fk_pagamento_cliente FOREIGN KEY (cpf_cliente) REFERENCES cliente(cpf)
   ON UPDATE RESTRICT ON DELETE SET NULL
);

CREATE TABLE fornece (
 cnpj_fornecedor VARCHAR(14) NOT NULL,
 codigo_produto INT NOT NULL,
 PRIMARY KEY (cnpj_fornecedor,codigo_produto),
 CONSTRAINT fk_fornece_fornecedor FOREIGN KEY (cnpj_fornecedor) REFERENCES fornecedor(cnpj)
   ON UPDATE CASCADE ON DELETE CASCADE,
 CONSTRAINT fk_fornece_produto FOREIGN KEY (codigo_produto) REFERENCES produto(codigo_produto)
   ON UPDATE CASCADE ON DELETE CASCADE
);
