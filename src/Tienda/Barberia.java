package Tienda;

public class Barberia {
	public static void main(String[] args) {
		try {
			Barbero barbero = new Barbero();
			barbero.start();

			barbero.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
