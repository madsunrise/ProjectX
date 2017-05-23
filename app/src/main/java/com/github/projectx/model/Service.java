package com.github.projectx.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ivan on 15.04.17.
 */

public class Service {
    private long id;
    private String name;
    private String description;
    private int price;
    private Date dateCreated = new Date();
    private long userId;
    private String userEmail;
    private List<String> photos = new ArrayList<>();

    public Service() {
    }

    public Service(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public static class ServiceDeserializer
            implements JsonSerializer<Service>, JsonDeserializer<Service> {

        @Override
        public JsonElement serialize(Service src, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }

        @Override
        public Service deserialize(JsonElement json, Type type,
                                   JsonDeserializationContext context) throws JsonParseException {

            Service service = new Gson().fromJson(json, Service.class);
            JsonElement jsonDate = json.getAsJsonObject().get("date_created");
            long dateInMillis = Long.valueOf(jsonDate.toString());
            service.setDateCreated(new Date(dateInMillis));
            return service;
        }
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public void addPhoto(String base64photo) {
        this.photos.add(base64photo);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
