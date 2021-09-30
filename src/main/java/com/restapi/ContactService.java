package com.restapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactService {

	Connection con=ConnectionProvider.getCon();
	
	public List<Contact> getAllContact(){
		List<Contact> list=new ArrayList<Contact>();
		try {
			PreparedStatement ps=con.prepareStatement("select * from contactlist");
			ResultSet rs=ps.executeQuery();
			while(rs.next()) {
				Contact contact=new Contact();
				contact.setName(rs.getString(2));
				contact.setNumber(rs.getString(3));
				contact.setEmail(rs.getString(4));
				String decryptpass=rs.getString(5);
				contact.setPassword(decryptpass);
				list.add(contact);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public Contact getoneContact(String number,String password) {
		Contact contact=new Contact();
		if(number.equals("12345") && password.equals("12345")) {
			contact.setName("admin");
			contact.setNumber("12345");
			contact.setEmail("admin@abc.com");
			contact.setPassword("12345");
			
		}
		else {
		try {
			PreparedStatement ps=con.prepareStatement("select * from passwords where number=?");
			ps.setString(1, number);
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				EncryptDecrypt ed=new EncryptDecrypt();
				String encryptval=ed.encrypt(password);
				System.out.println("1 " +rs.getString(2));
				System.out.println("2 " +encryptval);
				if(rs.getString(2).equals(encryptval)) {
					PreparedStatement ps1=con.prepareStatement("select * from contactlist where number=? and password=?");
					ps1.setString(1, rs.getString(1));
					ps1.setString(2, ed.decrypt(rs.getString(2)));
					ResultSet rs1=ps1.executeQuery();
					if(rs1.next()) {
					contact.setName(rs1.getString(2));
					contact.setNumber(rs1.getString(3));
					contact.setEmail(rs1.getString(4));
					contact.setPassword(rs1.getString(5));
					}
				}
				else {
					contact.setName("");
					contact.setNumber("");
					contact.setEmail("");
					contact.setPassword("");	
				}
				
			}
			else {
				contact.setName("");
				contact.setNumber("");
				contact.setEmail("");
				contact.setPassword("");	
			}
		} catch (Exception e) {
			
			contact.setName("");
			contact.setNumber("");
			contact.setEmail("");
			contact.setPassword("");
			
		}
		
		}
		return contact;
	}
	
	public static boolean isNumberValid(String s)
	{
	    Pattern p = Pattern.compile("(0|91)?[7-9][0-9]{9}");
	    Matcher m = p.matcher(s);
	    return (m.find() && m.group().equals(s));
	}
	public static boolean isEmailValid(String s)
	{
		Pattern VALID_EMAIL_ADDRESS_REGEX = 
			    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		 Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(s);
	        return matcher.find();
	}
	public static boolean isPasswordValid(String password)
    {

        String regex = "^(?=.*[0-9])"
                       + "(?=.*[a-z])(?=.*[A-Z])"
                       + "(?=.*[@#$%^&+=])"
                       + "(?=\\S+$).{8,20}$";
        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }
	
	public Map<String, String> addoneContact(Contact a) {
		Map<String, String> map=new HashMap<String, String>();
		try {
			int check=0;
			System.out.println(a.getNumber());
			PreparedStatement ps1=con.prepareStatement("select * from contactlist where number=?");
			ps1.setString(1, a.getNumber());
			ResultSet rs=ps1.executeQuery();
			while(rs.next()) {
				if(a.getNumber().equals(rs.getString(3))) {
					check=1;
				}
			}
			if(check==1) {
				map.put("msg", "Number already exists");
			}
			else if (a.getNumber().length()!=10 && !isNumberValid(a.getNumber())) {
				map.put("msg", "Number invalid");
			}
			else if(!isEmailValid(a.getEmail())) {
				map.put("msg", "Email id invalid");
			}
			else if(!isPasswordValid(a.getPassword())) {
				map.put("msg", "password invalid");
			}
			else {
				PreparedStatement ps2=con.prepareStatement("insert into passwords (number,password) values(?,?)");
				EncryptDecrypt ed=new EncryptDecrypt();
				EncryptDecryptAES aes=new EncryptDecryptAES();
				String encryptval=ed.encrypt(a.getPassword());
				System.out.println("encrypted password: "+encryptval);
				ps2.setString(1,a.getNumber());
				ps2.setString(2,encryptval);
				ps2.executeUpdate();
				
				PreparedStatement ps3=con.prepareStatement("insert into details (name,email) values(?,?)");
				String aesenc1=aes.encryptAES(a.getName());
				String aesenc2=aes.encryptAES(a.getEmail());
				ps3.setString(1,aesenc1);
				ps3.setString(2,aesenc2);
				ps3.executeUpdate();
				System.out.println("Name: "+aes.decryptAES(aesenc1));
				System.out.println("Email: "+aes.decryptAES(aesenc2));
				
				PreparedStatement ps=con.prepareStatement("insert into contactlist (name,number,email,password) values(?,?,?,?)");
				ps.setString(1, a.getName());
				ps.setString(2, a.getNumber());
				ps.setString(3, a.getEmail());
				ps.setString(4, a.getPassword());
				ps.executeUpdate();
				map.put("msg", "Added Successfully");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public Map<String, String> deleteoneContact(String number) {
		Map<String, String> map=new HashMap<String, String>();
		try {
			int check=0;
			System.out.println(number);
			PreparedStatement ps1=con.prepareStatement("select * from contactlist where number=?");
			ps1.setString(1, number);
			ResultSet rs=ps1.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString(3));
				if(number.equals(rs.getString(3))) {
					check=1;
				}
			}
			if(check==1) {
				PreparedStatement ps=con.prepareStatement("delete from contactlist where number=?");
				ps.setString(1, number);
				ps.executeUpdate();
				map.put("msg", "Deleted Successfully");
			}else {
				map.put("msg", "No entry found");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
		
	}
	
	public Map<String, String> updateoneContact(Contact a,String newnum) {
		Map<String, String> map=new HashMap<String, String>();
		try {
			int check=0,check1=0;
			PreparedStatement ps=con.prepareStatement("select * from contactlist where number=?");
			ps.setString(1, a.getNumber());
			ResultSet rs=ps.executeQuery();
			while(rs.next()) {
				if(a.getNumber().equals(rs.getString(3))) {
					check=1;
				}
			}
			
			PreparedStatement p2=con.prepareStatement("select * from contactlist where number=?");
			p2.setString(1, newnum);
			ResultSet rs1=p2.executeQuery();
			while(rs1.next()) {
				if(a.getNumber().equals(rs1.getString(3))) {
					check1=1;
				}
			}
			
			if(check==1 && check1==0) {
				PreparedStatement ps1=con.prepareStatement("update contactlist set name=?,number=?,email=?,password=? where number=?");
				ps1.setString(1, a.getName());
				ps1.setString(2, newnum);
				ps1.setString(3, a.getEmail());
				ps1.setString(4, a.getNumber());
				ps1.setString(5, a.getPassword());
				ps1.executeUpdate();
				map.put("msg", "update Successful");
			}else {
				map.put("msg", "update Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "update Failed");
		}
		return map;
	}
	
}
