package com.mycompany.prolab2_1;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.border.MatteBorder;
import java.awt.Cursor;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.Calendar;
import javax.swing.JMenuBar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import java.awt.Insets;
import java.awt.image.BufferedImage;

/**
 * İzmit Ulaşım Rota Planlama Sistemi
 */
public class Main extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(45, 52, 54);
    private static final Color SECONDARY_COLOR = new Color(0, 151, 230);
    private static final Color ACCENT_COLOR = new Color(255, 107, 107);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BUTTON_COLOR = new Color(52, 172, 224);
    private static final Color BUTTON_TEXT_COLOR = new Color(255, 255, 255);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private GraphBuilder graphBuilder;
    
    private JPanel contentPane;
    private JPanel headerPanel;
    private JPanel inputPanel;
    private JPanel resultPanel;
    
    private JPanel startOptionsPanel;
    private JRadioButton rbStartCoord;
    private JRadioButton rbStartStation;
    private JTextField startLatField;
    private JTextField startLonField;
    private JComboBox<String> startTypeCombo;
    private JComboBox<Stop> startStopCombo;
    
    private JPanel destOptionsPanel;
    private JRadioButton rbDestCoord;
    private JRadioButton rbDestStation;
    private JTextField destLatField;
    private JTextField destLonField;
    private JComboBox<String> destTypeCombo;
    private JComboBox<Stop> destStopCombo;
    
    private JComboBox<String> passengerTypeCombo;
    
    private JList<String> routeList;
    private DefaultListModel<String> routeListModel;
    private JTextArea routeDetailsArea;
    private RouteVisualizer routeVisualizer;
    
    private List<Route> currentRoutes;
    private List<Route> filteredRoutes = new ArrayList<>();
    
    private double routeStartLat = -1;
    private double routeStartLon = -1;
    private double routeDestLat = -1;
    private double routeDestLon = -1;
    
    private JMenuBar menuBar;
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            UIManager.put("Button.font", NORMAL_FONT);
            UIManager.put("Label.font", NORMAL_FONT);
            UIManager.put("ComboBox.font", NORMAL_FONT);
            UIManager.put("TextField.font", NORMAL_FONT);
            UIManager.put("RadioButton.font", NORMAL_FONT);
            UIManager.put("TextArea.font", new Font("Monospaced", Font.PLAIN, 12));
            UIManager.put("Panel.background", PANEL_COLOR);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextArea.background", Color.WHITE);
            
            System.out.println("UI Manager ayarlandı");
        } catch (Exception e) {
            System.err.println("UI ayarlama hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Ana uygulama başlatılıyor...");
                    Main mainFrame = new Main();
                    mainFrame.setVisible(true);
                    System.out.println("Ana uygulama görünür hale getirildi");
                } catch (Exception e) {
                    System.err.println("Ana uygulama başlatma hatası: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    public Main() {
        try {
            System.out.println("GraphBuilder başlatılıyor...");
            graphBuilder = new GraphBuilder();
            graphBuilder.loadFromJson("veriseti.json");
            System.out.println("GraphBuilder başarıyla başlatıldı, veri seti yüklendi");
            
            System.out.println("GUI bileşenleri oluşturuluyor...");
            createGUI();
            System.out.println("GUI bileşenleri oluşturuldu");
            
            checkHolidayTransportation();
        } catch (IOException e) {
            System.err.println("Veri seti yükleme hatası: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Veri seti yüklenirken bir hata oluştu: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (NullPointerException e) {
            System.err.println("Veri seti bulunamadı hatası: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Veri seti dosyası bulunamadı: veriseti.json",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Genel hata: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Uygulama başlatılırken bir hata oluştu: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void createGUI() {
        setTitle("İzmit Ulaşım Rota Planlama Sistemi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 850);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBackground(BACKGROUND_COLOR);
        setContentPane(contentPane);
        
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu dateMenu = new JMenu("Tarih");
        dateMenu.setForeground(ACCENT_COLOR);
        dateMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JMenuItem simulateDateItem = new JMenuItem("Tarih Simülasyonu");
        simulateDateItem.setIcon(createDateIcon());
        simulateDateItem.addActionListener(e -> showDateSimulationDialog());
        dateMenu.add(simulateDateItem);

        JMenuItem resetDateItem = new JMenuItem("Bugüne Dön");
        resetDateItem.addActionListener(e -> {
            HolidayManager.resetDateSimulation();
            updateRoutesDisplay();
            JOptionPane.showMessageDialog(this, "Tarih simülasyonu sıfırlandı. Gerçek tarih kullanılıyor.", 
                    "Simülasyon Sıfırlama", JOptionPane.INFORMATION_MESSAGE);
        });
        dateMenu.add(resetDateItem);
        
        menuBar.add(dateMenu);
        
        createHeaderPanel();
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        createInputPanel();
        contentPane.add(inputPanel, BorderLayout.WEST);
        
        createResultPanel();
        contentPane.add(resultPanel, BorderLayout.CENTER);
        
        JPanel footerPanel = createFooterPanel();
        contentPane.add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("İzmit Ulaşım Rota Planlama Sistemi");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setOpaque(false);
        
        JLabel dateLabel = createDateLabel();
        datePanel.add(dateLabel);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(datePanel, BorderLayout.EAST);
    }
    
    private JLabel createDateLabel() {
        Calendar cal = Calendar.getInstance();
        if (HolidayManager.getTestDate() != null) {
            cal.setTime(HolidayManager.getTestDate());
        }
        
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        
        String ayAdi = getMonthName(month);
        
        String dateText = String.format("<html><b>%d %s %d</b>", day, ayAdi, year);
        
        if (HolidayManager.isTodayFreeTransportationDay()) {
            dateText += "<br><font color='#FF6B6B'>⭐ Bayram Günü - Toplu Taşıma Ücretsiz!</font>";
        }
        dateText += "</html>";
        
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(NORMAL_FONT);
        dateLabel.setForeground(Color.WHITE);
        
        Timer timer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDateLabel(dateLabel);
            }
        });
        timer.start();
        
        return dateLabel;
    }

    private void updateDateLabel(JLabel dateLabel) {
        Calendar cal = Calendar.getInstance();
        if (HolidayManager.getTestDate() != null) {
            cal.setTime(HolidayManager.getTestDate());
        }
        
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        
        String ayAdi = getMonthName(month);
        
        String dateText = String.format("<html><b>%d %s %d</b>", day, ayAdi, year);
        
        if (HolidayManager.isTodayFreeTransportationDay()) {
            dateText += "<br><font color='#FF6B6B'>⭐ Bayram Günü - Toplu Taşıma Ücretsiz!</font>";
        }
        dateText += "</html>";
        
        dateLabel.setText(dateText);
    }

    private String getMonthName(int month) {
        switch(month) {
            case 1: return "Ocak";
            case 2: return "Şubat";
            case 3: return "Mart";
            case 4: return "Nisan";
            case 5: return "Mayıs";
            case 6: return "Haziran";
            case 7: return "Temmuz";
            case 8: return "Ağustos";
            case 9: return "Eylül";
            case 10: return "Ekim";
            case 11: return "Kasım";
            case 12: return "Aralık";
            default: return "";
        }
    }
    
    private void createInputPanel() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new CompoundBorder(
                new LineBorder(SECONDARY_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
            )
        ));
        inputPanel.setPreferredSize(new Dimension(350, 0));
        
        JLabel inputTitle = new JLabel("Rota Bilgileri");
        inputTitle.setFont(HEADER_FONT);
        inputTitle.setForeground(PRIMARY_COLOR);
        inputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(inputTitle);
        inputPanel.add(createSpacerPanel(15));
        
        JLabel lblStartOptions = new JLabel("Başlangıç Seçenekleri");
        lblStartOptions.setFont(HEADER_FONT);
        lblStartOptions.setForeground(SECONDARY_COLOR);
        lblStartOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(lblStartOptions);
        inputPanel.add(createSpacerPanel(5));
        
        startOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        startOptionsPanel.setBackground(PANEL_COLOR);
        startOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rbStartCoord = createStyledRadioButton("Koordinat Gir", true);
        rbStartStation = createStyledRadioButton("Durak Seç", false);
        
        ButtonGroup startGroup = new ButtonGroup();
        startGroup.add(rbStartCoord);
        startGroup.add(rbStartStation);
        
        startOptionsPanel.add(rbStartCoord);
        startOptionsPanel.add(rbStartStation);
        inputPanel.add(startOptionsPanel);
        inputPanel.add(createSpacerPanel(10));
        
        JPanel startCoordPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        startCoordPanel.setBackground(PANEL_COLOR);
        startCoordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblStartLat = new JLabel("Enlem:");
        lblStartLat.setFont(NORMAL_FONT);
        startCoordPanel.add(lblStartLat);
        
        startLatField = createStyledTextField("40.7760");
        startCoordPanel.add(startLatField);
        
        JLabel lblStartLon = new JLabel("Boylam:");
        lblStartLon.setFont(NORMAL_FONT);
        startCoordPanel.add(lblStartLon);
        
        startLonField = createStyledTextField("29.9400");
        startCoordPanel.add(startLonField);
        
        inputPanel.add(startCoordPanel);
        inputPanel.add(createSpacerPanel(10));
        
        JPanel startStopPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        startStopPanel.setBackground(PANEL_COLOR);
        startStopPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblStartType = new JLabel("Durak Tipi:");
        lblStartType.setFont(NORMAL_FONT);
        startStopPanel.add(lblStartType);
        
        startTypeCombo = createStyledComboBox(new String[]{"Otobüs", "Tramvay"});
        startStopPanel.add(startTypeCombo);
        
        JLabel lblStartStop = new JLabel("Durak:");
        lblStartStop.setFont(NORMAL_FONT);
        startStopPanel.add(lblStartStop);
        
        startStopCombo = new JComboBox<>();
        startStopCombo.setFont(NORMAL_FONT);
        updateStopCombo(startStopCombo, "bus");
        startStopPanel.add(startStopCombo);
        
        inputPanel.add(startStopPanel);
        inputPanel.add(createSpacerPanel(20));
        
        enableStartStopSelection(false);
        
        JLabel lblDestOptions = new JLabel("Hedef Seçenekleri");
        lblDestOptions.setFont(HEADER_FONT);
        lblDestOptions.setForeground(SECONDARY_COLOR);
        lblDestOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(lblDestOptions);
        inputPanel.add(createSpacerPanel(5));
        
        destOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        destOptionsPanel.setBackground(PANEL_COLOR);
        destOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rbDestCoord = createStyledRadioButton("Koordinat Gir", true);
        rbDestStation = createStyledRadioButton("Durak Seç", false);
        
        ButtonGroup destGroup = new ButtonGroup();
        destGroup.add(rbDestCoord);
        destGroup.add(rbDestStation);
        
        destOptionsPanel.add(rbDestCoord);
        destOptionsPanel.add(rbDestStation);
        inputPanel.add(destOptionsPanel);
        inputPanel.add(createSpacerPanel(10));
        
        JPanel destCoordPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        destCoordPanel.setBackground(PANEL_COLOR);
        destCoordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblDestLat = new JLabel("Enlem:");
        lblDestLat.setFont(NORMAL_FONT);
        destCoordPanel.add(lblDestLat);
        
        destLatField = createStyledTextField("40.7640");
        destCoordPanel.add(destLatField);
        
        JLabel lblDestLon = new JLabel("Boylam:");
        lblDestLon.setFont(NORMAL_FONT);
        destCoordPanel.add(lblDestLon);
        
        destLonField = createStyledTextField("29.9350");
        destCoordPanel.add(destLonField);
        
        inputPanel.add(destCoordPanel);
        inputPanel.add(createSpacerPanel(10));
        
        JPanel destStopPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        destStopPanel.setBackground(PANEL_COLOR);
        destStopPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblDestType = new JLabel("Durak Tipi:");
        lblDestType.setFont(NORMAL_FONT);
        destStopPanel.add(lblDestType);
        
        destTypeCombo = createStyledComboBox(new String[]{"Otobüs", "Tramvay"});
        destStopPanel.add(destTypeCombo);
        
        JLabel lblDestStop = new JLabel("Durak:");
        lblDestStop.setFont(NORMAL_FONT);
        destStopPanel.add(lblDestStop);
        
        destStopCombo = new JComboBox<>();
        destStopCombo.setFont(NORMAL_FONT);
        updateStopCombo(destStopCombo, "bus");
        destStopPanel.add(destStopCombo);
        
        inputPanel.add(destStopPanel);
        inputPanel.add(createSpacerPanel(20));
        
        enableDestStopSelection(false);
        
        JLabel lblPassengerType = new JLabel("Yolcu Tipi");
        lblPassengerType.setFont(HEADER_FONT);
        lblPassengerType.setForeground(SECONDARY_COLOR);
        lblPassengerType.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(lblPassengerType);
        inputPanel.add(createSpacerPanel(5));
        
        passengerTypeCombo = createStyledComboBox(new String[] {"Genel", "Öğrenci", "Yaşlı (65+)"});
        passengerTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel passengerPanel = new JPanel(new BorderLayout());
        passengerPanel.setBackground(PANEL_COLOR);
        passengerPanel.add(passengerTypeCombo, BorderLayout.CENTER);
        passengerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(passengerPanel);
        inputPanel.add(createSpacerPanel(25));
        
        JButton btnCalculate = createStyledButton("Rotaları Hesapla");
        btnCalculate.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(btnCalculate);

        JButton btnShowCoordinate = createStyledButton("Koordinatı Haritada Göster");
        btnShowCoordinate.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(btnShowCoordinate);

        btnShowCoordinate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCoordinateOnMap();
            }
        });
        
        rbStartCoord.addActionListener(e -> enableStartStopSelection(false));
        rbStartStation.addActionListener(e -> enableStartStopSelection(true));
        
        rbDestCoord.addActionListener(e -> enableDestStopSelection(false));
        rbDestStation.addActionListener(e -> enableDestStopSelection(true));
        
        startTypeCombo.addActionListener(e -> {
            String type = startTypeCombo.getSelectedIndex() == 0 ? "bus" : "tram";
            updateStopCombo(startStopCombo, type);
        });
        
        destTypeCombo.addActionListener(e -> {
            String type = destTypeCombo.getSelectedIndex() == 0 ? "bus" : "tram";
            updateStopCombo(destStopCombo, type);
        });
        
        btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateRoutes();
            }
        });
    }
    
    private void enableStartStopSelection(boolean enable) {
        startLatField.setEnabled(!enable);
        startLonField.setEnabled(!enable);
        startTypeCombo.setEnabled(enable);
        startStopCombo.setEnabled(enable);
    }
    
    private void enableDestStopSelection(boolean enable) {
        destLatField.setEnabled(!enable);
        destLonField.setEnabled(!enable);
        destTypeCombo.setEnabled(enable);
        destStopCombo.setEnabled(enable);
    }
    
    private void updateStopCombo(JComboBox<Stop> comboBox, String type) {
        comboBox.removeAllItems();
        
        for (Stop stop : graphBuilder.getStops().values()) {
            if (type.equals(stop.getType())) {
                comboBox.addItem(stop);
            }
        }
    }
    
    private void createResultPanel() {
        resultPanel = new JPanel();
        resultPanel.setBackground(PANEL_COLOR);
        resultPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 0, 10, 10),
            new CompoundBorder(
                new LineBorder(SECONDARY_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
            )
        ));
        resultPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel resultTitle = new JLabel("Rota Sonuçları");
        resultTitle.setFont(HEADER_FONT);
        resultTitle.setForeground(PRIMARY_COLOR);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PANEL_COLOR);
        titlePanel.add(resultTitle, BorderLayout.WEST);
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        resultPanel.add(titlePanel, BorderLayout.NORTH);
        
        routeListModel = new DefaultListModel<>();
        routeList = new JList<>(routeListModel);
        routeList.setCellRenderer(new RouteCellRenderer());
        routeList.setFont(NORMAL_FONT);
        routeList.setBackground(Color.WHITE);
        
        JScrollPane routeScrollPane = new JScrollPane(routeList);
        routeScrollPane.setPreferredSize(new Dimension(300, 500));
        routeScrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        
        routeDetailsArea = new JTextArea();
        routeDetailsArea.setEditable(false);
        routeDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        routeDetailsArea.setBackground(new Color(250, 250, 250));
        routeDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane detailsScrollPane = new JScrollPane(routeDetailsArea);
        detailsScrollPane.setPreferredSize(new Dimension(400, 300));
        detailsScrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        
        routeVisualizer = new RouteVisualizer();
        routeVisualizer.setAllStops(graphBuilder.getStops());
        
        JScrollPane visualizerScrollPane = new JScrollPane(routeVisualizer);
        visualizerScrollPane.setPreferredSize(new Dimension(400, 300));
        visualizerScrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                                 detailsScrollPane, 
                                                 visualizerScrollPane);
        rightSplitPane.setResizeWeight(0.4);
        rightSplitPane.setDividerSize(5);
        rightSplitPane.setBorder(null);
        
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                                routeScrollPane, 
                                                rightSplitPane);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setBorder(null);
        
        resultPanel.add(mainSplitPane, BorderLayout.CENTER);
        
        routeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = routeList.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < filteredRoutes.size()) {
                        Route selectedRoute = filteredRoutes.get(selectedIndex);
                        if (selectedRoute != null) {
                            double startLat = -1, startLon = -1, destLat = -1, destLon = -1;
                            try {
                                if (rbStartCoord.isSelected()) {
                                    startLat = Double.parseDouble(startLatField.getText().trim());
                                    startLon = Double.parseDouble(startLonField.getText().trim());
                                } else {
                                    Stop startStop = (Stop) startStopCombo.getSelectedItem();
                                    if (startStop != null) {
                                        startLat = startStop.getLat();
                                        startLon = startStop.getLon();
                                    }
                                }
                                
                                if (rbDestCoord.isSelected()) {
                                    destLat = Double.parseDouble(destLatField.getText().trim());
                                    destLon = Double.parseDouble(destLonField.getText().trim());
                                } else {
                                    Stop destStop = (Stop) destStopCombo.getSelectedItem();
                                    if (destStop != null) {
                                        destLat = destStop.getLat();
                                        destLon = destStop.getLon();
                                    }
                                }
                            } catch (NumberFormatException ex) {
                                System.err.println("Seçili rota için koordinatlar alınamadı: " + ex.getMessage());
                            }
                            
                            routeDetailsArea.setText(selectedRoute.getDetails());
                            routeDetailsArea.setCaretPosition(0);
                            routeVisualizer.setSelectedRoute(selectedRoute, startLat, startLon, destLat, destLon);
                            showAlternativeRoutes();
                        } else {
                            routeDetailsArea.setText("");
                            routeVisualizer.setSelectedRoute(null, -1, -1, -1, -1);
                        }
                    }
                }
            }
        });
    }
    
    private JPanel createSpacerPanel(int height) {
        JPanel spacer = new JPanel();
        spacer.setBackground(PANEL_COLOR);
        spacer.setPreferredSize(new Dimension(1, height));
        spacer.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
        spacer.setAlignmentX(Component.LEFT_ALIGNMENT);
        return spacer;
    }
    
    private JRadioButton createStyledRadioButton(String text, boolean selected) {
        JRadioButton button = new JRadioButton(text, selected);
        button.setFont(NORMAL_FONT);
        button.setBackground(PANEL_COLOR);
        button.setForeground(TEXT_COLOR);
        return button;
    }
    
    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(NORMAL_FONT);
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 7, 5, 7)
        ));
        return field;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(NORMAL_FONT);
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? BUTTON_COLOR.darker() : BUTTON_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        button.setFont(NORMAL_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }
    
    private void calculateRoutes() {
        try {
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            
            List<Route> routes;
            
            double startLat = 0, startLon = 0, destLat = 0, destLon = 0;
            String startStopId = null, destStopId = null;
            
            if (rbStartCoord.isSelected()) {
                startLat = Double.parseDouble(startLatField.getText().trim());
                startLon = Double.parseDouble(startLonField.getText().trim());
            } else {
                Stop selectedStop = (Stop) startStopCombo.getSelectedItem();
                if (selectedStop == null) {
                    JOptionPane.showMessageDialog(this, 
                            "Lütfen başlangıç durağı seçin.", 
                            "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    return;
                }
                startStopId = selectedStop.getId();
                startLat = selectedStop.getLat();
                startLon = selectedStop.getLon();
            }
            
            if (rbDestCoord.isSelected()) {
                destLat = Double.parseDouble(destLatField.getText().trim());
                destLon = Double.parseDouble(destLonField.getText().trim());
            } else {
                Stop selectedStop = (Stop) destStopCombo.getSelectedItem();
                if (selectedStop == null) {
                    JOptionPane.showMessageDialog(this, 
                            "Lütfen hedef durağı seçin.", 
                            "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    return;
                }
                destStopId = selectedStop.getId();
                destLat = selectedStop.getLat();
                destLon = selectedStop.getLon();
            }
            
            routeStartLat = startLat;
            routeStartLon = startLon;
            routeDestLat = destLat;
            routeDestLon = destLon;
            
            int passengerTypeIndex = passengerTypeCombo.getSelectedIndex();
            PassengerType passenger = new PassengerType(passengerTypeIndex);

            routes = graphBuilder.calculateRoutes(startLat, startLon, destLat, destLon, 
                                                        startStopId, destStopId);

            routeListModel.clear();
            filteredRoutes.clear();
            
            for (Route route : routes) {
                if (route != null) {
                    route.setPassengerType(passenger);
                    filteredRoutes.add(route);
                    routeListModel.addElement(route.getSummary());
                }
            }
            
            if (routeListModel.size() > 0) {
                routeList.setSelectedIndex(0);
                routeList.ensureIndexIsVisible(0);
            } else {
                routeDetailsArea.setText("Uygun rota bulunamadı.\nLütfen başka koordinatlar veya duraklar deneyiniz.");
                routeVisualizer.setSelectedRoute(null);
            }
            
            currentRoutes = routes;
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Lütfen geçerli koordinat değerleri girin.", 
                    "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                    "Rota hesaplanırken bir hata oluştu: " + e.getMessage(), 
                    "Hesaplama Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Rota hesaplanırken bir hata oluştu: " + e.getMessage(), 
                    "Hesaplama Hatası", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }
    
    private void showAlternativeRoutes() {
        StringBuilder alternatives = new StringBuilder("\n\n═════════════════════════════ ALTERNATIF ROTALAR ═════════════════════════════\n\n");
        
        if (filteredRoutes.size() > 1) {
            int selectedIndex = routeList.getSelectedIndex();
            
            for (int i = 0; i < filteredRoutes.size(); i++) {
                if (i == selectedIndex) continue;
                
                Route route = filteredRoutes.get(i);
                String routeType = route.getSummary();
                String routeDesc = "";
                
                switch (route.getRouteType()) {
                    case Route.ONLY_BUS:
                        routeDesc = "(Daha uygun maliyetli, ancak daha uzun sürebilir)";
                        break;
                    case Route.ONLY_TRAM:
                        routeDesc = "(Rahat ve dengeli bir ulaşım seçeneği)";
                        break;
                    case Route.BUS_TRAM:
                        routeDesc = "(Daha az durak, daha az bekleme süresi)";
                        break;
                    case Route.TAXI_BUS:
                        routeDesc = "(Karma ulaşım)";
                        break;
                    case Route.TAXI_TRAM:
                        routeDesc = "(Karma ulaşım)";
                        break;
                    case Route.ONLY_TAXI:
                        routeDesc = "(Daha hızlı, ancak maliyetli)";
                        break;
                    case Route.WALKING_ONLY:
                        routeDesc = "(Ücretsiz, sağlıklı)";
                        break;
                    default:
                        routeDesc = "";
                }
                
                double displayFare = route.getTotalFare();
                if (route.getPassengerType() != null) {
                    displayFare = route.getDiscountedTotalFare();
                }
                
                alternatives.append(String.format(" ⦿ %-20s %s\n   └─ %.1f km, %d dakika, %.1f TL", 
                    routeType, routeDesc, route.getTotalDistance(), 
                    route.getTotalDuration(), displayFare));
                
                if (route.getPassengerType() != null && 
                    route.getPassengerType().getType() != PassengerType.REGULAR) {
                    alternatives.append(String.format(" (%s - Normal: %.1f TL)", 
                        route.getPassengerType().getDiscountDescription(), 
                        route.getTotalFare()));
                }
                
                alternatives.append("\n\n");
            }
        } else {
            alternatives.append("   Alternatif rota bulunamadı.\n");
        }
        
        if (routeDetailsArea != null && routeList.getSelectedValue() != null) {
            String currentDetails = routeDetailsArea.getText();
            routeDetailsArea.setText(currentDetails + alternatives.toString());
        }
    }    
    
    private class RouteCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(10, 0));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            if (isSelected) {
                panel.setBackground(new Color(240, 248, 255));
                panel.setBorder(new CompoundBorder(
                    new MatteBorder(0, 4, 0, 0, SECONDARY_COLOR),
                    new EmptyBorder(10, 10, 10, 10)
                ));
            } else {
                panel.setBackground(Color.WHITE);
            }
            
            if (index >= 0 && index < filteredRoutes.size()) {
                Route route = filteredRoutes.get(index);
                
                String emoji = "";
                switch (route.getRouteType()) {
                    case Route.ONLY_BUS:
                        emoji = "🚌";
                        break;
                    case Route.ONLY_TRAM:
                        emoji = "🚋";
                        break;
                    case Route.BUS_TRAM:
                        emoji = "🚌 + 🚋";
                        break;
                    case Route.TAXI_BUS:
                        emoji = "🚕 + 🚌";
                        break;
                    case Route.TAXI_TRAM:
                        emoji = "🚕 + 🚋";
                        break;
                    case Route.ONLY_TAXI:
                        emoji = "🚕";
                        break;
                    case Route.WALKING_ONLY:
                        emoji = "🚶";
                        break;
                    default:
                        emoji = "";
                }
                
                JLabel label = new JLabel("<html><b>" + emoji + " " + route.getSummary() + "</b><br>" +
                    String.format("%.1f km | %d dk | %.1f TL", 
                    route.getTotalDistance(), route.getTotalDuration(), route.getDiscountedTotalFare()) + "</html>");
                label.setFont(NORMAL_FONT);
                panel.add(label, BorderLayout.CENTER);
            }
            
            return panel;
        }
    }
    
    private void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCoordinateOnMap() {
        try {
            double lat, lon;
            String label = "Seçilen Nokta";
            
            Object[] options = {"Başlangıç Koordinatı", "Hedef Koordinatı", "Özel Koordinat"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Hangi koordinatı haritada göstermek istiyorsunuz?",
                    "Koordinat Seçimi",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            
            if (choice == 0) {
                if (rbStartCoord.isSelected()) {
                    lat = Double.parseDouble(startLatField.getText().trim());
                    lon = Double.parseDouble(startLonField.getText().trim());
                    label = "Başlangıç Noktası";
                } else {
                    Stop selectedStop = (Stop) startStopCombo.getSelectedItem();
                    if (selectedStop == null) {
                        JOptionPane.showMessageDialog(this, 
                                "Lütfen bir başlangıç durağı seçin.", 
                                "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    lat = selectedStop.getLat();
                    lon = selectedStop.getLon();
                    label = "Başlangıç Durağı: " + selectedStop.getName();
                }
            } else if (choice == 1) {
                if (rbDestCoord.isSelected()) {
                    lat = Double.parseDouble(destLatField.getText().trim());
                    lon = Double.parseDouble(destLonField.getText().trim());
                    label = "Hedef Noktası";
                } else {
                    Stop selectedStop = (Stop) destStopCombo.getSelectedItem();
                    if (selectedStop == null) {
                        JOptionPane.showMessageDialog(this, 
                                "Lütfen bir hedef durağı seçin.", 
                                "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    lat = selectedStop.getLat();
                    lon = selectedStop.getLon();
                    label = "Hedef Durağı: " + selectedStop.getName();
                }
            } else if (choice == 2) {
                String latText = JOptionPane.showInputDialog(this, 
                        "Enlem (Latitude) değerini girin:", 
                        "Koordinat Girişi", 
                        JOptionPane.QUESTION_MESSAGE);
                
                if (latText == null) return;
                
                String lonText = JOptionPane.showInputDialog(this, 
                        "Boylam (Longitude) değerini girin:", 
                        "Koordinat Girişi", 
                        JOptionPane.QUESTION_MESSAGE);
                
                if (lonText == null) return;
                
                lat = Double.parseDouble(latText.trim());
                lon = Double.parseDouble(lonText.trim());
                label = "Özel Nokta";
            } else {
                return;
            }
            
            routeVisualizer.showCoordinate(lat, lon, label);
            
            JOptionPane.showMessageDialog(this, 
                    "Koordinat haritada işaretlendi: " + label + " (" + lat + ", " + lon + ")",
                    "Koordinat Gösteriliyor", 
                    JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Lütfen geçerli koordinat değerleri girin.", 
                    "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Koordinat gösterilirken bir hata oluştu: " + e.getMessage(), 
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkHolidayTransportation() {
        if (HolidayManager.isTodayFreeTransportationDay()) {
            showInfoDialog("Ücretsiz Toplu Taşıma", 
                    "Bugün bayram günü olduğu için toplu taşıma ücretsizdir! 🎉\n" + 
                    "Taksi ücretleri normal tarife üzerinden uygulanacaktır.");
        }
    }

    private void showDateSimulationDialog() {
        String[] aylar = {"1 - Ocak", "2 - Şubat", "3 - Mart", "4 - Nisan", "5 - Mayıs", "6 - Haziran", 
                          "7 - Temmuz", "8 - Ağustos", "9 - Eylül", "10 - Ekim", "11 - Kasım", "12 - Aralık"};
        JComboBox<String> ayCombo = new JComboBox<>(aylar);
        
        String[] gunler = new String[31];
        for (int i = 0; i < 31; i++) {
            gunler[i] = String.valueOf(i + 1);
        }
        JComboBox<String> gunCombo = new JComboBox<>(gunler);
        
        JTextField yearField = new JTextField(4);
        
        Calendar now = Calendar.getInstance();
        gunCombo.setSelectedIndex(now.get(Calendar.DAY_OF_MONTH) - 1);
        ayCombo.setSelectedIndex(now.get(Calendar.MONTH));
        yearField.setText(String.valueOf(now.get(Calendar.YEAR)));
        
        ayCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (index == 0 || index == 3 || index == 4 || index == 6 || index == 7 || index == 9) {
                    c.setForeground(Color.RED);
                    if (!isSelected) {
                        c.setBackground(new Color(255, 240, 240));
                    }
                }
                
                return c;
            }
        });
        
        JTextArea bilgiAlani = new JTextArea(
            "Ücretsiz ulaşım günleri:\n" +
            "• 1 Ocak - Yılbaşı\n" +
            "• 23 Nisan - Ulusal Egemenlik ve Çocuk Bayramı\n" +
            "• 1 Mayıs - Emek ve Dayanışma Günü\n" +
            "• 19 Mayıs - Atatürk'ü Anma, Gençlik ve Spor Bayramı\n" +
            "• 29 Mayıs - İstanbul'un Fethi\n" +
            "• 15 Temmuz - Demokrasi ve Milli Birlik Günü\n" +
            "• 30 Ağustos - Zafer Bayramı\n" +
            "• 6 Ekim - İstanbul'un Kurtuluşu\n" +
            "• 29 Ekim - Cumhuriyet Bayramı"
        );
        bilgiAlani.setEditable(false);
        bilgiAlani.setFont(new Font("SansSerif", Font.PLAIN, 11));
        bilgiAlani.setBackground(new Color(245, 245, 245));
        bilgiAlani.setBorder(new LineBorder(new Color(200, 200, 200)));
        
        ayCombo.addActionListener(e -> {
            int ay = ayCombo.getSelectedIndex() + 1;
            int gunSayisi;
            switch (ay) {
                case 2:
                    int yil = Integer.parseInt(yearField.getText());
                    boolean artikYil = (yil % 4 == 0 && yil % 100 != 0) || (yil % 400 == 0);
                    gunSayisi = artikYil ? 29 : 28;
                    break;
                case 4: case 6: case 9: case 11:
                    gunSayisi = 30;
                    break;
                default:
                    gunSayisi = 31;
                    break;
            }
            
            DefaultComboBoxModel<String> gunModel = new DefaultComboBoxModel<>();
            for (int i = 1; i <= gunSayisi; i++) {
                gunModel.addElement(String.valueOf(i));
            }
            gunCombo.setModel(gunModel);
            
            gunCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    
                    int gun = index + 1;
                    int selectedAy = ayCombo.getSelectedIndex() + 1;
                    
                    if ((gun == 1 && selectedAy == 1) ||
                        (gun == 23 && selectedAy == 4) ||
                        (gun == 1 && selectedAy == 5) ||
                        (gun == 19 && selectedAy == 5) ||
                        (gun == 29 && selectedAy == 5) ||
                        (gun == 15 && selectedAy == 7) ||
                        (gun == 30 && selectedAy == 8) ||
                        (gun == 6 && selectedAy == 10) ||
                        (gun == 29 && selectedAy == 10)) {
                        c.setForeground(Color.RED);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                        if (!isSelected) {
                            c.setBackground(new Color(255, 240, 240));
                        }
                    }
                    
                    return c;
                }
            });
        });
        
        yearField.addActionListener(e -> {
            if (ayCombo.getSelectedIndex() == 1) {
                ayCombo.actionPerformed(new ActionEvent(ayCombo, ActionEvent.ACTION_PERFORMED, "yearChanged"));
            }
        });
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel datePanel = new JPanel(new GridLayout(3, 2, 5, 5));
        datePanel.add(new JLabel("Gün:"));
        datePanel.add(gunCombo);
        datePanel.add(new JLabel("Ay:"));
        datePanel.add(ayCombo);
        datePanel.add(new JLabel("Yıl:"));
        datePanel.add(yearField);
        
        panel.add(datePanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(bilgiAlani), BorderLayout.CENTER);
        
        ayCombo.actionPerformed(new ActionEvent(ayCombo, ActionEvent.ACTION_PERFORMED, "init"));
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Tarih Simülasyonu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int day = Integer.parseInt(gunCombo.getSelectedItem().toString());
                int month = ayCombo.getSelectedIndex() + 1;
                int year = Integer.parseInt(yearField.getText());
                
                if (year < 2000) {
                    JOptionPane.showMessageDialog(this, "Geçersiz yıl bilgisi! Lütfen 2000 veya sonrası bir yıl girin.", 
                            "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                HolidayManager.simulateDate(day, month, year);
                
                if (HolidayManager.isTodayFreeTransportationDay()) {
                    JOptionPane.showMessageDialog(this, 
                            day + " " + aylar[month-1].substring(4) + " " + year + " tarihi bir bayram günü!\n" +
                            "Bu gün toplu taşıma ücretsizdir. Taksi ücretleri normal tarife üzerinden uygulanır.", 
                            "Bayram Günü", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                            day + " " + aylar[month-1].substring(4) + " " + year + " tarihi normal bir gün.", 
                            "Normal Gün", JOptionPane.INFORMATION_MESSAGE);
                }
                
                updateRoutesDisplay();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz sayı formatı!", 
                        "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateRoutesDisplay() {
        JOptionPane.showMessageDialog(this, 
                "Tarih değişikliği yapıldı. Yeni bir rota hesaplaması yapınız.", 
                "Bilgi", JOptionPane.INFORMATION_MESSAGE);
    }

    private ImageIcon createDateIcon() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.WHITE);
        g2.fillRect(1, 3, 14, 12);
        g2.setColor(Color.RED);
        g2.fillRect(1, 3, 14, 3);
        
        g2.setColor(Color.BLACK);
        g2.drawRect(1, 3, 14, 12);
        
        g2.drawLine(4, 1, 4, 3);
        g2.drawLine(12, 1, 12, 3);
        
        g2.dispose();
        return new ImageIcon(img);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        footerPanel.setLayout(new BorderLayout());
        
        JPanel holidayInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        holidayInfoPanel.setOpaque(false);
        
        JLabel holidayLabel = new JLabel();
        holidayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        if (HolidayManager.isTodayFreeTransportationDay()) {
            holidayLabel.setText("<html><b>ℹ️ Bugün toplu taşıma hizmetleri ücretsizdir.</b></html>");
            holidayLabel.setForeground(new Color(0, 128, 0));
        } else {
            String nextHoliday = findNextHoliday();
            if (nextHoliday != null && !nextHoliday.isEmpty()) {
                holidayLabel.setText("<html>ℹ️ Bir sonraki ücretsiz toplu taşıma günü: <b>" + nextHoliday + "</b></html>");
                holidayLabel.setForeground(TEXT_COLOR);
            }
        }
        
        holidayInfoPanel.add(holidayLabel);
        
        
        // Tarih simülasyonu düğmesi
        JButton dateSimButton = new JButton("Tarih Simülasyonu");
        dateSimButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateSimButton.setMargin(new Insets(2, 6, 2, 6));
        dateSimButton.addActionListener(e -> showDateSimulationDialog());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(dateSimButton);
        
        footerPanel.add(holidayInfoPanel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return footerPanel;
    }

    // Bir sonraki bayramı bul
    private String findNextHoliday() {
        Calendar now = Calendar.getInstance();
        if (HolidayManager.getTestDate() != null) {
            now.setTime(HolidayManager.getTestDate());
        }
        
        int currentDay = now.get(Calendar.DAY_OF_MONTH);
        int currentMonth = now.get(Calendar.MONTH) + 1;
        int currentYear = now.get(Calendar.YEAR);
        
        // Tüm bayramları kontrol et
        Object[][] holidays = {
            {1, 1, "1 Ocak - Yılbaşı"},
            {23, 4, "23 Nisan - Çocuk Bayramı"},
            {1, 5, "1 Mayıs - Emek ve Dayanışma"},
            {19, 5, "19 Mayıs - Gençlik ve Spor"},
            {29, 5, "29 Mayıs - İstanbul'un Fethi"},
            {15, 7, "15 Temmuz - Demokrasi"},
            {30, 8, "30 Ağustos - Zafer Bayramı"},
            {6, 10, "6 Ekim - İstanbul'un Kurtuluşu"},
            {29, 10, "29 Ekim - Cumhuriyet Bayramı"}
        };
        
        // Yıl içinde bir sonraki bayramı bul
        for (Object[] holiday : holidays) {
            int day = (int) holiday[0];
            int month = (int) holiday[1];
            String name = (String) holiday[2];
            
            if ((month > currentMonth) || 
                (month == currentMonth && day > currentDay)) {
                return name;
            }
        }
        
        // Eğer yıl içinde kalan bayram yoksa bir sonraki yıl 1 Ocak'ı göster
        return "1 Ocak " + (currentYear + 1) + " - Yılbaşı";
    }
}
