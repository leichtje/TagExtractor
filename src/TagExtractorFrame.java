import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class  TagExtractorFrame extends JFrame {
    ArrayList<String> stopWords = new ArrayList<>();
    TreeMap<String,Integer> map = new TreeMap<>();
    String[] words;
    boolean halt;
    JPanel mainPnl;
    JPanel chooserPnl;
    JButton txtFileBtn;
    JFileChooser txtFileChooser;
    File txtSelectedFile;
    JButton stopFileBtn;
    JFileChooser stopFileChooser;
    File stopSelectedFile;
    JButton startBtn;
    JPanel txtAreaPnl;
    JTextArea txtArea;
    JScrollPane scroller;
    JPanel ctrlPnl;
    JButton quitBtn;
    JButton clearBtn;
    JButton saveBtn;
    JFileChooser saveTxtFile;
    File workingDirectory = new File(System.getProperty("user.dir"));
    TagExtractorFrame(){
        setTitle("Tag Extractor");
        mainPnl = new JPanel();
        setLayout(new BorderLayout());

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize(3*(screenWidth / 4), 3*(screenHeight / 4));
        setLocationRelativeTo(null);

        setResizable(false);
        createChooserPnl();
        mainPnl.add(chooserPnl,BorderLayout.WEST);

        createTxtAreaPnl();
        mainPnl.add(txtAreaPnl,BorderLayout.CENTER);

        createCtrlPnl();
        mainPnl.add(ctrlPnl,BorderLayout.EAST);

        add(mainPnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    private void createChooserPnl(){
        chooserPnl = new JPanel();
        chooserPnl.setLayout(new GridLayout(3, 1));
        txtFileBtn = new JButton();
        txtFileBtn.setText("Select text file");
        txtFileBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        txtFileBtn.addActionListener((ActionEvent ae) ->{
            txtFileChooser = new JFileChooser();
            txtFileChooser.setCurrentDirectory(workingDirectory);
            txtFileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt","text"));
            int result = txtFileChooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                txtSelectedFile = txtFileChooser.getSelectedFile();
                txtFileBtn.setText(txtSelectedFile.getName());
            }
            else if(result == JFileChooser.CANCEL_OPTION){
                txtSelectedFile = null;
            }});

        stopFileBtn = new JButton();
        stopFileBtn.setText("Select stop file");
        stopFileBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        stopFileBtn.addActionListener((ActionEvent ae) -> {
            stopFileChooser = new JFileChooser();
            stopFileChooser.setCurrentDirectory(workingDirectory);
            stopFileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
            int result = stopFileChooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                stopSelectedFile = stopFileChooser.getSelectedFile();
                stopFileBtn.setText(stopSelectedFile.getName());
            }
            else if(result == JFileChooser.CANCEL_OPTION){
                stopSelectedFile = null;
            }});

        startBtn = new JButton();
        startBtn.setText("Start");
        startBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        startBtn.addActionListener((ActionEvent ae) -> {
            if (stopSelectedFile == null && txtSelectedFile == null) {
                JOptionPane.showMessageDialog(null, "Select text and noise file before starting tag extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else if (stopSelectedFile == null) {
                JOptionPane.showMessageDialog(null, "Select noise file before starting tag extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else if (txtSelectedFile == null) {
                JOptionPane.showMessageDialog(null, "Select text file before starting tag extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else {
                txtArea.append("=========================================================\n       File Name: " + txtSelectedFile.getName() + "\n=========================================================\n");
                txtArea.append("\n");
                validWords();
                display();
            }});

        chooserPnl.add(txtFileBtn);
        chooserPnl.add(stopFileBtn);
        chooserPnl.add(startBtn);
    }
    private void createTxtAreaPnl(){
        txtAreaPnl = new JPanel();
        txtArea = new JTextArea(20, 55);
        txtArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        txtArea.setEditable(false);
        scroller = new JScrollPane(txtArea);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtAreaPnl.add(scroller);
    }
    private void createCtrlPnl() {
        ctrlPnl = new JPanel();
        ctrlPnl.setLayout(new GridLayout(3, 1));
        clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        clearBtn.addActionListener((ActionEvent ae) -> {
            txtArea.setText("");
            txtSelectedFile = null;
            txtFileBtn.setText("Select text file");
            stopSelectedFile = null;
            stopFileBtn.setText("Select tag file");
        });
        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        quitBtn.addActionListener((ActionEvent ae) -> System.exit(0));

        saveBtn = new JButton("Save");
        saveBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        saveBtn.addActionListener((ActionEvent ae) -> {
            if (txtArea.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "File is null. Please start tag extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else {
                saveTxtFile = new JFileChooser();
                saveTxtFile.setCurrentDirectory(workingDirectory);
                saveTxtFile.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
                int result = saveTxtFile.showSaveDialog(null);
                File file = saveTxtFile.getSelectedFile();
                BufferedWriter writer;
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        writer = new BufferedWriter(new FileWriter(file));
                        writer.write(txtArea.getText());
                        writer.close();
                        JOptionPane.showMessageDialog(null, "The file was saved successfully.", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "The file could not be saved.", "[ERROR]", JOptionPane.ERROR_MESSAGE);

                    }
                }
            }});

        ctrlPnl.add(saveBtn);
        ctrlPnl.add(clearBtn);
        ctrlPnl.add(quitBtn);
    }
    private void validWords(){
        Scanner stopWordSrc = null;
        try {
            stopWordSrc = new Scanner(stopSelectedFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        while(true){
            assert stopWordSrc != null;
            if (!stopWordSrc.hasNextLine()) break;
            String line = stopWordSrc.nextLine();
            stopWords.add(line.toLowerCase());
        }
        Scanner txtFileSrc = null;
        try {
            txtFileSrc = new Scanner(txtSelectedFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        while(true){
            assert txtFileSrc != null;
            if (!txtFileSrc.hasNextLine()) break;
            String line = txtFileSrc.nextLine().toLowerCase();
            words = line.split("[^a-zA-Z]+");
            for (String word : words){
                halt = false;
                for(String stop : stopWords){
                    if(word.equals(stop)){
                        halt = true;
                        break;
                    }
                }
                if(!halt){
                    if(!map.containsKey(word)){
                        map.put(word, 1);
                    }
                    else{
                        map.put(word, map.get(word) + 1);
                    }
                }
            }
        }
    }
    private void display(){
        for(String key: map.keySet()){
            if(key.length() > 2 ){
                txtArea.append(" Word \"" + key + "\"    detected " + map.get(key) + " times!\n");
            }
        }
    }
}
