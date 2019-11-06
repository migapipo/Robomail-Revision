package automail;

import exceptions.MailAlreadyDeliveredException;
import java.util.ArrayList;

/****************************************************************
 * This class is used to measures the performance of the automail
 * system.
 ***************************************************************/
public class ReportDelivery implements IMailDelivery {
    private static double total_score;
    private static ArrayList<MailItem> MAIL_DELIVERED;

    public ReportDelivery(){
        MAIL_DELIVERED = new ArrayList<MailItem>();
        total_score = 0;
    }

    /****************************************************************
     * This method gets the total_score of the Automail system.
     *
     * @return
     ***************************************************************/
    public double getTotalScore(){
        return total_score;
    }

    /****************************************************************
     * This method is used to get list of delivered mailItems.
     *
     * @return ArrayList of delivered mailItem objects
     ***************************************************************/
    public ArrayList<MailItem> getDeliveredMail(){
        return MAIL_DELIVERED;
    }

    /****************************************************************
     * This method confirm the delivery and calculate the total score.
     *
     * @param deliveryItem
     ***************************************************************/
    public void deliver(MailItem deliveryItem){

        if(!MAIL_DELIVERED.contains(deliveryItem)){
            MAIL_DELIVERED.add(deliveryItem);
            System.out.printf("T: %3d > Delivered(%4d) [%s]%n", Clock.Time(), MAIL_DELIVERED.size(), deliveryItem.toString());

            // Calculate delivery score
            total_score += calculateDeliveryScore(deliveryItem);
        }
        else{
            try {
                throw new MailAlreadyDeliveredException();
            } catch (MailAlreadyDeliveredException e) {
                e.printStackTrace();
            }
        }
    }

    /****************************************************************
     * This method calculate the delivery score.
     *
     * @param deliveryItem
     * @return
     ***************************************************************/
    private static double calculateDeliveryScore(MailItem deliveryItem) {

        // Penalty for longer delivery times
        final double penalty = 1.2;
        double priority_weight = 0;

        if(deliveryItem instanceof PriorityMailItem){
            priority_weight = ((PriorityMailItem) deliveryItem).getPriorityLevel();
        }
        return Math.pow(Clock.Time() - deliveryItem.getArrivalTime(),penalty)*(1+Math.sqrt(priority_weight));
    }
}
