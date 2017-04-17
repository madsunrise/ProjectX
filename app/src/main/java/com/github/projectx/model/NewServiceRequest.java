package com.github.projectx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 16.04.17.
 */

public class NewServiceRequest {
    private String name;
    private String description;
    private int price;
    private List<String> photos = new ArrayList<>();

    public NewServiceRequest() {
    }

    public NewServiceRequest(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public NewServiceRequest(Service service) {
        this.name = service.getName();
        this.description = service.getDescription();
        this.price = service.getPrice();
        this.photos = service.getPhotos();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void addPhoto(String base64photo) {
        photos.add(base64photo);
    }
}
