package cl.aplacplac.pollafacil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
	private final String[] xlsxExtension = new String[] { 
			"xlsx"
			, "xlsm" 
	};
	private final String[] gruposPrimerFase = new String[] { 
			"GRUPO A"
			, "GRUPO B"
			, "GRUPO C"
			, "GRUPO D"
			, "GRUPO E"
			, "GRUPO F"
			, "GRUPO G"
			, "GRUPO H" 
	};

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

	@Autowired
	private Environment environment;
	
	// Archivos resultados
	private String resultFile;
	
	// Fase Grupo
	private String readPathFaseGrupo;
	private String prefixFaseGrupo;
	// Fase Grupo
	private String readPathOctavos;
	private String prefixOctavos;

	private ArrayList<Participante> participantes;
	private ArrayList<Partido> resultados;
	private ArrayList<Equipo> equipos;
	private ArrayList<Grupo> grupos;
	private ArrayList<Regla> reglas;

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
		// cargado variables
		this.resultFile = environment.getProperty("pollafacil.resultFile");
		
		this.readPathFaseGrupo = environment.getProperty("pollafacil.readPathFaseGrupo");
		this.prefixFaseGrupo = environment.getProperty("pollafacil.prefixFaseGrupo");
		
		this.readPathOctavos = environment.getProperty("pollafacil.readPathOctavos");
		this.prefixFaseGrupo = environment.getProperty("pollafacil.prefixOctavos");
		refresh();
	}

	@RequestMapping(path = "/refresh", method = { RequestMethod.GET })
	public ModelAndView refresh() throws Exception {
		logger.info("\n*******************************************" + 
				"\nLlamado a Refresh!"
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
		logger.info("\n*******************************************" 
				+ "\nLlamado a viewParticipante: " + id + "!"
				+ "\n*******************************************\n");
		ModelAndView mav = new ModelAndView("participante");
		Participante p = getParticipante(id);
		mav.getModel().put("domain", "pollafacil");
		mav.getModel().put("equipos", equipos);
		if (p != null) {
			mav.getModel().put("participante", p);
			mav.getModel().put("partidos", p.getPartidos());
			mav.getModel().put("puntajes", p.getPuntajes());
		}
		return mav;
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
		mav.getModel().put("domain", "pollafacil");
		mav.getModel().put("today", new Date());
		mav.getModel().put("participantes", participantes);
		mav.getModel().put("equipos", equipos);
		mav.getModel().put("resultados", resultados);
		mav.getModel().put("fases", Fase.values());
		return mav;
	}

	private void loadResult() throws Exception {
		File loadFile = new File(resultFile);
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
					resultados.addAll(getResultados(Fase.PRIMERA, loadFile));
					// TODO agregar el resto de resultados

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
				+ "\nNo se logro cargar resultados: "
				+ loadFile.getName() + "\n*******************************************\n");
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
			break;
		case FINAL:
			break;
		case OCTAVOS:
			break;
		case PRIMERA:
			return getPartidosFromTo(fase, workbook.getSheet("PRIMERA FASE"), 4, 51);
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return null;
	}

	private ArrayList<Partido> getPartidosFromTo(Fase fase, Sheet sheet, int fromRow, int toRow) throws Exception {
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
			for(Grupo grupo : participante.getGrupos()) {
				equipos.addAll(grupo.getClasificados());
			}
			for (Equipo equipo : equipos) {
				for (Equipo equipoReal : PollaFacil.this.equipos) {
					if(equipoReal.getFasesCompletadas().contains(Fase.PRIMERA) && equipoReal.getNombre().compareToIgnoreCase(equipo.getNombre()) == 0) {
						puntaje.setPuntosEquiposClasificados((short) (puntaje.getPuntosEquiposClasificados() + regla.getEquipoClasificado()));
					}
				}	
			}
			participante.setPuntos((short) (participante.getPuntos() + puntaje.getPuntosEquiposClasificados()));
		}
		participante.getNombre();
	}

	private void setPuntosFromPartido(Participante participante, Fase fase, Partido partido, Regla regla) {
		Partido resultadoReal = getPartidoFromResults(partido);
		Puntaje puntaje = getPuntaje(participante, fase);
		int result = 0;
		if (resultadoReal.getEstado() == 2 && resultadoReal.getGolesLocales() != null
				&& resultadoReal.getGolesVisita() != null) {
			if (resultadoReal.getGolesLocales() == partido.getGolesLocales()
					&& resultadoReal.getGolesVisita() == partido.getGolesVisita()) {
				puntaje.setPuntosMarcador((short) (puntaje.getPuntosMarcador() + regla.getPuntosMarcador()));
				result += regla.getPuntosMarcador();
			}
			if (resultadoReal.getGolesLocales() > resultadoReal.getGolesVisita()
					&& partido.getGolesLocales() > partido.getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
			if (resultadoReal.getGolesLocales() < resultadoReal.getGolesVisita()
					&& partido.getGolesLocales() < partido.getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
			if (resultadoReal.getGolesLocales() == resultadoReal.getGolesVisita()
					&& partido.getGolesLocales() == partido.getGolesVisita()) {
				result += regla.getPuntosResultado();
				puntaje.setPuntosResultado((short) (puntaje.getPuntosResultado() + regla.getPuntosResultado()));
			}
		}
		partido.setPuntos((short) result);
		participante.addPuntos((short) result);
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
			break;
		case FINAL:
			break;
		case OCTAVOS:
			break;
		case PRIMERA:
			calcularPrimeraFase();
			break;
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
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
			regla.setEquipoClasificado(floatToShort(getCell(column + 15, hoja)));
			return regla;
		case FINAL:
			regla.setPuntosResultado(floatToShort(getCell(column + 23, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 24, hoja)));
			regla.setEquipoClasificado(floatToShort(getCell(column + 25, hoja)));
			return regla;
		case OCTAVOS:
			regla.setPuntosResultado(floatToShort(getCell(column + 8, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 9, hoja)));
			regla.setEquipoClasificado(floatToShort(getCell(column + 10, hoja)));
			return regla;
		case PRIMERA:
			regla.setPuntosResultado(floatToShort(getCell(column + 3, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 4, hoja)));
			regla.setEquipoClasificado(floatToShort(getCell(column + 5, hoja)));
			return regla;
		case SEMIFINAL:
			regla.setPuntosResultado(floatToShort(getCell(column + 18, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 19, hoja)));
			regla.setEquipoClasificado(floatToShort(getCell(column + 20, hoja)));
			return regla;
		case TERCERPUESTO:
			regla.setPuntosResultado(floatToShort(getCell(column + 23, hoja)));
			regla.setPuntosMarcador(floatToShort(getCell(column + 24, hoja)));
			regla.setEquipoClasificado(floatToShort(getCell(column + 25, hoja)));
			return regla;
		}
		return null;
	}

	private void loadPlanillas() throws Exception {
		if (readPathFaseGrupo != null) {
			File folder = new File(readPathFaseGrupo);
			if(folder.exists()) {
				logger.info("folder.listFiles().length: " + folder.listFiles().length);
				for (final File fileEntry : folder.listFiles()) {
					if (!(fileEntry.isDirectory()) && checkExtension(fileEntry)) {
						try {
							String name = fileEntry.getName();
							name = name.replaceAll("\\Q" + prefixFaseGrupo + "\\E", "");
							for (String ext : xlsxExtension) {
								name = name.replaceAll("\\Q" + "." + ext + "\\E", "");
							}
							participantes.add(getParticipante(name, fileEntry));
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("\n*******************************************" 
									+ "Error cargando " + fileEntry.getName() + ": " + e.getMessage()
									+ "\n*******************************************\n");
							throw e;
						}
					} else {
						logger.error("\n*******************************************\n" 
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

	private boolean checkExtension(File fileEntry) {
		String extension = FilenameUtils.getExtension(fileEntry.getName());
		for (String ext : xlsxExtension) {
			if (extension.compareToIgnoreCase(ext) == 0) {
				return true;
			}
		}
		return false;
	}

	private Participante getParticipante(String name, File fileEntry) throws IOException {
		Participante participante = new Participante(i++);
		participante.setNombre(name);
		participante.getPartidos().addAll(loadPartidos(participante, fileEntry));
		return participante;
	}

	private ArrayList<Partido> loadPartidos(Participante participante, File fileEntry) throws IOException {
		ArrayList<Partido> resultado = new ArrayList<>();
		FileInputStream excelFile = new FileInputStream(fileEntry);
		Workbook workbook = new XSSFWorkbook(excelFile);
		for (String grupoName : gruposPrimerFase) {
			ArrayList<Partido> partidosPorGrupo = getPartidosDesdeHoja(workbook, grupoName, Fase.PRIMERA);
			resultado.addAll(partidosPorGrupo);
			Grupo grupo = new Grupo();
			grupo.setNombre(grupoName);
			grupo.setPartidos(partidosPorGrupo);
			participante.getGrupos().add(grupo);
		}
		return resultado;
	}

	private ArrayList<Partido> getPartidosDesdeHoja(Workbook workbook, String grupoName, Fase fase) {
		ArrayList<Partido> resultado = new ArrayList<>();
		Sheet hoja = workbook.getSheet(grupoName);
		resultado.add(getPartidoFromRow(17, hoja, fase, grupoName));
		resultado.add(getPartidoFromRow(18, hoja, fase, grupoName));
		resultado.add(getPartidoFromRow(19, hoja, fase, grupoName));
		resultado.add(getPartidoFromRow(20, hoja, fase, grupoName));
		resultado.add(getPartidoFromRow(21, hoja, fase, grupoName));
		resultado.add(getPartidoFromRow(22, hoja, fase, grupoName));
		return resultado;
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