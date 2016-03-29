package com.marc0x71.mycontacts.data;

/**
 * Created by marc0x71 on 22/03/2016.
 */
public class Contact {

    private String id;
    private String name;
    private String photoUri;

    public Contact() {
    }

    public Contact(String id, String name, String photoUri) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }
}
