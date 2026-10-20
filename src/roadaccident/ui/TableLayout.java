package roadaccident.ui;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

final class TableLayout {

    private TableLayout() {
    }

    static void wrapCellsAndFitRows(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setDefaultRenderer(Object.class, new WrappedCellRenderer());

        Runnable adjustRows = () -> {
            for (int row = 0; row < table.getRowCount(); row++) {
                int rowHeight = table.getRowHeight();
                for (int column = 0; column < table.getColumnCount(); column++) {
                    TableCellRenderer renderer =
                            table.getCellRenderer(row, column);
                    Component component = table.prepareRenderer(
                            renderer,
                            row,
                            column
                    );
                    int width = Math.max(
                            1,
                            table.getColumnModel()
                                    .getColumn(column)
                                    .getWidth() - table.getIntercellSpacing().width
                    );
                    component.setSize(new Dimension(width, Short.MAX_VALUE));
                    rowHeight = Math.max(
                            rowHeight,
                            component.getPreferredSize().height
                                    + table.getIntercellSpacing().height
                    );
                }
                table.setRowHeight(row, rowHeight);
            }
        };

        Runnable queueAdjustment = () -> SwingUtilities.invokeLater(adjustRows);

        table.getModel().addTableModelListener(
                event -> queueAdjustment.run()
        );
        table.getColumnModel().addColumnModelListener(
                new javax.swing.event.TableColumnModelListener() {
                    @Override
                    public void columnAdded(
                            javax.swing.event.TableColumnModelEvent event) {
                        queueAdjustment.run();
                    }

                    @Override
                    public void columnRemoved(
                            javax.swing.event.TableColumnModelEvent event) {
                        queueAdjustment.run();
                    }

                    @Override
                    public void columnMoved(
                            javax.swing.event.TableColumnModelEvent event) {
                        queueAdjustment.run();
                    }

                    @Override
                    public void columnMarginChanged(
                            javax.swing.event.ChangeEvent event) {
                        queueAdjustment.run();
                    }

                    @Override
                    public void columnSelectionChanged(
                            javax.swing.event.ListSelectionEvent event) {
                    }
                }
        );
        table.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                queueAdjustment.run();
            }
        });

        queueAdjustment.run();
    }

    private static final class WrappedCellRenderer extends JTextArea
            implements TableCellRenderer {

        private WrappedCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 5, 3, 5));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {
            setText(value == null ? "" : value.toString());
            setFont(table.getFont());
            setForeground(selected
                    ? table.getSelectionForeground()
                    : table.getForeground());
            setBackground(selected
                    ? table.getSelectionBackground()
                    : table.getBackground());
            return this;
        }
    }
}
