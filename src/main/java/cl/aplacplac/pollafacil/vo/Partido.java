package cl.aplacplac.pollafacil.vo;

import java.util.Calendar;
import java.util.Date;

public class Partido {
	private Equipo local;
	private Equipo visita;
	private Date fecha;
	private Estadio estadio;
	private Fase fase;
	private Short golesLocales;
	private Short golesVisita;
	private Short penalesLocal;
	private Short penalesVisita;
	private Grupo grupo;
	private boolean esApuesta;
	private Partido resultadoReal;
	private short puntos;

	public Partido() {
	}

	public Partido(boolean esApuesta) {
		this.setEsApuesta(esApuesta);
	}

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
	 * @return the local
	 */
	public Equipo getLocal() {
		return local;
	}

	/**
	 * @param local
	 *            the local to set
	 */
	public void setLocal(Equipo local) {
		this.local = local;
	}

	/**
	 * @return the visita
	 */
	public Equipo getVisita() {
		return visita;
	}

	/**
	 * @param visita
	 *            the visita to set
	 */
	public void setVisita(Equipo visita) {
		this.visita = visita;
	}

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param fecha
	 *            the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the estadio
	 */
	public Estadio getEstadio() {
		return estadio;
	}

	/**
	 * @param estadio
	 *            the estadio to set
	 */
	public void setEstadio(Estadio estadio) {
		this.estadio = estadio;
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
	 * @return the golesLocales
	 */
	public Short getGolesLocales() {
		return golesLocales;
	}

	/**
	 * @param golesLocales
	 *            the golesLocales to set
	 */
	public void setGolesLocales(Short golesLocales) {
		this.golesLocales = golesLocales;
	}

	/**
	 * @return the golesVisita
	 */
	public Short getGolesVisita() {
		return golesVisita;
	}

	/**
	 * @param golesVisita
	 *            the golesVisita to set
	 */
	public void setGolesVisita(Short golesVisita) {
		this.golesVisita = golesVisita;
	}

	/**
	 * @return the penalesLocal
	 */
	public Short getPenalesLocal() {
		return penalesLocal;
	}

	/**
	 * @param penalesLocal
	 *            the penalesLocal to set
	 */
	public void setPenalesLocal(Short penalesLocal) {
		this.penalesLocal = penalesLocal;
	}

	/**
	 * @return the penalesVisita
	 */
	public Short getPenalesVisita() {
		return penalesVisita;
	}

	/**
	 * @param penalesVisita
	 *            the penalesVisita to set
	 */
	public void setPenalesVisita(Short penalesVisita) {
		this.penalesVisita = penalesVisita;
	}

	public String getStringGolesLocales() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getGolesLocales() == null ? "" : getGolesLocales().toString();
	}

	public String getStringGolesVisita() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getGolesVisita() == null ? "" : getGolesVisita().toString();
	}

	public String getStringPenalesLocales() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getPenalesLocal() == null ? "" : getPenalesLocal().toString();
	}

	public String getStringPenalesVisita() {
		if (!isEsApuesta() && getEstado() != 2) {
			return "";
		}
		return getPenalesVisita() == null ? "" : getPenalesVisita().toString();
	}

	/**
	 * @return the grupo
	 */
	public Grupo getGrupo() {
		return grupo;
	}

	/**
	 * @param grupo
	 *            the grupo to set
	 */
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	public boolean isEsApuesta() {
		return esApuesta;
	}

	public void setEsApuesta(boolean esApuesta) {
		this.esApuesta = esApuesta;
	}

	public Partido getResultadoReal() {
		return resultadoReal;
	}

	public void setResultadoReal(Partido resultadoReal) {
		this.resultadoReal = resultadoReal;
	}

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

	public short getPuntos() {
		return puntos;
	}

	public void setPuntos(short puntos) {
		this.puntos = puntos;
	}

	public Equipo getGanador() {
		if (getEstado() == 2) {
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
}
