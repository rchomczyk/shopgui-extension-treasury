package moe.rafal.shopgui.extension.treasury.economy;

import me.lokka30.treasury.api.economy.EconomyProvider;

public final class TreasuryEconomyProviderFactory {

  private TreasuryEconomyProviderFactory() {

  }

  public static EconomyProviderDelegate createTreasuryEconomyProvider(
      EconomyProvider economyProvider) {
    return new TreasuryEconomyProvider(economyProvider);
  }
}
