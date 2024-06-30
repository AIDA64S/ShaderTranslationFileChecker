import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

public class FileChecker {
    private final JFrame frame = new JFrame("Shader Translation File Checker");
    private final JTextField sourceFilePath;
    private final JTextField targetFilePath;
    private final JFileChooser fileChooser;
    private String sourceFilePathText;
    private String targetFilePathText;

    public static void main(String[] args) {
        new FileChecker();
    }

    public FileChecker() {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 600);
        this.frame.setLayout(new GridLayout(3, 3));

        this.sourceFilePath = new JTextField();
        this.targetFilePath = new JTextField();
        this.fileChooser = new JFileChooser();

        JButton sourceFileSelectButton = new JButton("Select  document");
        JButton targetFileSelectButton = new JButton("Select the file you want to check");
        JButton checkButton = new JButton("Check");

        sourceFileSelectButton.addActionListener((e) -> {
            int returnVal = this.fileChooser.showOpenDialog(null);
            if (returnVal == 0) {
                this.sourceFilePathText = this.fileChooser.getSelectedFile().getAbsolutePath();
                this.sourceFilePath.setText(this.sourceFilePathText);
            }
        });

        targetFileSelectButton.addActionListener((e) -> {
            int returnVal = this.fileChooser.showOpenDialog(null);
            if (returnVal == 0) {
                this.targetFilePathText = this.fileChooser.getSelectedFile().getAbsolutePath();
                this.targetFilePath.setText(this.targetFilePathText);
            }
        });

        checkButton.addActionListener((e) -> {
            Properties sourceProperties = this.loadProperties(this.sourceFilePathText);
            Properties targetProperties = this.loadProperties(this.targetFilePathText);
            if (sourceProperties != null && targetProperties != null) {
                Set<String> sourceKeys = sourceProperties.stringPropertyNames();
                Set<String> targetKeys = targetProperties.stringPropertyNames();
                StringBuilder stringBuilder = new StringBuilder();
                for (String key : sourceKeys) {
                    if (!targetKeys.contains(key)) {
                        stringBuilder.append("Key missing: ").append(key).append("\n");
                    }
                }
                for (String key : targetKeys) {
                    if (!sourceKeys.contains(key)) {
                        stringBuilder.append("Extra Key : ").append(key).append("\n");
                    }
                }
                if (stringBuilder.length() > 0) {
                    JOptionPane.showMessageDialog(this.frame, stringBuilder.toString(), "result", 1);
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Inspection passed!", "result", 1);
                }
            }

        });
        this.frame.add(new JLabel("Official language file"));
        this.frame.add(this.sourceFilePath);
        this.frame.add(sourceFileSelectButton);
        this.frame.add(new JLabel("Unofficial language file"));
        this.frame.add(this.targetFilePath);
        this.frame.add(targetFileSelectButton);
        this.frame.add(new JLabel());
        this.frame.add(checkButton);
        this.frame.setVisible(true);
    }

    private Properties loadProperties(String filePath) {
        try {
            if (!filePath.endsWith(".lang")) {
                throw new IOException("File does not end with .lang");
            } else {
                Properties properties = new Properties();
                try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
                    properties.load(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                }
                return properties;
            }
        } catch (IOException var16) {
            JOptionPane.showMessageDialog(this.frame, "Unable to read file: \"" + filePath + " Please check if your file path is correct.", "Error", 0);
            return null;
        }
    }
}