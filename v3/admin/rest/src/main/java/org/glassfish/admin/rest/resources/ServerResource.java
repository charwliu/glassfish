/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009-2010 Sun Microsystems, Inc. All rights reserved.
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
package org.glassfish.admin.rest.resources;
import javax.ws.rs.Path;
import org.glassfish.admin.rest.TemplateResource;
public class ServerResource extends TemplateResource {

@Path("delete-instance/")
public ServerDeleteInstanceResource getServerDeleteInstanceResource() {
ServerDeleteInstanceResource resource = resourceContext.getResource(ServerDeleteInstanceResource.class);
return resource;
}

@Path("start-instance/")
public ServerStartInstanceResource getServerStartInstanceResource() {
ServerStartInstanceResource resource = resourceContext.getResource(ServerStartInstanceResource.class);
return resource;
}

@Path("stop-instance/")
public ServerStopInstanceResource getServerStopInstanceResource() {
ServerStopInstanceResource resource = resourceContext.getResource(ServerStopInstanceResource.class);
return resource;
}

@Path("recover-transactions/")
public ServerRecoverTransactionsResource getServerRecoverTransactionsResource() {
ServerRecoverTransactionsResource resource = resourceContext.getResource(ServerRecoverTransactionsResource.class);
return resource;
}

@Override
public String[][] getCommandResourcesPaths() {
return new String[][]{{"delete-instance", "DELETE"}, {"start-instance", "POST"}, {"stop-instance", "POST"}, {"recover-transactions", "POST"}};
}

	@Path("application-ref/")
	public ListApplicationRefResource getApplicationRefResource() {
		ListApplicationRefResource resource = resourceContext.getResource(ListApplicationRefResource.class);
		resource.setParentAndTagName(getEntity() , "application-ref");
		return resource;
	}
	@Path("resource-ref/")
	public ListResourceRefResource getResourceRefResource() {
		ListResourceRefResource resource = resourceContext.getResource(ListResourceRefResource.class);
		resource.setParentAndTagName(getEntity() , "resource-ref");
		return resource;
	}
	@Path("property/")
	public ListPropertyResource getPropertyResource() {
		ListPropertyResource resource = resourceContext.getResource(ListPropertyResource.class);
		resource.setParentAndTagName(getEntity() , "property");
		return resource;
	}
	@Path("system-property/")
	public ListSystemPropertyResource getSystemPropertyResource() {
		ListSystemPropertyResource resource = resourceContext.getResource(ListSystemPropertyResource.class);
		resource.setParentAndTagName(getEntity() , "system-property");
		return resource;
	}
}
