/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.are.nec;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aimerrivera
 */
public class ProcessFile {

    private Database db;
    private String id_interface = null;
    private String read_date = null;

    public ProcessFile(Database db) {
        this.db = db;
    }

    public void process(String filename) throws FileNotFoundException, IOException {
        String cadena;
        FileReader f = new FileReader(filename);
        BufferedReader b = new BufferedReader(f);
        int linea= 1;
        while ((cadena = b.readLine()) != null) {
            if (linea == 1) {
                String[] header1 = cadena.split(",");
                if (header1.length == 8) {
                    String c_name = header1[0];
                    String c_ip_addres = header1[1];
                    String c_type = header1[2];
                    String c_interface = header1[3];
                    //String c_mode1 = header1[4];
                    //String c_mode2 = header1[5];
                    //String c_duration = header1[6];
                    read_date = header1[7];  // Fecha de captura de información
                    
                    System.out.println("Device IP: " + c_ip_addres);
                    System.out.println("Device name: " + c_name);
                    System.out.println("Device Type: " + c_type);
                    
                    System.out.println("Registrando en base de datos...");
                    try {
                        System.out.println("Create record Device and Interface");
                        createDeviceAndInterface(c_name, c_ip_addres, c_type, c_interface);
                       
                    } catch (SQLException ex) {
                        Logger.getLogger(ProcessFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    
                }else {
                    break;  // no es un archivo a subir
                }
                
            }else if (linea > 2) {
                String[] record = cadena.split(",");
                if (record.length == 49 ) {
                    String time_stamp = record[0];
                    String HCRxetherStatsOctets = record[1];
                    String HCTxetherStatsOctets = record[2];
                    String RxetherStatsPkts = record[3];
                    
                    try {
                        
                        insertRecord(time_stamp, HCRxetherStatsOctets, HCTxetherStatsOctets, RxetherStatsPkts);
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(ProcessFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }else {
                    System.out.println("Longitud de la fila no corresponde: " + record.length);
                }
                
            }
          
            
            
            linea++;
        }
        b.close();
        

    }
    
    
    public void insertRecord(String time_stamp,String HCRxetherStatsOctets, 
            String HCTxetherStatsOctets, String RxetherStatsPkts) throws SQLException {
        
       if (id_interface != null) {
           
           String sql = "INSERT INTO data_eth_15min (interfaces_id,read_date,read_time,hcrxether_stats_octets,hctxether_stats_octets,rxether_stats_pkts) VALUES (?,?,?,?,?,?)";
           java.sql.PreparedStatement pst = db.getConn().prepareStatement(sql);
           pst.setString(1,id_interface);
           pst.setString(2,read_date);
           pst.setString(3,time_stamp);
           pst.setLong(4, Long.parseLong(HCRxetherStatsOctets));
           pst.setLong(5,Long.parseLong(HCTxetherStatsOctets));
           pst.setLong(6,Long.parseLong(RxetherStatsPkts));
           
           if (db.executeUpdate(pst) > 0) {
               db.getConn().commit();
           }
           
           
       } 
        
        
    }
    
    
    public void createDeviceAndInterface(String c_name, String c_ip_address, String c_type, String c_interface) throws SQLException {
        String sql = "SELECT id,ip_address FROM devices WHERE ip_address = ?";
        java.sql.PreparedStatement pst = db.getConn().prepareStatement(sql);
        pst.setString(1, c_ip_address);
        java.sql.ResultSet rs = db.executeQuery(pst);
        if (rs.next()) {
            
            sql = "SELECT id,name FROM interfaces WHERE name=? and device_id=?";
            java.sql.PreparedStatement pst1 = db.getConn().prepareStatement(sql);
            pst1.setString(1, c_interface);
            pst1.setString(2, rs.getString("id"));
            java.sql.ResultSet rs1 = db.executeQuery(pst1);
            if (!rs1.next()) {
                id_interface = UUID.randomUUID().toString(); // Id Interface
                
                sql = "INSERT INTO interfaces (id,device_id,name) VALUES (?,?,?)";
                java.sql.PreparedStatement pst2 = db.getConn().prepareStatement(sql);
                pst2.setString(1, id_interface);
                pst2.setString(2, rs.getString("id"));
                pst2.setString(3, c_interface);
                
                if (db.executeUpdate(pst2) > 0) {
                    // Se guardo bien
                    db.getConn().commit();
                }
                
            }else {
                id_interface = rs1.getString("id");
                
            }
            
            
        }else { // No Existe device
            
            String id_device = UUID.randomUUID().toString();  // Id Device
            
            sql = "INSERT INTO devices (id,name,ip_address,type) VALUES (?,?,?,?)";
            java.sql.PreparedStatement pst1 = db.getConn().prepareStatement(sql);
            pst1.setString(1, id_device);
            pst1.setString(2, c_name);
            pst1.setString(3, c_ip_address);
            pst1.setString(4, c_type);
            
            if (db.executeUpdate(pst1) > 0) {
                
                id_interface = UUID.randomUUID().toString(); // Id Interface
                
                sql = "INSERT INTO interfaces (id,device_id,name) VALUES (?,?,?)";
                java.sql.PreparedStatement pst2 = db.getConn().prepareStatement(sql);
                pst2.setString(1, id_interface);
                pst2.setString(2, id_device);
                pst2.setString(3, c_interface);
                
                if (db.executeUpdate(pst2) > 0) {
                    // Se guardo bien
                    db.getConn().commit();
                }
                
            }
            
            
        }
        
        
    }

}
