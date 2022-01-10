package lubao_1;

import battlecode.common.*;


strictfp class WatchtowerStrategy {
    static final RobotType myType = RobotType.WATCHTOWER;
    final static int ATTACK_RADIUS_SQUARED = myType.actionRadiusSquared;

    static void run(RobotController rc) throws GameActionException {
        MapLocation me = rc.getLocation();
        
        SharedArrayTargetAndIndex indexAndTarget = RobotPlayer.locateCombatTarget(rc, me);
        MapLocation target = indexAndTarget.location;
        int sharedArrayIndex = indexAndTarget.idx;
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, RobotPlayer.opponent);


        RobotPlayer.attackGlobalTargetIfAble(rc, target, me);

        TripleTarget localTargets = RobotPlayer.acquireLocalTargets(rc, target, enemies, me);

        MapLocation primaryTarget = localTargets.primary;
        MapLocation secondaryTarget = localTargets.secondary;
        MapLocation tertiaryTarget = localTargets.tertiary;
    
        int d1 = me.distanceSquaredTo(primaryTarget);
        int d2 = me.distanceSquaredTo(secondaryTarget);
        int d3 = me.distanceSquaredTo(tertiaryTarget);
        int shortestDistance = Math.min(Math.min(d1, d2), d3);

        rc.setIndicatorLine(me, primaryTarget, 100, 100, 100);

        RobotMode currentMode = rc.getMode();
        if (currentMode.equals(RobotMode.TURRET)) {
            if (rc.canAttack(primaryTarget)) {
                rc.attack(primaryTarget);
                if (sharedArrayIndex != -1) {
                    RobotPlayer.addLocationToSharedArray(rc, primaryTarget, 0, sharedArrayIndex);
                }
            }

            if (rc.canAttack(secondaryTarget)) {
                rc.attack(secondaryTarget);
            }
            if (rc.canAttack(tertiaryTarget)) {
                rc.attack(tertiaryTarget);
            }

            if (shortestDistance >= rc.getType().visionRadiusSquared && rc.getRoundNum() > 150) {
                if (rc.canTransform()) {
                    rc.transform();
                }
            }
        } else if (currentMode.equals(RobotMode.PORTABLE)) {
            if (shortestDistance <= rc.getType().actionRadiusSquared /* || enemies.length > 0 */) {
                if (rc.canTransform() && rc.isTransformReady()) {
                    RobotPlayer.stepOffRubble(rc, me);
                    if (rc.isTransformReady()) {
                        rc.transform();
                    }
                    // if (RobotPlayer.isLandSuitableForBuilding(rc, me)) {
                    //     rc.transform();
                    // } else {
                    //     for (Direction dir : RobotPlayer.directions) {
                    //         MapLocation potentialLoc = rc.adjacentLocation(dir);
                    //         if (rc.canMove(dir) && RobotPlayer.isLandSuitableForBuilding(rc, potentialLoc)) {
                    //             rc.move(dir);
                    //             if (rc.canTransform()) {
                    //                 rc.transform();
                    //             }
                    //         } 
                    //     }
                    // }
                }
            }
            // if (rc.canMove(me.directionTo(target))) {
                RobotPlayer.move2(rc, target, 3);;
            // }
        }
    }
}
