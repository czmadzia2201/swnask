package com.nask.swapireader;

import com.nask.model.Page;
import com.nask.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

@Component
public class PageInitializer {

    @Autowired
    private CacheBuilder cacheBuilder;

    public Page getPage(int pageNo) {
        List<Person> characters = new ArrayList();
        int count = cacheBuilder.getPeople().size();
        int pages = (int) Math.ceil((double) count/10);
        if(pageNo <= pages) {
            setPageElements(pageNo, count, characters);
        }
        return new Page(count, pages, characters);
    }

    private void setPageElements(int pageNo, int count, List<Person> characters) {
        int startId = cacheBuilder.getIdList().get((pageNo-1)*10);
        int lastId = (pageNo*10 <= count) ? cacheBuilder.getIdList().get(pageNo*10-1) : cacheBuilder.getIdList().get(count-1);
        SortedMap<Integer, Person> pagePeople = cacheBuilder.getPeople().subMap(startId, true, lastId, true);
        for(Person character : pagePeople.values()) {
            characters.add(character);
        }
    }

}
