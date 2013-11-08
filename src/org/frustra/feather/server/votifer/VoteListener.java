package org.frustra.feather.server.votifer;

public interface VoteListener {
	public void voteReceived(String service, String username, String address, String timestamp);
}
