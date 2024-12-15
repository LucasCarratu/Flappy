package com.cate.flappy.net;

public abstract class RedUtiles {
    public static ClienteFlappy cliente;
    public static ServidorFlappy server;

    public static int ganador = -1;

    public static final int puerto = 54123;

    public static int idCliente;


    public static void empezarServidor(){
        if(server == null){
            server = new ServidorFlappy();
            server.start();
        }
    }

    public static void empezarCliente(){
        idCliente = -1;
        ganador = -1;
        if(cliente != null && !cliente.fin){
            cliente.fin();
            cliente.enviarMensaje("conectar");
            return;
        }
        cliente = new ClienteFlappy();
        cliente.start();
        cliente.enviarMensaje("conectar");
    }


    public static void terminarRed() {
        if(cliente != null){
            cliente.terminarCliente();
        }
        if(server != null){
            server.terminarServer();
        }
    }
}