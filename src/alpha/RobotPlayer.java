package alpha;
import battlecode.common.*;

import java.awt.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;

    // locations
    static MapLocation hq_loc;

    // robot counters
    static int num_miners = 0;
    static int num_landscapers = 0;
    static int num_design_schools = 0;

    // state trackers
    // for landscaper:
    static boolean prompt_landscaper_to_move = false;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        alpha.RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runHQ() throws GameActionException {
        // Only make certain amount of miners
        if (num_miners<1) {
            for (Direction dir : directions) {
                if (tryBuild(RobotType.MINER, dir)) {
                    num_miners++;
                }
            }
        }
    }

    static void runMiner() throws GameActionException {

        if (num_design_schools<1) {
            if (hq_loc==null) {
                MapLocation test_loc = find_hq();
                if (test_loc!=null) {
                    hq_loc = test_loc;
                    int dist_to_hq = calc_dist(rc.getLocation(), hq_loc);
                    if (2 < dist_to_hq && dist_to_hq <= 8) { // only build if within radius of hq
                        if (num_design_schools<1) { // only build if num design schools < 1
                            Direction ds_build_dir = rc.getLocation().directionTo(hq_loc).opposite();
                            if (tryBuild(RobotType.DESIGN_SCHOOL, ds_build_dir)) {
                                System.out.println("I made a design school!");
                                num_design_schools++;
                            }
                        }
                    }
                    else {
                        if (tryMove(randomDirection())) {
                            System.out.println("I moved away from HQ!");
                        }
                    }
                }
                else {
                    if (tryMove(randomDirection())) {
                        System.out.println("I know where hq is but I moved!");
                    }

                }

            }

            else {
                int dist_to_hq = calc_dist(rc.getLocation(), hq_loc);
                if (2 < dist_to_hq && dist_to_hq <= 8) { // only build if within radius of hq
                    if (num_design_schools<1) { // only build if num design schools < 1
                        Direction ds_build_dir = rc.getLocation().directionTo(hq_loc).opposite();
                        if (tryBuild(RobotType.DESIGN_SCHOOL, ds_build_dir)) {
                            System.out.println("I made a design school!");
                            num_design_schools++;
                        }
                    }
                }
                else {
                    if (tryMove(randomDirection())) {
                        System.out.println("I know where HQ is but I moved away!");
                    }
                }
            }
        }

        else {
            if (tryMove(randomDirection()))
                System.out.println("I moved!");

            if (num_landscapers<1) {
                for (Direction dir : directions)
                    if (tryRefine(dir))
                        System.out.println("I refined soup! " + rc.getTeamSoup());
            }

            for (Direction dir : directions)
                if (tryMine(dir))
                    System.out.println("I mined soup! " + rc.getSoupCarrying());
        }


        /* TURTLE PLAN
        1. get location of hq
        2.  if current msd from hq is 2 < msd <= 8 (else move randomly, mine soup)
        3.  if ds number < 1 (else move randomly, mine soup)
        4. build ds at at random direction nearby

        - if ds < 1 then go back to refine soup? need to make refinery after ds >=1

        - after turtling strategy is complete, add on to this.

         */
        /*
        tryBlockchain();
        tryMove(randomDirection());
        if (tryMove(randomDirection()))
            System.out.println("I moved!");
        // tryBuild(randomSpawnedByMiner(), randomDirection());
        for (Direction dir : directions)
            tryBuild(RobotType.FULFILLMENT_CENTER, dir);
        for (Direction dir : directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        for (Direction dir : directions)
            if (tryMine(dir))
                System.out.println("I mined soup! " + rc.getSoupCarrying());

    */
    }

    static void runDesignSchool() throws GameActionException {
        /* Only make intitial 8 for turtle bot*/
        if (num_landscapers<1) {
            if (tryBuild(RobotType.LANDSCAPER, randomDirection())) {
                System.out.println("I made a landscaper!");
                num_landscapers++;
            }
        }
    }

    static void runLandscaper() throws GameActionException {
        if (hq_loc==null) {
            MapLocation test_loc = find_hq();

            // calculate distance to HQ

            //int distance_to_hq = calc_dist()

            if (test_loc != null) {
                hq_loc = test_loc;
                // single bot rotate dig deposit code
                int distance_to_hq = calc_dist(rc.getLocation(), hq_loc);
                if (distance_to_hq<=2) {
                    if (solo_landscaper_loop(distance_to_hq)) {
                        System.out.println("I did a step in the single landscaper loop.");
                    }
                }

                else {
                    if (tryMove(rc.getLocation().directionTo(hq_loc))) {
                        System.out.println("I moved towards HQ!");
                    }
                    else {
                        if (tryMove(randomDirection())) {
                            System.out.println("I couldn't move towards HQ, so I moved randomly.");
                        }
                    }
                }
            }

            else {
                if (tryMove(randomDirection())) {
                    System.out.println(("I don't know where HQ is (and I couldn't find it) so I moved randomly."));
                }
            }
        }

        else {

            int distance_to_hq = calc_dist(rc.getLocation(), hq_loc);

            if (distance_to_hq<=2) {
                if(solo_landscaper_loop(distance_to_hq)) {
                    System.out.println("I did a step in the single landscaper loop.");
                }
            }

            else {
                if (tryMove(rc.getLocation().directionTo(hq_loc))) {
                    System.out.println("I moved towards HQ!");
                }
                else {
                    if (tryMove(randomDirection())) {
                        System.out.println("I couldn't move towards HQ, so I moved randomly.");
                    }
                }
            }
        }


        /*
        if (hq_loc==null) {
            MapLocation test_loc = find_hq();
            if (test_loc != null) {


            }
        }
        */

        /* PLAN TO EXECUTE
        1. locate HQ
        2. check if self is currently within msd of 1 <= msd <= 2 of HQ
        2.
            a. if within msd, repeat process:
                i. try to dig in opposite direction of hq if holding < 1 dirt
                ii. if holding > 1 dirt, deposit dirt on current block

        2.
            b. else, repeat process:
                i. try to move in direction of an empty block within radius of hq
                ii. else, move in random direction

         - To implement after that ^^ : dig in the block one over or rotated from the earlier block and deposit

         */
    }

    /* Robots I have not modified yet */

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }

    static void runFulfillmentCenter() throws GameActionException {
        /*
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
        */
    }

    static void runDeliveryDrone() throws GameActionException {
        /*
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }

         */
    }

    static void runNetGun() throws GameActionException {

    }

    /* MY METHODS */

    static MapLocation find_hq() {
        MapLocation hq_location = null;
        RobotInfo [] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.type==RobotType.HQ && robot.team==rc.getTeam()) {
                hq_location = robot.location;

            }
        }
        return hq_location;
    }

    /* Return manhattan distance between two points */
    static int calc_dist(MapLocation point_1, MapLocation point_2) {
        return Math.abs(point_1.x-point_2.x)+Math.abs(point_1.y-point_2.y);
    }

    /* run single landscaper loop around hq given loc is known */
    static boolean solo_landscaper_loop(int distance_to_hq) throws GameActionException {
         // need to return true if loop step is run
         // use this?
         boolean task_accomplished = false;

         if (distance_to_hq==1) {
             Direction dir_to_hq = rc.getLocation().directionTo(hq_loc);
             if (dir_to_hq == Direction.EAST) {
                 // move bot north
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.NORTH)) {
                         System.out.println("I moved North!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
             else if (dir_to_hq == Direction.NORTH) {
                 // move bot west
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.WEST)) {
                         System.out.println("I moved West!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
             else if (dir_to_hq == Direction.WEST) {
                 // move bot south
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.SOUTH)) {
                         System.out.println("I moved south!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
             else {
                 // move bot east
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.EAST)) {
                         System.out.println("I moved East!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
         }

         else {
             Direction dir_to_hq = rc.getLocation().directionTo(hq_loc);
             if (dir_to_hq == Direction.SOUTHWEST) {
                 // move south
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.SOUTH)) {
                         System.out.println("I moved South!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
             else if (dir_to_hq == Direction.NORTHEAST) {
                 // move west
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.NORTH)) {
                         System.out.println("I moved north!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
             else if (dir_to_hq == Direction.NORTHWEST) {
                 // move north
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.WEST)) {
                         System.out.println("I moved West!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
             else {
                 // move east
                 if (rc.getDirtCarrying()<1 && prompt_landscaper_to_move==false) {
                     if (rc.canDigDirt(rc.getLocation().directionTo(hq_loc).opposite())) {
                         rc.digDirt(rc.getLocation().directionTo(hq_loc).opposite());
                         System.out.println("I dug some dirt opposite of HQ!");
                         task_accomplished = true;
                     }
                 }
                 else if (prompt_landscaper_to_move==false) {
                     if (rc.canDepositDirt(Direction.CENTER)) {
                         rc.depositDirt(Direction.CENTER);
                         System.out.println("I deposited some dirt in my spot!");
                         prompt_landscaper_to_move = true;
                         task_accomplished = true;
                     }
                 }

                 else {
                     if (tryMove(Direction.EAST)) {
                         System.out.println("I moved east!");
                         prompt_landscaper_to_move = false;
                         task_accomplished = true;
                     }
                 }
             }
         }
         return task_accomplished;
    }

    /* MISC. DEFAULT METHODS */

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }

    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }
}
