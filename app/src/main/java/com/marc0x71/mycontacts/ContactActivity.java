package com.marc0x71.mycontacts;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.marc0x71.mycontacts.data.Contact;
import com.marc0x71.mycontacts.usecase.GetContactUseCase;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import timber.log.Timber;

public class ContactActivity extends AppCompatActivity {

    public static final String CONTACT_ID = "CONTACT_ID";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.contact_image)
    ImageView contactImage;

    @Bind(R.id.emails)
    TextView contactEmails;

    @Bind(R.id.phones)
    TextView contactPhones;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setTitle("");
        String contactId = getIntent().getStringExtra(CONTACT_ID);

        if (contactId != null) loadContact(contactId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadContact(String contactId) {

        Timber.d("loading contact '%s'...", contactId);
        GetContactUseCase useCase = new GetContactUseCase(this);
        useCase.get(contactId).subscribe(new Subscriber<Contact>() {
            Contact contact;

            @Override
            public void onCompleted() {
                showContact(contact);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("Loading contact error", e);
            }

            @Override
            public void onNext(Contact contact) {
                this.contact = contact;
            }
        });
    }

    private void showContact(Contact contact) {
        Timber.d("contact=%s", contact);
        actionBar.setTitle(contact.getName());
        contactPhones.setText(contact.getPhonesAsString());
        contactEmails.setText(contact.getEmailsAsString());
        if (contact.getPhotoUri() != null && !contact.getPhotoUri().isEmpty()) {
            Picasso.with(this).load(contact.getPhotoUri()).into(contactImage);
        } else {
            contactImage.setImageResource(R.drawable.ic_contact);
        }


    }
}
