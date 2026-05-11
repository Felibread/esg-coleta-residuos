# language: pt
Funcionalidade: Gestão de Pontos de Coleta de Resíduos
  Como gestor ambiental
  Quero gerenciar pontos de coleta de resíduos
  Para garantir a eficiência da coleta seletiva e conformidade ESG

  Contexto:
    Dado que a aplicação está em execução

  # ==========================================
  # CENÁRIO 1 - Caminho Feliz (Positivo)
  # ==========================================
  Cenário: Cadastrar um novo ponto de coleta com sucesso
    Dado que tenho os dados de um novo ponto de coleta:
      | nome         | Ecoponto Jardim América     |
      | tipoResiduo  | PLASTICO                    |
      | endereco     | Rua das Flores, 100         |
      | capacidadeKg | 500.0                       |
    Quando envio uma requisição POST para "/api/pontos-coleta"
    Então o status da resposta deve ser 201
    E o corpo da resposta deve conter o campo "id" não nulo
    E o corpo da resposta deve conter "nome" com valor "Ecoponto Jardim América"
    E o corpo da resposta deve conter "tipoResiduo" com valor "PLASTICO"
    E o corpo da resposta deve conter "ativo" com valor "true"

  # ==========================================
  # CENÁRIO 2 - Caminho Feliz (Positivo)
  # ==========================================
  Cenário: Consultar um ponto de coleta existente por ID
    Dado que existe um ponto de coleta cadastrado com nome "Ecoponto Vila Verde" e tipo "VIDRO"
    Quando envio uma requisição GET para "/api/pontos-coleta/{id}"
    Então o status da resposta deve ser 200
    E o corpo da resposta deve conter "nome" com valor "Ecoponto Vila Verde"
    E o corpo da resposta deve conter "tipoResiduo" com valor "VIDRO"

  # ==========================================
  # CENÁRIO 3 - Caminho Negativo (Falha)
  # ==========================================
  Cenário: Tentar cadastrar ponto de coleta com dados inválidos
    Dado que tenho dados inválidos para um ponto de coleta:
      | nome         |        |
      | tipoResiduo  | PLASTICO |
      | endereco     |        |
      | capacidadeKg | -100   |
    Quando envio uma requisição POST para "/api/pontos-coleta"
    Então o status da resposta deve ser 400
    E o corpo da resposta deve conter o campo "erro" com valor "Dados inválidos"
    E o corpo da resposta deve conter o campo "mensagens" com erros de validação

  # ==========================================
  # CENÁRIO 4 - Caminho Negativo (Falha)
  # ==========================================
  Cenário: Consultar ponto de coleta inexistente
    Quando envio uma requisição GET para "/api/pontos-coleta/99999"
    Então o status da resposta deve ser 404
    E o corpo da resposta deve conter o campo "erro" com valor "Recurso não encontrado"

  # ==========================================
  # CENÁRIO 5 - Caminho Feliz (Positivo)
  # ==========================================
  Cenário: Listar todos os pontos de coleta ativos
    Dado que existem pontos de coleta cadastrados no sistema
    Quando envio uma requisição GET para "/api/pontos-coleta?apenasAtivos=true"
    Então o status da resposta deve ser 200
    E o corpo da resposta deve ser uma lista
    E todos os itens da lista devem ter "ativo" igual a "true"
