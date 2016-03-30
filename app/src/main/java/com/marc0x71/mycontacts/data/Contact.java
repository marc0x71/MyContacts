package com.marc0x71.mycontacts.data;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by marc0x71 on 22/03/2016.
 */
public class Contact {

    private String id;
    private String name;
    private String photoUri;
    private Set<String> phones = new HashSet<>();
    private Set<String> emails = new HashSet<>();

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
                ", phones=" + phones +
                ", emails=" + emails +
                '}';
    }

    public void addPhone(String string) {
        phones.add(string);
    }

    public void addEmail(String string) {
        emails.add(string);
    }

    public Set<String> getPhones() {
        return phones;
    }

    public void setPhones(Set<String> phones) {
        this.phones = phones;
    }

    public String getPhonesAsString() {
        return buildStringFromSet(phones);
    }

    private String buildStringFromSet(Set<String> values) {
        return TextUtils.join("\n", values.toArray());
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public String getEmailsAsString() {
        return buildStringFromSet(emails);
    }
}
