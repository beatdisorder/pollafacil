package cl.aplacplac.pollafacil.util;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Clase con m&eacute;todos utiles para trabajar valores
 * 
 * @author fgonzalez
 *
 */
public class ValueUtil {
	/**
	 * M&eacute;todo que retorna el valor de la celda en {@link Short}
	 * 
	 * @param cell
	 *            La celda a extraer valor
	 * @return El valor encontrado
	 */
	@SuppressWarnings("deprecation")
	public static Short floatToShort(Cell cell) {
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
