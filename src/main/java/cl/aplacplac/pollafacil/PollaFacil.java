package cl.aplacplac.pollafacil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cl.aplacplac.pollafacil.util.Util;
import cl.aplacplac.pollafacil.vo.Equipo;
import cl.aplacplac.pollafacil.vo.Fase;
import cl.aplacplac.pollafacil.vo.Grupo;
import cl.aplacplac.pollafacil.vo.Participante;
import cl.aplacplac.pollafacil.vo.Partido;
import cl.aplacplac.pollafacil.vo.Puntaje;
import cl.aplacplac.pollafacil.vo.Regla;

/**
 * Clase que representa un campeonato
 * 
 * @author retsuchan
 *
 */
public class PollaFacil {

	/**
	 * @return the campeonato
	 */
	public String getCampeonato() {
		return campeonato;
	}

	/**
	 * @param campeonato the campeonato to set
	 */
	public void setCampeonato(String campeonato) {
		this.campeonato = campeonato;
	}

	// Nombre del campeonato a representar
	private String campeonato;

	// Arreglo de paticipantes cargados en la aplicacion
	private ArrayList<Participante> participantes;
	// Arreglo de resultados reales de partidos
	private ArrayList<Partido> resultados;
	// Arreglo de equipos cargados
	private ArrayList<Equipo> equipos;
	// Arreglo con los grupos de primera fase
	private ArrayList<Grupo> grupos;
	// Arreglo con las reglas cargadas, se utiliza para definir puntaje de jugadores
	private ArrayList<Regla> reglas;

	// Clase con metodos utiles
	private Util util;

	public PollaFacil(String campeonato) {
		this.campeonato = campeonato;
		util = new Util();
		participantes = new ArrayList<>();
		equipos = new ArrayList<>();
		resultados = new ArrayList<>();
		grupos = new ArrayList<>();
		reglas = new ArrayList<>();
	}

	// Metodos utiles
	/**
	 * M&eacute;todo que retorna el participante con id enviado
	 * 
	 * @param id El id del participante a buscar
	 * @return El Participante encontrado
	 */
	public Participante getParticipante(int id) {
		for (Participante participante : getParticipantes()) {
			if (id == participante.getId()) {
				return participante;
			}
		}
		return null;
	}
	
	/**
	 * M&eacute;todo que ordena los participantes segun puntuacion y define lugares
	 */
	public void calculaLugares() {
		ArrayList<Short> puntos = new ArrayList<>();
		for (Participante p : getParticipantes()) {
			if (!(puntos.contains(p.getPuntos()))) {
				puntos.add(p.getPuntos());
			}
		}
		Collections.sort(puntos, Collections.reverseOrder());
		for (Participante participante : participantes) {
			participante.setLugar(puntos.indexOf(participante.getPuntos()) + 1);
		}

		Collections.sort(participantes, new Comparator<Participante>() {
			@Override
			public int compare(Participante o1, Participante o2) {
				return o1.getLugar() - o2.getLugar();
			}
		});
	}

	/**
	 * M&eacute;todo que calcula los puntos de todos los participantes
	 */
	public void calculaPuntosParticipantes() {
		for (Fase fase : Fase.values()) {
			calculaPuntosPorFase(fase);
		}
	}

	/**
	 * M&eacute;todo que carga los puntos por fase
	 * 
	 * @param fase La fase a calcular
	 */
	private void calculaPuntosPorFase(Fase fase) {
		Regla regla = getReglaPorFase(fase);
		for (Participante participante : getParticipantes()) {
			for (Partido partido : participante.getPartidos()) {
				if (partido.getFase().equals(fase)) {
					setPuntosFromPartido(participante, fase, partido, regla);
				}
			}
			setPuntosClasificados(participante, regla);
		}
	}

	/**
	 * M&eacute;todo que retorna la regla cargada de la fase enviada
	 * 
	 * @param fase Fase a buscar resultados
	 * @return La regla encontrada
	 */
	private Regla getReglaPorFase(Fase fase) {
		for (Regla regla : reglas) {
			if (regla.getFase().equals(fase)) {
				return regla;
			}
		}
		return null;
	}

	/**
	 * M&eacute;todo que calcula los puntos obtenidos desde un partido
	 * 
	 * @param participante El participante a calcular puntos
	 * @param fase         La fase a calcular
	 * @param partido      El partido a calcular
	 * @param regla        La regla con la que calcular puntaje
	 */
	private void setPuntosFromPartido(Participante participante, Fase fase, Partido partido, Regla regla) {
		Partido resultadoReal = getPartidoFromResults(partido);
		Puntaje puntaje = getPuntaje(participante, fase);
		if (resultadoReal != null) {
			if (resultadoReal.getEstado() == 2 && resultadoReal.getGolesLocales() != null
					&& resultadoReal.getGolesVisita() != null) {
				partido.calculatePuntos(resultadoReal, puntaje, regla);
			}
			participante.addPuntos(partido.getPuntos());
		}
	}

	/**
	 * M&eacute;todo que calcula los puntos por equipo clasificado del participante
	 * cargado
	 * 
	 * @param participante Rl participante a calcular puntos
	 * @param regla        La regla con puntajes a cargar
	 */
	private void setPuntosClasificados(Participante participante, Regla regla) {
		if (Fase.PRIMERA.equals(regla.getFase())) {
			Puntaje puntaje = getPuntaje(participante, regla.getFase());
			ArrayList<Equipo> equipos = new ArrayList<>();
			for (Grupo grupo : participante.getGrupos()) {
				equipos.addAll(grupo.getClasificados());
			}
			for (Equipo equipo : equipos) {
				for (Equipo equipoReal : getEquipos()) {
					if (equipoReal.getFasesCompletadas().contains(Fase.PRIMERA)
							&& equipoReal.getNombre().compareToIgnoreCase(equipo.getNombre()) == 0) {
						puntaje.setPuntosEquiposClasificados(
								(short) (puntaje.getPuntosEquiposClasificados() + regla.getPuntosEquipoClasificado()));
					}
				}
			}
			participante.setPuntos((short) (participante.getPuntos() + puntaje.getPuntosEquiposClasificados()));
		} else {
			for (Partido partido : util.filtrarPartidos(participante.getPartidos(), regla.getFase())) {
				Equipo e = partido.getGanador();
				for (Equipo equipo : equipos) {
					if (equipo.equals(e) && equipo.getFasesCompletadas().contains(regla.getFase())) {
						Puntaje puntaje = getPuntaje(participante, regla.getFase());
						puntaje.setPuntosEquiposClasificados(
								(short) (puntaje.getPuntosEquiposClasificados() + regla.getPuntosEquipoClasificado()));
						participante.setPuntos((short) (participante.getPuntos() + regla.getPuntosEquipoClasificado()));
					}
				}
			}
		}
	}

	/**
	 * M&eacute;todo que retorna un partido resultado desde el partido apuesta
	 * enviado
	 * 
	 * @param partido EL partido apuesta a buscar partido resultado
	 * @return El partido encontrado
	 */
	public Partido getPartidoFromResults(Partido partido) {
		for (Partido partidoR : getResultados()) {
			if (partidoR.equals(partido)) {
				return partidoR;
			}
		}
		return null;
	}

	/**
	 * M&eacute;todo que carga los puntajes del participante
	 * 
	 * @param participante El participante a calcular puntaje
	 * @param fase         La fase a calcular puntaje
	 * @return El puntaje resultante
	 */
	private Puntaje getPuntaje(Participante participante, Fase fase) {
		for (Puntaje puntaje : participante.getPuntajes()) {
			if (puntaje.getFase().equals(fase)) {
				return puntaje;
			}
		}
		Puntaje puntaje = new Puntaje(fase);
		participante.getPuntajes().add(puntaje);
		return puntaje;
	}

	/**
	 * M&eacute;todo que retorn el equipo con el nombre enviado
	 * 
	 * @param equipoName El nombre del equipo
	 * @return El equipo encontrado
	 */
	public Equipo getEquipo(String equipoName) {
		for (Equipo equipo : getEquipos()) {
			if (equipo.getNombre().equals(equipoName)) {
				return equipo;
			}
		}
		Equipo e = new Equipo(equipoName);
		equipos.add(e);
		return e;
	}
	
	/**
	 * M&eacute;todo que retorna el grupo segun nombre enviado
	 * 
	 * @param grupoName El nombre del grupo
	 * @return El grupo encontrado
	 */
	public Grupo getGrupo(String grupoName) {
		for (Grupo grupo : getGrupos()) {
			if (grupo.getNombre().equals(grupoName)) {
				return grupo;
			}
		}
		Grupo result = new Grupo(grupoName);
		grupos.add(result);
		return result;
	}
	
	/**
	 * M&eacute;todo que calcula los equipos clasificados de todas las fases
	 * 
	 * @throws Exception
	 */
	public void calculaClasificados(ArrayList<String> clasificados) throws Exception {
		calcularClasificadosPrimeraFase(clasificados);
		for (Fase fase : Fase.values()) {
			calcularClasificadosOtraFase(fase);
		}
	}

	/**
	 * M&eacute;todo que calcula los puntos de una fase que no sea de grupo
	 * 
	 * @param fase La fase a calcular
	 * @throws Exception
	 */
	@SuppressWarnings("incomplete-switch")
	public void calcularClasificadosOtraFase(Fase fase) throws Exception {
		switch (fase) {
		case CUARTOS:
		case FINAL:
		case OCTAVOS:
		case SEMIFINAL:
		case TERCERPUESTO:
			for (Partido partido : util.filtrarPartidos(getResultados(), fase)) {
				if (partido.getEstado().equals(2)) {
					if (partido.getGanador() != null) {
						partido.getGanador().addFaseCompletada(fase);
					}
				}
			}
		}
	}

	/**
	 * M&eacute;todo que calcular los clasificados de primera fase
	 * 
	 * @throws Exception
	 */
	public void calcularClasificadosPrimeraFase(ArrayList<String> clasificados) throws Exception {
		for (Equipo equipo : getEquipos()) {
			for (String clasificado : clasificados) {
				if (clasificado.equals(equipo.getNombre())) {
					equipo.getFasesCompletadas().add(Fase.PRIMERA);
				}
			}
		}
	}
	
	// Getter y Setters

	/**
	 * @return the participantes
	 */
	public ArrayList<Participante> getParticipantes() {
		return participantes;
	}

	/**
	 * @param participantes the participantes to set
	 */
	public void setParticipantes(ArrayList<Participante> participantes) {
		this.participantes = participantes;
	}

	/**
	 * @return the resultados
	 */
	public ArrayList<Partido> getResultados() {
		return resultados;
	}

	/**
	 * @param resultados the resultados to set
	 */
	public void setResultados(ArrayList<Partido> resultados) {
		this.resultados = resultados;
	}

	/**
	 * @return the equipos
	 */
	public ArrayList<Equipo> getEquipos() {
		return equipos;
	}

	/**
	 * @param equipos the equipos to set
	 */
	public void setEquipos(ArrayList<Equipo> equipos) {
		this.equipos = equipos;
	}

	/**
	 * @return the grupos
	 */
	public ArrayList<Grupo> getGrupos() {
		return grupos;
	}

	/**
	 * @param grupos the grupos to set
	 */
	public void setGrupos(ArrayList<Grupo> grupos) {
		this.grupos = grupos;
	}

	/**
	 * @return the reglas
	 */
	public ArrayList<Regla> getReglas() {
		return reglas;
	}

	/**
	 * @param reglas the reglas to set
	 */
	public void setReglas(ArrayList<Regla> reglas) {
		this.reglas = reglas;
	}
	

	/**
	 * M&eacute;todo que devuelve las reglas en arreglo
	 * 
	 * @return Arreglo con las reglas cargadas
	 */
	public HashMap<String, Object> getReglasMaps() {
		HashMap<String, Object> result = new HashMap<>();
		for (Regla regla : getReglas()) {
			Fase fase = regla.getFase();
			result.put(fase.toString() + "PuntosResultado", regla.getPuntosResultado());
			result.put(fase.toString() + "PuntosMarcador", regla.getPuntosMarcador());
			result.put(fase.toString() + "PuntosEquipoClasificado", regla.getPuntosEquipoClasificado());
		}
		return result;
	}
}
