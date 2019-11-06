package strategies;

import java.util.*;
import automail.Clock;
import automail.MailItem;
import automail.PriorityMailItem;

/*************************************************************************
 * This class collects and maintains the pool of MainItems and sort them 
 * based on the priority and destination.
 ************************************************************************/
public class MailPool implements IMailPool {

	private LinkedList<Item> pool;
	private Map<Integer,ArrayList<MailItem>> allMail;

	public MailPool(Map<Integer, ArrayList<MailItem>> allMail){
		pool = new LinkedList<Item>();
		this.allMail = allMail;
	}

    /*************************************************************************
     * This method returns the liked list of pool.
     * @return Linked list of pool items
     ************************************************************************/
	public LinkedList<Item> getPool(){
        return this.pool;
    }

    /*************************************************************************
     * This method is the main step method that execute the loadNew main method
     * everytime the clock ticks.
     ************************************************************************/
    @Override
    public void step() {
        loadNewMail();
    }

    /*************************************************************************
     * While there are steps left, create a new mail item to deliver
     * @return Priority
     ************************************************************************/
    private PriorityMailItem loadNewMail(){

        PriorityMailItem priority = null;

        // Check if there are any mail to create
        if(this.allMail.containsKey(Clock.Time())){
            for(MailItem mailItem : allMail.get(Clock.Time())){
                if (mailItem instanceof PriorityMailItem) priority = ((PriorityMailItem) mailItem);
                System.out.printf("T: %3d > new addToPool [%s]%n", Clock.Time(), mailItem.toString());
                addToPool(mailItem);
            }
        }
        return priority;
    }

    /*************************************************************************
     * This class is used to compare the pool of Items with the destination and
     * priority.
     ************************************************************************/
    public class ItemComparator implements Comparator<Item> {
        @Override
        public int compare(Item i1, Item i2) {
            int order = 0;
            if (i1.getPriority() < i2.getPriority()) {
                order = 1;
            } else if (i1.getPriority() > i2.getPriority()) {
                order = -1;
            } else if (i1.getDestination() < i2.getDestination()) {
                order = 1;
            } else if (i1.getDestination()> i2.getDestination()) {
                order = -1;
            }
            return order;
        }
    }

    /*************************************************************************
     * This method is used to add the mainItem into the mailPool.
     * @param mailItem
     ************************************************************************/
    public void addToPool(MailItem mailItem) {
        Item item = new Item(mailItem);
        pool.add(item);
        pool.sort(new ItemComparator());
    }
}
