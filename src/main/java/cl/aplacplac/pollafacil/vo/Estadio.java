package cl.aplacplac.pollafacil.vo;

/**
 * Clase que representa un estadio dentro de las apuestas
 * 
 * @author fgonzalez
 *
 */
public class Estadio {
	// El nombre del estadio
	private String nombre;

	/**
	 * Constructor por defecto
	 */
	public Estadio() {
	}

	/**
	 * Sobrecarga del constructor
	 * 
	 * @param nombre
	 *            El nombre del estadio
	 */
	public Estadio(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return el nombre del estadio
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            el nuevo nombre del estadio
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
