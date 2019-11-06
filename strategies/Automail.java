package strategies;

import automail.*;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import java.util.*;

/*************************************************************************
 * The Class is the mail controller class of the Automail system. The class
 * execute the sequence of steps that makes the entire delivery system
 * process.
 ************************************************************************/
public class Automail {

    private int nRobots;
    private Robot [] robots;
    private IMailPool mailPool;
    private DeliveryScheme deliverySchemes;
    
    public Automail(Map<Integer, ArrayList<MailItem>> allMail, IMailDelivery delivery, int numRobots) {

        /** Number of robots in the system */
        this.nRobots = numRobots;

    	/** Initialize the MailPool */
    	this.mailPool = new MailPool(allMail);

    	/** Initialize robots */
        robots = new Robot[numRobots];

        /** Initialize Robots */
    	for (int i = 0; i < numRobots; i++) {
            robots[i] = new Robot(delivery);
        }

        /** Initialize delivery scheme with the mail-pool */
        deliverySchemes = new DeliveryScheme(mailPool);
    }

    /*************************************************************************
     * The method executes the steps for mailPool, diliveryScheme and robot
     * classes.
     ************************************************************************/
    public void step(){

        try {
            /** Step for mailPool */
            mailPool.step();

            /** Step for deliveryScheme */
            deliverySchemes.step();

            /** Step for all the robots in the system */
            for (int i = 0; i < nRobots; i++)
                robots[i].step(deliverySchemes);

        } catch (ExcessiveDeliveryException | ItemTooHeavyException e) {
            e.printStackTrace();
            System.out.println("Simulation unable to complete.");
            System.exit(0);
        }
    }
}
