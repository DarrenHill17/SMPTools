package org.phlyer.smptools.Waypoints;

import org.apache.commons.lang3.SerializationUtils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class WaypointDataType implements PersistentDataType<byte[], ArrayList<Waypoint>> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ArrayList<Waypoint>> getComplexType() {
        return (Class<ArrayList<Waypoint>>) (Class<?>) ArrayList.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull ArrayList<Waypoint> waypoints, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return SerializationUtils.serialize(waypoints);
    }

    @Override
    public ArrayList<Waypoint> fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        try (InputStream is = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(is)) {
            return (ArrayList<Waypoint>) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
