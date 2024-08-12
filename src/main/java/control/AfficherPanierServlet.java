package control;

import datastore.DB_Connector;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PanierItem;
import model.TypeAnimal;
import persistence.TypeAnimalDAO_JDBC;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/afficherPanier")
public class AfficherPanierServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Connection connection = null;
        try {
            DB_Connector dbConnector = new DB_Connector();
            connection = dbConnector.getConnection();

            List<PanierItem> panier = (List<PanierItem>) session.getAttribute("panier");
            if (panier == null) {
                panier = new ArrayList<>();
                session.setAttribute("panier", panier);
            }

            // Récupérer les détails des animaux du panier
            TypeAnimalDAO_JDBC typeAnimalDAO = new TypeAnimalDAO_JDBC(connection);
            List<TypeAnimal> animaux = new ArrayList<>();
            for (PanierItem item : panier) {
                TypeAnimal animal = typeAnimalDAO.getTypeAnimalById(item.getId());
                if (animal != null) {
                    animal.setQuantite(item.getQuantite());
                    animaux.add(animal);
                }
            }

            request.setAttribute("animaux", animaux);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/afficherPanier.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'affichage du panier", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}