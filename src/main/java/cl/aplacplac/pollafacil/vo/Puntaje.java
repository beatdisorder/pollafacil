package cl.aplacplac.pollafacil.vo;

public class Puntaje {
	private Short puntosResultado;
	private Short puntosMarcador;
	private Short puntosEquiposClasificados;
	private Fase fase;
	
	public Puntaje(Fase fase) {
		this.fase = fase;
		this.puntosResultado = 0;
		this.puntosMarcador = 0;
		this.puntosEquiposClasificados  = 0;
	}

	public String getFaseString() {
		return fase.toString();
	}

	
	/**
	 * @return the fase
	 */
	public Fase getFase() {
		return fase;
	}

	/**
	 * @param fase
	 *            the fase to set
	 */
	public void setFase(Fase fase) {
		this.fase = fase;
	}

	/**
	 * @return the puntosResultado
	 */
	public Short getPuntosResultado() {
		return puntosResultado;
	}

	/**
	 * @param puntosResultado
	 *            the puntosResultado to set
	 */
	public void setPuntosResultado(Short puntosResultado) {
		this.puntosResultado = puntosResultado;
	}

	/**
	 * @return the puntosMarcador
	 */
	public Short getPuntosMarcador() {
		return puntosMarcador;
	}

	/**
	 * @param puntosMarcador
	 *            the puntosMarcador to set
	 */
	public void setPuntosMarcador(Short puntosMarcador) {
		this.puntosMarcador = puntosMarcador;
	}

	/**
	 * @return the puntosEquiposClasificados
	 */
	public Short getPuntosEquiposClasificados() {
		return puntosEquiposClasificados;
	}

	/**
	 * @param puntosEquiposClasificados
	 *            the puntosEquiposClasificados to set
	 */
	public void setPuntosEquiposClasificados(Short puntosEquiposClasificados) {
		this.puntosEquiposClasificados = puntosEquiposClasificados;
	}

	/**
	 * @return the total
	 */
	public Short getTotal() {
		return (short) (getPuntosEquiposClasificados() + getPuntosMarcador() + getPuntosResultado());
	}
}
