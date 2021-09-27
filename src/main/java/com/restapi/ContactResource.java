package com.restapi;

import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/contacts")
public class ContactResource {

	ContactService cs=new ContactService();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Contact> getContacts() {
		return cs.getAllContacts();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> addContact(Contact a) {
		Map<String, String> msg=cs.addoneContact(a);
		return msg;
	}

	@Path("/{number}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> deleteContact(@PathParam("number")String number) {
		Map<String, String> a=cs.deleteoneContact(number);
		return a;
	}
	
	@Path("/{number}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> updateContact(@PathParam("number")String newnum,Contact a) {
		Map<String, String> msg=cs.updateoneContact(a,newnum);
		return msg;
	}
	
}

