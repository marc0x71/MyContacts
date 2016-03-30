package com.marc0x71.mycontacts;

import android.graphics.Canvas;
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

public class MainActivity extends AppCompatActivity {

    private static final int POSITION_OFFSET = 32;
    private static final String[] ALPHABET = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private ContactAdapter myAdapter;
    private ListView indexList;
    private LinearLayoutManager myLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indexList = (ListView) findViewById(R.id.index_list);

        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.contact_list);

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
                        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                            c.setDensity(50);
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }

                        @Override
                        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                            if (myAdapter.isSeparator(viewHolder)) return 0;
                            return super.getSwipeDirs(recyclerView, viewHolder);
                        }

                        @Override
                        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                            return !myAdapter.isSeparator(current) && !myAdapter.isSeparator(target) && super.canDropOver(recyclerView, current, target);
                        }

                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            final int fromPos = viewHolder.getAdapterPosition();
                            final int toPos = target.getAdapterPosition();
                            if (fromPos == toPos) return false;
                            if (!myAdapter.canMoveContact(fromPos, toPos)) return false;
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

            myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    myAdapter.onScroll(myLayoutManager.findFirstVisibleItemPosition(), myLayoutManager.findLastVisibleItemPosition());
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            loadContacts();
        }

        buildIndex();
    }

    private void buildIndex() {

        indexList.setAdapter(new ArrayAdapter<>(this, R.layout.letter, ALPHABET));

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
    }

    private void loadContacts() {
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
