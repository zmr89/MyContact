package Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Model.Contact;

@Dao
public interface ContactDAO {
    @Insert
    public long createContact(Contact contact);

    @Update
    public void updateContact(Contact contact);

    @Delete
    public void deleteContact(Contact contact);

    @Query("SELECT * FROM contacts")
    public List<Contact> getAllContact();

    @Query("SELECT * FROM contacts WHERE contact_id ==:idContact")
    public Contact getContact(long idContact);

}
