package com.ge.apm.domain.bo;

import com.deepoove.poi.data.TableRenderData;
import com.ge.apm.domain.bo.parser.SchemaParser;
import com.ge.apm.domain.pure.TableSchema;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Template for maintenance report.
 */
public class MaintenanceTemplate {

    private Map<String, Object> content;

    private MaintenanceTemplate(Builder builder) {
        this.content = builder.content;
    }

    public Map<String, Object> data() {
        for (Component ignore : Lists.newArrayList(Component.PERFORMANCE_REPORT, Component.SAFETY_REPORT)) {
            if (!this.content.containsKey(ignore.toString())) {
                this.content.put(ignore.toString(), empty());
            }
        }

        return this.content;
    }

    private TableRenderData empty() {
        return new TableRenderData(
                null,
                new ArrayList<Object>() {{
                    add("暂无");
                }},
                "no data",
                8000);
    }

    /**
     * Builder for {@link MaintenanceTemplate}.
     */
    public static class Builder {

        private Map<String, Object> content;

        public Builder() {
            this.content = new HashMap<>();

            // add Device stuff
            for (Device d : Device.values()) {
                this.content.put(d.toString(), d.value());
            }

            // add Maintenance stuff
            for (Maintenance m : Maintenance.values()) {
                this.content.put(m.toString(), m.value());
            }

            this.content.put(Component.ACCESSORY_INFORMATION.toString(), Component.ACCESSORY_INFORMATION.value());
            this.content.put(Component.ACCESSORY_SIGNATURE.toString(), Component.ACCESSORY_SIGNATURE.value());
            this.content.put(Component.ACCESSORY_SIGNATURE_PLACE_HOLDER.toString(), Component.ACCESSORY_SIGNATURE_PLACE_HOLDER.value());
            this.content.put(Component.MAINTENANCE_WORKFLOW.toString(), Component.MAINTENANCE_WORKFLOW.value());
            this.content.put(Component.AUDIT_RECORDS.toString(), Component.AUDIT_RECORDS.value());
            this.content.put(Component.CHECK_LIST.toString(), Component.CHECK_LIST.value());
        }

        public MaintenanceTemplate build() {
            return new MaintenanceTemplate(this);
        }

        public Builder safetyReport(TableSchema schema) {
            this.updateData(Component.SAFETY_REPORT, schema);
            return this;
        }

        public Builder performanceReport(TableSchema schema) {
            this.updateData(Component.PERFORMANCE_REPORT, schema);
            return this;
        }

        /**
         * Common Method for Dynamic Table.
         *
         * @param component table key
         * @param schema    poi formatted content
         */
        private void updateData(Component component, TableSchema schema) {
            SchemaParser.Builder builder = new SchemaParser.Builder(component.toString(), schema);
            this.content.put(component.toString(), builder.build().apply());
        }
    }

    /**
     * Fields in accessory information table.
     */
    public enum AccessoryInfo {
        NAME("name"),
        QUANTITY("quantity"),
        PRICE("price"),
        TYPE("type"),
        SERIAL_NUM("serial_num"),
        WARRANTY_DATE("warranty_date"),
        ARRIVED("arrived");

        private final String key;

        AccessoryInfo(String key) {
            this.key = key;
        }


        @Override
        public String toString() {
            return key;
        }
    }

    /**
     * Fields in audit message table.
     */
    public enum AuditMsgInfo {
        TIME("time"),
        OWNER("owner"),
        INFO("info");
        private final String key;

        AuditMsgInfo(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    /**
     * Fields in check list table.
     */
    public enum CheckListInfo {
        NAME("name"),
        PERIOD("period"),
        CONTENT("content"),
        RESULT("result");
        private final String key;

        CheckListInfo(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    /**
     * Fields in maintenance information table.
     */
    public enum MaintenanceInfo {
        STEP("step"),
        OWNER("owner"),
        START_TIME("start_time"),
        END_TIME("end_time");

        private final String key;

        MaintenanceInfo(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    /**
     * Component in Maintenance Report.
     */
    public enum Component {
        ACCESSORY_INFORMATION("accessory_information", Widget.TABLE),
        ACCESSORY_SIGNATURE("accessory_signature", Widget.PICTURE),
        ACCESSORY_SIGNATURE_PLACE_HOLDER("accessory_signature_place_holder", Widget.TEXT),
        MAINTENANCE_WORKFLOW("maintenance_workflow", Widget.TABLE),
        AUDIT_RECORDS("audit_records", Widget.TABLE),
        CHECK_LIST("check_list", Widget.TABLE),
        SAFETY_REPORT("safety_report", Widget.DYNAMIC),
        PERFORMANCE_REPORT("performance_report", Widget.DYNAMIC);

        private final String key;
        private final Widget widget;

        Component(String name, Widget widget) {
            this.key = name;
            this.widget = widget;
        }

        public String value() {
            return this.widget.apply(toString());
        }

        @Override
        public String toString() {
            return key;
        }
    }

    /**
     * Device Information related fields.
     */
    public enum Device {
        NAME("name", Widget.TEXT),
        DEPARTMENT("department", Widget.TEXT),
        TYPE("type", Widget.TEXT),
        OEM("oem", Widget.TEXT),
        SERIAL("serial", Widget.TEXT),
        NUMBER("number", Widget.TEXT),
        QR("qr", Widget.TEXT);

        private final String key;
        private final Widget widget;

        Device(String name, Widget widget) {
            this.key = name;
            this.widget = widget;
        }

        public String value() {
            return this.widget.apply(toString());
        }

        @Override
        public String toString() {
            return "device_" + key;
        }
    }

    /**
     * Maintenance Information related fields.
     */
    public enum Maintenance {
        NUMBER("number", Widget.TEXT),
        CREATOR("creator", Widget.TEXT),
        LEVEL("level", Widget.TEXT),
        TYPE("type", Widget.TEXT),
        PLAN_TIME("plan_time", Widget.TEXT),
        COMPLETE_TIME("complete_time", Widget.TEXT),
        OPERATOR("op", Widget.TEXT),
        OP_TIME("op_time", Widget.TEXT),
        COWORKER1("coworker1", Widget.TEXT),
        COWORKER1_TIME("coworker1_time", Widget.TEXT),
        COWORKER2("coworker2", Widget.TEXT),
        COWORKER2_TIME("coworker2_time", Widget.TEXT),
        RESULT("result", Widget.TEXT);

        private final String key;
        private final Widget widget;

        Maintenance(String name, Widget widget) {
            this.key = name;
            this.widget = widget;
        }

        public String value() {
            return this.widget.apply(toString());
        }

        @Override
        public String toString() {
            return "maintenance_" + key;
        }
    }

    /**
     * Supported widgit in Word template.
     */
    public enum Widget {
        TEXT {
            public String apply(String value) {
                return "{{" + value + "}}";
            }
        },
        TABLE {
            public String apply(String value) {
                return "{{#" + value + "}}";
            }
        },
        PICTURE {
            public String apply(String value) {
                return "{{@" + value + "}}";
            }
        },
        DYNAMIC {
            public String apply(String value) {
                return "";
            }
        };

        public abstract String apply(String value);
    }
}
