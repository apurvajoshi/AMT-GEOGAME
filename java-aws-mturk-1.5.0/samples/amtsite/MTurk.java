
package amtsite;

import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.service.exception.ServiceException;
import com.amazonaws.mturk.util.PropertiesClientConfig;
import com.amazonaws.mturk.requester.HIT;


public class MTurk {
  private RequesterService service;

  // Defining the atributes of the HIT to be created
  private String title = "Play a game";
  private String description = 
    "Play a simple game geo location game and help us in our study.";
  private int numAssignments = 200;
  private double reward = 0.05;

  /**
   * Constructor
   * 
   */
  public MTurk() {
    service = new RequesterService(new PropertiesClientConfig("../mturk.properties"));
  }

  /**
   * Check if there are enough funds in your account in order to create the HIT
   * on Mechanical Turk
   * 
   * @return true if there are sufficient funds. False if not.
   */
  public boolean hasEnoughFund() {
    double balance = service.getAccountBalance();
    System.out.println("Got account balance: " + RequesterService.formatCurrency(balance));
    return balance > reward;
  }

  /**
   * Creates the simple HIT.
   * 
   */
  public void createHelloWorld() {
    try {

      // The createHIT method is called using a convenience static method of
      // RequesterService.getBasicFreeTextQuestion that generates the QAP for
      // the HIT.
      HIT hit = service.createHIT(
              title,
              description,
              reward,
              RequesterService.getBasicFreeTextQuestion("1)Go to this link http://halle.psy.cmu.edu/\n  2) Register yourself\n  3) Play the game\n  4) Once you have finished the game you will get a code. Paste the code in the below box to complete the HIT."),
              numAssignments);

      System.out.println("Created HIT: " + hit.getHITId());

      System.out.println("You may see your HIT with HITTypeId '" 
          + hit.getHITTypeId() + "' here: ");
      System.out.println(service.getWebsiteURL() 
          + "/mturk/preview?groupId=" + hit.getHITTypeId());

    } catch (ServiceException e) {
      System.err.println(e.getLocalizedMessage());
    }
  }

  /**
   * Main method
   * 
   * @param args
   */
  public static void main(String[] args) {

    MTurk app = new MTurk();

    if (app.hasEnoughFund()) {
      app.createHelloWorld();
      System.out.println("Success.");
    } else {
      System.out.println("You do not have enough funds to create the HIT.");
    }
  }
}
