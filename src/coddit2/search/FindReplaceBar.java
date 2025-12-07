package coddit2.search;

import coddit2.managers.EditorTabManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class FindReplaceBar extends JPanel {
    private final EditorTabManager tabManager;
    private JTextField searchField;
    private JTextField replaceField;
    private JButton findNextBtn;
    private JButton findPrevBtn;
    private JButton replaceBtn;
    private JButton replaceAllBtn;
    private JButton closeSearchBtn;
    private JLabel replaceLabel;

    public FindReplaceBar(EditorTabManager tabManager) {
        this.tabManager = tabManager;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchField = new JTextField(15);
        replaceField = new JTextField(15);
        
        findNextBtn = new JButton("Next");
        findPrevBtn = new JButton("Prev");
        replaceBtn = new JButton("Replace");
        replaceAllBtn = new JButton("Replace All");
        
        closeSearchBtn = new JButton("\uE711");
        closeSearchBtn.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 12));
        closeSearchBtn.setBorderPainted(false);
        closeSearchBtn.setContentAreaFilled(false);
        closeSearchBtn.setFocusable(false);
        closeSearchBtn.setMargin(new Insets(5, 10, 5, 10));
        closeSearchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeSearchBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                closeSearchBtn.setContentAreaFilled(true);
                closeSearchBtn.setBackground(new Color(242, 242, 242));
                closeSearchBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent evt) {
                closeSearchBtn.setContentAreaFilled(false);
                closeSearchBtn.setForeground(UIManager.getColor("Button.foreground"));
            }
        });
        
        replaceLabel = new JLabel("Replace:");
        
        // Add components
        controlsPanel.add(new JLabel("Find:"));
        controlsPanel.add(searchField);
        controlsPanel.add(findNextBtn);
        controlsPanel.add(findPrevBtn);
        
        controlsPanel.add(replaceLabel);
        controlsPanel.add(replaceField);
        controlsPanel.add(replaceBtn);
        controlsPanel.add(replaceAllBtn);
        
        add(controlsPanel, BorderLayout.CENTER);
        add(closeSearchBtn, BorderLayout.EAST);
        
        setVisible(false);
        
        // Listeners
        closeSearchBtn.addActionListener(e -> setVisible(false));
        
        findNextBtn.addActionListener(e -> findNext());
        findPrevBtn.addActionListener(e -> findPrev());
        replaceBtn.addActionListener(e -> replace());
        replaceAllBtn.addActionListener(e -> replaceAll());
    }

    public void showSearchBar(boolean replaceMode) {
        setVisible(true);
        replaceLabel.setVisible(replaceMode);
        replaceField.setVisible(replaceMode);
        replaceBtn.setVisible(replaceMode);
        replaceAllBtn.setVisible(replaceMode);
        searchField.requestFocus();
        revalidate();
    }

    private void findNext() {
        SearchEngine.findNext(tabManager.getCurrentTextArea(), searchField.getText());
    }

    private void findPrev() {
        SearchEngine.findPrev(tabManager.getCurrentTextArea(), searchField.getText());
    }

    private void replace() {
        SearchEngine.replace(tabManager.getCurrentTextArea(), searchField.getText(), replaceField.getText());
    }

    private void replaceAll() {
        SearchEngine.replaceAll(tabManager.getCurrentTextArea(), searchField.getText(), replaceField.getText());
    }
}
