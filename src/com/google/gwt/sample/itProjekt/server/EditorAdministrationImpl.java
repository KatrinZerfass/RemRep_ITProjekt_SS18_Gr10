package com.google.gwt.sample.itProjekt.server;

import java.util.Vector;

import com.google.gwt.sample.itProjekt.shared.*;
import com.google.gwt.sample.itProjekt.shared.bo.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.sample.itProjekt.server.db.*;

public class EditorAdministrationImpl extends RemoteServiceServlet implements EditorAdministration{
	
	/**
	 * 
	 */
	
	private ContactListMapper clMapper;
	private ContactMapper cMapper;
	private PropertyMapper pMapper;
	private PermissionMapper pmMapper;
	private UserMapper uMapper;
	private ValueMapper vMapper;

	@Override
	public void init() throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
		this.clMapper = ContactListMapper.contactListMapper();
		this.cMapper = ContactMapper.contactMapper();
		this.pMapper = PropertyMapper.propertyMapper();
		this.pmMapper = PermissionMapper.permissionMapper();
		this.uMapper = UserMapper.userMapper();
		this.vMapper = ValueMapper.valueMapper();
		
	}
	

	public User getUserInformation (String email) throws IllegalArgumentException{
	
		//Wenn der User noch nicht in der Datenbank existiert, wird ein neuer User angelegt. 
		if(uMapper.findByEMail(email) == null){
			return createUser(email);	
		}
		else{
			return uMapper.findByEMail(email);
		}
	}
	
	@Override
	public User createUser(String email)
			throws IllegalArgumentException {

		User newuser = new User();
		newuser.setEmail(email);
		
		return uMapper.insert(newuser);
	}
	
	public Vector<Contact> getAllContactsOfActiveUser(User user) throws IllegalArgumentException {
		
		Vector<Contact> result = cMapper.findAllByUID(user);
		result.addAll(pmMapper.getAllContactsByUID(user));
		
		return result;
	}
	
	public Vector<ContactList> getAllContactListsOfActiveUser(User user) throws IllegalArgumentException {
		
		return clMapper.findAllByUID(user);
	}

	@Override
	public Vector<ContactList> getAllContactListsOf(String email) throws IllegalArgumentException {

		return clMapper.findAllByUID(uMapper.findByEMail(email));
	}

	@Override
	public Vector<Contact> getAllOwnedContactsOf(String email) throws IllegalArgumentException {

		Vector<Contact> resultcontacts = new Vector<Contact>();
		resultcontacts = cMapper.findAllByUID(uMapper.findByEMail(email));
		return resultcontacts;
	}

	@Override
	public Vector<Contact> getAllSharedContactsWith(String email) throws IllegalArgumentException {

		return pmMapper.getAllContactsByUID(uMapper.findByEMail(email));
	}

	@Override
	public Vector<Contact> getAllContactsOf(ContactList contactlist, User user) throws IllegalArgumentException {

		return clMapper.getAllContacts(contactlist, user);
	}

	@Override
	public Vector<Contact> getAllContactsWith(Value value) throws IllegalArgumentException {
		
		return vMapper.findAllContactsByValue(value);
	}

	@Override
	public Contact getContact(int id) throws IllegalArgumentException {

		Contact contact = new Contact();
		contact.setId(id);
		return cMapper.findByID(contact);
	}


	@Override
	public Contact createContact(String firstname, String lastname, String sex, User user) throws IllegalArgumentException {

		Contact newcontact = new Contact();
		newcontact.setFirstname(firstname);
		newcontact.setLastname(lastname);
		newcontact.setSex(sex);
		
		return cMapper.insert(newcontact, user);
	}

	@Override
	public Contact editContact(int id, String firstname, String lastname, String sex) throws IllegalArgumentException {
		
		Contact changedcontact = new Contact();
		changedcontact.setFirstname(firstname);
		changedcontact.setLastname(lastname);
		changedcontact.setSex(sex);
		changedcontact.setId(id);

		return cMapper.update(changedcontact);
	}

	@Override
	public Permission shareContact(Contact contact, String email) throws IllegalArgumentException {
		
		Permission newpermission = new Permission();
		newpermission.setParticipant(uMapper.findByEMail(email));
		newpermission.setIsowner(false);
		newpermission.setShareableobject(contact);
		
		return pmMapper.shareContact(newpermission);
	}

	@Override
	public void deleteContact(int id) throws IllegalArgumentException {
		
		Contact deletedcontact = new Contact();
		deletedcontact.setId(id);
		
		cMapper.delete(deletedcontact);
	}

	@Override
	public ContactList createContactList(String name, User user) throws IllegalArgumentException {

		ContactList newcontactlist = new ContactList();
		newcontactlist.setName(name);
		
		return clMapper.insert(newcontactlist, user);
	}

	@Override
	public ContactList editContactList(int id, String name) throws IllegalArgumentException {

		ContactList changedcontactlist = new ContactList();
		changedcontactlist.setId(id);
		changedcontactlist.setName(name);
		
		return clMapper.update(changedcontactlist);
	}

	@Override
	public ContactList addContactToContactList(ContactList contactlist, Contact contact)
			throws IllegalArgumentException {
		
		return clMapper.addContact(contactlist, contact);
	}

	@Override
	public Permission shareContactList(ContactList contactlist, String email) throws IllegalArgumentException {
		
		Permission newpermission = new Permission();
		User user = new User();
		user = getUserInformation(email);
		newpermission.setParticipant(user);
		newpermission.setIsowner(false);
		newpermission.setShareableobject(contactlist);
		
		return pmMapper.shareContactList(newpermission);
	}

	@Override
	public ContactList removeContactFromContactList(ContactList contactlist, Contact contact)
			throws IllegalArgumentException {
		
		return clMapper.removeContact(contactlist, contact);
	}

	@Override
	public void deleteContactList(ContactList contactlist) throws IllegalArgumentException {
		
		clMapper.delete(contactlist);
	}

	@Override
	public Value createValue(Contact contact, int propertyid, String content) throws IllegalArgumentException {
		
		Value newvalue = new Value();
		newvalue.setContent(content);
		Property property = new Property();
		property.setId(propertyid);
		
		return vMapper.insert(newvalue, contact, property);
	}

	@Override
	public Value editValue(Contact contact, int propertyId, Value value, String content, boolean isshared)
			throws IllegalArgumentException {
		
		Value newvalue = new Value();
		newvalue = value;
		newvalue.setIsShared(isshared);
		newvalue.setContent(content);
		Property property = new Property();
		property.setId(propertyId);
		
		return vMapper.update(newvalue, contact, property);
	}

	@Override
	public void deleteValue(Value value) throws IllegalArgumentException {
		
		vMapper.delete(value);
	}

	@Override
	public Vector<Value> getAllValuesOf(Contact contact) throws IllegalArgumentException {
		
		return vMapper.getAllValueByCID(contact);
	}

	@Override
	public Vector<ContactList> getAllContactListsWith(Contact contact) throws IllegalArgumentException {
		return clMapper.findAllByCID(contact);
	}


	@Override
	public void deletePermission(User user, BusinessObject bo) throws IllegalArgumentException {
		Permission permission = new Permission();
		permission.setParticipant(user);
		
	}


	@Override
	public Vector<Contact> getAllContactsBy(String content) throws IllegalArgumentException {
		
		Value value = new Value();
		value.setContent(content);
		
		return vMapper.findAllContactsByValue(value);
	}


	@Override
	public Vector<Contact> getAllContactsWith(String name) throws IllegalArgumentException {
	
		return cMapper.findAllByName(name);
	}


	@Override
	public Property getPropertyOfValue(Value value) throws IllegalArgumentException {
		
		Property property = new Property();
		property.setId(value.getId());
		
		return pMapper.findByID(property);
	}


	@Override
	public Vector<Value> getAllSharedValuesOfContact(Contact contact) throws IllegalArgumentException {
		
		return vMapper.getAllSharedValueByCID(contact);
	}


	@Override
	public Vector<Property> getAllPredefindedPropertiesOf() throws IllegalArgumentException {
		
		return pMapper.findAllDefault();
	}


	@Override
	public Property createProperty(Contact contact, String type) throws IllegalArgumentException {
		
		Property newProperty = new Property();
		newProperty.setType(type);
		
		return pMapper.insert(newProperty, contact);
	}
}
