/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package coddit2;
import com.formdev.flatlaf.*;
import javax.swing.*;
import java.awt.Color;
import taste.TASTE;


/**
 *
 * @author vince
 */
public class Coddit2 extends javax.swing.JFrame {

    private java.io.File currentProjectFolder;
    private ConfigManager configManager;
    private javax.swing.JMenu openRecentMenu;
    private javax.swing.JLabel statusLabel;

    // Search Bar Components
    private javax.swing.JPanel SearchBar;
    private javax.swing.JTextField SearchField;
    private javax.swing.JTextField ReplaceField;
    private javax.swing.JButton FindNextBtn;
    private javax.swing.JButton FindPrevBtn;
    private javax.swing.JButton ReplaceBtn;
    private javax.swing.JButton ReplaceAllBtn;
    private javax.swing.JButton CloseSearchBtn;
    private javax.swing.JLabel ReplaceLabel;

    /**
     * Creates new form Coddit2
     */
    public Coddit2() {
        initComponents();
        
        // Re-layout components to allow full-width status bar
        getContentPane().remove(RunBar);
        getContentPane().remove(HorizontalSep);
        
        javax.swing.JPanel mainContainer = new javax.swing.JPanel(new java.awt.BorderLayout());
        mainContainer.add(HorizontalSep, java.awt.BorderLayout.CENTER);
        mainContainer.add(RunBar, java.awt.BorderLayout.EAST);
        
        getContentPane().add(mainContainer, java.awt.BorderLayout.CENTER);
        
        configManager = new ConfigManager();
        setupRecentMenu();
        
        // Check for first instance and load last project
        if (configManager.acquireInstanceLock()) {
            String lastProj = configManager.getLastProject();
            if (lastProj != null) {
                java.io.File projFile = new java.io.File(lastProj);
                if (projFile.exists()) {
                    updateFileTree(projFile);
                }
            }
        }
        
        // Initialize Search Bar
        createSearchBar();
        setupStatusBar();
        setupOutputTabHeader();
        
        // Setup Undo/Redo in Edit Menu
        javax.swing.JMenuItem undoItem = new javax.swing.JMenuItem("Undo");
        undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> {
            javax.swing.text.JTextComponent textArea = getCurrentTextArea();
            if (textArea != null) {
                javax.swing.undo.UndoManager um = (javax.swing.undo.UndoManager) textArea.getClientProperty("undoManager");
                if (um != null && um.canUndo()) um.undo();
            }
        });
        
        javax.swing.JMenuItem redoItem = new javax.swing.JMenuItem("Redo");
        redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        redoItem.addActionListener(e -> {
            javax.swing.text.JTextComponent textArea = getCurrentTextArea();
            if (textArea != null) {
                javax.swing.undo.UndoManager um = (javax.swing.undo.UndoManager) textArea.getClientProperty("undoManager");
                if (um != null && um.canRedo()) um.redo();
            }
        });
        
        EditMenu.add(undoItem, 0);
        EditMenu.add(redoItem, 1);
        EditMenu.add(new javax.swing.JPopupMenu.Separator(), 2);
        
        // Initialize File Tree
        setupFileTree();

        
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        this.getRootPane().putClientProperty("JRootPane.menuBarEmbedded", true);
        
        if (currentProjectFolder != null) {
            this.setTitle("Coddit - " + currentProjectFolder.getName());
        } else {
            this.setTitle("Coddit");
        }
        
        this.setLocationRelativeTo(null); 

        // Add Tab on New Start
        createNewTab();
        
        // Close Tab Shortcut (Ctrl+W)
        javax.swing.JRootPane rootPane = this.getRootPane();
        javax.swing.InputMap inputMap = rootPane.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        javax.swing.ActionMap actionMap = rootPane.getActionMap();
        
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK), "CloseTab");
        actionMap.put("CloseTab", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int index = EditorTabs.getSelectedIndex();
                if (index != -1) {
                    closeTab(index);
                }
            }
        });
        
        
        this.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(0xDBDBDB));
        this.revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        RunBar = new javax.swing.JPanel();
        RunCode = new javax.swing.JButton();
        StopCode = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        HorizontalSep = new javax.swing.JSplitPane();
        VerticalSep1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        OutputPane = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        OutputTab = new javax.swing.JEditorPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        TerminalTab = new javax.swing.JEditorPane();
        EditorTabs = new javax.swing.JTabbedPane();
        FileTreeScroll = new javax.swing.JScrollPane();
        FileTree = new javax.swing.JTree();
        WindowMenu = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        NewFile = new javax.swing.JMenuItem();
        OpenProj = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        SaveFile = new javax.swing.JMenuItem();
        SaveFileAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ExitIDE = new javax.swing.JMenuItem();
        EditMenu = new javax.swing.JMenu();
        CopyText = new javax.swing.JMenuItem();
        CutText = new javax.swing.JMenuItem();
        PasteText = new javax.swing.JMenuItem();
        FindMenu = new javax.swing.JMenuItem();
        ReplaceMenu = new javax.swing.JMenuItem();
        TASTEMenu = new javax.swing.JMenu();
        FunErrCode = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(720, 480));

        RunBar.setBackground(new java.awt.Color(234, 234, 234));
        RunBar.setLayout(new java.awt.GridBagLayout());

        RunCode.setBackground(new java.awt.Color(234, 234, 234));
        RunCode.setFont(new java.awt.Font("Segoe Fluent Icons", 1, 18)); // NOI18N
        RunCode.setForeground(new java.awt.Color(0, 153, 0));
        RunCode.setText("");
        RunCode.setToolTipText("Run Code");
        RunCode.setBorderPainted(false);
        RunCode.setFocusable(false);
        RunCode.setMaximumSize(new java.awt.Dimension(35, 35));
        RunCode.setPreferredSize(new java.awt.Dimension(35, 35));
        RunCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunCodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        RunBar.add(RunCode, gridBagConstraints);

        StopCode.setBackground(new java.awt.Color(234, 234, 234));
        StopCode.setFont(new java.awt.Font("Segoe Fluent Icons", 1, 18)); // NOI18N
        StopCode.setForeground(new java.awt.Color(255, 0, 0));
        StopCode.setText("");
        StopCode.setToolTipText("Stop Program");
        StopCode.setBorderPainted(false);
        StopCode.setFocusable(false);
        StopCode.setMaximumSize(new java.awt.Dimension(35, 35));
        StopCode.setPreferredSize(new java.awt.Dimension(35, 35));
        StopCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StopCodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        RunBar.add(StopCode, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weighty = 1.0;
        RunBar.add(filler1, gridBagConstraints);

        getContentPane().add(RunBar, java.awt.BorderLayout.EAST);

        HorizontalSep.setBackground(new java.awt.Color(234, 234, 234));
        HorizontalSep.setResizeWeight(0.3);
        HorizontalSep.setToolTipText("");

        VerticalSep1.setBackground(new java.awt.Color(228, 228, 228));
        VerticalSep1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        VerticalSep1.setResizeWeight(0.7);

        jScrollPane1.setBackground(new java.awt.Color(102, 255, 0));
        jScrollPane1.setBorder(null);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N

        jEditorPane1.setPreferredSize(null);
        jScrollPane1.setViewportView(jEditorPane1);

        VerticalSep1.setTopComponent(jScrollPane1);

        OutputPane.setBackground(new java.awt.Color(255, 255, 255));
        OutputPane.setFocusable(false);
        OutputPane.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        OutputPane.setOpaque(true);

        jScrollPane3.setBackground(new java.awt.Color(102, 102, 0));
        jScrollPane3.setBorder(null);
        jScrollPane3.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N

        OutputTab.setEditable(false);
        OutputTab.setBackground(new java.awt.Color(255, 255, 255));
        OutputTab.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(OutputTab);

        OutputPane.addTab("Output", jScrollPane3);

        jScrollPane4.setBackground(new java.awt.Color(102, 102, 0));
        jScrollPane4.setBorder(null);

        TerminalTab.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        jScrollPane4.setViewportView(TerminalTab);

        OutputPane.addTab("Terminal", jScrollPane4);

        VerticalSep1.setRightComponent(OutputPane);

        EditorTabs.setBackground(new java.awt.Color(255, 255, 255));
        EditorTabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        EditorTabs.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        EditorTabs.setOpaque(true);
        EditorTabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                EditorTabsStateChanged(evt);
            }
        });
        VerticalSep1.setTopComponent(EditorTabs);

        HorizontalSep.setRightComponent(VerticalSep1);

        FileTreeScroll.setBorder(null);

        FileTree.setBackground(new java.awt.Color(234, 234, 234));
        FileTree.setBorder(null);
        FileTree.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        FileTree.setModel(null);
        FileTreeScroll.setViewportView(FileTree);

        HorizontalSep.setLeftComponent(FileTreeScroll);

        getContentPane().add(HorizontalSep, java.awt.BorderLayout.CENTER);

        WindowMenu.setBackground(new java.awt.Color(212, 212, 212));
        WindowMenu.setBorder(null);
        WindowMenu.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        WindowMenu.setName(""); // NOI18N

        FileMenu.setText("File");
        FileMenu.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N

        NewFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        NewFile.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        NewFile.setText("New File");
        NewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewFileActionPerformed(evt);
            }
        });
        FileMenu.add(NewFile);

        OpenProj.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        OpenProj.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        OpenProj.setText("Open Project");
        OpenProj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenProjActionPerformed(evt);
            }
        });
        FileMenu.add(OpenProj);
        FileMenu.add(jSeparator3);

        SaveFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        SaveFile.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        SaveFile.setText("Save");
        SaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveFileActionPerformed(evt);
            }
        });
        FileMenu.add(SaveFile);

        SaveFileAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        SaveFileAs.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        SaveFileAs.setText("Save As.");
        SaveFileAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveFileAsActionPerformed(evt);
            }
        });
        FileMenu.add(SaveFileAs);
        FileMenu.add(jSeparator1);

        ExitIDE.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ExitIDE.setText("Exit");
        ExitIDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitIDEActionPerformed(evt);
            }
        });
        FileMenu.add(ExitIDE);

        WindowMenu.add(FileMenu);

        EditMenu.setText("Edit");
        EditMenu.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N

        CopyText.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        CopyText.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        CopyText.setText("Copy");
        EditMenu.add(CopyText);

        CutText.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        CutText.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        CutText.setText("Cut");
        EditMenu.add(CutText);

        PasteText.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        PasteText.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        PasteText.setText("Paste");
        EditMenu.add(PasteText);

        FindMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        FindMenu.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        FindMenu.setText("Find");
        FindMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindMenuActionPerformed(evt);
            }
        });
        EditMenu.add(FindMenu);

        ReplaceMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        ReplaceMenu.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ReplaceMenu.setText("Replace");
        ReplaceMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReplaceMenuActionPerformed(evt);
            }
        });
        EditMenu.add(ReplaceMenu);

        WindowMenu.add(EditMenu);

        TASTEMenu.setText("TASTE");
        TASTEMenu.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N

        FunErrCode.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        FunErrCode.setSelected(true);
        FunErrCode.setText("Fun Error Codes");
        TASTEMenu.add(FunErrCode);

        WindowMenu.add(TASTEMenu);

        setJMenuBar(WindowMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OpenProjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenProjActionPerformed
        java.io.File folder = FileHandler.openDirectoryChooser(this);
        if (folder != null) {
            updateFileTree(folder);
            configManager.addRecentProject(folder.getAbsolutePath());
            updateRecentMenu();
        }
    }//GEN-LAST:event_OpenProjActionPerformed

    private void SaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveFileActionPerformed
        javax.swing.text.JTextComponent textArea = getCurrentTextArea();
        if (textArea != null) {
            java.io.File file = (java.io.File) textArea.getClientProperty("file");
            if (file != null) {
                try {
                    FileHandler.saveFile(file, textArea.getText());
                    setTabTitleBold(textArea, false);
                } catch (java.io.IOException ex) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                }
            } else {
                SaveFileAsActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_SaveFileActionPerformed

    private void ExitIDEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitIDEActionPerformed
        System.exit(0);
    }//GEN-LAST:event_ExitIDEActionPerformed

    private void SaveFileAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveFileAsActionPerformed
        javax.swing.text.JTextComponent textArea = getCurrentTextArea();
        if (textArea != null) {
            java.io.File file = (java.io.File) textArea.getClientProperty("file");
            if (file == null) {
                file = FileHandler.saveFileChooser(this);
            }
            
            if (file != null) {
                try {
                    FileHandler.saveFile(file, textArea.getText());
                    textArea.putClientProperty("file", file);
                    
                    // Update tab title
                    int index = EditorTabs.getSelectedIndex();
                    EditorTabs.setTitleAt(index, file.getName());
                    EditorTabs.setTabComponentAt(index, createTabTitlePanel(file.getName()));
                    setTabTitleBold(textArea, false);
                    
                } catch (java.io.IOException ex) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_SaveFileAsActionPerformed

    private void NewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewFileActionPerformed
        createNewTab();
    }//GEN-LAST:event_NewFileActionPerformed

    private void ReplaceMenuActionPerformed(java.awt.event.ActionEvent evt) {                                               
        showSearchBar(false);
    }                                          

    private void FindMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        showSearchBar(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void EditorTabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_EditorTabsStateChanged
        updateCursorPosition();
    }//GEN-LAST:event_EditorTabsStateChanged

    private void RunCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunCodeActionPerformed
        javax.swing.text.JTextComponent textArea = getCurrentTextArea();
        if (textArea != null) {
            java.io.File file = (java.io.File) textArea.getClientProperty("file");
            if (file == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Please save the file before running.");
                return;
            }
            
            // Auto-save before running
            try {
                FileHandler.saveFile(file, textArea.getText());
                setTabTitleBold(textArea, false);
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                return;
            }
            
            // Switch to Output tab
            OutputPane.setSelectedIndex(0);
            OutputTab.setText("");
            
            // Loading Animation
            String[] frames = {"⣾", "⣽", "⣻", "⢿", "⡿", "⣟", "⣯", "⣷"};
            javax.swing.Timer animTimer = new javax.swing.Timer(100, new java.awt.event.ActionListener() {
                int i = 0;
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    OutputTab.setText(frames[i] + " " +"Running " + file.getName());
                    i = (i + 1) % frames.length;
                }
            });
            animTimer.start();
            
            // Run in background
            new javax.swing.SwingWorker<TASTE.RunResult, Void>() {
                @Override
                protected TASTE.RunResult doInBackground() throws Exception {
                    Thread.sleep(600); // Aesthetic delay
                    return TASTE.run(file, FunErrCode.isSelected());
                }

                @Override
                protected void done() {
                    animTimer.stop();
                    try {
                        TASTE.RunResult result = get();
                        String output = result.getOutput();
                        boolean success = !output.contains("⨯") && !output.contains("Error");
                        
                        String header = success ? "✔ Execution Successful" : "⨯ Execution Failed";
                        OutputTab.setText(header + "\n\n" + output);
                        
                    } catch (Exception ex) {
                        OutputTab.setText("Internal Error: " + ex.getMessage());
                    }
                }
            }.execute();
        }
    }//GEN-LAST:event_RunCodeActionPerformed

    private void StopCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StopCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StopCodeActionPerformed
     
    private void setupRecentMenu() {
        openRecentMenu = new javax.swing.JMenu("Open Recent");
        openRecentMenu.setFont(new java.awt.Font("Roboto", 0, 12));
        FileMenu.add(openRecentMenu, 2); // Insert after Open Project
        updateRecentMenu();
    }

    private void updateRecentMenu() {
        openRecentMenu.removeAll();
        java.util.List<String> recent = configManager.getRecentProjects();
        if (recent.isEmpty()) {
            javax.swing.JMenuItem item = new javax.swing.JMenuItem("No Recent Projects");
            item.setEnabled(false);
            openRecentMenu.add(item);
        } else {
            for (String path : recent) {
                javax.swing.JMenuItem item = new javax.swing.JMenuItem(path);
                item.setFont(new java.awt.Font("Roboto", 0, 12));
                item.addActionListener(e -> {
                    java.io.File folder = new java.io.File(path);
                    if (folder.exists()) {
                        updateFileTree(folder);
                        configManager.addRecentProject(path);
                        updateRecentMenu();
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this, "Project directory not found.");
                    }
                });
                openRecentMenu.add(item);
            }
        }
    }

    private void createSearchBar() {
        SearchBar = new javax.swing.JPanel();
        SearchBar.setLayout(new java.awt.BorderLayout());
        
        javax.swing.JPanel controlsPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        
        SearchField = new javax.swing.JTextField(15);
        ReplaceField = new javax.swing.JTextField(15);
        
        FindNextBtn = new javax.swing.JButton("Next");
        FindPrevBtn = new javax.swing.JButton("Prev");
        ReplaceBtn = new javax.swing.JButton("Replace");
        ReplaceAllBtn = new javax.swing.JButton("Replace All");
        
        CloseSearchBtn = new javax.swing.JButton("\uE711");
        CloseSearchBtn.setFont(new java.awt.Font("Segoe MDL2 Assets", java.awt.Font.PLAIN, 12));
        CloseSearchBtn.setBorderPainted(false);
        CloseSearchBtn.setContentAreaFilled(false);
        CloseSearchBtn.setFocusable(false);
        CloseSearchBtn.setMargin(new java.awt.Insets(5, 10, 5, 10));
        CloseSearchBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CloseSearchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                CloseSearchBtn.setContentAreaFilled(true);
                CloseSearchBtn.setBackground(new java.awt.Color(242, 242, 242));
                CloseSearchBtn.setForeground(java.awt.Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                CloseSearchBtn.setContentAreaFilled(false);
                CloseSearchBtn.setForeground(javax.swing.UIManager.getColor("Button.foreground"));
            }
        });
        
        ReplaceLabel = new javax.swing.JLabel("Replace:");
        
        // Add components
        controlsPanel.add(new javax.swing.JLabel("Find:"));
        controlsPanel.add(SearchField);
        controlsPanel.add(FindNextBtn);
        controlsPanel.add(FindPrevBtn);
        
        controlsPanel.add(ReplaceLabel);
        controlsPanel.add(ReplaceField);
        controlsPanel.add(ReplaceBtn);
        controlsPanel.add(ReplaceAllBtn);
        
        SearchBar.add(controlsPanel, java.awt.BorderLayout.CENTER);
        SearchBar.add(CloseSearchBtn, java.awt.BorderLayout.EAST);
        
        // Add to frame
        javax.swing.JPanel editorContainer = new javax.swing.JPanel(new java.awt.BorderLayout());
        editorContainer.add(EditorTabs, java.awt.BorderLayout.CENTER);
        editorContainer.add(SearchBar, java.awt.BorderLayout.SOUTH);
        VerticalSep1.setTopComponent(editorContainer);
        
        SearchBar.setVisible(false);
        
        // Listeners
        CloseSearchBtn.addActionListener(e -> SearchBar.setVisible(false));
        
        FindNextBtn.addActionListener(e -> findNext());
        FindPrevBtn.addActionListener(e -> findPrev());
        ReplaceBtn.addActionListener(e -> replace());
        ReplaceAllBtn.addActionListener(e -> replaceAll());
    }

    private void showSearchBar(boolean replaceMode) {
        SearchBar.setVisible(true);
        ReplaceLabel.setVisible(replaceMode);
        ReplaceField.setVisible(replaceMode);
        ReplaceBtn.setVisible(replaceMode);
        ReplaceAllBtn.setVisible(replaceMode);
        SearchField.requestFocus();
        SearchBar.revalidate();
    }

    private void findNext() {
        SearchEngine.findNext(getCurrentTextArea(), SearchField.getText());
    }

    private void findPrev() {
        SearchEngine.findPrev(getCurrentTextArea(), SearchField.getText());
    }

    private void replace() {
        SearchEngine.replace(getCurrentTextArea(), SearchField.getText(), ReplaceField.getText());
    }

    private void replaceAll() {
        SearchEngine.replaceAll(getCurrentTextArea(), SearchField.getText(), ReplaceField.getText());
    }

    private javax.swing.Icon loadIcon(String name) {
        try {
            // Try file system first (Dev mode)
            java.io.File f = new java.io.File("src/icons/" + name);
            if (f.exists()) {
                return new com.formdev.flatlaf.extras.FlatSVGIcon(f).derive(16, 16);
            }
            // Try classpath
            return new com.formdev.flatlaf.extras.FlatSVGIcon("icons/" + name).derive(16, 16);
        } catch (Exception e) {
            System.err.println("Error loading icon " + name + ": " + e.getMessage());
            return null;
        }
    }

    private void setupFileTree() {
        FileTree.setShowsRootHandles(true);
        
        final javax.swing.Icon folderIcon = loadIcon("folder.svg");
        final javax.swing.Icon fileIcon = loadIcon("file.svg");
        final javax.swing.Icon sauceIcon = loadIcon("burger.svg");
        
        FileTree.setCellRenderer(new javax.swing.tree.DefaultTreeCellRenderer() {
            @Override
            public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof javax.swing.tree.DefaultMutableTreeNode) {
                    Object userObject = ((javax.swing.tree.DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof java.io.File) {
                        java.io.File file = (java.io.File) userObject;
                        setText(file.getName());
                        
                        if (file.isDirectory()) {
                            if (folderIcon != null) setIcon(folderIcon);
                        } else {
                            if (file.getName().endsWith(".sauce") && sauceIcon != null) {
                                setIcon(sauceIcon);
                            } else if (fileIcon != null) {
                                setIcon(fileIcon);
                            }
                        }
                    }
                }
                return this;
            }
        });

        FileTree.addMouseListener(new FileTreeMouseListener(this, FileTree));
    }

    private void updateFileTree(java.io.File folder) {
        this.currentProjectFolder = folder;
        this.setTitle("Coddit - " + folder.getName());
        
        javax.swing.tree.DefaultMutableTreeNode root = new javax.swing.tree.DefaultMutableTreeNode(folder);
        createTreeNodes(folder, root);
        FileTree.setModel(new javax.swing.tree.DefaultTreeModel(root));
        
        FileTree.expandRow(0);
        FileTree.validate();
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.awt.Dimension treeSize = FileTree.getPreferredSize();
            int newDividerLocation = treeSize.width + 64;
            int maxWidth = 300; 
            if (newDividerLocation > maxWidth) {
                newDividerLocation = maxWidth;
            }
            
            HorizontalSep.setDividerLocation(newDividerLocation);
        });
    }

    void createNewFile() {
        javax.swing.tree.TreePath path = FileTree.getSelectionPath();
        if (path == null) return;
        
        javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent();
        java.io.File selectedFile = (java.io.File) node.getUserObject();
        
        java.io.File parentDir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
        
        String name = javax.swing.JOptionPane.showInputDialog(this, "Enter file name:");
        if (name != null && !name.trim().isEmpty()) {
            java.io.File newFile = new java.io.File(parentDir, name);
            try {
                if (newFile.createNewFile()) {
                    updateFileTree(currentProjectFolder);
                    openFileInTab(newFile);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "File already exists or could not be created.");
                }
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error creating file: " + ex.getMessage());
            }
        }
    }

    void deleteFile() {
        javax.swing.tree.TreePath path = FileTree.getSelectionPath();
        if (path == null) return;
        
        javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent();
        java.io.File selectedFile = (java.io.File) node.getUserObject();
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + selectedFile.getName() + "?", 
            "Confirm Delete", 
            javax.swing.JOptionPane.YES_NO_OPTION);
            
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            if (deleteRecursive(selectedFile)) {
                updateFileTree(currentProjectFolder);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Could not delete file/directory.");
            }
        }
    }
    
    private boolean deleteRecursive(java.io.File file) {
        if (file.isDirectory()) {
            for (java.io.File c : file.listFiles()) {
                deleteRecursive(c);
            }
        }
        return file.delete();
    }

    void refreshFileTree() {
        if (currentProjectFolder != null) {
            updateFileTree(currentProjectFolder);
        }
    }

    void openFileInTab(java.io.File file) {
        // Check if file is already open
        for (int i = 0; i < EditorTabs.getTabCount(); i++) {
            java.awt.Component comp = EditorTabs.getComponentAt(i);
            if (comp instanceof CodeEditor) {
                CodeEditor editor = (CodeEditor) comp;
                javax.swing.text.JTextComponent text = editor.getTextPane();
                java.io.File openFile = (java.io.File) text.getClientProperty("file");
                if (openFile != null && openFile.equals(file)) {
                    EditorTabs.setSelectedIndex(i);
                    return;
                }
            }
        }

        if (FileHandler.isBinaryFile(file)) {
            createNewTab(file.getName(), "This file cannot be opened.", file);
            javax.swing.text.JTextComponent textArea = getCurrentTextArea();
            if (textArea != null) {
                textArea.setEditable(false);
            }
            return;
        }

        try {
            String content = FileHandler.readFile(file);
            createNewTab(file.getName(), content, file);
        } catch (java.io.IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
        }
    }

    private void createTreeNodes(java.io.File file, javax.swing.tree.DefaultMutableTreeNode node) {
        java.io.File[] files = file.listFiles();
        if (files != null) {
            for (java.io.File child : files) {
                javax.swing.tree.DefaultMutableTreeNode childNode = new javax.swing.tree.DefaultMutableTreeNode(child);
                node.add(childNode);
                if (child.isDirectory()) {
                    createTreeNodes(child, childNode);
                }
            }
        }
    }

    private javax.swing.text.JTextComponent getCurrentTextArea() {
        java.awt.Component selected = EditorTabs.getSelectedComponent();
        if (selected instanceof CodeEditor) {
            return ((CodeEditor) selected).getTextPane();
        }
        return null;
    }

    private void setTabTitleBold(javax.swing.text.JTextComponent textArea, boolean bold) {
        for (int i = 0; i < EditorTabs.getTabCount(); i++) {
            java.awt.Component comp = EditorTabs.getComponentAt(i);
            if (comp instanceof CodeEditor) {
                CodeEditor editor = (CodeEditor) comp;
                if (editor.getTextPane() == textArea) {
                    java.awt.Component tabComp = EditorTabs.getTabComponentAt(i);
                    if (tabComp instanceof javax.swing.JPanel) {
                        javax.swing.JPanel panel = (javax.swing.JPanel) tabComp;
                        for (java.awt.Component c : panel.getComponents()) {
                            if (c instanceof javax.swing.JLabel) {
                                javax.swing.JLabel label = (javax.swing.JLabel) c;
                                java.awt.Font font = label.getFont();
                                int style = bold ? java.awt.Font.BOLD : java.awt.Font.PLAIN;
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

    private void closeTab(int index) {
        if (index >= 0) {
            EditorTabs.remove(index);
        }
    }
    
    private String getNextUntitledName() {
        javax.swing.JTabbedPane tabs = EditorTabs;
        int count = tabs.getTabCount();
        
        boolean baseExists = false;
        for (int i = 0; i < count; i++) {
            if (tabs.getTitleAt(i).equals("Untitled")) {
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
                if (tabs.getTitleAt(i).equals(candidate)) {
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

    private javax.swing.JPanel createTabTitlePanel(String title) {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setOpaque(false);
        
        javax.swing.JLabel label = new javax.swing.JLabel(title);
        
        javax.swing.JButton closeBtn = new javax.swing.JButton();
        closeBtn.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 12)); 
        closeBtn.setText("");
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                java.awt.Component source = (java.awt.Component) evt.getSource();
                java.awt.Container tabTitlePanel = source.getParent();
                int index = EditorTabs.indexOfTabComponent(tabTitlePanel);
                closeTab(index);
            }
        });
        
        panel.add(label);
        panel.add(closeBtn);
        
        return panel;
    }

    private void createNewTab() {
        createNewTab(getNextUntitledName(), "", null);
    }

    private void createNewTab(String title, String content, java.io.File file) {
        String fileName = (file != null) ? file.getName() : title;
        CodeEditor editor = new CodeEditor(content, fileName);
        javax.swing.JTextPane textArea = editor.getTextPane();
        
        if (file != null) {
            textArea.putClientProperty("file", file);
        }
        
        // Listener to update tab title bold state
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void changedUpdate(javax.swing.event.DocumentEvent de) { setTabTitleBold(textArea, true); }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent de) { setTabTitleBold(textArea, true); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent de) { setTabTitleBold(textArea, true); }
        });
        
        textArea.addCaretListener(e -> updateCursorPosition());

        EditorTabs.addTab(title, editor);
        
        int index = EditorTabs.indexOfComponent(editor);
        EditorTabs.setTabComponentAt(index, createTabTitlePanel(title));
        
        EditorTabs.setSelectedComponent(editor);
    }

    private void setupStatusBar() {
        javax.swing.JPanel statusBar = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        statusBar.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getColor("Component.borderColor")));
        statusLabel = new javax.swing.JLabel("Line: 1, Column: 1");
        statusLabel.setFont(new java.awt.Font("Roboto", 0, 11));
        statusBar.add(statusLabel);
        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
    }

    private void setupOutputTabHeader() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        panel.add(new javax.swing.JLabel("Output"));
        
        javax.swing.JButton clearBtn = new javax.swing.JButton(""); // Trash icon
        clearBtn.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 12));
        clearBtn.setToolTipText("Clear Output");
        clearBtn.setBorderPainted(false);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        clearBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> OutputTab.setText(""));
        
        panel.add(clearBtn);
        
        int index = OutputPane.indexOfComponent(jScrollPane3);
        if (index != -1) {
            OutputPane.setTabComponentAt(index, panel);
        }
    }

    private void updateCursorPosition() {
        javax.swing.text.JTextComponent textArea = getCurrentTextArea();
        if (textArea != null) {
            try {
                int caretPos = textArea.getCaretPosition();
                int line = -1;
                int col = -1;
                
                // Get line and column
                javax.swing.text.Element root = textArea.getDocument().getDefaultRootElement();
                line = root.getElementIndex(caretPos) + 1;
                int startOfLineOffset = root.getElement(line - 1).getStartOffset();
                col = caretPos - startOfLineOffset + 1;
                
                statusLabel.setText("Line: " + line + ", Column: " + col);
            } catch (Exception e) {
                statusLabel.setText("Line: -, Column: -");
            }
        } else {
            statusLabel.setText("");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Adjust DPI Scaling
        System.setProperty("sun.java2d.uiScale", "1.75");
        System.setProperty("sun.java2d.dpiaware", "true");
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Coddit2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Coddit2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Coddit2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Coddit2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        FlatLightLaf.setup();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Coddit2().setVisible(true);
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem CopyText;
    private javax.swing.JMenuItem CutText;
    private javax.swing.JMenu EditMenu;
    private javax.swing.JTabbedPane EditorTabs;
    private javax.swing.JMenuItem ExitIDE;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JTree FileTree;
    private javax.swing.JScrollPane FileTreeScroll;
    private javax.swing.JMenuItem FindMenu;
    private javax.swing.JCheckBoxMenuItem FunErrCode;
    private javax.swing.JSplitPane HorizontalSep;
    private javax.swing.JMenuItem NewFile;
    private javax.swing.JMenuItem OpenProj;
    private javax.swing.JTabbedPane OutputPane;
    private javax.swing.JEditorPane OutputTab;
    private javax.swing.JMenuItem PasteText;
    private javax.swing.JMenuItem ReplaceMenu;
    private javax.swing.JPanel RunBar;
    private javax.swing.JButton RunCode;
    private javax.swing.JMenuItem SaveFile;
    private javax.swing.JMenuItem SaveFileAs;
    private javax.swing.JButton StopCode;
    private javax.swing.JMenu TASTEMenu;
    private javax.swing.JEditorPane TerminalTab;
    private javax.swing.JSplitPane VerticalSep1;
    private javax.swing.JMenuBar WindowMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
