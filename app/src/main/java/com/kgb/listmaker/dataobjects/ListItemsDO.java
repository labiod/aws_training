package com.kgb.listmaker.dataobjects;


import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ListItemsDO {
    private String _itemId = "";
    private String _item = "";
    private String _listNameId = "";

    public ListItemsDO() {
    }

    public ListItemsDO(String listString) {

        // Try to split the comma-separated string
        String[] array = listString.split(",");

        // Add the properties
        if (array.length > 2) {
            this._itemId = array[0];
            this._item = array[1];
            this._listNameId = array[2];
        }
    }

    public String getItemId() {
        return _itemId;
    }

    public void setItemId(final String _itemId) {
        this._itemId = _itemId;
    }

    public String getItem() {
        return _item;
    }

    public void setItem(final String _item) {
        this._item = _item;
    }

    public String getListNameId() {
        return _listNameId;
    }

    public void setListNameId(final String _listNameId) {
        this._listNameId = _listNameId;
    }

    public String toString() {
        return _itemId + "," + _item + "," + _listNameId;
    }

    // This method saves the ListItemsDO object to the shared preferences
    public void saveToSharedPrefs(SharedPreferences sharedPrefs) {

        // Get the list items for this list from the shared preferences
        Set<String> listItemsSet = sharedPrefs.getStringSet(this._listNameId, null);

        // Create an empty array if necessary
        ArrayList<String> listItems = new ArrayList<>();
        if (listItemsSet != null) {
            listItems = new ArrayList<>(listItemsSet);
        }

        // Add the serialized form of this list item to the listItems array
        listItems.add(this.toString());

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet(this._listNameId, new HashSet<>(listItems));
        editor.apply();
    }

    // This method removes this ListItemsDO object from the shared preferences
    public void removeFromSharedPreferences(SharedPreferences sharedPrefs) {

        SharedPreferences.Editor editor = sharedPrefs.edit();

        // Get the list items from the shared preferences
        Set<String> listItemsSet = sharedPrefs.getStringSet(this._listNameId, null);

        // Remove this list item
        if (listItemsSet != null) {
            ArrayList<String> listItems = new ArrayList<>(listItemsSet);
            if (listItems.contains(this.toString())) {
                listItems.remove(this.toString());
                editor.putStringSet(this._listNameId, new HashSet<>(listItems));
            }
        }

        editor.apply();
    }
}
