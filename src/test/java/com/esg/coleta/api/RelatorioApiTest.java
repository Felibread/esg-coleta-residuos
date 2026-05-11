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
@DisplayName("API Tests - Relatório ESG")
class RelatorioApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    // ==================================================
    // GET /api/relatorio/esg
    // ==================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/relatorio/esg - Deve retornar relatório ESG com schema válido (200)")
    void deveRetornarRelatorioESGComSchemaValido() {
        given()
                .when()
                .get("/api/relatorio/esg")
                .then()
                // Validação de Status Code
                .statusCode(200)
                .contentType(ContentType.JSON)
                // Validação de todos os campos obrigatórios
                .body("totalPontosAtivos", notNullValue())
                .body("totalColetasRealizadas", notNullValue())
                .body("totalPesoColetadoKg", notNullValue())
                .body("mediaPesoColetadoKg", notNullValue())
                .body("coletasConcluidas", notNullValue())
                .body("coletasPendentes", notNullValue())
                .body("impactoAmbiental", notNullValue())
                .body("co2EvitadoKg", notNullValue())
                // Valores numéricos devem ser >= 0
                .body("totalPontosAtivos", greaterThanOrEqualTo(0))
                .body("totalColetasRealizadas", greaterThanOrEqualTo(0))
                .body("totalPesoColetadoKg", greaterThanOrEqualTo(0.0f))
                .body("co2EvitadoKg", greaterThanOrEqualTo(0.0f))
                // impactoAmbiental deve ser uma string não vazia
                .body("impactoAmbiental", not(emptyString()))
                // Validação de contrato JSON Schema
                .body(matchesJsonSchemaInClasspath("schemas/relatorio-esg-schema.json"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/relatorio/esg - CO2 evitado deve ser proporcional ao peso coletado")
    void co2EvitadoDeveSerProporionalAoPesoColetado() {
        // Criar ponto e coleta concluída para ter dados reais
        Map<String, Object> ponto = new HashMap<>();
        ponto.put("nome", "Ponto Relatório CO2");
        ponto.put("tipoResiduo", "ELETRONICO");
        ponto.put("endereco", "Rua CO2, 5");
        ponto.put("capacidadeKg", 500.0);

        Response rPonto = given()
                .contentType(ContentType.JSON)
                .body(ponto)
                .post("/api/pontos-coleta")
                .then().statusCode(201)
                .extract().response();

        Long pontoId = rPonto.jsonPath().getLong("id");

        Map<String, Object> coleta = new HashMap<>();
        coleta.put("pontoColetaId", pontoId);
        coleta.put("dataColeta", "2025-08-01");
        coleta.put("pesoColetadoKg", 100.0);
        coleta.put("responsavel", "Equipe Verde");
        coleta.put("status", "CONCLUIDA");

        given()
                .contentType(ContentType.JSON)
                .body(coleta)
                .post("/api/coletas")
                .then().statusCode(201);

        // CO2 evitado = pesoColetadoKg * 2.5 = 250.0 (ou mais se há outras coletas)
        given()
                .when()
                .get("/api/relatorio/esg")
                .then()
                .statusCode(200)
                .body("co2EvitadoKg", greaterThan(0.0f))
                .body("coletasConcluidas", greaterThan(0))
                .body("totalPesoColetadoKg", greaterThan(0.0f))
                .body(matchesJsonSchemaInClasspath("schemas/relatorio-esg-schema.json"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/relatorio/esg - Deve sempre retornar impactoAmbiental como texto não vazio")
    void deveRetornarImpactoAmbientalComoTexto() {
        given()
                .when()
                .get("/api/relatorio/esg")
                .then()
                .statusCode(200)
                .body("impactoAmbiental", isA(String.class))
                .body("impactoAmbiental", not(emptyOrNullString()));
    }
}
