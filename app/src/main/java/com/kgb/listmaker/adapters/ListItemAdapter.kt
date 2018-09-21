package com.kgb.listmaker.adapters

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.kgb.listmaker.R
import com.kgb.listmaker.activities.ListItemActivity
import com.kgb.listmaker.dataobjects.ListItemsDO
import com.kgb.listmaker.views.ListMakerViewHolders.DetailTextViewHolder

class ListItemAdapter(thisActivity: ListItemActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var thisActivity = thisActivity

    // The listItems array contains the items in this list
    lateinit var listItems: ArrayList<ListItemsDO>

    // This method presents an alert that allows the user to input a new list item
    fun showNewItemAlert() {

        // Create an alert
        val builder = AlertDialog.Builder(thisActivity)
        val inflater = thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = inflater.inflate(R.layout.dialog_one_text_input, null)
        builder.setTitle("New List Item")

        // Get references to the text input
        val input0 = thisView.findViewById<View>(R.id.editText1) as EditText

        // Set the values for the text inputs
        input0.hint = "List Item"

        // Set the input type
        input0.inputType = InputType.TYPE_CLASS_TEXT

        // Set a listener for the positive button
        builder.setPositiveButton("Ok", { dialog, id ->

            // Get the text from the text input
            var newListItem = input0.hint.toString()
            if (input0.text.toString() != "") {
                newListItem = input0.text.toString()
            }

            // Add this new item to the list
            thisActivity.addItemToList(newListItem)
        })

        // Set the default negative button
        builder.setNegativeButton("Cancel", { dialog, id -> })

        // Display the alert
        builder.setView(thisView)
        val alert = builder.create()
        alert.show()
    }

    /////////////////////////////////
    // RecyclerViewAdapter methods //
    /////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(DetailTextViewHolder.layoutResource, parent, false)
        return DetailTextViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as DetailTextViewHolder

        // Create new item row
        if (position == 0) {
            viewHolder.textLabel.text = thisActivity.getString(R.string.add_new_item_title)
            viewHolder.detailTextLabel.visibility = View.GONE

            // Set the listener for this row
            viewHolder.itemView.setOnClickListener { showNewItemAlert() }
        }

        // List item row
        else {
            val thisListItem = listItems[position - 1]
            viewHolder.textLabel.text = thisListItem.item
            viewHolder.detailTextLabel.visibility = View.GONE

            // Add a long click listener so that the long press can be passed to the parent to present the context menu to delete this row
            viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?) : Boolean {

                    // Set the tempSelectedItem property of the activity
                    thisActivity.tempSelectedItem = position - 1
                    return false
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return listItems.size + 1
    }
}