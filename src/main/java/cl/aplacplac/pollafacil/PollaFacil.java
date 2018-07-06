package cl.aplacplac.pollafacil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cl.aplacplac.pollafacil.util.Util;
import cl.aplacplac.pollafacil.util.ValueUtil;
import cl.aplacplac.pollafacil.vo.Equipo;
import cl.aplacplac.pollafacil.vo.Fase;
import cl.aplacplac.pollafacil.vo.Grupo;
import cl.aplacplac.pollafacil.vo.Participante;
import cl.aplacplac.pollafacil.vo.Partido;
import cl.aplacplac.pollafacil.vo.Puntaje;
import cl.aplacplac.pollafacil.vo.Regla;

/**
 * Clase principal de la aplicacion
 * @author fgonzalez
 *
 */
@Component
@RestController
@RequestMapping("/pollafacil")
@SpringBootApplication
public class PollaFacil {
	// Logger de la clase
	private final static Logger logger = Logger.getLogger(PollaFacil.class);
	// Extenciones de archivos permitidas 
	private final String[] xlsxExtension = new String[] { "xlsx", "xlsm" };

	// Clase con definiciones escritas en application.yml
	@Autowired
	Environment env;

	// variable que representa los indices de los participantes
	private int i = 0;

	// Dominio a mostrar dentro de la aplicacion
	@Value("${pollaFacil.domain}")
	private String domain;

	// Fase actual del campeonato, filtra lo calculado y mostrado dentro de la aplicacion
	@Value("${pollaFacil.faseActual}")
	private Integer faseActual;

	// Archivos resultados
	@Value("${pollaFacil.resultados.resultFile}")
	private String resultFile;
	// Propiedad con la que se espera encontrar la hoja de reglas
	@Value("${pollaFacil.resultados.reglasSheet}")
	private String reglasSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de primera fase
	@Value("${pollaFacil.resultados.primeraFaseSheet}")
	private String primeraFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de Octavos de final
	@Value("${pollaFacil.resultados.octavosFaseSheet}")
	private String octavosFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de Cuartos de final
	@Value("${pollaFacil.resultados.cuartosFaseSheet}")
	private String cuartosFaseSheet;

	// Propiedades de la Fase Grupo
	// Directorio en el que se espera encontrar archivos de apuestas
	@Value("${pollaFacil.faseGrupo.readPath}")
	private String faseGrupoReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de participante
	@Value("${pollaFacil.faseGrupo.prefix}")
	private String faseGrupoPrefix;
	// Arreglo con propiedades que representan las hojas de la fase de grupo
	private String[] faseGrupoSheets;

	// Octavos
	// Directorio en el que se espera encontrar archivos de apuestas
	@Value("${pollaFacil.octavos.readPath}")
	private String octavosReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de participante
	@Value("${pollaFacil.octavos.prefix}")
	private String octavosPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	@Value("${pollaFacil.octavos.sheet}")
	private String octavosSheet;
	
	// Cuartos
	// Directorio en el que se espera encontrar archivos de apuestas
	@Value("${pollaFacil.cuartos.readPath}")
	private String cuartosReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de participantes
	@Value("${pollaFacil.cuartos.prefix}")
	private String cuartosPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	@Value("${pollaFacil.cuartos.sheet}")
	private String cuartosSheet;
	
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
	// Clase utilitaria para filtrar partidos
	private Util util = new Util();

	/**
	 * M&eacute;todo principal
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(PollaFacil.class, args);
		logger.info("\n*******************************************" 
				+ "\nPolla Facil Lista"
				+ "\n*******************************************\n");
	}

	/**
	 * Acciones a realizar despues de carga correcta de aplicacion
	 * @throws Exception
	 */
	@PostConstruct
	public void construct() throws Exception {
		faseGrupoSheets = env.getProperty("pollaFacil.faseGrupo.sheets").split("-");
		refresh();
	}

	/**
	 * M&eacute;todo que recalcula los archivos y resultados
	 * @return Pagina de portada
	 * @throws Exception
	 */
	@RequestMapping(path = "/refresh", method = { RequestMethod.GET })
	public ModelAndView refresh() throws Exception {
		logger.info("\n*******************************************" 
				+ "\nLlamado a Refresh!"
				+ "\n*******************************************\n");
		i = 0;
		participantes = new ArrayList<>();
		equipos = new ArrayList<>();
		resultados = new ArrayList<>();
		grupos = new ArrayList<>();
		reglas = new ArrayList<>();
		loadResult();
		loadPlanillas();
		calculatePoints();
		return index();
	}

	/**
	 * M&eacute;todo que carga y muestra el participante con el id enviado por parametro
	 * @param id El id del participante
	 * @return Pagina del participante 
	 * @throws Exception
	 */
	@RequestMapping(path = "/participante", method = { RequestMethod.GET })
	public ModelAndView viewParticipante(@RequestParam(value = "id") int id) throws Exception {
		logger.info("\n*******************************************" 
	            + "\nLlamado a viewParticipante: " + id + "!"
				+ "\n*******************************************\n");
		ModelAndView mav = new ModelAndView("participante");
		Participante p = getParticipante(id);
		mav.getModel().put("domain", domain);
		mav.getModel().put("today", new Date());
		mav.getModel().put("faseActual", faseActual);
		mav.getModel().put("equipos", equipos);
		mav.getModel().put("Util", util);
		mav.getModel().putAll(getReglas());
		if (p != null) {
			mav.getModel().put("participante", p);
			mav.getModel().put("partidos", p.getPartidos());
			mav.getModel().put("puntajes", p.getPuntajes());
		}
		return mav;
	}

	/**
	 * M&eacute;todo que devuelve las reglas en arreglo
	 * @return Arreglo con las reglas cargadas
	 */
	private HashMap<String, Object> getReglas() {
		HashMap<String, Object> result = new HashMap<>();
		for (Regla regla : reglas) {
			Fase fase = regla.getFase();
			result.put(fase.toString() + "PuntosResultado", regla.getPuntosResultado());
			result.put(fase.toString() + "PuntosMarcador", regla.getPuntosMarcador());
			result.put(fase.toString() + "PuntosEquipoClasificado", regla.getPuntosEquipoClasificado());
		}
		return result;
	}

	/**
	 * M&eacute;todo que retorna el participante con id enviado 
	 * @param id El id del participante a buscar
	 * @return El Participante encontrado
	 */
	private Participante getParticipante(int id) {
		for (Participante participante : participantes) {
			if (id == participante.getId()) {
				return participante;
			}
		}
		return null;
	}

	/**
	 * M&eacute;todo que retorna la pantalla de portada de la aplicacion
	 * @return Pagina de portada
	 */
	@RequestMapping(path = "/index")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("portada");
		mav.getModel().put("domain", domain);
		mav.getModel().put("today", new Date());
		mav.getModel().put("faseActual", faseActual);
		mav.getModel().put("participantes", participantes);
		mav.getModel().put("equipos", equipos);
		mav.getModel().put("resultados", resultados);
		mav.getModel().put("fases", Fase.values());
		mav.getModel().put("Util", util);
		mav.getModel().putAll(getReglas());
		return mav;
	}

	/**
	 * M&eacute;todo que carga los resultados desde archivo de resultados
	 */
	private void loadResult() throws Exception {
		File loadFile = new File(resultFile);
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
					switch (faseActual) {
					case 6:
					case 5:
					case 4:
					case 3:
						resultados.addAll(getResultados(Fase.CUARTOS, loadFile));
					case 2:
						resultados.addAll(getResultados(Fase.OCTAVOS, loadFile));
					case 1:
						resultados.addAll(getResultados(Fase.PRIMERA, loadFile));
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"\n*******************************************" 
							+ "Error cargando " + loadFile.getName() + ": " + e.getMessage() 
							+ "\n*******************************************\n");
					throw e;
				}
			} else {
				logger.error("\n*******************************************\n" 
						+ "\nNo se logro cargar resultados: " + loadFile.getName() 
						+ "\n*******************************************\n");
			}
		} else {
			throw new Exception("No se encontro el archivo de resultados: " + resultFile);
		}
	}

	/**
	 * M&eacute;todo los resultados de una fase
	 * @param fase La fase a cargar resultados
	 * @param loadFile Archivo desde el cual se cargaran los archivos
	 * @return Arreglo con partidos cargados
	 * @throws Exception
	 */
	private ArrayList<Partido> getResultados(Fase fase, File loadFile) throws Exception {
		return getPartidos(fase, new XSSFWorkbook(new FileInputStream(loadFile)));
	}

	/**
	 * M&eacute;todo que carga partidos desde un objeto excel
	 * @param fase La fase a cargar
	 * @param workbook El archivo excel cargado
	 * @return Arreglo de partidos
	 * @throws Exception
	 */
	private ArrayList<Partido> getPartidos(Fase fase, Workbook workbook) throws Exception {
		switch (fase) {
		case CUARTOS:
			return getPartidosOtrasFases(fase, workbook, cuartosFaseSheet);
		case FINAL:
			break;
		case OCTAVOS:
			return getPartidosOtrasFases(fase, workbook, octavosFaseSheet);
		case PRIMERA:
			return getPartidosPrimerFase(fase, workbook.getSheet(primeraFaseSheet), 4, 51);
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return null;
	}

	/**
	 * M&eacute;todo que carga un partido que no sea de primera fase
	 * @param fase La fase a cargar
	 * @param workbook El archivo excel cargado
	 * @param sheetName El nombre de la hoja a cargar
	 * @return Arreglo de partidos encontrados
	 * @throws Exception
	 */
	@SuppressWarnings("incomplete-switch")
	private ArrayList<Partido> getPartidosOtrasFases(Fase fase, Workbook workbook, String sheetName) throws Exception {
		if (workbook == null || workbook.getSheet(sheetName) == null) {
			logger.error("Hoja no existe");
			return new ArrayList<>();
		}
		ArrayList<Partido> result = new ArrayList<>();
		switch (fase) {
		case CUARTOS:
			result.addAll(getPartidosFromSheet(workbook, cuartosSheet, fase));
			break;
		case FINAL:
			break;
		case OCTAVOS:
			result.addAll(getPartidosFromSheet(workbook, octavosSheet, fase));
			break;
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return result;
	}

	/**
	 * M&eacute;todo que carga los partidos de primera fase
	 * @param fase La fase a carga 
	 * @param sheet La hoja desde la cual se cargaran los partidos
	 * @param fromRow La fila desde la que se cargaran partidos
	 * @param toRow La fila hasta la que se cargaran los partidos
	 * @return Arreglo de partidos encontrados
	 * @throws Exception
	 */
	private ArrayList<Partido> getPartidosPrimerFase(Fase fase, Sheet sheet, int fromRow, int toRow) throws Exception {
		if (sheet == null) {
			throw new Exception("Hoja no existe");
		}
		ArrayList<Partido> result = new ArrayList<>();
		for (int i = fromRow; i <= toRow; i++) {
			result.add(getResultadoPartidoFromRow(fase, i, sheet));
		}
		return result;
	}

	/**
	 * M&eacute;todo que carga un partido resultado real desde una fila
	 * @param fase La fase a cargar 
	 * @param column Columna en la que se espera encontrar valor
	 * @param sheet La hoja desde la cual se cargaran resultados
	 * @return El partido cargado
	 */
	private Partido getResultadoPartidoFromRow(Fase fase, int column, Sheet sheet) {
		Partido result = new Partido();
		result.setFase(fase);
		result.setLocal(getEquipo(getCell("B" + column, sheet).getStringCellValue()));
		result.setVisita(getEquipo(getCell("F" + column, sheet).getStringCellValue()));
		result.setFecha(HSSFDateUtil.getJavaDate(getCell("H" + column, sheet).getNumericCellValue()));
		addHour(result.getFecha(), getCell("I" + column, sheet));
		result.setGolesLocales(ValueUtil.floatToShort(getCell("C" + column, sheet)));
		result.setGolesVisita(ValueUtil.floatToShort(getCell("E" + column, sheet)));
		return result;
	}

	/**
	 * M&eacute;todo que calcula los puntos de los participantes
	 * @throws Exception
	 */
	private void calculatePoints() throws Exception {
		File loadFile = new File(resultFile);
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
					cargarReglas(new XSSFWorkbook(new FileInputStream(loadFile))); // Cargando reglas
					calculaClasificados(); // Calculando clasificados
					calculaPuntosParticipantes(); // Calculando puntos
					calculaLugares();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"\n*******************************************" 
							+ "Error cargando " + loadFile.getName() + ": " + e.getMessage() 
							+ "\n*******************************************\n");
					throw e;
				}
			} else {
				logger.error("\n*******************************************\n" 
						+ "\nNo se logro cargar resultados: " + loadFile.getName() 
						+ "\n*******************************************\n");
			}
		} else {
			throw new Exception("No se encontro el archivo de resultados: " + resultFile);
		}
	}

	/**
	 * M&eacute;todo que ordena los participantes segun puntuacion y define lugares
	 */
	private void calculaLugares() {
		ArrayList<Short> puntos = new ArrayList<>();
		for (Participante p : participantes) {
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
	private void calculaPuntosParticipantes() {
		for (Fase fase : Fase.values()) {
			calculaPuntosPorFase(fase);
		}
	}

	/**
	 * M&eacute;todo que carga los puntos por fase
	 * @param fase La fase a calcular
	 */
	private void calculaPuntosPorFase(Fase fase) {
		Regla regla = getReglaPorFase(fase);
		for (Participante participante : participantes) {
			for (Partido partido : participante.getPartidos()) {
				if (partido.getFase().equals(fase)) {
					setPuntosFromPartido(participante, fase, partido, regla);
				}
			}
			setPuntosClasificados(participante, regla);
		}
	}

	/**
	 * M&eacute;todo que calcula los puntos por equipo clasificado del participante cargado
	 * @param participante Rl participante a calcular puntos
	 * @param regla La regla con puntajes a cargar
	 */
	private void setPuntosClasificados(Participante participante, Regla regla) {
		if (Fase.PRIMERA.equals(regla.getFase())) {
			Puntaje puntaje = getPuntaje(participante, regla.getFase());
			ArrayList<Equipo> equipos = new ArrayList<>();
			for (Grupo grupo : participante.getGrupos()) {
				equipos.addAll(grupo.getClasificados());
			}
			for (Equipo equipo : equipos) {
				for (Equipo equipoReal : PollaFacil.this.equipos) {
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
					if(equipo.equals(e) && equipo.getFasesCompletadas().contains(regla.getFase())) {
						Puntaje puntaje = getPuntaje(participante, regla.getFase());
						puntaje.setPuntosEquiposClasificados((short) (puntaje.getPuntosEquiposClasificados() + regla.getPuntosEquipoClasificado()));
						participante.setPuntos((short) (participante.getPuntos() + regla.getPuntosEquipoClasificado()));
					}
				}
			}
		}
	}

	/**
	 * M&eacute;todo que calcula los puntos obtenidos desde un partido
	 * @param participante El participante a calcular puntos
	 * @param fase La fase a calcular
	 * @param partido El partido a calcular
	 * @param regla La regla con la que calcular puntaje
	 */
	private void setPuntosFromPartido(Participante participante, Fase fase, Partido partido, Regla regla) {
		Partido resultadoReal = getPartidoFromResults(partido);
		Puntaje puntaje = getPuntaje(participante, fase);
		if(resultadoReal != null) {
			if (resultadoReal.getEstado() == 2 && resultadoReal.getGolesLocales() != null && resultadoReal.getGolesVisita() != null) {
				partido.calculatePuntos(resultadoReal, puntaje, regla);
			}		
			participante.addPuntos(partido.getPuntos());	
		}
	}

	/**
	 * M&eacute;todo que carga los puntajes del participante
	 * @param participante El participante a calcular puntaje
	 * @param fase La fase a calcular puntaje
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
	 * M&eacute;todo que retorna un partido resultado desde el partido apuesta enviado
	 * @param partido EL partido apuesta a buscar partido resultado
	 * @return El partido encontrado
	 */
	private Partido getPartidoFromResults(Partido partido) {
		for (Partido partidoR : resultados) {
			if (partidoR.equals(partido)) {
				return partidoR;
			}
		}
		return null;
	}

	/**
	 * M&eacute;todo que retorna la regla cargada de la fase enviada 
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
	 * M&eacute;todo que calcula los equipos clasificados de todas las fases
	 * @throws Exception
	 */
	private void calculaClasificados() throws Exception {
		for (Fase fase : Fase.values()) {
			calculaClasificadosPorFase(fase);
		}
	}

	/**
	 * M&eacute;todo que calcula los equipos clasificados de la fase enviada
	 * @param fase La fase a calcular
	 * @throws Exception
	 */
	private void calculaClasificadosPorFase(Fase fase) throws Exception {
		switch (fase) {
		case CUARTOS:
		case FINAL:
		case OCTAVOS:
		case SEMIFINAL:
		case TERCERPUESTO:
			calcularClasificadosOtraFase(fase);
			break;
		case PRIMERA:
			calcularClasificadosPrimeraFase();
			break;
		}
	}

	/**
	 * M&eacute;todo que calcula los puntos de una fase que no sea de grupo
	 * @param fase La fase a calcular
	 * @throws Exception
	 */
	@SuppressWarnings("incomplete-switch")
	private void calcularClasificadosOtraFase(Fase fase) throws Exception {
		switch (fase) {
		case CUARTOS:
		case FINAL:
		case OCTAVOS:
		case SEMIFINAL:
		case TERCERPUESTO:
			for (Partido partido : util.filtrarPartidos(resultados, fase)) {
				if(partido.getEstado().equals(2)) {
					if(partido.getGanador() != null) {
						partido.getGanador().addFaseCompletada(fase);
					}
				}
			}
		}
	}

	/**
	 * M&eacute;todo que calcular los clasificados de primera fase
	 * @throws Exception
	 */
	private void calcularClasificadosPrimeraFase() throws Exception {
		ArrayList<String> clasificados = getClasificadosDesdeHoja();
		for (Equipo equipo : equipos) {
			for (String clasificado : clasificados) {
				if (clasificado.equals(equipo.getNombre())) {
					equipo.getFasesCompletadas().add(Fase.PRIMERA);
				}
			}
		}
	}

	/**
	 * M&eacute;todo que carga los equipos clasificados de primera fase
	 * @return Arreglo con los equipos clasificados
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private ArrayList<String> getClasificadosDesdeHoja() throws Exception {
		ArrayList<String> result = new ArrayList<>();

		File loadFile = new File(resultFile);
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
					Sheet hoja = new XSSFWorkbook(new FileInputStream(loadFile)).getSheet(primeraFaseSheet);
					for (int i = 4; i <= 18; i++) {
						String clasificado = getCell("M" + i, hoja).getStringCellValue();
						if (clasificado != null && clasificado.trim().length() > 0) {
							result.add(clasificado);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"\n*******************************************" 
							+ "Error cargando " + loadFile.getName() + ": " + e.getMessage() 
							+ "\n*******************************************\n");
					throw e;
				}
			} else {
				logger.error("\n*******************************************\n" 
						+ "\nNo se logro cargar resultados: " + loadFile.getName() 
						+ "\n*******************************************\n");
			}
		} else {
			throw new Exception("No se encontro el archivo de resultados: " + resultFile);
		}
		return result;
	}

	/**
	 * M&eacute;todo que carga reglas desde el excel enviado
	 * @param resultsXlsx El archivo excel
	 */
	private void cargarReglas(XSSFWorkbook resultsXlsx) {
		Sheet sheetReglas = resultsXlsx.getSheet(reglasSheet);
		for (Fase fase : Fase.values()) {
			reglas.add(getRegla(fase, sheetReglas));
		}
	}

	/**
	 * M&eacute;todo que carga las reglas desde hoija enviada por parametro 
	 * @param fase La fase a cargar
	 * @param hoja La hoja a cargar
	 * @return La regla creada desde definicion
	 */
	private Regla getRegla(Fase fase, Sheet hoja) {
		Regla regla = new Regla();
		String column = "E";
		regla.setFase(fase);
		switch (fase) {
		case CUARTOS:
			regla.setPuntosResultado(ValueUtil.floatToShort(getCell(column + 13, hoja)));
			regla.setPuntosMarcador(ValueUtil.floatToShort(getCell(column + 14, hoja)));
			regla.setPuntosEquipoClasificado(ValueUtil.floatToShort(getCell(column + 15, hoja)));
			return regla;
		case FINAL:
			regla.setPuntosResultado(ValueUtil.floatToShort(getCell(column + 23, hoja)));
			regla.setPuntosMarcador(ValueUtil.floatToShort(getCell(column + 24, hoja)));
			regla.setPuntosEquipoClasificado(ValueUtil.floatToShort(getCell(column + 25, hoja)));
			return regla;
		case OCTAVOS:
			regla.setPuntosResultado(ValueUtil.floatToShort(getCell(column + 8, hoja)));
			regla.setPuntosMarcador(ValueUtil.floatToShort(getCell(column + 9, hoja)));
			regla.setPuntosEquipoClasificado(ValueUtil.floatToShort(getCell(column + 10, hoja)));
			return regla;
		case PRIMERA:
			regla.setPuntosResultado(ValueUtil.floatToShort(getCell(column + 3, hoja)));
			regla.setPuntosMarcador(ValueUtil.floatToShort(getCell(column + 4, hoja)));
			regla.setPuntosEquipoClasificado(ValueUtil.floatToShort(getCell(column + 5, hoja)));
			return regla;
		case SEMIFINAL:
			regla.setPuntosResultado(ValueUtil.floatToShort(getCell(column + 18, hoja)));
			regla.setPuntosMarcador(ValueUtil.floatToShort(getCell(column + 19, hoja)));
			regla.setPuntosEquipoClasificado(ValueUtil.floatToShort(getCell(column + 20, hoja)));
			return regla;
		case TERCERPUESTO:
			regla.setPuntosResultado(ValueUtil.floatToShort(getCell(column + 23, hoja)));
			regla.setPuntosMarcador(ValueUtil.floatToShort(getCell(column + 24, hoja)));
			regla.setPuntosEquipoClasificado(ValueUtil.floatToShort(getCell(column + 25, hoja)));
			return regla;
		}
		return null;
	}

	/**
	 * M&eacute;todo que carga las pantillas de apuestas
	 * @throws Exception
	 */
	private void loadPlanillas() throws Exception {
		switch (faseActual) {
		case 6:
			loadPlanillasPorFase(Fase.FINAL);
		case 5:
			loadPlanillasPorFase(Fase.TERCERPUESTO);
		case 4:
			loadPlanillasPorFase(Fase.SEMIFINAL);
		case 3:
			loadPlanillasPorFase(Fase.CUARTOS);
		case 2:
			loadPlanillasPorFase(Fase.OCTAVOS);
		case 1:
			loadPlanillasPorFase(Fase.PRIMERA);
		}
	}

	/**
	 * M&eacute;todo que carga las planillas de la fase solicitada
	 * @param fase La fase a cargar
	 * @throws Exception
	 */
	private void loadPlanillasPorFase(Fase fase) throws Exception {
		if (faseGrupoReadPath != null) {
			File folder = new File(getReadPath(fase));
			if (folder.exists()) {
				logger.info("folder.listFiles().length: " + folder.listFiles().length);
				for (final File fileEntry : folder.listFiles()) {
					if (!(fileEntry.isDirectory()) && checkExtension(fileEntry)) {
						try {
							String name = fileEntry.getName();
							name = name.replaceAll("\\Q" + getPrefix(fase) + "\\E", "");
							for (String ext : xlsxExtension) {
								name = name.replaceAll("\\Q" + "." + ext + "\\E", "");
							}
							name = name.replaceAll("\\Q" + "_" + "\\E", " ");
							getParticipante(name.trim(), fileEntry, fase);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("\n*******************************************" 
									+ "Error cargando " + fileEntry.getName() + ": " + e.getMessage()
									+ "\n*******************************************\n");
							throw e;
						}
					} else {
						logger.error(
								"\n*******************************************\n" 
								+ "\nNo se logro cargar planilla: " + fileEntry.getName() 
								+ "\n*******************************************\n");
					}
				}
			}
		} else {
			Exception e = new Exception("readPath no puede ser Nulo!");
			logger.error("\n*******************************************" 
					+ "\n" + e.getMessage()
					+ "\n*******************************************\n");
			throw e;
		}
	}

	/**
	 * M&eacute;todo que retorna el prefijo de archivo segun fase enviada
	 * @param fase La fase a obtener prefijo 
	 * @return EL prefijo
	 */
	private String getPrefix(Fase fase) {
		switch (fase) {
		case CUARTOS:
			return cuartosPrefix;
		case FINAL:
			break;
		case OCTAVOS:
			return octavosPrefix;
		case PRIMERA:
			return faseGrupoPrefix;
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return null;
	}

	/**
	 * M&eacute;todo que retorna el path de las planillas de apuesta segun fase enviada
	 * @param fase La fase a cargar
	 * @return El path encontrado
	 */
	private String getReadPath(Fase fase) {
		switch (fase) {
		case CUARTOS:
			return cuartosReadPath;
		case FINAL:
			break;
		case OCTAVOS:
			return octavosReadPath;
		case PRIMERA:
			return faseGrupoReadPath;
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return null;
	}

	/**
	 * M&eacute;todo que verifica que el archivo a cargar sea de la extencion correcta
	 * @param fileEntry EL archivo a cargar
	 * @return Verdadero para archivo valido
	 */
	private boolean checkExtension(File fileEntry) {
		String extension = FilenameUtils.getExtension(fileEntry.getName());
		for (String ext : xlsxExtension) {
			if (extension.compareToIgnoreCase(ext) == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * M&eacute;todo que crea el Participante si es que este no existe
	 * @param name El nombre del participante
	 * @param fileEntry El archivo a cargar 
	 * @param fase La fase del archivo a cargar
	 * @return El participante con partidos cargados
	 * @throws IOException
	 */
	private Participante getParticipante(String name, File fileEntry, Fase fase) throws IOException {
		Participante participante = null;
		for (Participante p : participantes) {
			if (p.getNombre().equals(name)) {
				participante = p;
				break;
			}
		}
		if (participante == null) {
			participante = new Participante(i++);
			participante.setNombre(name);
			participantes.add(participante);
		}
		participante.getPartidos().addAll(loadPartidos(participante, fileEntry, fase));
		return participante;
	}

	/**
	 * M&eacute;todo que carga los partidos del participante enviado
	 * @param participante EL participante a cargar
	 * @param fileEntry El archivo desde el cual se cargaran apuestas
	 * @param fase La fase que se esta cargando
	 * @return Arreglo de partidos cargados
	 * @throws IOException
	 */
	private ArrayList<Partido> loadPartidos(Participante participante, File fileEntry, Fase fase) throws IOException {
		ArrayList<Partido> resultado = new ArrayList<>();
		FileInputStream excelFile = new FileInputStream(fileEntry);
		Workbook workbook = new XSSFWorkbook(excelFile);
		switch (fase) {
		case CUARTOS:
			ArrayList<Partido> partidosC = getPartidosFromSheet(workbook, cuartosSheet, fase);
			resultado.addAll(partidosC);
			break;
		case FINAL:
			break;
		case OCTAVOS:
			ArrayList<Partido> partidosO = getPartidosFromSheet(workbook, octavosSheet, fase);
			resultado.addAll(partidosO);
			break;
		case PRIMERA:
			for (String grupoName : faseGrupoSheets) {
				ArrayList<Partido> partidosP = getPartidosFromSheet(workbook, grupoName, fase);
				resultado.addAll(partidosP);
				Grupo grupo = new Grupo();
				grupo.setNombre(grupoName);
				grupo.setPartidos(partidosP);
				participante.getGrupos().add(grupo);
			}
			break;
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return resultado;
	}

	/**
	 * M&eacute;todo que carga los partidos desde la hoja especificada
	 * @param workbook  El archivo excel a cargar
	 * @param sheet La hoja a cargar
	 * @param fase La fase de las apuestas a cargar
	 * @return Arreglo de partidos cargados
	 */
	private ArrayList<Partido> getPartidosFromSheet(Workbook workbook, String sheet, Fase fase) {
		ArrayList<Partido> resultado = new ArrayList<>();
		switch (fase) {
		case CUARTOS:
			Sheet hojaC = workbook.getSheet(sheet);
			resultado.add(getPartidoOtrasFases(hojaC, fase, 4));
			resultado.add(getPartidoOtrasFases(hojaC, fase, 10));
			resultado.add(getPartidoOtrasFases(hojaC, fase, 16));
			resultado.add(getPartidoOtrasFases(hojaC, fase, 22));
			break;
		case FINAL:
			break;
		case OCTAVOS:
			Sheet hojaO = workbook.getSheet(sheet);
			resultado.add(getPartidoOtrasFases(hojaO, fase, 4));
			resultado.add(getPartidoOtrasFases(hojaO, fase, 10));
			resultado.add(getPartidoOtrasFases(hojaO, fase, 16));
			resultado.add(getPartidoOtrasFases(hojaO, fase, 22));
			resultado.add(getPartidoOtrasFases(hojaO, fase, 28));
			resultado.add(getPartidoOtrasFases(hojaO, fase, 34));
			resultado.add(getPartidoOtrasFases(hojaO, fase, 40));
			resultado.add(getPartidoOtrasFases(hojaO, fase, 46));
			break;
		case PRIMERA:
			Sheet hojaP = workbook.getSheet(sheet);
			resultado.add(getPartidoPrimeraFase(17, hojaP, fase, sheet));
			resultado.add(getPartidoPrimeraFase(18, hojaP, fase, sheet));
			resultado.add(getPartidoPrimeraFase(19, hojaP, fase, sheet));
			resultado.add(getPartidoPrimeraFase(20, hojaP, fase, sheet));
			resultado.add(getPartidoPrimeraFase(21, hojaP, fase, sheet));
			resultado.add(getPartidoPrimeraFase(22, hojaP, fase, sheet));
			break;
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		default:
			break;
		}
		return resultado;
	}

	/**
	 * M&eacute;todo que retorna un partido no de primera fase 
	 * @param hoja La hoja desde la que se cargaran apuestas
	 * @param fase La fase de las apuesta 
	 * @param rowInicio Fila de inicio
	 * @return El partido cargado
	 */
	private Partido getPartidoOtrasFases(Sheet hoja, Fase fase, int rowInicio) {
		Partido partido = new Partido(true);
		partido.setFase(fase);
		partido.setLocal(getEquipo(getCell("B" + rowInicio, hoja).getStringCellValue()));
		partido.setVisita(getEquipo(getCell("B" + (rowInicio + 4), hoja).getStringCellValue()));

		partido.setFecha(HSSFDateUtil.getJavaDate(getCell("B" + (rowInicio + 2), hoja).getNumericCellValue()));
		addHour(partido.getFecha(), getCell("B" + (rowInicio + 3), hoja));

		partido.setGolesLocales(ValueUtil.floatToShort(getCell("C" + rowInicio, hoja)));
		partido.setGolesVisita(ValueUtil.floatToShort(getCell("C" + (rowInicio + 4), hoja)));

		if (partido.getGolesLocales() == partido.getGolesVisita()) {
			partido.setPenalesLocal(ValueUtil.floatToShort(getCell("D" + rowInicio, hoja)));
			partido.setPenalesVisita(ValueUtil.floatToShort(getCell("D" + (rowInicio + 4), hoja)));
		}

		partido.setResultadoReal(getPartidoFromResults(partido));
		return partido;
	}

	/**
	 * M&eacute;todo que carga apuesta de primera fase
	 * @param row La fila a cargar
	 * @param hoja La hoja desde la que se cargara
	 * @param fase La fase de la apuesta
	 * @param grupoName EL nombre del grupo a cargar
	 * @return El partido cargado
	 */
	private Partido getPartidoPrimeraFase(int row, Sheet hoja, Fase fase, String grupoName) {
		Partido partido = new Partido(true);
		partido.setFase(fase);
		partido.setLocal(getEquipo(getCell("F" + row, hoja).getStringCellValue()));
		partido.setVisita(getEquipo(getCell("H" + row, hoja).getStringCellValue()));
		partido.setFecha(HSSFDateUtil.getJavaDate(getCell("B" + row, hoja).getNumericCellValue()));
		addHour(partido.getFecha(), getCell("C" + row, hoja));
		partido.setGolesLocales(ValueUtil.floatToShort(getCell("G" + row, hoja)));
		partido.setGolesVisita(ValueUtil.floatToShort(getCell("I" + row, hoja)));
		partido.setResultadoReal(getPartidoFromResults(partido));
		partido.setGrupo(getGrupo(grupoName));
		return partido;
	}

	/**
	 * M&eacute;todo que retorna el grupo segun nombre enviado
	 * @param grupoName El nombre del grupo
	 * @return El grupo encontrado
	 */
	private Grupo getGrupo(String grupoName) {
		for (Grupo grupo : grupos) {
			if (grupo.getNombre().equals(grupoName)) {
				return grupo;
			}
		}
		Grupo result = new Grupo(grupoName);
		grupos.add(result);
		return result;
	}

	/**
	 * M&eacute;todo que agrega horas a la fecha enviada
	 * @param fecha La fecha a agregar horas
	 * @param cell La celda con el valor a agregar horas
	 */
	@SuppressWarnings("deprecation")
	private void addHour(Date fecha, Cell cell) {
		if (cell != null && fecha != null) {
			Double value = cell.getNumericCellValue();
			Date date = HSSFDateUtil.getJavaDate(value);
			if (value != null && date != null) {
				fecha.setHours(date.getHours());
				fecha.setMinutes(date.getMinutes());
				fecha.setSeconds(date.getSeconds());
			}
		}
	}

	/**
	 * M&eacute;todo que retorna la celda segun columna y fila
	 * @param cellCR Columna/fila a buscar
	 * @param hoja La hoja en la que se buscara celda
	 * @return La celda encontrada
	 */
	private Cell getCell(String cellCR, Sheet hoja) {
		if (hoja != null) {
			try {
				CellReference cellReference = new CellReference(cellCR);
				Row row = hoja.getRow(cellReference.getRow());
				Cell cell = row.getCell(cellReference.getCol());
				return cell;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * M&eacute;todo que retorn el equipo con el nombre enviado
	 * @param equipoName El nombre del equipo
	 * @return El equipo encontrado
	 */
	private Equipo getEquipo(String equipoName) {
		for (Equipo equipo : equipos) {
			if (equipo.getNombre().equals(equipoName)) {
				return equipo;
			}
		}
		Equipo e = new Equipo(equipoName);
		equipos.add(e);
		return e;
	}
}