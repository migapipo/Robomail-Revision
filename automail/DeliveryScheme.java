package automail;

import exceptions.ItemTooHeavyException;
import strategies.IMailPool;
import strategies.Item;
import java.util.*;

/*************************************************************************
 * This class is the pure fabrication class that used to assign delivery
 * schedule to the robot when they are available.
 ************************************************************************/
public class DeliveryScheme {

    static private final int TEAM_OF_THREE   = 3;
    static private final int TEAM_OF_TWO     = 2;

    private IMailPool mailPool;
    private MailItem deliveryItem = null;
    private LinkedList<Robot> robots;
    private ArrayList<Robot> teamRobot;

    public DeliveryScheme(IMailPool mailPool){

        this.mailPool = mailPool;
        teamRobot = new ArrayList<Robot>();
        robots = new LinkedList<Robot>();

    }

    /*************************************************************************
     * The method gets the mailPool object.
     ************************************************************************/
    public IMailPool getMailPool(){
        return mailPool;
    }

    /*************************************************************************
     * The method steps though the robot and assigns the delivery item to the
     * available robot.
     ************************************************************************/
    public void step() throws ItemTooHeavyException{

        LinkedList<Item> pool = mailPool.getPool();

        try{
            ListIterator<Robot> robot = robots.listIterator();
            while (robot.hasNext()) loadRobot(robot, pool);
        } catch (Exception e) {
            throw e;
        }
    }

    /*************************************************************************
     * The method registers the robot into the waiting mode to let the Automail
     * System know it is ready to deliver another item.
     *
     * @param robot
     ************************************************************************/
    public void registerWaiting(Robot robot) {
        robots.add(robot);
    }

    /*************************************************************************
     * The method is used to load robot with relevant delivery item and form
     * team of robot in case the item is heavy.
     *
     * @param i Aval
     * @param pool
     ************************************************************************/
    private void loadRobot(ListIterator<Robot> i, LinkedList<Item> pool) throws ItemTooHeavyException {

        // Check if the next robot is empty handed
        Robot robot = i.next();
        assert(robot.isEmpty());

        ListIterator<Item> poolItem = pool.listIterator();

        // Check if the pool has some sorted mails to deliver
        if (pool.size() > 0 || (deliveryItem != null)) {
            try {

                // Check if the system needs to load a new item to deliver.
                if(deliveryItem == null) {
                    deliveryItem = poolItem.next().getMailItem();
                    poolItem.remove();
                }

                robot.addToHand(deliveryItem);
                addRobotToTeam(robot);
                i.remove();

                // Check, schedule and dispatch an individual and a team of robots to deliver the mail.
                if(scheduleDelivery(robot, poolItem, pool.size()))
                    dispatchTeam();

            } catch (Exception e) {
                throw e;
            }
        }
    }
    private void addRobotToTeam(Robot robot){
        teamRobot.add(robot);
    }
    /*************************************************************************
     * This method determines the team requirement and decides when to dispatch
     * the robots.
     *
     * @param robot
     * @param poolItem
     * @param poolSize
     ************************************************************************/
    private boolean scheduleDelivery(Robot robot, ListIterator<Item> poolItem, int poolSize)throws ItemTooHeavyException{

        boolean isReadToGo = false;

        // Check if the weight is less than the limit of the overall system
        if (deliveryItem.getWeight() > Robot.TRIPLE_MAX_WEIGHT) {

            throw new ItemTooHeavyException();

          // Check if the team of 3 robots is need to lift the heavy mail.
        } else if (deliveryItem.getWeight() > Robot.PAIR_MAX_WEIGHT) {

            robot.setHeavyMode(true);
            if(teamSize() == TEAM_OF_THREE)
                isReadToGo = true;

            // Check if the team of 2 robots is need to lift the heavy mail.
        } else if(deliveryItem.getWeight() > Robot.INDIVIDUAL_MAX_WEIGHT) {

            robot.setHeavyMode(true);
            if(teamSize() == TEAM_OF_TWO)
                isReadToGo = true;

        } else {
            // Execute the individual robot with tube functionality.
            isReadToGo = true;

            if (poolSize > 0) {
                MailItem tubeItem = poolItem.next().getMailItem();

                if(tubeItem.getWeight() <= Robot.INDIVIDUAL_MAX_WEIGHT) {
                    robot.addToTube(tubeItem);
                    poolItem.remove();
                }
            }
        }
        return isReadToGo;
    }

    private int teamSize(){
        return teamRobot.size();
    }

    /*************************************************************************
     * This method dispatches all the robots present in the team robot list.
     ************************************************************************/
    private void dispatchTeam(){

        for(Robot r: teamRobot)
            r.dispatch();

        setDeliveryIncharge();

        deliveryItem = null;
        teamRobot.clear();
    }

    /*************************************************************************
     * This method set the delivery inchange robot in the case of the team 
     * robots.
     ************************************************************************/
    private void setDeliveryIncharge(){

        if (teamSize() > 1)
            Collections.sort(teamRobot);

        teamRobot.get(0).setDeliveryResp(true);
    }
}