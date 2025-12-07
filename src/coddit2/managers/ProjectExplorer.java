package coddit2.managers;

import coddit2.Coddit2;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class ProjectExplorer {
    private final Coddit2 mainFrame;
    private final JTree fileTree;
    private final JSplitPane splitPane;
    private File currentProjectFolder;

    public ProjectExplorer(Coddit2 mainFrame, JTree fileTree, JSplitPane splitPane) {
        this.mainFrame = mainFrame;
        this.fileTree = fileTree;
        this.splitPane = splitPane;
    }

    public void setupFileTree() {
        fileTree.setShowsRootHandles(true);
        
        final Icon folderIcon = loadIcon("folder.svg");
        final Icon fileIcon = loadIcon("file.svg");
        final Icon sauceIcon = loadIcon("burger.svg");
        
        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof File) {
                        File file = (File) userObject;
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

        fileTree.addMouseListener(new FileTreeMouseListener(mainFrame, fileTree));
    }

    private Icon loadIcon(String name) {
        try {
            // Try file system first (Dev mode)
            File f = new File("src/icons/" + name);
            if (f.exists()) {
                return new FlatSVGIcon(f).derive(16, 16);
            }
            // Try classpath
            return new FlatSVGIcon("icons/" + name).derive(16, 16);
        } catch (Exception e) {
            System.err.println("Error loading icon " + name + ": " + e.getMessage());
            return null;
        }
    }

    public void updateFileTree(File folder) {
        this.currentProjectFolder = folder;
        mainFrame.setTitle("Coddit - " + folder.getName());
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(folder);
        createTreeNodes(folder, root);
        fileTree.setModel(new DefaultTreeModel(root));
        
        fileTree.expandRow(0);
        fileTree.validate();
        
        SwingUtilities.invokeLater(() -> {
            Dimension treeSize = fileTree.getPreferredSize();
            int newDividerLocation = treeSize.width + 64;
            int maxWidth = 300;  
            if (newDividerLocation > maxWidth) {
                newDividerLocation = maxWidth;
            }
            
            splitPane.setDividerLocation(newDividerLocation);
        });
    }

    private void createTreeNodes(File file, DefaultMutableTreeNode node) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File child : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                node.add(childNode);
                if (child.isDirectory()) {
                    createTreeNodes(child, childNode);
                }
            }
        }
    }

    public void createNewFile() {
        TreePath path = fileTree.getSelectionPath();
        if (path == null) return;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        File selectedFile = (File) node.getUserObject();
        
        File parentDir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
        
        String name = JOptionPane.showInputDialog(mainFrame, "Enter file name:");
        if (name != null && !name.trim().isEmpty()) {
            File newFile = new File(parentDir, name);
            try {
                if (newFile.createNewFile()) {
                    updateFileTree(currentProjectFolder);
                    mainFrame.openFileInTab(newFile);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "File already exists or could not be created.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error creating file: " + ex.getMessage());
            }
        }
    }

    public void deleteFile() {
        TreePath path = fileTree.getSelectionPath();
        if (path == null) return;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        File selectedFile = (File) node.getUserObject();
        
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "Are you sure you want to delete " + selectedFile.getName() + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (deleteRecursive(selectedFile)) {
                updateFileTree(currentProjectFolder);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Could not delete file/directory.");
            }
        }
    }
    
    private boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                deleteRecursive(c);
            }
        }
        return file.delete();
    }

    public void refreshFileTree() {
        if (currentProjectFolder != null) {
            updateFileTree(currentProjectFolder);
        }
    }
    
    public File getCurrentProjectFolder() {
        return currentProjectFolder;
    }
}
