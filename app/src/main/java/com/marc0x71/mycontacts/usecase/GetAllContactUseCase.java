package com.marc0x71.mycontacts.usecase;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.marc0x71.mycontacts.data.Contact;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GetAllContactUseCase {

    Context context;

    public GetAllContactUseCase(Context context) {
        this.context = context;
    }

    private Cursor prepareCursor() {
        ContentResolver resolver = context.getContentResolver();
        return resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.PhoneLookup.LOOKUP_KEY, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI},
                ContactsContract.Data.HAS_PHONE_NUMBER + " = '1'",
                null,
                ContactsContract.PhoneLookup.DISPLAY_NAME);
    }

    private Contact buildFromCursor(Cursor cursor) {
        Contact contact = new Contact();
        contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)));
        contact.setId(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.LOOKUP_KEY)));
        contact.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI)));
        return contact;
    }

    @SuppressWarnings("unused")
    public Observable<Contact> getAll() {
        return Observable.create(new Observable.OnSubscribe<Contact>() {
            @Override
            public void call(Subscriber<? super Contact> subscriber) {
                Cursor cursor = prepareCursor();
                while (cursor.moveToNext()) {
                    subscriber.onNext(buildFromCursor(cursor));
                }
                cursor.close();
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<List<Contact>> getAllAsList() {
        return Observable.create(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                List<Contact> list = new ArrayList<>();

                Cursor cursor = prepareCursor();
                while (cursor.moveToNext()) {
                    list.add(buildFromCursor(cursor));
                }
                cursor.close();

                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}