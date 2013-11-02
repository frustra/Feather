package org.frustra.featherweight;

public class KickVote extends Vote {
	public static final long Threshold = 1000;
	public Player target;

	public KickVote(Player target) {
		super(KickVote.Threshold);
		this.target = target;
	}
}
