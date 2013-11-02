package org.frustra.featherweight;

import java.io.File;
import java.util.HashMap;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class Database {
	private static final int Version = 1;

	private static HashMap<String, Player> players;
	private static SqlJetDb db = null;

	public static void init() throws Exception {
		players = new HashMap<String, Player>();
		File file = new File("./feather.sqlite");
		db = SqlJetDb.open(file, true);
		migrate(Version);
	}

	public static void close() {
		try {
			db.close();
		} catch (SqlJetException e) {
			e.printStackTrace();
		}
	}

	public static Player loadPlayer(String name) {
		Player p = fetchPlayer(name);
		if (p != null) {
			p.seen();
			savePlayer(p);
			players.put(name, p);
		}
		return p;
	}

	public static void unloadPlayer(String name) {
		Player p = players.remove(name);
		if (p != null) {
			p.seen();
			savePlayer(p);
		}
	}

	public static Player fetchPlayer(String name) {
		Player p = players.get(name);
		if (p == null) {
			p = new Player(name);
			try {
				db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
				ISqlJetTable playersTable = db.getTable("players");
				ISqlJetCursor c = playersTable.lookup(playersTable.getPrimaryKeyIndexName(), name);
				if (!c.eof()) {
					p.karma = c.getInteger("karma");
					p.firstJoin = c.getInteger("firstJoin");
					p.lastSeen = c.getInteger("lastSeen");
				} else {
					p.firstJoin = p.seen();
				}
				c.close();
				db.commit();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					db.rollback();
				} catch (SqlJetException e1) {
					e1.printStackTrace();
				}
				return null;
			}
		}
		return p;
	}

	public static void savePlayer(Player p) {
		try {
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable playersTable = db.getTable("players");
			playersTable.insertOr(SqlJetConflictAction.REPLACE, p.name, p.karma, p.firstJoin, p.lastSeen);
			db.commit();
		} catch (Exception e) {
			try {
				db.rollback();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
	}

	private static int getVersion() throws SqlJetException {
		return db.getOptions().getUserVersion();
	}

	private static void migrate(int version) throws SqlJetException {
		if (version < 1) return;
		try {
			if (getVersion() < 1) {
				System.out.println("feather.sqlite not found, creating it now");
				db.getOptions().setAutovacuum(true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.createTable("create table players (name text not null primary key, karma int default 0, firstJoin int, lastSeen int)");
				db.getOptions().setUserVersion(1);
				db.commit();
			}
		} catch (SqlJetException e) {
			db.rollback();
			throw e;
		}
	}
}
