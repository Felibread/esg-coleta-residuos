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
@DisplayName("API Tests - Coletas de Resíduos")
class ColetaApiTest {

    @LocalServerPort
    private int port;

    private static Long pontoColetaId;
    private static Long coletaId;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeEach
    void criarPontoDeColetaBase() {
        if (pontoColetaId == null) {
            Map<String, Object> ponto = new HashMap<>();
            ponto.put("nome", "Ponto Base Coleta Test");
            ponto.put("tipoResiduo", "PAPEL");
            ponto.put("endereco", "Rua ESG, 10");
            ponto.put("capacidadeKg", 700.0);

            Response r = given()
                    .contentType(ContentType.JSON)
                    .body(ponto)
                    .post("/api/pontos-coleta")
                    .then()
                    .statusCode(201)
                    .extract().response();

            pontoColetaId = r.jsonPath().getLong("id");
        }
    }

    private Map<String, Object> buildColetaValida() {
        Map<String, Object> body = new HashMap<>();
        body.put("pontoColetaId", pontoColetaId);
        body.put("dataColeta", "2025-07-15");
        body.put("pesoColetadoKg", 250.0);
        body.put("responsavel", "Carlos Ambiental");
        body.put("status", "AGENDADA");
        body.put("observacoes", "Coleta programada ESG");
        return body;
    }

    // ==================================================
    // POST /api/coletas
    // ==================================================

    @Test
    @Order(1)
    @DisplayName("POST /api/coletas - Deve registrar coleta com sucesso (201)")
    void deveRegistrarColetaComSucesso() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(buildColetaValida())
                .when()
                .post("/api/coletas")
                .then()
                // Validação Status Code
                .statusCode(201)
                // Validação do corpo
                .body("id", notNullValue())
                .body("pontoColetaId", equalTo(pontoColetaId.intValue()))
                .body("pesoColetadoKg", equalTo(250.0f))
                .body("responsavel", equalTo("Carlos Ambiental"))
                .body("status", equalTo("AGENDADA"))
                .body("tipoResiduo", equalTo("PAPEL"))
                // Validação de contrato JSON Schema
                .body(matchesJsonSchemaInClasspath("schemas/coleta-schema.json"))
                .extract().response();

        coletaId = response.jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/coletas - Deve retornar 400 para peso negativo")
    void deveRetornar400ParaPesoNegativo() {
        Map<String, Object> body = buildColetaValida();
        body.put("pesoColetadoKg", -10.0);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/coletas")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("erro", equalTo("Dados inválidos"))
                .body("mensagens", not(empty()));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/coletas - Deve retornar 400 para responsável em branco")
    void deveRetornar400ParaResponsavelEmBranco() {
        Map<String, Object> body = buildColetaValida();
        body.put("responsavel", "");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/coletas")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Dados inválidos"));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/coletas - Deve retornar 404 para ponto de coleta inexistente")
    void deveRetornar404ParaPontoInexistente() {
        Map<String, Object> body = buildColetaValida();
        body.put("pontoColetaId", 999999L);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/coletas")
                .then()
                .statusCode(404)
                .body("erro", equalTo("Recurso não encontrado"));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/coletas - Deve retornar 400 para data nula")
    void deveRetornar400ParaDataNula() {
        Map<String, Object> body = buildColetaValida();
        body.remove("dataColeta");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/coletas")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Dados inválidos"));
    }

    // ==================================================
    // GET /api/coletas
    // ==================================================

    @Test
    @Order(6)
    @DisplayName("GET /api/coletas - Deve listar todas as coletas (200)")
    void deveListarTodasAsColetas() {
        given()
                .when()
                .get("/api/coletas")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/coletas?pontoColetaId={id} - Deve filtrar coletas por ponto")
    void deveFiltrarColetasPorPonto() {
        given()
                .queryParam("pontoColetaId", pontoColetaId)
                .when()
                .get("/api/coletas")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("pontoColetaId", everyItem(equalTo(pontoColetaId.intValue())));
    }

    // ==================================================
    // GET /api/coletas/{id}
    // ==================================================

    @Test
    @Order(8)
    @DisplayName("GET /api/coletas/{id} - Deve retornar coleta existente com schema válido")
    void deveRetornarColetaExistente() {
        Assumptions.assumeTrue(coletaId != null, "ID da coleta não disponível");

        given()
                .when()
                .get("/api/coletas/" + coletaId)
                .then()
                .statusCode(200)
                .body("id", equalTo(coletaId.intValue()))
                // Validação de contrato JSON Schema
                .body(matchesJsonSchemaInClasspath("schemas/coleta-schema.json"));
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/coletas/{id} - Deve retornar 404 para ID inexistente")
    void deveRetornar404ParaColetaInexistente() {
        given()
                .when()
                .get("/api/coletas/999999")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("erro", equalTo("Recurso não encontrado"));
    }

    // ==================================================
    // PATCH /api/coletas/{id}/status
    // ==================================================

    @Test
    @Order(10)
    @DisplayName("PATCH /api/coletas/{id}/status - Deve atualizar status para CONCLUIDA")
    void deveAtualizarStatusParaConcluida() {
        Assumptions.assumeTrue(coletaId != null, "ID da coleta não disponível");

        given()
                .queryParam("status", "CONCLUIDA")
                .when()
                .patch("/api/coletas/" + coletaId + "/status")
                .then()
                .statusCode(200)
                .body("status", equalTo("CONCLUIDA"))
                .body(matchesJsonSchemaInClasspath("schemas/coleta-schema.json"));
    }

    @Test
    @Order(11)
    @DisplayName("PATCH /api/coletas/{id}/status - Deve retornar 404 para ID inexistente")
    void deveRetornar404AoAtualizarStatusDeColetaInexistente() {
        given()
                .queryParam("status", "CONCLUIDA")
                .when()
                .patch("/api/coletas/999999/status")
                .then()
                .statusCode(404);
    }

    // ==================================================
    // DELETE /api/coletas/{id}
    // ==================================================

    @Test
    @Order(12)
    @DisplayName("DELETE /api/coletas/{id} - Deve deletar coleta existente (204)")
    void deveDeletarColetaExistente() {
        Response r = given()
                .contentType(ContentType.JSON)
                .body(buildColetaValida())
                .post("/api/coletas")
                .then()
                .statusCode(201)
                .extract().response();

        Long id = r.jsonPath().getLong("id");

        given()
                .when()
                .delete("/api/coletas/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/coletas/" + id)
                .then()
                .statusCode(404);
    }
}
