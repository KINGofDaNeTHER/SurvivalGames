package me.kingofdanether.survivalgames.arena;

import org.bukkit.block.Sign;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.sign.GameSign;
import me.kingofdanether.survivalgames.util.StringUtils;

public class ArenaSign extends GameSign {
	
	public ArenaSign(Sign sign, int id, Arena a) {
		super(sign, id, a);
		this.getSign().setLine(0, StringUtils.colorize("&6[&a" + SurvivalGames.getInstance().getName() + "&6]"));
		this.getSign().setLine(1, StringUtils.colorize("&6" + a.getName()));
		this.getSign().update();
	}
	
	@Override
	public void update() {
		//SIGN LINES:
		//(0) [<plugin name>]
		//(1) <arena name>
		//(2) 0/<players in game>
		//(3) <game state>
		if (a.isEnabled()) {
			this.getSign().setLine(0, StringUtils.colorize("&6[&a" + SurvivalGames.getInstance().getName() + "&6]"));
			this.getSign().setLine(1, StringUtils.colorize("&a" + a.getName()));
			if (a.getGameState().toString().startsWith("LOBBY")) {
				if (a.getPlayersAliveAmount() < a.getMinPlayers()) {
					this.getSign().setLine(2, StringUtils.colorize("&2" + a.getPlayersAliveAmount() + "/" + a.getMaxPlayers()));
				} else {
					this.getSign().setLine(2, StringUtils.colorize("&6" + a.getPlayersAliveAmount() + "/" + a.getMaxPlayers()));
				}
			} else {
				this.getSign().setLine(2, StringUtils.colorize("&c" + a.getPlayersAliveAmount() + "/" + a.getMaxPlayers()));
			}
			switch (a.getGameState()) {
			case INGAME_COUNTDOWN:
				this.getSign().setLine(3, StringUtils.colorize("&c(InGame) Starting"));
				break;
			case INGAME_DEATHMATCH:
				this.getSign().setLine(3, StringUtils.colorize("&c(InGame) D-Match"));
				break;
			case INGAME_DEATHMATCH_COUNTOWN:
				this.getSign().setLine(3, StringUtils.colorize("&c(InGame) D-Match"));
				break;
			case INGAME_DEATHMATCH_WAITING:
				this.getSign().setLine(3, StringUtils.colorize("&c(InGame) D-Match"));
				break;
			case INGAME_STARTED:
				this.getSign().setLine(3, StringUtils.colorize("&c(In Game)"));
				break;
			case INGAME_WAITING:
				this.getSign().setLine(3, StringUtils.colorize("&c(InGame) Starting"));
				break;
			case LOBBY_COUNTDOWN:
				this.getSign().setLine(3, StringUtils.colorize("&6(Lobby) Waiting"));
				break;
			case LOBBY_WAITING:
				this.getSign().setLine(3, StringUtils.colorize("&2(Lobby) Waiting"));
				break;
			case POSTGAME_ENDING:
				this.getSign().setLine(3, StringUtils.colorize("&4(InGame) Ending"));
				break;
			case POSTGAME_REBUILDING:
				this.getSign().setLine(3, StringUtils.colorize("&6(Rebuilding)"));
				break;
			default:
				break;
			}
		} else {
			this.getSign().setLine(0, StringUtils.colorize("&4[&c" + SurvivalGames.getInstance().getName() + "&4]"));
			this.getSign().setLine(1, StringUtils.colorize("&4" + a.getName()));
			this.getSign().setLine(2, StringUtils.colorize("&c" + a.getPlayersAliveAmount() + "/" + a.getMaxPlayers()));
			this.getSign().setLine(3, StringUtils.colorize("&4(Disabled)"));
		}
		this.getSign().update();
	}

}
