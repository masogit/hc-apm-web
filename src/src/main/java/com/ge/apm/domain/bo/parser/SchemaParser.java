package com.ge.apm.domain.bo.parser;

import com.deepoove.poi.data.TableRenderData;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import static com.ge.apm.domain.bo.MaintenanceTemplate.Widget.TEXT;

/**
 * SchemaParser parse {@link com.ge.apm.domain.pure.TableSchema} to POI stuff.
 */
public class SchemaParser {

    private static final Joiner SEMICOLON_JOINER = Joiner.on(";");

    private final TableSchema schema;
    private final String description;

    private SchemaParser(Builder builder) {
        this.schema = builder.schema;
        this.description = builder.description;
    }

    public TableRenderData apply() {
        List<Object> rows = new ArrayList<>();
        for(TableGroup group : schema.getGroups()) {
            rows.add(group.getTitle());
            for (TableItem item : group.getItems()) {
                rows.add(SEMICOLON_JOINER.join(item.getLabel(), TEXT.apply(item.getKey())));
            }
        }
        return new TableRenderData(
                rows,
                this.description);
    }

    public static class Builder {
        private final TableSchema schema;

        private String description = "table";

        public Builder(TableSchema schema) {
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
