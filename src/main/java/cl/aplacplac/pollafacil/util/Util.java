package cl.aplacplac.pollafacil.util;

import java.util.ArrayList;

import cl.aplacplac.pollafacil.vo.Fase;
import cl.aplacplac.pollafacil.vo.Partido;

/**
 * Clase utilitaria para mostrar resultados en paginas retornadas
 * 
 * @author fgonzalez
 *
 */
public class Util {
	/**
	 * M&eacute;todo que filtra los partidos segun fase especificada
	 * 
	 * @param partidos
	 *            Los partidos a filtrar
	 * @param fase
	 *            La fase con la cual se filtrara
	 * @return El arreglo de partidos filtrado
	 */
	public ArrayList<Partido> filtrarPartidos(ArrayList<Partido> partidos, String fase) {
		return filtrarPartidos(partidos, Fase.valueOf(fase));
	}

	/**
	 * M&eacute;todo que filtra los partidos segun fase especificada
	 * 
	 * @param partidos
	 *            Los partidos a filtrar
	 * @param fase
	 *            La fase con la cual se filtrara
	 * @return El arreglo de partidos filtrado
	 */
	public ArrayList<Partido> filtrarPartidos(ArrayList<Partido> partidos, Fase fase) {
		ArrayList<Partido> result = new ArrayList<>();
		for (Partido partido : partidos) {
			if (partido.getFase().equals(fase)) {
				result.add(partido);
			}
		}
		return result;
	}
}
