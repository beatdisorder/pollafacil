package cl.aplacplac.pollafacil.vo;

public class PollaFacilConfiguracion {

	// Nombre del campeonato
	private String campeonato;

	// Arreglo con extenciones permitidas
	private String[] xlsxExtension;
	// Fase actual del campeonato, filtra lo calculado y mostrado dentro de la
	// aplicacion
	private Fase faseActual;
	
	// Result File
	// Archivos resultados
	private String resultFile;
	// Propiedad con la que se espera encontrar la hoja de reglas
	private String reglasSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de primera
	// fase
	private String primeraFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de Octavos de
	// final
	private String octavosFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de Cuartos de
	// final
	private String cuartosFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de Semifinal
	private String semifinalFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de tercer puesto
	private String tercerPuestoFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de la final
	private String finalFaseSheet;
	
	
	// Fase Grupo
	// Directorio en el que se espera encontrar archivos de apuestas
	private String faseGrupoReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participante
	private String faseGrupoPrefix;
	// Arreglo con propiedades que representan las hojas de la fase de grupo
	private String[] faseGrupoSheets;
	
	// Octavos
	// Directorio en el que se espera encontrar archivos de apuestas
	private String octavosReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participante
	private String octavosPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	private String octavosSheet;

	// Cuartos
	// Directorio en el que se espera encontrar archivos de apuestas
	private String cuartosReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participantes
	private String cuartosPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	private String cuartosSheet;
	
	// Semifinal
	// Directorio en el que se espera encontrar archivos de apuestas
	private String semifinalReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participantes
	private String semifinalPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	private String semifinalSheet;
	
	//Tercer Puesto
	// Directorio en el que se espera encontrar archivos de apuestas
	private String tercerPuestoReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participantes
	private String tercerPuestoPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	private String tercerPuestoSheet;

	//Tercer Puesto
	// Directorio en el que se espera encontrar archivos de apuestas
	private String finalReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participantes
	private String finalPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	private String finalSheet;
	
	/**
	 * @return the faseActual
	 */
	public Fase getFaseActual() {
		return faseActual;
	}

	/**
	 * @param faseActual
	 *            the faseActual to set
	 */
	public void setFaseActual(Fase faseActual) {
		this.faseActual = faseActual;
	}

	/**
	 * @return the resultFile
	 */
	public String getResultFile() {
		return resultFile;
	}

	/**
	 * @param resultFile
	 *            the resultFile to set
	 */
	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}

	/**
	 * @return the reglasSheet
	 */
	public String getReglasSheet() {
		return reglasSheet;
	}

	/**
	 * @param reglasSheet
	 *            the reglasSheet to set
	 */
	public void setReglasSheet(String reglasSheet) {
		this.reglasSheet = reglasSheet;
	}

	/**
	 * @return the primeraFaseSheet
	 */
	public String getPrimeraFaseSheet() {
		return primeraFaseSheet;
	}

	/**
	 * @param primeraFaseSheet
	 *            the primeraFaseSheet to set
	 */
	public void setPrimeraFaseSheet(String primeraFaseSheet) {
		this.primeraFaseSheet = primeraFaseSheet;
	}

	/**
	 * @return the octavosFaseSheet
	 */
	public String getOctavosFaseSheet() {
		return octavosFaseSheet;
	}

	/**
	 * @param octavosFaseSheet
	 *            the octavosFaseSheet to set
	 */
	public void setOctavosFaseSheet(String octavosFaseSheet) {
		this.octavosFaseSheet = octavosFaseSheet;
	}

	/**
	 * @return the cuartosFaseSheet
	 */
	public String getCuartosFaseSheet() {
		return cuartosFaseSheet;
	}

	/**
	 * @param cuartosFaseSheet
	 *            the cuartosFaseSheet to set
	 */
	public void setCuartosFaseSheet(String cuartosFaseSheet) {
		this.cuartosFaseSheet = cuartosFaseSheet;
	}

	/**
	 * @return the faseGrupoReadPath
	 */
	public String getFaseGrupoReadPath() {
		return faseGrupoReadPath;
	}

	/**
	 * @param faseGrupoReadPath
	 *            the faseGrupoReadPath to set
	 */
	public void setFaseGrupoReadPath(String faseGrupoReadPath) {
		this.faseGrupoReadPath = faseGrupoReadPath;
	}

	/**
	 * @return the faseGrupoPrefix
	 */
	public String getFaseGrupoPrefix() {
		return faseGrupoPrefix;
	}

	/**
	 * @param faseGrupoPrefix
	 *            the faseGrupoPrefix to set
	 */
	public void setFaseGrupoPrefix(String faseGrupoPrefix) {
		this.faseGrupoPrefix = faseGrupoPrefix;
	}

	/**
	 * @return the faseGrupoSheets
	 */
	public String[] getFaseGrupoSheets() {
		return faseGrupoSheets;
	}

	/**
	 * @param faseGrupoSheets
	 *            the faseGrupoSheets to set
	 */
	public void setFaseGrupoSheets(String[] faseGrupoSheets) {
		this.faseGrupoSheets = faseGrupoSheets;
	}

	/**
	 * @param faseGrupoSheets
	 *            the faseGrupoSheets to set
	 */
	public void setFaseGrupoSheets(String faseGrupoSheetsString) {
		this.faseGrupoSheets = faseGrupoSheetsString == null ? null : faseGrupoSheetsString.split("-");
	}

	/**
	 * @return the octavosReadPath
	 */
	public String getOctavosReadPath() {
		return octavosReadPath;
	}

	/**
	 * @param octavosReadPath
	 *            the octavosReadPath to set
	 */
	public void setOctavosReadPath(String octavosReadPath) {
		this.octavosReadPath = octavosReadPath;
	}

	/**
	 * @return the octavosPrefix
	 */
	public String getOctavosPrefix() {
		return octavosPrefix;
	}

	/**
	 * @param octavosPrefix
	 *            the octavosPrefix to set
	 */
	public void setOctavosPrefix(String octavosPrefix) {
		this.octavosPrefix = octavosPrefix;
	}

	/**
	 * @return the octavosSheet
	 */
	public String getOctavosSheet() {
		return octavosSheet;
	}

	/**
	 * @param octavosSheet
	 *            the octavosSheet to set
	 */
	public void setOctavosSheet(String octavosSheet) {
		this.octavosSheet = octavosSheet;
	}

	/**
	 * @return the cuartosReadPath
	 */
	public String getCuartosReadPath() {
		return cuartosReadPath;
	}

	/**
	 * @param cuartosReadPath
	 *            the cuartosReadPath to set
	 */
	public void setCuartosReadPath(String cuartosReadPath) {
		this.cuartosReadPath = cuartosReadPath;
	}

	/**
	 * @return the cuartosPrefix
	 */
	public String getCuartosPrefix() {
		return cuartosPrefix;
	}

	/**
	 * @param cuartosPrefix
	 *            the cuartosPrefix to set
	 */
	public void setCuartosPrefix(String cuartosPrefix) {
		this.cuartosPrefix = cuartosPrefix;
	}

	/**
	 * @return the cuartosSheet
	 */
	public String getCuartosSheet() {
		return cuartosSheet;
	}

	/**
	 * @param cuartosSheet
	 *            the cuartosSheet to set
	 */
	public void setCuartosSheet(String cuartosSheet) {
		this.cuartosSheet = cuartosSheet;
	}

	/**
	 * M&eacute;todo que sete la fase actual a partir de un numero: 1: Fase de grupos, 2: Octavos, 3: cuartos, 4: semifinal, 5: tercer lugar, 6: final
	 * @param faseActual
	 */
	public void setFaseActual(Integer faseActual) {
		switch (faseActual) {
		case 6:
			setFaseActual(Fase.FINAL);
			break;
		case 5:
			setFaseActual(Fase.TERCERPUESTO);
			break;
		case 4:
			setFaseActual(Fase.SEMIFINAL);
			break;
		case 3:
			setFaseActual(Fase.CUARTOS);
			break;
		case 2:
			setFaseActual(Fase.OCTAVOS);
			break;
		case 1:
			setFaseActual(Fase.PRIMERA);
			break;
		}
	}

	/**
	 * @return the campeonato
	 */
	public String getCampeonato() {
		return campeonato;
	}

	/**
	 * @param campeonato
	 *            the campeonato to set
	 */
	public void setCampeonato(String campeonato) {
		this.campeonato = campeonato;
	}

	/**
	 * @return the xlsxExtension
	 */
	public String[] getXlsxExtension() {
		return xlsxExtension;
	}

	/**
	 * @param xlsxExtension
	 *            the xlsxExtension to set
	 */
	public void setXlsxExtension(String[] xlsxExtension) {
		this.xlsxExtension = xlsxExtension;
	}

	/**
	 * @return the semifinalReadPath
	 */
	public String getSemifinalReadPath() {
		return semifinalReadPath;
	}

	/**
	 * @param semifinalReadPath the semifinalReadPath to set
	 */
	public void setSemifinalReadPath(String semifinalReadPath) {
		this.semifinalReadPath = semifinalReadPath;
	}

	/**
	 * @return the semifinalPrefix
	 */
	public String getSemifinalPrefix() {
		return semifinalPrefix;
	}

	/**
	 * @param semifinalPrefix the semifinalPrefix to set
	 */
	public void setSemifinalPrefix(String semifinalPrefix) {
		this.semifinalPrefix = semifinalPrefix;
	}

	/**
	 * @return the semifinalSheet
	 */
	public String getSemifinalSheet() {
		return semifinalSheet;
	}

	/**
	 * @param semifinalSheet the semifinalSheet to set
	 */
	public void setSemifinalSheet(String semifinalSheet) {
		this.semifinalSheet = semifinalSheet;
	}

	/**
	 * @return the semifinalFaseSheet
	 */
	public String getSemifinalFaseSheet() {
		return semifinalFaseSheet;
	}

	/**
	 * @param semifinalFaseSheet the semifinalFaseSheet to set
	 */
	public void setSemifinalFaseSheet(String semifinalFaseSheet) {
		this.semifinalFaseSheet = semifinalFaseSheet;
	}

	/**
	 * @return the tercerPuestoFaseSheet
	 */
	public String getTercerPuestoFaseSheet() {
		return tercerPuestoFaseSheet;
	}

	/**
	 * @param tercerPuestoFaseSheet the tercerPuestoFaseSheet to set
	 */
	public void setTercerPuestoFaseSheet(String tercerPuestoFaseSheet) {
		this.tercerPuestoFaseSheet = tercerPuestoFaseSheet;
	}

	/**
	 * @return the finalFaseSheet
	 */
	public String getFinalFaseSheet() {
		return finalFaseSheet;
	}

	/**
	 * @param finalFaseSheet the finalFaseSheet to set
	 */
	public void setFinalFaseSheet(String finalFaseSheet) {
		this.finalFaseSheet = finalFaseSheet;
	}

	/**
	 * @return the tercerPuestoReadPath
	 */
	public String getTercerPuestoReadPath() {
		return tercerPuestoReadPath;
	}

	/**
	 * @param tercerPuestoReadPath the tercerPuestoReadPath to set
	 */
	public void setTercerPuestoReadPath(String tercerPuestoReadPath) {
		this.tercerPuestoReadPath = tercerPuestoReadPath;
	}

	/**
	 * @return the tercerPuestoPrefix
	 */
	public String getTercerPuestoPrefix() {
		return tercerPuestoPrefix;
	}

	/**
	 * @param tercerPuestoPrefix the tercerPuestoPrefix to set
	 */
	public void setTercerPuestoPrefix(String tercerPuestoPrefix) {
		this.tercerPuestoPrefix = tercerPuestoPrefix;
	}

	/**
	 * @return the tercerPuestoSheet
	 */
	public String getTercerPuestoSheet() {
		return tercerPuestoSheet;
	}

	/**
	 * @param tercerPuestoSheet the tercerPuestoSheet to set
	 */
	public void setTercerPuestoSheet(String tercerPuestoSheet) {
		this.tercerPuestoSheet = tercerPuestoSheet;
	}

	/**
	 * @return the finalReadPath
	 */
	public String getFinalReadPath() {
		return finalReadPath;
	}

	/**
	 * @param finalReadPath the finalReadPath to set
	 */
	public void setFinalReadPath(String finalReadPath) {
		this.finalReadPath = finalReadPath;
	}

	/**
	 * @return the finalPrefix
	 */
	public String getFinalPrefix() {
		return finalPrefix;
	}

	/**
	 * @param finalPrefix the finalPrefix to set
	 */
	public void setFinalPrefix(String finalPrefix) {
		this.finalPrefix = finalPrefix;
	}

	/**
	 * @return the finalSheet
	 */
	public String getFinalSheet() {
		return finalSheet;
	}

	/**
	 * @param finalSheet the finalSheet to set
	 */
	public void setFinalSheet(String finalSheet) {
		this.finalSheet = finalSheet;
	}
}
