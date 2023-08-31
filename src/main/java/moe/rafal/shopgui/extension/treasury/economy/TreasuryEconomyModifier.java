package moe.rafal.shopgui.extension.treasury.economy;

@FunctionalInterface
interface TreasuryEconomyModifier<K, V, S, T> {

  void accept(K k, V v, S s, T t);
}
