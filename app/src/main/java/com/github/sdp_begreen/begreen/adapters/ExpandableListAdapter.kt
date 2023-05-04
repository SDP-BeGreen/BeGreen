package com.github.sdp_begreen.begreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.Group
import com.github.sdp_begreen.begreen.models.Item

const val NBQUOTES = 1

// This adapter is responsible for displaying expandable lists with groups and their respective items.
class ExpandableListAdapter(
    private val context: Context,
    private val groups: List<Group>
) : BaseExpandableListAdapter() {

    // Returns the number of groups in the list.
    override fun getGroupCount(): Int {
        return groups.size
    }

    // Returns the number of children (items) we would like to show in a group.
    override fun getChildrenCount(groupPosition: Int): Int {
        if (groupPosition < 0 || groupPosition >= groups.size) {
            throw IllegalArgumentException()
        }
        return NBQUOTES
    }


    // Returns the group object at the specified position.
    override fun getGroup(groupPosition: Int): Any {
        if (groupPosition < 0 || groupPosition >= groups.size) {
            throw IllegalArgumentException()
        }
        return groups[groupPosition]
    }

    // Returns the randomly chosen child (item) object in the specified group.
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        if (groupPosition < 0 || groupPosition >= groups.size || childPosition < 0 || childPosition >= groups[groupPosition].items.size) {
            throw IllegalArgumentException()
        }

        // generated random
        val rnds = (0..groups[groupPosition].items.size-1).random()

        return groups[groupPosition].items[rnds]
    }

    // Returns a unique identifier for a group.
    override fun getGroupId(groupPosition: Int): Long {
        if (groupPosition < 0 || groupPosition >= groups.size) {
            throw IllegalArgumentException()
        }
        return groupPosition.toLong()
    }

    // Returns a unique identifier for a child (item) within a group.
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        if (groupPosition < 0 || groupPosition >= groups.size || childPosition < 0 || childPosition >= groups[groupPosition].items.size) {
            throw IllegalArgumentException()
        }
        return childPosition.toLong()
    }

    // Indicates whether the adapter has stable IDs for its items.
    override fun hasStableIds(): Boolean {
        return false
    }

    // Provides the view for the group header, including its icon and title.
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        val group = getGroup(groupPosition) as Group

        // Inflate the group view if it hasn't been created yet.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_group, parent, false)
        }

        // Set the icon and title for the group view.
        val icon = view?.findViewById(R.id.list_group_icon) as ImageView
        val title = view.findViewById(R.id.list_group_title) as TextView

        icon.setImageResource(group.icon)
        title.text = group.title

        return view
    }

    // Provides the view for each child (item) in the group.
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        val item = getChild(groupPosition, childPosition) as Item

        // Inflate the item view if it hasn't been created yet.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item, parent, false)
        }

        // Set the title for the item view.
        val title = view?.findViewById(R.id.list_item_title) as TextView

        title.text = item.title

        return view
    }

    // Indicates whether a child (item) is selectable.
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}