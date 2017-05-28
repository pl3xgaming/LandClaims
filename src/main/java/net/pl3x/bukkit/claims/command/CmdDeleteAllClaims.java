package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdDeleteAllClaims implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdDeleteAllClaims(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(target -> target.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.deleteallclaims")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            Lang.send(sender, Lang.COMMAND_MISSING_PLAYER);
            return true;
        }

        //noinspection deprecation
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            Lang.send(sender, Lang.COMMAND_PLAYER_NOT_FOUND);
            return true;
        }

        plugin.getPlayerManager().getPlayer(target.getUniqueId())
                .getClaims().forEach(plugin.getClaimManager()::deleteClaim);

        if (!target.isOnline()) {
            plugin.getPlayerManager().unload(target.getUniqueId());
        }

        Lang.send(sender, Lang.DELETE_ALL_CLAIMS_SUCCESS
                .replace("{target}", target.getName()));

        if (sender instanceof Player) {
            plugin.getPlayerManager().getPlayer((Player) sender).revertVisualization();
        }
        return true;
    }
}