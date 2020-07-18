package com.are.nec;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Upload files to Database Server" );
        
        Database db = new Database();
        //db.connect();
        db.close();
        
        if (args.length == 0) {
            System.out.println("Directorio no recibido.");
            return;
        }

        System.out.println("Procesando directorio: " + args[0]);
        
        ProcessFilesDirectory obj = new ProcessFilesDirectory(args[0]);
        obj.process();
        
        System.out.println("Proceso finalizado...");
    }

}
