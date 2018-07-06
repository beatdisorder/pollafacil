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
import cl.aplacplac.pollafacil.vo.Equipo;
import cl.aplacplac.pollafacil.vo.Fase;
import cl.aplacplac.pollafacil.vo.Grupo;
import cl.aplacplac.pollafacil.vo.Participante;
import cl.aplacplac.pollafacil.vo.Partido;
import cl.aplacplac.pollafacil.vo.Puntaje;
import cl.aplacplac.pollafacil.vo.Regla;

@Component
@RestController
@RequestMapping("/pollafacil")
@SpringBootApplication
public class PollaFacil {
	private final static Logger logger = Logger.getLogger(PollaFacil.class);
	private final String[] xlsxExtension = new String[] { "xlsx", "xlsm" };

	@Autowired
	Environment env;

	private final String hojaReglas = "REGLAS";
	@SuppressWarnings("unused")
	private final String hojaPrimera = "PRIMERA";
	@SuppressWarnings("unused")
	private final String hojaCuartos = "CUARTOS";
	@SuppressWarnings("unused")
	private final String hojaOctavos = "OCTAVOS";
	@SuppressWarnings("unused")
	private final String hojaSemiFinal = "SEMIFINAL";
	@SuppressWarnings("unused")
	private final String hojaTercerPuesto = "TERCERPUESTO";
	@SuppressWarnings("unused")
	private final String hojaFinal = "FINAL";
	private int i = 0;

	@Value("${pollaFacil.domain}")
	private String domain;

	// Archivos resultados
	@Value("${pollaFacil.faseActual}")
	private Integer faseActual;

	// Archivos resultados
	@Value("${pollaFacil.resultados.resultFile}")
	private String resultFile;
	@Value("${pollaFacil.resultados.reglasSheet}")
	private String reglasSheet;
	@Value("${pollaFacil.resultados.primeraFaseSheet}")
	private String primeraFaseSheet;
	@Value("${pollaFacil.resultados.octavosFaseSheet}")
	private String octavosFaseSheet;
	@Value("${pollaFacil.resultados.cuartosFaseSheet}")
	private String cuartosFaseSheet;

	// Fase Grupo
	@Value("${pollaFacil.faseGrupo.readPath}")
	private String faseGrupoReadPath;
	@Value("${pollaFacil.faseGrupo.prefix}")
	private String faseGrupoPrefix;
	private String[] faseGrupoSheets;

	// Octavos
	@Value("${pollaFacil.octavos.readPath}")
	private String octavosReadPath;
	@Value("${pollaFacil.octavos.prefix}")
	private String octavosPrefix;
	@Value("${pollaFacil.octavos.sheet}")
	private String octavosSheet;
	
	// Cuartos
	@Value("${pollaFacil.cuartos.readPath}")
	private String cuartosReadPath;
	@Value("${pollaFacil.cuartos.prefix}")
	private String cuartosPrefix;
	@Value("${pollaFacil.cuartos.sheet}")
	private String cuartosSheet;
	
	private ArrayList<Participante> participantes;
	private ArrayList<Partido> resultados;
	private ArrayList<Equipo> equipos;
	private ArrayList<Grupo> grupos;
	private ArrayList<Regla> reglas;

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

	@PostConstruct
	public void construct() throws Exception {
		faseGrupoSheets = env.getProperty("pollaFacil.faseGrupo.sheets").split("-");
		refresh();
	}

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

	@RequestMapping(path = "/participante", method = { RequestMethod.GET })
	public ModelAndView viewParticipante(@RequestParam(value = "id") int id) throws Exception {
		logger.info("\n*******************************************" + "\nLlamado a viewParticipante: " + id + "!"
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

	private Participante getParticipante(int id) {
		for (Participante participante : participantes) {
			if (id == participante.getId()) {
				return participante;
			}
		}
		return null;
	}

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

	private ArrayList<Partido> getResultados(Fase fase, File loadFile) throws Exception {
		return getPartidos(fase, new XSSFWorkbook(new FileInputStream(loadFile)));
	}

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

	@SuppressWarnings("incomplete-switch")
	private ArrayList<Partido> getPartidosOtrasFases(Fase fase, Workbook workbook, String sheetName) throws Exception {
		if (workbook.getSheet(sheetName) == null) {
			throw new Exception("Hoja no existe");
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

	private Partido getResultadoPartidoFromRow(Fase fase, int i, Sheet sheet) {
		Partido result = new Partido();
		result.setFase(fase);
		result.setLocal(getEquipo(getCell("B" + i, sheet).getStringCellValue()));
		result.setVisita(getEquipo(getCell("F" + i, sheet).getStringCellValue()));
		result.setFecha(HSSFDateUtil.getJavaDate(getCell("H" + i, sheet).getNumericCellValue()));
		addHour(result.getFecha(), getCell("I" + i, sheet));
		result.setGolesLocales(floatToShort(getCell("C" + i, sheet)));
		result.setGolesVisita(floatToShort(getCell("E" + i, sheet)));
		return result;
	}

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

	private void calculaPuntosParticipantes() {
		for (Fase fase : Fase.values()) {
			calculaPuntosPorFase(fase);
		}
	}

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

	private void setPuntosFromPartido(Participante participante, Fase fase, Partido partido, Regla regla) {
		Partido resultadoReal = getPartidoFromResults(partido);
		Puntaje puntaje = getPuntaje(participante, fase);
		if (resultadoReal.getEstado() == 2 && resultadoReal.getGolesLocales() != null && resultadoReal.getGolesVisita() != null) {
			partido.calculatePuntos(resultadoReal, puntaje, regla);
		}		
		participante.addPuntos(partido.getPuntos());
	}

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

	private Partido getPartidoFromResults(Partido partido) {
		for (Partido partidoR : resultados) {
			System.out.println(partidoR + " " + partido);
			if (partidoR.equals(partido)) {
				return partidoR;
			}
		}
		return null;
	}

	private Regla getReglaPorFase(Fase fase) {
		for (Regla regla : reglas) {

			if (regla == null || regla.getFase() == null) {
				System.out.println("regla => " + regla);
			}

			if (regla.getFase().equals(fase)) {
				return regla;
			}
		}
		return null;
	}

	private void calculaClasificados() throws Exception {
		for (Fase fase : Fase.values()) {
			calculaClasificadosPorFase(fase);
		}
	}

	private void calculaClasificadosPorFase(Fase fase) throws Exception {
		switch (fase) {
		case CUARTOS:
		case FINAL:
		case OCTAVOS:
		case SEMIFINAL:
		case TERCERPUESTO:
			calcularOtraFase(fase);
			break;
		case PRIMERA:
			calcularPrimeraFase();
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void calcularOtraFase(Fase fase) throws Exception {
		switch (fase) {
		case CUARTOS:
		case FINAL:
		case OCTAVOS:
		case SEMIFINAL:
		case TERCERPUESTO:
			for (Partido partido : util.filtrarPartidos(resultados, fase)) {
				if(partido.getEstado().equals(2)) {
					partido.getGanador().addFaseCompletada(fase);
				}
			}
		}
	}

	private void calcularPrimeraFase() throws Exception {
		ArrayList<String> clasificados = getClasificadosDesdeHoja();
		for (Equipo equipo : equipos) {
			for (String clasificado : clasificados) {
				if (clasificado.equals(equipo.getNombre())) {
					equipo.getFasesCompletadas().add(Fase.PRIMERA);
				}
			}
		}
	}

	@SuppressWarnings("resource")
	private ArrayList<String> getClasificadosDesdeHoja() throws Exception {
		ArrayList<String> result = new ArrayList<>();

		File loadFile = new File(resultFile);
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
					Sheet hoja = new XSSFWorkbook(new FileInputStream(loadFile)).getSheet("PRIMERA FASE");
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

	private void cargarReglas(XSSFWorkbook resultsXlsx) {
		Sheet sheetReglas = resultsXlsx.getSheet(hojaReglas);
		for (Fase fase : Fase.values()) {
			reglas.add(getRegla(fase, sheetReglas));
		}
	}

	private Regla getRegla(Fase fase, Sheet hoja) {
		Regla regla = new Regla();
		String column = "E";
		regla.setFase(fase);
		switch (fase) {
		case CUARTOS:
			regla.setPuntosResultado(floatToShort(getCell(column + 13, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 14, hoja)));
			regla.setPuntosEquipoClasificado(floatToShort(getCell(column + 15, hoja)));
			return regla;
		case FINAL:
			regla.setPuntosResultado(floatToShort(getCell(column + 23, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 24, hoja)));
			regla.setPuntosEquipoClasificado(floatToShort(getCell(column + 25, hoja)));
			return regla;
		case OCTAVOS:
			regla.setPuntosResultado(floatToShort(getCell(column + 8, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 9, hoja)));
			regla.setPuntosEquipoClasificado(floatToShort(getCell(column + 10, hoja)));
			return regla;
		case PRIMERA:
			regla.setPuntosResultado(floatToShort(getCell(column + 3, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 4, hoja)));
			regla.setPuntosEquipoClasificado(floatToShort(getCell(column + 5, hoja)));
			return regla;
		case SEMIFINAL:
			regla.setPuntosResultado(floatToShort(getCell(column + 18, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 19, hoja)));
			regla.setPuntosEquipoClasificado(floatToShort(getCell(column + 20, hoja)));
			return regla;
		case TERCERPUESTO:
			regla.setPuntosResultado(floatToShort(getCell(column + 23, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 24, hoja)));
			regla.setPuntosEquipoClasificado(floatToShort(getCell(column + 25, hoja)));
			return regla;
		}
		return null;
	}

	private void loadPlanillas() throws Exception {
		switch (faseActual) {
		case 6:
			loadFase(Fase.FINAL);
		case 5:
			loadFase(Fase.TERCERPUESTO);
		case 4:
			loadFase(Fase.SEMIFINAL);
		case 3:
			loadFase(Fase.CUARTOS);
		case 2:
			loadFase(Fase.OCTAVOS);
		case 1:
			loadFase(Fase.PRIMERA);
		}
	}

	private void loadFase(Fase fase) throws Exception {
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

	private boolean checkExtension(File fileEntry) {
		String extension = FilenameUtils.getExtension(fileEntry.getName());
		for (String ext : xlsxExtension) {
			if (extension.compareToIgnoreCase(ext) == 0) {
				return true;
			}
		}
		return false;
	}

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

	private ArrayList<Partido> getPartidosFromSheet(Workbook workbook, String sheet, Fase fase) {
		ArrayList<Partido> resultado = new ArrayList<>();
		switch (fase) {
		case CUARTOS:
			Sheet hojaC = workbook.getSheet(sheet);
			resultado.add(getPartidoFromRows(hojaC, fase, 4));
			resultado.add(getPartidoFromRows(hojaC, fase, 10));
			resultado.add(getPartidoFromRows(hojaC, fase, 16));
			resultado.add(getPartidoFromRows(hojaC, fase, 22));
			break;
		case FINAL:
			break;
		case OCTAVOS:
			Sheet hojaO = workbook.getSheet(sheet);
			resultado.add(getPartidoFromRows(hojaO, fase, 4));
			resultado.add(getPartidoFromRows(hojaO, fase, 10));
			resultado.add(getPartidoFromRows(hojaO, fase, 16));
			resultado.add(getPartidoFromRows(hojaO, fase, 22));
			resultado.add(getPartidoFromRows(hojaO, fase, 28));
			resultado.add(getPartidoFromRows(hojaO, fase, 34));
			resultado.add(getPartidoFromRows(hojaO, fase, 40));
			resultado.add(getPartidoFromRows(hojaO, fase, 46));
			break;
		case PRIMERA:
			Sheet hojaP = workbook.getSheet(sheet);
			resultado.add(getPartidoFromRow(17, hojaP, fase, sheet));
			resultado.add(getPartidoFromRow(18, hojaP, fase, sheet));
			resultado.add(getPartidoFromRow(19, hojaP, fase, sheet));
			resultado.add(getPartidoFromRow(20, hojaP, fase, sheet));
			resultado.add(getPartidoFromRow(21, hojaP, fase, sheet));
			resultado.add(getPartidoFromRow(22, hojaP, fase, sheet));
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

	private Partido getPartidoFromRows(Sheet hoja, Fase fase, int rowInicio) {
		Partido partido = new Partido(true);
		partido.setFase(fase);
		partido.setLocal(getEquipo(getCell("B" + rowInicio, hoja).getStringCellValue()));
		partido.setVisita(getEquipo(getCell("B" + (rowInicio + 4), hoja).getStringCellValue()));

		partido.setFecha(HSSFDateUtil.getJavaDate(getCell("B" + (rowInicio + 2), hoja).getNumericCellValue()));
		addHour(partido.getFecha(), getCell("B" + (rowInicio + 3), hoja));

		partido.setGolesLocales(floatToShort(getCell("C" + rowInicio, hoja)));
		partido.setGolesVisita(floatToShort(getCell("C" + (rowInicio + 4), hoja)));

		if (partido.getGolesLocales() == partido.getGolesVisita()) {
			partido.setPenalesLocal(floatToShort(getCell("D" + rowInicio, hoja)));
			partido.setPenalesVisita(floatToShort(getCell("D" + (rowInicio + 4), hoja)));
		}

		partido.setResultadoReal(getResultadoReal(partido));
		return partido;
	}

	private Partido getPartidoFromRow(int row, Sheet hoja, Fase fase, String grupoName) {
		Partido partido = new Partido(true);
		partido.setFase(fase);
		partido.setLocal(getEquipo(getCell("F" + row, hoja).getStringCellValue()));
		partido.setVisita(getEquipo(getCell("H" + row, hoja).getStringCellValue()));
		partido.setFecha(HSSFDateUtil.getJavaDate(getCell("B" + row, hoja).getNumericCellValue()));
		addHour(partido.getFecha(), getCell("C" + row, hoja));
		partido.setGolesLocales(floatToShort(getCell("G" + row, hoja)));
		partido.setGolesVisita(floatToShort(getCell("I" + row, hoja)));
		partido.setResultadoReal(getResultadoReal(partido));
		partido.setGrupo(getGrupo(grupoName));
		return partido;
	}

	private Partido getResultadoReal(Partido partido) {
		for (Partido resultado : resultados) {
			if (resultado.equals(partido)) {
				return resultado;
			}
		}
		return null;
	}

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

	@SuppressWarnings("deprecation")
	private static Short floatToShort(Cell cell) {
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return null;
		}
		Double x = cell.getNumericCellValue();
		if (x < Short.MIN_VALUE) {
			return Short.MIN_VALUE;
		}
		if (x > Short.MAX_VALUE) {
			return Short.MAX_VALUE;
		}
		return (short) Math.round(x);
	}
}