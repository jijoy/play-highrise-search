package controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import models.Contact;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.db.Database;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.XML;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import utils.Constants;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

	
	 private JPAApi  db;
	 @Inject
	 public HomeController(JPAApi db) {
	        this.db = db;
	 }
	
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }
    @Transactional(readOnly=true)
    public Result contacts() {
    	List<Contact> contactList = new ArrayList<Contact>();
    	for(Contact ct : db.em().createNamedQuery("Contact.getAllContacts", Contact.class).getResultList()){
    		contactList.add(ct);
    	}

        return ok(contacts.render(contactList));
    }
    
    @Transactional
    public Result save()throws Exception{
    	RequestBody body = request().body();
    	String tag = body.asFormUrlEncoded().get("tags")[0];
    	
    	String url = Constants.SEARCH_URL + tag;
    	
    	URL urlU = new URL(url);
    	URLConnection urlConnection = urlU.openConnection();
    	urlConnection.setDoOutput(true);
    	String authStr = Constants.API_KEY + ":" + "X";
        byte[] authEncoded = Base64.getUrlEncoder().encode(authStr.getBytes());

    	urlConnection.setRequestProperty  ("Authorization", "Basic " + new String(authEncoded));
    	
    	InputStream is = urlConnection.getInputStream();
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	StringBuilder response = new StringBuilder();
    	String line = "";
    	while((line = br.readLine()) != null){
    		response.append(line);
    		System.out.println(line);
    	}
    	
    	Document doc = XML.fromString(response.toString());
    	XPath xpath = XPathFactory.newInstance().newXPath();
    	String expression = "/people/person";
    	NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
    	for(int i = 0 ; i < nodes.getLength();i++){
    		Node node = nodes.item(i);
    		Contact c = new Contact();
    		Node company = getNode("company-name", node.getChildNodes());
    		if(company != null){
    			c.companyName = company.getFirstChild().getTextContent();
    		}
    		Node fname = getNode("first-name", node.getChildNodes());
    		if(fname != null){
    			c.firstName = fname.getFirstChild().getTextContent();
    		}
    		
    		Node lname = getNode("last-name", node.getChildNodes());
    		if(lname != null){
    			c.familyName = lname.getFirstChild().getTextContent();
    		}
    		
    		db.em().persist(c);
    	}
    	return ok(index.render("Your new application is ready."));
    }
    
    
    private Node getNode(String nodeName,NodeList list){
    	for(int i = 0 ; i < list.getLength() ; i++){
    		Node n = list.item(i);
    		if(n.getNodeName().equals(nodeName)){
    			return n;
    		}
    	}
    	return null;
    }
}
