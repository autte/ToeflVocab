package com.example.pagergalleryloadmorepart2;

public class ExploreBean {
    private String id;
    private String title;
    private String regular;

    public ExploreBean(String id, String title) {
        this.id = id;
        this.title = title;
    }
    public ExploreBean(String title) {
        this.title = title;
    }
    public ExploreBean(String id,String title, String regular) {
        this.id = id;
        this.title = title;
        this.regular = regular;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }
}
