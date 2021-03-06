package Models;

import Annotations.Entity;
import DAO.BasicDao;
import DB.ConnectionPool.BasicConnPool;
import DB.ConnectionPool.ConnectionPool2;
import RefelctionsWork.GetClasses;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class Database {

    public Set<TableModel> tables = new HashSet<>();
    private BasicDao dao = new BasicDao();
    private static ConnectionPool2 connectionPool = null;


    public Database() {
        for (Class clazz : GetClasses.getEntities()) {
            TableModel tableModel = new TableModel(clazz);
            tables.add(tableModel);

        }
        try {
            connectionPool = BasicConnPool
                    .create("jdbc:h2:tcp://localhost/~/test", "sa", "");
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public void printTablesNames() {
        for (TableModel table : tables) {
            System.out.println(table.getClazz().getSimpleName());
        }
    }

    public void printFields(TableModel tableModel) {
        tableModel.printColumns();
    }

    public void printFields(String tableName) {
        getTable(tableName).printColumns();
    }

    public static Connection accessPool() {
        return connectionPool.getConnection();
    }

    public static void releaseConn(Connection conn) {
        connectionPool.releaseConnection(conn);
    }

    // This doesn't make any sense why would i want this ?
    private TableModel getTable(TableModel tableModel) {
        for (TableModel table : tables) {
            if (table.equals(tableModel)) {
                return tableModel;
            }
        }
        return null;

    }

    public Set<TableModel> getTables() {
        return tables;
    }

    public TableModel getTable(String tableName) {
        for (TableModel table : getTables()) {
            if (table.getClazz().getSimpleName().equalsIgnoreCase(tableName)) {
                return table;
            }
        }
        return null;
    }

    public boolean createTable(TableModel table) {
        return dao.CreateTable(table);
    }


    // So my thinking is that they do not want to initialize every table
    public boolean createTable(String... tableNames) {
        int size = tableNames.length;
        for (int i = 0; i < size; i++) {
            dao.CreateTable(getTable(tableNames[i]));
        }
        return true;
    }

    // This method will create all the tables that were marked
    public boolean createAllTables() {

        int size = tables.size();
        for (int i = 0; i < size; i++) {
            dao.CreateTable(getTableModelFromTables(i));
        }


        return true;


    }

    private TableModel getTableModelFromTables(int x) {
        int counter = 0;
        for (TableModel table : tables) {
            if (counter == x) {

                return table;
            } else {
                counter++;
            }
        }
        return null;
    }


    public <T> boolean insertIntoTable(T obj, String tableName) {
        return dao.insertIntoTable(obj, getTable(tableName));
    }

    public <T> boolean insertIntoTable(T obj, TableModel table) {
        return dao.insertIntoTable(obj, table);
    }


    public int deleteFromTable(String tableName) {
        return dao.delete(getTable(tableName));
    }


    public int deleteFromTable(TableModel table) {
        return dao.delete(table);
    }

    public boolean insertIntoTable(String tableName, Field[] colNames, String[] colVals) {
        return dao.insertIntoTable(tableName, colNames, colVals);
    }

    public ResultSet showAll(TableModel tableModel) {
        return dao.showAll(tableModel);
    }

    public int updateTable(String tableName, String colName, String colVal, Field... fields) {
        return dao.updateTable(tableName, colName, colVal, fields);
    }

    public ResultSet readAllTable(TableModel table) {
        return dao.showAll(table);
    }

    public void readTable(TableModel table, Field... fields) {
        dao.readTable(table, fields);
    }

    public void printReadTable(TableModel table, Field... fields) {
        dao.printReadTable(table, fields);
    }

    public boolean dropTable(TableModel tableModel) {
        return dao.dropTable(tableModel);
    }

    public boolean dropAllTables() {
        int size = tables.size();
        for (int i = 0; i < size; i++) {
            dao.dropTable(getTableModelFromTables(i));
        }


        return true;
    }

    public boolean deleteByID(String tableName, Field[] fields, String[] values) {
        return dao.deleteById(tableName, fields, values);
    }

    public <T> boolean add(T obj) {


        return dao.insert(obj);


    }

    public <T> boolean create(T obj) {

        if (obj.getClass().isAnnotationPresent(Entity.class)) {
            return dao.create(new TableModel(obj.getClass()));
        } else {
            return dao.create(obj);
        }


    }

    public <T> int update(T obj, String[] colNames, String[] colVals) {
        return dao.update(obj, colNames, colVals);
    }

    public <T> int update(T obj) {
       return dao.update(obj);
    }

    public <T> int delete(T obj, String[] colNames, String[] colvals) {
        return dao.delete(obj, colNames, colvals);
    }

    public <T> int delete(T obj) {

        return dao.delete(obj);

    }

    public <T> boolean drop(T obj){
        return dao.drop(obj);
    }


    public <T> ResultSet read(T obj){return dao.read(obj);};

    public <T> ResultSet readAll(T obj){return dao.readAll(obj);};

    public <T> ResultSet readRow(T obj){return dao.readRow(obj);};

    public void printResultSet(ResultSet rs){
        System.out.println();
        try {
            System.out.println("Select from " + rs.getMetaData().getTableName(1));
            System.out.println(rs.getMetaData().getColumnLabel(1));
            if(rs != null){
                for(int i = 1; i <= rs.getMetaData().getColumnCount();i++){
                    System.out.print(rs.getMetaData().getColumnName(i) + "-------------");
                }
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
//                    System.out.printf("'%-10s' %n", rs.getString(i) );
                    System.out.print(rs.getString(i)+ "           ");
                }
                System.out.println();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*
        For the transaction i think all of the queries and method should be on the same thread an connection
        So im thinking of setting up a different DAO for transaction this way i can keep all of them on the same thread
        Make it be like transActionAdd() . . . Not sure yet though
     */
//    public boolean startTransaction(){
//        return dao.
//    }


}


