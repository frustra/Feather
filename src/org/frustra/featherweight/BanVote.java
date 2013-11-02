package org.frustra.featherweight;

public class BanVote extends Vote {
	public static final long threshold = 2000;
	public Player target;

	public BanVote(Player target) {
		super(BanVote.threshold);
		this.target = target;
	}
}
