package thesmartbros.sagilbe.classes.proveedor;


//clase de la zona que se analiza
public class Zona {

	//id de la zona
	private int id;
	
	//viviendas que tiene, su gasto...
	private int numero_viviendas;
	private int gasto_energetico;
	private float porcentaje_gasto;
	
	//tecnicos, ocupados y disponibles
	
	private int tecnicos_disp;
	private int tecnicos_ocup;
	
	//averias solucionadas
	private int averias_sol;
	
	
	//historial de consumo, vector de 24 posiciones (horas) para hacer estadistica
	private int[] historial_consumo =new int[24];
	
	
	//por ejemplo maximo num usuarios
	private int max_users=10;
	//lista de users
	private int[] lista_usuarios =new int[max_users];
	
	//describe el estado de la zona
	private String estado;
	
	
	
	//Getters y Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumero_viviendas() {
		return numero_viviendas;
	}
	public void setNumero_viviendas(int numero_viviendas) {
		this.numero_viviendas = numero_viviendas;
	}
	public int getGasto_energetico() {
		return gasto_energetico;
	}
	public void setGasto_energetico(int gasto_energetico) {
		this.gasto_energetico = gasto_energetico;
	}
	public float getPorcentaje_gasto() {
		return porcentaje_gasto;
	}
	public void setPorcentaje_gasto(float porcentaje_gasto) {
		this.porcentaje_gasto = porcentaje_gasto;
	}
	public int getTecnicos_disp() {
		return tecnicos_disp;
	}
	public void setTecnicos_disp(int tecnicos_disp) {
		this.tecnicos_disp = tecnicos_disp;
	}
	public int getTecnicos_ocup() {
		return tecnicos_ocup;
	}
	public void setTecnicos_ocup(int tecnicos_ocup) {
		this.tecnicos_ocup = tecnicos_ocup;
	}
	public int getAverias_sol() {
		return averias_sol;
	}
	public void setAverias_sol(int averias_sol) {
		this.averias_sol = averias_sol;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
}
