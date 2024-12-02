//package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;
//
//import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
//import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
//
//public abstract class PassiveDragonAbility extends DragonAbility {
//    public int getLevelCost() {
//        return ServerConfig.initialPassiveCost + (int) (ServerConfig.passiveScalingCost * getLevel());
//    }
//
//    public int getLevelCost(int change) {
//        return ServerConfig.initialPassiveCost + (int) (ServerConfig.passiveScalingCost * (getLevel() + change));
//    }
//}