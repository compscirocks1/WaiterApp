import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static com.itextpdf.text.Font.FontFamily.TIMES_ROMAN;

public class McFred extends JFrame{
    private JPanel MainPanel;
    public JList itemList;
    private JPanel panelBottom;
    private JButton saveAsPDFButton;
    private JList orderList;
    private JSpinner quantitySpinner;
    private JButton addButton;
    private JTextArea chefsNotesTextArea;
    private JButton backButton;
    private JPanel panelTop;
    private McFred screenMcFred;
    private File fileItems = new File("fileItems");
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<String> order = new ArrayList<>();
    private DefaultListModel itemListModel;
    private DefaultListModel orderListModel;
    private double price;


    McFred(){
        super("your order");
        this.setContentPane(this.MainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screenMcFred = this;
        itemListModel = new DefaultListModel();
        itemList.setModel(itemListModel);
        orderListModel = new DefaultListModel();
        orderList.setModel(orderListModel);
        readFile();

        saveAsPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //creating a new pdf document
                Document doc = new Document();
                try {
                    //creating bold font for the title
                    Font titleFont = new Font(TIMES_ROMAN, 18, Font.BOLD | Font.UNDERLINE);
                    //creating bold font
                    Font boldFont = new Font(TIMES_ROMAN, 12, Font.BOLD);
                    //creating regular font for the body of the pdf containing the order
                    Font regularFont = new com.itextpdf.text.Font(TIMES_ROMAN, 12, Font.NORMAL);
                    //creating a directory where the orders are stored
                    File directory = new File("OrderFiles");
                    boolean success = true;
                    if (!directory.exists()) {
                        //creating a directory if it doesn't exist
                        success = directory.mkdir();
                    }
                    //if there is a directory or the directory has been created
                    if (success) {
                        //creating a pdf writer to write on the document
                        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("OrderFiles/Order_" + Objects.requireNonNull(directory.list()).length  +".pdf"));
                        doc.open();
                        //adds title with the boldUnderlinedFont
                        doc.add(new Paragraph("Order:", titleFont));
                        doc.add(new Paragraph("\n\n"));
                        for (String order: order) {
                            doc.add(new Paragraph("- "+order, regularFont));
                        }
                        doc.add(new Paragraph("\nChefs Notes:\n", boldFont));
                        doc.add(new Paragraph(chefsNotesTextArea.getText(), regularFont));
                        doc.add(new Paragraph("\nTotal Price = € "+ price , boldFont));

                        doc.close();
                        writer.close();

                    }
                } catch (DocumentException | FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Screen screen = new Screen();
                screen.setSize(screenMcFred.getSize());
                screen.setLocation(screenMcFred.getLocation());
                screen.setVisible(true);
                screenMcFred.setVisible(false);
            }
        });
        itemList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                quantitySpinner.setValue(1);
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = itemList.getSelectedIndex();
                Integer value = (Integer) quantitySpinner.getValue();
                if (selectedIndex >= 0 && value > 0) {
                    Item item = items.get(selectedIndex);
                    order.add(item.getName() + " x" + value);
                    price = price + (item.getPriceDouble() * value);

                }
                refreshLists();
            }
        });
    }

    public void refreshLists(){
        itemListModel.removeAllElements();
        for( Item p : items){
            itemListModel.addElement(p.getName());
        }
        orderListModel.removeAllElements();
        for (String order: order) {
            orderListModel.addElement(order);
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
                refreshLists();
                objectInputStream.close();
                fileInputStream.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
