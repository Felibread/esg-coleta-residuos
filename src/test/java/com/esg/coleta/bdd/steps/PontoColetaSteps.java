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

public class PontoColetaSteps {

    @LocalServerPort
    private int port;

    private Map<String, Object> requestBody;
    private Response response;
    private Long pontoColetaId;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        requestBody = new HashMap<>();
    }

    @Dado("que a aplicação está em execução")
    public void queAAplicacaoEstaEmExecucao() {
        Response healthCheck = given()
                .when()
                .get("/api/pontos-coleta")
                .then()
                .extract().response();
        assertThat(healthCheck.statusCode()).isIn(200, 404);
    }

    @Dado("que tenho os dados de um novo ponto de coleta:")
    public void queTenhoOsDadosDeUmNovoPontoDeColeta(Map<String, String> dados) {
        requestBody = new HashMap<>();
        requestBody.put("nome", dados.get("nome"));
        requestBody.put("tipoResiduo", dados.get("tipoResiduo"));
        requestBody.put("endereco", dados.get("endereco"));
        requestBody.put("capacidadeKg", Double.parseDouble(dados.get("capacidadeKg")));
    }

    @Dado("que tenho dados inválidos para um ponto de coleta:")
    public void queTenhoDadosInvalidosParaUmPontoDeColeta(Map<String, String> dados) {
        requestBody = new HashMap<>();
        requestBody.put("nome", dados.get("nome"));
        requestBody.put("tipoResiduo", dados.get("tipoResiduo"));
        requestBody.put("endereco", dados.get("endereco"));
        // Capacidade negativa = inválida
        String cap = dados.get("capacidadeKg");
        if (cap != null && !cap.isBlank()) {
            requestBody.put("capacidadeKg", Double.parseDouble(cap));
        }
    }

    @Dado("que existe um ponto de coleta cadastrado com nome {string} e tipo {string}")
    public void queExisteUmPontoDeColetaCadastrado(String nome, String tipo) {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("tipoResiduo", tipo);
        body.put("endereco", "Endereço de Teste, 123");
        body.put("capacidadeKg", 300.0);

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/pontos-coleta")
                .then()
                .statusCode(201)
                .extract().response();

        pontoColetaId = createResponse.jsonPath().getLong("id");
    }

    @Dado("que existem pontos de coleta cadastrados no sistema")
    public void queExistemPontosDeColetaCadastradosNoSistema() {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Ponto Teste Ativo");
        body.put("tipoResiduo", "METAL");
        body.put("endereco", "Av. Sustentável, 500");
        body.put("capacidadeKg", 200.0);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/pontos-coleta")
                .then()
                .statusCode(201);
    }

    @Quando("envio uma requisição POST para {string}")
    public void envioUmaRequisicaoPOST(String endpoint) {
        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .extract().response();
    }

    @Quando("envio uma requisição GET para {string}")
    public void envioUmaRequisicaoGET(String endpoint) {
        String resolvedEndpoint = endpoint.replace("{id}",
                pontoColetaId != null ? pontoColetaId.toString() : "0");
        response = given()
                .when()
                .get(resolvedEndpoint)
                .then()
                .extract().response();
    }

    @Então("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int expectedStatus) {
        assertThat(response.statusCode())
                .as("Status HTTP esperado: %d, obtido: %d", expectedStatus, response.statusCode())
                .isEqualTo(expectedStatus);
    }

    @Então("o corpo da resposta deve conter o campo {string} não nulo")
    public void oCorpoDaRespostaDeveConterCampoNaoNulo(String campo) {
        Object valor = response.jsonPath().get(campo);
        assertThat(valor)
                .as("Campo '%s' deveria estar presente e não nulo", campo)
                .isNotNull();
    }

    @Então("o corpo da resposta deve conter {string} com valor {string}")
    public void oCorpoDaRespostaDeveConterComValor(String campo, String valorEsperado) {
        String valor = response.jsonPath().getString(campo);
        assertThat(valor)
                .as("Campo '%s' deveria ser '%s' mas foi '%s'", campo, valorEsperado, valor)
                .isEqualTo(valorEsperado);
    }

    @Então("o corpo da resposta deve conter o campo {string} com valor {string}")
    public void oCorpoDaRespostaDeveConterOCampoComValor(String campo, String valorEsperado) {
        String valor = response.jsonPath().getString(campo);
        assertThat(valor)
                .as("Campo '%s' deveria ser '%s'", campo, valorEsperado)
                .isEqualTo(valorEsperado);
    }

    @Então("o corpo da resposta deve conter o campo {string} com erros de validação")
    public void oCorpoDaRespostaDeveConterErrosDeValidacao(String campo) {
        List<?> mensagens = response.jsonPath().getList(campo);
        assertThat(mensagens)
                .as("Campo '%s' deveria conter erros de validação", campo)
                .isNotNull()
                .isNotEmpty();
    }

    @Então("o corpo da resposta deve ser uma lista")
    public void oCorpoDaRespostaDeveSerUmaLista() {
        List<?> lista = response.jsonPath().getList("$");
        assertThat(lista).isNotNull();
    }

    @Então("todos os itens da lista devem ter {string} igual a {string}")
    public void todosOsItensDaListaDevemTer(String campo, String valorEsperado) {
        List<String> valores = response.jsonPath().getList(campo);
        if (valores != null && !valores.isEmpty()) {
            assertThat(valores).allSatisfy(v ->
                    assertThat(v).isEqualTo(valorEsperado));
        }
    }
}
