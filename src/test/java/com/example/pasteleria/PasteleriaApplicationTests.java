package com.example.pasteleria;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:pasteleria_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PasteleriaApplicationTests {

    private static final String PRODUCTO_NUEVO = """
            {"nombre":"Cupcake Test","descripcion":"x","precio":2000,"stock":5,"stockCritico":1,
             "oferta":0,"destacado":false,"categoria":{"idCategoria":1}}""";

    @Autowired
    private WebApplicationContext contexto;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    }

    private String login(String correo, String clave) throws Exception {
        String cuerpo = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"" + correo + "\",\"clave\":\"" + clave + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.token");
    }

    @Test
    void catalogoEsPublico() throws Exception {
        mvc.perform(get("/producto")).andExpect(status().isOk());
        mvc.perform(get("/categoria")).andExpect(status().isOk());
    }

    @Test
    void swaggerEsPublico() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void escrituraSinTokenDa401() throws Exception {
        mvc.perform(post("/producto").contentType(MediaType.APPLICATION_JSON).content(PRODUCTO_NUEVO))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/producto/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoDa401() throws Exception {
        mvc.perform(post("/producto").header("Authorization", "Bearer token.falso.xyz")
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO_NUEVO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConClaveIncorrectaDa401() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@milsabores.cl\",\"clave\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clienteNoPuedeEscribirCatalogoPeroAdminSi() throws Exception {
        String cliente = login("camila@duocuc.cl", "camila123");
        mvc.perform(post("/producto").header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO_NUEVO))
                .andExpect(status().isForbidden());

        String admin = login("admin@milsabores.cl", "admin123");
        mvc.perform(post("/producto").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO_NUEVO))
                .andExpect(status().isCreated());
    }

    @Test
    void soloAdminAccedeAUsuarios() throws Exception {
        String cliente = login("camila@duocuc.cl", "camila123");
        mvc.perform(get("/usuario").header("Authorization", "Bearer " + cliente)).andExpect(status().isForbidden());

        String admin = login("admin@milsabores.cl", "admin123");
        mvc.perform(get("/usuario").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clave").doesNotExist()); 
    }

    @Test
    void usuarioMeDevuelveElRolDelToken() throws Exception {
        String admin = login("admin@milsabores.cl", "admin123");
        mvc.perform(get("/usuario/me").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("admin"));
    }

    @Test
    void registroCreaClienteYNuncaAdmin() throws Exception {
        mvc.perform(post("/auth/registro").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nombre":"Ana","apellidos":"Lopez","correo":"ana.test@correo.cl","clave":"secreta1",
                         "nacimiento":"1990-01-01","rol":"ADMIN"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.rol").value("cliente"));
        // correo duplicado
        mvc.perform(post("/auth/registro").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nombre":"Ana","apellidos":"Lopez","correo":"ana.test@correo.cl","clave":"secreta1","nacimiento":"1990-01-01"}"""))
                .andExpect(status().isConflict());
    }

    @Test
    void refreshTokenEmiteNuevoAccessToken() throws Exception {
        String cuerpo = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"camila@duocuc.cl\",\"clave\":\"camila123\"}"))
                .andReturn().getResponse().getContentAsString();
        String access = JsonPath.read(cuerpo, "$.token");
        String refresh = JsonPath.read(cuerpo, "$.refreshToken");

        mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refresh + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + access + "\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/usuario/me").header("Authorization", "Bearer " + refresh))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ordenCalculaTotalesDescuentaStockYValida() throws Exception {
        String cliente = login("camila@duocuc.cl", "camila123");
        mvc.perform(post("/orden").header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoEntrega\":\"retiro\",\"items\":[{\"productoId\":5,\"cantidad\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(8500))
                .andExpect(jsonPath("$.estado").value("pagada"));

        String producto = mvc.perform(get("/producto/5")).andReturn().getResponse().getContentAsString();
        Integer stock = JsonPath.read(producto, "$.stock");
        assertNotEquals(30, stock);

        mvc.perform(post("/orden").header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoEntrega\":\"retiro\",\"items\":[{\"productoId\":5,\"cantidad\":9999}]}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/orden").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoEntrega\":\"retiro\",\"items\":[{\"productoId\":5,\"cantidad\":1}]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void descuentoDelClienteSeAplicaEnElBackend() throws Exception {
        String hernan = login("hernan@correo.cl", "hernan123"); 
        mvc.perform(post("/orden").header("Authorization", "Bearer " + hernan)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoEntrega\":\"delivery\",\"items\":[{\"productoId\":3,\"cantidad\":1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subtotal").value(40000))
                .andExpect(jsonPath("$.descuento").value(20000))
                .andExpect(jsonPath("$.total").value(20000));
    }

    @Test
    void soloAdminListaTodasLasOrdenes() throws Exception {
        String cliente = login("camila@duocuc.cl", "camila123");
        mvc.perform(get("/orden").header("Authorization", "Bearer " + cliente)).andExpect(status().isForbidden());
        mvc.perform(get("/orden/mis-ordenes").header("Authorization", "Bearer " + cliente)).andExpect(status().isOk());
        String admin = login("admin@milsabores.cl", "admin123");
        mvc.perform(get("/orden").header("Authorization", "Bearer " + admin)).andExpect(status().isOk());
    }
}
