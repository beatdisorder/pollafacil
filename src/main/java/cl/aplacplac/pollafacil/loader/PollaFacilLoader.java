package cl.aplacplac.pollafacil.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cl.aplacplac.pollafacil.PollaFacil;
import cl.aplacplac.pollafacil.util.ValueUtil;
import cl.aplacplac.pollafacil.vo.Equipo;
import cl.aplacplac.pollafacil.vo.Fase;
import cl.aplacplac.pollafacil.vo.Grupo;
import cl.aplacplac.pollafacil.vo.Participante;
import cl.aplacplac.pollafacil.vo.Partido;
import cl.aplacplac.pollafacil.vo.PollaFacilConfiguracion;
import cl.aplacplac.pollafacil.vo.Regla;

/**
 * Clase encargada de cargar un objecto {@link PollaFacil} a partir de una
 * {@link Map} de configuraciones
 * 
 * @author retsuchan
 *
 */
public class PollaFacilLoader {
	// Logger de la clase
	private final static Logger logger = Logger.getLogger(PollaFacilLoader.class);

	private PollaFacilConfiguracion configuracion;
	private PollaFacil pollafacil;
	private int contadorId;

	public PollaFacilLoader() {
	}
	
	public PollaFacilLoader load(PollaFacilConfiguracion configuracion) throws Exception {
		this.setConfiguracion(configuracion);
		contadorId = 0;
		logger.info("\n*******************************************\n");
		logger.info("Configuracion recibida" + configuracion);
		logger.info("\n*******************************************\n");
		loadPollaFacil();
		return this;
	}

	/**
	 * M&eacute;todo que calcula los puntos de los participantes
	 * 
	 * @throws Exception
	 */
	private void loadPollaFacil() throws Exception {
		logger.info("\n*******************************************" 
				+ "\nLlamado a Refresh!"
				+ "\n*******************************************\n");
		pollafacil = new PollaFacil(configuracion.getCampeonato());
		loadResult();
		loadPlanillas();
		calculatePoints();
	}
	
	/**
	 * M&eacute;todo que carga los resultados desde archivo de resultados
	 */
	private void loadResult() throws Exception {
		File loadFile = new File(configuracion.getResultFile());
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
 					switch (configuracion.getFaseActual()) {
					case FINAL:
					case TERCERPUESTO:
					case SEMIFINAL:
					case CUARTOS:
						this.pollafacil.getResultados().addAll(getResultados(Fase.CUARTOS, loadFile));
					case OCTAVOS:
						this.pollafacil.getResultados().addAll(getResultados(Fase.OCTAVOS, loadFile));
					case PRIMERA:
						this.pollafacil.getResultados().addAll(getResultados(Fase.PRIMERA, loadFile));
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"\n*******************************************" 
							+ "Error cargando " + loadFile.getName()
							+ ": " + e.getMessage() 
							+ "\n*******************************************\n");
					throw e;
				}
			} else {
				logger.error("\n*******************************************\n" 
						+ "No se logro cargar resultados: "
						+ loadFile.getName() 
						+ "\n*******************************************\n");
			}
		} else {
			throw new Exception("No se encontro el archivo de resultados: " + configuracion.getResultFile());
		}
	}
	
	/**
	 * M&eacute;todo que carga las pantillas de apuestas
	 * 
	 * @throws Exception
	 */
	private void loadPlanillas() throws Exception {
		switch (configuracion.getFaseActual()) {
		case FINAL:
			loadPlanillasPorFase(Fase.FINAL);
		case TERCERPUESTO:
			loadPlanillasPorFase(Fase.TERCERPUESTO);
		case SEMIFINAL:
			loadPlanillasPorFase(Fase.SEMIFINAL);
		case CUARTOS:
			loadPlanillasPorFase(Fase.CUARTOS);
		case OCTAVOS:
			loadPlanillasPorFase(Fase.OCTAVOS);
		case PRIMERA:
			loadPlanillasPorFase(Fase.PRIMERA);
		}
	}
	
	/**
	 * M&eacute;todo que carga las planillas de la fase solicitada
	 * 
	 * @param fase La fase a cargar
	 * @throws Exception
	 */
	private void loadPlanillasPorFase(Fase fase) throws Exception {
		if (configuracion.getFaseGrupoReadPath() != null) {
			File folder = new File(getReadPath(fase));
			if (folder.exists()) {
				logger.info("folder.listFiles().length: " + folder.listFiles().length);
				for (final File fileEntry : folder.listFiles()) {
					if (!(fileEntry.isDirectory()) && checkExtension(fileEntry)) {
						try {
							String name = fileEntry.getName();
							name = name.replaceAll("\\Q" + getPrefix(fase) + "\\E", "");
							for (String ext : configuracion.getXlsxExtension()) {
								name = name.replaceAll("\\Q" + "." + ext + "\\E", "");
							}
							name = name.replaceAll("\\Q" + "_" + "\\E", " ");
							getParticipante(name.trim(), fileEntry, fase);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("\n*******************************************\n" 
									+ "Error cargando " + fileEntry.getName() + ": " + e.getMessage()
									+ "\n*******************************************\n");
							throw e;
						}
					} else {
						logger.error(
								"\n*******************************************\n" 
								+ "No se logro cargar planilla: " + fileEntry.getName() 
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
	 * 
	 * @param fase La fase a obtener prefijo
	 * @return EL prefijo
	 */
	private String getPrefix(Fase fase) {
		switch (fase) {
		case CUARTOS:
			return configuracion.getCuartosPrefix();
		case FINAL:
			break;
		case OCTAVOS:
			return configuracion.getOctavosPrefix();
		case PRIMERA:
			return configuracion.getFaseGrupoPrefix();
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return null;
	}

	/**
	 * M&eacute;todo que retorna el path de las planillas de apuesta segun fase
	 * enviada
	 * 
	 * @param fase La fase a cargar
	 * @return El path encontrado
	 */
	private String getReadPath(Fase fase) {
		switch (fase) {
		case CUARTOS:
			return configuracion.getCuartosReadPath();
		case FINAL:
			break;
		case OCTAVOS:
			return configuracion.getOctavosReadPath();
		case PRIMERA:
			return configuracion.getFaseGrupoReadPath();
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return null;
	}

	/**
	 * M&eacute;todo que verifica que el archivo a cargar sea de la extencion
	 * correcta
	 * 
	 * @param fileEntry EL archivo a cargar
	 * @return Verdadero para archivo valido
	 */
	private boolean checkExtension(File fileEntry) {
		String extension = FilenameUtils.getExtension(fileEntry.getName());
		for (String ext : configuracion.getXlsxExtension()) {
			if (extension.compareToIgnoreCase(ext) == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * M&eacute;todo que crea el Participante si es que este no existe
	 * 
	 * @param name      El nombre del participante
	 * @param fileEntry El archivo a cargar
	 * @param fase      La fase del archivo a cargar
	 * @return El participante con partidos cargados
	 * @throws IOException
	 */
	private Participante getParticipante(String name, File fileEntry, Fase fase) throws IOException {
		Participante participante = null;
		for (Participante p : pollafacil.getParticipantes()) {
			if (p.getNombre().equals(name)) {
				participante = p;
				break;
			}
		}
		if (participante == null) {
			participante = new Participante(contadorId++);
			participante.setNombre(name);
			pollafacil.getParticipantes().add(participante);
		}
		participante.getPartidos().addAll(loadPartidos(participante, fileEntry, fase));
		return participante;
	}

	/**
	 * M&eacute;todo que carga los partidos del participante enviado
	 * 
	2 * @param participante EL participante a cargar
	 * @param fileEntry    El archivo desde el cual se cargaran apuestas
	 * @param fase         La fase que se esta cargando
	 * @return Arreglo de partidos cargados
	 * @throws IOException
	 */
	private ArrayList<Partido> loadPartidos(Participante participante, File fileEntry, Fase fase) throws IOException {
		logger.info("\n*******************************************");
		logger.info("\nCargando: " + fileEntry.getName() + ", fase: " + fase.toString());
		logger.info("\n*******************************************");
		ArrayList<Partido> resultado = new ArrayList<>();
		FileInputStream excelFile = new FileInputStream(fileEntry);
		Workbook workbook = new XSSFWorkbook(excelFile);
		switch (fase) {
		case CUARTOS:
			ArrayList<Partido> partidosC = getPartidosFromSheet(workbook, configuracion.getCuartosSheet(), fase);
			resultado.addAll(partidosC);
			break;
		case FINAL:
			break;
		case OCTAVOS:
			ArrayList<Partido> partidosO = getPartidosFromSheet(workbook, configuracion.getOctavosSheet(), fase);
			resultado.addAll(partidosO);
			break;
		case PRIMERA:
			for (String grupoName : configuracion.getFaseGrupoSheets()) {
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
	 * M&eacute;todo los resultados de una fase
	 * 
	 * @param fase     La fase a cargar resultados
	 * @param loadFile Archivo desde el cual se cargaran los archivos
	 * @return Arreglo con partidos cargados
	 * @throws Exception
	 */
	private ArrayList<Partido> getResultados(Fase fase, File loadFile) throws Exception {
		logger.error("\n*******************************************\n" 
				+ "\ngetResultados(): "
				+ loadFile.getName() 
				+ "\n*******************************************\n");
		return getPartidos(fase, new XSSFWorkbook(new FileInputStream(loadFile)));
	}

	/**
	 * M&eacute;todo que carga partidos desde un objeto excel
	 * 
	 * @param fase     La fase a cargar
	 * @param workbook El archivo excel cargado
	 * @return Arreglo de partidos
	 * @throws Exception
	 */
	private ArrayList<Partido> getPartidos(Fase fase, Workbook workbook) throws Exception {
	
		switch (fase) {
		case CUARTOS:
			return getPartidosOtrasFases(fase, workbook, configuracion.getCuartosFaseSheet());
		case FINAL:
			break;
		case OCTAVOS:
			return getPartidosOtrasFases(fase, workbook, configuracion.getOctavosFaseSheet());
		case PRIMERA:
			return getPartidosPrimerFase(fase, workbook.getSheet(configuracion.getPrimeraFaseSheet()), 4, 51);
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return null;
	}
	


	/**
	 * M&eacute;todo que carga un partido que no sea de primera fase
	 * 
	 * @param fase      La fase a cargar
	 * @param workbook  El archivo excel cargado
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
			result.addAll(getPartidosFromSheet(workbook, configuracion.getCuartosSheet(), fase));
			break;
		case FINAL:
			break;
		case OCTAVOS:
			result.addAll(getPartidosFromSheet(workbook, configuracion.getOctavosSheet(), fase));
			break;
		case SEMIFINAL:
			break;
		case TERCERPUESTO:
			break;
		}
		return result;
	}
	
	/**
	 * M&eacute;todo que carga los partidos desde la hoja especificada
	 * 
	 * @param workbook El archivo excel a cargar
	 * @param sheet    La hoja a cargar
	 * @param fase     La fase de las apuestas a cargar
	 * @return Arreglo de partidos cargados
	 */
	private ArrayList<Partido> getPartidosFromSheet(Workbook workbook, String sheet, Fase fase) {
		ArrayList<Partido> resultado = new ArrayList<>();
		switch (fase) {
		case CUARTOS:
			Sheet hojaC = workbook.getSheet(sheet);
			resultado.add(getPartidoOtrasFases(hojaC, fase, 3));
			resultado.add(getPartidoOtrasFases(hojaC, fase, 11));
			resultado.add(getPartidoOtrasFases(hojaC, fase, 19));
			resultado.add(getPartidoOtrasFases(hojaC, fase, 27));
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
			logger.error("leyendo: " + sheet);
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
	 * M&eacute;todo que carga apuesta de primera fase
	 * 
	 * @param row       La fila a cargar
	 * @param hoja      La hoja desde la que se cargara
	 * @param fase      La fase de la apuesta
	 * @param grupoName EL nombre del grupo a cargar
	 * @return El partido cargado
	 */
	private Partido getPartidoPrimeraFase(int row, Sheet hoja, Fase fase, String grupoName) {
		logger.info("\n*******************************************");
		logger.info("\nrow: " + row);
		logger.info("\nhoja: " + hoja.getSheetName());
		logger.info("\ngrupoName: " + grupoName);
		logger.info("\n*******************************************");
		Partido partido = new Partido(true);
		partido.setFase(fase);
		partido.setLocal(this.pollafacil.getEquipo(getCell("F" + row, hoja).getStringCellValue()));
		partido.setVisita(this.pollafacil.getEquipo(getCell("H" + row, hoja).getStringCellValue()));
		partido.setFecha(HSSFDateUtil.getJavaDate(getCell("B" + row, hoja).getNumericCellValue()));
		addHour(partido.getFecha(), getCell("C" + row, hoja));
		partido.setGolesLocales(ValueUtil.floatToShort(getCell("G" + row, hoja)));
		partido.setGolesVisita(ValueUtil.floatToShort(getCell("I" + row, hoja)));
		partido.setResultadoReal(this.pollafacil.getPartidoFromResults(partido));
		partido.setGrupo(this.pollafacil.getGrupo(grupoName));
		return partido;
	}
	
	/**
	 * M&eacute;todo que retorna un partido no de primera fase
	 * 
	 * @param hoja      La hoja desde la que se cargaran apuestas
	 * @param fase      La fase de las apuesta
	 * @param rowInicio Fila de inicio
	 * @return El partido cargado
	 */
	private Partido getPartidoOtrasFases(Sheet hoja, Fase fase, int rowInicio) {
		Partido partido = new Partido(true);
		partido.setFase(fase);
		
		Cell c = getCell("B" + rowInicio, hoja);
		if(c == null)
		{
			System.out.println("ACA ESTA!!!");
			throw new RuntimeException("ACA") ;
		}
		Equipo p = this.pollafacil.getEquipo(c.getStringCellValue());
		
		partido.setLocal(p);
		partido.setVisita(this.pollafacil.getEquipo(getCell("B" + (rowInicio + 4), hoja).getStringCellValue()));

		partido.setFecha(HSSFDateUtil.getJavaDate(getCell("B" + (rowInicio + 2), hoja).getNumericCellValue()));
		addHour(partido.getFecha(), getCell("B" + (rowInicio + 3), hoja));

		partido.setGolesLocales(ValueUtil.floatToShort(getCell("C" + rowInicio, hoja)));
		partido.setGolesVisita(ValueUtil.floatToShort(getCell("C" + (rowInicio + 4), hoja)));

		if (partido.getGolesLocales() == partido.getGolesVisita()) {
			partido.setPenalesLocal(ValueUtil.floatToShort(getCell("D" + rowInicio, hoja)));
			partido.setPenalesVisita(ValueUtil.floatToShort(getCell("D" + (rowInicio + 4), hoja)));
		}

		partido.setResultadoReal(this.pollafacil.getPartidoFromResults(partido));
		return partido;
	}
	
	/**
	 * M&eacute;todo que carga los partidos de primera fase
	 * 
	 * @param fase    La fase a carga
	 * @param sheet   La hoja desde la cual se cargaran los partidos
	 * @param fromRow La fila desde la que se cargaran partidos
	 * @param toRow   La fila hasta la que se cargaran los partidos
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
	 * 
	 * @param fase   La fase a cargar
	 * @param column Columna en la que se espera encontrar valor
	 * @param sheet  La hoja desde la cual se cargaran resultados
	 * @return El partido cargado
	 */
	private Partido getResultadoPartidoFromRow(Fase fase, int column, Sheet sheet) {
		Partido result = new Partido();
		result.setFase(fase);
		result.setLocal(this.pollafacil.getEquipo(getCell("B" + column, sheet).getStringCellValue()));
		result.setVisita(this.pollafacil.getEquipo(getCell("F" + column, sheet).getStringCellValue()));
		result.setFecha(HSSFDateUtil.getJavaDate(getCell("H" + column, sheet).getNumericCellValue()));
		addHour(result.getFecha(), getCell("I" + column, sheet));
		result.setGolesLocales(ValueUtil.floatToShort(getCell("C" + column, sheet)));
		result.setGolesVisita(ValueUtil.floatToShort(getCell("E" + column, sheet)));
		return result;
	}
	
	/**
	 * M&eacute;todo que agrega horas a la fecha enviada
	 * 
	 * @param fecha La fecha a agregar horas
	 * @param cell  La celda con el valor a agregar horas
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
	 * M&eacute;todo que realiza las acciones de cargado de reglas y calculo de puntos
	 * @throws Exception 
	 */
	private void calculatePoints() throws Exception {
		File loadFile = new File(configuracion.getResultFile());
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
					cargarReglas(new XSSFWorkbook(new FileInputStream(loadFile))); // Cargando reglas
					getPollaFacil().calculaClasificados(getClasificadosDesdeHoja()); // Calculando clasificados
					getPollaFacil().calculaPuntosParticipantes(); // Calculando puntos
					getPollaFacil().calculaLugares();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"\n*******************************************" 
							+ "\nError cargando " + loadFile.getName() + ": " + e.getMessage() 
							+ "\n*******************************************\n");
					throw e;
				}
			} else {
				logger.error("\n*******************************************\n" 
						+ "\nNo se logro cargar resultados: " + loadFile.getName() 
						+ "\n*******************************************\n");
			}
		} else {
			throw new Exception("No se encontro el archivo de resultados: " + configuracion.getResultFile());
		}
	}

	/**
	 * M&eacute;todo que carga reglas desde el excel enviado
	 * 
	 * @param resultsXlsx El archivo excel
	 */
	private void cargarReglas(XSSFWorkbook resultsXlsx) {
		Sheet sheetReglas = resultsXlsx.getSheet(configuracion.getReglasSheet()
				);
		for (Fase fase : Fase.values()) {
			this.pollafacil.getReglas().add(getRegla(fase, sheetReglas));
		}
	}
	
	/**
	 * M&eacute;todo que carga las reglas desde hoija enviada por parametro
	 * 
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
	 * M&eacute;todo que carga los equipos clasificados de primera fase
	 * 
	 * @return Arreglo con los equipos clasificados
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private ArrayList<String> getClasificadosDesdeHoja() throws Exception {
		ArrayList<String> result = new ArrayList<>();

		File loadFile = new File(configuracion.getResultFile());
		if (loadFile != null && loadFile.exists()) {
			if (!(loadFile.isDirectory())
					&& FilenameUtils.getExtension(loadFile.getName()).compareToIgnoreCase("xlsx") == 0) {
				try {
					Sheet hoja = new XSSFWorkbook(new FileInputStream(loadFile))
							.getSheet(configuracion.getPrimeraFaseSheet());
					for (int i = 4; i <= 19; i++) {
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
			throw new Exception("No se encontro el archivo de resultados: " + configuracion.getResultFile());
		}
		return result;
	}
	
	/**
	 * M&eacute;todo que retorna la celda segun columna y fila
	 * 
	 * @param cellCR Columna/fila a buscar
	 * @param hoja   La hoja en la que se buscara celda
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
	
	// Getter y Setter
	/**
	 * M&eacute;todo que retorna la {@link PollaFacil} actual
	 * @return
	 */
	public PollaFacil getPollaFacil() {
		return pollafacil;
	}

	/**
	 * M&eacute;todo que retorna la configuraci&oacute;n cargada
	 * @return La configuraci&oacute;n
	 */
	public PollaFacilConfiguracion getConfiguracion() {
		return configuracion;
	}

	/**	
	 * M&eacute;todo que setea La configuraci&oacute;n actual
	 * @param configuracion La nueva configuraci&oacute;n
	 */
	public void setConfiguracion(PollaFacilConfiguracion configuracion) {
		this.configuracion = configuracion;
	}
}
