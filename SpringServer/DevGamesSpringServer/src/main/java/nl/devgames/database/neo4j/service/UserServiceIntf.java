package nl.devgames.database.neo4j.service;

import java.util.Map;

/**
 * Created by Marcel on 19-3-2016.
 */
public interface UserServiceIntf {

    Iterable<Map<String,Object>> getOwnUser();
}
