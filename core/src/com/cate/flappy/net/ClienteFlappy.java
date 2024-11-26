package com.cate.flappy.net;

import com.cate.flappy.game.screens.ClientScreen;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

public class ClienteFlappy extends Thread {

    private DatagramSocket socket;
    public boolean fin = false;
    private InetAddress ipServer;
    private boolean conectado = false;
    public int jugadorId;

    public enum EstadoCliente {
        ESPERANDO,
        JUGANDO,
        FIN
    }

    public EstadoCliente estado = EstadoCliente.ESPERANDO;

    public ClienteFlappy() {
        try {
            socket = new DatagramSocket();
            ipServer = InetAddress.getByName("127.0.0.1");
            System.out.println("Iniciar cliente");
        } catch (SocketException | UnknownHostException e) {
            fin = true;
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        do {
            byte[] datos = new byte[1024];
            DatagramPacket dp = new DatagramPacket(datos, datos.length);
            try {
                socket.receive(dp);
                procesarMensaje(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!fin);
        socket.close();
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData()).trim();
        System.out.println("Mensaje recibido: " + msg);
        String[] mensajeCompuesto = msg.split("#");

        if (mensajeCompuesto[0].equals("conexion")) {
            System.out.println("Conectado al servidor");
            conectado = true;
            ipServer = dp.getAddress();
            jugadorId = Integer.parseInt(mensajeCompuesto[1]);
            RedUtiles.idCliente = jugadorId;
            System.out.println("ID del jugador: " + jugadorId);
        }

        if (!conectado) return;

        switch (mensajeCompuesto[0]) {
            case "empezar": {
                estado = EstadoCliente.JUGANDO;
                System.out.println("El juego ha comenzado");
                break;
            }

            case "fin": {
                int ganadorId = Integer.parseInt(mensajeCompuesto[1]);
                estado = EstadoCliente.FIN;
                RedUtiles.ganador = ganadorId;
                System.out.println("Juego finalizado. Ganador: Jugador " + ganadorId);
                break;
            }

            case "posicion": {
                // posicion#id#x#y => La posición X e Y del jugador con ID id
                int id = Integer.parseInt(mensajeCompuesto[1]);
                float x = Float.parseFloat(mensajeCompuesto[2]);
                float y = Float.parseFloat(mensajeCompuesto[3]);
                ClientScreen.birds.get(id).setPosition(x - .3f, y - .25f);
                break;
            }
            case "pipe_create": {
                // pipe#x#y#type => Se crea una nueva tubería en la posición X, Y y de tipo type
                UUID pipeId = UUID.fromString(mensajeCompuesto[1]);
                float pipeX = Float.parseFloat(mensajeCompuesto[2]);
                float pipeY = Float.parseFloat(mensajeCompuesto[3]);
                int type = Integer.parseInt(mensajeCompuesto[4]);
                ClientScreen.addPipe(pipeX, pipeY, type, pipeId);
                break;
            }
            case "pipe_update": {
                UUID pipeId = UUID.fromString(mensajeCompuesto[1]);
                float pipeX = Float.parseFloat(mensajeCompuesto[2]);
                float pipeY = Float.parseFloat(mensajeCompuesto[3]);
                ClientScreen.updatePipe(pipeId, pipeX, pipeY);
                break;
            }
            case "pipe_delete": {
                UUID pipeId = UUID.fromString(mensajeCompuesto[1]);
                ClientScreen.pipes.remove(pipeId);
                break;
            }
            case "score": {
                int id = Integer.parseInt(mensajeCompuesto[1]);
                int score = Integer.parseInt(mensajeCompuesto[2]);
                ClientScreen.birds.get(id).score = score;
                break;
            }
            case "bird_dead": {

                int id = Integer.parseInt(mensajeCompuesto[1]);
                ClientScreen.birds.get(id).dead = true;
                break;
            }
            default:
                System.out.println("Evento no reconocido: " + mensajeCompuesto[0]);
                break;
        }
    }

    public void enviarMensaje(String msg) {
        byte[] mensaje = msg.getBytes();

        try {
            DatagramPacket dp = new DatagramPacket(mensaje, mensaje.length, ipServer, RedUtiles.puerto);
            socket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fin() {
        estado = EstadoCliente.ESPERANDO;
    }

    public void terminarCliente(){
        this.fin = true;
        socket.close();
    }
}

