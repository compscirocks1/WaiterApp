import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Screen extends JFrame  {
    public JPanel panelTop;
    private JPanel panelLeft;
    private JPanel panelRight;
    public JList itemList;
    private JTextField textName;
    private JTextField textPrice;
    private Screen screen;

    private JPanel panelMain;
    private JButton createOrderButton;
    private JPanel imagePanel;
    private JButton clearButton;
    private JButton selectImageButton;
    private JButton newButton;
    private JButton saveButton;
    public ArrayList<Item> items = new ArrayList<>();
    private DefaultListModel listPeopleModel;
    private File fileItems = new File("fileItems");

    Screen(){
        super("Today's Menu");
        this.setContentPane(this.panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        screen=this;
        panelTop.setMaximumSize(new Dimension(800,50));
        listPeopleModel = new DefaultListModel();
        itemList.setModel(listPeopleModel);
        saveButton.setEnabled(false);
        selectImageButton.setEnabled(false);
        readFile();

        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textName.getText().isEmpty()) {
                    Item p = new Item(
                            textName.getText(),
                            textPrice.getText());
                    if (imagePanel.getComponents().length != 0) {
                        p.setImageIcon((JLabel)imagePanel.getComponents()[0]);
                    }
                    items.add(p);
                    refreshPeopleList();
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int personNumber = itemList.getSelectedIndex();
                if (personNumber >= 0 && !textName.getText().equals("")){
                    Item p = items.get(personNumber);
                    p.setName(textName.getText());
                    p.setPrice(textPrice.getText());
                    //if there is an image in the imagePanel, it is added to p
                    if (imagePanel.getComponents().length != 0) {
                        p.setImageIcon((JLabel)imagePanel.getComponents()[0]);
                    }
                    refreshPeopleList();
                }
            }
        });
        itemList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int personNumber = itemList.getSelectedIndex();
                if (personNumber >= 0 ){
                    Item p = items.get(personNumber);
                    textName.setText(p.getName());
                    textPrice.setText(p.getPriceString());
                    //if there is an image in the imagePanel, it is added to p
                    addImageToScreen(p.getImageIcon());
                    saveButton.setEnabled(true);
                    selectImageButton.setEnabled(true);
                }
                else{
                    saveButton.setEnabled(false);
                    selectImageButton.setEnabled(false);
                }

            }
        });

        createOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                McFred mcFred = new McFred();
                mcFred.setSize(screen.getSize());
                mcFred.setLocation(screen.getLocation());
                mcFred.setVisible(true);
                screen.setVisible(false);
            }
        });

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create a file chooser
                JFileChooser fc = new JFileChooser(System.getProperty("user.home") + "/Downloads");
                fc.setDialogTitle("File Chooser");

                //centering the file chooser
                double screenX = screen.getX();
                double screenY = screen.getY();
                double screenWidth = screen.getWidth();
                double screenHeight = screen.getHeight();
                double screenCenterX = screenX + (screenWidth/2);
                double screenCenterY = screenY + (screenHeight/2);
                double fcWidth = fc.getWidth();
                double fcHeight = fc.getHeight();
                double fcX = screenCenterX - (fcWidth/2);
                double fcY = screenCenterY - (fcHeight/2);
                fc.setLocation((int)fcX, (int)fcY);

                //Setting filter for only image files that can be processed by swing such as .jpeg, .gif, and .png
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "JPEG, GIF, PNG","jpeg", "gif", "png");
                fc.setFileFilter(filter);

                //getting the value of the selected file and showing the file chooser
                int returnVal = fc.showOpenDialog(Screen.this);

                //if the return value from the open dialogue is approved, then the image can be created
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //getting the selected file
                    File file = fc.getSelectedFile();

                    try {
                        //passes in file with desired dimensions (dimensions of the imagePanel to be resized and added
                        // to screen
                        resizeAndAdd(file.getPath(), imagePanel.getWidth(),imagePanel.getHeight());
                    } catch (Exception exception) {
                        exception.printStackTrace();                    }
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textName.setText("");
                textPrice.setText("");
                clearImage();
                itemList.clearSelection();
            }
        });
    }

    public void clearImage () {
        if (imagePanel.getComponents().length != 0) {
            //removes all components from the imagePanel
            imagePanel.removeAll();
            //repaints imagePanel without any components as they have been removed
            imagePanel.repaint();
            imagePanel.revalidate();
        }
    }

    public void addImageToScreen(JLabel labelIcon) {
        clearImage();
        if (labelIcon != null) {
            imagePanel.add(labelIcon);
            //repaints imagePanel with the labelIcon
            imagePanel.repaint();
            imagePanel.revalidate();
        }
    }

    public void resizeAndAdd(String inputImagePath, int scaledWidth, int scaledHeight)
            throws IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = inputImagePath.substring(inputImagePath
                .lastIndexOf(".") + 1);

        // writes to output file
        File imageDirectory = new File("images");
        boolean success = true;
        if (!imageDirectory.exists()) {
            success = imageDirectory.mkdir();
        }
        //if there is a imageDirectory (meaning that success = true) creates an image and adds to screen
        if (success) {
            File resizedImageFile = new File("images/image" + Objects.requireNonNull(imageDirectory.list()).length  +".jpeg");
            ImageIO.write(outputImage, formatName, resizedImageFile);
            ImageIcon resizedImage = new ImageIcon(String.valueOf(resizedImageFile));
            JLabel label = new JLabel(resizedImage, SwingConstants.CENTER);
            addImageToScreen(label);
        }
    }

    public void refreshPeopleList(){
        listPeopleModel.removeAllElements();
        for( Item p : items){
            listPeopleModel.addElement(p.getName());
        }
        writeFile();
    }

    //updates file with the latest list of items
    public void writeFile (){
        try {
            if (!fileItems.exists()) {
                fileItems = new File("fileItems");
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileItems);
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);
            //adds ArrayList items into the file
            objectOutputStream.writeObject(items);
            objectOutputStream.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile () {
        try {
            if (fileItems.exists() && fileItems.length() != 0) {
                FileInputStream fileInputStream = new FileInputStream(fileItems);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                //reads ArrayList of items from file
                items = (ArrayList<Item>) objectInputStream.readObject();
                //refreshes list with the items from the file
                refreshPeopleList();
                objectInputStream.close();
                fileInputStream.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
