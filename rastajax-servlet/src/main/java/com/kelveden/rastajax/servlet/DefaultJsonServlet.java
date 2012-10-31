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
        apiPackages = context.getInitParameter("apiPackages");
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