package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import Data.ContactDAO;
import Data.ContactDatabase;
import Model.Contact;

public class MainActivity extends AppCompatActivity {

    ContactAdapter contactAdapter;
    ArrayList<Contact> contactArrayList = new ArrayList<>();
    RecyclerView recyclerView;

    ContactDatabase contactDatabase;
    ContactDAO contactDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditContacts(false, null, -1);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        contactAdapter = new ContactAdapter(this, contactArrayList, MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactAdapter);


        contactDatabase = Room.databaseBuilder(getApplicationContext(), ContactDatabase.class, "ContactsDB").build();
        contactDAO = contactDatabase.getContactDAO();

        getAllContacts();
    }

    public void addAndEditContacts(final boolean isUpdate, final Contact contact, final int position){
//        LayoutInflater ltInflater = getLayoutInflater();
//        View view = ltInflater.inflate(R.layout.contact_add_layout, null);

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.contact_add_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(view);

        TextView newContactTitle = view.findViewById(R.id.titleContact);
        final EditText firstName = view.findViewById(R.id.firstNameEditText);
        final EditText lastName = view.findViewById(R.id.lastNameEditText);
        final EditText email = view.findViewById(R.id.emailEditText);
        final EditText phoneNumber = view.findViewById(R.id.phoneNumberEditText);

        newContactTitle.setText(!isUpdate ? "Add contact" : "Edit contact");

        if (isUpdate && contact != null) {
            firstName.setText(contact.getFirstName());
            lastName.setText(contact.getLastName());
            email.setText(contact.getEmail());
            phoneNumber.setText(contact.getPhoneNumber());
        }

//        alertDialogBuilder.setCancelable(false).setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        }).setNegativeButton(isUpdate ? "Delete" : "Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (isUpdate){
//                    deleteContact(contact, position);
//                } else {
//                    dialog.cancel();
//                }
//            }
//        });

        alertDialogBuilder.setCancelable(true).setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(firstName.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter first name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(lastName.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter email!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(phoneNumber.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter phone number!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                if (isUpdate && contact != null){
                    updateContact(firstName.getText().toString(), lastName.getText().toString(),
                            email.getText().toString(),phoneNumber.getText().toString(), position);
                } else {
                    createContact(firstName.getText().toString(), lastName.getText().toString(),
                            email.getText().toString(),phoneNumber.getText().toString());
                }

            }
        });

    }

    public void createContact(String firstName, String lastName, String email, String phoneNumber){
        Contact contact = new Contact(0, firstName, lastName, email, phoneNumber);
        new createAsyncTask().execute(contact);

//        long id = contactDAO.createContact(new Contact(0, firstName, lastName, email, phoneNumber));
//        Contact contact = contactDAO.getContact(id);
//        if (contact != null) {
//            contactArrayList.add(0, contact);
//        }
//        contactAdapter.notifyDataSetChanged();
    }

    public class createAsyncTask extends AsyncTask<Contact, Void, Contact> {
        @Override
        protected Contact doInBackground(Contact... contacts) {
            long id = contactDAO.createContact(contacts[0]);
            Contact contact = contactDAO.getContact(id);
            return contact;
        }
        @Override
        protected void onPostExecute(Contact contact) {
            super.onPostExecute(contact);
        if (contact != null) {
            contactArrayList.add(0, contact);
        }
        contactAdapter.notifyDataSetChanged();
        }
    }


    public void updateContact(String firstName, String lastName, String email, String phoneNumber, int position){

        Contact contact = contactArrayList.get(position);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);

        new updateAsyncTask().execute(contact);

//        contactDAO.updateContact(contact);
        contactArrayList.set(position, contact);
//        contactAdapter.notifyDataSetChanged();
    }

    public class updateAsyncTask extends AsyncTask<Contact, Void, Contact>{

        @Override
        protected Contact doInBackground(Contact... contacts) {
            Contact contact = contacts[0];
            contactDAO.updateContact(contact);

            return contact;
        }

        @Override
        protected void onPostExecute(Contact contact) {
            super.onPostExecute(contact);

            contactAdapter.notifyDataSetChanged();
        }
    }

    public void deleteContact(Contact contact, int position){
        contactArrayList.remove(position);
        new deleteAsyncTask().execute(contact);
//        contactDAO.deleteContact(contact);
//        contactAdapter.notifyDataSetChanged();
    }

    public class deleteAsyncTask extends AsyncTask<Contact, Void, Void>{
        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDAO.deleteContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            contactAdapter.notifyDataSetChanged();
        }
    }

    public void getAllContacts(){
        new getAllAsyncTask().execute();
//        contactArrayList.addAll(contactDAO.getAllContact());
//        contactAdapter.notifyDataSetChanged();
    }

    public class getAllAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            contactArrayList.addAll(contactDAO.getAllContact());
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            contactAdapter.notifyDataSetChanged();
        }
    }

    public void getContact(long id){
        new getAsyncTask().execute(id);

//        contactDAO.getContact(id);
//        contactAdapter.notifyDataSetChanged();
    }

    public class getAsyncTask extends AsyncTask<Long, Void, Void>{
        @Override
        protected Void doInBackground(Long... longs) {
            contactDAO.getContact(longs[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            contactAdapter.notifyDataSetChanged();
        }
    }

    public void swipeContact() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        Toast.makeText(MainActivity.this, "Contact delete", Toast.LENGTH_SHORT).show();

                        int position = viewHolder.getAdapterPosition();
                        Contact contact = contactArrayList.get(position);
                        deleteContact(contact, position);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

}