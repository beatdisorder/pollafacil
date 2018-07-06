package cl.aplacplac.pollafacil.vo;

import java.util.ArrayList;

/**
 * Clase que representa el objeto equipo
 * 
 * @author fgonzalez
 *
 */
public class Equipo {
	// EL nombre del equipo
	private String nombre;
	// Las fases clasificadas por el equipo
	private ArrayList<Fase> fasesCompletadas;

	/**
	 * Constructor por defecto
	 */
	public Equipo() {
		this(null);
	}

	/**
	 * Sobre carga del constructor
	 * 
	 * @param nombre
	 *            El nombre del equipo
	 */
	public Equipo(String nombre) {
		setNombre(nombre);
		setFasesCompletadas(new ArrayList<>());
	}

	/**
	 * @return el nombre del equipo
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            el nuevo nombre del equipo
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Especifica la comparacion de objetos del mismo tipo
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Equipo) {
			Equipo comparable = (Equipo) obj;
			if (comparable.getNombre().compareTo(getNombre()) == 0) {
				return true;
			}
		}
		return super.equals(obj);
	}

	/**
	 * M&eacute;todo que agrega una fase completada al equipo
	 * 
	 * @param fase
	 *            La fase a agregar
	 */
	public void addFaseCompletada(Fase fase) {
		fasesCompletadas.add(fase);
	}

	/**
	 * @return las fasesCompletadas
	 */
	public ArrayList<Fase> getFasesCompletadas() {
		return fasesCompletadas;
	}

	/**
	 * @param fasesCompletadas
	 *            las nuevas fasesCompletadas
	 */
	public void setFasesCompletadas(ArrayList<Fase> fasesCompletadas) {
		this.fasesCompletadas = fasesCompletadas;
	}
}