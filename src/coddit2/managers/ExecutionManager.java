package coddit2.managers;

import coddit2.Coddit2;
import coddit2.utils.FileHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.text.JTextComponent;
import taste.TASTE;

public class ExecutionManager {
    private final Coddit2 mainFrame;
    private final EditorTabManager tabManager;
    private final JEditorPane outputTab;
    private final JTabbedPane outputPane;
    private final JCheckBoxMenuItem funErrCode;

    public ExecutionManager(Coddit2 mainFrame, EditorTabManager tabManager, JEditorPane outputTab, JTabbedPane outputPane, JCheckBoxMenuItem funErrCode) {
        this.mainFrame = mainFrame;
        this.tabManager = tabManager;
        this.outputTab = outputTab;
        this.outputPane = outputPane;
        this.funErrCode = funErrCode;
    }

    public void runCode() {
        JTextComponent textArea = tabManager.getCurrentTextArea();
        if (textArea != null) {
            File file = (File) textArea.getClientProperty("file");
            if (file == null) {
                JOptionPane.showMessageDialog(mainFrame, "Please save the file before running.");
                return;
            }
            
            // Auto-save before running
            try {
                FileHandler.saveFile(file, textArea.getText());
                tabManager.setTabTitleBold(textArea, false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error saving file: " + ex.getMessage());
                return;
            }
            
            // Switch to Output tab
            outputPane.setSelectedIndex(0);
            outputTab.setText("");
            
            // Loading Animation
            String[] frames = {"⣾", "⣽", "⣻", "⢿", "⡿", "⣟", "⣯", "⣷"};
            Timer animTimer = new Timer(100, new ActionListener() {
                int i = 0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    outputTab.setText(frames[i] + " " +"Running " + file.getName());
                    i = (i + 1) % frames.length;
                }
            });
            animTimer.start();
            
            // Run in background
            new SwingWorker<TASTE.RunResult, Void>() {
                @Override
                protected TASTE.RunResult doInBackground() throws Exception {
                    Thread.sleep(600); // Aesthetic delay
                    return TASTE.run(file, funErrCode.isSelected());
                }

                @Override
                protected void done() {
                    animTimer.stop();
                    try {
                        TASTE.RunResult result = get();
                        String output = result.getOutput();
                        boolean success = !output.contains("⨯") && !output.contains("Error");
                        
                        String header = success ? "✔ Execution Successful" : "⨯ Execution Failed";
                        outputTab.setText(header + "\n\n" + output);
                        
                    } catch (Exception ex) {
                        outputTab.setText("Internal Error: " + ex.getMessage());
                    }
                }
            }.execute();
        }
    }

    public void stopCode() {
        
    }
}
