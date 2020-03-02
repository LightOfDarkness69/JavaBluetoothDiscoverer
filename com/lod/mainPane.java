package com.lod;

import java.awt.*;
import java.io.IOException;
import java.nio.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.bluetooth.*;

/**
 * This class implements GUI initialization, checking LocalDevice info, starting infinite loop of searching
 * for new Bluetooth devices in the area of LocalDevice.
 */

public class mainPane extends JPanel {

    //language to use ("rus"/"eng")
    private String language = "rus";
    private BluetoothFinder bf;
    private JLabel findLabel;
    private JTextArea devices;
    private JTextArea devInfo;
    private static Font bigFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    private static Border border = BorderFactory.createLineBorder(Color.BLACK, 12);



    //Initialization of the GUI
    public mainPane() {
        setBackground(new Color(243, 202, 32));
        setLayout(new FlowLayout(1, 5, 10));

        //init Devices text area
        devices = new JTextArea(21, 22);
        devices.setFont(bigFont);
        devices.setEditable(false);
        JScrollPane devScroller = new JScrollPane(devices, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        devScroller.setBorder(border);

        //init LocalDevice info text area
        devInfo = new JTextArea(21, 24);
        devInfo.setFont(bigFont);
        devInfo.setEditable(false);
        JScrollPane devInfoScroller = new JScrollPane(devInfo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        devInfoScroller.setBorder(border);

        add(devScroller);
        add(devInfoScroller);

        //creating new BluetoothFinder for local device initialization and bluetooth-device search
        bf = new BluetoothFinder();
    }

    protected class BluetoothFinder implements Runnable {
        //Object represents LocalDevice
        private LocalDevice myLocalDevice;
        //Object for bluetooth discovery
        private DiscoveryAgent myDiscoveryAgent;
        //Listener device discovery and completion of a searching for devices
        private DiscoveryListener myDiscoveryListener;
        //List of all devices that have been discovered
        private ArrayList<RemoteBluetoothDevice> remoteDevicesList = new ArrayList<>();


        public BluetoothFinder() {
           try {
               initLocalDevInfo();
           } catch(BluetoothStateException bluetoothE) {
               devInfo.append("Что-то не так с блютузом. Он включен?");
           }

           initInquiry();
        }

        private void initLocalDevInfo() throws BluetoothStateException {
            myLocalDevice = LocalDevice.getLocalDevice();
            devInfo.append(Labeles.LOCALDEVICA_INFO.getValue(language));
            devInfo.append(Labeles.LOCALDEVICE_NAME.getValue(language) + myLocalDevice.getFriendlyName() + "\n");
            devInfo.append(Labeles.LOCALDEVICE_ADDRESS.getValue(language) + myLocalDevice.getBluetoothAddress() + "\n");
            devInfo.append(Labeles.LOCALDEVICE_TYPE.getValue(language) + DeviceClasses.classes.get(Integer.toString(myLocalDevice.getDeviceClass().getMajorDeviceClass())).getValue(language) + "\n");
            devInfo.append(Labeles.LOCALDEVICE_SUBTYPE.getValue(language) + DeviceClasses.classes.get(Integer.toString(myLocalDevice.getDeviceClass().getMajorDeviceClass()) + Integer.toString(myLocalDevice.getDeviceClass().getMinorDeviceClass())).getValue(language) + "\n");
            devInfo.append(Labeles.LOCALDEVICE_SERVICES.getValue(language) + getServices(myLocalDevice.getDeviceClass().toString()) + "\n");
            devInfo.append(Labeles.LOCALDEVICE_STATUS.getValue(language) + checkDiscoverable(myLocalDevice.getDiscoverable()).getValue(language) + "\n");
            devInfo.append(Labeles.LOCALDEVICE_MODE.getValue(language) + checkIfRunning(LocalDevice.isPowerOn()).getValue(language) + "\n");

            devInfo.setFont(bigFont);
        }

        private void initInquiry() {
            devices.append(Labeles.DEVICES.getValue(language));
            Thread inquiryThread = new Thread(this, "inquiryThread");
            myDiscoveryAgent = myLocalDevice.getDiscoveryAgent();

            myDiscoveryListener = new DiscoveryListener() {
                @Override
                public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
                    System.out.println("S");
                    String devAddress = remoteDevice.getBluetoothAddress();
                    String devName = "";
                    String devType = "";
                    String devServices = "";
                    try {
                       devName = remoteDevice.getFriendlyName(true);
                    } catch (IOException ioe) {
                        devName = Labeles.REMOTEDEVICE_NAMEUNKNOWN.getValue(language);
                    }

                    new RemoteBluetoothDevice(devName, devAddress, devType, devServices);

                    devices.setText(null);
                    System.out.println(remoteDevicesList.size());
                    for (RemoteBluetoothDevice dev : remoteDevicesList) {
                        ArrayList<String> data = dev.getData();
                        devices.append(Labeles.REMOTEDEVICE_NAME.getValue(language) + data.get(0) + "\n");
                        devices.append(Labeles.REMOTEDEVICE_ADDRESS.getValue(language) + data.get(1) + "\n");

                        devices.append("******\n");
                    }

                }

                @Override
                public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {

                }

                @Override
                public void serviceSearchCompleted(int i, int i1) {

                }

                @Override
                public void inquiryCompleted(int i) {
                    System.out.println("Searching is completed!");
                    synchronized (BluetoothFinder.class) {
                        BluetoothFinder.class.notifyAll();
                    }
                }

            };

            inquiryThread.start();
        }

        public void run() {
            try {
                while (true) {
                    myDiscoveryAgent.startInquiry(DiscoveryAgent.GIAC, myDiscoveryListener);

                    synchronized (BluetoothFinder.class) {
                        BluetoothFinder.class.wait();
                    }

                }
            } catch (BluetoothStateException bluetoothStateE) {
                devices.append("AAAAAAAAAAAAAAAAAAAAAAAAAAA!");
            } catch (InterruptedException it) {

            }
        }

        private class RemoteBluetoothDevice {
            protected String name;
            protected String address;
            protected String type;
            protected String services;

            public RemoteBluetoothDevice(String name, String address, String type, String services) {
                this.name = name;
                this.address = address;
                this.type = type;
                this.services = services;

                checkUniqueness();
            }

            public void checkUniqueness() {
                boolean isPresented = false;
                for (RemoteBluetoothDevice dev : remoteDevicesList) {
                    if (dev.equals(this)) {
                        isPresented = true;
                    }
                }
                if (!isPresented) {
                    remoteDevicesList.add(this);
                }
            }

            public boolean equals(Object obj) {
                if (obj == null)
                    return false;

                if (obj instanceof RemoteBluetoothDevice) {
                    RemoteBluetoothDevice anotherBD = (RemoteBluetoothDevice) obj;
                    if (anotherBD.name.equals(this.name) && anotherBD.address.equals(this.address)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            public ArrayList<String> getData() {
               return new ArrayList<String>(Arrays.asList(name, address, type, services));
            }

        }


        //getting services out of whole Info
        private String getServices(String list) {
            String[] myList = list.split("[()]", 2);
            String ans = "";
            for (int i = 0; i < myList[1].length() - 1; i++) {
                ans += myList[1].charAt(i);
            }
            return ans;
        }


        private Labeles checkDiscoverable(int num) {
            if (num != 0x00) {
                return Labeles.LOCALDEVICE_DISCOVERABLE;
            } else {
                return Labeles.LOCALDEVICE_NOTDISCOVERABLE;
            }
        }


        private Labeles checkIfRunning(boolean running) {
            if (running) {
                return Labeles.LOCALDEVICE_RUNNING;
            } else {
                return Labeles.LOCALDEVICE_NOTRUNNING;
            }
        }
    }


}
