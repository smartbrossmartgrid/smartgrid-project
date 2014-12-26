package thesmartbros.sagilbe.classes.agregador;

import java.math.BigInteger;

public class ConjuntoCasas {

	private int idcasa; // ID Casa
	private BigInteger consuma_enc; // Consumo encriptado con Paillier
	private int zonaid; // ID Zona
	private int not_found = 0; // Veces que no ha enviado el consumo
	private int time = 0; // Indica el tiempo en el que se recibio la medida
	private boolean nuevo = true; // Valor viejo (false) o valor nuevo (true)

	public void incrementarNotFound() {
		this.not_found++;
	}
	
	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public int getIdcasa() {
		return idcasa;
	}

	public void setIdcasa(int idcasa) {
		this.idcasa = idcasa;
	}

	public BigInteger getConsuma_enc() {
		return consuma_enc;
	}

	public void setConsuma_enc(BigInteger consuma_enc) {
		this.consuma_enc = consuma_enc;
	}

	public int getZonaid() {
		return zonaid;
	}

	public void setZonaid(int zonaid) {
		this.zonaid = zonaid;
	}

	public int getNot_found() {
		return not_found;
	}

	public void setNot_found(int not_found) {
		this.not_found = not_found;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
