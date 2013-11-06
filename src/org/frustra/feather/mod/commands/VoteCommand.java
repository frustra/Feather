package org.frustra.feather.mod.commands;

import org.frustra.feather.mod.Bootstrap;
import org.frustra.feather.mod.Command;
import org.frustra.feather.mod.server.Entity;
import org.frustra.feather.mod.server.Player;
import org.frustra.feather.mod.voting.KickVote;

public class VoteCommand extends Command {

	public String getName() {
		return "vote";
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length >= 1 && arguments.length <= 2) {
			Player sourcePlayer = source.getPlayer();
			if (sourcePlayer == null || sourcePlayer.karma <= 0) {
				source.sendMessage("You need positive karma to use this command.");
				return;
			}
			
			if (arguments[0].equals("kick") && arguments.length == 2) {
				Player target = Bootstrap.server.getPlayer(arguments[1]);
				if (target != null) {
					KickVote vote = Bootstrap.server.activeKickVotes.get(target);
					if (vote == null) {
						vote = Bootstrap.server.activeKickVotes.put(target, new KickVote(target));
						Command.execute("tellraw @a {\"text\":\"A vote kick has been initiated on " + target.name + "\",\"color\":\"dark_blue\"}");
						Command.execute("tellraw @a {\"text\":\"Use /vote <yes|no> to respond.\",\"color\":\"dark_blue\"}");
					}

					vote.addVote(sourcePlayer, true);
				} else {
					source.sendMessage("This player is not currently online.");
					return;
				}
			} else if (arguments[0].equals("yes") || arguments[0].equals("no")) {
				if (Bootstrap.server.activeKickVotes.size() < 1) {
					source.sendMessage("There are no active votes, use /vote kick to start one.");
					return;
				} else if (arguments.length == 2) {
					Player target = Bootstrap.server.getPlayer(arguments[1]);
					KickVote vote = Bootstrap.server.activeKickVotes.get(target);
					if (vote != null) {
						vote.addVote(sourcePlayer, arguments[0].equals("yes"));
					} else {
						source.sendMessage("There are no active votes for this player.");
						return;
					}
				} else {
					if (Bootstrap.server.activeKickVotes.size() > 2) {
						source.sendMessage("There is more than one active vote, please specify a player.");
						return;
					} else {
						KickVote vote = Bootstrap.server.activeKickVotes.values().iterator().next();
						vote.addVote(sourcePlayer, arguments[0].equals("yes"));
					}
				}
			} else {
				// TODO throw a ch
				source.sendMessage("Invalid usage.");
			}
		} else {
			// TODO throw a ch
			source.sendMessage("Invalid usage.");
		}
	}

	public boolean hasPermission(Entity source) {
		return true;
	}

	public String getUsage(Entity source) {
		return "/vote <kick|yes|no> [player]";
	}
}
