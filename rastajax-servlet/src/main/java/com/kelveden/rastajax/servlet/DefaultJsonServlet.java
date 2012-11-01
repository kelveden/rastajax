/**
 * Copyright 2012 Alistair Dutton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kelveden.rastajax.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelveden.rastajax.core.RestDescriber;
import com.kelveden.rastajax.representation.flat.*;
import com.kelveden.rastajax.core.ClassLoaderRootResourceScanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class DefaultJsonServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String apiPackages;

    @Override
    public void init() throws ServletException {
        super.init();

        final ServletContext context = getServletContext();
        apiPackages = context.getInitParameter("rastajax.apipackages");
    }

    @Override
    protected final void doGet(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws ServletException, IOException {

        final ClassLoaderRootResourceScanner scanner = new ClassLoaderRootResourceScanner(this.getClass().getClassLoader(), apiPackages.split(",")).allowInterfaceInheritance();

        final Set<FlatResource> representation = RestDescriber.describeApplication(
                scanner.scan(),
                new FlatRepresentationBuilder());

        final JsonNode jsonRepresentation = MAPPER.valueToTree(representation);
        writeRepresentationToResponse(jsonRepresentation, httpResponse);
    }

    private void writeRepresentationToResponse(JsonNode representation, HttpServletResponse httpResponse) throws IOException {

        httpResponse.setContentType("application/json; charset=utf8");

        final OutputStream outputStream = httpResponse.getOutputStream();

        outputStream.write(representation.toString().getBytes("UTF-8"));
        outputStream.flush();
    }
}