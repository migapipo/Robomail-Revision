package automail;

import java.util.Map;
import java.util.TreeMap;

/*******************************************************************
 * This class represents a mail item.
 ******************************************************************/
public class MailItem {
	
    /** Represents the destination floor to which the mail is intended to go */
    private final int destination_floor;
    /** The mail identifier */
    private final String id;
    /** The time the mail item arrived */
    private final int arrival_time;
    /** The weight in grams of the mail item */
    private final int weight;

    static private int count = 0;
    static private Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();


    /**************************************************************************
     * Constructor for a MailItem
     * @param dest_floor the destination floor intended for this mail item
     * @param arrival_time the time that the mail arrived
     * @param weight the weight of this mail item
     **************************************************************************/
    public MailItem(int dest_floor, int arrival_time, int weight){
        this.destination_floor = dest_floor;
        this.id = String.valueOf(hashCode());
        this.arrival_time = arrival_time;
        this.weight = weight;
    }

    @Override
    public String toString(){
        return String.format("Mail Item:: ID: %6s | Arrival: %4d | Destination: %2d | Weight: %4d", id, arrival_time, destination_floor, weight);
    }

    /**************************************************************************
     * This method gets the destination of the mailItem
     * @return the destination floor of the mail item
     **************************************************************************/
    public int getDestFloor() {
        return destination_floor;
    }
    
    /**************************************************************************
     * This method gets the id of the mailItem.
     * @return the ID of the mail item
     *************************************************************************/
    public String getId() {
        return id;
    }

    /**************************************************************************
     * This method gets the arrival time.
     * @return the arrival time of the mail item
     *************************************************************************/
    public int getArrivalTime(){
        return arrival_time;
    }

    /**************************************************************************
    * This method gets the weight of the item.
    * @return the weight of the mail item
    *************************************************************************/
   public int getWeight(){
       return weight;
   }

    /**************************************************************************
     * This method gets the hashCode of the item.
     * @return
     *************************************************************************/
	@Override
	public int hashCode() {
		Integer hash0 = super.hashCode();
		Integer hash = hashMap.get(hash0);
		if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
		return hash;
	}
}
