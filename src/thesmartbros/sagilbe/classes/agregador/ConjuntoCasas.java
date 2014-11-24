package thesmartbros.sagilbe.classes.agregador;

import java.math.BigInteger;

public class ConjuntoCasas {
	
	int idcasa;
	BigInteger consuma_enc;
	int zonaid;
	boolean nuevo;
	
	
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

}
