package com.kgb.listmaker.utilities

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.kgb.listmaker.dataobjects.ListItemsDO
import com.kgb.listmaker.dataobjects.ListNamesDO
import java.util.*

class ListManager {

    companion object {

        @JvmField var listNames = ArrayList<ListNamesDO>()
        @JvmField var lists = HashMap<String, ArrayList<ListItemsDO>>()

        // This method retrieves the list for the given listNameId or returns the first list in the array if no matching list is found
        fun getListForId(listNameId: String) : ListNamesDO {

            for (thisList in listNames) {
                if (thisList.nameId == listNameId) {
                    return thisList
                }
            }

            // If control reaches this point, then no list with the given listNameId was found, so return the first list in the array
            if (listNames.size > 0) {
                return listNames[0]
            }

            // If there weren't any items in the lists array at all, then simply return a new ListNamesDO object
            else {
                return ListNamesDO()
            }
        }

        // This method retrieves the names of the existing lists
        fun requestListNames(context: Context) {

            // Reset the listNames array
            listNames = ArrayList()

            // Get the shared preferences
            val sharedPrefs = context.getSharedPreferences("LIST_MAKER_PREFS", 0)

            // Get the list names from the shared preferences
            if (sharedPrefs.contains("listNames")) {

                val listNamesStrings = sharedPrefs.getStringSet("listNames", null)
                if (listNamesStrings != null) {
                    for (thisListName in listNamesStrings) {

                        // Create a ListNamesDO object from this string
                        val thisList = ListNamesDO(thisListName)

                        // Add the list to the listNames array
                        listNames.add(thisList)
                    }
                }
            }

            // Send a broadcast to let any interested objects know that the list names have been retrieved
            val intent = Intent("listNamesDownloaded")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        // This method retrieves the list items for the given list
        fun requestListItems(list: ListNamesDO, context: Context) {

            val listItems = ArrayList<ListItemsDO>()

            // Get the shared preferences
            val sharedPrefs = context.getSharedPreferences("LIST_MAKER_PREFS", 0)

            // Get the list items for this list from the shared preferences
            if (sharedPrefs.contains(list.nameId)) {

                // Get the array of list item strings
                val listItemStrings = sharedPrefs.getStringSet(list.nameId, null)
                if (listItemStrings != null) {
                    for (thisListItem in listItemStrings) {

                        // Create a ListItemsDO object from this string
                        val thisItem = ListItemsDO(thisListItem)

                        // Add the list item to the listItems array
                        listItems.add(thisItem)
                    }
                }
            }

            // Add the listItems to the lists array
            lists[list.nameId] = listItems

            // Send a broadcast to let any interested objects know that the list items have been retrieved
            val intent = Intent("listItemsDownloaded")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        // This method creates a new list
        fun createList(listName: String, context: Context) {

            // Get the shared preferences
            val sharedPrefs = context.getSharedPreferences("LIST_MAKER_PREFS", 0)

            // Create a new ListNamesDO object
            val newList = ListNamesDO()
            newList.nameId = UUID.randomUUID().toString()
            newList.name = listName

            // Add the new list locally
            listNames.add(newList)
            lists[newList.nameId] = ArrayList()

            // Add the list to the shared preferences
            newList.saveToSharedPrefs(sharedPrefs)

            // Send a broadcast to let any interested objects know that the list has been added
            val intent = Intent("listAdded")
            intent.putExtra("listNameId", newList.nameId)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        // This method deletes a list and all of its items
        fun deleteList(list: ListNamesDO, context: Context) {

            // Get the shared preferences
            val sharedPrefs = context.getSharedPreferences("LIST_MAKER_PREFS", 0)

            // Delete the list items locally
            lists.remove(list.nameId)

            // Delete the list locally
            listNames.remove(list)

            // Remove this list and its items from the shared preferences
            list.removeFromSharedPreferences(sharedPrefs)

            // Send a broadcast to let any interested objects know that the list has been deleted
            val intent = Intent("listRemoved")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        // This method creates a new list item
        fun createListItem(listItem: String, list: ListNamesDO, context: Context) {

            // Get the shared preferences
            val sharedPrefs = context.getSharedPreferences("LIST_MAKER_PREFS", 0)

            // Create a new ListItemsDO object
            val newItem = ListItemsDO()
            newItem.itemId = UUID.randomUUID().toString()
            newItem.item = listItem
            newItem.listNameId = list.nameId

            // Add the new item locally
            var listItems = ArrayList<ListItemsDO>()
            if (lists.containsKey(list.nameId)) {
                listItems = lists[list.nameId]!!
            }
            listItems.add(newItem)
            lists[list.nameId] = listItems

            // Add the new item to the shared preferences
            newItem.saveToSharedPrefs(sharedPrefs)

            // Send a broadcast to let any interested objects know that the list item has been added
            val intent = Intent("listItemAdded")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        // This method deletes a given list item
        fun deleteListItem(listItem: ListItemsDO, context: Context) {

            // Get the shared preferences
            val sharedPrefs = context.getSharedPreferences("LIST_MAKER_PREFS", 0)

            // Delete the list item locally
            if (lists.containsKey(listItem.listNameId)) {
                val listItems = lists[listItem.listNameId]!!
                for (thisListItem in listItems) {
                    if (thisListItem.itemId == listItem.itemId) {
                        listItems.remove(thisListItem)
                        break
                    }
                }
                lists[listItem.listNameId] = listItems
            }

            // Delete the item from the shared preferences
            listItem.removeFromSharedPreferences(sharedPrefs)

            // Send a broadcast to let any interested objects know that the list item has been deleted
            val intent = Intent("listItemRemoved")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}