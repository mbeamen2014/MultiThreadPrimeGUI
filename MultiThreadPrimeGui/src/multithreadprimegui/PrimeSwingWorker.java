/*
 * Multithreaded GUI - Prime numbers
 * 
 */
package multithreadprimegui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 *
 * CLASS DESCRIPTION: PrimeSwingWork inherits from SwingWorker
 * and implements its abstract methods. For this project, a new instance of the
 * PrimeSwingWorker class is created for every number from 1 to N. This class
 * implements a background thread and then publishes a result to the Event
 * thread, where the GUI is then updated.
 */
public class PrimeSwingWorker extends SwingWorker<Boolean, Integer> {

   private final Integer n;
   private Integer mostRecentPrimeValue;
   private Integer mostRecentTotalValue;
   private JTextArea output;
   private JTextArea numPrimesField;
   private AtomicInteger atomicCounter;
   private JTextArea totalField;
   private AtomicInteger totalCounter;
   private ArrayList<String> outputList = new ArrayList<String>();

  /**
    *
    * Constructor
    * @param Integer n, JTextArea output, JTextArea numPrimesField, AtomicInteger atomicCounter, 
    * JTextArea totalField, AtomicInteger totalCounter
    */
   public PrimeSwingWorker(Integer n, JTextArea output, JTextArea numPrimesField, 
                           AtomicInteger atomicCounter, JTextArea totalField, AtomicInteger totalCounter) {
        this.n = n;
        this.output = output;
        this.numPrimesField = numPrimesField;
        this.atomicCounter = atomicCounter;
        this.totalField = totalField;
        this.totalCounter = totalCounter;
   }

   /**
    *
    * PrimeSwingWork inherits from SwingWorker and implements its abstract
    * methods. For this project, a new instance of the PrimeSwingWorker class is
    * created for every number from 1 to N. This class implements a background
    * thread and then publishes a result to the Event thread, where the GUI is
    * then updated.
    *
    * @return isPrime: true - the number is prime; false - number is not prime
    * @param none
    */
   @Override
   protected Boolean doInBackground() throws Exception {

        //try-catch handles Interrupted Exception when the shutdownNow()
      //method is called.
        try {
             Thread.sleep(100);
        } 
        catch (InterruptedException ex) {
             System.out.println(Thread.currentThread().getName() + " was interrupted: " + ex);
        }

        boolean isPrime = true;

        if (n <= 1) {
           isPrime = false;
           
        }

        for (int j = 2; j < n; j++) {
            if (n % j == 0) {
                 isPrime = false;
                 
                 break;
            }
        }
      
        if (isPrime) {
           
         publish(n); // publish to process() method
        }
        
        return isPrime; // returns to done() method.
   }

   /**
    *
    * process accepts the published integer that is prime and updates the GUI in the Event
    * thread. The event thread accepts integers from all threads running
    * concurrently. As multiple threads are passing values to the single event
    * thread, the List never grows. The value is simply appended to the text
    * area in the GUI. The numbers appear semi-randomly as they are based on how
    * the JVM handles the threads.
    *
    * @param List<Integer> ints
    */
   @Override
   protected void process(List<Integer> ints) {
        mostRecentPrimeValue = atomicCounter.incrementAndGet();
        output.append(Integer.toString(ints.get(ints.size() - 1)) + ", ");
       
   }

   /**
    *
    * done updates the GUI in the Event thread after the threads are completed.
    * The get() method is called to receive the return value (isPrime) from the
    * background threads. If the value is true, then the text field in the GUI
    * receives the most recent value updated in the Event thread through the
    * process method.
    */
   @Override
   protected void done() {
  
      mostRecentTotalValue = totalCounter.incrementAndGet();
      totalField.setText(mostRecentTotalValue.toString());
     
      try {
         
         if (this.get() == true) {
                if(validateData(mostRecentPrimeValue, mostRecentTotalValue)){
                numPrimesField.setText(mostRecentPrimeValue.toString());
            }
            else{
                output.append("Invariant violated");   
            }
          }
        
      } 
      catch (InterruptedException ex) {
           Logger.getLogger(PrimeSwingWorker.class.getName()).log(Level.SEVERE, null, ex);
      } 
      catch (ExecutionException ex) {
           Logger.getLogger(PrimeSwingWorker.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
   /*
    * validateData() compares the number of primes with the total number of
    * integers processed at the time. The result returns false if the 
    * number of primes ever exceeds the total number processed, which should
    * never occur. 
    * @param Integer numPrimes, Integer totalProcessed
    * @return boolean result
    */
   private boolean validateData(Integer numPrimes, Integer totalProcessed){
      boolean result = true;
           if (numPrimes > totalProcessed){
                result = false;   
           }
       return result;
   }
   
}
