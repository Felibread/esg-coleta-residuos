package com.esg.coleta.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ColetaSteps {

    @LocalServerPort
    private int port;

    private Map<String, Object> requestBody;
    private Response response;
    private Long pontoColetaId;
    private Long coletaId;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        requestBody = new HashMap<>();
    }

    @Dado("que existe um ponto de coleta ativo com tipo {string}")
    public void queExisteUmPontoDeColetaAtivoComTipo(String tipo) {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Ponto BDD Coleta " + tipo);
        body.put("tipoResiduo", tipo);
        body.put("endereco", "Rua BDD, 42");
        body.put("capacidadeKg", 1000.0);

        Response r = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/pontos-coleta")
                .then()
                .statusCode(201)
                .extract().response();

        pontoColetaId = r.jsonPath().getLong("id");
    }

    @Dado("que tenho os dados de uma nova coleta:")
    public void queTenhoOsDadosDeUmaNovaColeta(Map<String, String> dados) {
        requestBody = new HashMap<>();
        requestBody.put("pontoColetaId", pontoColetaId);
        requestBody.put("dataColeta", dados.get("dataColeta"));
        requestBody.put("pesoColetadoKg", Double.parseDouble(dados.get("pesoColetadoKg")));
        requestBody.put("responsavel", dados.get("responsavel"));
        if (dados.containsKey("status") && !dados.get("status").isBlank()) {
            requestBody.put("status", dados.get("status"));
        }
        if (dados.containsKey("observacoes")) {
            requestBody.put("observacoes", dados.get("observacoes"));
        }
    }

    @Dado("que tenho os dados de uma coleta inválida:")
    public void queTenhoOsDadosDeUmaColetaInvalida(Map<String, String> dados) {
        requestBody = new HashMap<>();
        requestBody.put("pontoColetaId", pontoColetaId);
        requestBody.put("dataColeta", dados.get("dataColeta"));
        requestBody.put("pesoColetadoKg", Double.parseDouble(dados.get("pesoColetadoKg")));
        requestBody.put("responsavel", dados.get("responsavel"));
    }

    @Dado("que existe uma coleta com status {string} cadastrada")
    public void queExisteUmaColetaComStatusCadastrada(String status) {
        Map<String, Object> body = new HashMap<>();
        body.put("pontoColetaId", pontoColetaId);
        body.put("dataColeta", "2025-06-20");
        body.put("pesoColetadoKg", 80.0);
        body.put("responsavel", "Operador BDD");
        body.put("status", status);

        Response r = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/coletas")
                .then()
                .statusCode(201)
                .extract().response();

        coletaId = r.jsonPath().getLong("id");
    }

    @Dado("que uso o ID 99999 como ponto de coleta inexistente")
    public void queUsoOIdComoPontoDeColetaInexistente() {
        pontoColetaId = 99999L;
    }

    @Quando("envio uma requisição POST para {string} com ponto inexistente")
    public void envioUmaRequisicaoPOSTComPontoInexistente(String endpoint) {
        requestBody = new HashMap<>();
        requestBody.put("pontoColetaId", pontoColetaId);
        requestBody.put("dataColeta", "2025-07-01");
        requestBody.put("pesoColetadoKg", 100.0);
        requestBody.put("responsavel", "Teste");

        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .extract().response();
    }

    @Quando("envio uma requisição PATCH para atualizar o status para {string}")
    public void envioUmaRequisicaoPATCHParaAtualizarOStatus(String novoStatus) {
        response = given()
                .when()
                .patch("/api/coletas/" + coletaId + "/status?status=" + novoStatus)
                .then()
                .extract().response();
    }
}
