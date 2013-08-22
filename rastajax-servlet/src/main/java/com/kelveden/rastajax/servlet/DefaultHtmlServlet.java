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

import com.kelveden.rastajax.core.RestDescriber;
import com.kelveden.rastajax.representation.flat.*;
import com.kelveden.rastajax.core.ClassLoaderRootResourceScanner;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Example servlet showcasing a simple integration with Rastajax that will use the {@link FlatRepresentationBuilder} that ships with Rastajax to build a flat
 * representation of a REST application that is then rendered as HTML. See the <a href="https://github.com/kelveden/rastajax">Rastajax Homepage</a> for more information.
 */
public class DefaultHtmlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String apiPackages;

    @Override
    public void init() throws ServletException {
        super.init();

        final ServletContext context = getServletContext();
        apiPackages = context.getInitParameter("rastajax.apipackages");
    }

    @Override
    protected final void doGet(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws ServletException, IOException {

        final ClassLoaderRootResourceScanner scanner = new ClassLoaderRootResourceScanner(this.getClass().getClassLoader(), apiPackages.split(","))
                .allowInterfaceInheritance();

        final Set<FlatResource> representation = RestDescriber.describeApplication(
                scanner.scan(),
                new FlatRepresentationBuilder());

        writeRepresentationToResponse(representation, httpResponse);
    }

    private void writeRepresentationToResponse(Set<FlatResource> representation, HttpServletResponse httpResponse) throws IOException {

        httpResponse.setContentType("text/html; charset=utf8");

        final OutputStream outputStream = httpResponse.getOutputStream();
        final PrintStream printStream = new PrintStream(outputStream);

        printStream.println("<html>");
        printStream.println("<body>");
        printStream.println("<head>");
        printStream.println("<style type=\"text/css\">");
        printStream.println("* { font-size:10px; }");
        printStream.println("table { border-width: 1px; border-collapse: collapse; margin-left: 10px; }");
        printStream.println("table th { border: 1px solid; padding: 4px; background-color: #dedede; }");
        printStream.println("table td { border: 1px solid; padding: 4px; }");
        printStream.println("</style>");
        printStream.println("</head>");

        for (FlatResource resource : representation) {
            writeResource(resource, printStream);
        }

        printStream.println("</body>");
        printStream.println("</html>");

        printStream.flush();
    }

    private void writeResource(FlatResource resource, PrintStream printStream) {

        printStream.println("<p style=\"font-weight: bold\">" + resource.getUriTemplate() + "</p>");

        printStream.println("<table>");
        printStream.println("<thead>");
        printStream.println("<tr>");
        printStream.println("<th>Request Method Designator</th>");
        printStream.println("<th>Parameters</th>");
        printStream.println("<th>Consumes</th>");
        printStream.println("<th>Produces</th>");
        printStream.println("</tr>");
        printStream.println("</thead>");

        printStream.println("<tbody>");

        for (FlatResourceMethod resourceMethod : resource.getResourceMethods()) {
            writeResourceMethod(resourceMethod, printStream);
        }

        printStream.println("</tbody>");
        printStream.println("</table>");
    }

    private void writeResourceMethod(FlatResourceMethod resourceMethod, PrintStream printStream) {

        printStream.println("<tr>");
        printStream.println("<td>" + resourceMethod.getRequestMethodDesignator() + "</td>");
        printStream.println("<td>" + parametersToString(resourceMethod) + "</td>");
        printStream.println("<td>" + mediaTypesToString(resourceMethod.getConsumes()) + "</td>");
        printStream.println("<td>" + mediaTypesToString(resourceMethod.getProduces()) + "</td>");
        printStream.println("</tr>");
    }

    private String parametersToString(final FlatResourceMethod method) {

        final List<String> parameterNames = new ArrayList<String>();

        for (Map.Entry<String, List<FlatResourceMethodParameter>> parameterEntry : method.getParameters().entrySet()) {
            for (FlatResourceMethodParameter parameter : parameterEntry.getValue()) {
                parameterNames.add(parameter.getName());
            }
        }

        return StringUtils.join(parameterNames.toArray(new String[parameterNames.size()]), ", ");
    }

    private String mediaTypesToString(final List<String> mediaTypes) {

        if (mediaTypes.size() == 0) {
            return "<i>Not Specified</i>";
        } else {
            return StringUtils.join(mediaTypes.toArray(new String[mediaTypes.size()]), ", ");
        }
    }
}