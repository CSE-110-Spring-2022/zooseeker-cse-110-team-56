package edu.ucsd.cse110.team56.zooseeker.dao;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.misc.JsonReader;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

@Database(entities = {EdgeInfo.class, NodeInfo.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ZooDatabase extends RoomDatabase {
    private static ZooDatabase singleton = null;

    public abstract ZooDao zooDao();

    /**
     * Get an instance of the database
     * @param context application context
     * @return database instance
     */
    public synchronized static ZooDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ZooDatabase.makeDatabase(context);
        }

        return singleton;
    }

    @VisibleForTesting
    public synchronized static void refreshDb(Context context) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = ZooDatabase.makeDatabase(context);
    }

    private static ZooDatabase makeDatabase(Context context) {
        ZooDatabase db = Room.inMemoryDatabaseBuilder(context, ZooDatabase.class)
                .allowMainThreadQueries()
                .build();

        List<NodeInfo> nodes = JsonReader.parseJsonList(context, "map/assets/sample_vertex_info.json", NodeInfo.class);
        db.zooDao().addNodes(nodes);
        List<EdgeInfo> edges = JsonReader.parseJsonList(context, "map/assets/sample_edge_info.json", EdgeInfo.class);
        db.zooDao().addEdges(edges);

        return db;
    }
}
