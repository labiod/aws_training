package com.kgb.listmaker.dataobjects;


import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ListNamesDO {
    private String _nameId = "";
    private String _name = "";

    public ListNamesDO() {
    }

    public ListNamesDO(String listString) {

        // Try to split the comma-separated string
        String[] array = listString.split(",");

        // Add the properties
        if (array.length > 1) {
            this._nameId = array[0];
            this._name = array[1];
        }
    }

    public String getNameId() {
        return _nameId;
    }

    public void setNameId(final String _nameId) {
        this._nameId = _nameId;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }

    public String toString() {
        return _nameId + "," + _name;
    }

    // This method saves the ListNamesDO object to the shared preferences
    public void saveToSharedPrefs(SharedPreferences sharedPrefs) {

        // Get the list names from the shared preferences
        Set<String> listNamesSet = sharedPrefs.getStringSet("listNames", null);

        // Create an empty array if necessary
        ArrayList<String> listNames = new ArrayList<>();
        if (listNamesSet != null) {
            listNames = new ArrayList<>(listNamesSet);
        }

        // Add the serialized form of this list object to the listNames array
        listNames.add(this.toString());

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet("listNames", new HashSet<>(listNames));
        editor.putStringSet(this._nameId, new HashSet<String>());
        editor.apply();
    }

    // This method removes this ListNamesDO object and all its list items from the shared perferences
    public void removeFromSharedPreferences(SharedPreferences sharedPrefs) {

        SharedPreferences.Editor editor = sharedPrefs.edit();

        // Get the list names from the shared preferences
        Set<String> listNamesSet = sharedPrefs.getStringSet("listNames", null);

        // Remove this list
        if (listNamesSet != null) {
            ArrayList<String> listNames = new ArrayList<>(listNamesSet);
            if (listNames.contains(this.toString())) {
                listNames.remove(this.toString());
                editor.putStringSet("listNames", new HashSet<>(listNames));
            }
        }

        // Remove any items for this list
        Set<String> listItemsSet = sharedPrefs.getStringSet(this._nameId, null);
        if (listItemsSet != null) {
            editor.remove(this._nameId);
        }

        editor.apply();
    }
}
