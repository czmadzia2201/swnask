package com.nask.swapireader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nask.model.Person;
import com.nask.model.Planet;
import com.nask.model.Starship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CacheBuilderTest {

    @InjectMocks
    private CacheBuilder cacheBuilder;

    @Mock
    private EntityRetriever entityRetriever;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void before() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(entityRetriever.getAll("people")).thenReturn(getMocked("people"));
        Mockito.when(entityRetriever.getAll("planets")).thenReturn(getMocked("planets"));
        Mockito.when(entityRetriever.getAll("starships")).thenReturn(getMocked("starships"));
    }

    @Test
    public void testGetPeople() throws IOException {
        NavigableMap<Integer, Person> people = cacheBuilder.getPeople();
        assertEquals(getMocked("people").size(), people.size());
        assertEquals(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10}), new ArrayList(people.keySet()));
        people.values().forEach(v -> assertTrue(v instanceof Person));
    }

    @Test
    public void testGetIdList() {
        assertEquals(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10}), cacheBuilder.getIdList());
    }

    @Test
    public void testConvertPerson() throws IOException {
        Person person = cacheBuilder.convertPerson(entityRetriever.getAll("people").get(0), cacheBuilder.convertPlanets(), cacheBuilder.convertStarships());
        assertEquals(1, person.getId());
        assertEquals("Tatooine", person.getHomeworld().getName());
        assertEquals(Arrays.asList(new String[]{"X-wing", "Imperial shuttle"}), person.getStarships().stream().map(el -> el.getName()).collect(Collectors.toList()));
        assertEquals(null, person.getErrors());
    }

    @Test
    public void testConvertPersonWithNullValues() throws IOException {
        Person person = cacheBuilder.convertPerson(entityRetriever.getAll("people").get(9), cacheBuilder.convertPlanets(), cacheBuilder.convertStarships());
        assertEquals(10, person.getId());
        assertEquals(null, person.getHomeworld());
        assertEquals(Arrays.asList(new String[]{"Naboo star skiff", "Jedi Interceptor"}), person.getStarships().stream().map(el -> el.getName()).collect(Collectors.toList()));
        assertEquals(4, person.getErrors().size());
    }

    @Test
    public void testConvertPlanets() throws IOException {
        Map<String, Planet> planets = cacheBuilder.convertPlanets();
        assertEquals(getMocked("planets").size(), planets.size());
        planets.keySet().forEach(k -> assertTrue(k.contains("swapi.dev/api/planets")));
        planets.values().forEach(v -> assertTrue(v instanceof Planet));
    }

    @Test
    public void testConvertStarships() throws IOException {
        Map<String, Starship> starships = cacheBuilder.convertStarships();
        assertEquals(getMocked("starships").size(), starships.size());
        starships.keySet().forEach(k -> assertTrue(k.contains("swapi.dev/api/starships")));
        starships.values().forEach(v -> assertTrue(v instanceof Starship));
    }

    private List<Map<String, Object>> getMocked(String type) throws IOException {
        return objectMapper.readValue(new File(String.format("src/test/resources/%s.json", type)), List.class);
    }

}