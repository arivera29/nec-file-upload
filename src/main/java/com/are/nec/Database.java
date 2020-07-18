package com.are.nec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private Connection conn = null;
    private Statement stm;

    public Database() {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 

        try {
            // db parameters
            String url = "jdbc:mysql://localhost:3306/data";
            // create a connection to the database
            
            conn = DriverManager.getConnection(url,"root","");
            conn.setAutoCommit(false);
            
            System.out.println("Conexion establecida");
            
        
        } catch (Exception e) {
            System.out.println("Error Exception:" + e.getMessage());
        
        } finally {
            
        }
    }

    public Connection getConn() {
        return conn;
    }

    public void close() {
        System.out.println("Cerrando base de datos");
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error. " + e.getMessage());
            }
        }
    }
    
    public java.sql.ResultSet executeQuery(String sql) throws SQLException {
        stm = conn.createStatement();
        return stm.executeQuery(sql);
    }
    
    public java.sql.ResultSet executeQuery(java.sql.PreparedStatement pst) throws SQLException {
        return pst.executeQuery();
    }

    public int executeUpdate(String sql) throws SQLException {
        stm = conn.createStatement();
        return stm.executeUpdate(sql);
    }
    
    public int executeUpdate(java.sql.PreparedStatement pst) throws SQLException {
        return pst.executeUpdate();
    }

}