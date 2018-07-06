package cl.aplacplac.pollafacil.vo;

/**
 * Clase que representa un puntaje
 * 
 * @author fgonzalez
 *
 */
public class Puntaje {
	// Puntos por resultado
	private Short puntosResultado;
	// Puntos por margador
	private Short puntosMarcador;
	// Puntos por equipo clasificado
	private Short puntosEquiposClasificados;
	// Fase del puntaje
	private Fase fase;

	/**
	 * Constructor de clase
	 * 
	 * @param fase
	 *            La fase del puntaje
	 */
	public Puntaje(Fase fase) {
		this.fase = fase;
		this.puntosResultado = 0;
		this.puntosMarcador = 0;
		this.puntosEquiposClasificados = 0;
	}

	/**
	 * 
	 * @return La fase del Puntaje en palabras
	 */
	public String getFaseString() {
		return fase.toString();
	}

	/**
	 * @return La fase
	 */
	public Fase getFase() {
		return fase;
	}

	/**
	 * @param fase
	 *            la nueva fase
	 */
	public void setFase(Fase fase) {
		this.fase = fase;
	}

	/**
	 * @return Los puntos obtenidos por resultado
	 */
	public Short getPuntosResultado() {
		return puntosResultado;
	}

	/**
	 * @param puntosResultado
	 *            Los nuevos puntos por resultado
	 */
	public void setPuntosResultado(Short puntosResultado) {
		this.puntosResultado = puntosResultado;
	}

	/**
	 * @return Los puntos por marcador
	 */
	public Short getPuntosMarcador() {
		return puntosMarcador;
	}

	/**
	 * @param puntosMarcador
	 *            Los nuevos puntos por marcador
	 */
	public void setPuntosMarcador(Short puntosMarcador) {
		this.puntosMarcador = puntosMarcador;
	}

	/**
	 * @return Los puntos por equipo clasificado
	 */
	public Short getPuntosEquiposClasificados() {
		return puntosEquiposClasificados;
	}

	/**
	 * @param puntosEquiposClasificados
	 *            Los nuevos puntos por equipo clasificado
	 */
	public void setPuntosEquiposClasificados(Short puntosEquiposClasificados) {
		this.puntosEquiposClasificados = puntosEquiposClasificados;
	}

	/**
	 * @return El total de puntos obtenidos en este puntaje
	 */
	public Short getTotal() {
		return (short) (getPuntosEquiposClasificados() + getPuntosMarcador() + getPuntosResultado());
	}
}
