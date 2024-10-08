package com.LucasH.park_api;

import com.LucasH.park_api.web.dto.EstacionamentoCreateDto;
import com.LucasH.park_api.web.dto.PageableDto;
import com.LucasH.park_api.web.exeception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/estacionamentos/estacionamentos-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/estacionamentos/estacionamentos-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EstacionamentoIT {
    @Autowired
    WebTestClient testClient;

    @Test
    public void CriarCheckin_ComDadosValidos_RetornarCreateAndLocationStatus201() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WSP-4569")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("VERMELHO")
                .clienteCpf("38352600060")
                .build();

        testClient
                .post()
                .uri("api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("placa").isEqualTo("WSP-4569")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO 1.0")
                .jsonPath("cor").isEqualTo("VERMELHO")
                .jsonPath("clienteCpf").isEqualTo("38352600060")
                .jsonPath("recibo").exists()
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void CriarCheckin_ComRoleUser_RetornarErrorMensageStatus403() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WEP-4658")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("VERMELHO")
                .clienteCpf("38352600060")
                .build();

        testClient
                .post()
                .uri("api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456" ))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void CriarCheckin_ComDadosInvalidos_RetornarErrorMensageStatus422() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("")
                .marca("")
                .modelo("")
                .cor("")
                .clienteCpf("")
                .build();

        testClient
                .post()
                .uri("api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456" ))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void CriarCheckin_ComCPFInexistente_RetornarErrorMensageStatus404() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WSP-4569")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("VERMELHO")
                .clienteCpf("98598204064")
                .build();

        testClient
                .post()
                .uri("api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound() // 404
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    @Sql(scripts = "/sql/estacionamentos/estacionamento-insert-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/estacionamentos/estacionamento-delete-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void CriarCheckin_ComVagaOcupada_RetornarErrorMensageStatus404() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WSP-4569")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("VERMELHO")
                .clienteCpf("38352600060")
                .build();

        testClient
                .post()
                .uri("api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound() // 404
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void BuscarPorRecibo_ComReciboValido_RetornarEstacionamentoResponseDtoStatus200() {

        testClient
                .get()
                .uri("api/v1/estacionamentos/check-in/{recibo}" ,"20241001-141519")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isOk() // 404
                .expectBody()
                .jsonPath("placa").isEqualTo("ASD-8564")
                .jsonPath("marca").isEqualTo("Honda")
                .jsonPath("modelo").isEqualTo("Civic Sport")
                .jsonPath("cor").isEqualTo("Branco")
                .jsonPath("clienteCpf").isEqualTo("38352600060")
                .jsonPath("recibo").isEqualTo("20241001-141519")
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void BuscarPorRecibo_ComRoleCliente_RetornarEstacionamentoResponseDtoStatus200() {

        testClient
                .get()
                .uri("api/v1/estacionamentos/check-in/{recibo}" ,"20241001-141519")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isOk() // 404
                .expectBody()
                .jsonPath("placa").isEqualTo("ASD-8564")
                .jsonPath("marca").isEqualTo("Honda")
                .jsonPath("modelo").isEqualTo("Civic Sport")
                .jsonPath("cor").isEqualTo("Branco")
                .jsonPath("clienteCpf").isEqualTo("38352600060")
                .jsonPath("recibo").isEqualTo("20241001-141519")
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void BuscarPorRecibo_ComReciboInexistente_RetornarErrorMensageStatus404() {

        testClient
                .get()
                .uri("api/v1/estacionamentos/check-in/{recibo}" ,"20241001-222222")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isNotFound() // 404
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in/20241001-222222")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void checkOut_ComReciboExistente_RetornarEstacionamentoResponseDtoStatus200() {

        testClient
                .post()
                .uri("api/v1/estacionamentos/check-out/{recibo}" ,"20241001-141519")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isOk() // 200
                .expectBody()
                .jsonPath("placa").isEqualTo("ASD-8564")
                .jsonPath("marca").isEqualTo("Honda")
                .jsonPath("modelo").isEqualTo("Civic Sport")
                .jsonPath("cor").isEqualTo("Branco")
                .jsonPath("clienteCpf").isEqualTo("38352600060")
                .jsonPath("valor").isNumber();
    }



    @Test
    public void checkOut_ComReciboInexistente_RetornarErrorMensageStatus404() {
        testClient
                .post()
                .uri("api/v1/estacionamentos/check-out/{recibo}" ,"20241001-111111")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20241001-111111")
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("status").isEqualTo("404");
    }

    @Test
    public void checkOut_ComRoleUser_RetornarErrorMensageStatus403() {
        testClient
                .post()
                .uri("api/v1/estacionamentos/check-out/{recibo}" ,"20241001-141519")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20241001-141519")
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("status").isEqualTo("403")
                .jsonPath("statusText").isEqualTo("Forbidden")
                .jsonPath("message").isEqualTo("Access Denied");
    }

    @Test
    public void BuscarEstacionamentos_PorClienteCpf_RetornarSucessoStatus200() {
      PageableDto responseBody = testClient
                .get()
                .uri("api/v1/estacionamentos/cpf/{cpf}?size=1&page=0" ,"38352600060")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
              .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent()).size().isEqualTo(1);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = testClient
                .get()
                .uri("api/v1/estacionamentos/cpf/{cpf}?size=1&page=1" ,"38352600060")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent()).size().isEqualTo(1);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void buscarEstacionamento_PorClienteCpfComRoleUser_RetornarErrorMensageStatus403() {
        testClient
                .get()
                .uri("api/v1/estacionamentos/cpf/{cpf}" ,"38352600060")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/cpf/38352600060")
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("status").isEqualTo("403")
                .jsonPath("statusText").isEqualTo("Forbidden")
                .jsonPath("message").isEqualTo("Access Denied");
    }


    @Test
    public void buscarEstacionamento_PorClienteAutenticado_retornarPageableDtoStatus200() {
        PageableDto responseBody = testClient
                .get()
                .uri("api/v1/estacionamentos")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"raissa@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent()).size().isEqualTo(2);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(responseBody.getSize()).isEqualTo(5);
        Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void buscarEstacionamento_PorRoleAdmin_retornarErrorMensageStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("api/v1/estacionamentos")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"admin@gmail.com", "123456" ))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
        Assertions.assertThat(responseBody.getStatusText()).isEqualTo("Forbidden");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("Access Denied");

    }



}
