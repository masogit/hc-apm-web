package webapp.framework.context;

import org.hibernate.dialect.PostgreSQL82Dialect;

public class PostgreSQL9DialectWithoutFK extends PostgreSQL82Dialect {

    public PostgreSQL9DialectWithoutFK(){
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {

        return " alter " + foreignKey[0] + " set default null ";
    }
}
