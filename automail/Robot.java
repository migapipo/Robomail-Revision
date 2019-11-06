package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import java.util.Map;
import java.util.TreeMap;

/*************************************************************************
 * This class robot class that delivers the mail.
 ************************************************************************/
public class Robot implements Comparable<Robot> {
    static public final int INDIVIDUAL_MAX_WEIGHT   = 2000;
    static public final int PAIR_MAX_WEIGHT         = 2600;
    static public final int TRIPLE_MAX_WEIGHT       = 3000;
    static public final int SLOW_STEP_SPEED         = 2;
    protected final String id;

    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private boolean receivedDispatch;

    private int deliveryCounter;
    private boolean heavyMode = false;
    private int slowStep = 0;
    private boolean deliveryResp = false;

    private MailItem deliveryItem = null;
    private MailItem tube = null;
    IMailDelivery delivery;
    static private int count = 0;
    static private Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();

    public Robot(IMailDelivery delivery){
    	id = "R" + hashCode();
    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        this.delivery = delivery;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }

    /*************************************************************************
     * The method gets the id of the robot.
     * @return
    **************************************************************************/
    public String getID(){
        return this.id;
    }

    /*************************************************************************
     * The method get the MailItem from the tube.
     * @return
    *************************************************************************/
    public MailItem getTube() {
        return tube;
    }

    /*************************************************************************
     * The method gets the id of the tube.
     * @return
    ************************************************************************/
    private String getIdTube() {
        return String.format("%s(%1d)", id, (tube == null ? 0 : 1));
    }

    /*************************************************************************
     * The method assigns the responsibility to the robot to report the delivery,
     * So that in the team effort no 2 robot reports the same delivery.
     * @param response
    ************************************************************************/
    public void setDeliveryResp(boolean response){
        deliveryResp = response;
    }

    /*************************************************************************
     * The method set the robot into a heavy mode.
     * @param heavy
    ************************************************************************/
    public void setHeavyMode(boolean heavy){
        this.heavyMode = heavy;
    }

    /*************************************************************************
     * The method compares the robot based on their ID.
     * @return
    ************************************************************************/
    @Override
    public int compareTo(Robot other) {
        return id.compareTo(other.getID());
    }

    /*************************************************************************
     * The method triggers the robot to start delivering
    ************************************************************************/
    public void dispatch() {
    	receivedDispatch = true;
    }

    /*************************************************************************
     * This method is called on every time step
     *
     * @throws ExcessiveDeliveryException if robot delivers more than the
     * capacity of the tube without refilling.
    ************************************************************************/
    public void step(DeliveryScheme deliverySchemes) throws ExcessiveDeliveryException {
    	switch(current_state) {
    		/** This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
    			/** If its current position is at the mailroom, then the robot should change state */
                if(current_floor == Building.MAILROOM_LOCATION){
                	if (tube != null) {
                        deliverySchemes.getMailPool().addToPool(tube);
                        System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), tube.toString());
                        tube = null;
                	}
        			/** Tell the sorter the robot is ready */
                    deliverySchemes.registerWaiting(this);
                	changeState(RobotState.WAITING);
                } else {
                	/** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.MAILROOM_LOCATION);
                	break;
                }
    		case WAITING:
                /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if(!isEmpty() && receivedDispatch){
                	receivedDispatch = false;
                	deliveryCounter = 0; // reset delivery counter
        			setRoute();
                    changeState(RobotState.DELIVERING);
                    slowStep = SLOW_STEP_SPEED;
                }
                break;
    		case DELIVERING:

                // Changes the speed of the robot when the heavy mode is activated.
    		    if(!heavyMode || (slowStep == SLOW_STEP_SPEED)) {
    		        slowStep = 0;
                    if (current_floor == destination_floor) { // If already here drop off either way

                        /** Delivery complete, report this to the simulator! */
                        if (deliveryResp)
                            delivery.deliver(deliveryItem);

                        deliveryItem = null;
                        heavyMode = false;
                        slowStep = 0;
                        deliveryCounter++;
                        if (deliveryCounter > 2) {  // Implies a simulation bug
                            throw new ExcessiveDeliveryException();
                        }

                        /** Check if want to return, i.e. if there is no item in the tube*/
                        if (tube == null) {
                            changeState(RobotState.RETURNING);
                        } else {
                            /** If there is another item, set the robot's route to the location to deliver the item */
                            deliveryItem = tube;
                            tube = null;
                            setRoute();
                            changeState(RobotState.DELIVERING);
                        }

                    } else {
                        /** The robot is not at the destination yet, move towards it! */
                        moveTowards(destination_floor);
                    }
                    if (isEmpty())
                        deliveryResp = false;
                }else
                    slowStep++;

                break;
    	}
    }


    /*************************************************************************
     * This method sets the route for the robot
    ************************************************************************/
    private void setRoute() {
        /** Set the destination floor */
        destination_floor = deliveryItem.getDestFloor();
    }

    /*************************************************************************
     * This method is Generic function that moves the robot towards the destination.
     *
     * @param destination the floor towards which the robot is moving
    ************************************************************************/
    private void moveTowards(int destination) {
        if(current_floor < destination){
            current_floor++;
        } else {
            current_floor--;
        }
    }

    /*************************************************************************
     * This method Prints out the change in state
     *
     * @param nextState the state to which the robot is transitioning
    **************************************************************************/
    private void changeState(RobotState nextState){
    	assert(!(deliveryItem == null && tube != null));
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    	}
    }

    /*************************************************************************
     * This method
     *
     * @return
    **************************************************************************/
	@Override
	public int hashCode() {
		Integer hash0 = super.hashCode();
		Integer hash = hashMap.get(hash0);
		if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
		return hash;
	}

    /*************************************************************************
     * This method
     *
     * @return
    **************************************************************************/
	public boolean isEmpty() {
		return (deliveryItem == null && tube == null);
	}

    /*************************************************************************
     * This method
     *
     * @param mailItem
     **************************************************************************/
	public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		assert(deliveryItem == null);
		deliveryItem = mailItem;

		if (deliveryItem.getWeight() > TRIPLE_MAX_WEIGHT) throw new ItemTooHeavyException();
	}

    /*************************************************************************
     * This method
     *
     * @param mailItem
    **************************************************************************/
	public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		assert(tube == null);
		tube = mailItem;

		if (tube.getWeight() > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}

}
