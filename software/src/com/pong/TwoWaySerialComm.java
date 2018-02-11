package com.pong;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    void connect(String incomingPort, String outcomingPort) throws Exception {
        //INCOMING

        for (Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); )
            System.out.println(e.nextElement().getName());
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(incomingPort);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Incoming port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                InputStream in = serialPort.getInputStream();
                reader = new SerialReader(in, velocity);
                //start reader thread
                new Thread(reader).start();
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        //OUTCOMING
        /*
        CommPortIdentifier portIdentifierOut = CommPortIdentifier.getPortIdentifier(outcomingPort);
        if (portIdentifierOut.isCurrentlyOwned()) {
            System.out.println("Error: Incoming port is currently in use");
        } else {
            CommPort commPort = portIdentifierOut.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                OutputStream out = serialPort.getOutputStream();
                writer = new SerialWriter(out);
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }*/
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
                        temp = "";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
            } catch (IOException ex) {

            }
        }
    }
}