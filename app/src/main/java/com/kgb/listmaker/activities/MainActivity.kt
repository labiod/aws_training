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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.kgb.listmaker.R
import com.kgb.listmaker.adapters.MainAdapter
import com.kgb.listmaker.dataobjects.ListMakerApplication
import com.kgb.listmaker.dataobjects.ListNamesDO
import com.kgb.listmaker.utilities.AWSProvider
import com.kgb.listmaker.utilities.ListManager


class MainActivity : AppCompatActivity() {

    private lateinit var mainList: RecyclerView
    lateinit var mainAdapter: MainAdapter

    // The tempSelectedList property is used to delete a selected list
    var tempSelectedList = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start requesting the lists and the list items
        val thisApp = application as ListMakerApplication
        thisApp.shouldDownloadLists = true
        val getListNamesTask = ListMakerApplication.GetListNamesTask(this)
        val t = Thread(getListNamesTask)
        t.start()

        // Get the set of lists from the ListManager
        val listNames = ListManager.listNames

        // Get a reference to the mainList
        mainList = this.findViewById(R.id.main_list)

        // Create an adapter for the list
        mainAdapter = MainAdapter(this)

        // Set the listNames for the adapter
        mainAdapter.listNames = listNames

        // Set the adapter for the mainList
        mainList.adapter = mainAdapter

        // Register the mainList for a context menu to delete the items
        registerForContextMenu(mainList)
    }

    override fun onResume() {
        super.onResume()

        // Register to receive a broadcast when the list names have been downloaded
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                IntentFilter("listNamesDownloaded"))

        // Register to receive a broadcast when a list has been added
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                IntentFilter("listAdded"))

        // Register to receive a broadcast when a list has been removed
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                IntentFilter("listRemoved"))

        // Get the set of saved lists from the ListManager
        val listNames = ListManager.listNames

        // Set the listNames for the adapter
        mainAdapter.listNames = listNames
        mainAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()

        // Unregister for broadcasts
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_main_sighout) {
            AWSProvider.getIdentityManager().signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    // This method presents the ListItemActivity
    fun presentListItemActivity(list: ListNamesDO) {

        // Create an intent to show the ListItemActivity
        val listItemIntent = Intent(this@MainActivity, ListItemActivity::class.java)
        listItemIntent.putExtra("listNameId", list.nameId)
        startActivity(listItemIntent)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.table_row_delete, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.menu_table_row_delete) {

            // Delete this list
            removeList(mainAdapter.listNames[tempSelectedList])

            return true
        }
        else {
            return super.onContextItemSelected(item)
        }
    }

    // This method adds a new list
    fun addList(listName: String) {

        // Add this list
        val addListTask = ListMakerApplication.AddListTask(listName, this)
        val t = Thread(addListTask)
        t.start()
    }

    // This method removes the given list
    private fun removeList(list: ListNamesDO) {

        // Remove this list
        val deleteListTask = ListMakerApplication.DeleteListTask(list, this)
        val t = Thread(deleteListTask)
        t.start()
    }

    ////////////////////////////////
    // Data requests and handling //
    ////////////////////////////////

    // This object responds to broadcasts
    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == "listNamesDownloaded" ||
                    intent.action == "listRemoved") {

                // Get the listNames from the ListManager and update the mainAdapter
                runOnUiThread {
                    mainAdapter.listNames = ListManager.listNames
                    mainAdapter.notifyDataSetChanged()
                }
            }
            else if (intent.action == "listAdded") {

                // Get the list from the intent
                val listNameId = intent.getStringExtra("listNameId")
                val list = ListManager.getListForId(listNameId)

                // Present the ListItemActivity after a new list has been added
                runOnUiThread { presentListItemActivity(list) }
            }
        }
    }
}
