package it.rpg.engine;

/**
 * The immutable result of a single exchange of blows, handed to listeners so the
 * UI can show what just happened without reaching into the combat internals.
 *
 * @param damageDealt  damage the player inflicted on the enemy this round
 * @param damageTaken  damage the player suffered this round (0 if the enemy fell)
 * @param enemyHealth  the enemy's remaining health after the round
 * @param playerHealth the player's remaining health after the round
 */
public record CombatRound(int damageDealt, int damageTaken, int enemyHealth, int playerHealth) {
}
