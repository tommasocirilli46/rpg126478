package it.rpg.model;

import java.util.Objects;

/**
 * Turns a {@link Scene} into a battle. It names the {@link Npc} to fight (by id,
 * resolved against the {@link StoryGraph} registry), where the story continues on
 * victory, the optional scene reached by fleeing, the experience awarded for
 * winning and which player attribute determines the damage dealt each round.
 */
public final class Encounter {

    private String npcId;
    private String victorySceneId;
    private String fleeSceneId;
    private int xpReward;
    private String playerAttackAttribute;

    private Encounter() {
    }

    public Encounter(String npcId, String victorySceneId, String fleeSceneId,
                     int xpReward, String playerAttackAttribute) {
        this.npcId = Objects.requireNonNull(npcId, "npcId");
        this.victorySceneId = Objects.requireNonNull(victorySceneId, "victorySceneId");
        this.fleeSceneId = fleeSceneId;
        this.xpReward = Math.max(0, xpReward);
        this.playerAttackAttribute = Objects.requireNonNull(playerAttackAttribute, "playerAttackAttribute");
    }

    public String npcId() {
        return npcId;
    }

    public String victorySceneId() {
        return victorySceneId;
    }

    /** @return the scene reached by fleeing, or {@code null} if fleeing is not allowed. */
    public String fleeSceneId() {
        return fleeSceneId;
    }

    public boolean canFlee() {
        return fleeSceneId != null && !fleeSceneId.isBlank();
    }

    public int xpReward() {
        return Math.max(0, xpReward);
    }

    public String playerAttackAttribute() {
        return playerAttackAttribute;
    }
}
