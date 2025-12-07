package coddit2.utils;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.swing.JFileChooser;

public class FileHandler {

    public static String readFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static void saveFile(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    public static File openFileChooser(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static File saveFileChooser(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static File openDirectoryChooser(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static boolean isBinaryFile(File file) {
        try {
            // Check for common binary extensions
            String name = file.getName().toLowerCase();
            if (name.endsWith(".exe") || name.endsWith(".dll") || name.endsWith(".obj") || 
                name.endsWith(".bin") || name.endsWith(".jpg") || name.endsWith(".png") || 
                name.endsWith(".gif") || name.endsWith(".mp4") || name.endsWith(".mp3") ||
                name.endsWith(".zip") || name.endsWith(".jar") || name.endsWith(".class") ||
                name.endsWith(".pdf") || name.endsWith(".iso")) {
                return true;
            }
            
            // Heuristic: Read first 8KB and look for null bytes
            try (InputStream in = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead = in.read(buffer);
                if (bytesRead == -1) return false; // Empty file
                
                for (int i = 0; i < bytesRead; i++) {
                    if (buffer[i] == 0) return true;
                }
            }
            return false;
        } catch (IOException e) {
            return true; // Treat as binary/unreadable on error
        }
    }
}
