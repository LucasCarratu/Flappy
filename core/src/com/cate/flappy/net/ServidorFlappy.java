package com.cate.flappy.net;


import com.cate.flappy.Counter.Bird;
import com.cate.flappy.game.screens.server.ServerWorld;
import com.cate.flappy.game.screens.singleplayer.WorldGame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServidorFlappy extends Thread {

    private DatagramSocket socket;
    private boolean fin = false;
    private int conectados = 0;
    private int maximo = 2; // Se pueden conectar más jugadores en Flappy Bird
    private JugadorRed[] jugadores;
    public boolean iniciaJuego;

    public ServidorFlappy() {
        jugadores = new JugadorRed[maximo];
        try {
            socket = new DatagramSocket(RedUtiles.puerto);
            System.out.println("Servidor Flappy iniciado");
        } catch (SocketException e) {
            fin = true;
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!fin) {
            byte[] datos = new byte[1024];
            DatagramPacket dp = new DatagramPacket(datos, datos.length);
            try {
                socket.receive(dp);
                procesarMensaje(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData()).trim();
        System.out.println("Mensaje: " + msg);
        String[] mensajeEvento = msg.split("#");

        int nroJugador = -1;
        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i] == null) continue;
            if (dp.getPort() == jugadores[i].puerto && dp.getAddress().equals(jugadores[i].ip)) {
                nroJugador = i;
                break;
            }
        }

        switch (mensajeEvento[0]) {
            case "conectar":
                if (conectados < maximo) {
                    jugadores[conectados] = new JugadorRed(dp.getAddress(), dp.getPort());
                    enviarMensaje("conexion#" + conectados, jugadores[conectados].ip, jugadores[conectados].puerto);
                    conectados++;
                    if (conectados == maximo) {
                        enviarMensaje("empezar");
                        iniciaJuego = true;
                    }
                }
                System.out.println("Jugador conectado: " + dp.getAddress() + " " + dp.getPort());
                break;

            case "volar":
                if (nroJugador == -1) return;
                // Lógica para indicar que el jugador nroJugador voló
                System.out.println("Jugador " + nroJugador + " voló");
                // Aquí deberías integrar la lógica que sincroniza el movimiento del jugador en el servidor
                if(ServerWorld.arrBirds.get(nroJugador).state == Bird.STATE_NORMAL) {
                    ServerWorld.arrBirds.get(nroJugador).volar();
                }

                break;

            default:
                System.out.println("Evento no reconocido: " + mensajeEvento[0]);
                break;
        }
    }

    public void enviarMensaje(String msg, InetAddress ipDestino, int puerto) {
        byte[] mensaje = msg.getBytes();
        try {
            DatagramPacket dp = new DatagramPacket(mensaje, mensaje.length, ipDestino, puerto);
            socket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String msg) {
        //System.out.println("Enviado: "+msg);
        byte[] mensaje = msg.getBytes();
        try {
            for (JugadorRed jugador : jugadores) {
                if (jugador != null) {
                    DatagramPacket dp = new DatagramPacket(mensaje, mensaje.length, jugador.ip, jugador.puerto);
                    socket.send(dp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminarJuego(int jugadorGanador) {
        enviarMensaje("fin#" + jugadorGanador);
        iniciaJuego = false;
        conectados = 0;
        jugadores = new JugadorRed[maximo];
    }

    public void terminarServer() {
        fin = true;
        socket.close();
    }
}
