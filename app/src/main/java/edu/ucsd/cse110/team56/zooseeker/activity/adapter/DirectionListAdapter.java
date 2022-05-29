package edu.ucsd.cse110.team56.zooseeker.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.CombinedGraphEdge;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

class DirectionInfo {
    GraphEdge edge;
    EdgeInfo info;

    DirectionInfo(Context context, GraphEdge edge) {
        this.edge = edge;
        this.info = ZooDatabase.getSingleton(context).zooDao().getEdge(edge.getId());
    }
}
public class DirectionListAdapter extends RecyclerView.Adapter<DirectionListAdapter.ViewHolder> {

    public DirectionListAdapter(Context context) {
        super();
        final var sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener((pref, key)->{
            if (key.equals(context.getString(R.string.directions_style_is_brief))) {
                this.refreshPaths(context);
            }
        });

    }
    private List<GraphEdge> rawInfo = Collections.emptyList();
    private List<DirectionInfo> infos = Collections.emptyList();

    public void refreshPaths(Context context) {
        final var sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean shouldDisplayBriefDirections = sharedPref.getBoolean(context.getString(R.string.directions_style_is_brief), true);

        this.infos.clear();
        this.infos = this.rawInfo.stream().map(e -> new DirectionInfo(context, e)).collect(Collectors.toList());

        if (shouldDisplayBriefDirections) {
            List<DirectionInfo> infos = new ArrayList<>();
            for(DirectionInfo info: this.infos) {
                if (infos.isEmpty() || !infos.get(infos.size() - 1).info.street.equals(info.info.street)) {
                    infos.add(new DirectionInfo(context, new CombinedGraphEdge(info.edge)));
                } else {
                    ((CombinedGraphEdge)infos.get(infos.size() - 1).edge).addEdge(info.edge);
                }
            }
            this.infos.clear();
            this.infos = infos;
        }
        notifyDataSetChanged();
    }

    public void setPaths(Context context, List<GraphEdge> paths) {
        this.rawInfo.clear();
        this.rawInfo = new ArrayList<>(paths);
        this.refreshPaths(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.direction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setEdge(infos.get(position), position != 0 ? infos.get(position - 1).info.street : null,position + 1);
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    @Override
    public long getItemId(int position) {
        return infos.get(position).hashCode();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView numView;
        private DirectionInfo info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.direction_text);
            this.numView = itemView.findViewById(R.id.direction_num_text);
        }

        public DirectionInfo getEdge() {
            return info;
        }

        public void setEdge(DirectionInfo info, String previousStreet, int index) {
            NodeInfo destination = ZooDatabase.getSingleton(textView.getContext()).zooDao().getNode(info.edge.getDestination());
            this.info = info;
            if (info.info.street.equals(previousStreet)) {
                this.textView.setText(String.format("Continue on %s %s ft towards %s", info.info.street, info.edge.getLength(), destination.name));
            } else {
                this.textView.setText(String.format("Proceed on %s %s ft towards %s", info.info.street, info.edge.getLength(), destination.name));
            }
            this.numView.setText(String.valueOf(index));
        }
    }
}
