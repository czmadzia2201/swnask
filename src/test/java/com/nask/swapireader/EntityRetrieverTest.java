package com.nask.swapireader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EntityRetrieverTest {

    @InjectMocks
    private EntityRetriever entityRetriever;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void before() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(restTemplate.getForObject("https://swapi.dev/api/mockResponse/?page=1", SwapiResponse.class)).thenReturn(getMockResponse("page1"));
        Mockito.when(restTemplate.getForObject("https://swapi.dev/api/mockResponse/?page=2", SwapiResponse.class)).thenReturn(getMockResponse("page2"));
        Mockito.when(restTemplate.getForObject("https://swapi.dev/api/mockResponse/?page=3", SwapiResponse.class)).thenReturn(getMockResponse("page3"));
    }

    @Test
    public void testGetAll() {
        List<Map<String, Object>> allPeople = entityRetriever.getAll("mockResponse");
        List<String> names = allPeople.stream().map(el -> (String) el.get("name")).collect(Collectors.toList());
        List<String> heights = allPeople.stream().map(el -> (String) el.get("height")).collect(Collectors.toList());
        List<String> masses = allPeople.stream().map(el -> (String) el.get("mass")).collect(Collectors.toList());
        assertEquals(6, allPeople.size());
        assertEquals(Arrays.asList(new String[]{"Luke Skywalker", "C-3PO", "Anakin Skywalker", "Wilhuff Tarkin", "Chewbacca", "Han Solo"}), names);
        assertEquals(Arrays.asList(new String[]{"172", "167", "188", "180", "228", "180"}), heights);
        assertEquals(Arrays.asList(new String[]{"77", "75", "84", "unknown", "112", "80"}), masses);
    }

    private SwapiResponse getMockResponse(String jsonName) throws IOException {
        return objectMapper.readValue(new File(String.format("src/test/resources/%s.json", jsonName)), SwapiResponse.class);
    }

}