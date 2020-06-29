package com.nask.swapireader;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityRetriever {

    private RestTemplate restTemplate = new RestTemplate();

    private static String URLBASE = "https://swapi.dev/api/%s/?page=%s";

    public List<Map<String, Object>> getAll(String type) {
        List<Map<String, Object>> itemList = new ArrayList();
        String next = "";
        int i = 1;
        while(next!=null) {
            SwapiResponse page = getSwapiPage(type, i);
            next = page.getNext();
            itemList.addAll(page.getResults());
            i++;
        }
        return itemList;
    }

    private SwapiResponse getSwapiPage(String type, int number) {
        String url = String.format(URLBASE, type, number);
        return restTemplate.getForObject(url, SwapiResponse.class);
    }

}
