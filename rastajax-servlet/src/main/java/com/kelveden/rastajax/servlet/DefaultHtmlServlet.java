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

public class DefaultHtmlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String apiPackages;

    @Override
    public void init() throws ServletException {
        super.init();

        final ServletContext context = getServletContext();
        apiPackages = context.getInitParameter("apiPackages");
    }

    @Override
    protected final void doGet(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws ServletException, IOException {

        final ClassLoaderRootResourceScanner scanner = new ClassLoaderRootResourceScanner(this.getClass().getClassLoader(), apiPackages.split(",")).allowInterfaceInheritance();

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