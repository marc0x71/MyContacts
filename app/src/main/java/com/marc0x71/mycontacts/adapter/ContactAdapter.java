package com.marc0x71.mycontacts.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marc0x71.mycontacts.R;
import com.marc0x71.mycontacts.data.Contact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by marc0x71 on 22/03/2016.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    public static final int CONTACT_ITEM = 100;
    public static final int CONTACT_SEPARATOR = 101;

    private List<ContactItem> contactList = new ArrayList<>();
    private HashMap<String, Integer> contactPositions = new HashMap<>();
    private Context context;
    private RecyclerView myRecyclerView;
    private OnClickListener clickListener;

    public ContactAdapter(Context context, OnClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    private void buildContacts(List<Contact> contacts) {
        contactPositions.clear();
        contactList.clear();
        for (int i = 0; i < contacts.size(); i++) {
            String letter = contacts.get(i).getName().substring(0, 1).toUpperCase();
            if (!contactPositions.containsKey(letter)) {
                contactPositions.put(letter, contactList.size());
                contactList.add(new ContactItem(letter));
            }
            contactList.add(new ContactItem(contacts.get(i)));
        }
        Timber.d("buildContacts() done!");
    }

    public void setContactList(List<Contact> contactList) {
        buildContacts(contactList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType == CONTACT_ITEM ? R.layout.contact_item : R.layout.contact_separator, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return contactList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactItem item = contactList.get(position);
        if (holder.type == CONTACT_ITEM) {
            Contact contact = item.contact;
            holder.position = position;
            holder.name.setText(contact.getName());
            if (contact.getPhotoUri() != null && !contact.getPhotoUri().isEmpty()) {
                Picasso.with(context).load(contact.getPhotoUri()).into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.ic_contact);
            }
        } else {
            holder.letter.setText(item.header);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void moveContact(int fromPos, int toPos) {
        Timber.d("moveContact() called with: fromPos = [%d], toPos = [%d]", fromPos, toPos);
        ContactItem item = contactList.get(fromPos);
        contactList.remove(fromPos);
        contactList.add(toPos, item);
        notifyItemMoved(fromPos, toPos);
    }

    public void removeContact(int position) {
        Timber.d("removeContact() called with: position = [%d]", position);
        contactList.remove(position);
        notifyItemRemoved(position);
    }

    public int getContactPositionByLetter(char letter) {
        Timber.d("getContactPositionByLetter() called with: letter = [%c] contactPositions=%d", letter, contactPositions.size());
        if (!contactPositions.containsKey(letter + "")) {
            return -1;
        }
        return contactPositions.get(letter + "");
    }

    public boolean isSeparator(RecyclerView.ViewHolder viewHolder) {
        ViewHolder vh = (ViewHolder) viewHolder;
        return vh.isSeparator();
    }

    public boolean canMoveContact(int fromPos, int toPos) {
        int startingSection = -1;
        for (int i = fromPos; i >= 0; i--) {
            if (contactList.get(i).getType() == CONTACT_SEPARATOR) {
                startingSection = i;
                break;
            }
        }
        int targetSection = -1;
        for (int i = toPos; i >= 0; i--) {
            if (contactList.get(i).getType() == CONTACT_SEPARATOR) {
                targetSection = i;
                break;
            }
        }
        return startingSection == targetSection;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        myRecyclerView = null;
    }

    public void onScroll(int firstVisibleItemPosition, int lastVisibleItemPosition) {
        if (myRecyclerView == null) {
            Timber.w("Unexpected call of onScroll");
            return;
        }
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            if (contactList.get(i).getType() == CONTACT_SEPARATOR) {
                ViewHolder holder = (ViewHolder) myRecyclerView.findViewHolderForAdapterPosition(i);
                holder.letter.setTranslationY((float) ((firstVisibleItemPosition - i) * 4.5));
            }
        }
    }

    private void onItemClick(int position) {
        Timber.d("onItemClick(%d)", position);
        if (clickListener != null)
            clickListener.onClick(position, contactList.get(position).contact);
    }

    public interface OnClickListener {
        public void onClick(int position, Contact contact);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @Bind(R.id.contact_name)
        public TextView name;

        @Nullable
        @Bind(R.id.contact_image)
        public ImageView image;

        @Nullable
        @Bind(R.id.contact_letter)
        public TextView letter;

        public int type;

        int position = -1;

        public ViewHolder(View v, int viewType) {
            super(v);
            ButterKnife.bind(this, v);
            type = viewType;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSeparator() && position >= 0) {
                        onItemClick(position);
                    }
                }
            });
        }

        public boolean isSeparator() {
            return type == CONTACT_SEPARATOR;
        }

    }

    private class ContactItem {
        String header = "";
        Contact contact;

        public ContactItem(String letter) {
            this.contact = null;
            this.header = letter;
        }

        public ContactItem(Contact contact) {
            this.header = "";
            this.contact = contact;
        }

        public int getType() {
            return contact != null ? CONTACT_ITEM : CONTACT_SEPARATOR;
        }
    }
}

