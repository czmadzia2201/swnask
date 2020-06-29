package com.nask.swapireader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nask.model.Person;
import com.nask.model.Planet;
import com.nask.model.Starship;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CacheBuilder {

    private static String HOMEWORLD = "homeworld";
    private static String STARSHIPS = "starships";

    private EntityRetriever entityRetriever;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NavigableMap<Integer, Person> people = new TreeMap();

    private List<Integer> idList = new ArrayList();

    public CacheBuilder(EntityRetriever entityRetriever) {
        this.entityRetriever = entityRetriever;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        convertPeople();
    }

    public CacheBuilder() {
        this(new EntityRetriever());
    }

    public NavigableMap<Integer, Person> getPeople() {
        if(people.equals(Collections.EMPTY_MAP))
            convertPeople();
        return people;
    }

    public List<Integer> getIdList() {
        if(idList.equals(Collections.EMPTY_LIST))
            convertPeople();
        return idList;
    }

    private void convertPeople() {
        Map<String, Planet> planets = convertPlanets();
        Map<String, Starship> starships = convertStarships();
        List<Map<String, Object>> peopleMaps = entityRetriever.getAll("people");
        for(Map<String, Object> personMap : peopleMaps) {
            Person person = convertPerson(personMap, planets, starships);
            people.put(person.getId(), person);
            idList.add(person.getId());
        }
    }

    protected Person convertPerson(Map<String, Object> personMap, Map<String, Planet> planets, Map<String, Starship> starships) {
        int id = retrieveIdFromUrl((String)personMap.get("url"));
        List<String> errors = new ArrayList();

        String homeworldUrl = (String) personMap.get(HOMEWORLD);
        Planet homeworld = planets.get(homeworldUrl);
        if(homeworld==null) {
            errors.add(String.format("Homeworld planet %s not found", homeworldUrl));
        }

        List<String> starshipUrls = (List<String>) personMap.get(STARSHIPS);
        List<Starship> starshipsList = new ArrayList();
        for(String starshipUrl : starshipUrls) {
            Starship starship = starships.get(starshipUrl);
            if(starship!=null) {
                starshipsList.add(starship);
            } else {
                errors.add(String.format("Starship %s not found", starshipUrl));
            }
        }

        personMap.put("id", id);
        personMap.put(HOMEWORLD, homeworld);
        personMap.put(STARSHIPS, starshipsList);
        if(!errors.equals(Collections.EMPTY_LIST))
            personMap.put("errors", errors);
        return objectMapper.convertValue(personMap, Person.class);
    }

    protected Map<String, Planet> convertPlanets() {
        List<Map<String, Object>> planetMaps = entityRetriever.getAll("planets");
        Map<String, Planet> planets = new HashMap();
        for(Map planetMap : planetMaps) {
            Planet planet = objectMapper.convertValue(planetMap, Planet.class);
            planets.put((String)planetMap.get("url"), planet);
        }
        return planets;
    }

    protected Map<String, Starship> convertStarships() {
        List<Map<String, Object>> starshipMaps = entityRetriever.getAll("starships");
        Map<String, Starship> starships = new HashMap();
        for(Map starshipMap : starshipMaps) {
            Starship starship = objectMapper.convertValue(starshipMap, Starship.class);
            starships.put((String)starshipMap.get("url"), starship);
        }
        return starships;
    }

    private int retrieveIdFromUrl(String url) {
        String[] splitted = url.split("/");
        return Integer.parseInt(splitted[splitted.length-1]);
    }

}
