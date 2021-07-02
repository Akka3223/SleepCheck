package me.Akka.SleepCheck;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.earth2me.essentials.Essentials;

import net.md_5.bungee.api.ChatColor;
public class Listeners implements Listener {
    @EventHandler
    public void OnPlayerBedEnterEvent(PlayerBedEnterEvent event) {
    	if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
    	if ( day() ) return;
		int PercentINT;
    	if(Main.plugin.getConfig().getString("Percentage") != null) {
			PercentINT = Integer.parseInt(Main.plugin.getConfig().getString("Percentage"));
		} else {
			PercentINT = 50;
		}

		int percentage = (PercentINT * PlayerCount()) / 100;
		if((int)Math.floor(percentage) <= 1) {
			World world = Bukkit.getServer().getWorld("world");
			assert world != null;
			world.setTime(0);
			String Message = Main.plugin.getConfig().getString("Skip-Message");
			Bukkit.getServer().broadcastMessage((ChatColor.translateAlternateColorCodes('&', translate(Message))));
			return;
		}
		if((int)Math.floor(percentage) > 1) {
			CheckHowMuchSleeping();
		}
    }
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
    	if(!day() ) {
    		CheckHowMuchSleeping();
    	}
    }
    public void CheckHowMuchSleeping()
    {
		int PercentINT = Integer.parseInt(Main.plugin.getConfig().getString("Percentage"));
		//int percentage = (int)(PercentINT * PlayerCount()) / 100;

	    if(SleepingPlayerCount() >= (int)Math.floor((PercentINT * PlayerCount()) / 100.0)){
	    	World world = Bukkit.getServer().getWorld("world");
			assert world != null;
			world.setTime(0);
	    	String Message = Main.plugin.getConfig().getString("Skip-Message");
	    	Bukkit.getServer().broadcastMessage((ChatColor.translateAlternateColorCodes('&', translate(Message))));
	    }
    }
    public int PlayerCount() {
    	int Players = 0;
    	Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player != null)
			{
				assert ess != null;
				if(ess.getUser(player) != null && ess.getUser(player).isAfk()) continue;
				if(ess.getUser(player) != null && ess.getUser(player).isVanished()) continue;
				if(player.isSleeping()) continue;
				if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) continue;
				if(player.getWorld().getName().equals("world")) {
					++Players;
				}
			}
		}
		return Players;
    }
	public int SleepingPlayerCount() {
		int SleepingCount = 0;
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player != null)
			{
				if(player.isSleeping())
					++SleepingCount;
			}
		}
		return SleepingCount;
	}

    public boolean day() {
        Server server = Bukkit.getServer();
        long time = Objects.requireNonNull(server.getWorld("world")).getTime();

        return time < 12300 || time > 23850;
    }
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]){6}");

    /**
     * Translates Hex and ChatColor's within a given string
     *
     * @param input Given {@link String} to be colorized
     * @return Translated string
     */
    public static String translate(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);

        while (matcher.find()) {
            final String hexString = matcher.group();

            final ChatColor hex = ChatColor.of(hexString);
            final String before = input.substring(0, matcher.start());
            final String after = input.substring(matcher.end());

            input = before + hex + after;
            matcher = HEX_PATTERN.matcher(input);
        }

        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
