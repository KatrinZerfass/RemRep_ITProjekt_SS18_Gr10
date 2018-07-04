package com.google.gwt.sample.itProjekt.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.google.gwt.sample.itProjekt.shared.bo.Contact;
import com.google.gwt.sample.itProjekt.shared.bo.ContactList;
import com.google.gwt.sample.itProjekt.shared.bo.Permission;
import com.google.gwt.sample.itProjekt.shared.bo.User;

/**
 * The Class PermissionMapper.
 * 
 * @author Egor Krämer
 * @author Robert Mattheis
 */
public class PermissionMapper {

	/** Konstruktor für den PermissionMapper (Singleton)
	 * static weil Singleton. Einzige Instanz dieser Klasse
	 * 
	 * @author Egor Krämer
	 * @author Robert Mattheis
	 */
	private static PermissionMapper  permissionmapper = null;
	
	/**
	 * PermissionMapper.
	 *
	 * Falls noch kein PermissionMapper existiert erstellt er ein neuen PermissionMapper und gibt ihn zurück
	 * 
	 * @author Egor Krämer
	 * @author Robert Mattheis
	 */
	public static PermissionMapper permissionMapper() {
		if (permissionmapper == null){
			permissionmapper = new PermissionMapper();
		}
		return permissionmapper;
		}
	
	/**
	 * FindAll.
	 *
	 * Gibt alle Permission Objekte zurück welche mit U_ID und C_ID befüllt sind
	 * Hierfür holen wir U_ID und C_ID aus der T_Permission_Contact Tabelle und setzten als permissionID ein zusammengesetzten Key aus U_ID und C_ID
	 * Für das setzten des Participant holen wir, durch das aufrufen der findByID im UserMapper, die U_ID
	 * und speichern diese in einem Permission Objekt ab
	 * zudem setzen wir die C_ID in einem neuen Contact Objekt und ein Shareableobject durch das aufrufen der findByID Methode im ContactMapper 
	 * Das selbe führen wir für ContactList durch und fügen das Permission Objekt dem Vector hinzu
	 * 
	 * @author Egor Krämer
	 * @author Robert Mattheis
	 */
	public Vector<Permission> findAll(){
		Connection con = DBConnection.connection();
		Vector<Permission> result = new Vector<Permission>();
				
				try{
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT U_ID, C_ID, srcU_ID FROM T_Permission_Contact ORDER BY U_ID");
					
					while (rs.next()){
						Permission p = new Permission();
						p.setId(rs.getInt("U_ID") + rs.getInt("C_ID") +rs.getInt("srcU_ID"));
						p.setParticipantID(rs.getInt("U_ID"));
						p.setSourceUserID(rs.getInt("srcU_ID"));
						p.setShareableObjectID(rs.getInt("C_ID"));
												
						result.addElement(p);
					}		
				}catch(SQLException e2){
					e2.printStackTrace();
				}
				try{
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT U_ID, CL_ID, srcU_ID FROM T_Permission_Contactlist ORDER BY U_ID");
					
					while (rs.next()){
						Permission p = new Permission();
						p.setId(rs.getInt("U_ID") + rs.getInt("CL_ID")+rs.getInt("srcU_ID"));
						p.setParticipantID(rs.getInt("U_ID"));
						p.setSourceUserID(rs.getInt("srcU_ID"));
						p.setShareableObjectID(rs.getInt("CL_ID"));
						result.addElement(p);
					}		
				}catch(SQLException e2){
					e2.printStackTrace();
				
				}
				return result;
			}
	
/**
 * Update.
 *
 * Update von Veränderungen falls sich die Shareableobject ändert
 * Falls die ID unter 30000000 liegt wird ein Contact geupdated, falls die ID �ber 30000000 liegt werden ContactLists geupdated
 * Gibt ein permission zurück
 * 
 * @param permission übergebenes Permission Objekt mit Attributen C_ID oder CL_ID und U_ID
 * 
 * @author Egor Krämer
 * @author Robert Mattheis
 */
public Permission update(Permission permission){
		
		Connection con = DBConnection.connection();
		
		if(permission.getShareableObjectID() < 30000000) {
		
		try{
			Statement stmt1 = con.createStatement();
			stmt1.executeUpdate("UPDATE T_Permission_Contact SET C_ID=" + permission.getShareableObjectID() + " WHERE U_ID =" + permission.getParticipantID());
						}
		
		catch (SQLException e2){
			e2.printStackTrace();
			return permission;
				}
		
		return permission;
			}
		
		if(permission.getShareableObjectID() >= 30000000) {
			
			try{
			Statement stmt2 = con.createStatement();
			stmt2.executeUpdate("UPDATE T_Permission_Contactlist SET CL_ID=" + permission.getShareableObjectID()+ " WHERE U_ID =" + permission.getParticipantID());
					}
	
			catch (SQLException e2){
				e2.printStackTrace();
				return permission;
				}
			return permission;
			}
		return permission;
	}
	
	/**
	 * Delete.
	 *
	 * falls die ID des übergebenen Shareableobjects Objekts unter 30000000 liegt wird aus der T_Permission_Contact alles entfernt
	 * wo die U_ID und C_ID der ID des Participants und des Shareableobjects entspricht
	 * 
	 * falls die ID des übergebenen Shareableobjects Objekts über 30000000 liegt wird aus der T_Permission_Contactlist alles entfernt
	 * wo die U_ID und C_ID der ID des Participants und des Shareableobjects entspricht
	 * 
	 * @param permission übergebenes Permission Objekt mit Attributen C_ID und U_ID
	 * 
	 * @author Egor Krämer
	 * @author Robert Mattheis
	 */
	public void delete (Permission permission){
		
		Connection con = DBConnection.connection();
					
		if(permission.getShareableObjectID() < 30000000)
			
					try{
						Statement stmt1 = con.createStatement();
						stmt1.executeUpdate("DELETE FROM T_Permission_Contact WHERE C_ID =" + permission.getShareableObjectID()+ " AND U_ID=" + permission.getParticipantID());
					}
				
				catch (SQLException e2){
					e2.printStackTrace();
				}
		
		if(permission.getShareableObjectID()>= 30000000) {
			
					try{
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate("DELETE FROM T_Permission_Contactlist WHERE CL_ID =" + permission.getShareableObjectID()+ " AND U_ID ="+ permission.getParticipantID());
				
			
					ContactList cl = new ContactList();
					cl.setId(permission.getShareableObjectID());
					
					Vector <Contact> c1 = ContactListMapper.contactListMapper().getAllContacts(cl);
					
					if(c1.size()>0){
						for(Contact c2: c1){
							
							Permission p = new Permission();
							p.setShareableObjectID(c2.getId());
							p.setParticipantID(permission.getParticipantID());
							
						PermissionMapper.permissionMapper().delete(p);
					}}}
				
				catch (SQLException e2){
					e2.printStackTrace();
				}
		}
	}
	
	/**
	 * DeleteAllByCLID.
	 *
	 * Entfernt alle Einträge aus T_Permission_Contactlist falls eine ContactList gelöscht wird
	 * Hierfür wird die T_Permission_Contactlist nach der CL_ID durchsucht wo sie der ID der ContactList entspricht welches wir übergeben bekommen haben
	 * 
	 * @param contactlist übergebenes ContactList Objekt mit Attribut CL_ID
	 * 
	 * @author Egor Krämer
	 * @author Robert Mattheis
	 */
	public void deleteAllByCLID (ContactList contactlist){
		
		Connection con = DBConnection.connection();
		
		try{
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE FROM T_Permission_Contactlist WHERE CL_ID =" + contactlist.getId());
		}
	
	catch (SQLException e2){
		e2.printStackTrace();
	}
}
	
	
	/**
	 * ShareContact.
	 *
	 * Befüllt die T_Permission_Contact mit einer zusammengesetzten ID aus C_ID und U_ID 
	 * und befüllt die Tabelle mit der C_ID aus dem Shareableobject und der U_ID aus Participant
	 * und gibt die permission zurück
	 * 
	 * @param permission übergebenes Permission Objekt mit Attributen C_ID, U_ID und srcU_ID
	 * 
	 * @author Egor Krämer
	 * @author Robert Mattheis
	 */
	public Permission shareContact(Permission permission){
		
		
		Connection con = DBConnection.connection();
		
		try{
			Statement stmt = con.createStatement();
				
				
				stmt.executeUpdate("INSERT INTO T_Permission_Contact (C_ID, U_ID, srcU_ID)"
						+ " VALUES ("
						+ permission.getShareableObjectID()
						+ ", " 
						+ permission.getParticipantID()
						+", "
						+ permission.getSourceUserID()
						
						+ ")") ;
						
				return permission;	
				
				}
		catch (SQLException e2){
			e2.printStackTrace();
			return permission;
			}
		}
	
	
	/**
	 * ShareContactList.
	 *
	 * Befüllt die T_Permission_Contactlist mit einer zusammengesetzten ID aus CL_ID und U_ID 
	 * und befüllt die Tabelle mit der CL_ID aus dem Shareableobject und der U_ID aus Participant
	 * und gibt die permission zurück
	 * 
	 * @param permission übergebenes Permission Objekt mit Attributen CL_ID, U_ID und srcU_ID
	 * 
	 * @author Egor Krämer
	 * @author Robert Mattheis
	 */
	public Permission shareContactList(Permission permission){
		
		
		Connection con = DBConnection.connection();
		
		try{
			
			Statement stmt = con.createStatement();
			
				
			stmt.executeUpdate("INSERT INTO T_Permission_Contactlist (CL_ID, U_ID, srcU_ID)"
						+ " VALUES ("
						+ permission.getShareableObjectID()
						+ ", " 
						+ permission.getParticipantID()
						+", "
						+ permission.getSourceUserID()
						+ ")") ;
			
			ContactList cl= new ContactList();
			cl.setId(permission.getShareableObjectID());
						
				Vector <Contact> c = ContactListMapper.contactListMapper().getAllContacts(cl);
				
				Permission p = new Permission();
				if(c.size()>0){
				for(Contact c1: c){
					
					p.setParticipantID(permission.getParticipantID());
					p.setSourceUserID(permission.getSourceUserID());
					p.setShareableObjectID(c1.getId());
					if(c1.getOwner()!= permission.getParticipantID()){
					shareContact(p);
					}
				}}
			
			return permission;	
				
				}
		catch (SQLException e2){
			e2.printStackTrace();
			return permission;
			}
		}
	

/**
 * GetAllContactsByUID.
 * 
 * Sucht nach allen Contacts die einem User zur Verfügung stehen
 * Hierfür suchen wir nach allen U_ID die der ID des User Objektes entsprechen in der T_Permission_Contact
 * Die gefundenen C_ID werden in einem Contact Objekt abgespeichert 
 * und durch den Aufruf der findByID im ContactMapper vollständig befüllt und dem Vector hinzugefügt
 * Zum Schluss geben wir den Vector zurück
 * 
 * @param user übergebenes User Objekt mit Attribut U_ID
 * 
 * @author Egor Krämer
 * @author Robert Mattheis
 */
public Vector<Contact> getAllContactsByUID(User user){
	
	Connection con = DBConnection.connection();
	Vector<Contact> result = new Vector<Contact>();
			
			try{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT DISTINCT C_ID FROM T_Permission_Contact WHERE U_ID=" + user.getId()+ " ORDER BY C_ID");
				
				while (rs.next()){
					Contact c = new Contact();
					c.setId(rs.getInt("C_ID"));
									
					result.addElement(ContactMapper.contactMapper().findByID(c));
				}		
			}catch(SQLException e2){
				e2.printStackTrace();
			return result;
			}
			return result;
		}

/**
 * GetAllContactsBySrcUID.
 * 
 * Sucht nach allen Contacts die einem User zur Verfügung stehen
 * Hierfür suchen wir nach allen srcU_ID die der ID des User Objektes entsprechen in der T_Permission_Contact
 * Die gefundenen C_ID werden in einem Contact Objekt abgespeichert 
 * und durch den Aufruf der findByID im ContactMapper vollständig befüllt und dem Vector hinzugefügt
 * Zum Schluss geben wir den Vector zurück
 * 
 * @param user übergebenes User Objekt mit Attribut srcU_ID
 * 
 * @author Egor Krämer
 * @author Robert Mattheis
 */
public Vector<Contact> getAllContactsBySrcUID(User user){
	
	Connection con = DBConnection.connection();
	Vector<Contact> result = new Vector<Contact>();
			
			try{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT DISTINCT C_ID FROM T_Permission_Contact WHERE srcU_ID=" + user.getId()+ " ORDER BY C_ID");
				
				while (rs.next()){
					Contact c = new Contact();
					c.setId(rs.getInt("C_ID"));
									
					result.addElement(ContactMapper.contactMapper().findByID(c));
				}		
			}catch(SQLException e2){
				e2.printStackTrace();
//				return result;
			}
			return result;
		}



/**
 * GetAllContactListsByUID.
 *
 * Sucht nach allen ContactLists die einem User zur Verfügung stehen
 * Hierfür suchen wir nach allen U_ID die der ID des User Objektes entsprechen in der T_Permission_Contactlist
 * Die gefundenen CL_ID werden in einem ContactList Objekt abgespeichert 
 * und durch den Aufruf der findByID im ContactListMapper vollständig befüllt und dem Vector hinzugefügt
 * Zum Schluss geben wir den Vector zurück
 * 
 * @param user übergebenes User Objekt mit Attribut U_ID
 * 
 * @author Egor Krämer
 * @author Robert Mattheis
 */
public Vector<ContactList> getAllContactListsByUID(User user){
	
	Connection con = DBConnection.connection();
	Vector<ContactList> result = new Vector<ContactList>();
			
			try{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT CL_ID FROM T_Permission_Contactlist WHERE U_ID=" + user.getId()+ " ORDER BY CL_ID");
				
				while (rs.next()){
					ContactList cl = new ContactList();
					cl.setId(rs.getInt("CL_ID"));
									
					result.addElement(ContactListMapper.contactListMapper().findByID(cl));
				}		
			}catch(SQLException e2){
				e2.printStackTrace();
			}
			return result;
		}

/**
 * getAllContactListsBySrcUID.
 *
 * Sucht nach allen ContactLists die einem User zur Verfügung stehen
 * Hierfür suchen wir nach allen srcU_ID die der ID des User Objektes entsprechen in der T_Permission_Contactlist
 * Die gefundenen CL_ID werden in einem ContactList Objekt abgespeichert 
 * und durch den Aufruf der findByID im ContactListMapper vollständig befüllt und dem Vector hinzugefügt
 * Zum Schluss geben wir den Vector zurück
 * 
 * @param user übergebenes User Objekt mit Attribut srcU_ID
 * 
 * @author Egor Krämer
 * @author Robert Mattheis
 */
public Vector<ContactList> getAllContactListsBySrcUID(User user){
	
	Connection con = DBConnection.connection();
	Vector<ContactList> result = new Vector<ContactList>();
			
			try{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT CL_ID FROM T_Permission_Contactlist WHERE srcU_ID=" + user.getId()+ " ORDER BY CL_ID");
				
				while (rs.next()){
					ContactList cl = new ContactList();
					cl.setId(rs.getInt("CL_ID"));
									
					result.addElement(ContactListMapper.contactListMapper().findByID(cl));
				}		
			}catch(SQLException e2){
				e2.printStackTrace();
			}
			return result;
		}

/**
 * GetSourceUserByUIDAndCID.
 *
 * Sucht nach dem User welcher einen Contact von einem bestimmten User geteilt bekommen hat
 * Hierfür suchen wir nach allen U_ID die der ID des User Objektes entsprechen und der C_ID der ID des contact Objekts in der T_Permission_Contact
 * und speichern die srcU_ID die gefunden wird in ein User Objekt
 * Durch den Aufruf der findByID im UserMapper wird das User Objekt vollständig befüllt
 * Zum Schluss geben wir den user zurück
 * 
 * @param user übergebenes User Objekt mit Attribut U_ID
 * @param contact übergebenes Contact Objekt mit Attribut C_ID
 * 
 * @author Egor Krämer
 * @author Robert Mattheis
 */
public User getSourceUserByUIDAndCID(User user, Contact contact){
	
	Connection con = DBConnection.connection();
			
			try{
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT srcU_ID FROM T_Permission_Contact WHERE U_ID=" + user.getId()+ " AND C_ID=" + contact.getId());
				
				if (rs.next()){
					User u = new User();
					u.setId(rs.getInt("srcU_ID"));
					
					return UserMapper.userMapper().findByID(u.getId());
					
				}		
			}catch(SQLException e2){
				e2.printStackTrace();
			}
			return user;
		}

}