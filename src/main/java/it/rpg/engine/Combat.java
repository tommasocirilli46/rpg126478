package it.rpg.engine;

import it.rpg.model.Character;
import it.rpg.model.Npc;

import java.util.Objects;

/**
 * A single, self-contained battle between the player's {@link Character} and an
 * {@link Npc}. It owns the enemy's volatile health (the {@code Npc} definition
 * stays immutable) and resolves the fight one round at a time.
 *
 * <p>Resolution is fully <strong>deterministic</strong> — no randomness — so it
 * mirrors the rest of the game rules and can be unit-tested directly. Each round
 * the player strikes first for {@code max(1, attribute)} damage; if the enemy
 * survives it strikes back for {@code max(1, attack)} damage.</p>
 */
public final class Combat {

    private final Character player;
    private final Npc enemy;
    private final String playerAttackAttribute;
    private int enemyHealth;

    public Combat(Character player, Npc enemy, String playerAttackAttribute) {
        this.player = Objects.requireNonNull(player, "player");
        this.enemy = Objects.requireNonNull(enemy, "enemy");
        this.playerAttackAttribute = Objects.requireNonNull(playerAttackAttribute, "playerAttackAttribute");
        this.enemyHealth = enemy.maxHealth();
    }

    /** Resolves one exchange of blows and returns what happened. */
    public CombatRound playerAttack() {
        if (isOver()) {
            throw new IllegalStateException("Il combattimento è già terminato.");
        }
        int damageDealt = Math.max(1, player.attribute(playerAttackAttribute));
        enemyHealth = Math.max(0, enemyHealth - damageDealt);

        int damageTaken = 0;
        if (enemyHealth > 0) {
            damageTaken = Math.max(1, enemy.attack());
            player.modifyHealth(-damageTaken);
        }
        return new CombatRound(damageDealt, damageTaken, enemyHealth, player.health());
    }

    public boolean isOver() {
        return enemyHealth <= 0 || !player.isAlive();
    }

    public boolean playerWon() {
        return enemyHealth <= 0 && player.isAlive();
    }

    public Npc enemy() {
        return enemy;
    }

    public String enemyName() {
        return enemy.name();
    }

    public int enemyHealth() {
        return enemyHealth;
    }

    public int enemyMaxHealth() {
        return enemy.maxHealth();
    }
}
