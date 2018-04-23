import javax.swing.table.AbstractTableModel;

public class Table
extends AbstractTableModel
implements ModelListener {
    private Canvas canvas;
    public static final int COLS = 4;
    public static final int X_COL = 0;
    public static final int Y_COL = 1;
    public static final int WIDTH_COL = 2;
    public static final int HEIGHT_COL = 3;

    public Table(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public int getRowCount() {
        return this.canvas.getShapeModelArray().size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DShapeModel model = this.canvas.getShapeModelArray().get(rowIndex);
        if (model instanceof DLineModel) {
            if (columnIndex == 0) {
                return "Start X " + ((DLineModel)model).getX();
            }
            if (columnIndex == 1) {
                return "Start Y " + ((DLineModel)model).getY();
            }
            if (columnIndex == 2) {
                return "End X " + ((DLineModel)model).getWidth();
            }
            if (columnIndex == 3) {
                return "End Y " + ((DLineModel)model).getHeight();
            }
        } else {
            if (columnIndex == 0) {
                return model.getX();
            }
            if (columnIndex == 1) {
                return model.getY();
            }
            if (columnIndex == 2) {
                return model.getWidth();
            }
            if (columnIndex == 3) {
                return model.getHeight();
            }
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "X";
        }
        if (column == 1) {
            return "Y";
        }
        if (column == 2) {
            return "WIDTH";
        }
        if (column == 3) {
            return "HEIGHT";
        }
        return null;
    }

    @Override
    public void modelChanged(DShapeModel model) {
        int i = 0;
        while (i < this.canvas.getShapeModelArray().size()) {
            if (model == this.canvas.getShapeModelArray().get(i)) {
                this.fireTableRowsUpdated(i, i);
            }
            ++i;
        }
    }
}