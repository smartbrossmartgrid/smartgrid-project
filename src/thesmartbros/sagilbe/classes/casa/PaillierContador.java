package thesmartbros.sagilbe.classes.casa;

/**
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 * more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.math.*;
import java.util.*;

final class PaillierContador {

	public BigInteger n = null;
	/**
	 * nsquare = n*n
	 */
	private BigInteger nsquare = null;
	/**
	 * a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1.
	 */
	public BigInteger g = null;
	/**
	 * number of bits of modulus
	 */
	private int bitLength = 512; //512

	private static PaillierContador INSTANCE = null;

	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PaillierContador();
		}
	}

	public static PaillierContador getInstance() {
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	public BigInteger Encryption(BigInteger m) {
		if (g == null || n == null)
			return BigInteger.ZERO;
		if (nsquare == null)
			nsquare = n.multiply(n);
		BigInteger r = new BigInteger(bitLength, new Random());
		return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);

	}

}