package com.kgb.listmaker.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import com.kgb.listmaker.R
import com.kgb.listmaker.dataobjects.ListItemsDO
import com.kgb.listmaker.dataobjects.ListMakerApplication
import com.kgb.listmaker.dataobjects.ListNamesDO
import com.kgb.listmaker.utilities.ListManager
import com.kgb.listmaker.adapters.ListItemAdapter

class ListItemActivity : AppCompatActivity() {

    lateinit var listView: RecyclerView
    lateinit var listAdapter: ListItemAdapter
    lateinit var list: ListNamesDO

    // The tempSelectedItem property is used to delete a selected item
    var tempSelectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Get the properties from the intent
        val b = intent.extras
        val listNameId = b.getString("listNameId")
        list = ListManager.getListForId(listNameId)

        // Get the list items for this list
        val getListItemsTask = ListMakerApplication.GetListItemsTask(list, this)
        val t = Thread(getListItemsTask)
        t.start()

        // Set the title for the action bar
        val bar = supportActionBar!!
        bar.title = list.name

        // Enable the up button
        bar.setDisplayHomeAsUpEnabled(true)
        bar.setHomeButtonEnabled(true)

        // Get a reference to the listView
        listView = this.findViewById(R.id.list_view)

        // Create an adapter for the list
        listAdapter = ListItemAdapter(this)

        // Set the list items for the adapter
        listAdapter.listItems = ListManager.lists[list.nameId] as ArrayList<ListItemsDO>

        // Set the adapter for the listView
        listView.adapter = listAdapter

        // Register the listView for a context menu to delete the items
        registerForContextMenu(listView)
    }

    override fun onResume() {
        super.onResume()

        // Register to receive a broadcast when list items have been downloaded
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                IntentFilter("listItemsDownloaded"))

        // Register to receive a broadcast when a list item has been added
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                IntentFilter("listItemAdded"))

        // Register to receive a broadcast when a list item has been removed
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                IntentFilter("listItemRemoved"))
    }

    override fun onPause() {
        super.onPause()

        // Unregister for broadcasts
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.table_row_delete, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.menu_table_row_delete) {

            // Remove this item from the list
            removeItemFromList(listAdapter.listItems[tempSelectedItem])

            return true
        }
        else {
            return super.onContextItemSelected(item)
        }
    }

    // This method adds the given item to the list
    fun addItemToList(listItem: String) {

        // Add this item to the list
        val addListItemTask = ListMakerApplication.AddListItemTask(listItem, list, this)
        val t = Thread(addListItemTask)
        t.start()
    }

    // This method removes the given item from the list
    private fun removeItemFromList(listItem: ListItemsDO) {

        // Remove this item from the list
        val deleteListItemTask = ListMakerApplication.DeleteListItemTask(listItem, this)
        val t = Thread(deleteListItemTask)
        t.start()
    }

    ////////////////////////////////
    // Data requests and handling //
    ////////////////////////////////

    // This object responds to broadcasts
    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == "listItemsDownloaded" ||
                    intent.action == "listItemAdded" ||
                    intent.action == "listItemRemoved") {

                // Get the listNames from the ListManager and update the mainAdapter
                runOnUiThread {
                    listAdapter.listItems = ListManager.lists[list.nameId] as ArrayList<ListItemsDO>
                    listAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}