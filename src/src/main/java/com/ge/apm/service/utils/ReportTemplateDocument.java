package com.ge.apm.service.utils;

import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.domain.pure.TableSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * ReportTemplateDocument represent Excel Template that generated from JSON object.
 */
public class ReportTemplateDocument {
    private static final String SEMICOLON = ";";
    private static final String DEFAULT_AUTHOR = "GEAPM";
    private static final int ALPHABET_SIZE = 'Z' - 'A' + 1;

    private static final String CELL_VALUE_FORMAT = "${X.%s}";
    private static final String CELL_RESULT_FORMAT = "$save{X.%s}";
    private static final String DROPDOWN_WIDGET_FORMAT = "$widget.dropdown{itemLabels=\"%s\" itemValues=\"%s\" }";
    private static final String CALENDAR_WIDGET_FORMAT = "$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}";
    private static final String FLOAT_WIDGET_FORMAT = "$widget.inputnumber{decimalPlaces=\"2\"}";
    private static final String SWITCH_VALUES = "是;否";

    private TableSchema content;
    private Workbook workbook;
    private CreationHelper factory;
    private Sheet sheet;

    // unlocked style
    private CellStyle unlockedStyle;

    private Map<String, CellPos> cellMap;

    public ReportTemplateDocument(Workbook workbook, TableSchema content, String sheetName) {
        this.content = content;
        this.workbook = workbook;
        this.factory = workbook.getCreationHelper();
        this.sheet = workbook.createSheet(sheetName);

        this.unlockedStyle = workbook.createCellStyle();
        unlockedStyle.setLocked(false);

        this.cellMap = new HashMap<>();
    }

    public void write(OutputStream out) throws IOException {
        int row = 0;

        writeTitle(row++);

        for (TableGroup group : content.getGroups()) {
            row = writeGroup(row, group);
        }
        // moved post process here to avoid early-obtained backref.
        postProcess();

        workbook.write(out);
    }

    private void postProcess() {
        for (TableGroup group : content.getGroups()) {
            for (TableItem item : group.getItems()) {
                if (!needPostProcess(item)) continue;
                CellPos pos = cellMap.get(item.getKey());
                Cell cell = getCell(pos.getRow(), pos.getCol());
                String resOutput = null;
                switch (item.getExp().get("rule")) {
                    case "RangeRule":
                        resOutput = showRangeRuleResult(cell, item);
                        writeCellData(cell, resOutput);
                        break;
                    case "ScopeRule":
                        resOutput = showScopeRuleResult(cell, item);
                        writeCellData(cell, resOutput);
                        break;
                    case "DeltaRule":
                        resOutput = showDeltaRuleResult(cell, item);
                        writeCellData(cell, resOutput);
                        break;
                    case "VerifyRule":
                        resOutput = showVerifyRuleResult(cell, item);
                        writeCellData(cell, resOutput);
                        break;
                    default:
                        break;
                }

                if (!StringUtils.isEmpty(resOutput)) {
                    writeCellComment(cell, resultWrapper(item.getResKey()));
                }
            }
        }
    }

    /**
     * Convert a Column to Excel Column Title.
     *
     * @param n column in int
     * @return Column Title in String
     */
    private String convertToTitle(int n) {
        StringBuilder buf = new StringBuilder();

        while (n > 0) {
            char c = (char) ('A' + (n - 1) % ALPHABET_SIZE);
            buf.append(c);
            n = (n - 1) / ALPHABET_SIZE;
        }

        return buf.reverse().toString();
    }

    @SuppressWarnings("UnusedAssignment")
    private void writeTitle(int row) {
        int col = 0;
        Cell cell = getCell(row, col++);
        writeCellData(cell, content.getTitle());
    }

    private int writeGroup(int row, TableGroup group) {
        int col = 0;
        Cell cell = getCell(row++, col);
        writeCellData(cell, group.getTitle());
        for (TableItem item : group.getItems()) {
            writeItem(row++, item);
        }

        return row;
    }

    @SuppressWarnings("UnusedAssignment")
    private void writeItem(int row, TableItem item) {
        int col = 1;
        // 1. write name
        Cell cell = getCell(row, col++);
        writeCellData(cell, item.getLabel());
        // 2. write input field
        cell = getCell(row, col++);
        writeCellData(cell, keyWrapper(item.getKey()));
        // add comment for input field
        switch (item.getComponent()) {
            case Select:
                renderSelectCell(cell, item.getOptions());
                break;
            case Switch:
                renderSwitchCell(cell);
                break;
            case Text:
            case TextArea:
                break;
            case Number:
                renderNumberCell(cell);
                break;
            case Date:
                renderDateCell(cell);
                break;
            default:
                break;
        }
        cell.setCellStyle(unlockedStyle);
        // link input field
        cellMap.put(item.getKey(), new CellPos(row, col));
    }

    private String showScopeRuleResult(Cell cell, TableItem item) {
        boolean hasMin = item.getExp().keySet().contains("min");
        boolean hasMax = item.getExp().keySet().contains("max");
        if (!item.getExp().keySet().contains("norm")) return null;
        if (!hasMax && !hasMin) return null;

        Double norm = toDouble(item.getExp().get("norm"));
        String loc = toCellLocation(cell.getColumnIndex(), cell.getRowIndex() + 1);
        List<String> conditions = new ArrayList<>();
        Double min = null;
        if (hasMin) {
            // ABS(loc - norm) <GT_OP> min
            min = Optional.ofNullable(toDouble(item.getExp().get("min"))).orElse(0.0);
            String exp = Operator.MINUS.apply(loc, String.valueOf(norm));
            Operator minCompareOp = Operator.fromString(item.getExp().get("minCompare"));
            if (minCompareOp == null) minCompareOp = Operator.GREATER_THAN;
            conditions.add(minCompareOp.apply(abs(exp), String.valueOf(min)));
        }
        Double max = null;
        if (hasMax) {
            // ABS(loc - norm) <LT_OP> max
            max = Optional.ofNullable(toDouble(item.getExp().get("max"))).orElse(0.0);
            String exp = Operator.MINUS.apply(loc, String.valueOf(norm));
            Operator maxCompareOp = Operator.fromString(item.getExp().get("maxCompare"));
            if (maxCompareOp == null) maxCompareOp = Operator.LESS_THAN;
            conditions.add(maxCompareOp.apply(abs(exp), String.valueOf(max)));
        }
        String condition = conditions.size() > 1 ? and(conditions) : noop(conditions);
        String statement = ifStatement(condition, "\"合格\"", "\"不合格\"");

        String checker = genChecker(loc);

        return funcWrapper(ifStatement(checker, "\"\"", statement));
    }

    private String showDeltaRuleResult(Cell cell, TableItem item) {
        String yKey = item.getExp().get("Y_Key");
        String zKey = item.getExp().get("Z_Key");
        String deltaMaxKey = item.getExp().get("deltaMax");
        if (yKey == null || zKey == null || deltaMaxKey == null) return null;

        CellPos yPos = cellMap.get(yKey);
        CellPos zPos = cellMap.get(zKey);

        String yLoc = toCellLocation(yPos.getCol(), yPos.getRow() + 1);
        String zLoc = toCellLocation(zPos.getCol(), zPos.getRow() + 1);

        List<String> conditions = new ArrayList<>();
        // ABS((y-z)/z) < max
        String exp = Operator.MINUS.apply(yLoc, zLoc);
        Double max = Double.parseDouble(deltaMaxKey) / 100;
        exp = Operator.DIVIDE.apply(exp, zLoc);
        conditions.add(Operator.LESS_THAN.apply(abs(exp), String.valueOf(max)));
        String condition = noop(conditions);
        String statement = ifStatement(condition, "\"合格\"", "\"不合格\"");

        String checker = genChecker(yLoc, zLoc);

        return funcWrapper(ifStatement(checker, "\"\"", statement));
    }

    private String showVerifyRuleResult(Cell cell, TableItem item) {
        Double x = toDouble(item.getExp().get("norm"));
        String yKey = item.getExp().get("Y_Key");
        String zKey = item.getExp().get("Z_Key");
        if (yKey == null || zKey == null || x == null) return null;

        CellPos yPos = cellMap.get(yKey);
        CellPos zPos = cellMap.get(zKey);

        String yLoc = toCellLocation(yPos.getCol(), yPos.getRow() + 1);
        String zLoc = toCellLocation(zPos.getCol(), zPos.getRow() + 1);

        Double deltaMax = Optional.ofNullable(toDouble(item.getExp().get("deltaMaxPercent"))).orElse(0.0) / 100;
        Double muMax = Optional.ofNullable(toDouble(item.getExp().get("MuMaxPercent"))).orElse(0.0) / 100;

        List<String> conditions = new ArrayList<>();
        // delta: ABS((y-x)/x) < deltaMax
        String exp = Operator.MINUS.apply(yLoc, String.valueOf(x));
        exp = Operator.DIVIDE.apply(exp, String.valueOf(x));
        String delta = Operator.GREATER_THAN.apply(abs(exp), String.valueOf(deltaMax));
        conditions.add(delta);
        // mu: ABS((y-z)/z) < muMax
        exp = Operator.MINUS.apply(yLoc, zLoc);
        exp = Operator.DIVIDE.apply(exp, zLoc);
        String mu = Operator.GREATER_THAN.apply(abs(exp), String.valueOf(muMax));
        conditions.add(mu);
        String condition = or(conditions);
        String statement = ifStatement(condition, "\"不合格\"", "\"合格\"");

        String checker = genChecker(yLoc, zLoc);

        return funcWrapper(ifStatement(checker, "\"\"", statement));
    }

    private String toCellLocation(int col, int row) {
        return String.format("%s%d", convertToTitle(col), row);
    }

    private String genChecker(String... pos) {
        List<String> expressions = new ArrayList<>();
        for (String p : pos) {
            expressions.add(String.format("ISBLANK(%s), %s=0", p, p));
        }
        return or(expressions);
    }

    //////
    // Function related method
    //////
    private String and(List<String> expressions) {
        return compose("AND", expressions);
    }

    private String or(List<String> expressions) {
        return compose("OR", expressions);
    }

    private String noop(List<String> expressions) {
        return compose("", expressions);
    }

    private String abs(String exp) {
        return String.format("ABS(%s)", exp);
    }

    private String compose(String operator, List<String> expressions) {
        StringBuilder buf = new StringBuilder(operator);
        buf.append("(");
        for (String exp : expressions) {
            buf.append(exp).append(",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        return buf.toString();
    }

    private String ifStatement(String condition, String... statments) {
        List<String> expressions = new ArrayList<>();
        expressions.add(condition);
        Collections.addAll(expressions, statments);
        return compose("IF", expressions);
    }

    private String showRangeRuleResult(Cell cell, TableItem item) {
        boolean hasMin = item.getExp().keySet().contains("min");
        boolean hasMax = item.getExp().keySet().contains("max");
        if (!hasMax && !hasMin) return null;

        String loc = toCellLocation(cell.getColumnIndex(), cell.getRowIndex() + 1);
        List<String> conditions = new ArrayList<>();
        Double min = null;
        if (hasMin) {
            // loc <GT_OP> min
            min = Optional.ofNullable(toDouble(item.getExp().get("min"))).orElse(0.0);
            Operator minCompareOp = Operator.fromString(item.getExp().get("minCompare"));
            if (minCompareOp == null) minCompareOp = Operator.GREATER_THAN;
            conditions.add(minCompareOp.apply(loc, String.valueOf(min)));
        }
        Double max = null;
        if (hasMax) {
            // loc <LT_OP> max
            max = Optional.ofNullable(toDouble(item.getExp().get("max"))).orElse(0.0);
            Operator maxCompareOp = Operator.fromString(item.getExp().get("maxCompare"));
            if (maxCompareOp == null) maxCompareOp = Operator.LESS_THAN;
            conditions.add(maxCompareOp.apply(loc, String.valueOf(max)));
        }
        String condition = conditions.size() > 1 ? and(conditions) : noop(conditions);
        String statement = ifStatement(condition, "\"合格\"", "\"不合格\"");

        String checker = genChecker(loc);

        return funcWrapper(ifStatement(checker, "\"\"", statement));
    }

    /**
     * Check whether the target item need post process.
     *
     * @param item item to be validated
     * @return true if the item need post process, false otherwise
     */
    private boolean needPostProcess(TableItem item) {
        if (item.getExp() == null || item.getExp().size() == 0) return false;
        if ("Number".equalsIgnoreCase(item.getComponent().toString())) {
            return true;
        } else if ("Label".equalsIgnoreCase(item.getComponent().toString())) {
            return true;
        }
        return false;
    }

    private Double toDouble(Object input) {
        try {
            return input == null ? null : Double.parseDouble(input.toString().replaceAll("\"", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void renderSelectCell(Cell cell, List<TableOption> options) {
        if (options == null || options.size() == 0) return;

        StringBuilder labels = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (TableOption option : options) {
            if (option.getKey() != null && option.getValue() != null) {
                labels.append(option.getValue()).append(SEMICOLON);
                values.append(option.getKey()).append(SEMICOLON);
            }
        }

        String comment = String.format(DROPDOWN_WIDGET_FORMAT, StringUtils.removeEnd(labels.toString(), SEMICOLON),
                StringUtils.removeEnd(values.toString(), SEMICOLON));
        writeCellComment(cell, comment);
    }

    private void renderSwitchCell(Cell cell) {
        String comment = String.format(DROPDOWN_WIDGET_FORMAT, SWITCH_VALUES, SWITCH_VALUES);
        writeCellComment(cell, comment);
    }

    private void renderNumberCell(Cell cell) {
        writeCellComment(cell, FLOAT_WIDGET_FORMAT);
    }

    private void renderDateCell(Cell cell) {
        writeCellComment(cell, CALENDAR_WIDGET_FORMAT);
    }

    private String keyWrapper(String key) {
        return String.format(CELL_VALUE_FORMAT, key);
    }

    private String funcWrapper(String func) {
        return String.format("$[%s]", func);
    }

    private String resultWrapper(String key) {
        return String.format(CELL_RESULT_FORMAT, key);
    }

    private Cell getCell(int row, int col) {
        Row dataRow = sheet.getRow(row);
        if (dataRow == null) {
            dataRow = sheet.createRow(row);
        }

        Cell cell = dataRow.getCell(col);
        if (null == cell) {
            cell = dataRow.createCell(col);
        }
        return cell;
    }

    private void writeCellData(Cell cell, Object data) {
        if (data == null) {
            return;
        }
        if (data instanceof String) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) data);
        }
        if (data instanceof Date) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Date) data);
        }
        if (data instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) data);
        }
        if (data instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Double) data);
        }
    }

    private void writeCellComment(Cell cell, String rawContent) {
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = factory.createClientAnchor();
        Comment comment = drawing.createCellComment(anchor);
        RichTextString contentStr = factory.createRichTextString(rawContent);
        comment.setString(contentStr);
        comment.setAuthor(DEFAULT_AUTHOR);
        cell.setCellComment(comment);
    }

    /**
     * Inner class for Cell Position.
     */
    static class CellPos {
        private final int col;
        private final int row;

        CellPos(int row, int col) {
            this.col = col;
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public int getRow() {
            return row;
        }
    }

    /**
     * Operator is the support operator in Excel.
     */
    enum Operator {
        PLUS("+") {
            String apply(String lhs, String rhs) {
                return String.format("%s %s %s", lhs, "+", rhs);
            }
        },
        MINUS("-") {
            String apply(String lhs, String rhs) {
                return String.format("%s %s %s", lhs, "-", rhs);
            }
        },
        DIVIDE("/") {
            String apply(String lhs, String rhs) {
                return String.format("%s %s %s", lhs, "/", rhs);
            }
        },
        GREATER_THAN("GT") {
            String apply(String lhs, String rhs) {
                return String.format("%s %s %s", lhs, ">", rhs);
            }
        },
        GREATER_EQUAL("GE") {
            String apply(String lhs, String rhs) {
                return String.format("%s %s %s", lhs, ">=", rhs);
            }
        },
        LESS_THAN("LT") {
            String apply(String lhs, String rhs) {
                return String.format("%s %s %s", lhs, "<", rhs);
            }
        },
        LESS_EQUAL("LE") {
            String apply(String lhs, String rhs) {
                return String.format("%s %s %s", lhs, "<=", rhs);
            }
        };
        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        abstract String apply(String lhs, String rhs);

        @Override
        public String toString() {
            return symbol;
        }

        // Implementing a fromString method on an Enmu type.
        private static final Map<String, Operator> stringToEnum = new HashMap<>();

        static { // Initialize map from constant name to enum constant
            for (Operator op : values())
                stringToEnum.put(op.toString(), op);
        }

        /**
         * Returns Operator for string, or null if string is invalid.
         *
         * @param symbol symobol string
         * @return Operator
         */
        public static Operator fromString(String symbol) {
            return stringToEnum.get(symbol);
        }
    }
}
