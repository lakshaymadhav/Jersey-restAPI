package com.restapi;




import java.io.StringReader;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;






@Path("/contacts")
public class ContactResource {

	ContactService cs=new ContactService();
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public List<Contact> getContacts(){
		return cs.getAllContact();
	}
	
	@Path("/login")
	@POST
	@Produces({MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Contact getContact(Contact a){
		
		return cs.getoneContact(a.getNumber(),a.getPassword());
	}
	
	@Path("/admin")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String AdminPage(@DefaultValue("user") @QueryParam("name") String username,@DefaultValue("user") @QueryParam("password") String password){
		String resultString = "";
		if(username.equals("admin")&& password.equals("adminzoho")) {
			resultString="Hello admin";
		}
		else if(username.equals("user") && password.equals("user")) {
			resultString="get lost user";
		}
		return resultString;
	}
	
	@Path("/xml")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response xmlentityerror(String a) {
		try {
				 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				 //to prevent xxe attack
//				 factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
//		        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
//		        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
//		        
//		        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//		       
//		        factory.setXIncludeAware(false);
//		        factory.setExpandEntityReferences(false);
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    InputSource is = new InputSource(new StringReader(a));
			    Document result=builder.parse(is);
			    org.w3c.dom.NodeList nodeList = result.getElementsByTagName("root");
			    for (int ind = 0; ind < nodeList.getLength(); ind++)   
			    {  
			      Node node = nodeList.item(ind);  
			      if(Node.ELEMENT_NODE == node.getNodeType() )
			      {
			        Element nodeElement = (Element) node;
			        System.out.println(nodeElement.getElementsByTagName("name").item(0).getTextContent());  
			      }
			    }   
			return Response.status(javax.ws.rs.core.Response.Status.OK).entity("good").type(MediaType.APPLICATION_XML).build();
		
		} catch (Exception e) {
			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity("bad").type(MediaType.APPLICATION_XML).build();
			
		}
		
		
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> addContact(Contact a) {
		Map<String, String> msg=cs.addoneContact(a);
		return msg;
	}
	
	@Path("/xss")
	@POST
	@Produces(MediaType.TEXT_HTML)
	public String xssattack(@FormParam("name") String name){
		//String result=name;
		String result=org.apache.commons.text.StringEscapeUtils.escapeHtml4(name);
		return result;
		
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

