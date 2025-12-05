package coddit2;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;

public class FileTreeMouseListener extends MouseAdapter {
    private final Coddit2 mainFrame;
    private final JTree fileTree;

    public FileTreeMouseListener(Coddit2 mainFrame, JTree fileTree) {
        this.mainFrame = mainFrame;
        this.fileTree = fileTree;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = fileTree.getClosestRowForLocation(e.getX(), e.getY());
            fileTree.setSelectionRow(row);
            showPopupMenu(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            TreePath path = fileTree.getPathForLocation(evt.getX(), evt.getY());
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof File) {
                    File file = (File) userObject;
                    if (file.isFile()) {
                        mainFrame.openFileInTab(file);
                    }
                }
            }
        }
    }

    private void showPopupMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        
        JMenuItem newItem = new JMenuItem("New File");
        newItem.addActionListener(evt -> mainFrame.createNewFile());
        popup.add(newItem);
        
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(evt -> mainFrame.deleteFile());
        popup.add(deleteItem);
        
        popup.addSeparator();
        
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(evt -> mainFrame.refreshFileTree());
        popup.add(refreshItem);
        
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}
