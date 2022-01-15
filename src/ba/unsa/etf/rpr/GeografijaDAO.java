package ba.unsa.etf.rpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;

    private Connection connection;
    private PreparedStatement dajGradUpit, dajGradoveUpit, dajDrzavuUpit, dajDrzavuPoNazivuUpit, dajGlavniGradUpit, dodajGradUpit, dodajDrzavuUpit, obrisiGradUpit,
            dajIdNovogGradaUpit, dajIdNoveDrzaveUpit, izmijeniGradUpit, obrisiDrzavuUpit, obrisiGradoveDrzaveUpit, dajDrzaveUpit, dajGradPoNazivuUpit;

    public Connection getConnection() {
        return connection;
    }

    private GeografijaDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dajGradoveUpit = connection.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
        } catch (SQLException e) {
            regenerisiBazu();
            try {
                dajGradoveUpit = connection.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            dajDrzaveUpit = connection.prepareStatement("SELECT * FROM drzava");
            dajGradUpit = connection.prepareStatement("SELECT * FROM grad WHERE id=?");
            dajDrzavuUpit = connection.prepareStatement("SELECT * FROM drzava WHERE id=?");
            dajGradPoNazivuUpit = connection.prepareStatement("SELECT * FROM grad WHERE naziv=?");
            dajDrzavuPoNazivuUpit = connection.prepareStatement("SELECT * FROM drzava WHERE naziv=?");
            dajGlavniGradUpit = connection.prepareStatement("SELECT g.* FROM grad g, drzava d WHERE d.naziv=? AND g.drzava=d.id");
            dodajGradUpit = connection.prepareStatement("INSERT INTO grad VALUES(?,?,?,?)");
            dodajDrzavuUpit = connection.prepareStatement("INSERT INTO drzava VALUES(?,?,?)");
            dajIdNovogGradaUpit = connection.prepareStatement("SELECT MAX(id)+1 FROM grad");
            dajIdNoveDrzaveUpit = connection.prepareStatement("SELECT MAX(id)+1 FROM drzava");
            izmijeniGradUpit = connection.prepareStatement("UPDATE grad SET naziv=?, broj_stanovnika=?, drzava=? WHERE id=?");
            obrisiGradUpit = connection.prepareStatement("DELETE FROM grad WHERE id=?");
            obrisiDrzavuUpit = connection.prepareStatement("DELETE FROM drzava WHERE naziv=?");
            obrisiGradoveDrzaveUpit = connection.prepareStatement("DELETE FROM grad WHERE drzava=?");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void regenerisiBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("baza.sql"));
            String sqlUpit = "";
            while(ulaz.hasNext()) {
                sqlUpit += ulaz.nextLine();
                if(sqlUpit.length() > 1 && sqlUpit.charAt(sqlUpit.length()-1) == ';') {
                    try {
                        Statement stmt = connection.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            ulaz.close();
        } catch(FileNotFoundException e) {
            System.out.println("Ne postoji SQL datoteka... nastavljam sa praznom bazom");
        }
    }

    public void vratiBazuNaDefault() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM grad");
            stmt.executeUpdate("DELETE FROM drzava");
            regenerisiBazu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static GeografijaDAO getInstance() {
        if(instance == null) instance = new GeografijaDAO();
        return instance;
    }

    public static void removeInstance() {
        if(instance != null) {
            try {
                instance.connection.close();
                instance = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        try {
            ResultSet resultSet = dajGradoveUpit.executeQuery();
            while(resultSet.next()) {
                Grad grad = new Grad(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), null);
                dajDrzavuUpit.setInt(1, resultSet.getInt(4));
                ResultSet rs1 = dajDrzavuUpit.executeQuery();
                if(rs1.next())
                    grad.setDrzava(new Drzava(rs1.getInt(1), rs1.getString(2), grad));
                gradovi.add(grad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gradovi;
    }

    public ArrayList<Drzava> drzave() {
        ArrayList<Drzava> drzave = new ArrayList<>();
        try {
            ResultSet resultSet = dajDrzaveUpit.executeQuery();
            while(resultSet.next()) {
                Drzava drzava = new Drzava(resultSet.getInt(1), resultSet.getString(2), null);
                dajGradUpit.setInt(1, resultSet.getInt(3));
                ResultSet rs1 = dajGradUpit.executeQuery();
                if(rs1.next())
                    drzava.setGlavniGrad(new Grad(rs1.getInt(1), rs1.getString(2), rs1.getInt(3), drzava));
                drzave.add(drzava);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drzave;
    }

    public Grad glavniGrad(String drzava) {
        try {
            dajGlavniGradUpit.setString(1, drzava);
            ResultSet resultSet = dajGlavniGradUpit.executeQuery();
            if(!resultSet.next()) return null;
            Grad grad = new Grad(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), null);
            dajDrzavuUpit.setInt(1, resultSet.getInt(4));
            ResultSet rs = dajDrzavuUpit.executeQuery();
            if(rs.next())
                grad.setDrzava(new Drzava(rs.getInt(1), rs.getString(2), grad));
            return grad;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void obrisiDrzavu(String drzava) {
        Drzava d = nadjiDrzavu(drzava);
        try {
            if(d != null) {
                obrisiDrzavuUpit.setString(1, drzava);
                obrisiDrzavuUpit.executeUpdate();
                obrisiGradoveDrzaveUpit.setInt(1, d.getId());
                obrisiGradoveDrzaveUpit.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Grad nadjiGrad(String grad) {
        try {
            dajGradPoNazivuUpit.setString(1, grad);
            ResultSet resultSet = dajGradPoNazivuUpit.executeQuery();
            if(resultSet.next()) {
                Grad g = new Grad(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), null);
                dajDrzavuUpit.setInt(1, resultSet.getInt(4));
                ResultSet rs = dajDrzavuUpit.executeQuery();
                if(rs.next()) {
                    Drzava d = new Drzava(rs.getInt(1), rs.getString(2), g);
                }
                return g;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            dajDrzavuPoNazivuUpit.setString(1, drzava);
            ResultSet resultSet = dajDrzavuPoNazivuUpit.executeQuery();
            if(resultSet.next()) {
                Drzava d = new Drzava(resultSet.getInt(1), resultSet.getString(2), null);
                dajGradUpit.setInt(1, resultSet.getInt(3));
                ResultSet rs = dajGradUpit.executeQuery();
                if(rs.next()) {
                    Grad g = new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3), d);
                    d.setGlavniGrad(g);
                }
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dodajGrad(Grad grad) {
        try {
            ResultSet resultSet = dajIdNovogGradaUpit.executeQuery();
            int id = 1;
            if(resultSet.next())
                id = resultSet.getInt(1);
            dodajGradUpit.setInt(1, id);
            dodajGradUpit.setString(2, grad.getNaziv());
            dodajGradUpit.setInt(3, grad.getBrojStanovnika());
            dodajGradUpit.setInt(4, grad.getDrzava().getId());
            dodajGradUpit.executeUpdate();
            grad.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            ResultSet resultSet = dajIdNoveDrzaveUpit.executeQuery();
            int id = 1;
            if(resultSet.next())
                id = resultSet.getInt(1);
            dodajDrzavuUpit.setInt(1, id);
            dodajDrzavuUpit.setString(2, drzava.getNaziv());
            dodajDrzavuUpit.setInt(3, drzava.getGlavniGrad().getId());
            dodajDrzavuUpit.executeUpdate();
            drzava.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            izmijeniGradUpit.setInt(4, grad.getId());
            izmijeniGradUpit.setString(1, grad.getNaziv());
            izmijeniGradUpit.setInt(2, grad.getBrojStanovnika());
            izmijeniGradUpit.setInt(3, grad.getDrzava().getId());
            izmijeniGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void obrisiGrad(Grad grad) {
        try {
            obrisiGradUpit.setInt(1, grad.getId());
            obrisiGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
