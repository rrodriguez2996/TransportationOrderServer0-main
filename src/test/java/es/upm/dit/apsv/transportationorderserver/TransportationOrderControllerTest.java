package es.upm.dit.apsv.transportationorderserver;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import es.upm.dit.apsv.transportationorderserver.repository.TransportationOrderRepository;
import es.upm.dit.apsv.transportationorderserver.controller.TransportationOrderController;
import es.upm.dit.apsv.transportationorderserver.model.TransportationOrder;

@WebMvcTest(TransportationOrderController.class)
public class TransportationOrderControllerTest {

    // Clase bajo prueba (el controlador)
    @InjectMocks
    private TransportationOrderController business;

    // Mock del repositorio (NO usa la BD real)
    @MockBean
    private TransportationOrderRepository repository;

    // Herramienta para simular peticiones HTTP al controlador
    @Autowired
    private MockMvc mockMvc;

    // =====================================================
    // 1) TEST GET /transportationorders -> lista completa
    // =====================================================

    @Test
    public void testGetOrders() throws Exception {

        // 1. Configuramos el mock: cuando findAll() sea llamado,
        // devolverá la lista cargada de orders.json
        when(repository.findAll()).thenReturn(getAllTestOrders());

        // 2. Construimos la petición GET /transportationorders
        RequestBuilder request = MockMvcRequestBuilders
                .get("/transportationorders")
                .accept(MediaType.APPLICATION_JSON);

        // 3. Ejecutamos la petición y comprobamos:
        // - status 200 OK
        // - el JSON raíz es una lista de tamaño 20
        // (ajusta 20 al número real de líneas en orders.json)
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(20)))
                .andReturn();
    }

    // =====================================================
    // 2) TEST GET /transportationorders/{truck}
    // - Caso feliz: truck "8962ZKR" existe -> 200
    // - Caso alternativo: truck NOEXISTE -> 404
    // =====================================================

    @Test
    public void testGetOrder() throws Exception {

        // ----------- CASO 1: EXISTE -----------

        when(repository.findById("8962ZKR")).thenReturn(Optional.of(
                new TransportationOrder(
                        "28",
                        "8962ZKR",
                        1591682400000L,
                        40.4562191, -3.8707211,
                        1591692196000L,
                        42.0206372, -4.5330132,
                        0,
                        0.0,
                        0.0,
                        0)));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/transportationorders/8962ZKR")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.truck", is("8962ZKR")))
                .andExpect(jsonPath("$.toid", is("28"))); // ← aquí el cambio

        // ----------- CASO 2: NO EXISTE -----------

        when(repository.findById("NOEXISTE")).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/transportationorders/NOEXISTE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // =====================================================
    // Método auxiliar: carga todas las órdenes desde orders.json
    // =====================================================

    private List<TransportationOrder> getAllTestOrders() {

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<TransportationOrder> orders = new ArrayList<>();
        TransportationOrder order = null;

        try (BufferedReader br = new BufferedReader(new FileReader(
                new ClassPathResource("orders.json").getFile()))) {

            for (String line; (line = br.readLine()) != null;) {
                order = objectMapper.readValue(line, TransportationOrder.class);
                orders.add(order);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return orders;
    }
}
