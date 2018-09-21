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
import com.kgb.listmaker.activities.MainActivity
import com.kgb.listmaker.dataobjects.ListNamesDO
import com.kgb.listmaker.views.ListMakerViewHolders.DetailTextViewHolder


class MainAdapter(thisActivity: MainActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var thisActivity = thisActivity

    // The listNames array contains the names of the saved lists
    lateinit var listNames: ArrayList<ListNamesDO>

    // This method presents an alert that allows the user to input the name of a new list
    fun showNewListAlert() {

        // Create an alert
        val builder = AlertDialog.Builder(thisActivity)
        val inflater = thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = inflater.inflate(R.layout.dialog_one_text_input, null)
        builder.setTitle("New List Name")

        // Get references to the text input
        val input0 = thisView.findViewById<View>(R.id.editText1) as EditText

        // Set the values for the text inputs
        input0.hint = "New List"

        // Set the input type
        input0.inputType = InputType.TYPE_CLASS_TEXT

        // Set a listener for the positive button
        builder.setPositiveButton("Ok", { dialog, id ->

            // Get the text from the text input
            var newListName = input0.hint.toString()
            if (input0.text.toString() != "") {
                newListName = input0.text.toString()
            }

            // Add the new list and present the ListItemActivity
            thisActivity.addList(newListName)
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

        if (position == 0) {

            // Create new list row
            viewHolder.textLabel.text = thisActivity.getString(R.string.create_new_list_title)
            viewHolder.detailTextLabel.visibility = View.GONE

            // Set the listener for this row
            viewHolder.itemView.setOnClickListener { showNewListAlert() }
        }
        else {

            // Existing list row
            val thisList = listNames[position - 1]
            viewHolder.textLabel.text = thisList.name
            viewHolder.detailTextLabel.visibility = View.GONE

            // Add a long click listener so that the long press can be passed to the parent to present the context menu to delete this row
            viewHolder.itemView.setOnLongClickListener {

                // Set the tempSelectedList property of the activity
                thisActivity.tempSelectedList = position - 1
                false
            }

            // Set the listener for this row
            viewHolder.itemView.setOnClickListener {

                // Present the ListItemActivity with this list
                thisActivity.presentListItemActivity(listNames[position - 1])
            }
        }

    }

    override fun getItemCount(): Int {
        return listNames.size + 1
    }
}