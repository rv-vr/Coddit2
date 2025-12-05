package coddit2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.undo.UndoManager;

/**
 * Encapsulates the code editing component, including syntax highlighting,
 * line numbers, and undo/redo functionality.
 */
public class CodeEditor extends JPanel {
    private final JTextPane textPane;
    private final JTextArea lineNumbers;
    private final UndoManager undoManager;
    private final JScrollPane scrollPane;
    private int lastLineCount = 0;

    public CodeEditor(String content) {
        super(new BorderLayout());
        
        textPane = new JTextPane();
        textPane.setText(content);
        textPane.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        textPane.setMargin(new java.awt.Insets(0, 5, 0, 5));

        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(Color.LIGHT_GRAY);
        lineNumbers.setEditable(false);
        lineNumbers.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        lineNumbers.setMargin(new java.awt.Insets(0, 5, 0, 5));

        undoManager = new UndoManager();
        
        setupHighlighter();
        setupUndoRedo();
        setupLineNumbers();

        scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumbers);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    private void setupHighlighter() {
        TasteHighlighter highlighter = new TasteHighlighter(textPane);
        textPane.getDocument().addDocumentListener(highlighter);
        highlighter.highlight();
    }

    private void setupUndoRedo() {
        textPane.putClientProperty("undoManager", undoManager);
        
        textPane.getDocument().addUndoableEditListener(e -> {
            if (e.getEdit() instanceof DocumentEvent) {
                DocumentEvent de = (DocumentEvent) e.getEdit();
                if (de.getType() == DocumentEvent.EventType.CHANGE) {
                    return;
                }
            }
            undoManager.addEdit(e.getEdit());
        });

        textPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        textPane.getActionMap().put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) undoManager.undo();
            }
        });

        textPane.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        textPane.getActionMap().put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) undoManager.redo();
            }
        });
    }

    private void setupLineNumbers() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent de) {
                updateLineNumbers();
            }

            @Override
            public void insertUpdate(DocumentEvent de) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                updateLineNumbers();
            }
        };
        textPane.getDocument().addDocumentListener(listener);
        updateLineNumbers(); // Initial update
    }

    private void updateLineNumbers() {
        Element root = textPane.getDocument().getDefaultRootElement();
        int lineCount = root.getElementCount();
        
        if (lineCount != lastLineCount) {
             StringBuilder text = new StringBuilder();
             for (int i = 1; i <= lineCount; i++) {
                 text.append(i).append(System.getProperty("line.separator"));
             }
             lineNumbers.setText(text.toString());
             lastLineCount = lineCount;
        }
    }
}
