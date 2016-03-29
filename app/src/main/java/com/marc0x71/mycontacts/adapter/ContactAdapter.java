package com.marc0x71.mycontacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * Created by marc0x71 on 22/03/2016.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    public static final int CONTACT_ITEM = 100;
    public static final int CONTACT_SEPARATOR = 101;
    private static final String TAG = "ContactAdapter";
    private List<ContactItem> contactList = new ArrayList<>();
    private HashMap<String, Integer> contactPositions = new HashMap<>();
    private Context context;

    public ContactAdapter(Context context) {
        Log.d(TAG, "ContactAdapter() called with: " + "context = [" + context + "]");
        this.context = context;
    }

    private void buildContacts(List<Contact> contacts) {
        contactPositions.clear();
        contactList.clear();
        int position = 0;
        for (int i = 0; i < contacts.size(); i++) {
            String letter = contacts.get(i).getName().substring(0, 1).toUpperCase();
            if (!contactPositions.containsKey(letter)) {
                contactPositions.put(letter, contactList.size());
                contactList.add(new ContactItem(letter));
            }
            contactList.add(new ContactItem(contacts.get(i)));
        }
    }

    public void setContactList(List<Contact> contactList) {
        buildContacts(contactList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType == CONTACT_ITEM ? R.layout.contact_item : R.layout.contact_separator, parent, false);
        ViewHolder vh = new ViewHolder(v, viewType);
        return vh;
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
            holder.name.setText(contact.getName());
            if (contact.getPhotoUri() != null && !contact.getPhotoUri().isEmpty()) {
                Picasso.with(context).load(contact.getPhotoUri()).into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.ic_contact);
            }
        } else {
            Log.d(TAG, "onBindViewHolder: pos:" + position + " is separator!");
            holder.letter.setText(item.header);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void moveContact(int fromPos, int toPos) {
        Log.d(TAG, "moveContact() called with: " + "fromPos = [" + fromPos + "], toPos = [" + toPos + "]");
        ContactItem item = contactList.get(fromPos);
        contactList.remove(fromPos);
        contactList.add(toPos, item);
        notifyItemMoved(fromPos, toPos);
    }

    public void removeContact(int position) {
        Log.d(TAG, "removeContact() called with: " + "position = [" + position + "]");
        contactList.remove(position);
        notifyItemRemoved(position);
    }

    public int getContactPositionByLetter(char letter) {
        Log.d(TAG, "getContactPositionByLetter() called with: " + "letter = [" + letter + "] contactPositions=" + contactPositions.size());
        if (!contactPositions.containsKey(letter + "")) {
            return -1;
        }
        return contactPositions.get(letter + "");
    }

    public boolean isSeparator(RecyclerView.ViewHolder viewHolder) {
        ViewHolder vh = (ViewHolder) viewHolder;
        return vh.isSeparator();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public TextView letter;
        public int type;

        public ViewHolder(View v, int viewType) {
            super(v);
            type = viewType;
            name = (TextView) itemView.findViewById(R.id.contact_name);
            image = (ImageView) itemView.findViewById(R.id.contact_image);
            letter = (TextView) itemView.findViewById(R.id.contact_letter);
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

    ;
}

