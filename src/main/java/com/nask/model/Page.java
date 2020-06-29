package com.nask.model;

import java.util.List;

public class Page {
    private int count;
    private int pages;
    private List<Person> elements;

    public Page(int count, int pages, List<Person> elements) {
        this.count = count;
        this.pages = pages;
        this.elements = elements;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<Person> getElements() {
        return elements;
    }

    public void setElements(List<Person> elements) {
        this.elements = elements;
    }
}
