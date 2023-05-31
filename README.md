# G-Stream-MOBILE

Android client of the game streaming service project.

## What is cloud gaming?

Cloud gaming is a service which gives its user the ability to play high-end games on any device using a server on the internet that does all the necessary computing for the game. The idea is to have the game run on a remote machine and have the user interact with this machine through an app to play the game. The server and device then communicate on what to be shared between them. The data usually contains what controller buttons the user pressed, where the joystick is, the frame to be displayed to give visual on the gaming device.

## What does this project aims at?

This project is an attempt to localize cloud gaming services. The way this project achieves this is by running high-end games on the user’s personal computer preferably a windows machine and streaming that game to their mobile device. The mobile app has joystick and game-pad buttons which are mapped to key-presses and mouse movement. The server receives this data and interprets it to perform necessary actions. To make sure the user can see the gameplay, The server would also stream the display to the mobile device. The localized implementation would run on a local network. This means that internet isn’t necessary to run the service. The network hops would take place locally between the mobile device and the PC through a LAN connection. This means the connection would be established using LAN IP addresses. The application uses a QR code to establish connection between the server (preferably a Windows machine) and the client (mobile device). The QR code containes the IP address of the server and the port numbers for different services such as port for gameplay screen sharing, joystick controls, etc. After establishing a connection, the user can launch the game of their choice and play as required. The strength and stability of the connection depends on the Wi-Fi connection strength and whether the network is already saturated with load. A free network would provide a lower latency and more stable connection.

## Project Repos

[![Game - Stream android app icon](https://github.com/Vaishnav-Kanhirathingal/G-Stream-MOBILE/blob/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png?raw=true "[Game - Stream Mobile] - This app is responsible for sending control signals to the desktop side. It also displays gameplay streamed from the PC")](https://github.com/Vaishnav-Kanhirathingal/G-Stream-MOBILE)
[![Game - Stream desktop app icon](https://github.com/Vaishnav-Kanhirathingal/G-Stream-Desktop/blob/main/src/main/resources/app_icon_mipmap/mipmap-xxxhdpi/ic_launcher.png?raw=true "[Game - Stream Desktop] - This app is responsible for recieving control signals from the android side. It also streams gameplay to the android device")](https://github.com/Vaishnav-Kanhirathingal/G-Stream-Desktop)

## Guide

### Main Screen

![Screenshot_20230530_184138_com example g_stream](https://github.com/Vaishnav-Kanhirathingal/G-Stream-MOBILE/assets/94210466/9e932d86-bbc7-4cdc-9b52-b0f2c2af9a9e)

This is the main screen of the mobile application. Each button has different actions:

- Scan Button - Starts the scanning service to scan a QR-code
- Documentation Button - Opens the github documentation for the app
- Desktop App Button - Opens the github repository of the desktop app
- Android App Button - Opens the github repository of the android app

### Scan Screen

![Screenshot_20230530_190422_com example g_stream](https://github.com/Vaishnav-Kanhirathingal/G-Stream-MOBILE/assets/94210466/f2f441c5-c221-4c39-b383-52a8d892b81c)

Start the desktop application and point the camera to the QR-code generated on the desktop app. This establishes a connection between the two devices.

### Gameplay Screen

https://github.com/Vaishnav-Kanhirathingal/G-Stream-MOBILE/assets/94210466/7c821e31-b0d4-4cfd-bdbf-e1c02176a487

The above video showcases the gameplay on both the android and the Windows side. The above screen is for the android side and the below one is for the Desktop side. This represents the latency and quality of gameplay. The UI gamepads and trackpads can be used to control the game. You can set custom keymaps for each button in the desktop app to modify gameplay in the desktop application. The buttons also support long presses.
