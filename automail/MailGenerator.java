package automail;

import java.util.*;


/*******************************************************************
 * This class generates the mail
 ******************************************************************/
public class MailGenerator {

    public final int MAIL_TO_CREATE;
    public final int MAIL_MAX_WEIGHT;
    private final Random random;
    /** This seed is used to make the behaviour deterministic */
    private int mailCreated;
    private boolean complete;
    private Map<Integer,ArrayList<MailItem>> allMail;

    /***************************************************************************************
     * Constructor for mail generation
     * @param mailToCreate roughly how many mail items to create
     * @param seed random seed for generating mail
     ***************************************************************************************/
    public MailGenerator(int mailToCreate, int mailMaxWeight, HashMap<Boolean,Integer> seed){

        if(seed.containsKey(true)){
        	this.random = new Random((long) seed.get(true));
        }
        else{
        	this.random = new Random();	
        }

        // Vary arriving mail by +/-20%
        MAIL_TO_CREATE = mailToCreate*4/5 + random.nextInt(mailToCreate*2/5);
        MAIL_MAX_WEIGHT = mailMaxWeight;
        mailCreated = 0;
        complete = false;
        allMail = new HashMap<Integer, ArrayList<MailItem>>();
    }

    /*************************************************************************
     * This method gets all the mails generated in the intiali process.
     * @return
     **************************************************************************/
    public Map<Integer,ArrayList<MailItem>> getAllMails(){
        return allMail;
    }

    /****************************************************************************************
     * This method generates all the mails.
     * @return a new mail item that needs to be delivered
     ****************************************************************************************/
    private MailItem generateMail(){
    	MailItem newMailItem;
        int dest_floor = generateDestinationFloor();
        int priority_level = generatePriorityLevel();
        int arrival_time = generateArrivalTime();
        int weight = generateWeight();
        // Check if arrival time has a priority mail
        if(	(random.nextInt(6) > 0) ||  // Skew towards non priority mail
        	(allMail.containsKey(arrival_time) &&
        	allMail.get(arrival_time).stream().anyMatch(e -> PriorityMailItem.class.isInstance(e))))
        {
        	newMailItem = new MailItem(dest_floor,arrival_time,weight);      	
        } else {
        	newMailItem = new PriorityMailItem(dest_floor,arrival_time,weight,priority_level);
        }
        return newMailItem;
    }

    /*****************************************************************************************
     * This method generate the destination of the mailItems generated.
     * @return a destination floor between the ranges of GROUND_FLOOR to FLOOR
     ****************************************************************************************/
    private int generateDestinationFloor(){
        return Building.LOWEST_FLOOR + random.nextInt(Building.FLOORS);
    }

    /*****************************************************************************************
     * This method generates the priority of the mails generated.
     * @return a random priority level selected from 1 - 100
     ****************************************************************************************/
    private int generatePriorityLevel(){
        return 10*(1 + random.nextInt(10));
    }

    /*****************************************************************************************
     * This method generated the weight of the main items.
     * @return a random weight
     ****************************************************************************************/
    private int generateWeight(){
    	final double mean = 200.0; // grams for normal item
    	final double stddev = 1000.0; // grams
    	double base = random.nextGaussian();
    	if (base < 0) base = -base;
    	int weight = (int) (mean + base * stddev);
        return weight > MAIL_MAX_WEIGHT ? MAIL_MAX_WEIGHT : weight;
    }
    
    /*****************************************************************************************
     * This method generates the random arrival time of the mails.
     * @return a random arrival time before the last delivery time
     ****************************************************************************************/
    private int generateArrivalTime(){
        return 1 + random.nextInt(Clock.LAST_DELIVERY_TIME);
    }

    /*****************************************************************************************
     * This method initializes all mail and sets their corresponding values.
     ****************************************************************************************/
    public void generateAllMail(){
        while(!complete){
            MailItem newMail =  generateMail();
            int timeToDeliver = newMail.getArrivalTime();
            /** Check if key exists for this time **/
            if(allMail.containsKey(timeToDeliver)){
                /** Add to existing array */
                allMail.get(timeToDeliver).add(newMail);
            }
            else{
                /** If the key doesn't exist then set a new key along with the array of MailItems to add during
                 * that time step.
                 */
                ArrayList<MailItem> newMailList = new ArrayList<MailItem>();
                newMailList.add(newMail);
                allMail.put(timeToDeliver,newMailList);
            }
            /** Mark the mail as created */
            mailCreated++;

            /** Once we have satisfied the amount of mail to create, we're done!*/
            if(mailCreated == MAIL_TO_CREATE){
                complete = true;
            }
        }

    }


}
