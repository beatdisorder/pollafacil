package cl.aplacplac.pollafacil.vo;

import java.util.ArrayList;

public class Equipo {
	private String nombre;
	private ArrayList<Fase> fasesCompletadas;
	
	public Equipo() {
		this(null);
	}
	
	public Equipo(String nombre) {
		setNombre(nombre);
		setFasesCompletadas(new ArrayList<>());
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Equipo) {
			Equipo comparable = (Equipo) obj;
			if(comparable.getNombre().compareTo(getNombre()) == 0) {
				return true;
			}
		}
		return super.equals(obj);
	}
	
	public void addFaseCompletada(Fase fase) {
		fasesCompletadas.add(fase);
	}

	/**
	 * @return the fasesCompletadas
	 */
	public ArrayList<Fase> getFasesCompletadas() {
		return fasesCompletadas;
	}

	/**
	 * @param fasesCompletadas the fasesCompletadas to set
	 */
	public void setFasesCompletadas(ArrayList<Fase> fasesCompletadas) {
		this.fasesCompletadas = fasesCompletadas;
	}
}