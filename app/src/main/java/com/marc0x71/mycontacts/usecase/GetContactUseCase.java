package com.marc0x71.mycontacts.usecase;

/**
 * Created by marc0x71 on 30/03/2016.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.marc0x71.mycontacts.data.Contact;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class GetContactUseCase {

    private Context context;

    public GetContactUseCase(Context context) {
        this.context = context;
    }

    private Cursor prepareCursor(String id) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.PhoneLookup.LOOKUP_KEY,
                        ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.PhoneLookup.PHOTO_URI,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.Data.MIMETYPE},
                ContactsContract.Data.LOOKUP_KEY + " = '" + id + "' AND (" +
                        ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'"
                        + " OR " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'"
                        + ")",
                null,
                null);
        return cursor;
    }

    private Contact buildFromCursor(Cursor cursor, Contact contact) {


        contact.setId(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.LOOKUP_KEY)));
        contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)));
        contact.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI)));

        switch (cursor.getString(cursor.getColumnIndexOrThrow((ContactsContract.Data.MIMETYPE)))) {
            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                contact.addPhone(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                break;
            case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                contact.addEmail(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                break;
            default:
        }

        return contact;
    }

    public Observable<Contact> get(final String id) {
        return Observable.create(new Observable.OnSubscribe<Contact>() {
            @Override
            public void call(Subscriber<? super Contact> subscriber) {
                Cursor cursor = prepareCursor(id);
                Contact contact = new Contact();
                while (cursor.moveToNext()) {
                    buildFromCursor(cursor, contact);

                }
                cursor.close();
                subscriber.onNext(contact);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

}