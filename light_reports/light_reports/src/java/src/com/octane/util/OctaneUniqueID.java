/**
 * -------------------------- COPYRIGHT_AND_LICENSE --
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * <p>All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>* Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. * Neither the name of the Botnode.com
 * (Berlin Brown) nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * -------------------------- END_COPYRIGHT_AND_LICENSE --
 */
package com.octane.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Usage: OctaneUniqueID.getUniqueId())
 *
 * @author Berlin Brown
 * @version $Revision: 1.0 $
 */
public class OctaneUniqueID {

  /**
   * Implementation Routine toHexString.
   *
   * @param bytes byte[]
   * @return String
   */
  private static final String toHexString(byte[] bytes) {

    char[] ret = new char[bytes.length * 2];
    for (int i = 0, j = 0; i < bytes.length; i++) {
      int c = (int) bytes[i];
      if (c < 0) {
        c += 0x100;
      }
      ret[j++] = Character.forDigit(c / 0x10, 0x10);
      ret[j++] = Character.forDigit(c % 0x10, 0x10);
    }

    return new String(ret);
  }

  /**
   * Implementation Routine getUniqueId.
   *
   * @return String
   */
  public static final String getUniqueId() {

    String digest = "";

    try {
      MessageDigest md = MessageDigest.getInstance("MD5");

      String timeVal = "" + (System.currentTimeMillis() + 1);
      String localHost = "";
      try {
        localHost = InetAddress.getLocalHost().toString();
      } catch (UnknownHostException e) {
        // If an error, we can use other values.
        throw new RuntimeException("Error trying to get localhost" + e.getMessage());
      }

      String randVal = "" + new Random().nextInt();
      String val = timeVal + localHost + randVal;
      md.reset();
      md.update(val.getBytes());

      // Generate the digest.
      digest = toHexString(md.digest());

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("NoSuchAlgorithmException : " + e.getMessage());
    } // End of the Try - Catch

    return digest;
  }
} // End of the Class
