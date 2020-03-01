package com.lod;

import java.awt.*;
import java.io.IOException;
import java.nio.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.Border;
import javax.bluetooth.*;


public class mainPane extends JPanel {
    private String language = "eng";

    private BluetoothFinder bf;

    private JLabel findLabel;
    private JTextArea devices;
    private JTextArea devInfo;
    private static Font bigFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    private static Border border = BorderFactory.createLineBorder(Color.BLACK, 12);



    public mainPane() {
        //init gui
        setBackground(new Color(243, 202, 32));
        setLayout(new FlowLayout(1, 5, 10));

        //init dev text area
        devices = new JTextArea(21, 22);
        devices.setFont(bigFont);
        devices.setEditable(false);
        JScrollPane devScroller = new JScrollPane(devices, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        devScroller.setBorder(border);

        //init dev info text area
        devInfo = new JTextArea(21, 24);
        devInfo.setFont(bigFont);
        devInfo.setEditable(false);
        JScrollPane devInfoScroller = new JScrollPane(devInfo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        devInfoScroller.setBorder(border);

        add(devScroller);
        add(devInfoScroller);

        bf = new BluetoothFinder();
    }

    protected class BluetoothFinder implements Runnable {
        private LocalDevice myLocalDevice;
        private Set<String> remoteDevicesAddress = new HashSet<>();
        private DiscoveryAgent myDiscoveryAgent;
        private DiscoveryListener myDiscoveryListener;

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
                    String devAddress = remoteDevice.getBluetoothAddress();
                    String devName = "";
                    try {
                       devName = remoteDevice.getFriendlyName(true);
                    } catch (IOException ioe) {
                        devName = Labeles.REMOTEDEVICE_NAMEUNKNOWN.getValue(language);
                    }

                    if (!remoteDevicesAddress.contains(devAddress)) {
                        devices.append("Name: " + devName + "\nAddress: " + devAddress + "\n");

                        devices.append("****************\n");

                    } else if (remoteDevicesAddress.contains(devAddress) && !devName.equals(Labeles.REMOTEDEVICE_NAMEUNKNOWN.getValue(language))) {
                        devices.append("Name: " + devName + "\nAddress: " + devAddress + "\n");

                        devices.append("***************\n");

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

                }

            };

            inquiryThread.start();
        }

        public void run() {

            while (true) {
                Object inquiryCompletedEvent = new Object();
                synchronized (inquiryCompletedEvent) {
                    boolean started = false;
                    try {
                        started = myDiscoveryAgent.startInquiry(DiscoveryAgent.GIAC, myDiscoveryListener);
                    } catch (BluetoothStateException bluetoothE) {

                    }

                }
            }
        }



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
