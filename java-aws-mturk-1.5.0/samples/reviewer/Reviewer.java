package reviewer;

import java.util.List;

import com.amazonaws.mturk.addon.HITDataCSVReader;
import com.amazonaws.mturk.addon.HITDataCSVWriter;
import com.amazonaws.mturk.addon.HITDataInput;
import com.amazonaws.mturk.addon.HITTypeResults;
import com.amazonaws.mturk.dataschema.QuestionFormAnswers;
import com.amazonaws.mturk.dataschema.QuestionFormAnswersType;
import com.amazonaws.mturk.requester.Assignment;
import com.amazonaws.mturk.requester.AssignmentStatus;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.util.PropertiesClientConfig;

public class Reviewer {

  private RequesterService service;

  /**
   * Constructor
   *
   */
  public Reviewer() {
    service = new RequesterService(new PropertiesClientConfig("samples/mturk.properties"));
  }

  /**
   * Prints the submitted results of HITs when provided with a .success file.
   * @param successFile The .success file containing the HIT ID and HIT Type ID
   * @param outputFile The output file to write the submitted results to
   */
  public void printResults(String successFile, String outputFile) {

    try {

      //Loads the .success file containing the HIT IDs and HIT Type IDs of HITs to be retrieved.
      HITDataInput success = new HITDataCSVReader(successFile);

      //Retrieves the submitted results of the specified HITs from Mechanical Turk
      HITTypeResults results = service.getHITTypeResults(success);
      results.setHITDataOutput(new HITDataCSVWriter(outputFile));

      //Writes the submitted results to the defined output file.
      //The output file is a tab delimited file containing all relevant details
      //of the HIT and assignments.  The submitted results are included as the last set of fields
      //and are represented as tab separated question/answer pairs
      results.writeResults();

      System.out.println("Results have been written to: " + outputFile);

    } catch (Exception e) {
      System.err.println("ERROR: Could not print results: " + e.getLocalizedMessage());
    }
  }

  @SuppressWarnings("unchecked")
  /**
   * Prints the submitted results of a HIT when provided with a HIT ID.
   * @param hitId The HIT ID of the HIT to be retrieved.
   */
  public void reviewAnswers(String hitId) {
    
    //Assignment[] assignments = service.getAllSubmittedAssignmentsForHIT(hitId);
    Assignment[] assignments = service.getAllAssignmentsForHIT(hitId);

    System.out.println("--[Reviewing HITs]----------");
    System.out.println("  HIT Id: " + hitId);


    for (Assignment assignment : assignments) {

      //Only assignments that have been submitted will contain answer data
      if (assignment.getAssignmentStatus() == AssignmentStatus.Submitted) {
          
        //By default, answers are specified in XML
        String answerXML = assignment.getAnswer();

        //Calling a convenience method that will parse the answer XML and extract out the question/answer pairs.
        QuestionFormAnswers qfa = RequesterService.parseAnswers(answerXML);
        List<QuestionFormAnswersType.AnswerType> answers =
          (List<QuestionFormAnswersType.AnswerType>) qfa.getAnswer();

        for (QuestionFormAnswersType.AnswerType answer : answers) {

          String assignmentId = assignment.getAssignmentId();
          String answerValue = RequesterService.getAnswerValue(assignmentId, answer);

          if (answerValue != null) {
            System.out.println("Got an answer \"" + answerValue
                + "\" from worker " + assignment.getWorkerId() + ".");


          }
        }
        //Approving the assignment.
        service.approveAssignment(assignment.getAssignmentId(), "Well Done!");
        System.out.println("Approved.");

      }
    }

    System.out.println("--[End Reviewing HITs]----------");
  }

  /**
   * @param args
   */
  public static void main(String[] args) {

    Reviewer app = new Reviewer();

   if (args.length == 1 && !args[0].equals("")) {
      app.reviewAnswers(args[0]);
    } else if (args.length == 2 && !args[0].equals("") && !args[1].equals("")) {
      app.printResults(args[0], args[1]);
    }
  }
}
