# language: pt
Funcionalidade: Relatório ESG de Impacto Ambiental
  Como gestor de sustentabilidade
  Quero consultar o relatório ESG consolidado
  Para acompanhar o impacto ambiental das coletas e reportar métricas de governança

  Contexto:
    Dado que a aplicação está em execução

  # ==========================================
  # CENÁRIO 1 - Caminho Feliz
  # ==========================================
  Cenário: Gerar relatório ESG com dados de coletas concluídas
    Dado que existem coletas concluídas no sistema
    Quando envio uma requisição GET para "/api/relatorio/esg"
    Então o status da resposta deve ser 200
    E o corpo da resposta deve conter os campos ESG obrigatórios:
      | totalPontosAtivos       |
      | totalColetasRealizadas  |
      | totalPesoColetadoKg     |
      | mediaPesoColetadoKg     |
      | coletasConcluidas       |
      | coletasPendentes        |
      | impactoAmbiental        |
      | co2EvitadoKg            |
    E o campo "co2EvitadoKg" deve ser maior que zero
    E o campo "impactoAmbiental" deve conter informações de sustentabilidade

  # ==========================================
  # CENÁRIO 2 - Caminho Feliz (sem dados)
  # ==========================================
  Cenário: Gerar relatório ESG sem coletas concluídas
    Dado que não há coletas concluídas no sistema
    Quando envio uma requisição GET para "/api/relatorio/esg"
    Então o status da resposta deve ser 200
    E o campo "co2EvitadoKg" deve ser igual a zero
    E o campo "impactoAmbiental" deve indicar ausência de coletas concluídas
