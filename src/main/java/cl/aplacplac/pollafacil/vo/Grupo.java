package cl.aplacplac.pollafacil.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Clase que representa un grupo de primera fase
 * 
 * @author fgonzalez
 *
 */
public class Grupo {
	// Nombre del grupo
	private String nombre;
	// Arreglo de partidos del grupo
	private ArrayList<Partido> partidos;

	/**
	 * Constructor por defecto
	 */
	public Grupo() {
		this(null);
	}

	/**
	 * Sobrecarga del constructor
	 * 
	 * @param nombre
	 *            Nombre del grupo
	 */
	public Grupo(String nombre) {
		setNombre(nombre);
		setPartidos(new ArrayList<>());
	}

	/**
	 * @return el nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            el nuevo nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return los partidos
	 */
	public ArrayList<Partido> getPartidos() {
		return partidos;
	}

	/**
	 * @param partidos
	 *            los nuevos partidos
	 */
	public void setPartidos(ArrayList<Partido> partidos) {
		this.partidos = partidos;
	}

	/**
	 * M&eacute;todo que retorna arreglo de equipos clasificados del grupo
	 * 
	 * @return Arreglo de equipos clasificados del grupo
	 */
	public ArrayList<Equipo> getClasificados() {
		ArrayList<EquipoPrimeraFase> equipos = new ArrayList<>();

		for (Partido partido : partidos) {
			getEquipoPrimeraFase(equipos, partido);
		}

		Collections.sort(equipos, new Comparator<EquipoPrimeraFase>() {
			@Override
			public int compare(EquipoPrimeraFase o1, EquipoPrimeraFase o2) {
				if (o1.getPuntos() == o2.getPuntos()) {
					if (o2.getDiferenciaGoles() == o1.getDiferenciaGoles()) {
						return o2.getGolesFavor() - o1.getGolesFavor();
					} else {
						return o2.getDiferenciaGoles() - o1.getDiferenciaGoles();
					}
				} else {
					return o2.getPuntos() - o1.getPuntos();
				}
			}
		});

		return getEquipos(equipos.subList(0, 2));
	}

	/**
	 * M&eacute;todo que retorna los equipos de primera fase
	 * 
	 * @param subList
	 *            sub lista con los equipos a encontrar
	 * @return Arreglo de equipos resultante
	 */
	private ArrayList<Equipo> getEquipos(List<EquipoPrimeraFase> subList) {
		ArrayList<Equipo> result = new ArrayList<>();
		for (EquipoPrimeraFase equipo : subList) {
			result.add(equipo.getEquipo());
		}
		return result;
	}

	/**
	 * M&eacute;todo que agrega el partido enviado a los equipos enviados
	 * 
	 * @param equipos
	 *            Los equipos a agregar
	 * @param partido
	 *            El partido a agregar
	 */
	private void getEquipoPrimeraFase(ArrayList<EquipoPrimeraFase> equipos, Partido partido) {
		EquipoPrimeraFase equipoLocal = getEquipo(equipos, partido.getLocal());
		EquipoPrimeraFase equipoVisita = getEquipo(equipos, partido.getVisita());

		equipoLocal.addPartido(partido, true);
		equipoVisita.addPartido(partido, false);
	}

	/**
	 * M&eacute;todo que retorna el Equipo correspondiente de los equipos cargados
	 * en el grupo
	 * 
	 * @param equipos
	 *            Los equipos donde buscar
	 * @param equipo
	 *            El equipo a buscar
	 * @return El {@link EquipoPrimeraFase} encontrado
	 */
	private EquipoPrimeraFase getEquipo(ArrayList<EquipoPrimeraFase> equipos, Equipo equipo) {
		for (EquipoPrimeraFase ep : equipos) {
			if (ep.getEquipo().equals(equipo)) {
				return ep;
			}
		}
		EquipoPrimeraFase ep = new EquipoPrimeraFase(equipo);
		equipos.add(ep);
		return ep;
	}

	/**
	 * Clase privada que extiende de {@link Equipo} para representar las cualidades
	 * de un equipo en primera fase
	 * 
	 * @author fgonzalez
	 *
	 */
	private class EquipoPrimeraFase extends Equipo {
		private Short partidosJugados;
		private Short golesFavor;

		public Short getGolesFavor() {
			return golesFavor;
		}

		private Short golesContra;
		private Short diferenciaGoles;
		private Short partidosGanados;
		private Short partidosPerdidos;
		private Short partidosEmpatados;
		private Short puntos;
		private Equipo equipo;

		public EquipoPrimeraFase(Equipo equipo) {
			this.equipo = equipo;
			partidosJugados = 0;
			golesFavor = 0;
			golesContra = 0;
			diferenciaGoles = 0;
			partidosGanados = 0;
			partidosPerdidos = 0;
			partidosEmpatados = 0;
			puntos = 0;
		}

		public void addPartido(Partido partido, boolean local) {
			partidosJugados++;
			if (local) {
				golesFavor = (short) (golesFavor + partido.getGolesLocales());
				golesContra = (short) (golesContra + partido.getGolesVisita());

			} else {
				golesFavor = (short) (golesFavor + partido.getGolesVisita());
				golesContra = (short) (golesContra + partido.getGolesLocales());
			}

			diferenciaGoles = (short) (golesFavor - golesContra);

			if (local && partido.getGolesLocales() > partido.getGolesVisita()
					|| !local && partido.getGolesVisita() > partido.getGolesLocales()) {
				partidosGanados++;
				puntos = (short) (puntos + 3);
			} else if (partido.getGolesLocales() == partido.getGolesVisita()) {
				if (partido.getFase().equals(Fase.PRIMERA)) {
					partidosEmpatados++;
					puntos = (short) (puntos + 1);
				}
			} else {
				partidosPerdidos++;
			}
		}

		public Short getDiferenciaGoles() {
			return diferenciaGoles;
		}

		public Equipo getEquipo() {
			return equipo;
		}

		public Short getPuntos() {
			return puntos;
		}
	}
}
