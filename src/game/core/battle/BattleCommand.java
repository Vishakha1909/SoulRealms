package game.core.battle;

/**
 * Hook for Command pattern – you can later create concrete commands for
 * Attack, CastSpell, Guard, etc. For now it's just a marker.
 */
public interface BattleCommand {
    void execute();
}
