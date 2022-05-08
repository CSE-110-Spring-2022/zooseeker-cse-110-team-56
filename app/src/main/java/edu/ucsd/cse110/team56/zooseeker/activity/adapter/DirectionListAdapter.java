package edu.ucsd.cse110.team56.zooseeker.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.GraphEdge;

public class DirectionListAdapter extends RecyclerView.Adapter<DirectionListAdapter.ViewHolder> {
    private List<GraphEdge> paths = Collections.emptyList();
    private List<EdgeInfo> infos = Collections.emptyList();

    public void setPaths(List<GraphEdge> paths, Context context) {
        this.paths.clear();
        this.paths = paths;
        this.infos.clear();
        ZooDao dao = ZooDatabase.getSingleton(context).zooDao();
        this.infos = this.paths.stream().map(e -> dao.getEdge(e.getId())).collect(Collectors.toList());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.direction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setEdge(paths.get(position), infos.get(position), position != 0 ? infos.get(position - 1).street : null,position + 1);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    @Override
    public long getItemId(int position) {
        return paths.get(position).hashCode();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView numView;
        private GraphEdge edge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.direction_text);
            this.numView = itemView.findViewById(R.id.direction_num_text);
        }

        public GraphEdge getEdge() {
            return edge;
        }

        public void setEdge(GraphEdge edge, EdgeInfo info, String previousStreet, int index) {
            NodeInfo destination = ZooDatabase.getSingleton(textView.getContext()).zooDao().getNode(edge.getDestination());
            if (info.street.equals(previousStreet)) {
                this.textView.setText(String.format("Continue on %s %s ft towards %s", info.street, edge.getLength(), destination.name));
            } else {
                this.textView.setText(String.format("Proceed on %s %s ft towards %s", info.street, edge.getLength(), destination.name));
            }
            this.numView.setText(String.valueOf(index));
        }
    }
}
