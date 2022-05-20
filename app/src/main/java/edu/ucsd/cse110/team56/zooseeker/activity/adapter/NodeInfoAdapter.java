package edu.ucsd.cse110.team56.zooseeker.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class NodeInfoAdapter extends ArrayAdapter<NodeInfo> {
    List<NodeInfo> objects;
    public NodeInfoAdapter(@NonNull Context context, int resource, @NonNull List<NodeInfo> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppCompatCheckedTextView view = (AppCompatCheckedTextView) super.getView(position, convertView, parent);
        view.setText(this.getItem(position).name);
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String prefix = constraint.toString().toLowerCase();

                if (prefix.length() == 0)
                {
                    ArrayList<NodeInfo> list = new ArrayList<>(objects);
                    results.values = list;
                    results.count = list.size();
                }
                else
                {
                    final ArrayList<NodeInfo> list = new ArrayList<>();

                    for(NodeInfo node: objects) {
                        if (node.name.toLowerCase().contains(prefix)) {
                            list.add(node);
                        } else {
                            for (String tag: node.tags) {
                                if (tag.toLowerCase().contains(prefix)) {
                                    list.add(node);
                                    break;
                                }
                            }
                        }
                    }

                    results.values = list;
                    results.count = list.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<NodeInfo> nodeList = (ArrayList<NodeInfo>)results.values;

                clear();
                for (NodeInfo node: nodeList)
                {
                    add(node);
                }
            }

        };
    }
}
