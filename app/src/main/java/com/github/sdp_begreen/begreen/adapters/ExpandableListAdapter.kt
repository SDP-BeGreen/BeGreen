package com.github.sdp_begreen.begreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.TipsGroup
import com.github.sdp_begreen.begreen.models.Tips

// This adapter is responsible for displaying expandable lists with groups and their respective items.
class ExpandableListAdapter(
    private val context: Context,
    private val tipsGroups: List<TipsGroup>
) : BaseExpandableListAdapter() {

    companion object {

        // We display NB_OF_TIPS_PER_GROUP random tips per group.
        // If we cant to display different numbers of tips, we could remove this constant and insert a field "numberOfTips"
        // in each group instance.
        private val NB_OF_TIPS_PER_GROUP = 1
    }

    // Returns the number of groups in the list.
    override fun getGroupCount(): Int {
        return tipsGroups.size
    }

    // Returns the number of children (items) we would like to show in a group.
    override fun getChildrenCount(groupPosition: Int): Int {

        if (groupPosition < 0 || groupPosition >= tipsGroups.size) {
            throw IllegalArgumentException()
        }

        return NB_OF_TIPS_PER_GROUP
    }


    // Returns the group object at the specified position.
    override fun getGroup(groupPosition: Int): Any {
        if (groupPosition < 0 || groupPosition >= tipsGroups.size) {
            throw IllegalArgumentException()
        }
        return tipsGroups[groupPosition]
    }

    // Returns the randomly chosen child (item) object in the specified group.
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        if (groupPosition < 0 || groupPosition >= tipsGroups.size || childPosition < 0 || childPosition >= tipsGroups[groupPosition].tips.size) {
            throw IllegalArgumentException()
        }

        // generated random
        val rnds = (0..tipsGroups[groupPosition].tips.size-1).random()

        return tipsGroups[groupPosition].tips[rnds]
    }

    // Returns a unique identifier for a group.
    override fun getGroupId(groupPosition: Int): Long {
        if (groupPosition < 0 || groupPosition >= tipsGroups.size) {
            throw IllegalArgumentException()
        }
        return groupPosition.toLong()
    }

    // Returns a unique identifier for a child (item) within a group.
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        if (groupPosition < 0 || groupPosition >= tipsGroups.size) {
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
        val tipsGroup = getGroup(groupPosition) as TipsGroup

        // Inflate the group view if it hasn't been created yet.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_group, parent, false)
        }

        // Set the icon and title for the group view.
        val icon = view?.findViewById(R.id.list_group_icon) as ImageView
        val title = view.findViewById(R.id.list_group_title) as TextView

        icon.setImageResource(tipsGroup.icon)
        title.text = tipsGroup.title

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
        val tips = getChild(groupPosition, childPosition) as Tips

        // Inflate the item view if it hasn't been created yet.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item, parent, false)
        }

        // Set the title for the item view.
        val title = view?.findViewById(R.id.list_item_title) as TextView

        title.text = tips.title

        return view
    }

    // Indicates whether a child (item) is selectable.
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}