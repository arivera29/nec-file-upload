package com.are.nec;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessFilesDirectory {

    private String directory;
    Database db = null;
    ProcessFile pf = null;

    public ProcessFilesDirectory(String directory) {
        this.directory = directory;
    }

    public void process() {
        db = new Database();
        if (db.getConn() != null) {
            
            
            
            pf = new ProcessFile(db);
        
            if (!directory.isEmpty()) {
                listarDirectorio(new File(directory));
            } else {
                System.out.println("Directorio no valido");

            }
            
            db.close();
        
        }else {
            System.out.println("Error al conectarse con el servidor");
        }

    }

    public void listarDirectorio(File directorio) {
        if (directorio.exists()) {
            File[] ficheros = directorio.listFiles();
            for (int x = 0; x < ficheros.length; x++) {
                String ruta = ficheros[x].getPath();
                System.out.println("Leyendo fichero: " + ruta);
                if (ficheros[x].isFile()) {
                    if (ruta.contains("15min-ETH")) {
                        System.out.println("Procesando fichero: " + ruta);
                        try {
                            pf.process(ruta);
                        } catch (IOException ex) {
                            Logger.getLogger(ProcessFilesDirectory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else if (ficheros[x].isDirectory()) {
                    listarDirectorio(ficheros[x]);
                }
            }
        }

    }

    public FilenameFilter FilterFiles(final String filter) {
        return new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.lastIndexOf('.') > 0) {
                    // get last index for '.' char
                    int lastIndex = name.lastIndexOf('.');

                    // get extension
                    String str = name.substring(lastIndex);

                    // match path name extension
                    if (str.equals(".rmon") && name.contains(filter)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

}
