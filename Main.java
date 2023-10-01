import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.sql.*;
import java.sql.Statement;
import java.util.*;
import java.util.List;

class Product {
    public int id;
    public String name;
    public String category;
    public int stocks;
    public int price;

    public Product(int id, String name, String category, int stocks, int price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stocks = stocks;
        this.price = price;
    }
}

class ShoppingCartItem {
    public int id;
    public int quantity;
    public String productName;
    public int price;

    public ShoppingCartItem(int id, int quantity, String productName, int price) {
        this.id = id;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }
}

class GroceryItem {
    public String productName;
    public int price;
    public int category;

    public GroceryItem(String productName, int price, int category) {
        this.productName = productName;
        this.price = price;
        this.category = category;
    }
}

public class Main {
    final private static Color 
        bgBlack = new Color(25, 25, 25),
        bgBtnBlack = new Color(50, 50, 50),
        bgBtnHoverBlack = new Color(60, 60, 60),
        bgBtnClickBlack = new Color(55, 55, 55),
        bgItemPanelBlack = new Color(65, 65, 65),
        bgScrollBarTrack = new Color(66, 66, 66),
        fgWhite = new Color(200, 200, 200),
        fgScrollBarThumb = new Color(115, 115, 115);

    final private static Font
        btnFont = new Font("Sans Serif", Font.BOLD, 18),
        titleFont = new Font("Sans Serif", Font.BOLD, 24),
        normalFont = new Font("Sans Serif", Font.PLAIN, 14);

    final private ImageIcon appIcon = new ImageIcon("Images/grocery.png");
    
    private static List<ShoppingCartItem> shoppingCartItems = new ArrayList<>();

    private static JFrame mainFrame, secondaryFrame, thirdFrame, fourthFrame;
    private static JTextArea taskOutput;
    private static CustomButton btnGenerateGroceryItems, btnBrowseCategories;
    private static JProgressBar taskProgress;
    private Task task;

    public Connection connect() {
        String url = "jdbc:sqlite:grocery-app.db";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return connection;
    }

    /**
     * Thanks to this answer from stackoverflow: https://stackoverflow.com/a/23958880
     */
    private static class CustomButton extends JButton {
        private CustomButton(String text) {
            super(text);

            setBorderPainted(false);
            setFocusPainted(false);

            setContentAreaFilled(false);
            setOpaque(true);

            setBackground(bgBtnBlack);
            setForeground(fgWhite);
            setFont(btnFont);
            setText(text);

            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(600, 50));

            addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (getModel().isPressed()) {
                        setBackground(bgBtnClickBlack);
                    } else if (getModel().isRollover()) {
                        setBackground(bgBtnHoverBlack);
                    } else {
                        setBackground(bgBtnBlack);
                    }
                }
            });
        }
    }

    class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            setProgress(0);

            Connection connection = connect();

            try {
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                statement.executeUpdate("DROP TABLE IF EXISTS grocery_items");

                String sql = "CREATE TABLE grocery_items (\n"
                    + " id INTEGER PRIMARY KEY,\n"
                    + " name TEXT NOT NULL,\n"
                    + " category INTEGER NOT NULL,\n"
                    + " stocks INTEGER NOT NULL,\n"
                    + " price INTEGER\n"
                    + ");";
                statement.executeUpdate(sql);

                List<GroceryItem> groceryItems = new ArrayList<>();

                groceryItems.add(new GroceryItem("Mangoes", 35, 0));
                groceryItems.add(new GroceryItem("Bananas", 30, 0));
                groceryItems.add(new GroceryItem("Pineapples", 30, 0));
                groceryItems.add(new GroceryItem("Papayas", 25, 0));
                groceryItems.add(new GroceryItem("Oranges", 25, 0));
                groceryItems.add(new GroceryItem("Apples", 20, 0));
                groceryItems.add(new GroceryItem("Grapes", 35, 0));
                groceryItems.add(new GroceryItem("Watermelons", 50, 0));
                groceryItems.add(new GroceryItem("Cantaloupe", 40, 0));
                groceryItems.add(new GroceryItem("Broccoli", 15, 1));
                groceryItems.add(new GroceryItem("Carrots", 20, 1));
                groceryItems.add(new GroceryItem("Cauliflower", 15, 1));
                groceryItems.add(new GroceryItem("Celery", 5, 1));
                groceryItems.add(new GroceryItem("Potatoes", 15, 1));
                groceryItems.add(new GroceryItem("Tomatoes", 15, 1));
                groceryItems.add(new GroceryItem("Chicken", 120, 2));
                groceryItems.add(new GroceryItem("Pork", 150, 2));
                groceryItems.add(new GroceryItem("Beef", 200, 2));
                groceryItems.add(new GroceryItem("Hotdogs", 30, 2));
                groceryItems.add(new GroceryItem("Bacon", 60, 2));
                groceryItems.add(new GroceryItem("Sausage", 40, 2));
                groceryItems.add(new GroceryItem("Fish", 120, 3));
                groceryItems.add(new GroceryItem("Shrimp", 150, 3));
                groceryItems.add(new GroceryItem("Squid", 150, 3));
                groceryItems.add(new GroceryItem("Crab", 135, 3));
                groceryItems.add(new GroceryItem("Tuna", 200, 3));
                groceryItems.add(new GroceryItem("Sardines", 135, 3));
                groceryItems.add(new GroceryItem("Milk", 25, 4));
                groceryItems.add(new GroceryItem("Cheese", 40, 4));
                groceryItems.add(new GroceryItem("Eggs (per tray)", 30, 4));
                groceryItems.add(new GroceryItem("Yogurt", 25, 4));
                groceryItems.add(new GroceryItem("Butter", 25, 4));
                groceryItems.add(new GroceryItem("Ice cream", 20, 4));
                groceryItems.add(new GroceryItem("Rice (sack)", 1250, 5));
                groceryItems.add(new GroceryItem("Bread", 50, 5));
                groceryItems.add(new GroceryItem("Flour", 50, 5));
                groceryItems.add(new GroceryItem("Sugar", 25, 5));
                groceryItems.add(new GroceryItem("Salt", 25, 5));
                groceryItems.add(new GroceryItem("Pepper", 10, 5));
                groceryItems.add(new GroceryItem("Garlic", 5, 5));
                groceryItems.add(new GroceryItem("Onions", 5, 5));
                groceryItems.add(new GroceryItem("Cooking oil", 10, 5));
                groceryItems.add(new GroceryItem("Soy sauce", 10, 5));
                groceryItems.add(new GroceryItem("Vinegar", 10, 5));
                groceryItems.add(new GroceryItem("Bagoong isda", 35, 5));
                groceryItems.add(new GroceryItem("Bagoong alamang", 35, 5));
                groceryItems.add(new GroceryItem("Patis", 15, 5));
                groceryItems.add(new GroceryItem("Sardines (canned)", 20, 5));
                groceryItems.add(new GroceryItem("Tuna (canned)", 20, 5));
                groceryItems.add(new GroceryItem("Corned beef", 20, 5));
                groceryItems.add(new GroceryItem("Fruits (canned)", 20, 5));
                groceryItems.add(new GroceryItem("Vegetables (canned)", 20, 5));
                groceryItems.add(new GroceryItem("Instant noodles", 15, 5));
                groceryItems.add(new GroceryItem("Pasta", 20, 5));
                groceryItems.add(new GroceryItem("Cereal", 20, 5));
                groceryItems.add(new GroceryItem("Chips", 15, 5));
                groceryItems.add(new GroceryItem("Cookies", 10, 5));
                groceryItems.add(new GroceryItem("Candy (pack)", 25, 5));
                groceryItems.add(new GroceryItem("Water", 15, 6));
                groceryItems.add(new GroceryItem("Juice", 15, 6));
                groceryItems.add(new GroceryItem("Soda", 15, 6));
                groceryItems.add(new GroceryItem("Coffee", 15, 6));
                groceryItems.add(new GroceryItem("Tea", 15, 6));
                groceryItems.add(new GroceryItem("Shampoo", 10, 7));
                groceryItems.add(new GroceryItem("Soap", 10, 7));
                groceryItems.add(new GroceryItem("Toothpaste", 10, 7));
                groceryItems.add(new GroceryItem("Deodorant", 10, 7));
                groceryItems.add(new GroceryItem("Pet food", 100, 7));
                groceryItems.add(new GroceryItem("Baby supplies", 50, 7));
                groceryItems.add(new GroceryItem("Puto", 15, 8));
                groceryItems.add(new GroceryItem("Bibingka", 25, 8));
                groceryItems.add(new GroceryItem("Kutsinta", 5, 8));
                groceryItems.add(new GroceryItem("Kalamay", 15, 8));
                groceryItems.add(new GroceryItem("Halo-halo", 15, 9));
                groceryItems.add(new GroceryItem("Leche flan", 25, 9));
                groceryItems.add(new GroceryItem("Turon", 15, 9));
                groceryItems.add(new GroceryItem("Taho", 10, 9));
            
                String pstr = "INSERT INTO grocery_items(name, category, stocks, price) VALUES (?, ?, ?, ?)";

                taskProgress.setMaximum(groceryItems.size());

                for (int i = 0; i < groceryItems.size(); i ++) {
                    int stocks = new Random().nextInt(200 - 10) + 10;
                    PreparedStatement pstmt = connection.prepareStatement(pstr);

                    pstmt.setString(1, groceryItems.get(i).productName);
                    pstmt.setInt(2, groceryItems.get(i).category);
                    pstmt.setInt(3, stocks);
                    pstmt.setInt(4, groceryItems.get(i).price);
                    pstmt.executeUpdate();
                    setProgress(i + 1);
                    taskOutput.setText(String.format("Added item %s, category %s, stocks %s, price $%s.\n%s", groceryItems.get(i).productName, getCategory(groceryItems.get(i).category), stocks, groceryItems.get(i).price, taskOutput.getText()));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }

            return null;
        }

        @Override
        public void done() {
            secondaryFrame.setCursor(null);
            taskOutput.setText(String.format("Done loading grocery items.\n%s", taskOutput.getText()));

            JOptionPane.showMessageDialog(secondaryFrame, "Done loading grocery items", "Tindahan ni Aling Nena", JOptionPane.INFORMATION_MESSAGE);
            secondaryFrame.dispose();
            mainFrame.setVisible(true);
        }
    }

    public String getCategory(int categoryId) {
        String category = "unknown";

        switch (categoryId) {
            case 0:
                category = "Fruits";
                break;

            case 1:
                category = "Vegetables";
                break;

            case 2:
                category = "Meats";
                break;

            case 3:
                category = "Seafoods";
                break;

            case 4:
                category = "Dairy and eggs";
                break;

            case 5:
                category = "Pantry items";
                break;

            case 6:
                category = "Drinks";
                break;

            case 7:
                category = "Others";
                break;

            case 8:
                category = "Rice cakes";
                break;

            case 9:
                category = "Sweets";
                break;
        }

        return category;
    }

    private void initializeSecondaryFrame() {
        mainFrame.dispose();

        /* ---------------------[ Secondary frame start ]--------------------- */
        secondaryFrame = new JFrame();
        secondaryFrame.setLayout(new BorderLayout());
        secondaryFrame.setTitle("Tindahan ni Aling Nena - Loading grocery items...");
        secondaryFrame.setSize(800, 600);
        secondaryFrame.setResizable(false);
        secondaryFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        secondaryFrame.setLocationRelativeTo(null);

        secondaryFrame.setIconImage(appIcon.getImage());

        /* ---------------------[ Main panel start ]--------------------- */
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgBlack);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* ---------------------[ Header start ]--------------------- */
        JLabel titleLabel = new JLabel("Loading grocery items...");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(fgWhite);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("This might take a while.");
        subtitleLabel.setFont(normalFont);
        subtitleLabel.setForeground(fgWhite);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        headerPanel.setBackground(bgBlack);
        headerPanel.setOpaque(false);
        /* ---------------------[ Header end ]--------------------- */
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        /* ---------------------[ Center start ]--------------------- */
        taskProgress = new JProgressBar(0, 78);
        taskProgress.setValue(0);
        taskProgress.setPreferredSize(new Dimension(560, 25));
        taskProgress.setStringPainted(true);
        taskProgress.setFont(normalFont);

        taskOutput = new JTextArea("Hi", 25, 50);
        taskOutput.setBackground(bgBtnBlack);
        taskOutput.setForeground(fgWhite);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);
        taskOutput.setFont(normalFont);

        JScrollPane taskScroll = new JScrollPane(taskOutput);
        taskScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        taskScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(bgBlack);
        centerPanel.setOpaque(false);
        centerPanel.add(taskProgress);
        centerPanel.add(taskScroll);
        /* ---------------------[ Center end ]--------------------- */

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        /* ---------------------[ Main panel end ]--------------------- */
        secondaryFrame.add(mainPanel);
        /* ---------------------[ Secondary frame end ]--------------------- */

        secondaryFrame.setVisible(true);
    }

    private void initializeThirdFrame() {
        if (secondaryFrame != null && secondaryFrame.isVisible()) {
            secondaryFrame.dispose();
        }
        if (mainFrame != null && mainFrame.isVisible()) {
            mainFrame.dispose();
        }

        /* ---------------------[ Third frame start ]--------------------- */
        thirdFrame = new JFrame();
        thirdFrame.setLayout(new BorderLayout());
        thirdFrame.setTitle("Tindahan ni Aling Nena - Categories");
        thirdFrame.setSize(800, 600);
        thirdFrame.setResizable(false);
        thirdFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        thirdFrame.setLocationRelativeTo(null);

        thirdFrame.setIconImage(appIcon.getImage());
        /* ---------------------[ Main panel start ]--------------------- */
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgBlack);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        /* ---------------------[ Header start ]--------------------- */
        JLabel titleLabel = new JLabel("Categories");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(fgWhite);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Choose the category below:");
        subtitleLabel.setFont(normalFont);
        subtitleLabel.setForeground(fgWhite);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        headerPanel.setBackground(bgBlack);
        headerPanel.setOpaque(false);
        /* ---------------------[ Header end ]--------------------- */

        /* ---------------------[ Center start ]--------------------- */
        JPanel centerPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        centerPanel.setBackground(bgBlack);
        centerPanel.setOpaque(false);

        for (int i = 0; i <= 9; i ++) {
            final int chosenCategory = i;
            CustomButton categoryButton = new CustomButton((chosenCategory == 9) ? "Back" : getCategory(i));
    
            categoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chosenCategory == 9) {
                        thirdFrame.dispose();
                        mainFrame.setVisible(true);
                    } else {
                        initializeItemsFrame(chosenCategory);
                    }
                }
            });

            centerPanel.add(categoryButton);
        }
        /* ---------------------[ Center end ]--------------------- */
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        /* ---------------------[ Main panel end ]--------------------- */
        thirdFrame.add(mainPanel);
        /* ---------------------[ Third frame end ]--------------------- */
        thirdFrame.setVisible(true);
    }

    private void initializeItemsFrame(int categoryId) {
        if (thirdFrame != null && thirdFrame.isVisible()) {
            thirdFrame.dispose();
        }
        if (secondaryFrame != null && secondaryFrame.isVisible()) {
            secondaryFrame.dispose();
        }
        if (mainFrame != null && mainFrame.isVisible()) {
            mainFrame.dispose();
        }

         /* ---------------------[ Fourth frame start ]--------------------- */
        fourthFrame = new JFrame();
        fourthFrame.setLayout(new BorderLayout());
        fourthFrame.setTitle(String.format("Tindahan ni Aling Nena - %s", getCategory(categoryId)));
        fourthFrame.setSize(800, 600);
        fourthFrame.setResizable(false);
        fourthFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fourthFrame.setLocationRelativeTo(null);

        fourthFrame.setIconImage(appIcon.getImage());

        /* ---------------------[ Main panel start ]--------------------- */
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgBlack);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* ---------------------[ Header start ]--------------------- */
        JLabel titleLabel = new JLabel(getCategory(categoryId));
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(fgWhite);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Choose the item(s) below:");
        subtitleLabel.setFont(normalFont);
        subtitleLabel.setForeground(fgWhite);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        headerPanel.setBackground(bgBlack);
        headerPanel.setOpaque(false);
        /* ---------------------[ Header end ]--------------------- */

        /* ---------------------[ Center start ]--------------------- */
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 3, 2, 2));
        centerPanel.setBackground(bgBlack);
        centerPanel.setOpaque(false);

        Connection connection = connect();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM grocery_items WHERE category = %d", categoryId));

            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                products.add(new Product(rs.getInt("id"), rs.getString("name"), getCategory(rs.getInt("category")), rs.getInt("stocks"), rs.getInt("price")));
            }

            for (int i = 0; i < products.size(); i ++) {
                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                itemPanel.setBackground(bgItemPanelBlack);
                itemPanel.setPreferredSize(new Dimension(200, 150));

                ImageIcon itemIcon = new ImageIcon(String.format("Images/products/%s.png", products.get(i).name));
                itemIcon = new ImageIcon(itemIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));
                JLabel itemName = new JLabel(products.get(i).name);
                itemName.setFont(btnFont);
                itemName.setForeground(fgWhite);
                itemName.setIcon(itemIcon);

                JLabel itemPriceAndStocks = new JLabel(String.format("<html>Price: $%d<br />Stocks: %s</html>", products.get(i).price, (products.get(i).stocks > 0) ? Integer.toString(products.get(i).stocks) : "out of stock"));
                itemPriceAndStocks.setFont(normalFont);
                itemPriceAndStocks.setForeground(fgWhite);

                CustomButton itemBuy = new CustomButton("Add to cart");
                
                final int chosenItem = i;
                itemBuy.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String value = JOptionPane.showInputDialog(null, String.format("Please enter the quantity below: (Make sure it's lower than %d)", products.get(chosenItem).stocks), String.format("Tindahan ni Aling Nena - Add %s to cart", products.get(chosenItem).name), JOptionPane.QUESTION_MESSAGE);
                        
                        if (value != null) {
                            int finalValue = Integer.parseInt(value);

                            if (products.get(chosenItem).stocks - finalValue < 0) {
                                JOptionPane.showConfirmDialog(null, String.format("Item %s's stock is not enough to your entered quantity, want to continue? (This will override your selected quantity to available stock)", products.get(chosenItem).name), String.format("Tindahan ni Aling Nena - Add %s to cart", products.get(chosenItem).name), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            
                                finalValue = products.get(chosenItem).stocks;
                            }

                            JOptionPane.showConfirmDialog(null, String.format("Want to add item %s with %d quantity to your shopping cart? It will add $%d to your total price.", products.get(chosenItem).name, finalValue, products.get(chosenItem).price * finalValue), String.format("Tindahan ni Aling Nena - Add %s to cart", products.get(chosenItem).name), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            shoppingCartItems.add(new ShoppingCartItem(products.get(chosenItem).id, finalValue, products.get(chosenItem).name, products.get(chosenItem).price));
                        }
                    }
                });

                itemPanel.add(itemName, BorderLayout.NORTH);
                itemPanel.add(itemPriceAndStocks, BorderLayout.CENTER);
                itemPanel.add(itemBuy, BorderLayout.SOUTH);

                centerPanel.add(itemPanel);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        ScrollBarUI myUI = new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = fgScrollBarThumb;
                this.trackColor = bgScrollBarTrack;
                this.scrollBarWidth = 15;
            }
        };
        
        JScrollPane itemsScroll = new JScrollPane(centerPanel);
        itemsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        itemsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemsScroll.getVerticalScrollBar().setUnitIncrement(8);
        itemsScroll.getHorizontalScrollBar().setUnitIncrement(8);
        itemsScroll.getViewport().setBackground(bgBlack);
        itemsScroll.getVerticalScrollBar().setUI(myUI);

        for (Component component : itemsScroll.getVerticalScrollBar().getComponents()) {
            itemsScroll.getVerticalScrollBar().remove(component);
        }
        itemsScroll.setBorder(BorderFactory.createEmptyBorder());
        /* ---------------------[ Center end ]--------------------- */

        /* ---------------------[ Footer start ]--------------------- */
        CustomButton backButton = new CustomButton("Back to Categories");
        CustomButton viewCartButton = new CustomButton("View shopping cart");

        backButton.setPreferredSize(new Dimension(300, 50));
        viewCartButton.setPreferredSize(new Dimension(300, 50));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fourthFrame.dispose();
                initializeThirdFrame();
                thirdFrame.setVisible(true);
            }
        });

        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ShoppingCartItem shoppingCartItem : shoppingCartItems) {
                    System.out.println(String.format("%d - %s - %d - %d", shoppingCartItem.id, shoppingCartItem.productName, shoppingCartItem.quantity, shoppingCartItem.price));
                }
            }
        });

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        footerPanel.setBackground(bgBlack);
        footerPanel.setOpaque(false);
        footerPanel.add(backButton, BorderLayout.WEST);
        footerPanel.add(viewCartButton, BorderLayout.EAST);
        /* ---------------------[ Footer end ]--------------------- */
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(itemsScroll, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        /* ---------------------[ Main panel end ]--------------------- */
        fourthFrame.add(mainPanel);
        /* ---------------------[ Fourth frame end ]--------------------- */
        
        fourthFrame.setVisible(true);
    }

    private void initialize() {
        /* ---------------------[ Main frame start ]--------------------- */
        mainFrame = new JFrame();
        mainFrame.setTitle("Tindahan ni Aling Nena");
        mainFrame.setSize(800, 600);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        mainFrame.setIconImage(appIcon.getImage());
        mainFrame.setVisible(true);
        /* ---------------------[ Main panel start ]--------------------- */
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgBlack);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        /* ---------------------[ Header start ]--------------------- */
        JLabel titleLabel = new JLabel("Welcome to Tindahan ni Aling Nena!");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(fgWhite);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Made with ♥ by Marlon & Jericho.");
        subtitleLabel.setFont(normalFont);
        subtitleLabel.setForeground(fgWhite);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(null);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        headerPanel.setOpaque(false);
        /* ---------------------[ Header end ]--------------------- */

        /* ---------------------[ Center start ]--------------------- */
        JLabel messageLabel = new JLabel("Choose the action below:");
        messageLabel.setFont(normalFont);
        messageLabel.setForeground(fgWhite);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        btnGenerateGroceryItems = new CustomButton("Generate Grocery Items (re-stock)");
        btnBrowseCategories = new CustomButton("Browse Categories");

        btnGenerateGroceryItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeSecondaryFrame();
                taskOutput.setText("");
                secondaryFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                task = new Task();
                task.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("progress" == evt.getPropertyName()) {
                            int progress = (Integer) evt.getNewValue();
                            taskProgress.setValue(progress);
                        }
                    }
                });
                task.execute();
            }
        });

        btnBrowseCategories.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeThirdFrame();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);

        buttonsPanel.setBackground(bgBlack);
        buttonsPanel.add(messageLabel, gbc);
        buttonsPanel.add(btnGenerateGroceryItems, gbc);
        buttonsPanel.add(btnBrowseCategories, gbc);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(bgBlack);
        centerPanel.add(buttonsPanel, gbc);
        
        /* ---------------------[ Center end ]--------------------- */
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        /* ---------------------[ Main panel end ]--------------------- */
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        /* ---------------------[ Main frame end ]--------------------- */
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.initialize();
    }
}
