package coddit2;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TasteHighlighter implements DocumentListener {
    private JTextPane textPane;
    private SimpleAttributeSet keywordStyle;
    private SimpleAttributeSet stringStyle;
    private SimpleAttributeSet normalStyle;
    private SimpleAttributeSet commentStyle;
    private SimpleAttributeSet valueStyle;
    private SimpleAttributeSet typeStyle;

    public TasteHighlighter(JTextPane textPane) {
        this.textPane = textPane;
        
        // Define styles
        StyleContext sc = StyleContext.getDefaultStyleContext();
        
        // Keywords: Blue and Bold
        AttributeSet keywordAttrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0, 0, 200)); 
        keywordAttrs = sc.addAttribute(keywordAttrs, StyleConstants.Bold, true);
        keywordStyle = new SimpleAttributeSet(keywordAttrs);

        // Types: Teal/Green (VS Code Class Color)
        AttributeSet typeAttrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(43, 145, 175)); 
        typeAttrs = sc.addAttribute(typeAttrs, StyleConstants.Bold, true);
        typeStyle = new SimpleAttributeSet(typeAttrs);

        // Strings: Green
        AttributeSet stringAttrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0, 150, 0)); 
        stringStyle = new SimpleAttributeSet(stringAttrs);
        
        // Comments: Gray and Italic
        AttributeSet commentAttrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(128, 128, 128)); 
        commentAttrs = sc.addAttribute(commentAttrs, StyleConstants.Italic, true);
        commentStyle = new SimpleAttributeSet(commentAttrs);
        
        // Values (Yum/Yuck): Orange
        AttributeSet valueAttrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(200, 100, 0));
        valueAttrs = sc.addAttribute(valueAttrs, StyleConstants.Bold, true);
        valueStyle = new SimpleAttributeSet(valueAttrs);

        // Normal Text: Black
        AttributeSet normalAttrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        normalAttrs = sc.addAttribute(normalAttrs, StyleConstants.Bold, false);
        normalStyle = new SimpleAttributeSet(normalAttrs);
    }

    public void highlight() {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                String text = doc.getText(0, doc.getLength());
                
                // Reset to normal
                doc.setCharacterAttributes(0, text.length(), normalStyle, true);

                // 1. Highlight Keywords
                String keywords = "\\b(grab|chew|spit|nibble|burp|swallow)\\b";
                Pattern keywordPattern = Pattern.compile(keywords);
                Matcher keywordMatcher = keywordPattern.matcher(text);
                while (keywordMatcher.find()) {
                    doc.setCharacterAttributes(keywordMatcher.start(), keywordMatcher.end() - keywordMatcher.start(), keywordStyle, false);
                }
                
                // 2. Highlight Types
                String types = "\\b(Chunk|Slurp|Wrap|Taste)\\b";
                Pattern typePattern = Pattern.compile(types);
                Matcher typeMatcher = typePattern.matcher(text);
                while (typeMatcher.find()) {
                    doc.setCharacterAttributes(typeMatcher.start(), typeMatcher.end() - typeMatcher.start(), typeStyle, false);
                }
                
                // 3. Highlight Values (Yum/Yuck)
                String values = "\\b(Yum|Yuck)\\b";
                Pattern valuePattern = Pattern.compile(values);
                Matcher valueMatcher = valuePattern.matcher(text);
                while (valueMatcher.find()) {
                    doc.setCharacterAttributes(valueMatcher.start(), valueMatcher.end() - valueMatcher.start(), valueStyle, false);
                }

                // 4. Highlight Strings
                Pattern stringPattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
                Matcher stringMatcher = stringPattern.matcher(text);
                while (stringMatcher.find()) {
                    doc.setCharacterAttributes(stringMatcher.start(), stringMatcher.end() - stringMatcher.start(), stringStyle, false);
                }
                
                // 5. Highlight Comments (Last to override others if inside comment)
                Pattern commentPattern = Pattern.compile("//.*");
                Matcher commentMatcher = commentPattern.matcher(text);
                while (commentMatcher.find()) {
                    doc.setCharacterAttributes(commentMatcher.start(), commentMatcher.end() - commentMatcher.start(), commentStyle, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void insertUpdate(DocumentEvent e) { highlight(); }

    @Override
    public void removeUpdate(DocumentEvent e) { highlight(); }

    @Override
    public void changedUpdate(DocumentEvent e) { }
}
