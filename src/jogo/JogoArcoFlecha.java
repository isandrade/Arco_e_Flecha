package jogo;

import java.applet.*;
import java.awt.*;
import java.util.Random;

public class JogoArcoFlecha extends Applet implements Runnable {

	private static final long serialVersionUID = -9075169431413415047L;

	private volatile Thread mainThread = null;
	
	
	int pontos;
	boolean fim = false;
	
	
	// dados cenario
	int width = 400;
	int heigth = 300;

	// Dados do arqueiro
	Point seta;
	int larguraSeta = 20;

	// Dados baloes
	Point baloes[];
	Point baloesCaindo[];
	int larguraBalao = 20;
	int alturaBalao = 30;
	int qtdBaloes = 10;
	int qtdBaloesCaindo = 0;
	int velocidadeSubindo = 3;
	int velocidadeDescendo = 5;
	int areaInicialBalao = 120;// Posi��o X que os bal�es n�o ir�o ultrapassar na tela

	// Dados flechas
	Point flechas[];
	int qtdFlecha = 10;
	int larguraFlecha = 10;
	int flechasAtiradas = 0;
	int velocidadeFlecha = 5;

	// Imagens
	Image imgBalao = null;
	Image ImgBalaoFurado = null;
	
	// Posi��es iniciais de objetos
	int posXPontos = 0;
	int posXSeta = 0;
	

	public void init() {
		
		resize(width, heigth);
		baloes = new Point[qtdBaloes];
		baloesCaindo = new Point[qtdBaloes];
		flechas = new Point[qtdFlecha];
		seta = new Point(0, 0);
		pontos = 0;
		imgBalao = getImage(getCodeBase(), "jogo/balao.png");
		ImgBalaoFurado = getImage(getCodeBase(), "jogo/balaoFurado.png");
		criarBaloes();
	}

	@Override
	public void start() {

		if (mainThread == null) {
			mainThread = new Thread(this, "Main");
			mainThread.start();
		}
	}

	@Override
	public void run() {

		Thread myThread = Thread.currentThread();
		while (mainThread == myThread || !fim) {
			moverBaloes();
			moverBaloesCaindo();
			moverFlechas();
			verificarBaloes();
			verificarFim();
			repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		mainThread = null;
	}

	
	//Metodo que desenha na tela
	@Override
	public void paint(Graphics g) {
		
		//Desenhar bal�es subindo
		for (int i = 0; i < qtdBaloes; i++) {
			if (baloes[i] != null) {
				if (baloes[i].y < -alturaBalao) {
					baloes[i].y = heigth - 1;
				}
				desenharBalao(g, baloes[i].x, baloes[i].y, larguraBalao, alturaBalao);
			}
		}
		//Desenhar bal�es caindo
		for (int i = 0; i < qtdBaloesCaindo; i++) {
			if (baloesCaindo[i] != null) {
				desenharBalaoFurado(g, baloesCaindo[i].x, baloesCaindo[i].y, larguraBalao, alturaBalao);
			}
		}
		
		//Desenhar flechas
		for (int i = 0; i < flechasAtiradas; i++) {
			if (flechas[i] != null) {
				desenharFlecha(g, flechas[i].x, flechas[i].y);
			}
		}
		//Desenhar seta
		desenharSeta(g);
		//Exibit pontua��o atual
		mostrarPontos(g);
	}

	public void criarBaloes() {

		//Criar bal�es em posi��es aleatorias.
		Random num = new Random();
		int criados = 0;
		int x, y;
		boolean criar = true;

		while (criados < qtdBaloes) {
			x = num.nextInt(width);
			y = num.nextInt(heigth);

			// diminuir a probabilidade de que um bal�o fique em cima de outro:
			if (x > areaInicialBalao && x < width - larguraBalao) {
				if (criados == 0) {
					baloes[criados] = new Point(x, y);
					criados++;
				} else {
					criar = true;
					for (int i = 0; i < criados && criar; i++) {
						if (x >= baloes[i].x-larguraBalao && x <= baloes[i].x + larguraBalao) {
							if (y >= baloes[i].y-alturaBalao && y <= baloes[i].y + alturaBalao) {
								criar = false;
							}
						}
					}
					if (criar) {
						baloes[criados] = new Point(x, y);
						criados++;
						criar = false;
					}
				}
			}
		}
	}

	public void verificarBaloes() {
		
		//Verifica se a alguma flecha atingiu algum bal�o
		for (int i = 0; i < flechasAtiradas; i++) {
			if (flechas[i] != null) {
				for (int j = 0; j < qtdBaloes; j++) {
					if (baloes[j] != null) {
						if (((flechas[i].x + larguraFlecha >= baloes[j].x) && (flechas[i].x + larguraFlecha <= baloes[j].x
								+ larguraBalao))
								&& (flechas[i].y >= baloes[j].y && flechas[i].y <= baloes[j].y + alturaBalao)) {
							System.out.println("Acertou");
							baloesCaindo[qtdBaloesCaindo] = baloes[j];
							baloes[j] = null;
							pontos++;
							qtdBaloesCaindo++;
						}
					}
				}
			}
		}
	}
	
	public void verificarFim(){
		
		if(qtdBaloesCaindo >= qtdBaloes || flechasAtiradas >= qtdFlecha) {
			fim = true;
		}
	}

	/****************** M�todos respons�veis pelos eventos da tela **********************/
	
	public boolean mouseDown(Event evt, int x, int y) {
	
		// Evento ao clicar
		if (flechasAtiradas < qtdFlecha) {
			flechas[flechasAtiradas] = new Point(seta.x + larguraSeta, seta.y);
			flechasAtiradas++;
		}
		return true;
	}

	public boolean mouseMove(Event evt, int x, int y) {
		
		// Evento ao mover o mouse
		seta = new Point(posXSeta, y);
		return true;
	}
	
	
	/****************************** M�todos respons�veis por movimentar objetos ***********************************/
	
	public void moverBaloes() {

		// Movimenta os bal�es
		for (int i = 0; i < qtdBaloes; i++) {
			if (baloes[i] != null) {
				// Faz o bal�o subir
				baloes[i] = new Point(baloes[i].x, baloes[i].y - velocidadeSubindo);
				// flag random para saber se o bal�o vai para a direita ou esquerda.
				boolean moverDireita = new Random().nextBoolean();
				if(moverDireita && baloes[i].x < width-larguraBalao){
					// Faz o bal�o ir para a direita
					baloes[i] = new Point(baloes[i].x+velocidadeSubindo-1, baloes[i].y);
				} else if(baloes[i].x > areaInicialBalao) {
					//Faz o bal�o ir para esquerda
					baloes[i] = new Point(baloes[i].x-velocidadeSubindo-1, baloes[i].y);
				}
			}
		}
	}
	
	public void moverBaloesCaindo() {

		//Movimenta os bal�es.
		for (int i = 0; i < qtdBaloesCaindo; i++) {
			if (baloesCaindo[i] != null) {
				if(baloesCaindo[i].y <= heigth - alturaBalao){
					//Se n�o chegou no fim continuar movendo
					baloesCaindo[i] = new Point(baloesCaindo[i].x, baloesCaindo[i].y + velocidadeDescendo);
				} else {
					//Se chegar no fim do cenario, deve-se remover o bal�o.
					baloesCaindo[i] = null;
				}
			}
		}
	}

	public void moverFlechas() {

		//Movimenta as flechas.
		for (int i = 0; i < flechasAtiradas; i++) {
			if (flechas[i] != null) {
				if (flechas[i].x < width) {
					flechas[i] = new Point(flechas[i].x + velocidadeFlecha, flechas[i].y);
				} else {
					flechas[i] = null;
				}
			}
		}
	}
	
	
	/**********************  M�todos respons�veis por desenhar na tela: *********************/
	
	public void mostrarPontos(Graphics g) {
		g.drawString("Pontos: " + pontos, posXPontos, heigth);
	}
	
	public void desenharSeta(Graphics g) {
		//Desenha uma seta (no lugar do arqueiro).
		g.drawLine(seta.x, seta.y, seta.x + larguraSeta, seta.y);
		g.drawLine(seta.x + larguraSeta - 5, seta.y - 5, seta.x + larguraSeta, seta.y);
		g.drawLine(seta.x + larguraSeta - 5, seta.y + 5, seta.x + larguraSeta, seta.y);
	}

	public void desenharBalao(Graphics g, int x1, int y1, int largura, int altura) {
		g.drawImage(imgBalao, x1, y1, this);
		// g.drawOval(x1, y1, largura, altura);//Desenha um circulo oval.
	}
	
	public void desenharBalaoFurado(Graphics g, int x1, int y1, int largura, int altura) {
		g.drawImage(ImgBalaoFurado, x1, y1, this);
		// g.drawOval(x1, y1, largura, altura);//Desenha um circulo oval.
	}

	public void desenharFlecha(Graphics g, int x, int y) {

		//Desenha uma flecha.
		g.drawLine(x, y, x + larguraFlecha, y);
		g.drawLine(x + larguraFlecha - 3, y - 3, x + larguraFlecha, y);
		g.drawLine(x + larguraFlecha - 3, y + 3, x + larguraFlecha, y);
	}

}