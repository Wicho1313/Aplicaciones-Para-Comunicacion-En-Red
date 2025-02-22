package logica;

import logica.busqueda.MD5Checksum;
import java.io.File;

public class OperacionesNodo {

    public OperacionesNodo(int puerto) {
        ruta = RUTA + puerto + "/";
    }

    public boolean buscarArchivo(String nombre) {
        File f = new File(ruta + nombre);
        return f.exists();
    }

    public String getChecksum(String nombre) throws Exception {
        return MD5Checksum.getMD5Checksum(ruta + nombre);
    }

    public static final String RUTA = "./Carpetas/";
    private String ruta = "";
}