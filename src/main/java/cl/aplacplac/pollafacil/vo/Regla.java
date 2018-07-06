package cl.aplacplac.pollafacil.vo;

/**
 * Clase con valores a sumar por cada fase del campeonato
 * 
 * @author fgonzalez
 *
 */
public class Regla {
	// La fase de la regla
	private Fase fase;
	// Los puntos obtenidos por acertar a resultado
	private Short puntosResultado;
	// Los puntos obtenidos por acertar a marcador
	private Short puntosMarcador;
	// Los puntos obtenidos por acertar a equipo clasificado
	private Short puntosEquipoClasificado;

	/**
	 * Constructor de clase
	 */
	public Regla() {
	}

	/**
	 * @return La fase
	 */
	public Fase getFase() {
		return fase;
	}

	/**
	 * @param fase
	 *            La nueva fase
	 */
	public void setFase(Fase fase) {
		this.fase = fase;
	}

	/**
	 * @return los puntosResultado
	 */
	public Short getPuntosResultado() {
		return puntosResultado;
	}

	/**
	 * @param puntosResultado
	 *            los nuevos puntosResultado
	 */
	public void setPuntosResultado(Short puntosResultado) {
		this.puntosResultado = puntosResultado;
	}

	/**
	 * @return los puntosMarcador
	 */
	public Short getPuntosMarcador() {
		return puntosMarcador;
	}

	/**
	 * @param puntosMarcador
	 *            los nuevos puntosMarcador
	 */
	public void setPuntosMarcador(Short puntosMarcador) {
		this.puntosMarcador = puntosMarcador;
	}

	/**
	 * @return el equipoClasificado
	 */
	public Short getPuntosEquipoClasificado() {
		return puntosEquipoClasificado;
	}

	/**
	 * @param equipoClasificado
	 *            el nuevo equipoClasificado
	 */
	public void setPuntosEquipoClasificado(Short equipoClasificado) {
		this.puntosEquipoClasificado = equipoClasificado;
	}

}
