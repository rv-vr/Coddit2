package coddit2.managers;

import coddit2.Coddit2;
import coddit2.components.CodeEditor;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import coddit2.utils.FileHandler;

public class EditorTabManager {
    private final Coddit2 mainFrame;
    private final JTabbedPane editorTabs;

    public EditorTabManager(Coddit2 mainFrame, JTabbedPane editorTabs) {
        this.mainFrame = mainFrame;
        this.editorTabs = editorTabs;
    }

    public void createNewTab() {
        createNewTab(getNextUntitledName(), "", null);
    }

    public void createNewTab(String title, String content, File file) {
        String fileName = (file != null) ? file.getName() : title;
        CodeEditor editor = new CodeEditor(content, fileName);
        JTextComponent textArea = editor.getTextPane();
        
        if (file != null) {
            textArea.putClientProperty("file", file);
        }
        
        // Listener to update tab title bold state
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void changedUpdate(DocumentEvent de) { setTabTitleBold(textArea, true); }
            @Override public void insertUpdate(DocumentEvent de) { setTabTitleBold(textArea, true); }
            @Override public void removeUpdate(DocumentEvent de) { setTabTitleBold(textArea, true); }
        });
        
        textArea.addCaretListener(e -> updateCursorPosition());

        editorTabs.addTab(title, editor);
        
        int index = editorTabs.indexOfComponent(editor);
        editorTabs.setTabComponentAt(index, createTabTitlePanel(title));
        
        editorTabs.setSelectedComponent(editor);
    }

    public void closeTab(int index) {
        if (index >= 0) {
            editorTabs.remove(index);
        }
    }

    public void setTabTitleBold(JTextComponent textArea, boolean bold) {
        for (int i = 0; i < editorTabs.getTabCount(); i++) {
            Component comp = editorTabs.getComponentAt(i);
            if (comp instanceof CodeEditor) {
                CodeEditor editor = (CodeEditor) comp;
                if (editor.getTextPane() == textArea) {
                    Component tabComp = editorTabs.getTabComponentAt(i);
                    if (tabComp instanceof JPanel) {
                        JPanel panel = (JPanel) tabComp;
                        for (Component c : panel.getComponents()) {
                            if (c instanceof JLabel) {
                                JLabel label = (JLabel) c;
                                Font font = label.getFont();
                                int style = bold ? Font.BOLD : Font.PLAIN;
                                if (font.getStyle() != style) {
                                    label.setFont(font.deriveFont(style));
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private String getNextUntitledName() {
        int count = editorTabs.getTabCount();
        
        boolean baseExists = false;
        for (int i = 0; i < count; i++) {
            if (editorTabs.getTitleAt(i).equals("Untitled")) {
                baseExists = true;
                break;
            }
        }
        
        if (!baseExists) {
            return "Untitled";
        }

        int nextIndex = 1;
        while (true) {
            String candidate = "Untitled-" + nextIndex;
            boolean exists = false;
            for (int i = 0; i < count; i++) {
                if (editorTabs.getTitleAt(i).equals(candidate)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                return candidate;
            }
            nextIndex++;
        }
    }

    private JPanel createTabTitlePanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        
        JLabel label = new JLabel(title);
        
        JButton closeBtn = new JButton();
        closeBtn.setFont(new Font("Segoe MDL2 Assets", 0, 12)); 
        closeBtn.setText("");
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Component source = (Component) evt.getSource();
                Container tabTitlePanel = source.getParent();
                int index = editorTabs.indexOfTabComponent(tabTitlePanel);
                closeTab(index);
            }
        });
        
        panel.add(label);
        panel.add(closeBtn);
        
        return panel;
    }

    public JTextComponent getCurrentTextArea() {
        Component selected = editorTabs.getSelectedComponent();
        if (selected instanceof CodeEditor) {
            return ((CodeEditor) selected).getTextPane();
        }
        return null;
    }

    public void updateCursorPosition() {
        JTextComponent textArea = getCurrentTextArea();
        if (textArea != null) {
            try {
                int caretPos = textArea.getCaretPosition();
                int line = -1;
                int col = -1;
                
                // Get line and column
                Element root = textArea.getDocument().getDefaultRootElement();
                line = root.getElementIndex(caretPos) + 1;
                int startOfLineOffset = root.getElement(line - 1).getStartOffset();
                col = caretPos - startOfLineOffset + 1;
                
                mainFrame.setStatusText("Line: " + line + ", Column: " + col);
            } catch (Exception e) {
                mainFrame.setStatusText("Line: -, Column: -");
            }
        } else {
            mainFrame.setStatusText("");
        }
    }

    public void updateTabTitle(int index, String title) {
        editorTabs.setTitleAt(index, title);
        editorTabs.setTabComponentAt(index, createTabTitlePanel(title));
    }

    public void openFileInTab(File file) {
        // Check if file is already open
        for (int i = 0; i < editorTabs.getTabCount(); i++) {
            Component comp = editorTabs.getComponentAt(i);
            if (comp instanceof CodeEditor) {
                CodeEditor editor = (CodeEditor) comp;
                JTextComponent text = editor.getTextPane();
                File openFile = (File) text.getClientProperty("file");
                if (openFile != null && openFile.equals(file)) {
                    editorTabs.setSelectedIndex(i);
                    return;
                }
            }
        }

        if (FileHandler.isBinaryFile(file)) {
            createNewTab(file.getName(), "This file cannot be opened.", file);
            JTextComponent textArea = getCurrentTextArea();
            if (textArea != null) {
                textArea.setEditable(false);
            }
            return;
        }

        try {
            String content = FileHandler.readFile(file);
            createNewTab(file.getName(), content, file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error reading file: " + ex.getMessage());
        }
    }
}
