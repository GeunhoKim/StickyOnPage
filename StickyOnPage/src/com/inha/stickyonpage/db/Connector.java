package com.inha.stickyonpage.db;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


/**
 *   Low level client, Thrift connection wrapper
 */
public class Connector {
        TTransport tr = new TSocket("165.246.44.92", 9160);
        
        // returns a new connection to our keyspace
        public Cassandra.Client connect() throws TException, InvalidRequestException {
                TFramedTransport tf = new TFramedTransport(tr);
                TProtocol proto = new TBinaryProtocol(tf);
                Cassandra.Client client = new Cassandra.Client(proto);
                tr.open();
                client.set_keyspace("sop_db_1");

                return client;
        }
        
        public Cassandra.Client connect(String keyspace) throws TException, InvalidRequestException {
                TFramedTransport tf = new TFramedTransport(tr);
                TProtocol proto = new TBinaryProtocol(tf);
                Cassandra.Client client = new Cassandra.Client(proto);
                tr.open();
                client.set_keyspace(keyspace);

                return client;
        }
        
        public void close() {
                tr.close();
        }
}