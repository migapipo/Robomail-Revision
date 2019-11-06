package strategies;

import automail.MailItem;
import automail.PriorityMailItem;

/*************************************************************************
 * This class is the wrapper class of the mailItem.
 ************************************************************************/
public class Item {
    private int priority;
    private int destination;
    private MailItem mailItem;
    // Use stable sort to keep arrival time relative positions

    public Item(MailItem mailItem) {
        priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
        destination = mailItem.getDestFloor();
        this.mailItem = mailItem;
    }

    /*************************************************************************
     * This method get the priority of the Item.
     * @return the priority
     **************************************************************************/
    public int getPriority(){
        return priority;
    }

    /*************************************************************************
     * This method gets the destination of the Item.
     * @return the destination
     **************************************************************************/
    public int getDestination(){
        return destination;
    }

    /*************************************************************************
     * This method gets the MailItem present in the Item wrapper class.
     * @return the mailItem
     **************************************************************************/
    public MailItem getMailItem(){
        return mailItem;
    }
}
