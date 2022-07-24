package Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import Model.Contact;

@Database(version = 2, entities = {Contact.class})
public abstract class ContactDatabase extends RoomDatabase {
    public abstract ContactDAO getContactDAO();
}
