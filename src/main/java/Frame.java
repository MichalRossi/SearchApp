import com.foundationdb.tuple.ByteArrayUtil;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Frame extends JFrame {
    private JPanel mainPanel;
    private JPanel secondaryPanel;

    private JPanel directory;
    private JPanel file;
    private JPanel bytes;

    private JTextField nazwaKataloguField;
    private JTextField nazwaPlikuField;
    private JTextField inputBytes;
    private JTextField outputBytes;


    private JList listaKatalogow;
    private JList listaPlikow;

    private DefaultListModel modelListaKatalogow = new DefaultListModel();
    private DefaultListModel modelListaPlikow = new DefaultListModel();

    private JLabel nazwaKataloguLabel;
    private JLabel rozszerzeniePlikuLabel;
    private JLabel wymienianyCiągBajtówLabel;
    private JLabel wprowadzonyCiągBajtówLabel;
    private JLabel inputByteError;
    private JLabel outputByteError;
    private JLabel brakKataloguLabel;
    private JLabel brakPlikuLabel;

    private JButton szukajKataloguButton;
    private JButton szukajPlikuButton;
    private JButton wykonajButton;
    private JButton restartButton;

    private JScrollPane listaKatalogowScroll;
    private JScrollPane listaPlikowScrollPane;
    private JButton pomocButton;


    public Frame() {
        super("Recruitment task");

        add(mainPanel);

        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        nazwaKataloguField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (nazwaKataloguField.getText().isEmpty()){
                    szukajKataloguButton.setEnabled(false);
                }
                else {
                    szukajKataloguButton.setEnabled(true);
                }
            }
        });


        nazwaPlikuField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (nazwaPlikuField.getText().isEmpty()){
                    szukajPlikuButton.setEnabled(false);
                }
                else {
                    szukajPlikuButton.setEnabled(true);
                }
            }
        });


        inputBytes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (inputBytes.getText().isEmpty()){
                    wykonajButton.setEnabled(false);
                }
                else if (inputBytes.getText().isEmpty() && !modelListaKatalogow.isEmpty() && !modelListaPlikow.isEmpty()){
                    inputByteError.setVisible(true);
                }
                else {
                    wykonajButton.setEnabled(true);
                    inputByteError.setVisible(false);
                }
            }
        });


        outputBytes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (outputBytes.getText().isEmpty()){
                    wykonajButton.setEnabled(false);
                }
                else if (outputBytes.getText().isEmpty() && !modelListaKatalogow.isEmpty() && !modelListaPlikow.isEmpty()){
                    outputByteError.setVisible(true);
                }
                else {
                    wykonajButton.setEnabled(true);
                    outputByteError.setVisible(false);

                }
            }
        });


        szukajKataloguButton.setEnabled(false);
        szukajKataloguButton.addActionListener(e -> {
            modelListaKatalogow.removeAllElements();
            modelListaPlikow.removeAllElements();
            inputBytes.setText("");
            outputBytes.setText("");
            nazwaPlikuField.setText("");
            brakKataloguLabel.setVisible(false);

            String folderName = nazwaKataloguField.getText();
            Path[] rootDirectories = Controller.getRoots();

            try {
                List<Path> folders = new ArrayList<>();

                for(Path directory : rootDirectories){
                    folders.addAll(Controller.search(folderName,directory));
                }

                if(folders.size()==0){
                    brakKataloguLabel.setVisible(true);
                }

                modelListaKatalogow.addAll(folders);
                listaKatalogow.setModel(modelListaKatalogow);
                listaKatalogow.setSelectedIndex(0);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Niepoprawny katalog główny");
            }
        });


        szukajPlikuButton.setEnabled(false);
        szukajPlikuButton.addActionListener(e -> {
            modelListaPlikow.removeAllElements();
            inputBytes.setText("");
            outputBytes.setText("");
            brakPlikuLabel.setVisible(false);

            Path folder = (Path)listaKatalogow.getSelectedValue();
            String extension = nazwaPlikuField.getText();
            try {
                List<File> inputFiles = Controller.searchByExtension(extension,folder).stream()
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                if(inputFiles.size()==0){
                    brakPlikuLabel.setVisible(true);
                }

                modelListaPlikow.addAll(inputFiles);
                listaPlikow.setModel(modelListaPlikow);
                listaPlikow.setSelectedIndex(0);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Niepoprawny folder!");
            }
        });


        wykonajButton.addActionListener(e -> {
            inputByteError.setVisible(false);
            outputByteError.setVisible(false);

            File inputFile = (File)listaPlikow.getSelectedValue();

            try {
                byte[] sourceBytes = FileUtils.readFileToByteArray(inputFile);
                byte[] input = this.inputBytes.getText().getBytes();
                byte[] output =  outputBytes.getText().getBytes();
                byte[] outputBytes = ByteArrayUtil.replace(sourceBytes, input, output);

                if(! FileUtils.readFileToString(inputFile).contains(this.inputBytes.getText()) ){
                    JOptionPane.showMessageDialog(this,"Brak wprowadzonego ciągu bajtów w pliku!");
                }
                else {
                    FileUtils.writeByteArrayToFile(inputFile, outputBytes, false);
                    JOptionPane.showMessageDialog(this, "Zamieniony ciągi bajtów!");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Niepoprawny plik!");
            }
        });

        pomocButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Program ma na celu wyszukanie w podanym katalogu pliku o\n" +
                    "przyjętym rozszerzeniu i podmianie zadanego ciągu bajtów \n" +
                    "na inny.\n" +
                    "Zasady działania programu:\n" +
                    "1. W pierwszej kolejności należy podać nazwę katalogu,\n" +
                    "który będzie przeszukiwany i wcisnąć przycisk \"Szukaj \n" +
                    "katalogu\".\n" +
                    "2. Następnie należy wybrać katalog z listy, podać\n" +
                    "rozszerzenie pliku, w którym zostanie wykonana podmiana\n" +
                    "i nacisnąć przycisk \"Szukaj pliku\".\n" +
                    "3. W kolejnym kroku należy wybrać z plik z listy i wprowadzić\n" +
                    "wymieniany oraz wprowadzony ciąg bajtów. \n" +
                    "4. Aby przeprowadzić operację wymiany bajtów należy wcisnąć\n" +
                    "przycisk \"Wykonaj\".\n" +
                    "\n" +
                    "Przycisk \"Restart\" powoduje reset programu.\n"
        );
        });


        restartButton.addActionListener(e->{
            brakKataloguLabel.setVisible(false);
            brakPlikuLabel.setVisible(false);
            inputByteError.setVisible(false);
            outputByteError.setVisible(false);

            nazwaKataloguField.setText("");
            nazwaPlikuField.setText("");
            inputBytes.setText("");
            outputBytes.setText("");
            listaKatalogow.removeAll();
            listaPlikow.removeAll();
            modelListaKatalogow.removeAllElements();
            modelListaPlikow.removeAllElements();
        });
    }

}
