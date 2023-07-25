package org.uom.cse2023.request;

public enum RequestType {
    ACK,        // acknowledgement
    BSC,        // Bootstrap Server Command
    DUMMY,      // Dummy request
    ERROR,
    GOSSIP,
    GOSSIPOK,
    HEARTBEAT,
    JOIN,
    JOINOK,
    LEAVE,
    LEAVEOK,
    SER,
    SEROK,
    REG,
    REGOK,
    UNREG,
    UNROK
}
