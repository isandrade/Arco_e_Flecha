package threads;

import main.JogoArcoFlecha;

public class ThreadDesenho extends Thread {

	JogoArcoFlecha jogo;
	
	public ThreadDesenho(JogoArcoFlecha jogo) {
		this.jogo = jogo;
	}
	
	@Override
	public void run() {
		while(!jogo.isFim()){
			jogo.repaint();
			try {
				Thread.sleep(this.jogo.getTempoThreadDesenho());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}