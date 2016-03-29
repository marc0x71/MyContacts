package com.marc0x71.mycontacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.marc0x71.mycontacts.adapter.ContactAdapter;
import com.marc0x71.mycontacts.data.Contact;
import com.marc0x71.mycontacts.usecase.GetAllContactUseCase;

import java.util.List;

import rx.Subscriber;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int POSITION_OFFSET = 32;

    private ContactAdapter myAdapter;
    private ListView indexList;
    private LinearLayoutManager myLayoutManager;
    private RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indexList = (ListView) findViewById(R.id.index_list);

        myRecyclerView = (RecyclerView) findViewById(R.id.contact_list);

        if (myRecyclerView != null) {
            myRecyclerView.setHasFixedSize(false);
            myLayoutManager = new LinearLayoutManager(getApplicationContext());
            myRecyclerView.setLayoutManager(myLayoutManager);

            myAdapter = new ContactAdapter(getApplicationContext());
            myRecyclerView.setAdapter(myAdapter);

            ItemTouchHelper myItemTouchHelper = new ItemTouchHelper(
                    new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                            ItemTouchHelper.LEFT) {

                        @Override
                        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                            if (myAdapter.isSeparator(viewHolder)) return 0;
                            return super.getSwipeDirs(recyclerView, viewHolder);
                        }

                        @Override
                        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                            if (myAdapter.isSeparator(current)) return false;
                            return super.canDropOver(recyclerView, current, target);
                        }

                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            final int fromPos = viewHolder.getAdapterPosition();
                            final int toPos = target.getAdapterPosition();
                            if (fromPos == toPos) return false;
                            myAdapter.moveContact(fromPos, toPos);
                            return true;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            final int position = viewHolder.getAdapterPosition();
                            myAdapter.removeContact(position);
                        }
                    });
            myItemTouchHelper.attachToRecyclerView(myRecyclerView);

            loadContacts();
        }

        buildIndex();
    }

    private void buildIndex() {
        String index[] = new String['Z' - 'A' + 1];
        int i = 0;
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            index[i++] = "" + letter;
        }
        indexList.setAdapter(new ArrayAdapter<String>(this, R.layout.letter, index));

        indexList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int contactPosition = myAdapter.getContactPositionByLetter((char) ('A' + id));
                if (contactPosition < 0) {
                    Toast.makeText(MainActivity.this, "No items found for " + (char) ('A' + id), Toast.LENGTH_SHORT).show();
                } else {
                    myLayoutManager.scrollToPositionWithOffset(contactPosition, POSITION_OFFSET);
                }
            }
        });
        Timber.d("buildIndex() called with: " + index);

    }

    private void loadContacts() {
        Timber.d("loadContacts: get list... " + getApplicationContext());
        GetAllContactUseCase useCase = new GetAllContactUseCase(getApplicationContext());
        useCase.getAllAsList().subscribe(new Subscriber<List<Contact>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Contact> contacts) {
                myAdapter.setContactList(contacts);
            }
        });
    }
}
