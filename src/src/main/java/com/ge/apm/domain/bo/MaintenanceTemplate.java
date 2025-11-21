package com.ge.apm.domain.bo;

import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.TableStyle;
import com.ge.apm.domain.bo.parser.SchemaParser;
import com.ge.apm.domain.pure.TableSchema;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
//        return new TableRenderData(
//                null,
//                new ArrayList<Object>() {{
//                    add("暂无");
//                }},
//                "no data",
//                8000);

        TableRenderData tableRenderData = Tables.create(Rows.create("暂无"));
        TableStyle tableStyle = new TableStyle();
        tableStyle.setWidth(String.valueOf(8000));
        tableRenderData.setTableStyle(tableStyle);
        return tableRenderData;
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
            this.content.put(Component.PM_PRE_PICTURE_TABLE.toString(), Component.PM_PRE_PICTURE_TABLE.value());
            this.content.put(Component.PM_PRE_PICTURE0.toString(), Component.PM_PRE_PICTURE0.value());
            this.content.put(Component.PM_PRE_PICTURE1.toString(), Component.PM_PRE_PICTURE1.value());
            this.content.put(Component.PM_PRE_PICTURE2.toString(), Component.PM_PRE_PICTURE2.value());
            this.content.put(Component.PM_PRE_PICTURE3.toString(), Component.PM_PRE_PICTURE3.value());
            this.content.put(Component.PM_PRE_PICTURE4.toString(), Component.PM_PRE_PICTURE4.value());
            this.content.put(Component.PM_AFTER_PICTURE_TABLE.toString(), Component.PM_AFTER_PICTURE_TABLE.value());
            this.content.put(Component.PM_AFTER_PICTURE0.toString(), Component.PM_AFTER_PICTURE0.value());
            this.content.put(Component.PM_AFTER_PICTURE1.toString(), Component.PM_AFTER_PICTURE1.value());
            this.content.put(Component.PM_AFTER_PICTURE2.toString(), Component.PM_AFTER_PICTURE2.value());
            this.content.put(Component.PM_AFTER_PICTURE3.toString(), Component.PM_AFTER_PICTURE3.value());
            this.content.put(Component.PM_AFTER_PICTURE4.toString(), Component.PM_AFTER_PICTURE4.value());
            this.content.put(Component.ACK_SIGNATURE.toString(), Component.ACK_SIGNATURE.value());
            this.content.put(Component.WC_SIGNATURE.toString(), Component.WC_SIGNATURE.value());
            this.content.put(Component.PM_PICTURE0.toString(), Component.PM_PICTURE0.value());
            this.content.put(Component.PM_PICTURE1.toString(), Component.PM_PICTURE1.value());
            this.content.put(Component.PM_PICTURE2.toString(), Component.PM_PICTURE2.value());
            this.content.put(Component.PM_PICTURE3.toString(), Component.PM_PICTURE3.value());
            this.content.put(Component.PM_PICTURE4.toString(), Component.PM_PICTURE4.value());
            this.content.put(Component.PM_PICTURE5.toString(), Component.PM_PICTURE5.value());
            this.content.put(Component.PM_PICTURE6.toString(), Component.PM_PICTURE6.value());
            this.content.put(Component.PM_PICTURE7.toString(), Component.PM_PICTURE7.value());
            this.content.put(Component.PM_PICTURE8.toString(), Component.PM_PICTURE8.value());
            this.content.put(Component.PM_PICTURE9.toString(), Component.PM_PICTURE9.value());
            this.content.put(Component.PM_PICTURE10.toString(), Component.PM_PICTURE10.value());
            this.content.put(Component.PM_PICTURE11.toString(), Component.PM_PICTURE11.value());
            this.content.put(Component.PM_PICTURE12.toString(), Component.PM_PICTURE12.value());
            this.content.put(Component.PM_PICTURE13.toString(), Component.PM_PICTURE13.value());
            this.content.put(Component.PM_PICTURE14.toString(), Component.PM_PICTURE14.value());
            this.content.put(Component.PM_PICTURE15.toString(), Component.PM_PICTURE15.value());
            this.content.put(Component.PM_PICTURE16.toString(), Component.PM_PICTURE16.value());
            this.content.put(Component.PM_PICTURE17.toString(), Component.PM_PICTURE17.value());
            this.content.put(Component.PM_PICTURE18.toString(), Component.PM_PICTURE18.value());
            this.content.put(Component.PM_PICTURE19.toString(), Component.PM_PICTURE19.value());
            this.content.put(Component.PM_PICTURE20.toString(), Component.PM_PICTURE20.value());
            this.content.put(Component.PM_PICTURE21.toString(), Component.PM_PICTURE21.value());
            this.content.put(Component.PM_PICTURE22.toString(), Component.PM_PICTURE22.value());
            this.content.put(Component.PM_PICTURE23.toString(), Component.PM_PICTURE23.value());
            this.content.put(Component.PM_PICTURE24.toString(), Component.PM_PICTURE24.value());
            this.content.put(Component.PM_PICTURE25.toString(), Component.PM_PICTURE25.value());
            this.content.put(Component.PM_PICTURE26.toString(), Component.PM_PICTURE26.value());
            this.content.put(Component.PM_PICTURE27.toString(), Component.PM_PICTURE27.value());
            this.content.put(Component.PM_PICTURE28.toString(), Component.PM_PICTURE28.value());
            this.content.put(Component.PM_PICTURE29.toString(), Component.PM_PICTURE29.value());
            this.content.put(Component.PARTS_PICTURE0.toString(), Component.PARTS_PICTURE0.value());
            this.content.put(Component.PARTS_PICTURE1.toString(), Component.PARTS_PICTURE1.value());
            this.content.put(Component.PARTS_PICTURE2.toString(), Component.PARTS_PICTURE2.value());
            this.content.put(Component.PARTS_PICTURE3.toString(), Component.PARTS_PICTURE3.value());
            this.content.put(Component.PARTS_PICTURE4.toString(), Component.PARTS_PICTURE4.value());
            this.content.put(Component.ACCESSORY_SIGNATURE_PLACE_HOLDER.toString(), Component.ACCESSORY_SIGNATURE_PLACE_HOLDER.value());
            this.content.put(Component.ACK_SIGNATURE_PLACE_HOLDER.toString(), Component.ACK_SIGNATURE_PLACE_HOLDER.value());
            this.content.put(Component.WC_SIGNATURE_PLACE_HOLDER.toString(), Component.WC_SIGNATURE_PLACE_HOLDER.value());
            this.content.put(Component.PM_PICTURE_HOLDER0.toString(), Component.PM_PICTURE_HOLDER0.value());
            this.content.put(Component.PM_PICTURE_HOLDER1.toString(), Component.PM_PICTURE_HOLDER1.value());
            this.content.put(Component.PM_PICTURE_HOLDER2.toString(), Component.PM_PICTURE_HOLDER2.value());
            this.content.put(Component.PM_PICTURE_HOLDER3.toString(), Component.PM_PICTURE_HOLDER3.value());
            this.content.put(Component.PM_PICTURE_HOLDER4.toString(), Component.PM_PICTURE_HOLDER4.value());
            this.content.put(Component.PM_PICTURE_HOLDER5.toString(), Component.PM_PICTURE_HOLDER5.value());
            this.content.put(Component.PM_PICTURE_HOLDER6.toString(), Component.PM_PICTURE_HOLDER6.value());
            this.content.put(Component.PM_PICTURE_HOLDER7.toString(), Component.PM_PICTURE_HOLDER7.value());
            this.content.put(Component.PM_PICTURE_HOLDER8.toString(), Component.PM_PICTURE_HOLDER8.value());
            this.content.put(Component.PM_PICTURE_HOLDER9.toString(), Component.PM_PICTURE_HOLDER9.value());
            this.content.put(Component.PM_PICTURE_HOLDER10.toString(), Component.PM_PICTURE_HOLDER10.value());
            this.content.put(Component.PM_PICTURE_HOLDER11.toString(), Component.PM_PICTURE_HOLDER11.value());
            this.content.put(Component.PM_PICTURE_HOLDER12.toString(), Component.PM_PICTURE_HOLDER12.value());
            this.content.put(Component.PM_PICTURE_HOLDER13.toString(), Component.PM_PICTURE_HOLDER13.value());
            this.content.put(Component.PM_PICTURE_HOLDER14.toString(), Component.PM_PICTURE_HOLDER14.value());
            this.content.put(Component.PM_PICTURE_HOLDER15.toString(), Component.PM_PICTURE_HOLDER15.value());
            this.content.put(Component.PM_PICTURE_HOLDER16.toString(), Component.PM_PICTURE_HOLDER16.value());
            this.content.put(Component.PM_PICTURE_HOLDER17.toString(), Component.PM_PICTURE_HOLDER17.value());
            this.content.put(Component.PM_PICTURE_HOLDER18.toString(), Component.PM_PICTURE_HOLDER18.value());
            this.content.put(Component.PM_PICTURE_HOLDER19.toString(), Component.PM_PICTURE_HOLDER19.value());
            this.content.put(Component.PM_PICTURE_HOLDER20.toString(), Component.PM_PICTURE_HOLDER20.value());
            this.content.put(Component.PM_PICTURE_HOLDER21.toString(), Component.PM_PICTURE_HOLDER21.value());
            this.content.put(Component.PM_PICTURE_HOLDER22.toString(), Component.PM_PICTURE_HOLDER22.value());
            this.content.put(Component.PM_PICTURE_HOLDER23.toString(), Component.PM_PICTURE_HOLDER23.value());
            this.content.put(Component.PM_PICTURE_HOLDER24.toString(), Component.PM_PICTURE_HOLDER24.value());
            this.content.put(Component.PM_PICTURE_HOLDER25.toString(), Component.PM_PICTURE_HOLDER25.value());
            this.content.put(Component.PM_PICTURE_HOLDER26.toString(), Component.PM_PICTURE_HOLDER26.value());
            this.content.put(Component.PM_PICTURE_HOLDER27.toString(), Component.PM_PICTURE_HOLDER27.value());
            this.content.put(Component.PM_PICTURE_HOLDER28.toString(), Component.PM_PICTURE_HOLDER28.value());
            this.content.put(Component.PM_PICTURE_HOLDER29.toString(), Component.PM_PICTURE_HOLDER29.value());
            this.content.put(Component.PARTS_PICTURE_HOLDER0.toString(), Component.PARTS_PICTURE_HOLDER0.value());
            this.content.put(Component.PARTS_PICTURE_HOLDER1.toString(), Component.PARTS_PICTURE_HOLDER1.value());
            this.content.put(Component.PARTS_PICTURE_HOLDER2.toString(), Component.PARTS_PICTURE_HOLDER2.value());
            this.content.put(Component.PARTS_PICTURE_HOLDER3.toString(), Component.PARTS_PICTURE_HOLDER3.value());
            this.content.put(Component.PARTS_PICTURE_HOLDER4.toString(), Component.PARTS_PICTURE_HOLDER4.value());
            this.content.put(Component.MAINTENANCE_COMPLETION_SIGNATURE.toString(), Component.MAINTENANCE_COMPLETION_SIGNATURE.value());
            this.content.put(Component.MAINTENANCE_COMPLETION_SIGNATURE_TITLE.toString(), Component.MAINTENANCE_COMPLETION_SIGNATURE_TITLE.value());
            this.content.put(Component.ACCEPTANCE_SIGNATURE.toString(), Component.ACCEPTANCE_SIGNATURE.value());
            this.content.put(Component.ACCEPTANCE_SIGNATURE_TITLE.toString(), Component.ACCEPTANCE_SIGNATURE_TITLE.value());
            this.content.put(Component.MAINTENANCE_WORKFLOW.toString(), Component.MAINTENANCE_WORKFLOW.value());
            this.content.put(Component.COWORKER_HOURS_TABLE.toString(), Component.COWORKER_HOURS_TABLE.value());
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

    public enum CoworkerHours {
        COWORKER_USER_NAME("coworker_user_name"),
        START_TIME("start_time"),
        END_TIME("end_time"),
        MAN_HOURS("man_hours"),
        REMARK("remark");

        private final String key;

        CoworkerHours(String key) {
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
        WC_SIGNATURE("wc_signature", Widget.PICTURE),
        ACK_SIGNATURE("ack_signature", Widget.PICTURE),
        PM_PICTURE0("pm_picture0",Widget.PICTURE),
        PM_PICTURE1("pm_picture1",Widget.PICTURE),
        PM_PICTURE2("pm_picture2",Widget.PICTURE),
        PM_PICTURE3("pm_picture3",Widget.PICTURE),
        PM_PICTURE4("pm_picture4",Widget.PICTURE),
        PM_PICTURE5("pm_picture5",Widget.PICTURE),
        PM_PICTURE6("pm_picture6",Widget.PICTURE),
        PM_PICTURE7("pm_picture7",Widget.PICTURE),
        PM_PICTURE8("pm_picture8",Widget.PICTURE),
        PM_PICTURE9("pm_picture9",Widget.PICTURE),
        PM_PICTURE10("pm_picture10",Widget.PICTURE),
        PM_PICTURE11("pm_picture11",Widget.PICTURE),
        PM_PICTURE12("pm_picture12",Widget.PICTURE),
        PM_PICTURE13("pm_picture13",Widget.PICTURE),
        PM_PICTURE14("pm_picture14",Widget.PICTURE),
        PM_PICTURE15("pm_picture15",Widget.PICTURE),
        PM_PICTURE16("pm_picture16",Widget.PICTURE),
        PM_PICTURE17("pm_picture17",Widget.PICTURE),
        PM_PICTURE18("pm_picture18",Widget.PICTURE),
        PM_PICTURE19("pm_picture19",Widget.PICTURE),
        PM_PICTURE20("pm_picture20",Widget.PICTURE),
        PM_PICTURE21("pm_picture21",Widget.PICTURE),
        PM_PICTURE22("pm_picture22",Widget.PICTURE),
        PM_PICTURE23("pm_picture23",Widget.PICTURE),
        PM_PICTURE24("pm_picture24",Widget.PICTURE),
        PM_PICTURE25("pm_picture25",Widget.PICTURE),
        PM_PICTURE26("pm_picture26",Widget.PICTURE),
        PM_PICTURE27("pm_picture27",Widget.PICTURE),
        PM_PICTURE28("pm_picture28",Widget.PICTURE),
        PM_PICTURE29("pm_picture29",Widget.PICTURE),
        PM_PRE_PICTURE_TABLE("pm_pre_picture_table",Widget.TEXT),
        PM_PRE_PICTURE0("pm_pre_picture0",Widget.PICTURE),
        PM_PRE_PICTURE1("pm_pre_picture1",Widget.PICTURE),
        PM_PRE_PICTURE2("pm_pre_picture2",Widget.PICTURE),
        PM_PRE_PICTURE3("pm_pre_picture3",Widget.PICTURE),
        PM_PRE_PICTURE4("pm_pre_picture4",Widget.PICTURE),
        PM_AFTER_PICTURE_TABLE("pm_after_picture_table",Widget.TEXT),
        PM_AFTER_PICTURE0("pm_after_picture0",Widget.PICTURE),
        PM_AFTER_PICTURE1("pm_after_picture1",Widget.PICTURE),
        PM_AFTER_PICTURE2("pm_after_picture2",Widget.PICTURE),
        PM_AFTER_PICTURE3("pm_after_picture3",Widget.PICTURE),
        PM_AFTER_PICTURE4("pm_after_picture4",Widget.PICTURE),

        PARTS_PICTURE0("parts_picture0",Widget.PICTURE),
        PARTS_PICTURE1("parts_picture1",Widget.PICTURE),
        PARTS_PICTURE2("parts_picture2",Widget.PICTURE),
        PARTS_PICTURE3("parts_picture3",Widget.PICTURE),
        PARTS_PICTURE4("parts_picture4",Widget.PICTURE),
        ACCESSORY_SIGNATURE_PLACE_HOLDER("accessory_signature_place_holder", Widget.TEXT),
        ACK_SIGNATURE_PLACE_HOLDER("ack_signature_place_holder", Widget.TEXT),
        WC_SIGNATURE_PLACE_HOLDER("wc_signature_place_holder", Widget.TEXT),
        PM_PICTURE_HOLDER0("pm_picture_holder0",Widget.TEXT),
        PM_PICTURE_HOLDER1("pm_picture_holder1",Widget.TEXT),
        PM_PICTURE_HOLDER2("pm_picture_holder2",Widget.TEXT),
        PM_PICTURE_HOLDER3("pm_picture_holder3",Widget.TEXT),
        PM_PICTURE_HOLDER4("pm_picture_holder4",Widget.TEXT),
        PM_PICTURE_HOLDER5("pm_picture_holder5",Widget.TEXT),
        PM_PICTURE_HOLDER6("pm_picture_holder6",Widget.TEXT),
        PM_PICTURE_HOLDER7("pm_picture_holder7",Widget.TEXT),
        PM_PICTURE_HOLDER8("pm_picture_holder8",Widget.TEXT),
        PM_PICTURE_HOLDER9("pm_picture_holder9",Widget.TEXT),
        PM_PICTURE_HOLDER10("pm_picture_holder10",Widget.TEXT),
        PM_PICTURE_HOLDER11("pm_picture_holder11",Widget.TEXT),
        PM_PICTURE_HOLDER12("pm_picture_holder12",Widget.TEXT),
        PM_PICTURE_HOLDER13("pm_picture_holder13",Widget.TEXT),
        PM_PICTURE_HOLDER14("pm_picture_holder14",Widget.TEXT),
        PM_PICTURE_HOLDER15("pm_picture_holder15",Widget.TEXT),
        PM_PICTURE_HOLDER16("pm_picture_holder16",Widget.TEXT),
        PM_PICTURE_HOLDER17("pm_picture_holder17",Widget.TEXT),
        PM_PICTURE_HOLDER18("pm_picture_holder18",Widget.TEXT),
        PM_PICTURE_HOLDER19("pm_picture_holder19",Widget.TEXT),
        PM_PICTURE_HOLDER20("pm_picture_holder20",Widget.TEXT),
        PM_PICTURE_HOLDER21("pm_picture_holder21",Widget.TEXT),
        PM_PICTURE_HOLDER22("pm_picture_holder22",Widget.TEXT),
        PM_PICTURE_HOLDER23("pm_picture_holder23",Widget.TEXT),
        PM_PICTURE_HOLDER24("pm_picture_holder24",Widget.TEXT),
        PM_PICTURE_HOLDER25("pm_picture_holder25",Widget.TEXT),
        PM_PICTURE_HOLDER26("pm_picture_holder26",Widget.TEXT),
        PM_PICTURE_HOLDER27("pm_picture_holder27",Widget.TEXT),
        PM_PICTURE_HOLDER28("pm_picture_holder28",Widget.TEXT),
        PM_PICTURE_HOLDER29("pm_picture_holder29",Widget.TEXT),
        PARTS_PICTURE_HOLDER0("parts_picture_holder0",Widget.TEXT),
        PARTS_PICTURE_HOLDER1("parts_picture_holder1",Widget.TEXT),
        PARTS_PICTURE_HOLDER2("parts_picture_holder2",Widget.TEXT),
        PARTS_PICTURE_HOLDER3("parts_picture_holder3",Widget.TEXT),
        PARTS_PICTURE_HOLDER4("parts_picture_holder4",Widget.TEXT),
        MAINTENANCE_COMPLETION_SIGNATURE("maintenance_completion_signature", Widget.PICTURE),
        MAINTENANCE_COMPLETION_SIGNATURE_TITLE("maintenance_completion_signature_title", Widget.TEXT),
        ACCEPTANCE_SIGNATURE("acceptance_signature", Widget.PICTURE),
        ACCEPTANCE_SIGNATURE_TITLE("acceptance_signature_title", Widget.TEXT),
        MAINTENANCE_WORKFLOW("maintenance_workflow", Widget.TABLE),
        COWORKER_HOURS_TABLE("coworker_hours_table", Widget.TABLE),
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
        Site("site", Widget.TEXT),
        TYPE("type", Widget.TEXT),
        OEM("oem", Widget.TEXT),
        SERIAL("serial", Widget.TEXT),
        NUMBER("number", Widget.TEXT),
        QR("qr", Widget.TEXT),
        MAKER("maker", Widget.TEXT),
        INSTALL_DATE("installDate", Widget.TEXT),
        LOCATION_NAME("locationName", Widget.TEXT),
        PERIOD("period", Widget.TEXT),
        GROUP("group", Widget.TEXT),
        ;

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
        RESULT("result", Widget.TEXT),
        COMMENTS("comments", Widget.TEXT);

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
