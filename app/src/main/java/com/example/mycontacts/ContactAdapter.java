package com.example.mycontacts;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Model.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactVewHolder> {
    Context context;
    ArrayList<Contact> contactArrayList;
    MainActivity mainActivity;


    public class ContactVewHolder extends RecyclerView.ViewHolder {
        public TextView firstName;
        public TextView lastName;
        public TextView email;
        public TextView phoneNumber;

        public ContactVewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.firstNameTextView);
            lastName = itemView.findViewById(R.id.lastNameTextView);
            email = itemView.findViewById(R.id.emailTextView);
            phoneNumber = itemView.findViewById(R.id.phoneNumberTextView);
        }
    }


    public ContactAdapter(Context context, ArrayList<Contact> contactArrayList, MainActivity mainActivity) {
        this.context = context;
        this.contactArrayList = contactArrayList;
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public ContactVewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new ContactVewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactVewHolder holder, int position) {
        final Contact contact = contactArrayList.get(position);
        holder.firstName.setText(contact.getFirstName());
        holder.lastName.setText(contact.getLastName());
        holder.phoneNumber.setText(contact.getPhoneNumber());
        holder.email.setText(contact.getEmail());

        //редактирвание контакта при нажатии на него
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // в видоуроке вместо getAdapterPosition() просто position
                mainActivity.addAndEditContacts(true, contact, holder.getAdapterPosition());
            }
        });

        //удаление контакта при его свайпе
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainActivity.swipeContact();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public void setContactArrayList(ArrayList<Contact> contactArrayList) {
        this.contactArrayList = contactArrayList;
        notifyDataSetChanged();
    }
}
