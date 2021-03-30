import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class GUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("NFA2DFA");
        frame.setLayout(null);
        frame.setBounds(300, 300, 400, 200);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.white);
        panel.setBounds(0, 0, 400, 200);

        JLabel label = new JLabel("文件名", JLabel.CENTER);
        label.setFont(new Font("微软雅黑", Font.BOLD, 15));
        label.setBounds(0, 0, 400, 50);

        JButton buttonChoose = new JButton("选择文件");
        buttonChoose.setFont(new Font("微软雅黑", Font.BOLD, 15));
        buttonChoose.setBounds(50, 50, 100, 50);
        buttonChoose.addActionListener(e -> {
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".xml");
                }

                @Override
                public String getDescription() {
                    return "xml";
                }
            };
            JFileChooser fileChooser = new JFileChooser(new File("./"));
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.showDialog(new JLabel(), "选择文件");
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                label.setText(file.getAbsolutePath());
            }
        });

        JButton buttonConvert = new JButton("转换");
        buttonConvert.setFont(new Font("微软雅黑", Font.BOLD, 15));
        buttonConvert.setBounds(150, 50, 100, 50);
        buttonConvert.addActionListener(e -> {
            String source = label.getText();
            if (!source.equals("文件名") && !source.equals("ERROR")) {
                try {
                    NodeList NFA = new NodeList();
                    NFA.loadFromXml(source);
                    NFA.parseNFAWithoutE();
                    NFA.generateXML("WithoutE.xml");
                    NodeList DFA = NFA.parseDFA();
                    DFA.generateXML("DFA.xml");
                    System.exit(0);
                } catch (Exception exception) {
                    label.setText("ERROR");
                }
            }
        });

        JButton buttonExit = new JButton("退出");
        buttonExit.setFont(new Font("微软雅黑", Font.BOLD, 15));
        buttonExit.setBounds(250, 50, 100, 50);
        buttonExit.addActionListener(e -> System.exit(0));


        frame.add(panel);
        panel.add(buttonChoose);
        panel.add(buttonConvert);
        panel.add(buttonExit);
        panel.add(label);

        frame.setVisible(true);
    }
}
