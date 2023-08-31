package moe.rafal.shopgui.extension.treasury;

import org.bukkit.plugin.java.JavaPlugin;

public class ExtensionBukkitPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new ExtensionInjector(), this);
  }
}
