package org.frustra.featherweight;

public class KickVote extends Vote {
	public static final long threshold = 1000;
	public Player target;

	public KickVote(Player target) {
		super(KickVote.threshold);
		this.target = target;
	}
}
