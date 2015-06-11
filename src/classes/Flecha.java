package classes;

import java.awt.Point;

public class Flecha {

	private Point posicao;
	private static int larguraFlecha = 10;
	private static int alturaFlecha = 10;
	private static int velocidadeFlecha = 5;
	private Point[] caminho;
	private int indice;
	
	
	public Flecha(){
		this.posicao = new Point();
		caminho = new Point[Cenario.width];
		indice = 0;
	}
	
	public Flecha (Point p){
		this.posicao = p;
	}
	
	public Point getPosicao() {
		return posicao;
	}
	
	public void setPosicao(Point posicao) {
		this.posicao = posicao;
	}
	
	public static int getLarguraFlecha() {
		return larguraFlecha;
	}
	
	public static int getVelocidadeFlecha() {
		return velocidadeFlecha;
	}

	public static int getAlturaFlecha() {
		return alturaFlecha;
	}

	public static void setAlturaFlecha(int alturaFlecha) {
		Flecha.alturaFlecha = alturaFlecha;
	}

	public Point[] getCaminho() {
		return caminho;
	}

	public void setCaminho(Point[] caminho) {
		this.caminho = caminho;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}
	
}
