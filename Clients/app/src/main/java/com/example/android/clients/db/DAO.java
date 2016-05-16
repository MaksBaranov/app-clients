package com.example.android.clients.db;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

public class DAO<T> {
    private static final Map<Class<?>, Boolean> DAOS = new HashMap<Class<?>, Boolean>();

    private static enum Type {
        BLOB("BLOB", byte[].class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException{
                field.set(object, isNull ? null : cursor.getBlob(columnIndex));
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (byte[]) field.get(object));
            }
        },

        BOOLEAN("INTEGER", boolean.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.setBoolean(object, cursor.getInt(columnIndex) != 0);
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                values.put(columnName, field.getBoolean(object));
            }
        },

        BOOLEAN_OBJECT("INTEGER", Boolean.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : cursor.getInt(columnIndex) != 0);
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (Boolean) field.get(object));
            }
        },

        DOUBLE("REAL", double.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.setDouble(object, cursor.getDouble(columnIndex));
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                values.put(columnName, field.getDouble(object));
            }
        },

        DOUBLE_OBJECT("REAL", Double.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : cursor.getDouble(columnIndex));
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (Double) field.get(object));
            }
        },

        FLOAT("REAL", float.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.setFloat(object, cursor.getFloat(columnIndex));
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                values.put(columnName, field.getFloat(object));
            }
        },

        FLOAT_OBJECT("REAL", Float.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : cursor.getFloat(columnIndex));
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (Float) field.get(object));
            }
        },

        INT("INTEGER", int.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.setInt(object, cursor.getInt(columnIndex));
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                values.put(columnName, field.getInt(object));
            }
        },

        INT_OBJECT("INTEGER", Integer.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : cursor.getInt(columnIndex));
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (Integer) field.get(object));
            }
        },

        LONG("INTEGER", long.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.setLong(object, cursor.getLong(columnIndex));
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                values.put(columnName, field.getLong(object));
            }
        },

        LONG_OBJECT("INTEGER", Long.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : cursor.getLong(columnIndex));
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (Long) field.get(object));
            }
        },

        SHORT("INTEGER", short.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.setShort(object, cursor.getShort(columnIndex));
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                values.put(columnName, field.getShort(object));
            }
        },

        SHORT_OBJECT("INTEGER", Short.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : cursor.getShort(columnIndex));
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (Short) field.get(object));
            }
        },

        STRING("TEXT", String.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : cursor.getString(columnIndex));
            }

            @Override
            void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
                values.put(columnName, (String) field.get(object));
            }
        },

        DATETIME("INTEGER", Date.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : new Date(cursor.getLong(columnIndex)));
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                if (field.get(object) != null) {
                    values.put(columnName, ((Date) field.get(object)).getTime());
                }
                else if (withNulls) {
                    values.put(columnName, (Long) null);
                }
            }
        },

        ENUM("TEXT", Enum.class) {
            @Override
            void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException {
                field.set(object, isNull ? null : Enum.valueOf(field.getType().asSubclass(Enum.class), cursor.getString(columnIndex)));
            }

            @Override
            void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
                if (field.get(object) != null) {
                    values.put(columnName, ((Enum) field.get(object)).name());
                }
                else if (withNulls) {
                    values.put(columnName, (String) null);
                }
            }
        };

        abstract void setValueFromCursor(Object object, Field field, Cursor cursor, int columnIndex, boolean isNull) throws IllegalAccessException;

        void setValueToContentValues(Object object, Field field, ContentValues values, String columnName, boolean withNulls) throws IllegalAccessException {
            if (field.get(object) != null || withNulls) {
                setObjectToContentValues(object, field, values, columnName);
            }
        }

        void setObjectToContentValues(Object object, Field field, ContentValues values, String columnName) throws IllegalAccessException {
        }

        private static final Map<Class, Type> classToType = new HashMap<Class, Type>();

        static {
            for (Type value : Type.values()) {
                classToType.put(value.aClass, value);
            }
        }

        static Type getTypeForClass(Class aClass) {
            if (aClass.isEnum()) {
                return ENUM;
            }
            return classToType.get(aClass);
        }

        private final String sqlType;
        private final Class aClass;

        private Type(String sqlType, Class aClass) {
            this.sqlType = sqlType;
            this.aClass = aClass;
        }

        String getSqlType() {
            return sqlType;
        }

        boolean isNotNull() {
            return aClass.isPrimitive();
        }
    }

    private final Map<Field, String> fieldToColumn;
    private final Map<String, Field> columnToField;
    private final Map<String, Type> columnTypes;
    private final Map<Field, Type> fieldTypes;
    private final List<String> columns;
    private final Class<T> aClass;
    private final Field primaryKeyField;
    private final Uri contentUri;
    private final String tableName;

    public DAO(Class<T> aClass) {
        if (DAOS.containsKey(aClass)) {
            throw new IllegalStateException("More than one DAO for class " + aClass.getSimpleName());
        }
        DAOS.put(aClass, true);

        this.aClass = aClass;
        tableName = aClass.getAnnotation(Table.class).name();
        contentUri = DatabaseProvider.tableUri(tableName);
        fieldToColumn = new HashMap<Field, String>();
        columnToField = new HashMap<String, Field>();
        columnTypes = new HashMap<String, Type>();
        fieldTypes = new HashMap<Field, Type>();
        columns = new ArrayList<String>();
        Field primaryKeyField = null;

        for (Field field : aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column annotation = field.getAnnotation(Column.class);
                String columnName = annotation.name();
                if (columnName.length() == 0) {
                    columnName = field.getName().replaceAll("([A-Z])", "_$1").toLowerCase();
                }

                fieldToColumn.put(field, columnName);
                columnToField.put(columnName, field);
                columnTypes.put(columnName, Type.getTypeForClass(field.getType()));
                fieldTypes.put(field, Type.getTypeForClass(field.getType()));
                columns.add(columnName);

                if (annotation.primaryKey()) {
                    primaryKeyField = field;
                }
            }
        }

        if (primaryKeyField == null) {
            throw new IllegalStateException("No primary key column in DAO.Table class " + aClass.getSimpleName());
        }
        else if (!primaryKeyField.getType().equals(long.class)) {
            throw new IllegalStateException("Primary key column in DAO.Table class " + aClass.getSimpleName() + " is not of type 'long'");
        }

        this.primaryKeyField = primaryKeyField;
    }

    public T cursorToObject(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        try {
            T object = aClass.newInstance();
            for (String column : columnToField.keySet()) {
                Field field = columnToField.get(column);
                int columnIndex = cursor.getColumnIndexOrThrow(column);
                boolean isNull = cursor.isNull(columnIndex);

                columnTypes.get(column).setValueFromCursor(object, field, cursor, columnIndex, isNull);
            }
            return object;
        }
        catch (Exception e) {
            Log.d("DAO", "cursorToObject", e);
            return null;
        }
    }

    public ContentValues objectToContentValues(T object, boolean withNulls) {
        try {
            ContentValues values = new ContentValues();
            for (Field field : fieldToColumn.keySet()) {
                if (field.getAnnotation(Column.class).primaryKey()) {
                    continue;
                }
                String columnName = fieldToColumn.get(field);

                fieldTypes.get(field).setValueToContentValues(object, field, values, columnName, withNulls);
            }
            return values;
        } catch (Exception e) {
            Log.d("DAO", "objectToContentValues", e);
            return null;
        }
    }

    private String[] getColumns() {
        return columns.toArray(new String[columns.size()]);
    }

    public long insert(ContentResolver contentResolver, T o) {
        long result = ContentUris.parseId(contentResolver.insert(contentUri, objectToContentValues(o, false)));
       // EventBus.DB_BUS.event(EventBus.Receiver.DB_INSERT, o);
        return result;
    }

    public int update(ContentResolver contentResolver, T o) {
        try {
            final Uri uri = ContentUris.withAppendedId(contentUri, primaryKeyField.getLong(o));
            int result = contentResolver.update(uri, objectToContentValues(o, true), null, null);
            if (result > 0){
         //  EventBus.DB_BUS.event(EventBus.Receiver.DB_UPDATE, o);
            }
            return result;
        }
        catch (IllegalAccessException e) {
            Log.e("DAO", "Could not get primary key from object " + o, e);
            return 0;
        }
    }

    public int delete(ContentResolver contentResolver, T o) {
        try {
            final Uri uri = ContentUris.withAppendedId(contentUri, primaryKeyField.getLong(o));
            int result = contentResolver.delete(uri, null, null);
            if (result > 0){
             //   EventBus.DB_BUS.event(EventBus.Receiver.DB_DELETE, o);
            }
            return result;
        }
        catch (IllegalAccessException e) {
            Log.e("DAO", "Could not get primary key from object " + o, e);
            return 0;
        }
    }

    public CursorLoader getCursorLoader(Context context, String selection, String[] selectionArgs, String sortOrder) {
        return new CursorLoader(context, contentUri, getColumns(), selection, selectionArgs, sortOrder);
    }

    public List<T> query(ContentResolver contentResolver, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = contentResolver.query(contentUri, getColumns(), selection, selectionArgs, sortOrder);
        if (cursor == null) {
            return new ArrayList<T>();
        }
        List<T> result = new ArrayList<T>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorToObject(cursor));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public T queryById(ContentResolver contentResolver, long id) {
        Cursor cursor = contentResolver.query(ContentUris.withAppendedId(contentUri, id), getColumns(), null, null, null);
        if (cursor == null) {
            return null;
        }
        T object = null;
        if (cursor.moveToFirst()) {
            object = cursorToObject(cursor);
        }
        cursor.close();
        return object;
    }

    private void fieldToSql(Field field, StringBuilder result) {
        result.append(fieldToColumn.get(field))
                .append(' ')
                .append(fieldTypes.get(field).getSqlType());

        Column annotation = field.getAnnotation(Column.class);
        if (annotation.primaryKey()) {
            result.append(" PRIMARY KEY AUTOINCREMENT");
        }
        if (fieldTypes.get(field).isNotNull() || annotation.notNull()) {
            result.append(" NOT NULL");
        }
        if (annotation.unique()) {
            result.append(" UNIQUE");
        }
        if (annotation.defaultValue().length() != 0) {
            result.append(" DEFAULT ").append(annotation.defaultValue());
        }
        if (annotation.references().length() != 0) {
            result.append(" REFERENCES ").append(annotation.references());
        }
    }

    public String getTableCreationSql() {
        StringBuilder result = new StringBuilder("CREATE TABLE ");
        result.append(tableName).append(" (");
        boolean appendComma = false;

        for (Field field : fieldTypes.keySet()) {
            if (appendComma) {
                result.append(", ");
            }
            appendComma = true;

            fieldToSql(field, result);
        }

        result.append(")");
        return result.toString();
    }

    public List<String> getTableUpdateSql(int oldVersion, int newVersion) {
        List<String> result = new ArrayList<String>();

        for (Field field : fieldTypes.keySet()) {
            Column annotation = field.getAnnotation(Column.class);
            int sinceVersion = annotation.sinceVersion();
            if (sinceVersion > oldVersion && sinceVersion <= newVersion) {
                StringBuilder query = new StringBuilder("ALTER TABLE ");
                query.append(tableName).append(" ADD COLUMN ");
                fieldToSql(field, query);
                result.add(query.toString());
            }
        }

        return result;
    }

    public boolean shouldCreateTable(int oldVersion, int newVersion) {
        Table annotation = aClass.getAnnotation(Table.class);
        int sinceVersion = annotation.sinceVersion();
        return sinceVersion > oldVersion && sinceVersion <= newVersion;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Column {
        public String name() default "";
        public boolean primaryKey() default false;
        public boolean notNull() default false;
        public boolean unique() default false;
        public String defaultValue() default "";
        public String references() default "";
        public int sinceVersion() default 1;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Table {
        public String name();
        public int sinceVersion() default 1;
    }
}