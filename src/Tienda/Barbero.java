package Tienda;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Barbero extends Thread {

	private Queue<Cliente> cola = new LinkedList<>(); // Hacemos una cola para los clientes en espera
	private Semaphore semaforoSala = new Semaphore(1); // Semaforo de la sala de espera
	private Semaphore barbero = new Semaphore(1); // Semaforo del barbero
	boolean atendiendo = false; // Esta variable nos indica si el barbero está ocupado con un cliente
	Cliente clienteAtendido; // Para acceder al cliente que actualmente está siendo atendido
	Random random = new Random();
	int numeroAleatorio;
	boolean tiendaAbierta = true;
	boolean cerrado = false;
	//// AÑADIDO////
	// Delimitamos un tiempo x en el que la barbería esta abierta
	final long tiempoLimite = System.currentTimeMillis() + 30000;

	// Para dar un nombre a los clientes
	int i = 1;

	//// AÑADIDO////
	// Variables para el informe del barbero
	private int numeroDeClientesPerdidos = 0;
	private int numeroDeClientesAtendidos = 0;

	public Barbero() {
		// TODO Auto-generated constructor stub
	}

	// Creamos el método principal que se ejecuta al iniciar el hilo del barbero
	public void run() {
		try {

			// Creamos el bucle principal que se ejecuta mientras la tienda esté abierta y
			// no haya pasado el tiempo límite
			while (System.currentTimeMillis() < tiempoLimite && tiendaAbierta) {

				System.out.println("*************************************************");
				System.out.println("En la sala de espera hay " + cola.size() + " clientes");

				// Si la cola está vacía y el barbero no está atendiendo, el barbero se
				// dueeeerrrrme
				if (cola.isEmpty() && !atendiendo) {
					System.out.println("El barbero esta ZZZZzzzzzz....");
				}

				//// AÑADIDO////
				// Generamos un número aleatorio para controlar frecuencia de la llegada de
				//// clientes
				numeroAleatorio = random.nextInt(3) + 1;
				if (numeroAleatorio == 2 || numeroAleatorio == 3) {
					Cliente cliente = new Cliente("Cliente " + i);
					i++;
					sleep(1000);
					// Iniciamos al cliente
					cliente.start();

					System.out.println("Cliente " + cliente.getName() + " llega a la barbería.");
					// Activamos el semáforo de la sala de espera
					semaforoSala.acquire();

					// Si la sala de espera no está llena, el cliente entra a la sala
					if (cola.size() < 5) {
						System.out.println("Cliente " + cliente.getName() + " entra a la sala de espera.");
						// y lo añadimos a la cola
						cola.add(cliente);
						sleep(1000);

					} else {
						// Si la sala está llena, el cliente se va y se interrumpe su hilo
						System.out.println(
								":( ---> Cliente " + cliente.getName() + " se va, la sala de espera está llena");
						numeroDeClientesPerdidos++;
						cliente.interrupt(); 

					}
				}
                //Creamos otro bucle para atender a los clientes mientras la tienda está abierta
				while (tiendaAbierta) {
					
                    // Si ha pasado el tiempo pero quedan clientes por atender mostramos el mensaje
					if (System.currentTimeMillis() > tiempoLimite && !cerrado) {
						System.out.println("CERRADO, pero aun hay clientes en la sala de espera por atender...");
						System.out.println("------------------------------------------------------------------");
						cerrado = true;
					}
                    // Si no se está atendiendo a un cliente, el barbero atiende al siguiente
					if (!atendiendo) {
						//Ya ahora se pone en modo atender
						atendiendo = true;
						if (!cola.isEmpty()) {
							//Atiende al primero que entró
							clienteAtendido = cola.poll();
							sleep(1000);
							// y le damos paso
							barbero.acquire();
							System.out.println("Barbero atiende a cliente " + clienteAtendido.getName());

						}

					}
					
					//// AÑADIDO////
					// Generamos un número aleatorio para controlar el "tiempo" que el barbero está con cada cliente
					numeroAleatorio = random.nextInt(3) + 1;
					if (numeroAleatorio == 1) {
						try {
							System.out.println("!!Barbero termina con cliente " + clienteAtendido.getName() + "!!");
						} catch (NullPointerException e) {
							System.out.print("");
						}
						
						numeroDeClientesAtendidos++;
					    // Liberamos el semáforo del barbero
						barbero.release();
						
						//Y el booleano para indicar que ya está disponible para atender
						atendiendo = false;
					}
					// Si ha pasado el tiempo límite y en la sala de espera no queda nadie el barbero se va a su casa.
					if (System.currentTimeMillis() > tiempoLimite && cola.isEmpty()) {
						tiendaAbierta = false;
						System.out.println("___El Barbero se va a su casa a dormir otra vez ZZZZzzzz.....___");
						
						//// AÑADIDO////
						//Imprimims un informe de clientes atendidos y perdidos.
						System.out.println("\n\nINFORME\n--------------\n- Se han perdido un total de "
								+ numeroDeClientesPerdidos + " clientes.\n- El barbero ha atendido un total de "
								+ numeroDeClientesAtendidos + " clientes.");
					}
					// Si no ha pasado el tiempo límite, sale del bucle principal
					if (System.currentTimeMillis() < tiempoLimite) {
						break;
					}
				}
				semaforoSala.release();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
