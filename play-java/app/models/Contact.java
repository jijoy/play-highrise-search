package models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
@Entity
@NamedQueries({
@NamedQuery(name="Contact.getAllContacts",
    query="SELECT e FROM Contact e")          
})
public class Contact {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId;
	
	public Contact(){
		
	}
	
	public String firstName;
	public String familyName;
	public String companyName;
	
}
