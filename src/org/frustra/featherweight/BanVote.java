package org.frustra.featherweight;

public class BanVote extends Vote {
	public static final long Threshold = 2000;
	public Player target;

	public BanVote(Player target) {
		super(BanVote.Threshold);
		this.target = target;
	}
}