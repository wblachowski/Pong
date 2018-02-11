package com.pong;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

public class TwoWaySerialComm {
    GameSurface.Velocity velocity;
    SerialReader reader;
    SerialWriter writer;

    public TwoWaySerialComm() {
        super();
    }

    public void setVelocity(GameSurface.Velocity velocity) {
        this.velocity = velocity;
        if(reader!=null)reader.setVelocity(velocity);
    }

    public void write(int value) {
        if (writer != null) {
            writer.write(value);
        }
    }

    void connect(String port) throws Exception {
        //INCOMING

        for (Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); )
            System.out.println(e.nextElement().getName());
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Incoming port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                reader = new SerialReader(in, velocity);
                writer = new SerialWriter(out);
                //start reader thread
                new Thread(reader).start();
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    /** */
    public static class SerialReader implements Runnable {
        InputStream in;
        GameSurface.Velocity velocity;

        public SerialReader(InputStream in, GameSurface.Velocity velocity) {
            this.in = in;
            this.velocity = velocity;
        }

        public void setVelocity(GameSurface.Velocity velocity) {
            this.velocity = velocity;
            velocity.value = 1;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                String temp = "";
                while ((len = this.in.read(buffer)) > -1) {
                    if (temp.length() < 10) {
                        temp += new String(buffer, 0, len);

                    } else {
                        System.out.println("." + temp + ".");
                        String[] parts = temp.split("\r\n");
                        if (parts.length > 2) {
                            try {
                                System.out.println("'" + parts[parts.length - 2] + "'");
                                if (velocity != null) {
                                    velocity.value = -1 * Integer.parseInt(parts[parts.length - 2]);
                                }
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                        checkForButtons(temp);
                        temp = "";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void checkForButtons(String buffer){
            for(int i=0;i<buffer.length();i++){
                if(Character.isLetter(buffer.charAt(i))){
                    Pong.getInstance().controllerButtonClicked(Character.toLowerCase(buffer.charAt(i)));
                }
            }
        }
    }

    /** */
    public static class SerialWriter {
        OutputStream out;

        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        public void write(int value) {
            try {
                this.out.write(value);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}