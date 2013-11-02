package org.frustra.feather.voting;

import org.frustra.feather.server.Player;


public class KickVote extends Vote {
	public static final long Threshold = 1000;
	public Player target;

	public KickVote(Player target) {
		super(KickVote.Threshold);
		this.target = target;
	}
}
