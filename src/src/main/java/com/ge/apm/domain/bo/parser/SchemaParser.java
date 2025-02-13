package com.ge.apm.domain.bo.parser;

import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.data.style.TableStyle;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ge.apm.domain.bo.MaintenanceTemplate.Widget.PICTURE;
import static com.ge.apm.domain.bo.MaintenanceTemplate.Widget.TEXT;

/**
 * SchemaParser parse {@link com.ge.apm.domain.pure.TableSchema} to POI stuff.
 */
public class SchemaParser {

    public static final Joiner UNDERSCORE_JOINER = Joiner.on("_");

    private static final Joiner SEMICOLON_JOINER = Joiner.on(";");

    private final TableSchema schema;
    private final String description;
    private final String prefix;

    private SchemaParser(Builder builder) {
        this.prefix = builder.prefix;
        this.schema = builder.schema;
        this.description = builder.description;
    }

    public TableRenderData apply() {
        List<Object> rows = new ArrayList<>();
        for(TableGroup group : schema.getGroups()) {
            rows.add(group.getTitle());
            for (TableItem item : group.getItems()) {
                if (item == null || item.getComponent() == null) {
                    continue;
                }
                switch (item.getComponent()) {
                    case Number:
                        if (Strings.isNullOrEmpty(item.getResKey())) {
                            rows.add(SEMICOLON_JOINER.join(item.getLabel(), TEXT.apply(wrap(item.getKey()))));
                        } else {
                            rows.add(SEMICOLON_JOINER.join(item.getLabel(), TEXT.apply(wrap(item.getKey())), TEXT.apply(wrap(item.getResKey()))));
                        }
                        break;
                    case Label:
                        rows.add(SEMICOLON_JOINER.join(item.getLabel(), TEXT.apply(wrap(item.getResKey()))));
                        break;
                    case Picture:
                        rows.add(SEMICOLON_JOINER.join(item.getLabel(), PICTURE.apply(wrap(item.getKey()))));
                        break;
                    case Switch:
                    case Select:
                    case Date:
                    case Text:
                    case TextArea:
                    default:
                        rows.add(SEMICOLON_JOINER.join(item.getLabel(), TEXT.apply(wrap(item.getKey()))));
                        break;
                }
            }
        }

//        TableRenderData res =  new TableRenderData(
//                rows,
//                this.description);
//        res.setWidth(5000);
//        return res;
        TableRenderData tableRenderData = convertToNewTableRenderData(rows, description, String.valueOf(5000));

        return tableRenderData;
    }


    public static TableRenderData convertToNewTableRenderData(List<Object> oldRows, String description, String tableWidth) {
        List<RowRenderData> newRows = new ArrayList<>();

        int maxColumns = 0;
        for (Object oldRowObj : oldRows) {
            String oldRow = oldRowObj.toString();
            String[] cells = oldRow.split(";");
            maxColumns = Math.max(maxColumns, cells.length);
        }
        Style textStyle = new Style();
        textStyle.setFontFamily("宋体");
        textStyle.setFontSize(11);
        for (Object oldRowObj : oldRows) {
            String oldRow = oldRowObj.toString();
            String[] cells = oldRow.split(";");
            RowRenderData newRow = new RowRenderData();

            for (int i = 0; i < maxColumns; i++) {
                String cellData = i < cells.length ? cells[i] : "";
                // 创建 TextRenderData
                TextRenderData textRenderData = new TextRenderData(cellData,textStyle);
                // 创建 ParagraphRenderData 并添加 TextRenderData
                ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
                paragraphRenderData.addText(textRenderData);
                // 创建 CellRenderData
                CellRenderData cell = new CellRenderData();
                cell.getParagraphs().add(paragraphRenderData);
                newRow.addCell(cell);
            }
            newRows.add(newRow);
        }

        TableStyle tableStyle = new TableStyle();
        tableStyle.setWidth(tableWidth);
        TableRenderData tableRenderData = new TableRenderData();
        tableRenderData.setRows(newRows);
//        tableRenderData.setTableStyle(tableStyle);

        return tableRenderData;
    }
    private String wrap(String key) {
        return UNDERSCORE_JOINER.join(prefix, key);
    }

    public static class Builder {
        private final TableSchema schema;

        private String description = "table";

        private String prefix;

        public Builder(String prefix, TableSchema schema) {
            this.prefix = prefix;
            this.schema = schema;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public SchemaParser build() {
            return new SchemaParser(this);
        }
    }
}
