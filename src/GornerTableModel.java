import javax.swing.*;
import javax.swing.table.AbstractTableModel;


public class GornerTableModel extends AbstractTableModel {

    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;

    public GornerTableModel(Double from, Double to, Double step, Double[] coefficients) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return new Double(Math.ceil((to - from) / step)).intValue() + 1;
    }

    public Object getValueAt(int row, int col) {

        double x = from + step * row;

        double result = 0.0;
        int n = coefficients.length - 1;
        for (int i = 0; i <= n; i++) {
            result += coefficients[i] * Math.pow(x, (n - i));
        }

        if (col == 0) {
            return x;
        } else if (col == 1) {
            return result;
        } else {
            int xIntegralPart = (int) x;
            int resultIntegralPart = (int) result;
            int k = 0;

           for (int i = 1; i <= xIntegralPart; i++) {
                if (xIntegralPart % i == 0) {
                    if (resultIntegralPart % i == 0) {
                        k++;
                    }
                }
            }

            if (k == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Значение X";
            case 1:
                return "Значение многочлена";
            default:
                return "Взаимно простые?";
        }
    }

    public Class<?> getColumnClass(int col) {
        if (col == 2) {
            return Boolean.class;
        } else {
            return Double.class;
        }
    }
}