package com.kgb.listmaker.dataobjects

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.multidex.MultiDexApplication
import android.support.v4.content.LocalBroadcastManager
import com.kgb.listmaker.utilities.AWSProvider
import com.kgb.listmaker.utilities.ListManager
import java.lang.ref.WeakReference

class ListMakerApplication : MultiDexApplication() {
    var shouldDownloadLists = false

    override fun onCreate() {
        super.onCreate()

        // Register to receive a broadcast when the list names have been downloaded
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                IntentFilter("listNamesDownloaded"))

        //Initialize AWSProvider
        AWSProvider.intialize(applicationContext)
    }

    ////////////////////////////////
    // Data requests and handling //
    ////////////////////////////////

    class GetListNamesTask(_context: Context) : Runnable {

        private val context : WeakReference<Context> = WeakReference(_context)

        override fun run() {

            // Request the list names
            ListManager.requestListNames(context.get()!!)
        }
    }

    class GetListItemsTask(_list: ListNamesDO, _context: Context) : Runnable {

        private val list = _list
        private val context : WeakReference<Context> = WeakReference(_context)

        override fun run() {

            // Request the list items
            ListManager.requestListItems(list, context.get()!!)
        }
    }

    class AddListTask(_listName: String, _context: Context) : Runnable {

        private val listName = _listName
        private val context : WeakReference<Context> = WeakReference(_context)

        override fun run() {

            // Add a new list
            ListManager.createList(listName, context.get()!!)
        }
    }

    class DeleteListTask(_list: ListNamesDO, _context: Context) : Runnable {

        private val list = _list
        private val context : WeakReference<Context> = WeakReference(_context)

        override fun run() {

            // Delete the given list
            ListManager.deleteList(list, context.get()!!)
        }
    }

    class AddListItemTask(_itemString: String, _list: ListNamesDO, _context: Context) : Runnable {

        private val itemString = _itemString
        private val list = _list
        private val context : WeakReference<Context> = WeakReference(_context)

        override fun run() {

            // Add the item to the given list
            ListManager.createListItem(itemString, list, context.get()!!)
        }
    }

    class DeleteListItemTask(_listItem: ListItemsDO, _context: Context) : Runnable {

        private val listItem = _listItem
        private val context : WeakReference<Context> = WeakReference(_context)

        override fun run() {

            // Remove the item from the given list
            ListManager.deleteListItem(listItem, context.get()!!)
        }
    }

    // This object responds to broadcasts
    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            // listNamesDownloaded
            if (intent.action == "listNamesDownloaded") {

                // If the lists need to be downloaded now, then download each list
                if (shouldDownloadLists) {
                    for (listNameId in ListManager.listNames) {
                        val getListItemsTask = GetListItemsTask(listNameId, applicationContext)
                        val t = Thread(getListItemsTask)
                        t.start()
                    }
                }

                // Reset the shouldDownloadLists flag
                shouldDownloadLists = false
            }
        }
    }
}