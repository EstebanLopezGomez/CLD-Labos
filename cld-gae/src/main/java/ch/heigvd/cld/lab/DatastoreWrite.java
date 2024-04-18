package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.UUID;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWrite extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter pw = resp.getWriter();
        pw.println("Writing entity to datastore.");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Enumeration<String> parameterNames = req.getParameterNames();
        String kind = getParameter(req, "_kind");
        String keyName = getParameter(req, "_key");

        Key entityKey;
        if (keyName != null) entityKey = KeyFactory.createKey(kind, keyName);
        else  entityKey = KeyFactory.createKey(kind, UUID.randomUUID().toString());


        Entity entity = new Entity(entityKey);

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = req.getParameter(paramName);

            if (!paramName.equals("_kind") && !paramName.equals("_key")) entity.setProperty(paramName, paramValue);

        }

        // Écrire l'entité dans le Datastore
        datastore.put(entity);
    }

    // Méthode utilitaire pour récupérer un paramètre de requête
    private String getParameter(HttpServletRequest req, String paramName) {
        return req.getParameter(paramName);
    }
}
