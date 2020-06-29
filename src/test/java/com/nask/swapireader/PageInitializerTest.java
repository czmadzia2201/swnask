package com.nask.swapireader;

import com.nask.model.Page;
import com.nask.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PageInitializerTest {

    @InjectMocks
    private PageInitializer pageInitializer;

    @Mock
    private CacheBuilder cacheBuilder;

    @BeforeEach
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cacheBuilder.getIdList()).thenReturn(new ArrayList(getMockedPeople().keySet()));
        Mockito.when(cacheBuilder.getPeople()).thenReturn(getMockedPeople());
    }

    @Test
    public void testGetFirstPage() {
        Page firstPage = pageInitializer.getPage(1);
        assertEquals(33, firstPage.getCount());
        assertEquals(4, firstPage.getPages());
        List<Integer> firstPageIds = firstPage.getElements().stream().map(el -> el.getId()).collect(Collectors.toList());
        assertEquals(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 8, 9, 10, 11}), firstPageIds);
    }

    @Test
    public void testGetLastPage() {
        Page lastPage = pageInitializer.getPage(4);
        assertEquals(33, lastPage.getCount());
        assertEquals(4, lastPage.getPages());
        List<Integer> lastPageIds = lastPage.getElements().stream().map(el -> el.getId()).collect(Collectors.toList());
        assertEquals(Arrays.asList(new Integer[]{36, 38, 39}), lastPageIds);
    }

    @Test
    public void testPageNotFound() {
        Page outOfBounds = pageInitializer.getPage(5);
        assertEquals(33, outOfBounds.getCount());
        assertEquals(4, outOfBounds.getPages());
        assertEquals(Collections.EMPTY_LIST, outOfBounds.getElements());
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

}