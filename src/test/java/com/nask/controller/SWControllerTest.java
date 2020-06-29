package com.nask.controller;

import com.nask.model.Page;
import com.nask.model.Person;
import com.nask.swapireader.CacheBuilder;
import com.nask.swapireader.PageInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SWControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CacheBuilder cacheBuilder;

    @MockBean
    private PageInitializer pageInitializer;

    @BeforeEach
    protected void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Mockito.when(cacheBuilder.getPeople()).thenReturn(getMockedPeople());
        Mockito.when(pageInitializer.getPage(1)).thenReturn(getPageOK());
        Mockito.when(pageInitializer.getPage(2)).thenReturn(getPageNotFound());
    }

    @Test
    public void testGetCharacterById_happyPath() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/characters/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"person1\"}"));

        mockMvc.perform(MockMvcRequestBuilders.get("/characters/2"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":2,\"name\":\"person2\"}"));
    }

    @Test
    public void testGetCharacterById_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/characters/40"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":\"NOT_FOUND\",\"message\":\"Character with id 40 not found.\"}"));
    }

    @Test
    public void testGetPage_happyPath() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/characters/?page=1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"count\":2,\"pages\":1,\"elements\":[{\"id\":1,\"name\":\"person1\"},{\"id\":2,\"name\":\"person2\"}]}"));
    }

    @Test
    public void testGetPage_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/characters/?page=2"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":\"NOT_FOUND\",\"message\":\"Page 2 not found. Last page number = 1\"}"));
    }

    private NavigableMap<Integer, Person> getMockedPeople() {
        NavigableMap<Integer, Person> people = new TreeMap();
        for(int i = 1; i < 40; i++) {
            Person person = new Person();
            person.setId(i);
            person.setName("person" + i);
            people.put(i, person);
            if(i%6==0)
                i++;
        }
        return people;
    }

    private Page getPageOK() {
        List<Person> people = new ArrayList(Arrays.asList(new Person[]{getMockedPeople().get(1), getMockedPeople().get(2)}));
        return new Page(2, 1, people);
    }

    private Page getPageNotFound() {
        List<Person> people = new ArrayList();
        return new Page(2, 1, people);
    }

}