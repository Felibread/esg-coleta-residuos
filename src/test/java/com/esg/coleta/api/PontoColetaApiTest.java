package com.esg.coleta.api;

import com.esg.coleta.EsgColetaApplication;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
    classes = EsgColetaApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("API Tests - Pontos de Coleta")
class PontoColetaApiTest {

    @LocalServerPort
    private int port;

    private static Long pontoIdCriado;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private Map<String, Object> buildPontoValido() {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Ecoponto API Test");
        body.put("tipoResiduo", "PLASTICO");
        body.put("endereco", "Rua dos Testes, 200");
        body.put("capacidadeKg", 400.0);
        return body;
    }

    // ==================================================
    // POST /api/pontos-coleta
    // ==================================================

    @Test
    @Order(1)
    @DisplayName("POST /api/pontos-coleta - Deve criar ponto com sucesso (201)")
    void deveCriarPontoDeColetaComSucesso() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(buildPontoValido())
                .when()
                .post("/api/pontos-coleta")
                .then()
                // Validação de Status Code
                .statusCode(201)
                // Validação do corpo JSON
                .body("id", notNullValue())
                .body("nome", equalTo("Ecoponto API Test"))
                .body("tipoResiduo", equalTo("PLASTICO"))
                .body("endereco", equalTo("Rua dos Testes, 200"))
                .body("capacidadeKg", equalTo(400.0f))
                .body("ativo", equalTo(true))
                // Validação de contrato via JSON Schema
                .body(matchesJsonSchemaInClasspath("schemas/ponto-coleta-schema.json"))
                .extract().response();

        pontoIdCriado = response.jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/pontos-coleta - Deve retornar 400 para nome em branco")
    void deveRetornar400ParaNomeEmBranco() {
        Map<String, Object> body = buildPontoValido();
        body.put("nome", "");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/pontos-coleta")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("erro", equalTo("Dados inválidos"))
                .body("mensagens", not(empty()))
                .body("timestamp", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/pontos-coleta - Deve retornar 400 para capacidade negativa")
    void deveRetornar400ParaCapacidadeNegativa() {
        Map<String, Object> body = buildPontoValido();
        body.put("capacidadeKg", -50.0);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/pontos-coleta")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("erro", equalTo("Dados inválidos"))
                .body("mensagens", hasItem(containsString("positiv")));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/pontos-coleta - Deve retornar 400 para tipoResiduo nulo")
    void deveRetornar400ParaTipoResiduoNulo() {
        Map<String, Object> body = buildPontoValido();
        body.remove("tipoResiduo");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/pontos-coleta")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Dados inválidos"));
    }

    // ==================================================
    // GET /api/pontos-coleta
    // ==================================================

    @Test
    @Order(5)
    @DisplayName("GET /api/pontos-coleta - Deve listar todos os pontos (200)")
    void deveListarTodosPontosDeColeta() {
        given()
                .when()
                .get("/api/pontos-coleta")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/pontos-coleta?apenasAtivos=true - Deve listar apenas ativos")
    void deveListarApenasAtivos() {
        given()
                .queryParam("apenasAtivos", true)
                .when()
                .get("/api/pontos-coleta")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("ativo", everyItem(equalTo(true)));
    }

    // ==================================================
    // GET /api/pontos-coleta/{id}
    // ==================================================

    @Test
    @Order(7)
    @DisplayName("GET /api/pontos-coleta/{id} - Deve retornar ponto existente (200)")
    void deveRetornarPontoExistente() {
        Assumptions.assumeTrue(pontoIdCriado != null, "ID do ponto não disponível");

        given()
                .when()
                .get("/api/pontos-coleta/" + pontoIdCriado)
                .then()
                .statusCode(200)
                .body("id", equalTo(pontoIdCriado.intValue()))
                .body("nome", equalTo("Ecoponto API Test"))
                // Validação de contrato JSON Schema
                .body(matchesJsonSchemaInClasspath("schemas/ponto-coleta-schema.json"));
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/pontos-coleta/{id} - Deve retornar 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() {
        given()
                .when()
                .get("/api/pontos-coleta/999999")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("erro", equalTo("Recurso não encontrado"))
                .body("timestamp", notNullValue());
    }

    // ==================================================
    // PUT /api/pontos-coleta/{id}
    // ==================================================

    @Test
    @Order(9)
    @DisplayName("PUT /api/pontos-coleta/{id} - Deve atualizar ponto existente (200)")
    void deveAtualizarPontoExistente() {
        Assumptions.assumeTrue(pontoIdCriado != null, "ID do ponto não disponível");

        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Ecoponto Atualizado");
        body.put("tipoResiduo", "METAL");
        body.put("endereco", "Rua Atualizada, 300");
        body.put("capacidadeKg", 600.0);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/pontos-coleta/" + pontoIdCriado)
                .then()
                .statusCode(200)
                .body("nome", equalTo("Ecoponto Atualizado"))
                .body("tipoResiduo", equalTo("METAL"))
                .body(matchesJsonSchemaInClasspath("schemas/ponto-coleta-schema.json"));
    }

    @Test
    @Order(10)
    @DisplayName("PUT /api/pontos-coleta/{id} - Deve retornar 404 para ID inexistente")
    void deveRetornar404AoAtualizarIdInexistente() {
        given()
                .contentType(ContentType.JSON)
                .body(buildPontoValido())
                .when()
                .put("/api/pontos-coleta/999999")
                .then()
                .statusCode(404);
    }

    // ==================================================
    // PATCH /api/pontos-coleta/{id}/desativar
    // ==================================================

    @Test
    @Order(11)
    @DisplayName("PATCH /api/pontos-coleta/{id}/desativar - Deve desativar ponto (204)")
    void deveDesativarPonto() {
        // Criar ponto específico para desativar
        Response r = given()
                .contentType(ContentType.JSON)
                .body(buildPontoValido())
                .post("/api/pontos-coleta")
                .then()
                .statusCode(201)
                .extract().response();

        Long id = r.jsonPath().getLong("id");

        given()
                .when()
                .patch("/api/pontos-coleta/" + id + "/desativar")
                .then()
                .statusCode(204);

        // Verificar se foi realmente desativado
        given()
                .when()
                .get("/api/pontos-coleta/" + id)
                .then()
                .statusCode(200)
                .body("ativo", equalTo(false));
    }

    // ==================================================
    // DELETE /api/pontos-coleta/{id}
    // ==================================================

    @Test
    @Order(12)
    @DisplayName("DELETE /api/pontos-coleta/{id} - Deve deletar ponto existente (204)")
    void deveDeletarPontoExistente() {
        Response r = given()
                .contentType(ContentType.JSON)
                .body(buildPontoValido())
                .post("/api/pontos-coleta")
                .then()
                .statusCode(201)
                .extract().response();

        Long id = r.jsonPath().getLong("id");

        given()
                .when()
                .delete("/api/pontos-coleta/" + id)
                .then()
                .statusCode(204);

        // Verificar se foi deletado
        given()
                .when()
                .get("/api/pontos-coleta/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(13)
    @DisplayName("DELETE /api/pontos-coleta/{id} - Deve retornar 404 para ID inexistente")
    void deveRetornar404AoDeletarIdInexistente() {
        given()
                .when()
                .delete("/api/pontos-coleta/999999")
                .then()
                .statusCode(404);
    }
}
