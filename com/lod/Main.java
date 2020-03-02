package com.lod;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Bluetooth-finder app based on bluecove and javax.bluetooth library.
 * It implements two methods: LocalDevice analysis and finding other bluetooth-enabled devices.
 *
 * @author Dmitry Lyahnitskiy
 */

public class Main {

    public static void main(String[] args) {

        //Initialization of the frame
        JFrame frame = new JFrame("Bluetooth discoverer");
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new mainPane());
        frame.setResizable(false);
        frame.setVisible(true);

    }
}
