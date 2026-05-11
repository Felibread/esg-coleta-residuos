# language: pt
Funcionalidade: Registro de Coletas de Resíduos
  Como operador de coleta
  Quero registrar as coletas realizadas nos pontos de coleta
  Para garantir rastreabilidade e conformidade com as metas ESG

  Contexto:
    Dado que a aplicação está em execução
    E que existe um ponto de coleta ativo com tipo "PAPEL"

  # ==========================================
  # CENÁRIO 1 - Caminho Feliz
  # ==========================================
  Cenário: Registrar uma coleta com sucesso
    Dado que tenho os dados de uma nova coleta:
      | dataColeta      | 2025-06-15         |
      | pesoColetadoKg  | 150.5              |
      | responsavel     | João Silva         |
      | status          | CONCLUIDA          |
      | observacoes     | Coleta regular ESG |
    Quando envio uma requisição POST para "/api/coletas"
    Então o status da resposta deve ser 201
    E o corpo da resposta deve conter o campo "id" não nulo
    E o corpo da resposta deve conter "responsavel" com valor "João Silva"
    E o corpo da resposta deve conter "status" com valor "CONCLUIDA"
    E o corpo da resposta deve conter "pesoColetadoKg" com valor "150.5"

  # ==========================================
  # CENÁRIO 2 - Caminho Negativo
  # ==========================================
  Cenário: Tentar registrar coleta com peso negativo
    Dado que tenho os dados de uma coleta inválida:
      | dataColeta      | 2025-06-15     |
      | pesoColetadoKg  | -50.0          |
      | responsavel     | Maria Souza    |
    Quando envio uma requisição POST para "/api/coletas"
    Então o status da resposta deve ser 400
    E o corpo da resposta deve conter o campo "erro" com valor "Dados inválidos"

  # ==========================================
  # CENÁRIO 3 - Caminho Feliz
  # ==========================================
  Cenário: Atualizar o status de uma coleta para CONCLUIDA
    Dado que existe uma coleta com status "AGENDADA" cadastrada
    Quando envio uma requisição PATCH para atualizar o status para "CONCLUIDA"
    Então o status da resposta deve ser 200
    E o corpo da resposta deve conter "status" com valor "CONCLUIDA"

  # ==========================================
  # CENÁRIO 4 - Caminho Negativo
  # ==========================================
  Cenário: Tentar registrar coleta em ponto inexistente
    Dado que uso o ID 99999 como ponto de coleta inexistente
    Quando envio uma requisição POST para "/api/coletas" com ponto inexistente
    Então o status da resposta deve ser 404
    E o corpo da resposta deve conter o campo "erro" com valor "Recurso não encontrado"
