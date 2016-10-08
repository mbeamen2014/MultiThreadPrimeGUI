/*
 * Multithreaded GUI - Prime numbers
 * 
 */
package multithreadprimegui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * Class Description: This class initializes all Swing components and creates
 * and shows the GUI. The actionPerformed methods launch the prime number 
 * calculation method and cancel the processing. The main thread and event
 * thread are created in this class.
 */
public class MultiThreadPrimeGui {

   private static PrimeSwingWorker pws;
   private static AtomicInteger atomicPrimeCounter;
   private static AtomicInteger atomicTotalCounter;
 /**
   *
   * initFrame initializes the JFrame and sets it to visible.
   * @param none
   * @return none
   */
   private static void initFrame() {

      JFrame frame = new JFrame("Find the Primes");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      initComponentsInPane(frame.getContentPane());
      frame.pack();
      frame.setVisible(true);
   }

 /**
   *
   * initComponentsInPane initializes the Swing components and adds action
   * listeners that create Event threads. 
   * @param Container pane
   * @return none
   */
   public static void initComponentsInPane(Container pane) {

      JButton process = new JButton("Process");
      final JButton cancel = new JButton("Cancel");
      final JTextField userNumber = new JTextField();
      userNumber.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            userNumber.setText("");//Set text in user entry field to empty on click.
         }
      });

      JLabel label = new JLabel("Enter a number: ");
      final JTextArea numPrimesField = new JTextArea("0");
      numPrimesField.setEditable(false);
      JLabel label2 = new JLabel("Number of primes up to N: ");
      final JTextArea output = new JTextArea();
      JScrollPane scroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      output.setEditable(false);
      output.setLineWrap(true);
      output.setWrapStyleWord(true);
      final JTextArea totalField = new JTextArea("0");
      numPrimesField.setEditable(false);
      JLabel label4 = new JLabel("Total numbers processed: ");     

      JPanel panel1 = new JPanel();
      panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
      panel1.setPreferredSize(new Dimension(300, 500));
      pane.add(panel1, BorderLayout.CENTER);

      panel1.add(Box.createRigidArea(new Dimension(30, 30)));
      panel1.add(process, panel1);
      panel1.add(Box.createRigidArea(new Dimension(30, 30)));
      panel1.add(cancel, panel1);

      //Process button action: call start() and begin calculating primes
      process.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {

            output.setText("");
            numPrimesField.setText("0");
            totalField.setText("0");
            String text = userNumber.getText();
            output.append("Finding primes for " + text + ":\n");
            userNumber.selectAll();
            
            //Determines if the input is an Integer.
            try {
               int n = Integer.parseInt(text);
               if (n <= 2500000){
                  start(n, output, totalField,numPrimesField, cancel);
               }
               else{
                  output.setText("Numbers over 1 million will cause performance to degrade further");
               }
            } catch (NumberFormatException ex) {
               System.out.println(ex);
               output.setText("Enter only numbers please");
            }
         }
      });

      JPanel panel2 = new JPanel();
      panel2.setPreferredSize(new Dimension(300, 500));
      pane.add(panel2, BorderLayout.LINE_START);
      panel2.add(label);
      userNumber.setPreferredSize(new Dimension(100, 30));
      panel2.add(userNumber);
      panel2.add(label2);
      numPrimesField.setPreferredSize(new Dimension(100, 30));
      panel2.add(numPrimesField);
      panel2.add(label4);
      totalField.setPreferredSize(new Dimension(100,30));
      panel2.add(totalField);

      JLabel label3 = new JLabel("Output: ");
      JPanel panel3 = new JPanel();
      panel3.setPreferredSize(new Dimension(300, 500));
      pane.add(panel3, BorderLayout.LINE_END);
      scroll.setPreferredSize(new Dimension(250, 400));
      panel3.add(label3);
      panel3.add(scroll);
   }

 /**
   *
   * start sets the size of the Thread Pool, instantiates an ExecutorService, 
   * and loops 1 from N, where N is the number entered by the user. For each 
   * number, a SwingWorker object is instantiated and submitted to the Executor
   * Service, so the number of SwingWorker tasks will be equivalent to the number
   * entered by the user. This creates overhead that slows down the 
   * processing when entering very large numbers.
   * @param int n, JTextArea outputArea, JTextArea numPrimesField, JButton butt2
   * @return none
   */
   private static void start(int n, JTextArea outputArea, JTextArea totalField, JTextArea numPrimesField, JButton butt2) {
      
      atomicPrimeCounter = new AtomicInteger(Integer.parseInt(numPrimesField.getText()));
      atomicTotalCounter = new AtomicInteger(Integer.parseInt(totalField.getText()));
      int numberOfThreads = 100;
      final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
      
      //Create a SwingWorker for each number from 1 to N and then submit using 
      // the thread pool. This causes performane issues.
      for (int i = 1; i <= n; i++) {
           pws = new PrimeSwingWorker(i, outputArea, numPrimesField, atomicPrimeCounter, totalField, atomicTotalCounter);
           executor.submit(pws);//submits each task   
      }

      //Cancel button
      butt2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
              executor.shutdownNow(); // Interrupts threads.
         }
      });
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {

      //Schedule a job for the event dispatch thread:
      //creating and showing this application's GUI.
      SwingUtilities.invokeLater(new Runnable() {

           @Override
           public void run() {
                initFrame();
           }
      });
   }
}
