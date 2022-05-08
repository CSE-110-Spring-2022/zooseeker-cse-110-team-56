package edu.ucsd.cse110.team56.zooseeker.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class NodeInfoAdapter extends ArrayAdapter<NodeInfo> {
    public NodeInfoAdapter(@NonNull Context context, int resource, @NonNull List<NodeInfo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppCompatCheckedTextView view = (AppCompatCheckedTextView) super.getView(position, convertView, parent);
        view.setText(this.getItem(position).name);
        return view;
    }

}
