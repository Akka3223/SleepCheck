package me.Akka.SleepCheck;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin {
	FileConfiguration config = getConfig();
	File configFile = new File(this.getDataFolder(), "config.yml");
	public static Main plugin;
	@Override
	public void onEnable() {
		tellConsole("Plugin ON");
        if (!configFile.exists()) {
			config.addDefault("Percentage", "50");
			config.addDefault("Skip-Message", "#444444Succesfully skipped the night!");
			config.addDefault("SleepingLower", "#ffffffMore players needed to skip the night!");
			config.options().copyDefaults(true);
			saveConfig();
        }
    	
        Bukkit.getScheduler().runTaskTimer(this, () -> {
    		int PercentINT = Integer.parseInt(this.getConfig().getString("Percentage"));
    		int percentage = (PercentINT * PlayerCount()) / 100;
    		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
    			if(player.isSleeping())
    				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((int)Math.floor(percentage) + " " + ChatColor.translateAlternateColorCodes('&', translate(this.getConfig().getString("SleepingLower")))));    		}
    	}, 20L, 20L);
		plugin = this;
        
		Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
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
    public int PlayerCount() {
    	int Players = 0;
    	Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player instanceof Player)
			{
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
			if(player instanceof Player)
			{
				if(player.isSleeping())
					++SleepingCount;	
			}
		}
		return SleepingCount;
	}
	@Override
	public void onDisable() {
	}
	public void tellConsole(String message){
	    Bukkit.getConsoleSender().sendMessage(message);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("bed")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4This plugin was made with love by Akka <3 for SLIMI\n/bed reload - reload config."));
				return true;	
			}
			else if(args[0].equalsIgnoreCase("reload") && sender.isOp()) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Config was succesfully reloaded"));
					reloadConfig();
					return true;
			}
			else if(args[0].equalsIgnoreCase("debug") && sender.isOp()) {
		    	int SleepingCount = SleepingPlayerCount();
		    	int PlayerCount = PlayerCount();//Bukkit.getServer().getOnlinePlayers().size();
				int PercentINT = Integer.parseInt(Main.plugin.getConfig().getString("Percentage"));
				int percentage = (PercentINT * PlayerCount) / 100;
				sender.sendMessage(String.valueOf(PlayerCount));
				sender.sendMessage(String.valueOf(PercentINT));
				sender.sendMessage(String.valueOf(percentage));
				sender.sendMessage(String.valueOf(SleepingCount));
				return true;
			}
		}
		return false;
	}
}