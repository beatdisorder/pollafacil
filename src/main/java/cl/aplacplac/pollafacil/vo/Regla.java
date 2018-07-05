package cl.aplacplac.pollafacil.vo;

public class Regla {
	private Fase fase;
	private Short puntosResultado;
	private Short puntosMarcador;
	private Short puntosEquipoClasificado;

	public Regla() {
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
	 * @return the equipoClasificado
	 */
	public Short getPuntosEquipoClasificado() {
		return puntosEquipoClasificado;
	}

	/**
	 * @param equipoClasificado
	 *            the equipoClasificado to set
	 */
	public void setPuntosEquipoClasificado(Short equipoClasificado) {
		this.puntosEquipoClasificado = equipoClasificado;
	}

}
