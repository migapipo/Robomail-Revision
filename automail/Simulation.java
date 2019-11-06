package automail;

import strategies.Automail;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/*******************************************************************
 * This class simulates the behaviour of AutoMail
 ******************************************************************/
public class Simulation {
	
    /** Constant for the mail generator */
    private static int MAIL_TO_CREATE;
    private static int MAIL_MAX_WEIGHT;

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

    	Properties automailProperties = new Properties();

    	/** Default properties */
    	automailProperties.setProperty("Robots", "Standard");
    	automailProperties.setProperty("MailPool", "strategies.SimpleMailPool");
    	automailProperties.setProperty("Floors", "10");
    	automailProperties.setProperty("Fragile", "false");
    	automailProperties.setProperty("Mail_to_Create", "80");
    	automailProperties.setProperty("Last_Delivery_Time", "100");

    	/** Read properties */
		FileReader inStream = null;
		try {
			inStream = new FileReader("automail.properties");
			automailProperties.load(inStream);
		} finally {
			 if (inStream != null) {
	                inStream.close();
	            }
		}

		// Seed
		String seedProp = automailProperties.getProperty("Seed");
		// Floors
		Building.FLOORS = Integer.parseInt(automailProperties.getProperty("Floors"));
        System.out.printf("Floors: %5d%n", Building.FLOORS);
        // Fragile
        boolean fragile = Boolean.parseBoolean(automailProperties.getProperty("Fragile"));
        System.out.printf("Fragile: %5b%n", fragile);
		// Mail_to_Create
		MAIL_TO_CREATE = Integer.parseInt(automailProperties.getProperty("Mail_to_Create"));
        System.out.printf("Mail_to_Create: %5d%n", MAIL_TO_CREATE);
        // Mail_to_Create
     	MAIL_MAX_WEIGHT = Integer.parseInt(automailProperties.getProperty("Mail_Max_Weight"));
        System.out.printf("Mail_Max_Weight: %5d%n", MAIL_MAX_WEIGHT);
		// Last_Delivery_Time
		Clock.LAST_DELIVERY_TIME = Integer.parseInt(automailProperties.getProperty("Last_Delivery_Time"));
        System.out.printf("Last_Delivery_Time: %5d%n", Clock.LAST_DELIVERY_TIME);
		// Robots
		int robots = Integer.parseInt(automailProperties.getProperty("Robots"));
		System.out.print("Robots: "); System.out.println(robots);
		assert(robots > 0);

        /** Used to see whether a seed is initialized or not */
        HashMap<Boolean, Integer> seedMap = new HashMap<>();
        
        /** Read the first argument and save it as a seed if it exists */
        if (args.length == 0 ) { // No arg
        	if (seedProp == null) { // and no property
        		seedMap.put(false, 0); // so randomise
        	} else { // Use property seed
        		seedMap.put(true, Integer.parseInt(seedProp));
        	}
        } else { // Use arg seed - overrides property
        	seedMap.put(true, Integer.parseInt(args[0]));
        }

        Integer seed = seedMap.get(true);
        System.out.printf("Seed: %s%n", seed == null ? "null" : seed.toString());

        /** Initiate the mail generator and create all the mails*/
        MailGenerator mailGenerator = new MailGenerator(MAIL_TO_CREATE, MAIL_MAX_WEIGHT, seedMap);
        mailGenerator.generateAllMail();
        Map<Integer,ArrayList<MailItem>> allMail = mailGenerator.getAllMails();

        /** Initiate the ReportDelivery class to Keeps track of the delivered items and total scores. */
        ReportDelivery delivery = new ReportDelivery();

        /** Initiate the controller(Automail) with all the mails and number of robots given to the system. */
        Automail automail = new Automail(allMail, delivery, robots);

        /** Take a step at every clock tick until all the generated mails are delivered. */
        while (delivery.getDeliveredMail().size() != mailGenerator.MAIL_TO_CREATE) {
            automail.step();
            Clock.Tick();
        }

        /** Print the final delivery time and total-score. */
        printResults(delivery.getTotalScore());
    }

    private static void printResults(double total_score){
        System.out.println("T: " + Clock.Time() + " | Simulation complete!");
        System.out.println("Final Delivery time: " + Clock.Time());
        System.out.printf("Final Score: %.2f%n", total_score);
    }
}
