package com.google.gwt.sample.itProjekt.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.google.gwt.sample.itProjekt.shared.bo.Contact;
import com.google.gwt.sample.itProjekt.shared.bo.ContactList;
import com.google.gwt.sample.itProjekt.shared.bo.User;

/**
 * The Class ContactListMapper.
 */
public class ContactListMapper {
	
	/** Konstruktor f�r den ContactListMapper (Singleton) */
	//static weil Singleton. Einzige Instanz dieser Klasse
	private static ContactListMapper  contactlistmapper = null;
	
	/**
	 * ContactListMapper.
	 *
	 * Falls noch kein ContactListMapper existiert erstellt er ein neuen ContactListMapper und gibt ihn zur�ck
	 * 
	 */
	public static ContactListMapper contactListMapper() {
		if (contactlistmapper == null){
			contactlistmapper = new ContactListMapper();
		}
		return contactlistmapper;
		}
	
	/**
	 * FindByID.
	 *
	 * Findet ContactList durch eine CL_ID und speichert die dazugeh�rigen Werte (CL_ID, listname und U_ID) in einem ContactList Objekt ab und gibt dieses wieder
	 */
	public ContactList findByID(ContactList cl){
		Connection con = DBConnection.connection();
		
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CL_ID, listname, U_ID FROM T_ContactList WHERE CL_ID =" + cl.getId() + " ORDER BY CL_ID");
			if (rs.next()){
				ContactList c = new ContactList();
				c.setId(rs.getInt("CL_ID"));
				c.setName((rs.getString("listname")));
				c.setOwner(rs.getInt("U_ID"));
				
				
				return c;	
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	
	/**
	 * FindAll.
	 *
	 *Gibt alle ContactList Objekte zur�ck welche mit CL_ID, listname und U_ID bef�llt sind
	 *Hierf�r holen wir CL_ID, listname und U_ID aus der T_ContactList Tabelle und speichern diese in einem ContactList Objekt ab und f�gen diese dem Vector hinzu
	 *Am Ende geben wir diesen Vector zur�ck
	 *
	 */
	public Vector<ContactList> findAll(){
		Connection con = DBConnection.connection();
		Vector<ContactList> result = new Vector<ContactList>();
				
				try{
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT CL_ID, listname, U_ID FROM T_ContactList ORDER BY CL_ID");
					
					while (rs.next()){
						ContactList c = new ContactList();
						c.setId(rs.getInt("CL_ID"));
						c.setName(rs.getString("listname"));
						c.setOwner(rs.getInt("U_ID"));
						result.addElement(c);
					}		
				}catch(SQLException e2){
					e2.printStackTrace();
				}
				return result;
			}
	
	/**
	 * FindByName.
	 *
	 * Findet ContactLists durch einen Namen und speichert die dazugeh�rigen Werte (CL_ID, listname und U_ID) in einem ContactList Objekt ab
	 * und Speichert dieses Objekt im Vector ab und gibt diesen wieder
	 * 
	 */
	public Vector <ContactList> findByName(String name){
		Connection con = DBConnection.connection();
		Vector<ContactList> result = new Vector<ContactList>();
		
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CL_ID, listname, U_ID FROM T_ContactList WHERE listname ='"+ name + "' ORDER BY CL_ID");
			while (rs.next()){
				ContactList c = new ContactList();
				c.setId(rs.getInt("CL_ID"));
				c.setName(rs.getString("listname"));
				c.setOwner(rs.getInt("U_ID"));
				result.addElement(c);	
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
			return result;
		}
		return result;
	}
	
	/**
	 * FindAllByUID.
	 *
	 * Findet alle ContactLists die ein User erstellt hat durch seine U_ID
	 * Alle Values werden aus T_ContactList ausgelesen und in einem ContactList Objekt gespeichert und einem Vector hinzugef�gt und zur�ckgegeben
	 * Gibt ein Vector voller ContactList Objekte zur�ck welche ein User erstellt hat
	 * 
	 */
	public Vector <ContactList> findAllByUID(User u){
		Connection con = DBConnection.connection();
		Vector<ContactList> result = new Vector<ContactList>();
		
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CL_ID, listname, U_ID FROM T_ContactList WHERE U_ID=" + u.getId() + " ORDER BY CL_ID");
			while (rs.next()){
				ContactList c = new ContactList();
				c.setId(rs.getInt("CL_ID"));
				c.setName(rs.getString("listname"));
				c.setOwner(rs.getInt("U_ID"));
				result.addElement(c);	
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
			return result;
		}
		return result;
	}
	
	/**
	 * Insert.
	 *
	 *Sucht nach der h�chsten CL_ID um diese um eins zu erh�hen und als neue CL_ID zu nutzen
	 *Bef�llt T_ContactList mit CL_ID, listname und U_ID
	 *Ein ContactList wird zur�ckgegeben
	 *
	 */
	public ContactList insert(ContactList c, User u){
		Connection con = DBConnection.connection();
		
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(CL_ID) AS maxclid FROM T_ContactList");
			if (rs.next()){
				
				c.setId(rs.getInt("maxclid")+1);
				Statement stmt2 = con.createStatement();
				stmt2.executeUpdate("INSERT INTO T_ContactList (CL_ID, listname, U_ID)"
				+ " VALUES ("
				+ c.getId() 
				+ ", '" 
				+ c.getName() 
				+ "', "
				+ u.getId() 
				+ ")") ;
						
				return c;	
				
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
			return c;
		}
		return c;}
	
		/**
		 * Update.
		 *
		 * Update von Ver�nderungen falls sich der listname �ndert
		 * Gibt ein ContactList zur�ck
		 */
		public ContactList update(ContactList c){
			Connection con = DBConnection.connection();
			
			try{
				Statement stmt = con.createStatement();
				stmt.executeUpdate("UPDATE T_ContactList SET listname ='" + c.getName() + "' " + "WHERE CL_ID =" + c.getId());
			}
		
		catch (SQLException e2){
			e2.printStackTrace();
			return c;
		}
		return c;}
		
		/**
		 * Delete.
		 *
		 * Entfernt alles aus T_Permission_Contactlist wo die CL_ID der ID des �bergebenen Objekts entspricht
		 * Damit l�sen wir die Teilhaberschaft an einem ContactList auf
		 * der n�chste Schritt entfernt alles aus T_ContactList wo die CL_ID der ID des �bergebenen Objekts entspricht
		 * 
		 */
		public void delete (ContactList c){
Connection con = DBConnection.connection();

			
		try{
	
				Statement stmt2 = con.createStatement();
					stmt2.executeUpdate("DELETE FROM T_Permission_Contactslist WHERE CL_ID =" + c.getId());
			}

			catch (SQLException e2){
				e2.printStackTrace();

				}	
		try{
				
				Statement stmt = con.createStatement();
				stmt.executeUpdate("DELETE FROM T_ContactsList WHERE CL_ID =" + c.getId());
			}
		
		catch (SQLException e2){
			e2.printStackTrace();
			
		}}
		
		/**
		 * GetAllContacts.
		 *
		 * Bef�llt den Vector mit Contacts die in einer ContactList enthalten sind
		 * Hierf�r durchsuchen wir die T_Contact_ContactList Tabelle nach C_ID wo die CL_ID der ID des �bergebenen Objektes entspricht
		 * Diese C_ID nutzen wir um die C_ID, firstName, lastName, gender und U_ID aus der T_Contact zu holen wo die C_ID 
		 *
		 */
		public Vector <Contact> getAllContacts(ContactList cl){
			Connection con = DBConnection.connection();
			
			Vector<Contact> result = new Vector<Contact>();
			
			try{
				Statement stmt = con.createStatement();
				Statement stmt2 = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT DISTINCT C_ID FROM T_Contact_Contactlist WHERE CL_ID =" + cl.getId() + " ORDER BY C_ID");

				
				
				while (rs.next()){
										
					ResultSet rs2 = stmt2.executeQuery("SELECT C_ID, firstName, lastName, gender, U_ID FROM T_Contact WHERE C_ID =" + rs.getInt("C_ID") + " ORDER BY C_ID");
					Contact c = new Contact();
					c.setId(rs2.getInt("C_ID"));
					c.setFirstname(rs2.getString("firstName"));
					c.setLastname(rs2.getString("lastName"));
					c.setSex(rs2.getString("gender"));
					c.setOwner(rs.getInt("U_ID"));
					result.addElement(c);
				}
				
				
			}
			catch (SQLException e2){
				e2.printStackTrace();
				return result;
			}
			return result;
		}	
		
		/**
		 * Adds the contact.
		 *
		 * @param cl the cl
		 * @param c the c
		 * @return the contact list
		 */
		public ContactList addContact(ContactList cl, Contact c){
			Connection con = DBConnection.connection();
			
			try{
				Statement stmt = con.createStatement();
				stmt.executeUpdate("INSERT INTO T_Contact_ContactList (CL_ID, C_ID)"
				+ " VALUES ('"
				+ cl.getId() 
				+ "', '" 
				+ c.getId() 
				+ "')") ;
						
				return cl;	
				
			}
			catch (SQLException e2){
				e2.printStackTrace();
				return cl;
			}
			}
		
		/**
		 * Removes the contact.
		 *
		 * @param cl the cl
		 * @param c the c
		 * @return the contact list
		 */
		public ContactList removeContact(ContactList cl, Contact c){
			Connection con = DBConnection.connection();
			
			try{
				Statement stmt = con.createStatement();
				stmt.executeUpdate("DELETE FROM T_Contact_ContactList WHERE CL_ID="
				+ cl.getId() 
				+ " AND C_ID=" 
				+ c.getId());
						
				return cl;	
				
			}
			catch (SQLException e2){
				e2.printStackTrace();
				return cl;
			}
			}
		
}
