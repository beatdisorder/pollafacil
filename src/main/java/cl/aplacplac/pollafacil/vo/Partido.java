package cl.aplacplac.pollafacil.vo;

import java.util.Calendar;
import java.util.Date;

/**
 * Clase que representa un partido dentro de las apuestas
 * 
 * @author fgonzalez
 *
 */
public class Partido {
	// Equipo local
	private Equipo local;
	// Equipo visita
	private Equipo visita;
	// Fecha del encuentro
	private Date fecha;
	// Estadio del encuentro
	private Estadio estadio;
	// Fase del encuentro
	private Fase fase;
	// Goles del equipo local
	private Short golesLocales;
	// Goles del equipo visita
	private Short golesVisita;
	// Penales del equipo local
	private Short penalesLocal;
	// Penales del equipo visita
	private Short penalesVisita;
	// Grupo del partido (si este es primera fase)
	private Grupo grupo;
	// Representa si es apuesta
	private boolean esApuesta;
	// En caso de ser apuesta este Partido representa el partido con resultado real
	private Partido resultadoReal;
	// En caso de apuesta representa los puntos ganados por este partido
	private short puntos;

	/**
	 * Constructor por defecto
	 */
	public Partido() {
	}

	/**
	 * Sobrecarga del constructor
	 * 
	 * @param esApuesta
	 *            Verdadero para apuesta
	 */
	public Partido(boolean esApuesta) {
		this.setEsApuesta(esApuesta);
	}

	/**
	 * M&eacute;todo que retorna el estado del Partido
	 * 
	 * @return EL estado 2: terminado 1: se juega hoy 0: no se ha jugado
	 */
	public Integer getEstado() {
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		Calendar fechaT = Calendar.getInstance();
		fechaT.setTime(getFecha());
		fechaT.add(Calendar.HOUR, 1);
		fechaT.add(Calendar.MINUTE, 40);
		if (today.after(fechaT)) {
			return 2;
		}
		if (isSameDay(today, fechaT)) {
			return 1;
		}
		return 0;
	}

	/**
	 * M&eacute;todro que
	 * 
	 * @param fecha1
	 * @param fecha2
	 * @return
	 */
	private boolean isSameDay(Date fecha1, Date fecha2) {
		Calendar fechaS = Calendar.getInstance();
		fechaS.setTime(fecha1);
		Calendar fechaT = Calendar.getInstance();
		fechaT.setTime(fecha2);
		return isSameDay(fechaS, fechaT);
	}

	private boolean isSameDay(Calendar fechaS, Calendar fechaT) {
		return (fechaS.get(Calendar.YEAR) == fechaT.get(Calendar.YEAR)
				&& fechaS.get(Calendar.DAY_OF_YEAR) == fechaT.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * M&eacute;todo que entrega el estado en palabras
	 * 
	 * @return El estado
	 */
	public String getStringEstado() {
		switch (getEstado()) {
		case 0:
			return "Proximamente";
		case 1:
			return "Hoy";
		case 2:
			return "Finalizado";
		}
		return "";
	}

	/**
	 * @return El equipo local
	 */
	public Equipo getLocal() {
		return local;
	}

	/**
	 * @param local
	 *            El nuevo equipo local
	 */
	public void setLocal(Equipo local) {
		this.local = local;
	}

	/**
	 * @return El equipo visita
	 */
	public Equipo getVisita() {
		return visita;
	}

	/**
	 * @param visita
	 *            El nuevo equipo visita
	 */
	public void setVisita(Equipo visita) {
		this.visita = visita;
	}

	/**
	 * @return La fecha del encuentro
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param fecha
	 *            La nueva fecha del encuentro
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return El estadio del encuentro
	 */
	public Estadio getEstadio() {
		return estadio;
	}

	/**
	 * @param estadio
	 *            el nuevo estadio del encuentro
	 */
	public void setEstadio(Estadio estadio) {
		this.estadio = estadio;
	}

	/**
	 * @return La fase del encuentro
	 */
	public Fase getFase() {
		return fase;
	}

	/**
	 * @param fase
	 *            La nueva fase del encuentro
	 */
	public void setFase(Fase fase) {
		this.fase = fase;
	}

	/**
	 * @return Los goles del equipo local
	 */
	public Short getGolesLocales() {
		return golesLocales;
	}

	/**
	 * @param golesLocales
	 *            Los nuevos goles del equipo local
	 */
	public void setGolesLocales(Short golesLocales) {
		this.golesLocales = golesLocales;
	}

	/**
	 * @return Los goles del equipo visita
	 */
	public Short getGolesVisita() {
		return golesVisita;
	}

	/**
	 * @param golesVisita
	 *            Los nuevos goles del equipo visita
	 */
	public void setGolesVisita(Short golesVisita) {
		this.golesVisita = golesVisita;
	}

	/**
	 * @return Los penales convertidos del equipo local
	 */
	public Short getPenalesLocal() {
		return penalesLocal;
	}

	/**
	 * @param penalesLocal
	 *            Los nuevos penales del equipo local
	 */
	public void setPenalesLocal(Short penalesLocal) {
		this.penalesLocal = penalesLocal;
	}

	/**
	 * @return Los penales del equipo visita
	 */
	public Short getPenalesVisita() {
		return penalesVisita;
	}

	/**
	 * @param penalesVisita
	 *            Los nuevos penales del equipo vista
	 */
	public void setPenalesVisita(Short penalesVisita) {
		this.penalesVisita = penalesVisita;
	}

	/**
	 * 
	 * @return Los goles del equipo local en texto
	 */
	public String getStringGolesLocales() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getGolesLocales() == null ? "" : getGolesLocales().toString();
	}

	/**
	 * 
	 * @return Los goles del equipo visita en texto
	 */
	public String getStringGolesVisita() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getGolesVisita() == null ? "" : getGolesVisita().toString();
	}

	/**
	 * 
	 * @return Los penales del equipo local en texto
	 */
	public String getStringPenalesLocales() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getPenalesLocal() == null ? "" : getPenalesLocal().toString();
	}

	/**
	 * 
	 * @return Los penales del equipo visita en texto
	 */
	public String getStringPenalesVisita() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getPenalesVisita() == null ? "" : getPenalesVisita().toString();
	}

	/**
	 * @return El prupo del parttido
	 */
	public Grupo getGrupo() {
		return grupo;
	}

	/**
	 * @param grupo
	 *            El nuevo grupo del equipo
	 */
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	/**
	 * 
	 * @return verdadero si representa una apuesta
	 */
	public boolean isEsApuesta() {
		return esApuesta;
	}

	/**
	 * 
	 * @param esApuesta
	 *            La nueva apuesta
	 */
	public void setEsApuesta(boolean esApuesta) {
		this.esApuesta = esApuesta;
	}

	/**
	 * El partido con resultado real
	 * 
	 * @return El resultado real
	 */
	public Partido getResultadoReal() {
		return resultadoReal;
	}

	/**
	 * 
	 * @param resultadoReal
	 *            El nuevo resultado real
	 */
	public void setResultadoReal(Partido resultadoReal) {
		this.resultadoReal = resultadoReal;
	}

	/**
	 * M&eacute;todo que describe como comparar objetos {@link Partido}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Partido) {
			Partido comparable = (Partido) obj;
			if (comparable.getLocal().equals(getLocal()) && comparable.getVisita().equals(getVisita())
					&& isSameDay(comparable.getFecha(), getFecha())) {
				return true;
			}
		}
		return super.equals(obj);
	}

	/**
	 * 
	 * @return Puntos del partido
	 */
	public short getPuntos() {
		return puntos;
	}

	/**
	 * 
	 * @param puntos
	 *            Nuevos puntos del partido
	 */
	public void setPuntos(short puntos) {
		this.puntos = puntos;
	}

	/**
	 * M&eacute;todo que calcula el equipo ganador
	 * 
	 * @return EL equipo ganador
	 */
	public Equipo getGanador() {
		if (getEstado() == 2 && getGolesLocales() != null && getGolesVisita() != null) {
			if (getGolesLocales() == getGolesVisita()) {
				if (getPenalesLocal() > getPenalesVisita()) {
					return getLocal();
				} else {
					return getVisita();
				}
			} else if (getGolesLocales() > getGolesVisita()) {
				return getLocal();
			} else {
				return getVisita();
			}
		}
		return null;
	}

	/**
	 * M&eacute;todo que calcula los puntos obtenidos en este partido
	 * 
	 * @param resultadoReal
	 *            El partido Real
	 * @param puntaje
	 *            El puntaje a setear
	 * @param regla
	 *            La regla con los valores de puntaje a agregar
	 */
	public void calculatePuntos(Partido resultadoReal, Puntaje puntaje, Regla regla) {
		setResultadoReal(resultadoReal);
		int result = 0;
		// Pronostico igual a resultado (puntos marcador)
		if (getResultadoReal().getGolesLocales() == getGolesLocales()
				&& getResultadoReal().getGolesVisita() == getGolesVisita()) {
			puntaje.setPuntosMarcador((short) (puntaje.getPuntosMarcador() + regla.getPuntosMarcador()));
			result += regla.getPuntosMarcador();
		}

		// Acierto de clasificado que no es Primera Fase
		if (getFase() != Fase.PRIMERA) {
			// Acierto de Resultado Local gana Visita (puntos resultado)
			if (getResultadoReal().getGolesLocales() > getResultadoReal().getGolesVisita()
					&& getGolesLocales() > getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
			// Acierto de Resultado Visita gana Local (puntos resultado)
			else if (getResultadoReal().getGolesLocales() < getResultadoReal().getGolesVisita()
					&& getGolesLocales() < getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
			// Puntos resultado empate + penales
			else if (getGolesLocales() == getGolesVisita() && getGanador() == getResultadoReal().getGanador()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
		} else {
			// Acierto de Resultado Local gana Visita (puntos resultado)
			if (getResultadoReal().getGolesLocales() > getResultadoReal().getGolesVisita()
					&& getGolesLocales() > getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
			// Acierto de Resultado Visita gana Local (puntos resultado)
			else if (getResultadoReal().getGolesLocales() < getResultadoReal().getGolesVisita()
					&& getGolesLocales() < getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
			// Acierto de Resultado empate (puntos resultado)
			else if (getResultadoReal().getGolesLocales() == getResultadoReal().getGolesVisita()
					&& getGolesLocales() == getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
		}
		setPuntos((short) result);
	}
}
