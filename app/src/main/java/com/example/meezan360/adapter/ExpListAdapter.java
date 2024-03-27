package com.example.meezan360.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.meezan360.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExpListAdapter extends BaseExpandableListAdapter {

    private final Context _context;
    private final List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private final HashMap<String, List<String>> _listDataChild;
    private final List<Integer> _icon;           //icon List


    public ExpListAdapter(Context context, List<String> listDataHeader,
                          HashMap<String, List<String>> listChildData,
                          List<Integer> icons) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._icon = icons;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


//        if (isLastChild)
//        {
//            convertView.setPadding(0, 0, 0, 30);
//        }


        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);

        }

        TextView txtListChild = convertView.findViewById(R.id.lblListItem);

        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        int size = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!Objects.equals(this._listDataChild.get(this._listDataHeader.get(groupPosition)), null)) {
                size = this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
            }
        }
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

//        this condition is use to hide and show last image of side bar view which is arrow
//        if (_listDataChild.get(_listDataHeader.get(groupPosition))!=null && !_listDataChild.get(_listDataHeader.get(groupPosition)).isEmpty()) {

//            convertView.findViewById(R.id.expNode).setVisibility(View.VISIBLE);
//        }else{

//            convertView.findViewById(R.id.expNode).setVisibility(View.GONE);
//        }

        if (_listDataHeader.get(groupPosition)!=null &&
                !_listDataHeader.get(groupPosition).isEmpty()
                && groupPosition == 0 ) {
            convertView.findViewById(R.id.lblListHeader).setBackgroundColor(_context.getResources().getColor(R.color.purple_dark));
            ((TextView) convertView.findViewById(R.id.lblListHeader)).setTextColor(_context.getResources().getColor(R.color.white));

        }



        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
//      lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);



        ConstraintLayout constraintLayout = convertView.findViewById(R.id.main_group);

        if (isExpanded) {
//            convertView.findViewById(R.id.expNode).setVisibility(View.GONE);
            constraintLayout.setBackgroundColor(ContextCompat.getColor(_context, R.color.zxing_transparent));
            lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        } else {
//            if (_listDataChild.get(_listDataHeader.get(groupPosition))!=null && !_listDataChild.get(_listDataHeader.get(groupPosition)).isEmpty()) {
//                convertView.findViewById(R.id.expNode).setVisibility(View.VISIBLE);
//            }else{
//                convertView.findViewById(R.id.expNode).setVisibility(View.GONE);
//            }
            constraintLayout.setBackgroundColor(ContextCompat.getColor(_context, R.color.zxing_transparent));
        }


        return convertView;
    }



    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;

        //txtListChild.setText(childText);

    }
}
