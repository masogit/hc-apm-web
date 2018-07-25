package com.ge.apm.domain.bo.parser;

import com.deepoove.poi.data.TableRenderData;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

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
        return new TableRenderData(
                rows,
                this.description);
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
