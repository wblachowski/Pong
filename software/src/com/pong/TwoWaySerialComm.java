package com.pong;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class TwoWaySerialComm
{
    Surface.Velocity velocity;
    public TwoWaySerialComm(Surface.Velocity velocity)
    {
        super();
        this.velocity = velocity;
    }

    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                (new Thread(new SerialReader(in,velocity))).start();
                (new Thread(new SerialWriter(out))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    /** */
    public static class SerialReader implements Runnable
    {
        InputStream in;
        Surface.Velocity velocity;
        public SerialReader (InputStream in, Surface.Velocity velocity)
        {
            this.in = in;
            this.velocity=velocity;
        }

        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                String temp="";
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    if(temp.length()<10){
                        temp+= new String(buffer,0,len);

                    }else {
                        System.out.println("." + temp + ".");
                        String[] parts = temp.split("\r\n");
                        if (parts.length > 2) {
                            try {
                                System.out.println("'" + parts[parts.length - 2] + "'");
                                velocity.value = -1 * Integer.parseInt(parts[parts.length - 2]);
                            } catch (Exception ex) {
                            }
                        }
                        temp = "";
                    }
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    /** */
    public static class SerialWriter implements Runnable
    {
        OutputStream out;

        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }

        public void run ()
        {
            try
            {
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    public static void main ( String[] args )
    {
        try
        {
            (new TwoWaySerialComm(null)).connect("COM4");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}