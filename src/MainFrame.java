import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class MainFrame extends JFrame {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private Double[] coefficients;

    private JFileChooser fileChooser = null;        // Объект диалогового окна для выбора файлов

    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;
    private JMenuItem informationMenuItem;

    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;
    private Box hBoxResult;

    // Визуализатор ячеек таблицы
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();

    // Модель данных с результатами вычислений
    private GornerTableModel data;

    public MainFrame(Double[] coefficients) {

        super("Табулирование многочлена на отрезке по схеме Горнера");      //конструктор предка
        this.coefficients = coefficients;

        setSize(WIDTH, HEIGHT);     //размер окна
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);

        // Создать меню
        JMenuBar menuBar = new JMenuBar();
// Установить меню в качестве главного меню приложения
        setJMenuBar(menuBar);
// Добавить в меню пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
// Добавить его в главное меню
        menuBar.add(fileMenu);
// Создать пункт меню "Таблица"
        JMenu tableMenu = new JMenu("Таблица");
// Добавить его в главное меню
        menuBar.add(tableMenu);
        JMenu referenceMenu = new JMenu ("Справка");
        menuBar.add(referenceMenu);

// Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
// Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    saveToTextFile(fileChooser.getSelectedFile());
            }
        };

        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        saveToTextMenuItem.setEnabled(false);

        // Создать новое "действие" по сохранению в двоичный файл
        Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
// Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) ;
                saveToGraphicsFile(fileChooser.getSelectedFile());
            }
        };

        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
        saveToGraphicsMenuItem.setEnabled(false);

// Создать новое действие по поиску значений многочлена
        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
// Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска", "Поиск значения", JOptionPane.QUESTION_MESSAGE);
// Установить введенное значение в качестве иголки
                renderer.setNeedle(value);
// Обновить таблицу
                getContentPane().repaint();
            }
        };

        searchValueMenuItem = tableMenu.add(searchValueAction);
        searchValueMenuItem.setEnabled(false);

        Action informationAction = new AbstractAction("О программе") {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(MainFrame.this, "Пехота, 9 груупа");
            }
        };

        informationMenuItem = referenceMenu.add(informationAction);
        informationMenuItem.setEnabled(true);

        JLabel labelForFrom = new JLabel("X изменяется на интервале от:");
        textFieldFrom = new JTextField("0.0", 10);
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
        JLabel labelForTo = new JLabel("до:");
        textFieldTo = new JTextField("1.0", 10);
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
        JLabel labelForStep = new JLabel("с шагом:");
        textFieldStep = new JTextField("0.1", 10);
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());

        Box hboxRange = Box.createHorizontalBox();
        hboxRange.setBorder(BorderFactory.createBevelBorder(1));    //тип рамки
        hboxRange.add(Box.createHorizontalGlue());
        hboxRange.add(labelForFrom);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldFrom);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(labelForTo);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldTo);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(labelForStep);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldStep);
        hboxRange.add(Box.createHorizontalGlue());

        hboxRange.setPreferredSize(new Dimension(
                new Double(hboxRange.getMaximumSize().getWidth()).intValue(),
                new Double(hboxRange.getMinimumSize().getHeight()).intValue() * 2));    // Установить предпочтительный размер области
        getContentPane().add(hboxRange, BorderLayout.NORTH);

        JButton buttonCalc = new JButton("Вычислить");
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {

                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());

                    data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);
                    JTable table = new JTable(data);
// Установить в качестве визуализатора ячеек для класса Double разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
                    table.setRowHeight(30);     //размер строки
// Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.removeAll();
// Добавить в hBoxResult таблицу, "обѐрнутую" в панель с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
// Обновить область содержания главного окна
                    getContentPane().validate();
// Пометить ряд элементов меню как доступных
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JButton buttonReset = new JButton("Очистить поля");
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");

                hBoxResult.removeAll();
                hBoxResult.add(new JPanel());

                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);

                getContentPane().validate();
            }
        });

        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());

        hboxButtons.setPreferredSize(new Dimension(new
                Double(hboxButtons.getMaximumSize().getWidth()).intValue(), new
                Double(hboxButtons.getMinimumSize().getHeight()).intValue() * 2));

        getContentPane().add(hboxButtons, BorderLayout.SOUTH);

// Область для вывода результата пока что пустая
        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
// Установить контейнер hBoxResult в главной (центральной) области граничной компоновки
        getContentPane().add(hBoxResult, BorderLayout.CENTER);
    }

    protected void saveToGraphicsFile(File selectedFile) {
        try {
// Создать новый байтовый поток вывода, направленный в указанный файл
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
// Записать в поток вывода значения х и у
            for (int i = 0; i < data.getRowCount(); i++) {
                out.writeDouble((Double) data.getValueAt(i, 0));
                out.writeDouble((Double) data.getValueAt(i, 1));
            }
// Закрыть поток вывода
            out.close();
        } catch (Exception e) {
// Исключительную ситуацию "ФайлНеНайден" в данном случае можно не обрабатывать,
// так как мы файл создаѐм, а не открываем для чтения
        }
    }

    protected void saveToTextFile(File selectedFile) {
        try {
// Создать новый символьный поток вывода, направленный в указанный файл
            PrintStream out = new PrintStream(selectedFile);

// Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");

            for (int i = 0; i < coefficients.length; i++) {
                out.print(coefficients[i] + "*X^" + (coefficients.length - i - 1));
                if (i != coefficients.length - 1)
                    out.print(" + ");
            }

            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " + data.getTo() + " с шагом " + data.getStep());
            out.println("====================================================");

// Записать в поток вывода значения в точках
            for (int i = 0; i < data.getRowCount(); i++) {
                out.println("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1));
            }

// Закрыть поток
            out.close();
        } catch (FileNotFoundException e) {
// Исключительную ситуацию "ФайлНеНайден" можно не
// обрабатывать, так как мы файл создаѐм, а не открываем
        }
    }

}