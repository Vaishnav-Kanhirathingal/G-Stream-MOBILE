# G-Stream-MOBILE

## What is cloud gaming?

Cloud gaming is a service which gives its user the ability to play high-end games on any device using a server on the internet that does all the necessary computing for the game. The idea is to have the game run on a remote machine and have the user interact with this machine through an app to play the game. The server and device then communicate on what to be shared between them. The data usually contains what controller buttons the user pressed, where the joystick is, the frame to be displayed to give visual on the gaming device.

## What does this project aims at?

This project is an attempt to localize cloud gaming services. The way this project achieves this is by running high-end games on the user’s personal computer preferably a windows machine and streaming that game to their mobile device. The mobile app has joystick and game-pad buttons which are mapped to key-presses and mouse movement. The server receives this data and interprets it to perform necessary actions. To make sure the user can see the gameplay, The server would also stream the display to the mobile device. The localized implementation would run on a local network. This means that internet isn’t necessary to run the service. The network hops would take place locally between the mobile device and the PC through a LAN connection. This means the connection would be established using LAN IP addresses. The application uses a QR code to establish connection between the server (preferably a Windows machine) and the client (mobile device). The QR code containes the IP address of the server and the port numbers for different services such as port for gameplay screen sharing, joystick controls, etc. After establishing a connection, the user can launch the game of their choice and play as required. The strength and stability of the connection depends on the Wi-Fi connection strength and whether the network is already saturated with load. A free network would provide a lower latency and more stable connection.

## Guide

todo...