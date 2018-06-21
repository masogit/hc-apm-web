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

import static com.ge.apm.service.utils.ReportTemplateDocument.Function.*;
import static com.ge.apm.service.utils.ReportTemplateDocument.Operator.*;
import static com.ge.apm.service.utils.ReportTemplateDocument.Widget.FUNC;

/**
 * ReportTemplateDocument represent Excel Template that generated from JSON object.
 *
 * @author 212697838
 */
public class ReportTemplateDocument {
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESES = ")";

    private static final String DEFAULT_AUTHOR = "GEAPM";

    private TableSchema content;
    private Workbook workbook;
    private CreationHelper factory;
    private Sheet sheet;
    private Drawing<?> drawing;
    private ClientAnchor anchor;

    // unlocked style
    private CellStyle unlockedStyle;

    private Map<String, CellPos> cellMap;

    public ReportTemplateDocument(Workbook workbook, TableSchema content, String sheetName) {
        this.content = content;
        this.workbook = workbook;
        this.factory = workbook.getCreationHelper();
        this.sheet = workbook.createSheet(sheetName);
        this.drawing = sheet.createDrawingPatriarch();
        this.anchor = factory.createClientAnchor();

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

        applyForm(row);
        // moved post process here to avoid early-obtained backref.
        postProcess();

        workbook.write(out);
    }

    /**
     * Suppress Column header.
     *
     * @param length actual length of Excel form.
     */
    private void applyForm(int length) {
        //
        // See: POI bug 59393
        // https://svn.apache.org/viewvc/poi/trunk/src/testcases/org/apache/poi/ss/usermodel/BaseTestBugzillaIssues.java?view=markup&pathrev=1742868
        //
        // fails with IllegalArgumentException("Multiple cell comments in one cell are not allowed, cell: A1")
        // because createCellComment tries to create a cell at A1
        // (from CellAddress(anchor.getRow1(), anchor.getCell1())),
        // but cell A1 already has a comment (comment1).
        // Need to atomically create a comment and attach it to a cell.
        // Current workaround: change anchor between each usage
        // anchor.setCol1(1);
        Cell head = getCell(1, 0);
        String formCmd = Widget.FORM_BODY.apply(String.valueOf(length));
        writeCellComment(head, formCmd);
    }

    private CellPos getCellPos(String key) {
        return cellMap.get(key);
    }

    private void postProcess() {
        for (TableGroup group : content.getGroups()) {
            for (TableItem item : group.getItems()) {
                if (!needPostProcess(item)) continue;
                CellPos pos = cellMap.get(item.getKey());
                Cell cell = getCell(pos.getRow(), pos.getCol());
                Rule rule = Rule.fromString(item.getExp().get("rule"));
                if (rule != null) {
                    Optional<String> resOutput = rule.apply(this, cell, item);
                    writeCellData(cell, resOutput.orElse(null));
                    if (resOutput.isPresent()) {
                        writeCellComment(cell, Widget.SAVE_COMMENT.apply(item.getResKey())); // resultWrapper(item.getResKey()));
                    }
                }
            }
        }
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
        writeCellData(cell, Widget.CELL_VALUE.apply(item.getKey()));  // keyWrapper(item.getKey()));
        // add comment for input field
        Widget widget = Widget.fromComponentType(item.getComponent());
        if (widget != null) {
            String comment = widget.apply(genOptions(item.getOptions()));
            writeCellComment(cell, comment);
        }
        cell.setCellStyle(unlockedStyle);
        // link input field
        cellMap.put(item.getKey(), new CellPos(row, col));
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

    private static Double toDouble(Object input) {
        try {
            return input == null ? null : Double.parseDouble(input.toString().replaceAll("\"", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] genOptions(List<TableOption> options) {
        if (options == null) return null;
        StringBuilder labels = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (TableOption option : options) {
            if (option.getKey() != null && option.getValue() != null) {
                labels.append(option.getValue()).append(SEMICOLON);
                values.append(option.getKey()).append(SEMICOLON);
            }
        }

        return new String[]{StringUtils.removeEnd(labels.toString(), SEMICOLON), StringUtils.removeEnd(values.toString(), SEMICOLON)};
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
        private static final int ALPHABET_SIZE = 'Z' - 'A' + 1;

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

        static String checkExpression(String... pos) {
            String[] exps = new String[pos.length];
            for (int i = 0; i < pos.length; i++) {
                String cell = pos[i];
                exps[i] = Operator.COMMA.apply(ISBLANK.apply(cell), ASSIGN.apply(cell, "0"));
            }
            return OR.apply(StringUtils.join(exps, ","));
        }

        static String location(int col, int row) {
            return String.format("%s%d", title(col), row);
        }

        /**
         * Convert a Column to Excel Column Title.
         *
         * @param n column in int
         * @return Column Title in String
         */
        static String title(int n) {
            StringBuilder buf = new StringBuilder();

            while (n > 0) {
                char c = (char) ('A' + (n - 1) % ALPHABET_SIZE);
                buf.append(c);
                n = (n - 1) / ALPHABET_SIZE;
            }

            return buf.reverse().toString();
        }
    }

    enum Widget {
        FUNC("Func", "$[%s]") {
            String apply(String... value) {
                return String.format(pattern(), value[0]);
            }
        },
        CELL_VALUE("CellValue", "${X.%s}") {
            String apply(String... value) {
                return String.format(pattern(), value[0]);
            }
        },
        SAVE_COMMENT("SaveComment", "$save{X.%s}") {
            String apply(String... value) {
                return String.format(pattern(), value[0]);
            }
        },
        DROP_DOWN("DropDownBox", "$widget.dropdown{itemLabels=\"%s\" itemValues=\"%s\" }") {
            String apply(String... value) {
                return String.format(pattern(), value[0], value[1]);
            }
        },
        SWITCH_BOX("Switch", "$widget.dropdown{itemLabels=\"%s\" itemValues=\"%s\" }") {
            String apply(String... value) {
                String v = "是;否";
                return String.format(pattern(), v, v);
            }
        },
        CALENDAR_BOX("Calendar", "$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}") {
            String apply(String... value) {
                return pattern();
            }
        },
        NUMBER_BOX("Number", "$widget.inputnumber{decimalPlaces=\"2\"}") {
            String apply(String... value) {
                return pattern();
            }
        },
        FORM_BODY("Form", "tie:form(name=\"Template\" length=\"%s\" headerLength=\"2\" footerLength=\"0\")") {
            String apply(String... value) {
                return String.format(pattern(), value[0]);
            }
        };
        private final String name;
        private final String pattern;

        Widget(String name, String pattern) {
            this.name = name;
            this.pattern = pattern;
        }

        abstract String apply(String... value);

        public String pattern() {
            return pattern;
        }

        @Override
        public String toString() {
            return name;
        }

        private static final Map<TableItem.ComponentType, Widget> componentToEnum = new HashMap<>();

        static {
            componentToEnum.put(TableItem.ComponentType.Select, DROP_DOWN);
            componentToEnum.put(TableItem.ComponentType.Switch, SWITCH_BOX);
            componentToEnum.put(TableItem.ComponentType.Number, NUMBER_BOX);
            componentToEnum.put(TableItem.ComponentType.Date, CALENDAR_BOX);
        }

        /**
         * Return Widget for ComponentType, or null if string is invalid.
         *
         * @param componentType component type
         * @return Widget
         */
        public static Widget fromComponentType(TableItem.ComponentType componentType) {
            return componentToEnum.get(componentType);
        }

    }

    /**
     * Function represents the counterpart in Excel.
     */
    enum Function {
        AND("AND") {
            String apply(String... expressions) {
                if (expressions == null || expressions.length == 0)
                    throw new IllegalArgumentException("Bad parameter in 'AND' function");
                return compose(toString(), expressions);
            }
        },
        OR("OR") {
            String apply(String... expressions) {
                if (expressions == null || expressions.length == 0)
                    throw new IllegalArgumentException("Bad parameter in 'OR' function");
                return compose(toString(), expressions);
            }
        },
        NOOP("") {
            String apply(String... expressions) {
                if (expressions == null || expressions.length == 0) return toString();
                return compose(toString(), expressions);
            }
        },
        ABS("ABS") {
            String apply(String... expressions) {
                if (expressions == null || expressions.length != 1)
                    throw new IllegalArgumentException("Bad parameter in 'ABS' function");
                return String.format("%s(%s)", toString(), expressions[0]);
            }
        },
        IF("IF") {
            String apply(String... expressions) {
                if (expressions == null || expressions.length < 2 || expressions.length > 3)
                    throw new IllegalArgumentException("Bad parameter in 'IF' function");
                return compose(toString(), expressions);
            }
        },
        ISBLANK("ISBLANK") {
            String apply(String... expressions) {
                if (expressions == null || expressions.length != 1)
                    throw new IllegalArgumentException("Bad parameter in 'ISBLANK' function");
                return String.format("%s(%s)", toString(), expressions[0]);
            }
        };
        private final String operator;

        Function(String operator) {
            this.operator = operator;
        }

        abstract String apply(String... expressions);

        @Override
        public String toString() {
            return operator;
        }

        String compose(String operator, String... expressions) {
            StringBuilder exp = new StringBuilder(operator);
            exp.append(OPEN_PARENTHESIS)
                    .append(StringUtils.join(expressions, COMMA))
                    .append(CLOSE_PARENTHESES);
            return exp.toString();
        }
    }

    /**
     * Rule is the parser of {@code exp} node in JSON object.
     */
    enum Rule {
        RANGE("RangeRule") {
            Optional<String> apply(ReportTemplateDocument doc, Cell cell, TableItem item) {
                boolean hasMin = item.getExp().keySet().contains("min");
                boolean hasMax = item.getExp().keySet().contains("max");
                if (!hasMax && !hasMin) return Optional.empty();

                String loc = CellPos.location(cell.getColumnIndex(), cell.getRowIndex() + 1);
                List<String> conditions = new ArrayList<>();
                Double min = null;
                if (hasMin) {
                    // loc <GT_OP> min
                    min = Optional.ofNullable(toDouble(item.getExp().get("min"))).orElse(0.0);
                    Operator minCompareOp = Operator.fromString(item.getExp().get("minCompare"));
                    if (minCompareOp == null) minCompareOp = GREATER_THAN;
                    conditions.add(minCompareOp.apply(loc, String.valueOf(min)));
                }
                Double max = null;
                if (hasMax) {
                    // loc <LT_OP> max
                    max = Optional.ofNullable(toDouble(item.getExp().get("max"))).orElse(0.0);
                    Operator maxCompareOp = Operator.fromString(item.getExp().get("maxCompare"));
                    if (maxCompareOp == null) maxCompareOp = LESS_THAN;
                    conditions.add(maxCompareOp.apply(loc, String.valueOf(max)));
                }
                String condition = conditions.size() > 1 ? AND.apply(conditions.toArray(new String[0])) : NOOP.apply(conditions.toArray(new String[0]));
                String statement = IF.apply(condition, "\"合格\"", "\"不合格\"");

                String checker = CellPos.checkExpression(loc);

                return Optional.of(FUNC.apply(IF.apply(checker, "\"\"", statement)));
            }
        },
        SCOPE("ScopeRule") {
            Optional<String> apply(ReportTemplateDocument doc, Cell cell, TableItem item) {
                boolean hasMin = item.getExp().keySet().contains("min");
                boolean hasMax = item.getExp().keySet().contains("max");
                if (!item.getExp().keySet().contains("norm")) return Optional.empty();
                if (!hasMax && !hasMin) return Optional.empty();

                Double norm = toDouble(item.getExp().get("norm"));
                String loc = CellPos.location(cell.getColumnIndex(), cell.getRowIndex() + 1);
                List<String> conditions = new ArrayList<>();
                Double min = null;
                if (hasMin) {
                    // ABS(loc - norm) <GT_OP> min
                    min = Optional.ofNullable(toDouble(item.getExp().get("min"))).orElse(0.0);
                    String exp = MINUS.apply(loc, String.valueOf(norm));
                    Operator minCompareOp = Operator.fromString(item.getExp().get("minCompare"));
                    if (minCompareOp == null) minCompareOp = GREATER_THAN;
                    conditions.add(minCompareOp.apply(ABS.apply(exp), String.valueOf(min)));
                }
                Double max = null;
                if (hasMax) {
                    // ABS(loc - norm) <LT_OP> max
                    max = Optional.ofNullable(toDouble(item.getExp().get("max"))).orElse(0.0);
                    String exp = MINUS.apply(loc, String.valueOf(norm));
                    Operator maxCompareOp = Operator.fromString(item.getExp().get("maxCompare"));
                    if (maxCompareOp == null) maxCompareOp = LESS_THAN;
                    conditions.add(maxCompareOp.apply(ABS.apply(exp), String.valueOf(max)));
                }
                String condition = conditions.size() > 1 ? AND.apply(conditions.toArray(new String[0])) : NOOP.apply(conditions.toArray(new String[0]));
                String statement = IF.apply(condition, "\"合格\"", "\"不合格\"");

                String checker = CellPos.checkExpression(loc);

                return Optional.of(FUNC.apply(IF.apply(checker, "\"\"", statement)));
            }
        },
        DELTA("DeltaRule") {
            Optional<String> apply(ReportTemplateDocument doc, Cell cell, TableItem item) {
                String yKey = item.getExp().get("Y_Key");
                String zKey = item.getExp().get("Z_Key");
                String deltaMaxKey = item.getExp().get("deltaMax");
                if (yKey == null || zKey == null || deltaMaxKey == null) return Optional.empty();

                CellPos yPos = doc.getCellPos(yKey);
                CellPos zPos = doc.getCellPos(zKey);

                String yLoc = CellPos.location(yPos.getCol(), yPos.getRow() + 1);
                String zLoc = CellPos.location(zPos.getCol(), zPos.getRow() + 1);

                List<String> conditions = new ArrayList<>();
                // ABS((y-z)/z) < max
                String exp = NOOP.apply(MINUS.apply(yLoc, zLoc));
                Double max = Double.parseDouble(deltaMaxKey) / 100;
                exp = DIVIDE.apply(exp, zLoc);
                conditions.add(LESS_THAN.apply(ABS.apply(exp), String.valueOf(max)));
                String condition = NOOP.apply(conditions.toArray(new String[0]));
                String statement = IF.apply(condition, "\"合格\"", "\"不合格\"");

                String checker = CellPos.checkExpression(yLoc, zLoc);

                return Optional.of(FUNC.apply(IF.apply(checker, "\"\"", statement)));
            }
        },
        VERIFY("VerifyRule") {
            Optional<String> apply(ReportTemplateDocument doc, Cell cell, TableItem item) {
                Double x = toDouble(item.getExp().get("norm"));
                String yKey = item.getExp().get("Y_Key");
                String zKey = item.getExp().get("Z_Key");
                if (yKey == null || zKey == null || x == null) return Optional.empty();

                CellPos yPos = doc.getCellPos(yKey);
                CellPos zPos = doc.getCellPos(zKey);

                String yLoc = CellPos.location(yPos.getCol(), yPos.getRow() + 1);
                String zLoc = CellPos.location(zPos.getCol(), zPos.getRow() + 1);

                Double deltaMax = Optional.ofNullable(toDouble(item.getExp().get("deltaMaxPercent"))).orElse(0.0) / 100;
                Double muMax = Optional.ofNullable(toDouble(item.getExp().get("MuMaxPercent"))).orElse(0.0) / 100;

                List<String> conditions = new ArrayList<>();
                // delta: ABS((y-x)/x) < deltaMax
                String exp = NOOP.apply(MINUS.apply(yLoc, String.valueOf(x)));
                exp = DIVIDE.apply(exp, String.valueOf(x));
                String delta = GREATER_THAN.apply(ABS.apply(exp), String.valueOf(deltaMax));
                conditions.add(delta);
                // mu: ABS((y-z)/z) < muMax
                exp = NOOP.apply(MINUS.apply(yLoc, zLoc));
                exp = DIVIDE.apply(exp, zLoc);
                String mu = GREATER_THAN.apply(ABS.apply(exp), String.valueOf(muMax));
                conditions.add(mu);
                String condition = OR.apply(conditions.toArray(new String[0]));
                String statement = IF.apply(condition, "\"不合格\"", "\"合格\"");

                String checker = CellPos.checkExpression(yLoc, zLoc);

                return Optional.of(FUNC.apply(IF.apply(checker, "\"\"", statement)));
            }
        };
        private final String name;

        Rule(String name) {
            this.name = name;
        }

        /**
         * Apply Different Strategy to make function on Excel Document.
         *
         * @param doc  Excel
         * @param cell cell
         * @param item JSON object
         * @return formatted function string in TieFaces
         */
        abstract Optional<String> apply(ReportTemplateDocument doc, Cell cell, TableItem item);

        @Override
        public String toString() {
            return name;
        }

        // Implementing a fromString method on an Enum type.
        private static final Map<String, Rule> stringToEnum = new HashMap<>();

        static { // Initialize map from constant name to enum constant
            for (Rule rule : values()) {
                stringToEnum.put(rule.toString(), rule);
            }
        }

        /**
         * Return Rule for string, or null if string is invalid.
         *
         * @param name rule name
         * @return Rule
         */
        public static Rule fromString(String name) {
            return stringToEnum.get(name);
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
        ASSIGN("=") {
            String apply(String lhs, String rhs) {
                return String.format("%s%s%s", lhs, "=", rhs);
            }
        },
        COMMA(",") {
            String apply(String lhs, String rhs) {
                return String.format("%s%s%s", lhs, ",", rhs);
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
