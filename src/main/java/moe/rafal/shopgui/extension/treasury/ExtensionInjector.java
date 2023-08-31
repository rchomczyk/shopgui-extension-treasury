package moe.rafal.shopgui.extension.treasury;

import me.lokka30.treasury.api.common.service.Service;
import me.lokka30.treasury.api.common.service.ServiceRegistry;
import me.lokka30.treasury.api.economy.EconomyProvider;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static moe.rafal.shopgui.extension.treasury.economy.TreasuryEconomyProviderFactory.createTreasuryEconomyProvider;
import static net.brcdev.shopgui.ShopGuiPlusApi.registerEconomyProvider;

class ExtensionInjector implements Listener {

  @EventHandler
  public void onExtensionInject(ShopGUIPlusPostEnableEvent event) {
    registerEconomyProvider(createTreasuryEconomyProvider(resolveEconomyProvider()));
  }

  private EconomyProvider resolveEconomyProvider() {
    return ServiceRegistry.INSTANCE.serviceFor(EconomyProvider.class)
        .map(Service::get)
        .orElseThrow(() -> new ExtensionInjectionException(
            "Could not resolve any implementation for Treasury's economy."));
  }
}
