
/**
 *
 * @author Jia
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GUIWindow window = new GUIWindow();
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
        });
    }
    
}
