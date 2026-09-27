USE mercado_db;


SELECT
    p.codigo_produto,
    p.nome AS produto,
    p.marca,
    c.nome AS categoria,
    SUM(iv.quantidade) AS quantidade_vendida,
    SUM(iv.subtotal) AS valor_vendido
FROM produto p
INNER JOIN categoria c ON c.codigo_categoria = p.codigo_categoria
INNER JOIN item_venda iv ON iv.codigo_produto = p.codigo_produto
GROUP BY p.codigo_produto, p.nome, p.marca, c.nome
HAVING SUM(iv.quantidade) > (
    SELECT AVG(media_produto.quantidade_total)
    FROM (
        SELECT SUM(iv2.quantidade) AS quantidade_total
        FROM item_venda iv2
        INNER JOIN produto p2 ON p2.codigo_produto = iv2.codigo_produto
        GROUP BY p2.codigo_produto
    ) AS media_produto
)
ORDER BY quantidade_vendida DESC, valor_vendido DESC;

SELECT
    c.cpf,
    c.nome AS cliente,
    COUNT(v.numero_venda) AS quantidade_compras,
    SUM(v.valor_total) AS total_gasto,
    AVG(v.valor_total) AS ticket_medio
FROM cliente c
INNER JOIN venda v ON v.cpf_cliente = c.cpf
GROUP BY c.cpf, c.nome
HAVING SUM(v.valor_total) > (
    SELECT AVG(t.total_cliente)
    FROM (
        SELECT v2.cpf_cliente, SUM(v2.valor_total) AS total_cliente
        FROM venda v2
        INNER JOIN cliente c2 ON c2.cpf = v2.cpf_cliente
        WHERE v2.cpf_cliente IS NOT NULL
        GROUP BY v2.cpf_cliente
    ) AS t
)
ORDER BY total_gasto DESC;

SELECT
    f.matricula,
    f.nome AS funcionario,
    COUNT(v.numero_venda) AS quantidade_vendas,
    SUM(v.valor_total) AS total_vendido,
    AVG(v.valor_total) AS ticket_medio
FROM funcionario f
INNER JOIN venda v ON v.matricula_funcionario = f.matricula
GROUP BY f.matricula, f.nome
HAVING SUM(v.valor_total) > (
    SELECT AVG(t.total_funcionario)
    FROM (
        SELECT v2.matricula_funcionario, SUM(v2.valor_total) AS total_funcionario
        FROM venda v2
        INNER JOIN funcionario f2 ON f2.matricula = v2.matricula_funcionario
        GROUP BY v2.matricula_funcionario
    ) AS t
)
ORDER BY total_vendido DESC;

SELECT
    c.codigo_categoria,
    c.nome AS categoria,
    COUNT(DISTINCT v.numero_venda) AS vendas_com_produtos_da_categoria,
    SUM(iv.quantidade) AS quantidade_itens,
    SUM(iv.subtotal) AS faturamento_categoria
FROM categoria c
INNER JOIN produto p ON p.codigo_categoria = c.codigo_categoria
INNER JOIN item_venda iv ON iv.codigo_produto = p.codigo_produto
INNER JOIN venda v ON v.numero_venda = iv.numero_venda
GROUP BY c.codigo_categoria, c.nome
HAVING SUM(iv.subtotal) > (
    SELECT AVG(t.total_categoria)
    FROM (
        SELECT p2.codigo_categoria, SUM(iv2.subtotal) AS total_categoria
        FROM produto p2
        INNER JOIN item_venda iv2 ON iv2.codigo_produto = p2.codigo_produto
        GROUP BY p2.codigo_categoria
    ) AS t
)
ORDER BY faturamento_categoria DESC;

SELECT
    f.cnpj,
    f.nome_fantasia AS fornecedor,
    COUNT(DISTINCT p.codigo_produto) AS produtos_acima_media,
    ROUND(AVG(p.preco), 2) AS preco_medio_desses_produtos,
    GROUP_CONCAT(DISTINCT c.nome ORDER BY c.nome SEPARATOR ', ') AS categorias
FROM fornecedor f
INNER JOIN fornece fr ON fr.cnpj_fornecedor = f.cnpj
INNER JOIN produto p ON p.codigo_produto = fr.codigo_produto
INNER JOIN categoria c ON c.codigo_categoria = p.codigo_categoria
WHERE p.preco > (
    SELECT AVG(p2.preco)
    FROM produto p2
    INNER JOIN categoria c2 ON c2.codigo_categoria = p2.codigo_categoria
)
GROUP BY f.cnpj, f.nome_fantasia
ORDER BY produtos_acima_media DESC, fornecedor;

SELECT
    v.numero_venda,
    v.data_hora,
    COALESCE(c.nome, 'Consumidor não identificado') AS cliente,
    f.nome AS funcionario,
    v.valor_total,
    ROUND(v.valor_total - (SELECT AVG(v2.valor_total) FROM venda v2), 2) AS acima_da_media
FROM venda v
LEFT JOIN cliente c ON c.cpf = v.cpf_cliente
INNER JOIN funcionario f ON f.matricula = v.matricula_funcionario
WHERE v.valor_total > (SELECT AVG(v3.valor_total) FROM venda v3)
ORDER BY v.valor_total DESC;

SELECT
    pg.forma_pagamento,
    COUNT(*) AS quantidade_pagamentos,
    COUNT(DISTINCT pg.numero_venda) AS vendas_atendidas,
    SUM(pg.valor) AS valor_movimentado,
    ROUND(AVG(v.valor_total), 2) AS ticket_medio_das_vendas,
    ROUND(
        SUM(pg.valor) * 100 / (SELECT SUM(pg2.valor) FROM pagamento pg2),
        2
    ) AS participacao_percentual
FROM pagamento pg
INNER JOIN venda v ON v.numero_venda = pg.numero_venda
GROUP BY pg.forma_pagamento
HAVING SUM(pg.valor) >= (
    SELECT AVG(t.total_forma)
    FROM (
        SELECT SUM(pg3.valor) AS total_forma
        FROM pagamento pg3
        GROUP BY pg3.forma_pagamento
    ) AS t
)
ORDER BY valor_movimentado DESC;

SELECT
    c.cpf,
    c.nome AS cliente,
    c.bairro,
    c.cidade,
    COUNT(t.telefone_pk) AS quantidade_telefones
FROM cliente c
LEFT JOIN telefone t ON t.cpf_cliente = c.cpf
WHERE NOT EXISTS (
    SELECT 1
    FROM venda v
    WHERE v.cpf_cliente = c.cpf
)
GROUP BY c.cpf, c.nome, c.bairro, c.cidade
ORDER BY c.nome;
