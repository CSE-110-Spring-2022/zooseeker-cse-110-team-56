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
    List<NodeInfo> list;
    public NodeInfoAdapter(@NonNull Context context, int resource, @NonNull List<NodeInfo> objects) {
        super(context, resource, objects);
        this.list = new ArrayList<>(objects);
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
                final var results = new FilterResults();
                final var searchString = constraint.toString().toLowerCase();

                final List<NodeInfo> nodes = new ArrayList<>(list);

                // empty case

                if (searchString.length() == 0) {
                    results.values = list;
                    results.count = list.size();
                    return results;
                }

                // default case

                final ArrayList<NodeInfo> resultList = new ArrayList<>();

                for(NodeInfo node : nodes) {
                    boolean shouldAddCurrentNode = false;

                    // match name to searchString
                    shouldAddCurrentNode |= node.name.toLowerCase().contains(searchString);

                    // match tags to searchString
                    for (String tag : node.tags) {
                        shouldAddCurrentNode |= tag.toLowerCase().contains(searchString);
                    }

                    // add to resultList
                    if (shouldAddCurrentNode) {
                        resultList.add(node);
                    }
                }

                results.values = resultList;
                results.count = resultList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<NodeInfo> nodeList = (ArrayList<NodeInfo>) results.values;

                clear();

                for (NodeInfo node : nodeList) {
                    add(node);
                }
            }

        };
    }
}
