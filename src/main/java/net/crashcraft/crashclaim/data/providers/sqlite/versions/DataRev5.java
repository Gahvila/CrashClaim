package net.crashcraft.crashclaim.data.providers.sqlite.versions;

import co.aikar.idb.DB;
import net.crashcraft.crashclaim.data.providers.sqlite.DataVersion;

import java.sql.SQLException;

public class DataRev5 implements DataVersion {
    @Override
    public int getVersion() {
        return 5;
    }

    @Override
    public void executeUpgrade(int fromRevision) throws SQLException {
        DB.executeUpdate("CREATE TABLE \"claimblocks\" (\n" +
                "\t\"player_id\"\tINTEGER NOT NULL UNIQUE,\n" +
                "\t\"amount\"\tINTEGER DEFAULT 0,\n" +
                "\tPRIMARY KEY(\"player_id\"),\n" +
                "\tFOREIGN KEY(\"player_id\") REFERENCES \"players\"(\"id\") ON DELETE CASCADE\n" +
                ");");
        DB.executeUpdate("CREATE TABLE \"playtime\" (\n" +
                "\t\"player_id\"\tINTEGER UNIQUE,\n" +
                "\t\"timeSinceReward\"\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\tFOREIGN KEY(\"player_id\") REFERENCES \"players\"(\"id\") ON DELETE CASCADE,\n" +
                "\tPRIMARY KEY(\"player_id\")\n" +
                ")");
    }
}
