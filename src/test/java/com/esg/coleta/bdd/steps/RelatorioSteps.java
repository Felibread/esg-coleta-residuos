package com.esg.coleta.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class RelatorioSteps {

    @LocalServerPort
    private int port;

    private Response response;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Dado("que existem coletas concluídas no sistema")
    public void queExistemColetasConcluidas() {
        // Criar ponto de coleta
        Map<String, Object> ponto = new HashMap<>();
        ponto.put("nome", "Ponto Relatório ESG");
        ponto.put("tipoResiduo", "ORGANICO");
        ponto.put("endereco", "Av. Verde, 100");
        ponto.put("capacidadeKg", 800.0);

        Response rPonto = given()
                .contentType(ContentType.JSON)
                .body(ponto)
                .post("/api/pontos-coleta")
                .then()
                .statusCode(201)
                .extract().response();

        Long pontoId = rPonto.jsonPath().getLong("id");

        // Registrar coleta concluída
        Map<String, Object> coleta = new HashMap<>();
        coleta.put("pontoColetaId", pontoId);
        coleta.put("dataColeta", "2025-06-10");
        coleta.put("pesoColetadoKg", 200.0);
        coleta.put("responsavel", "Equipe ESG");
        coleta.put("status", "CONCLUIDA");

        given()
                .contentType(ContentType.JSON)
                .body(coleta)
                .post("/api/coletas")
                .then()
                .statusCode(201);
    }

    @Dado("que não há coletas concluídas no sistema")
    public void queNaoHaColetasConcluidas() {
        // Sem coletas — banco limpo por @Transactional no test
        // Apenas validar que a API responde
    }

    @Então("o corpo da resposta deve conter os campos ESG obrigatórios:")
    public void oCorpoDaRespostaDeveConterOsCamposESGObrigatorios(List<String> campos) {
        for (String campo : campos) {
            Object valor = response.jsonPath().get(campo.trim());
            assertThat(valor)
                    .as("Campo ESG '%s' deve estar presente na resposta", campo.trim())
                    .isNotNull();
        }
    }

    @Então("o campo {string} deve ser maior que zero")
    public void oCampoDeveSerMaiorQueZero(String campo) {
        Double valor = response.jsonPath().getDouble(campo);
        assertThat(valor)
                .as("Campo '%s' deve ser maior que zero", campo)
                .isGreaterThan(0.0);
    }

    @Então("o campo {string} deve ser igual a zero")
    public void oCampoDeveSerIgualAZero(String campo) {
        Double valor = response.jsonPath().getDouble(campo);
        assertThat(valor)
                .as("Campo '%s' deve ser igual a zero", campo)
                .isEqualTo(0.0);
    }

    @Então("o campo {string} deve conter informações de sustentabilidade")
    public void oCampoDeveConterInformacoesDeSustentabilidade(String campo) {
        String valor = response.jsonPath().getString(campo);
        assertThat(valor)
                .as("Campo '%s' deve conter informações de sustentabilidade", campo)
                .isNotBlank()
                .containsAnyOf("coleta", "resíduo", "reciclad", "CO2", "sustent", "ODS");
    }

    @Então("o campo {string} deve indicar ausência de coletas concluídas")
    public void oCampoDeveIndicarAusenciaDeColetas(String campo) {
        String valor = response.jsonPath().getString(campo);
        assertThat(valor)
                .as("Campo '%s' deve indicar ausência de coletas concluídas", campo)
                .isNotBlank();
    }
}
