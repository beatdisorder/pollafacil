package cl.aplacplac.pollafacil.vo;

import java.util.ArrayList;

/***
 * Clase que representa un participante dentro de las apuestas
 * 
 * @author fgonzalez
 *
 */
public class Participante {
	// Id del participante
	private int id;
	// Nombre del participante
	private String nombre;
	// Puntos totales del participante
	private Short puntos;
	// Apuestas del participante
	private ArrayList<Partido> partidos;
	// Posicion dentro de todos los participantes
	private int lugar;
	// Puntajes del participante
	private ArrayList<Puntaje> puntajes;
	// Grupos del participante - primera fase
	private ArrayList<Grupo> grupos;

	/**
	 * Constructor de clase
	 * 
	 * @param id
	 *            El id del participante
	 */
	public Participante(int id) {
		partidos = new ArrayList<>();
		puntajes = new ArrayList<>();
		setGrupos(new ArrayList<>());
		puntos = 0;
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
	 *            Los nuevos puntos
	 */
	public void setPuntos(Short puntos) {
		this.puntos = puntos;
	}

	/**
	 * @return Los partidos
	 */
	public ArrayList<Partido> getPartidos() {
		return partidos;
	}

	/**
	 * @param partidos
	 *            Los nuevos partidos
	 */
	public void setPartidos(ArrayList<Partido> partidos) {
		this.partidos = partidos;
	}

	/**
	 * M&eacute;todo que agrega el partido enviado a los partidos del aprticipante
	 * 
	 * @param partido
	 *            El partido a agregar
	 */
	public void addPartido(Partido partido) {
		if (partidos != null) {
			partidos.add(partido);
		}
	}

	/**
	 * @return El id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            El nuevo id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return El lugar
	 */
	public int getLugar() {
		return lugar;
	}

	/**
	 * @param lugar
	 *            El nuevo lugar
	 */
	public void setLugar(int lugar) {
		this.lugar = lugar;
	}

	/**
	 * 
	 * @param nuevosPuntos
	 *            los nuevos puntos
	 */
	public void addPuntos(Short nuevosPuntos) {
		Short x = new Short(nuevosPuntos);
		x = (short) (x + (getPuntos() == null ? 0 : getPuntos()));
		setPuntos(x);
	}

	/**
	 * @return Los puntajes
	 */
	public ArrayList<Puntaje> getPuntajes() {
		return puntajes;
	}

	/**
	 * @param puntajes
	 *            Los nuevos puntajes
	 */
	public void setPuntajes(ArrayList<Puntaje> puntajes) {
		this.puntajes = puntajes;
	}

	/**
	 * 
	 * @return Los grupos de primera fase del participante
	 */
	public ArrayList<Grupo> getGrupos() {
		return grupos;
	}

	/**
	 * 
	 * @param grupos
	 *            Los nuevos grupos del participante
	 */
	public void setGrupos(ArrayList<Grupo> grupos) {
		this.grupos = grupos;
	}
}
