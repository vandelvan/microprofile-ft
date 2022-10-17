package com.vandelvan.controller;

import com.vandelvan.model.Person;

import org.eclipse.microprofile.faulttolerance.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {
    List<Person> personList = new ArrayList<>();
    Logger LOGGER = Logger.getLogger("logger");

    @GET
    @Timeout(value = 5000L)
    @Retry(maxRetries = 4)
    @CircuitBreaker(failureRatio = 01, delay = 15000L)
    @Bulkhead(value = 1)
    @Fallback(fallbackMethod = "getPersonFallbackList")
    public List<Person> getPersonList() {
        LOGGER.info("Ejecutando getPersonList");
        doWait();
        doFail();
        return this.personList;
    }

    public List<Person> getPersonFallbackList() {
        Person person = new Person(-1L, "Ivan", "jose.otorrez@alumnos.udg.mx");
        return List.of(person);
    }

    public void doFail() {
        var random = new Random();
        if (random.nextBoolean()) {
            LOGGER.warning("Fallo producido");
            throw new RuntimeException("Falla implementacion");
        }
    }

    public void doWait() {
        var random = new Random();
        try {
            Thread.sleep(random.nextInt(10) + 1 * 1000L);
        } catch (Exception e) {
        }
    }
}
