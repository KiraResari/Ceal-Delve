The Chronicles of Ceal ~ Deeper Delving
(c)2019 by Kira Resari

How to play this game:

You have multiple options.

Either way, you first need to run a Delve Server, and then connect to it with a Delve Client.
How you combine these options is up to you

You have three options for running the Delve Server:
1.) Run Delve Server.exe (requires java version 1.8 or newer)
2.) Using cmd, navigate to Ceal Delve/bin and run the command `java server.DelveServer` (requires java version 1.8 or newer)
3.) Using cmd, navigate to Ceal Delve and run the command `docker-compose up` (requires Docker installed and running)

The Delve Client will run as a non-interactive console, displaying messages received and sent to the client

You have two options for running the Delve Client:
1.) Run Delve Client.exe (requires java version 1.8 or newer)
2.) Using cmd, navigate to Ceal Delve/bin and run the command `java client.DelveClient` (requires java version 1.8 or newer)

Once the Delve Client is running, you will be prompted to select whether you want to play locally, or over network.
If the Delve Server is running on the same machine as the Delve Server, simply select [L] Local Machine.
If the Delve Server is running on a different machine, select [N] and then enter the IP address of the machine that the Delve Server is running on
Be aware that when using Docker, the IP Addresses may vary due to Subnet Masks
For example, when Delve Server is running on Docker, it might display its IP Address as "172.18.0.2".
However, to locally connect to it using the network feature, you instead have to use the Subnet IP Address "10.0.75.2".

To leave the game, either close the windows, or if you're using cmd, press [Ctrl] + [C].
