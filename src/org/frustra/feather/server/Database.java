package org.frustra.feather.server;

import java.io.File;

import org.frustra.feather.server.logging.LogManager;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class Database {
	private static final int Version = 1;

	private SqlJetDb db = null;

	public Database() throws Exception {
		File file = new File("./feather.sqlite");
		db = SqlJetDb.open(file, true);
		migrate(Version);
	}

	public void close() {
		try {
			db.close();
		} catch (SqlJetException e) {
			e.printStackTrace();
		}
	}

	public Player createPlayer(String name) {
		Player p = new Player(name);
		try {
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable playersTable = db.getTable("players");
			ISqlJetCursor c = playersTable.lookup(playersTable.getPrimaryKeyIndexName(), name);
			if (!c.eof()) {
				p.karma = c.getInteger("karma");
				p.firstJoin = c.getInteger("firstJoin");
				p.lastSeen = c.getInteger("lastSeen");
				p.level = c.getInteger("level");
			} else {
				p.firstJoin = p.lastSeen = System.currentTimeMillis() / 1000;
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
		return p;
	}

	public Player fetchPlayer(String name) {
		Player p = null;
		try {
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable playersTable = db.getTable("players");
			ISqlJetCursor c = playersTable.lookup(playersTable.getPrimaryKeyIndexName(), name);
			if (!c.eof()) {
				p = new Player(name);
				p.karma = c.getInteger("karma");
				p.firstJoin = c.getInteger("firstJoin");
				p.lastSeen = c.getInteger("lastSeen");
				p.level = c.getInteger("level");
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
		return p;
	}

	public void savePlayer(Player p) {
		try {
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable playersTable = db.getTable("players");
			playersTable.insertOr(SqlJetConflictAction.REPLACE, p.name, p.karma, p.firstJoin, p.lastSeen, p.level);
			db.commit();
		} catch (Exception e) {
			try {
				db.rollback();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
	}

	private int getVersion() throws SqlJetException {
		return db.getOptions().getUserVersion();
	}

	private void migrate(int version) throws SqlJetException {
		if (version < 1) return;
		try {
			if (getVersion() < 1) {
				LogManager.getLogger().info("feather.sqlite not found, creating it now");
				db.getOptions().setAutovacuum(true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.createTable("create table players (name text not null primary key, karma real default 0, firstJoin int, lastSeen int, level int default 0)");
				db.getOptions().setUserVersion(1);
				db.commit();
			}
		} catch (SqlJetException e) {
			db.rollback();
			throw e;
		}
	}
}
