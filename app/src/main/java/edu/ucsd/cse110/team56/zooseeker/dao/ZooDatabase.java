package edu.ucsd.cse110.team56.zooseeker.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.team56.zooseeker.Converters;
import edu.ucsd.cse110.team56.zooseeker.Utility;
import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.entity.Graph;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

@Database(entities = {EdgeInfo.class, NodeInfo.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ZooDatabase extends RoomDatabase {
    private static ZooDatabase singleton = null;

    public abstract ZooDao zooDao();

    public synchronized static ZooDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ZooDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ZooDatabase makeDatabase(Context context) {
        ZooDatabase db = Room.inMemoryDatabaseBuilder(context, ZooDatabase.class)
                .allowMainThreadQueries()
                .build();

        List<NodeInfo> nodes = Utility.parseJson(context, "map/assets/sample_node_info.json", NodeInfo.class);
        db.zooDao().addNodes(nodes);
        List<EdgeInfo> edges = Utility.parseJson(context, "map/assets/sample_edge_info.json", EdgeInfo.class);
        db.zooDao().addEdges(edges);

        return db;
    }
}
