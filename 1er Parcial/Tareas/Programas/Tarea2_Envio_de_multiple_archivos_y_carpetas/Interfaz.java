import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Interfaz extends JFrame {
    private static final long serialVersionUID = 1L;
	public Interfaz() {
		setBounds(450, 150, 500, 300);
		setTitle("Envio de archivos");
		setResizable(false);

		panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new BorderLayout());
		panelSuperior = new JPanel();
		panelInferior = new JPanel();
		elegirArchivo = new JButton("Elegir archivo");
        enviarArchivo = new JButton("Enviar");
        enviarCarpeta = new JButton("Enviar Carpeta");
        //enviarCarpeta.setEnabled(false);
        //conectar = new JButton("Conectar");
		estado = new JLabel("Estado: Desconectado");
		porcentajeE = new JLabel("Porcentaje: ");
        texto = new JTextArea(50, 100);
        texto.setEditable(false);
        texto.append("Archivo:\t\tTama\u00F1o:");
        misArchivos = new ArrayList<>();


        elegirArchivo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                eleccionArchivo();
            }
        });

        enviarArchivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enviarArchivos();
            }
        });

        enviarCarpeta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                directory = new JFileChooser();
                directory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                directory.requestFocus();
                int r = directory.showOpenDialog(Interfaz.this);
                if (r == JFileChooser.APPROVE_OPTION) {
                    carpetas(directory.getSelectedFile(), "" + directory.getCurrentDirectory());
                }
            }
        });

		panelSuperior.setLayout(new BorderLayout());
		panelSuperior.add(estado, BorderLayout.NORTH);
		panelSuperior.add(porcentajeE, BorderLayout.CENTER);
		panelInferior.add(elegirArchivo);
        panelInferior.add(enviarArchivo);
        panelInferior.add(enviarCarpeta);
        //panelInferior.add(conectar);
		panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
		panelPrincipal.add(texto, BorderLayout.CENTER);
		panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
		add(panelPrincipal);


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
    }
    
    public void eleccionArchivo() {
        file = new JFileChooser();
        file.requestFocus();
        int r = file.showOpenDialog(Interfaz.this);
        if(r == JFileChooser.APPROVE_OPTION) {
            File f = file.getSelectedFile();
            misArchivos.add(new Archivo(f.getName(), f.length(), f.getAbsolutePath()));
            texto.append("\n" + f.getName() + "\t\t" + f.length());
        }
    }

    public void enviarArchivos() {
        enviarArchivo.setEnabled(false);
        try {
            for(Archivo a : misArchivos) {
                cl = new Socket(HOST, PUERTO);
                estado.setText("Estado: Conectado");
                dos = new DataOutputStream(cl.getOutputStream());
                System.out.println(a.getNombre() + "," + a.getTamanio() + "," + a.getPath());
                long e = 0;
                int n = 0; 
                int porcentaje = 0;
                    dis = new DataInputStream(new FileInputStream(a.getPath()));
                    dos.writeUTF(a.getNombre());
                    dos.flush();
                    dos.writeLong(a.getTamanio());
                    dos.flush();
                    while (e < a.getTamanio()) {
                        byte[] b = new byte[2000];
                        n = dis.read(b);
                        e += n;
                        dos.write(b, 0, n);
                        dos.flush();
                        porcentaje = (int) ((e*100)/a.getTamanio());
                        System.out.print("\rEnviando el " + porcentaje + "%");
                        porcentajeE.setText("Porcentaje: " + porcentaje + "%");
                    }
                    System.out.println("\nArchivo Enviado");
                dis.close();
                dos.close();
                cl.close();
            }
            misArchivos.clear();
            estado.setText("Estado: Desconectado");
            texto.setText("");
        } catch(Exception e1) { 
            estado.setText("Estado: Desconectado"); 
            e1.printStackTrace(); 
         } 
    }

    public void carpetas(File carpeta, String destino) {
        if(destino.equalsIgnoreCase(""))
            destino = carpeta.getName();
        else 
            destino = destino + "//" + carpeta.getName();
        
        for(File f : carpeta.listFiles()) {
            if(f.isDirectory()) { carpetas(f, destino); }
            else { 
                misArchivos.add(new Archivo(f.getName(), f.length(), f.getAbsolutePath())); 
                texto.append("\n" + f.getName() + "\t\t" + f.length());
            }
        }
    }

	public static void main(String[] args) {
		new Interfaz();
	}

	private JPanel panelPrincipal;
	private JPanel panelSuperior;
	private JPanel panelInferior;
    private JButton elegirArchivo, enviarArchivo;
    private JButton enviarCarpeta;
	private JButton conectar;
	private JLabel estado, porcentajeE;
	private JTextArea texto;
	final int PUERTO = 9000;
	final String HOST = "127.0.0.1";
    private Socket cl;
    private JFileChooser file;
    private JFileChooser directory;
    private ArrayList <Archivo> misArchivos;
    private DataOutputStream dos;
    private DataInputStream dis;
}
