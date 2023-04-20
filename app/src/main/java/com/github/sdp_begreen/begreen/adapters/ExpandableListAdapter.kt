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

class ExpandableListAdapter(
    private val context: Context,
    private val groups: List<Group>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return groups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return groups[groupPosition].items.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return groups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return groups[groupPosition].items[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    @SuppressLint("InflateParams")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        val group = getGroup(groupPosition) as Group

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_group, null)
        }

        val icon = view?.findViewById(R.id.list_group_icon) as ImageView
        val title = view.findViewById(R.id.list_group_title) as TextView

        icon.setImageResource(group.icon)
        title.text = group.title

        return view
    }

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

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item, null)
        }

        val title = view?.findViewById(R.id.list_item_title) as TextView

        title.text = item.title

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}