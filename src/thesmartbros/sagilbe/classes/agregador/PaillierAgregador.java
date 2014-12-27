package thesmartbros.sagilbe.classes.agregador;

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

import java.math.BigInteger;

class PaillierAgregador {

	public BigInteger n = null;
	/**
	 * nsquare = n*n
	 */
	public BigInteger nsquare = null;
	/**
	 * a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1.
	 */
	public BigInteger g = null;

	public BigInteger AgreggatorFunction(BigInteger nsquare, BigInteger... em) {
		if (em == null)
			return BigInteger.ZERO;
		else if (n == null)
			return BigInteger.ZERO;
		else if (n != null)
			nsquare = n.multiply(n);
		if (em.length == 0)
			return BigInteger.ZERO;
		else if (em.length == 1)
			return em[0].mod(nsquare);
		else if (em.length == 2)
			return em[0].multiply(em[1]).mod(nsquare);
		else {
			BigInteger result = em[0].multiply(em[1]).mod(nsquare);
			for (int i = 2; i < em.length; i++)
				result = result.multiply(em[i]).mod(nsquare);
			return result;
		}
	}
}