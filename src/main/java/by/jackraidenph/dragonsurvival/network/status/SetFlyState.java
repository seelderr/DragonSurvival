package by.jackraidenph.dragonsurvival.network.status;

public class SetFlyState {
    public int playerid;
    public boolean flying;

    public SetFlyState(int playerid, boolean flying) {
        this.playerid = playerid;
        this.flying = flying;
    }

    public SetFlyState() {
    }
}
