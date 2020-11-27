import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GornerTableCellRenderer implements TableCellRenderer {

    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();

    private String needle = null;
    private DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance();

    public GornerTableCellRenderer() {

        formatter.setMaximumFractionDigits(5);      //5 знаков после запятой
        formatter.setGroupingUsed(false);       //не использовать группировку
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();       //. как знак разделения
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);

        panel.add(label);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        String formattedDouble = formatter.format(value);
        label.setText(formattedDouble);
        String[] val = formattedDouble.split("\\.");

        if (col==1 && needle!=null && needle.equals(formattedDouble)) {
            panel.setBackground(Color.RED);     //окрасить в красный
        } else  if (col == 0){
            int k = 0;
            if(val.length > 1) {
                k = val[1].length();
            }

            if(k<=3) {
                panel.setBackground(Color.ORANGE);
            } else {
                panel.setBackground(Color.WHITE);
            }
        } else if(col == 1 ) {
            int k = 0;
            if(val.length > 1) {
                k = val[1].length();
            }

            if(k<=3) {
                panel.setBackground(Color.ORANGE);
            } else {
                panel.setBackground(Color.WHITE);       //окрасить в белый
            }
        }
        return panel;
    }

    public void setNeedle(String needle) {
        this.needle = needle;
    }
}