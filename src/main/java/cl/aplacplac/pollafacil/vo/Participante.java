package cl.aplacplac.pollafacil.vo;

import java.util.ArrayList;

public class Participante {
	private int id;
	private String nombre;
	private Short puntos;
	private Short posicion;
	private ArrayList<Partido> partidos;
	private int lugar;
	private ArrayList<Puntaje> puntajes;
	private ArrayList<Grupo> grupos;
	
	public Participante(int id) {
		partidos = new ArrayList<>();
		puntajes = new ArrayList<>();
		setGrupos(new ArrayList<>());
		puntos = 0;
		posicion = 0;
		lugar = 0;
		this.id = id;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the puntos
	 */
	public Short getPuntos() {
		return puntos;
	}

	/**
	 * @param puntos
	 *            the puntos to set
	 */
	public void setPuntos(Short puntos) {
		this.puntos = puntos;
	}

	/**
	 * @return the posicion
	 */
	public Short getPosicion() {
		return posicion;
	}

	/**
	 * @param posicion
	 *            the posicion to set
	 */
	public void setPosicion(Short posicion) {
		this.posicion = posicion;
	}

	/**
	 * @return the partidos
	 */
	public ArrayList<Partido> getPartidos() {
		return partidos;
	}

	/**
	 * @param partidos
	 *            the partidos to set
	 */
	public void setPartidos(ArrayList<Partido> partidos) {
		this.partidos = partidos;
	}
	
	public void addPartido(Partido partido) {
		if(partidos != null) {
			partidos.add(partido);
		}
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the lugar
	 */
	public int getLugar() {
		return lugar;
	}

	/**
	 * @param lugar the lugar to set
	 */
	public void setLugar(int lugar) {
		this.lugar = lugar;
	}

	public void addPuntos(Short nuevosPuntos) {
		Short x = new Short(nuevosPuntos);
		x = (short) (x + (getPuntos() == null ? 0 : getPuntos()));
		setPuntos(x);
	}

	/**
	 * @return the puntajes
	 */
	public ArrayList<Puntaje> getPuntajes() {
		return puntajes;
	}

	/**
	 * @param puntajes the puntajes to set
	 */
	public void setPuntajes(ArrayList<Puntaje> puntajes) {
		this.puntajes = puntajes;
	}

	public ArrayList<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(ArrayList<Grupo> grupos) {
		this.grupos = grupos;
	}
}
