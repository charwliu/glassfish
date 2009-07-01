/*
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package test.admin.admincli;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Configuration;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.Assert;

import java.io.*;
import java.net.*;
import java.util.*;

public class RestartDomainTests {

    private BufferedReader in = null;
    private String outPutFile=null, expectedOutPut=null, testErr=null;
    String host=System.getProperty("http.host");
    String port=System.getProperty("http.port");
    private boolean result=false;
    private Runtime runtime = Runtime.getRuntime();

    @BeforeTest
    @Test(groups = { "init" })
    public void stopDomainTest() throws Exception {
	String ASADMIN = System.getProperty("ASADMIN");
        Process proc = runtime.exec(ASADMIN+" stop-domain");
	try {
            if (proc.waitFor() != 0) {
               System.err.println("asadmin exit value = " + proc.exitValue());
            }
	    if (proc.exitValue() == 0) 
		result = true;
	    Assert.assertEquals(result, true, "Stop-domain test failed ...");
        }
        catch (Exception e) {
                System.err.println(e);
        }
    }

    @Test(dependsOnMethods = { "stopDomainTest" })
    public void startDomainTest() throws Exception {
	String ASADMIN = System.getProperty("ASADMIN");
	Process proc = runtime.exec(ASADMIN+" start-domain");
        try {
            if (proc.waitFor() != 0) {
               System.err.println("asadmin exit value = " + proc.exitValue());
            }
            if (proc.exitValue() == 0)
                result = true;
            Assert.assertEquals(result, true, "Start-domain test failed ...");
        }
        catch (InterruptedException e) {
                System.err.println(e);
        }
    }

/*
    @Test(groups = { "init" })
    public void createJdbcPoolTest() throws Exception {
	outPutFile = "admincli-restart.output";
	expectedOutPut = "create-jdbc-connection-pool executed successfully";
	testErr = "Create-jdbc failed.";
	parseTestResults(outPutFile, expectedOutPut, testErr);    
    }

    @Test(dependsOnGroups = { "init.*" })
    public void pingJdbcPoolBefore() throws Exception {
	outPutFile = "admincli-restart.output";
        expectedOutPut = "ping-connection-pool executed successfully";
        testErr = "Ping-jdbc failed.";
        parseTestResults(outPutFile, expectedOutPut, testErr);
    }

    @Test(dependsOnMethods = { "pingJdbcPoolBefore" })
    public void pingJdbcPoolAfter() throws Exception {
	outPutFile = "admincli-ping-jdbc.output";
	expectedOutPut = "ping-connection-pool executed successfully";
	testErr = "Ping-jdbc failed.";
	parseTestResults(outPutFile, expectedOutPut, testErr);    
    }
*/

    public void parseTestResults(String outPutFile, String expectedOutPut, String testErr) throws Exception {
       boolean result=false;
       File dir1 = new File (".");
       String fileName = dir1.getCanonicalPath()+"/"+outPutFile;
       this.expectedOutPut = expectedOutPut;
       this.testErr = testErr;
       //System.out.println("Parsing output file: "+fileName );
       try {
           in = new BufferedReader(new FileReader(fileName));
       } catch (FileNotFoundException e) {
           System.out.println("Could not open file " + fileName + " for reading ");
       }

       if(in != null) {
           String line;
           String testLine = null;
           try {
              while (( line = in.readLine() )  != null ) {
                //System.out.println("The line read is: " + line);
                if(line.indexOf(expectedOutPut)!=-1){
                  result=true;
                  testLine = line;
                  //System.out.println(testLine); 
		} 
	      }
              Assert.assertEquals(result, true, testErr);
           }catch(Exception e){
              e.printStackTrace();
              throw new Exception(e);
         }
       }
    }
}
