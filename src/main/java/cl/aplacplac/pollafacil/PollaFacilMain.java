package cl.aplacplac.pollafacil;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cl.aplacplac.pollafacil.loader.PollaFacilLoader;
import cl.aplacplac.pollafacil.util.Util;
import cl.aplacplac.pollafacil.vo.Fase;
import cl.aplacplac.pollafacil.vo.Participante;
import cl.aplacplac.pollafacil.vo.PollaFacilConfiguracion;

/**
 * Clase principal de la aplicacion
 * 
 * @author fgonzalez
 *
 */
@Component
@RestController
@RequestMapping("/pollafacil")
@SpringBootApplication
public class PollaFacilMain {

	// Instancia de pollafacil cargada
	private PollaFacil pollaFacilLoaded;

	// Logger de la clase
	private final static Logger logger = Logger.getLogger(PollaFacilMain.class);
	
	// Extenciones de archivos permitidas
	private final String[] xlsxExtension = new String[] { "xlsx", "xlsm" };

	// Dominio a mostrar dentro de la aplicacion
	@Value("${pollaFacil.domain}")
	private String domain;
	
	// Dominio a mostrar dentro de la aplicacion
	@Value("${pollaFacil.campeonato}")
	private String campeonato;

	// Fase actual del campeonato, filtra lo calculado y mostrado dentro de la
	// aplicacion
	@Value("${pollaFacil.faseActual}")
	private Integer faseActual;

	// Archivos resultados
	@Value("${pollaFacil.resultados.resultFile}")
	private String resultFile;
	// Propiedad con la que se espera encontrar la hoja de reglas
	@Value("${pollaFacil.resultados.reglasSheet}")
	private String reglasSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de primera
	// fase
	@Value("${pollaFacil.resultados.primeraFaseSheet}")
	private String primeraFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de Octavos de
	// final
	@Value("${pollaFacil.resultados.octavosFaseSheet}")
	private String octavosFaseSheet;
	// Propiedad con la que se espera encontrar la hoja con resultados de Cuartos de
	// final
	@Value("${pollaFacil.resultados.cuartosFaseSheet}")
	private String cuartosFaseSheet;

	// Propiedades de la Fase Grupo
	// Directorio en el que se espera encontrar archivos de apuestas
	@Value("${pollaFacil.faseGrupo.readPath}")
	private String faseGrupoReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participante
	@Value("${pollaFacil.faseGrupo.prefix}")
	private String faseGrupoPrefix;
	// Arreglo con propiedades que representan las hojas de la fase de grupo
	private String[] faseGrupoSheets;

	// Octavos
	// Directorio en el que se espera encontrar archivos de apuestas
	@Value("${pollaFacil.octavos.readPath}")
	private String octavosReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participante
	@Value("${pollaFacil.octavos.prefix}")
	private String octavosPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	@Value("${pollaFacil.octavos.sheet}")
	private String octavosSheet;

	// Cuartos
	// Directorio en el que se espera encontrar archivos de apuestas
	@Value("${pollaFacil.cuartos.readPath}")
	private String cuartosReadPath;
	// Prefijo de los archivos de apuesta, se utiliza para extraer nombre de
	// participantes
	@Value("${pollaFacil.cuartos.prefix}")
	private String cuartosPrefix;
	// Propiedad en la que se espera encontrar la hoja de apuestas
	@Value("${pollaFacil.cuartos.sheet}")
	private String cuartosSheet;
	
	// Clase utilitaria para filtrar partidos
	private static Util util = new Util();

	/**
	 * M&eacute;todo principal
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(PollaFacilMain.class, args);
		logger.info("\n*******************************************" 
				+ "\nPolla Facil Lista"
				+ "\n*******************************************\n");
	}

	/**
	 * Acciones a realizar despues de carga correcta de aplicacion
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void construct() throws Exception {
		refresh();
	}

	/**
	 * M&eacute;todo que recalcula los archivos y resultados
	 * 
	 * @return Pagina de portada
	 * @throws Exception
	 */
	@RequestMapping(path = "/refresh", method = { RequestMethod.GET })
	public ModelAndView refresh() throws Exception {
		logger.info("\n*******************************************" 
				+ "\nLlamado a Refresh!"
				+ "\n*******************************************\n");
		pollaFacilLoaded = new PollaFacilLoader().load(getConfiguration()).getPollaFacil();
		return index();
	}

	private PollaFacilConfiguracion getConfiguration() {
		PollaFacilConfiguracion conf = new PollaFacilConfiguracion();
		// Fase Grupo
		conf.setFaseGrupoPrefix(faseGrupoPrefix);
		conf.setFaseGrupoReadPath(faseGrupoReadPath);
		conf.setFaseGrupoSheets(faseGrupoSheets);
		
		// Octavos de final
		conf.setOctavosFaseSheet(octavosFaseSheet);
		conf.setOctavosPrefix(octavosPrefix);
		conf.setOctavosReadPath(octavosReadPath);
		conf.setOctavosSheet(octavosSheet);
		
		// Cuartos de final
		conf.setCuartosFaseSheet(cuartosFaseSheet);
		conf.setCuartosPrefix(cuartosPrefix);
		conf.setCuartosReadPath(cuartosReadPath);
		conf.setCuartosSheet(cuartosSheet);
		
		// Fase actual
		conf.setFaseActual(faseActual);
		
		// Reglas
		conf.setReglasSheet(reglasSheet);
		
		// Primera fase
		conf.setPrimeraFaseSheet(primeraFaseSheet);
		
		// FAse grupo
		conf.setFaseGrupoSheets(faseGrupoSheets);
		
		// Archivo resultados
		conf.setResultFile(resultFile);
		
		// Extensiones permitidas
		conf.setXlsxExtension(xlsxExtension);
		
		// Nombre campeontato
		conf.setCampeonato(campeonato);
		return conf;
	}

	/**
	 * M&eacute;todo que carga y muestra el participante con el id enviado por
	 * parametro
	 * 
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
		mav.getModel().put("domain", domain);
		mav.getModel().put("today", new Date());
		mav.getModel().put("faseActual", faseActual);
		mav.getModel().put("equipos", this.pollaFacilLoaded.getEquipos());
		mav.getModel().put("Util", util);
		mav.getModel().putAll(this.pollaFacilLoaded.getReglasMaps());
		
		Participante p = this.pollaFacilLoaded.getParticipante(id);
		if (p != null) {
			mav.getModel().put("participante", p);
			mav.getModel().put("partidos", p.getPartidos());
			mav.getModel().put("puntajes", p.getPuntajes());
		}
		return mav;
	}

	/**
	 * M&eacute;todo que retorna la pantalla de portada de la aplicacion
	 * 
	 * @return Pagina de portada
	 */
	@RequestMapping(path = "/index")
	public ModelAndView index() {
		logger.info("\n*******************************************" 
				+ "\nLlamado a index!"
				+ "\n*******************************************\n");
		ModelAndView mav = new ModelAndView("portada");
		mav.getModel().put("domain", domain);
		mav.getModel().put("today", new Date());
		mav.getModel().put("faseActual", faseActual);
		mav.getModel().put("participantes", this.pollaFacilLoaded.getParticipantes());
		mav.getModel().put("equipos", this.pollaFacilLoaded.getEquipos());
		mav.getModel().put("resultados", this.pollaFacilLoaded.getResultados());
		mav.getModel().put("fases", Fase.values());
		mav.getModel().put("Util", util);
		mav.getModel().putAll(this.pollaFacilLoaded.getReglasMaps());
		return mav;
	}
}