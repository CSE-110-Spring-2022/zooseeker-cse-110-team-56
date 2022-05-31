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
import edu.ucsd.cse110.team56.zooseeker.path.Path;

class DirectionInfo {
    GraphEdge edge;
    EdgeInfo edgeInfo;
    NodeInfo destinationInfo;

    DirectionInfo(Context context, GraphEdge edge) {
        this.edge = edge;
        this.edgeInfo = ZooDatabase.getSingleton(context).zooDao().getEdge(edge.getId());
        this.destinationInfo = ZooDatabase.getSingleton(context).zooDao().getNode(edge.getDestination());
    }

    DirectionInfo(Context context, String exhibit) {
        this.edge = null;
        this.edgeInfo = null;
        this.destinationInfo = ZooDatabase.getSingleton(context).zooDao().getNode(exhibit);
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
    private Path rawInfo = null;
    private List<DirectionInfo> infos = Collections.emptyList();

    public void refreshPaths(Context context) {
        final var sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean shouldDisplayBriefDirections = sharedPref.getBoolean(context.getString(R.string.directions_style_is_brief), true);

        this.infos.clear();
        this.infos = this.rawInfo.path.getEdgeList().stream().map(e -> new DirectionInfo(context, e)).collect(Collectors.toList());

        if (this.rawInfo.path.getEdgeList().size() == 0) {
            this.infos.add(new DirectionInfo(context, this.rawInfo.endInfo.getNavigatableId()));
        } else if (shouldDisplayBriefDirections) {
            List<DirectionInfo> infos = new ArrayList<>();
            for(DirectionInfo info: this.infos) {
                if (infos.isEmpty() || !infos.get(infos.size() - 1).edgeInfo.street.equals(info.edgeInfo.street)) {
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

    public void setPaths(Context context, Path paths) {
        this.rawInfo = paths;
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
        holder.setEdge(
                infos.get(position),
                position != 0 ? infos.get(position - 1).edgeInfo.street : null,
                position + 1,
                position != infos.size() - 1 ? null : this.rawInfo.endInfo.getActualExhibit());
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

        public void setEdge(DirectionInfo info, String previousStreet, int index, NodeInfo childExhibit) {
            this.info = info;
            String findText = "";
            if (childExhibit != null && !childExhibit.name.equals(info.destinationInfo.name)) {
                findText = String.format("and find %s", childExhibit.name);
            }

            if (info.edge == null) {
                this.textView.setText(String.format("Arrive at %s %s", info.destinationInfo.name, findText));
            } else if (info.edgeInfo.street.equals(previousStreet)) {
                this.textView.setText(String.format("Continue on %s %s ft towards %s %s", info.edgeInfo.street, info.edge.getLength(), info.destinationInfo.name, findText));
            } else {
                this.textView.setText(String.format("Proceed on %s %s ft towards %s %s", info.edgeInfo.street, info.edge.getLength(), info.destinationInfo.name, findText));
            }
            this.numView.setText(String.valueOf(index));
        }
    }
}
