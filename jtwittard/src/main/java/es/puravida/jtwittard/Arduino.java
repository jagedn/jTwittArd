package es.puravida.jtwittard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.util.Enumeration;

public class Arduino implements FindTwitterListener, SerialPortEventListener {

	private static final String HANDSACKING = "jTwittard 1.0";

	boolean found = false;

	public Arduino() {

	}

	CommPortIdentifier portIdentifier = null;
	InputStream in = null;
	OutputStream out = null;
	LineNumberReader inReader;
	PrintWriter outWriter;

	public boolean initialize() {
		try {
			String portName = System.getProperty("usb.port");
			if (portName == null)
				portName = "COM3";

			CommPortIdentifier portId = null;
			portId = CommPortIdentifier.getPortIdentifier(portName);
			if (initializePort(portName, portId)) {
				return true;
			}
			
			System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
						.nextElement();
				if (initializePort(portName, currPortId)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean initializePort(String portName,
			CommPortIdentifier portIdentifier) {

		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
			return false;
		}

		CommPort commPort = null;
		try {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);
		} catch (PortInUseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		if (!(commPort instanceof SerialPort)) {
			commPort.close();
			return false;
		}

		SerialPort serialPort = (SerialPort) commPort;
		try {
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			commPort.close();
			return false;
		}

		try {
			Thread.currentThread().sleep(5*1000);
			in = serialPort.getInputStream();
			inReader = new LineNumberReader(new InputStreamReader(in));
			String handsaking = inReader.readLine();
			if (handsaking.startsWith(HANDSACKING) == false) {
				commPort.close();
				return false;
			}

			out = serialPort.getOutputStream();
			outWriter = new PrintWriter(new OutputStreamWriter(out));
			outWriter.println(HANDSACKING);
			outWriter.flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			commPort.close();
			return false;
		}

		found = true;

		return true;
	}

	@Override
	public void serialEvent(SerialPortEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean getFound() {
		return found;
	}

	public void hashtagFounded(int index, String hashtag, String author) {
		if (!found)
			return;
		try {
			out.write('A' + index);
			out.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
