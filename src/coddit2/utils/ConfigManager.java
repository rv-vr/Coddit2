package coddit2.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;

public class ConfigManager {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".coddit2";
    private static final String RECENT_FILE = "recent_projects.txt";
    private static final String LOCK_FILE = "instance.lock";
    private static final int MAX_RECENT = 10;

    private List<String> recentProjects = new ArrayList<>();
    private FileLock instanceLock;
    private FileChannel lockChannel;

    public ConfigManager() {
        File dir = new File(CONFIG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        loadRecentProjects();
    }

    private void loadRecentProjects() {
        File file = new File(CONFIG_DIR, RECENT_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    recentProjects.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRecentProjects() {
        File file = new File(CONFIG_DIR, RECENT_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String path : recentProjects) {
                writer.write(path);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRecentProject(String path) {
        recentProjects.remove(path); // Remove if exists to move to top
        recentProjects.add(0, path);
        if (recentProjects.size() > MAX_RECENT) {
            recentProjects.remove(recentProjects.size() - 1);
        }
        saveRecentProjects();
    }

    public List<String> getRecentProjects() {
        return new ArrayList<>(recentProjects);
    }

    public String getLastProject() {
        return recentProjects.isEmpty() ? null : recentProjects.get(0);
    }

    public boolean acquireInstanceLock() {
        try {
            File file = new File(CONFIG_DIR, LOCK_FILE);
            lockChannel = new RandomAccessFile(file, "rw").getChannel();
            instanceLock = lockChannel.tryLock();
            return instanceLock != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Call this on shutdown if needed, though OS usually releases locks on process exit
    public void releaseLock() {
        try {
            if (instanceLock != null) instanceLock.release();
            if (lockChannel != null) lockChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
