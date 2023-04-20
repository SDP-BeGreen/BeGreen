package com.github.sdp_begreen.begreen.adapters

import android.annotation.SuppressLint
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

// This adapter is responsible for displaying expandable lists with groups and their respective items.
class ExpandableListAdapter(
    private val context: Context,
    private val groups: List<Group>
) : BaseExpandableListAdapter() {

    // Returns the number of groups in the list.
    override fun getGroupCount(): Int {
        return groups.size
    }

    // Returns the number of children (items) in a particular group.
    override fun getChildrenCount(groupPosition: Int): Int {
        return groups[groupPosition].items.size
    }

    // Returns the group object at the specified position.
    override fun getGroup(groupPosition: Int): Any {
        return groups[groupPosition]
    }

    // Returns the child (item) object in the specified group and child position.
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return groups[groupPosition].items[childPosition]
    }

    // Returns a unique identifier for a group.
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    // Returns a unique identifier for a child (item) within a group.
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    // Indicates whether the adapter has stable IDs for its items.
    override fun hasStableIds(): Boolean {
        return false
    }

    // Provides the view for the group header, including its icon and title.
    @SuppressLint("InflateParams")
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
            view = inflater.inflate(R.layout.list_group, null)
        }

        // Set the icon and title for the group view.
        val icon = view?.findViewById(R.id.list_group_icon) as ImageView
        val title = view.findViewById(R.id.list_group_title) as TextView

        icon.setImageResource(group.icon)
        title.text = group.title

        return view
    }

    // Provides the view for each child (item) in the group.
    @SuppressLint("InflateParams")
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
            view = inflater.inflate(R.layout.list_item, null)
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