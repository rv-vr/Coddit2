package coddit2.components;

import coddit2.components.TasteHighlighter;
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
    private float currentFontSize = 12f;

    public CodeEditor(String content, String fileName) {
        super(new BorderLayout());
        
        textPane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getUI().getPreferredSize(this).width <= getParent().getSize().width;
            }
        };
        textPane.setText(content);
        textPane.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        textPane.setMargin(new java.awt.Insets(0, 5, 0, 5));

        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(Color.LIGHT_GRAY);
        lineNumbers.setEditable(false);
        lineNumbers.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        lineNumbers.setMargin(new java.awt.Insets(0, 5, 0, 5));

        undoManager = new UndoManager();
        
        if (fileName != null && (fileName.endsWith(".taste") || fileName.endsWith(".sauce"))) {
            setupHighlighter();
        }
        setupUndoRedo();
        setupSmartInput();
        setupFontZoom();
        setupContextMenu();
        setupLineNumbers();

        scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumbers);
        
        add(scrollPane, BorderLayout.CENTER);
        
        this.setBorder(null);
        textPane.setBorder(null);
        scrollPane.setBorder(null);
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

    private void setupSmartInput() {
        // Map Tab to 4 spaces
        textPane.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "insert-spaces");
        textPane.getActionMap().put("insert-spaces", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int caret = textPane.getCaretPosition();
                    textPane.getDocument().insertString(caret, "    ", null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        textPane.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    e.consume();
                    try {
                        int caret = textPane.getCaretPosition();
                        int rowStart = javax.swing.text.Utilities.getRowStart(textPane, caret);
                        String line = textPane.getText(rowStart, caret - rowStart);
                        
                        StringBuilder whitespace = new StringBuilder();
                        for (char c : line.toCharArray()) {
                            if (Character.isWhitespace(c)) {
                                whitespace.append(c);
                            } else {
                                break;
                            }
                        }
                        String indent = whitespace.toString();
                        String trimmed = line.trim();
                        
                        String nextChar = "";
                        if (caret < textPane.getDocument().getLength()) {
                            nextChar = textPane.getText(caret, 1);
                        }
                        
                        boolean splitBrace = (trimmed.endsWith("{") && "}".equals(nextChar));
                        boolean splitBracket = (trimmed.endsWith("[") && "]".equals(nextChar));
                        boolean splitParen = (trimmed.endsWith("(") && ")".equals(nextChar));
                        
                        if (splitBrace || splitBracket || splitParen) {
                            String insert = "\n" + indent + "    " + "\n" + indent;
                            textPane.getDocument().insertString(caret, insert, null);
                            textPane.setCaretPosition(caret + indent.length() + 5);
                        } else {
                            String prefix = "\n" + indent;
                            if (trimmed.endsWith("{") || trimmed.endsWith("[") || trimmed.endsWith("(") || trimmed.endsWith(":")) {
                                prefix += "    ";
                            }
                            textPane.getDocument().insertString(caret, prefix, null);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                String pair = "";
                switch (c) {
                    case '{': pair = "}"; break;
                    case '(': pair = ")"; break;
                    case '[': pair = "]"; break;
                    case '"': pair = "\""; break;
                }
                
                if (!pair.isEmpty()) {
                    try {
                        int caret = textPane.getCaretPosition();
                        textPane.getDocument().insertString(caret, String.valueOf(c) + pair, null);
                        textPane.setCaretPosition(caret + 1);
                        e.consume();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupFontZoom() {
        javax.swing.Action zoomIn = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFontSize += 1.0f;
                Font newFont = textPane.getFont().deriveFont(currentFontSize);
                textPane.setFont(newFont);
                lineNumbers.setFont(newFont);
            }
        };

        javax.swing.Action zoomOut = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFontSize = Math.max(8.0f, currentFontSize - 1.0f);
                Font newFont = textPane.getFont().deriveFont(currentFontSize);
                textPane.setFont(newFont);
                lineNumbers.setFont(newFont);
            }
        };

        javax.swing.Action zoomReset = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFontSize = 12.0f;
                Font newFont = textPane.getFont().deriveFont(currentFontSize);
                textPane.setFont(newFont);
                lineNumbers.setFont(newFont);
            }
        };

        javax.swing.InputMap inputMap = textPane.getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
        javax.swing.ActionMap actionMap = textPane.getActionMap();

        // Zoom In: Ctrl + =, Ctrl + Numpad +
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ZoomIn");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ZoomIn");
        
        // Zoom Out: Ctrl + -, Ctrl + Numpad -
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ZoomOut");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ZoomOut");

        // Reset: Ctrl + 0
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ZoomReset");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_NUMPAD0, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ZoomReset");

        actionMap.put("ZoomIn", zoomIn);
        actionMap.put("ZoomOut", zoomOut);
        actionMap.put("ZoomReset", zoomReset);
    }

    private void setupContextMenu() {
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
        
        javax.swing.JMenuItem cut = new javax.swing.JMenuItem(new javax.swing.text.DefaultEditorKit.CutAction());
        cut.setText("Cut");
        popup.add(cut);
        
        javax.swing.JMenuItem copy = new javax.swing.JMenuItem(new javax.swing.text.DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        popup.add(copy);
        
        javax.swing.JMenuItem paste = new javax.swing.JMenuItem(new javax.swing.text.DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        popup.add(paste);
        
        popup.addSeparator();
        
        javax.swing.JMenuItem selectAll = new javax.swing.JMenuItem("Select All");
        selectAll.addActionListener(e -> textPane.selectAll());
        popup.add(selectAll);
        
        textPane.setComponentPopupMenu(popup);
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
