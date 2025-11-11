# INTRODUCTION

Over the summer I was super interested in learning how computers connected to each other. Naturally, I then learned about Java Sockets, which made those connections happen. This repo was me experimenting with Sockets and making a java messaging application fully in the terminal for communicating with other computers

## SETUP

simply clone the repo.
This repo shows off the a lot of different files the only important ones though are Server.java and Client.java. The others are just helper method used inside them so you can ignore them. I did push the .class bytecode files but obviously it's a good idea to recompile them on your computer before using them

## RUNNING THE PROGRAM
If you want to just run this on yoru computer to test it out, it works out of the box
Open up a window in the terminal and run:

javac Server.java
java Server

Afterwards, now that the server application is running its constantly looking for incoming client connections so open another terminal window and run:

javac Client.java
java Client

Now a connection was made, but sending texts through it no other client will see it since only one is connected so open a third terminal window and run:

java Client

The client code is already compiled so no need to recompile it unless you really want to. now you have a second client connected and you can send messages, which basically just go to the server and from there the server broadcasts to all the clients connected to it stored in an synchronizedlist which from my research is basically a safer list to use for this scenario.
