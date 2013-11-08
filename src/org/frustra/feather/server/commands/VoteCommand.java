package org.frustra.feather.server.commands;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Command;
import org.frustra.feather.server.CommandException;
import org.frustra.feather.server.CommandUsageException;
import org.frustra.feather.server.Entity;
import org.frustra.feather.server.Player;
import org.frustra.feather.server.voting.KickVote;

public class VoteCommand extends Command {

	public String getName() {
		return "vote";
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length >= 1 && arguments.length <= 2) {
			Player sourcePlayer = source.getPlayer();
			if (sourcePlayer == null || sourcePlayer.karma <= 0) {
				throw new CommandException("You need positive karma to use this command");
			}

			if (arguments[0].equals("kick") && arguments.length == 2) {
				Player target = Bootstrap.server.getPlayer(arguments[1]);
				if (target != null) {
					if (sourcePlayer.equals(target)) throw new CommandException("You cannot vote kick yourself");
					KickVote vote = Bootstrap.server.activeKickVotes.get(target);
					if (vote == null) {
						Command.execute("tellraw @a {\"text\":\"A vote kick has been initiated on " + target.name + "\",\"color\":\"blue\"}");
						vote = new KickVote(target);
						for (Player p : Bootstrap.server.getPlayers()) {
							if (!p.equals(sourcePlayer) && !p.equals(target)) {
								Command.execute("tellraw " + p.name + " {\"text\":\"Use /vote <yes|no> [player] to respond.\",\"color\":\"blue\"}");
							}
						}
					}

					vote.addVote(sourcePlayer, true);
				} else {
					throw new CommandException("This player is not currently online.");
				}
			} else if (arguments[0].equals("yes") || arguments[0].equals("no")) {
				if (Bootstrap.server.activeKickVotes.size() < 1) {
					throw new CommandException("There are no active votes, use /vote kick to start one");
				} else if (arguments.length == 2) {
					Player target = Bootstrap.server.getPlayer(arguments[1]);
					if (sourcePlayer.equals(target)) throw new CommandException("You cannot vote on yourself");
					KickVote vote = Bootstrap.server.activeKickVotes.get(target);
					if (vote != null) {
						vote.addVote(sourcePlayer, arguments[0].equals("yes"));
					} else {
						throw new CommandException("There are no active votes for this player.");
					}
				} else {
					if (Bootstrap.server.activeKickVotes.size() > 2) {
						throw new CommandException("There is more than one active vote, please specify a player");
					} else {
						KickVote vote = Bootstrap.server.activeKickVotes.values().iterator().next();
						vote.addVote(sourcePlayer, arguments[0].equals("yes"));
					}
				}
			} else {
				throw new CommandUsageException(this);
			}
		} else {
			throw new CommandUsageException(this);
		}
	}

	public boolean hasPermission(Entity source) {
		return true;
	}

	public String getUsage(Entity source) {
		return "/vote <kick|yes|no> [player]";
	}
}
