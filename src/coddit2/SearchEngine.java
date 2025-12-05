package coddit2;

import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;

/**
 * Handles text search and replacement operations within a text component.
 */
public class SearchEngine {
    
    public static void findNext(JTextComponent textArea, String search) {
        if (textArea == null || search == null || search.isEmpty()) return;
        
        String text = getText(textArea);
        int caret = textArea.getCaretPosition();
        int index = text.indexOf(search, caret);
        
        if (index == -1) {
            // Wrap around
            index = text.indexOf(search, 0);
        }
        
        selectFoundText(textArea, index, search.length());
    }

    public static void findPrev(JTextComponent textArea, String search) {
        if (textArea == null || search == null || search.isEmpty()) return;
        
        String text = getText(textArea);
        int caret = textArea.getSelectionStart();
        // Search backwards from caret - 1
        int index = text.lastIndexOf(search, caret - 1);
        
        if (index == -1) {
            // Wrap around to end
            index = text.lastIndexOf(search);
        }
        
        selectFoundText(textArea, index, search.length());
    }
    
    public static void replace(JTextComponent textArea, String search, String replacement) {
        if (textArea == null) return;
        
        String selection = textArea.getSelectedText();
        if (selection != null && selection.equals(search)) {
            textArea.replaceSelection(replacement);
            findNext(textArea, search);
        } else {
            findNext(textArea, search);
        }
    }
    
    public static void replaceAll(JTextComponent textArea, String search, String replacement) {
        if (textArea == null || search == null || search.isEmpty()) return;
        
        String text = getText(textArea);
        String newText = text.replace(search, replacement);
        textArea.setText(newText);
    }

    private static void selectFoundText(JTextComponent textArea, int index, int length) {
        if (index != -1) {
            textArea.select(index, index + length);
            textArea.grabFocus();
        } else {
            JOptionPane.showMessageDialog(textArea, "Text not found");
        }
    }

    private static String getText(JTextComponent textArea) {
        try {
            return textArea.getDocument().getText(0, textArea.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }
}
