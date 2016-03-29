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

    private static final String TAG = "ContactAdapter";

    private List<Contact> contactList = new ArrayList<>();
    private HashMap<String, Integer> contactPositions = new HashMap<>();
    private Context context;

    public ContactAdapter(Context context) {
        Log.d(TAG, "ContactAdapter() called with: " + "context = [" + context + "]");
        this.context = context;
    }

    private void buildContactPositions() {
        contactPositions.clear();
        for (int i = 0; i < contactList.size(); i++) {
            String letter = contactList.get(i).getName().substring(0, 1);
            if (!contactPositions.containsKey(letter)) {
                Log.d(TAG, "buildContactPositions: '" + letter + "' pos=" + i);
                contactPositions.put(letter, i);
            }
        }
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
        buildContactPositions();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        if (contact.getPhotoUri() != null && !contact.getPhotoUri().isEmpty()) {
            Picasso.with(context).load(contact.getPhotoUri()).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_contact);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void moveContact(int fromPos, int toPos) {
        Log.d(TAG, "moveContact() called with: " + "fromPos = [" + fromPos + "], toPos = [" + toPos + "]");
        Contact contact = contactList.get(fromPos);
        contactList.remove(fromPos);
        contactList.add(toPos, contact);
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
            Log.d(TAG, "getContactPositionByLetter: 0");
            return 0;
        }
        return contactPositions.get(letter + "");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            image = (ImageView) itemView.findViewById(R.id.contact_image);
        }
    }
}
