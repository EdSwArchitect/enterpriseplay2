# akka.play
I'm playing around with the AKKA framework. The "main" is an "ingest" application that has 2 components. The
first reads a UDP socket for certain syslog data, parses it, and sends it to an ESP streaming model. The second reads
a TCP/IP socket for web proxy data, parses it, and sends it to an ESP streaming model.

When building, ./gradlew assemble, builds everything of course. ./gradlew copyDependencies is used to pull down all
of the libraries files on which the application is dependent. Since this is SAS ESP, you have to put the ESP API
jar into your local Maven repository.

To run the full application, do the following in this order:
1. Run start_xml_server.sh
2. Run start_esp_adapter.sh
3. Run ingest.sh

start_xml_server.sh - starts the ESP XML server
start_esp_adapter.sh - starts the two adapters that listen to the output windows and writes the data to disk
ingest.sh - starts the application to read UDP and TCP data.

It will stop after 10 minutes. Still in draft, so you will have to CTRL-C to make sure it stops.

