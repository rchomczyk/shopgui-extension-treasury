package moe.rafal.shopgui.extension.treasury.economy;

import static java.util.concurrent.TimeUnit.SECONDS;
import static me.lokka30.treasury.api.common.Cause.plugin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import me.lokka30.treasury.api.common.Cause;
import me.lokka30.treasury.api.common.NamespacedKey;
import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

class TreasuryEconomyProvider extends EconomyProviderDelegate {

  private static final String TREASURY_PLUGIN_NAME = "Treasury";
  private static final String INTEGRATION_KEY_NAMESPACE = "shopgui";
  private static final String INTEGRATION_KEY_ID = "treasury_extension";
  private static final NamespacedKey IDENTIFYING_KEY = NamespacedKey.of(
      INTEGRATION_KEY_NAMESPACE,
      INTEGRATION_KEY_ID);
  private final EconomyProvider economyProvider;

  TreasuryEconomyProvider(EconomyProvider economyProvider) {
    this.economyProvider = economyProvider;
  }

  @Override
  public String getName() {
    return TREASURY_PLUGIN_NAME;
  }

  @Override
  public double getBalance(Player player) {
    try {
      return getPlayerAccountOf(player.getUniqueId())
          .thenCompose(account -> account.retrieveBalance(economyProvider.getPrimaryCurrency()))
          .thenApply(BigDecimal::doubleValue)
          .get(3, SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException exception) {
      throw new BalanceResolvingException(
          "Could not resolve balance for %s, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public void deposit(Player player, double value) {
    modifyPlayerBalance(
        player.getUniqueId(), BigDecimal.valueOf(value), PlayerAccount::depositBalance);
  }

  @Override
  public void withdraw(Player player, double value) {
    modifyPlayerBalance(
        player.getUniqueId(), BigDecimal.valueOf(value), PlayerAccount::withdrawBalance);
  }

  private void modifyPlayerBalance(UUID playerUniqueId, BigDecimal amount,
      TreasuryEconomyModifier<PlayerAccount, BigDecimal, Cause<?>, Currency> modifier) {
    getPlayerAccountOf(playerUniqueId)
        .thenAccept(account ->
            modifier.accept(account, amount, plugin(IDENTIFYING_KEY), economyProvider.getPrimaryCurrency()))
        .exceptionally(exception -> {
          throw new BalanceModifyingException(
              "Could not modify balance for %s, because of unexpected exception.",
              exception);
        });
  }

  private CompletableFuture<PlayerAccount> getPlayerAccountOf(UUID playerUniqueId) {
    return economyProvider.accountAccessor().player()
        .withUniqueId(playerUniqueId)
        .get();
  }
}
